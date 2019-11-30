
import func AVFoundation.AudioServicesPlaySystemSound
import typealias AVFoundation.SystemSoundID
import var AVFoundation.kSystemSoundID_Vibrate

enum Vibration {

  static func on () {
    AudioServicesPlaySystemSound(SystemSoundID(kSystemSoundID_Vibrate))
  }
}
