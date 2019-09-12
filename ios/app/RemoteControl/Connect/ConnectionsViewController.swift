//
//  ConnectionsViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 10/09/2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import UIKit

class ConnectionsViewController: UIViewController {
    
    @IBOutlet weak var qrReaderSubViewWrapper: UIView!
    
    override func viewDidLoad() {
        //todo beware the user to allow access and/or handle no-access
        if isSimulationEnv(){
            skipQR()
        }
        super.viewDidLoad()
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    
    private func jumpToInspiration() {
        print("skip")
        performSegue(withIdentifier: "skipQR", sender: nil)
    }
    
    private func isSimulationEnv() -> Bool {
    
    #if targetEnvironment(simulator)
    //        print("simulator")
      return true
    
    // your simulator code
    #else
      return false
    //        print("real")
    // your real device code
    #endif
    }

    private func skipQR() {
        Timer.scheduledTimer(withTimeInterval: timeout, repeats: false) { [weak self] timer in
            self?.jumpToInspiration();
        }
    }
    
}
