
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
  
  private static func makeImpact(_ style: UIImpactFeedbackGenerator.FeedbackStyle = .medium) {
    isVibrationOn = true
    let impactFeedbackgenerator = UIImpactFeedbackGenerator(style: style)
    impactFeedbackgenerator.prepare()
    impactFeedbackgenerator.impactOccurred()
    isVibrationOn = false
  }
  
  
  private static func makeWarning() {
    isVibrationOn = true
    let list: [UIImpactFeedbackGenerator.FeedbackStyle] = [.soft, .soft, .medium, .heavy, .medium]
    var counter = 5
    
    Timer.scheduledTimer(withTimeInterval: 0.225, repeats: true, block: {timer in
      if (counter == 0) {
        isVibrationOn = false
        timer.invalidate()
        return;
      }
      let style = list[counter % list.count]
      let gen = UIImpactFeedbackGenerator(style: style)
      gen.prepare()
      gen.impactOccurred()
      counter -= 1
    })
    
  }
  
}
