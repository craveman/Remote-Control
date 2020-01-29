//
//  AppDelegate.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

let rs = RemoteService.shared

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

  var window: UIWindow?

  func application (_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
    // Override point for customization after application launch.
    rs.on(event: { [weak self] (event) in
      guard case .serverDown = event else {
        return
      }
      guard let remote = rs.connection.address else {
        return
      }
      self?.invalidate(connection: remote)
    })
    return true
  }

  func applicationWillResignActive (_ application: UIApplication) {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
  }

  func applicationDidEnterBackground (_ application: UIApplication) {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    if rs.connection.isConnected {
      rs.connection.disconnect(temporary: true)
    }
  }

  func applicationWillEnterForeground (_ application: UIApplication) {
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    guard let remote = rs.connection.address else {
      return
    }
    if case .failure(_) = rs.connection.connect(to: remote) {
      invalidate(connection: remote)
    }
  }

  func applicationDidBecomeActive (_ application: UIApplication) {
//    guard let controller = self.window?.rootViewController as? ConnectionsViewController else {
//      return
//    }
//    if (rs.connection.isConnected) {
//      controller.jumpToInspiration()
//    }
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
  }

  func applicationWillTerminate (_ application: UIApplication) {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
  }

  private func invalidate (connection remote: RemoteAddress) {
    DispatchQueue.main.async {
      rs.connection.forget()
      guard let controller = self.window?.rootViewController as? ConnectionsViewController else {
        return
      }
      controller.dismiss(animated: true)
      controller.warning(remote)
    }
  }
}
