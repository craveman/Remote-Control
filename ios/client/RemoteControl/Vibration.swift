
import func AVFoundation.AudioServicesPlaySystemSound
import typealias AVFoundation.SystemSoundID
import var AVFoundation.kSystemSoundID_Vibrate
import UIKit.UIImpactFeedbackGenerator
enum Vibration {

  static func on () {
    AudioServicesPlaySystemSound(SystemSoundID(kSystemSoundID_Vibrate))
  }
  
  static func impact() {
    let impactFeedbackgenerator = UIImpactFeedbackGenerator(style: .medium)
    impactFeedbackgenerator.prepare()
    impactFeedbackgenerator.impactOccurred()
  }
}
