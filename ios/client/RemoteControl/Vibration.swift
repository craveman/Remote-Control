import Foundation
import Dispatch
import AVFoundation
import CoreHaptics
import UIKit.UIImpactFeedbackGenerator

enum Vibration {
  
  static private var isVibrationOn = false
  static private func  isHapticOn() -> Bool {
      return CHHapticEngine.capabilitiesForHardware().supportsHaptics ||
        UIDevice.current.value(forKey: "_feedbackSupportLevel") as! Int == 2
  }
  
  static func on (_ separatedOnly: Bool = false) {
    if (!separatedOnly) {
      vibration()
      return
    }
    withGuard({makeVibration()}, {on(true)})
  }
  
  
  static func notification(_ style: UINotificationFeedbackGenerator.FeedbackType = .success) {
    withDelay({
      withGuard({makeNotificationImpact(style)})
    }, 0.1)
    
  }
  
  
  static func impact(_ weight: UIImpactFeedbackGenerator.FeedbackStyle = .medium, insureDone separatedOnly: Bool = false) {
    if (!separatedOnly) {
      withGuard({makeFeedbackImpact(weight)})
      return
    }
    withGuard({makeFeedbackImpact(weight)}, {impact(weight, insureDone: true)})
  }
  
  static func warning() {
    withGuard({makeWarning()}, {warning()})
  }
  
  private static func withGuard(_ callback: @escaping () -> Void, _ retry: (() -> Void)? = nil) {
    guard !isVibrationOn else {
      if retry != nil {
        withDelay({
          retry!()
        }, 0.1)
      }
      
      return
    }
    callback()
  }
  
  private static func makeVibration() {
    isVibrationOn = true
    AudioServicesPlaySystemSoundWithCompletion(SystemSoundID(kSystemSoundID_Vibrate), {
      isVibrationOn = false
    })
  }
  
  
  private static func vibration() {
    AudioServicesPlaySystemSoundWithCompletion(SystemSoundID(kSystemSoundID_Vibrate), {})
  }
  
  private static func makeNotificationImpact(_ style: UINotificationFeedbackGenerator.FeedbackType = .success) {
    guard isHapticOn() else {
      makeVibration()
      return
    }
    
    
    isVibrationOn = true
    let impactFeedbackgenerator = UINotificationFeedbackGenerator()
    impactFeedbackgenerator.prepare()
    impactFeedbackgenerator.notificationOccurred(style)
    isVibrationOn = false
  }
  
  
  private static func makeFeedbackImpact(_  style: UIImpactFeedbackGenerator.FeedbackStyle = .medium) {
    guard isHapticOn() else {
      makeVibration()
      return
    }
    
    isVibrationOn = true
    let impactFeedbackgenerator = UIImpactFeedbackGenerator(style: style)
    impactFeedbackgenerator.prepare()
    impactFeedbackgenerator.impactOccurred()
    isVibrationOn = false
  }
  
  
  private static func makeWarning() {
    guard isHapticOn() else {
      makeVibration()
      return
    }
    
    isVibrationOn = true
    let impactFeedbackgenerator = UINotificationFeedbackGenerator()
    impactFeedbackgenerator.prepare()
    impactFeedbackgenerator.notificationOccurred(.warning)
    withDelay({
      isVibrationOn = false
    }, 0.5)
  }
  
}
