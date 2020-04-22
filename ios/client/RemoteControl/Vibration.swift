
import func AVFoundation.AudioServicesPlaySystemSoundWithCompletion
import typealias AVFoundation.SystemSoundID
import var AVFoundation.kSystemSoundID_Vibrate
import UIKit.UIImpactFeedbackGenerator
enum Vibration {
  static private var isVibrationOn = false;
  
  static func on () {
    withGuard({makeVibration()}, {on()})
  }
  
  
  static func notification(_ style: UINotificationFeedbackGenerator.FeedbackType = .success) {
    withGuard({makeNotificationImpact(style)})
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
  
  private static func withGuard(_ callback: () -> Void, _ retry: (() -> Void)? = nil) {
    guard !isVibrationOn else {
      if retry != nil {
         Timer.scheduledTimer(withTimeInterval: 0.1, repeats: false, block: { _ in
           retry!()
         })
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
  
  private static func makeNotificationImpact(_ style: UINotificationFeedbackGenerator.FeedbackType = .success) {
    isVibrationOn = true
    let impactFeedbackgenerator = UINotificationFeedbackGenerator()
    impactFeedbackgenerator.prepare()
    impactFeedbackgenerator.notificationOccurred(style)
    isVibrationOn = false
  }
  
  
  private static func makeFeedbackImpact(_  style: UIImpactFeedbackGenerator.FeedbackStyle = .medium) {
    isVibrationOn = true
    let impactFeedbackgenerator = UIImpactFeedbackGenerator(style: style)
    impactFeedbackgenerator.prepare()
    impactFeedbackgenerator.impactOccurred()
    isVibrationOn = false
  }
  
  
  private static func makeWarning() {
    isVibrationOn = true
    let impactFeedbackgenerator = UINotificationFeedbackGenerator()
    impactFeedbackgenerator.prepare()
    impactFeedbackgenerator.notificationOccurred(.warning)
    Timer.scheduledTimer(withTimeInterval: 0.9, repeats: false, block: {_ in
      isVibrationOn = false
    })
    
  }
  
}
