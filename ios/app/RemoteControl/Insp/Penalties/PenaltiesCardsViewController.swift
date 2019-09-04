//
//  PenaltiesCardsViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 04/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit


enum PenaltiesTypes {
    case basic
    case p
}

class PenaltiesCardsViewController: UIViewController {

    private var type: PenaltiesTypes = .basic
    @IBOutlet weak var left: UIView!
    @IBOutlet weak var right: UIView!
    
    public func setType(_ type: PenaltiesTypes) {
        self.type = type;
        updateView();
    }
    
    func updateView() -> Void {
        updateSubViewType(left.subviews.first)
        updateSubViewType(right.subviews.first)
    }
    
    func updateSubViewType(_ view: UIView?) {
        if let vc = view?.next as? PenaltySelectorViewController {
            vc.penaltyType = self.type
        }
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
