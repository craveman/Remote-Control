//
//  FightTimePickerViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 13/09/2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import UIKit

let maxMinutValue = 20

class FightTimePickerViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate {
    @IBOutlet weak var navBar: UINavigationBar!
    
    @IBOutlet weak var time: UIPickerView!
    
    @IBAction func save(_ sender: Any) {
        print("\(minSelected):\(secSelected)")
        dismiss(animated: true, completion: nil)
    }
    @IBAction func cancel(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    private var minSelected = 0
    private var secSelected = 0
    
    var showsSelectionIndicator = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        time.delegate = self
        time.dataSource = self
        // Do any additional setup after loading the view.
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int)
    {
        if component == 0 {
            minSelected = row
        }
        
        if component == 2 {
            secSelected = row
        }
    }

    func pickerView(_ pickerView: UIPickerView, widthForComponent component: Int) -> CGFloat {
        if component == 1 {
            return CGFloat(20)
        }
        return CGFloat(100)
        
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 3
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if component == 0 {
            return 1 + maxMinutValue
        }
        if component == 1 {
            return 1
        }
        if component == 2 {
            return 60
        }
        return 0;
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if component == 1 {
            return ":"
        }
        return String(format: "%02d", row)
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
