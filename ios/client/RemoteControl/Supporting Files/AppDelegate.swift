//
//  AppDelegate.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin, Sergei Andreev. All rights reserved.
//

import UIKit
import BackgroundTasks
import class Combine.AnyCancellable


fileprivate func log(_ items: Any...) {
  print("AppDelegate:log: ", items)
}

let rs = RemoteService.shared
fileprivate let standByTaskId = "com.inspirationApp.RemoteControl.Sm02BgConnection"
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
  
  var window: UIWindow?
  var app: UIApplication?
  private var wasInvalidated = false
  private var hasRegisteredBgTask = false
  private var pingMissed = false
  private var messageListener: AnyCancellable?
  private var eventListenerUUID: UUID?
  private var pingTimer: Timer?
  
  private var connectionBackgroundTask: UIBackgroundTaskIdentifier = .invalid
  func application (_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
    // Override point for customization after application launch.
    self.app = application;
    setEventsAndTimers()
    //    TODO: if needed; N.B.! add task id to Info.plist
    //    registerBgTask()
    
    return true
  }
  
  func applicationWillResignActive (_ application: UIApplication) {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    log("applicationWillResignActive")
    stopScanner()
    if rs.connection.isConnected {
      runConnectionInBackground()
    }
  }
  
  func applicationDidEnterBackground (_ application: UIApplication) {
    log("applicationDidEnterBackground")
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    
    tempoparyDisconnect()
    startShedulledUpdates()
  }
  
  func applicationWillEnterForeground (_ application: UIApplication) {
    
    log("applicationWillEnterForeground")
    stopShedulledUpdates()
    reconnect()
    
  }
  
  func applicationDidBecomeActive (_ application: UIApplication) {
    log("applicationDidBecomeActive")
    stopBgTasks()
    activateViewController()
    
    
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
  }
  
  func applicationWillTerminate (_ application: UIApplication) {
    log("applicationWillTerminate")
    stopEventsAndTimers()
    terminateConnection()
    stopBgTasks()
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
  }
  
  private func terminateConnection() {
    log("terminateConnection")
    if rs.connection.isConnected {
      rs.connection.disconnect()
      stopBgConnectionTask()
    }
  }
  
  private func setEventsAndTimers() {
    log("setEventsAndTimers")
    //    setNetworkEventsListerers()
    setSmEventsListerers()
    setPingCheckerTimer()
  }
  
  private func stopEventsAndTimers() {
    log("stopEventsAndTimers")
    if let uuid = self.eventListenerUUID {
      rs.remove(event: uuid)
    }
    if let msg = self.messageListener {
      msg.cancel()
    }
    
    if let tmr = self.pingTimer {
      tmr.invalidate()
    }
  }
  
  private func tempoparyDisconnect() {
    if rs.connection.isConnected, self.connectionBackgroundTask == .invalid {
      log("applicationDidEnterBackground:disconnect self.backgroundTask == .invalid")
      rs.connection.disconnect(temporary: true)
    }
  }
  
  private func startShedulledUpdates() {
    //      if self.app?.backgroundRefreshStatus != UIBackgroundRefreshStatus.available {
    //        log("self.app?.backgroundRefreshStatus != UIBackgroundRefreshStatus.available")
    //        return
    //      }
    //    self.scheduleAppSm02BgConnection()
  }
  
  private func stopShedulledUpdates() {
    
  }
  
  private func activateViewController() {
    log("applicationDidBecomeActive::activateViewController")
    guard let controller = self.window?.rootViewController as? ConnectionsViewController else {
      return
    }
    
    if controller.presentedViewController == nil {
      if (rs.connection.isConnected) {
        controller.jumpToInspiration()
      } else {
        
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
  }
  
  private func reconnect() {
    log("reconnect::before-checks")
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
    log("reconnect::connecting...")
    if case .failure(_) = rs.connection.connect(to: remote) {
      log("reconnect::invalidated after failure")
      invalidate(connection: remote)
    }
    
    setEventsAndTimers()
  }
  
  private func stopScanner() {
    guard let controller = self.window?.rootViewController as? ConnectionsViewController else {
      return
    }
    controller.stop()
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
  
  private func runConnectionInBackground(_ name: String = "default") {
    log("runConnectionInBackground \(name)")
    
    let tskName = "runConnectionInBackground:\(name)";
    self.connectionBackgroundTask = self.app?.beginBackgroundTask(withName: tskName, expirationHandler: connectionTaskExpirationHandler) ?? .invalid
  }
  
  private func connectionTaskExpirationHandler () -> Void {
    log("expirationHandler \(self.connectionBackgroundTask)")
    if rs.connection.isConnected {
      rs.connection.disconnect(temporary: true)
      stopEventsAndTimers()
    }
    if (self.connectionBackgroundTask != .invalid) {
      self.app?.endBackgroundTask(self.connectionBackgroundTask)
      self.connectionBackgroundTask = .invalid
    }
  }
  
  private func setSmEventsListerers() {
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
    
  }
  
  private func setNetworkEventsListerers() {
    let networkHandler = NetworkReachability();
    networkHandler.start()
  }
  
  private func setPingCheckerTimer() {
    
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
      self.stopBgTasks()
    }
  }
  
  private func registerBgTask() {
    
    let regesterResult = BGTaskScheduler.shared.register(forTaskWithIdentifier: standByTaskId, using: .global()) {task in
      log("BGTaskScheduler Task \(standByTaskId) run")
      task.setTaskCompleted(success: true)
    }
    
    hasRegisteredBgTask = regesterResult
    
    log("BGTaskScheduler task \(standByTaskId) register result", regesterResult)
  }
  
  private func scheduleAppSm02BgConnection() {
    BGTaskScheduler.shared.cancel(taskRequestWithIdentifier: standByTaskId)
    let request = BGProcessingTaskRequest(identifier: standByTaskId)
    
    request.requiresNetworkConnectivity = true
    //    request.earliestBeginDate = Date(timeIntervalSinceNow: 20)
    do {
      log("scheduleAppSm02BgConnection submit")
      try BGTaskScheduler.shared.submit(request)
    } catch {
      log("Could not schedule app refresh: \(error)")
    }
  }
  
  private func stopBgConnectionTask() {
    if (self.connectionBackgroundTask != .invalid) {
      self.app?.endBackgroundTask(self.connectionBackgroundTask)
      self.connectionBackgroundTask = .invalid
    }
  }
  
  private func stopBgTasks() {
    stopBgConnectionTask()
    if (hasRegisteredBgTask) {
      BGTaskScheduler.shared.cancel(taskRequestWithIdentifier: standByTaskId)
    }
  }
  
}
