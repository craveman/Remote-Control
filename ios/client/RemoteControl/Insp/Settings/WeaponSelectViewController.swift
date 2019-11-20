//
//  WeaponSelectViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 11/09/2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import UIKit

class WeaponSelectViewController: UIViewController {

    let rs = RemoteService.shared

    @IBOutlet weak var foilButton: UIButton!
    @IBOutlet weak var epeeButton: UIButton!
    @IBOutlet weak var sabreButton: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()

        [foilButton, epeeButton, sabreButton].forEach({ btn in
            btn?.addTarget(self, action: Selector(("selectWeaponAction:")), for: .touchUpInside)
        })

    }

    @objc func selectWeaponAction(_ sender: UIButton) {
      switch sender {
      case foilButton: rs.competition.weapon = .foil
      case epeeButton: rs.competition.weapon = .epee
      case sabreButton: rs.competition.weapon = .sabre
      default: break
      }
        performSegue(withIdentifier: "done", sender: sender)
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
