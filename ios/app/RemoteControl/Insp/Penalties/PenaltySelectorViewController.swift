//
//  PenaltySelectorViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 04/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

class PenaltySelectorViewController: UIViewController {
    var blackIsSet = false;
    
    var penaltyCounter = 0
    
    var penaltyType: PenaltiesTypes = .basic {
        didSet(type) {
            updateView()
        }
    }
    
    @IBOutlet weak var blackCard: UIButton!
    
    @IBOutlet weak var decreaseCard: UIButton!
    
    @IBOutlet weak var increaseCard: UIButton!
    
    @IBAction func blackSelected(_ sender: UIButton) {
        blackIsSet = !blackIsSet
        updateView()
    }
    
    @IBAction func decreaseSelected(_ sender: UIButton) {
        print("decrease")
        penaltyCounter -= 1
        updateView()
    }
    
    @IBAction func increaseSelected(_ sender: UIButton) {
        penaltyCounter += 1
        print("increase")
        updateView()
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        updateView()
        // Do any additional setup after loading the view.
    }
    
    func updateView() {
        increaseCard.backgroundColor = penaltyCounter == 0 ? #colorLiteral(red: 0.9994240403, green: 0.9855536819, blue: 0, alpha: 1) : #colorLiteral(red: 1, green: 0.1491314173, blue: 0, alpha: 1);
        increaseCard.isEnabled = !blackIsSet
        
        
        decreaseCard.isEnabled = !blackIsSet && penaltyCounter > 0
        
        blackCard.isSelected = blackIsSet
        blackCard.backgroundColor = blackIsSet ? #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1) : #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1)
        
        switch penaltyType {
        case .p:
            increaseCard.setTitle("P", for: .normal)
            blackCard.setTitle("P", for: .normal)
            break
        default:
            increaseCard.setTitle("", for: .normal)
            blackCard.setTitle("", for: .normal)
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
