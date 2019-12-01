//
//  KeyboardGuardian.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 01.12.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//
// https://stackoverflow.com/questions/56491881/move-textfield-up-when-thekeyboard-has-appeared-by-using-swiftui-ios




import SwiftUI
import Combine

final class KeyboardGuardian: ObservableObject {
    public var rects: Array<CGRect>
    public var keyboardRect: CGRect = CGRect()

    // keyboardWillShow notification may be posted repeatedly,
    // this flag makes sure we only act once per keyboard appearance
    public var keyboardIsHidden = true

    @Published var slide: CGFloat = 0

    var showField: Int = 0 {
        didSet {
            updateSlide()
        }
    }

    init(textFieldCount: Int) {
        self.rects = Array<CGRect>(repeating: CGRect(), count: textFieldCount)

    }

    func addObserver() {
NotificationCenter.default.addObserver(self, selector: #selector(keyBoardWillShow(notification:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyBoardDidHide(notification:)), name: UIResponder.keyboardDidHideNotification, object: nil)
}

func removeObserver() {
 NotificationCenter.default.removeObserver(self)
}

    deinit {
        NotificationCenter.default.removeObserver(self)
    }



    @objc func keyBoardWillShow(notification: Notification) {
        if keyboardIsHidden {
            keyboardIsHidden = false
            if let rect = notification.userInfo?["UIKeyboardFrameEndUserInfoKey"] as? CGRect {
                keyboardRect = rect
                updateSlide()
            }
        }
    }

    @objc func keyBoardDidHide(notification: Notification) {
        keyboardIsHidden = true
        updateSlide()
    }

    func updateSlide() {
        if keyboardIsHidden {
            slide = 0
        } else {
            let tfRect = self.rects[self.showField]
            let diff = keyboardRect.minY - tfRect.maxY

            if diff > 0 {
                slide += diff
            } else {
                slide += min(diff, 0)
            }

        }
    }
}
//GeometryGetter
//
//This is a view that absorbs the size and position of its parent view. In order to achieve that, it is called inside the .background modifier. This is a very powerful modifier, not just a way to decorate the background of a view. When passing a view to .background(MyView()), MyView is getting the modified view as the parent. Using GeometryReader is what makes it possible for the view to know the geometry of the parent.
//
//For example: Text("hello").background(GeometryGetter(rect: $bounds)) will fill variable bounds, with the size and position of the Text view, and using the global coordinate space.

struct GeometryGetter: View {
    @Binding var rect: CGRect

    var body: some View {
        GeometryReader { geometry in
            Group { () -> AnyView in
                DispatchQueue.main.async {
                    self.rect = geometry.frame(in: .global)
                }

                return AnyView(Color.clear)
            }
        }
    }
}
