
import AVFoundation
import UIKit

enum Vibration {

  static func on () {
    AudioServicesPlaySystemSound(SystemSoundID(kSystemSoundID_Vibrate))
  }
}
