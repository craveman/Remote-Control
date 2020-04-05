//
//  AppDelegate.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import class Combine.AnyCancellable


fileprivate func log(_ items: Any...) {
  print("AppDelegate:log: ", items)
}

let rs = RemoteService.shared

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
  
  var window: UIWindow?
  var app: UIApplication?
  private var wasInvalidated = false
  private var pingMissed = false
  private var messageListener: AnyCancellable?
  private var eventListenerUUID: UUID?
  private var pingTimer: Timer?
  private var backgroundTask: UIBackgroundTaskIdentifier = .invalid
  func application (_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
    // Override point for customization after application launch.
    self.app = application;
    self.eventListenerUUID = rs.on(event: { [weak self] (event) in
      guard case .serverDown = event else {
        return
      }
      guard let remote = rs.connection.address else {
        return
      }
      log("eventListenerUUID invalidate")
      self?.invalidate(connection: remote)
    })
    
    self.messageListener = rs.connection.$lastMessageAt.on(change: {[unowned self] _ in
      if (rs.connection.isConnected && self.pingMissed) {
        self.pingMissed = false
      }
    })
    
    self.pingTimer = Timer.scheduledTimer(withTimeInterval: RemoteService.PING_INTERVAL, repeats: true) {[unowned self] _ in
      guard rs.connection.isConnected else {
        self.pingMissed = false
        return
      }
      log("ping check")
      if (!self.pingMissed) {
        self.pingMissed = true
        return
      }
      log("Ping disconnect")
      rs.connection.disconnect(temporary: true)
    }
    
    return true
  }
  
  func applicationWillResignActive (_ application: UIApplication) {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    log("applicationWillResignActive")
    guard let controller = self.window?.rootViewController as? ConnectionsViewController else {
      return
    }
    controller.stop()
  }
  
  func applicationDidEnterBackground (_ application: UIApplication) {
    log("applicationDidEnterBackground")
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    
    
    if rs.connection.isConnected {
      if self.app?.backgroundRefreshStatus != UIBackgroundRefreshStatus.available {
        log("disconnect self.app?.backgroundRefreshStatus != UIBackgroundRefreshStatus.available")
        rs.connection.disconnect(temporary: true)
        return
      }
      
      runInBackground()
    }
  }
  
  func runInBackground() {
    
    var timer: Timer?
    
    if (self.backgroundTask != .invalid) {
      self.app?.endBackgroundTask(self.backgroundTask)
    }
    
    self.backgroundTask = self.app?.beginBackgroundTask(expirationHandler: {
      log("expirationHandler")
      timer?.invalidate()
      if rs.connection.isConnected {
        rs.connection.disconnect(temporary: true)
      }
      self.app?.endBackgroundTask(self.backgroundTask)
      self.backgroundTask = .invalid
    }) ?? .invalid
    
    timer = Timer.scheduledTimer(withTimeInterval: RemoteService.PING_INTERVAL, repeats: false) {[unowned self] _ in
      if (self.backgroundTask != .invalid) {
        log("Background")
        self.runInBackground()
      }
    }
    
    
    
  }
  
  func applicationWillEnterForeground (_ application: UIApplication) {
    log("applicationWillEnterForeground")
    guard rs.connection.isAuthenticated else {
      return
    }
    
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    guard let remote = rs.connection.address else {
      return
    }
    
    guard !rs.connection.isConnected else {
      return
    }
    
    if case .failure(_) = rs.connection.connect(to: remote) {
      log("invalidate in applicationWillEnterForeground")
      invalidate(connection: remote)
    }
  }
  
  func applicationDidBecomeActive (_ application: UIApplication) {
    if (self.backgroundTask != .invalid) {
      application.endBackgroundTask(self.backgroundTask)
      self.backgroundTask = .invalid
    }
    log("applicationDidBecomeActive")
    guard let controller = self.window?.rootViewController as? ConnectionsViewController else {
      return
    }
    
    if controller.presentedViewController == nil {
      if (rs.connection.isConnected) {
        controller.jumpToInspiration()
      } else {
        log("applicationDidBecomeActive::start")
        controller.start()
      }
    }
    else {
      // todo:
      if rs.connection.isConnected {
        // do nothig
      } else {
        log("else not connected", self.wasInvalidated)
        
        
        self.wasInvalidated = false;
        // presenter will be dismissed by invalidator???
      }
    }
    
    
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
  }
  
  func applicationWillTerminate (_ application: UIApplication) {
    log("applicationWillTerminate")
    if let uuid = self.eventListenerUUID {
      rs.remove(event: uuid)
    }
    if let msg = self.messageListener {
      msg.cancel()
    }
    
    if let tmr = self.pingTimer {
      tmr.invalidate()
    }
    
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
  }
  
  private func invalidate (connection remote: RemoteAddress) {
    self.wasInvalidated = true
    DispatchQueue.main.async {
      rs.connection.forget()
      
      guard let controller = self.window?.rootViewController as? ConnectionsViewController else {
        return
      }
      Timer.scheduledTimer(withTimeInterval: RemoteService.SYNC_INTERVAL, repeats: false) {_ in
        log("invalidated")
        if controller.presentedViewController != nil {
          controller.dismiss(animated: true)
        }
        controller.warning(remote)
        
      }
      
    }
  }
}
