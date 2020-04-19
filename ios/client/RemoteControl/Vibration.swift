
import func AVFoundation.AudioServicesPlaySystemSoundWithCompletion
import typealias AVFoundation.SystemSoundID
import var AVFoundation.kSystemSoundID_Vibrate
import UIKit.UIImpactFeedbackGenerator
enum Vibration {
  static private var isVibrationOn = false;
  
  static func on () {
    withGuard({makeVibration()}, {on()})
  }
  
  
  static func impact() {
    withGuard({makeImpact()})
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
  
  private static func makeImpact(_ style: UINotificationFeedbackGenerator.FeedbackType = .success) {
    isVibrationOn = true
    let impactFeedbackgenerator = UINotificationFeedbackGenerator()
    impactFeedbackgenerator.prepare()
    impactFeedbackgenerator.notificationOccurred(style)
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
