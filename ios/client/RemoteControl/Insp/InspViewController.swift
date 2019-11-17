//
//  InspViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import SwiftUI

var inspFontName: String = "DIN Alternate"

@available(iOS 13, macCatalyst 13, tvOS 13, watchOS 6, *)
struct ScaledFont: ViewModifier {
    @Environment(\.sizeCategory) var sizeCategory
    var name: String
    var size: CGFloat

    func body(content: Content) -> some View {
       let scaledSize = UIFontMetrics.default.scaledValue(for: size)
        return content.font(.custom(name, size: scaledSize))
    }
}

@available(iOS 13, macCatalyst 13, tvOS 13, watchOS 6, *)
extension View {
    func scaledFont(name: String = inspFontName, size: CGFloat = 24) -> some View {
        return self.modifier(ScaledFont(name: name, size: size))
    }
}

class InspViewController: UITabBarController {

    override func viewDidLoad() {
        self.selectedIndex = 1
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
