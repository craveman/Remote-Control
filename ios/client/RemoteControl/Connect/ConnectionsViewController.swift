//
//  ConnectionsViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 10/09/2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import UIKit
import Sm02Client

class ConnectionsViewController: UIViewController, UIAdaptivePresentationControllerDelegate {
    
    @IBOutlet weak var qrReaderSubViewWrapper: UIView!
    
    override func viewDidAppear(_ animated: Bool) {
        if isSimulationEnv() {
            skipQR()
        }
        super.viewDidAppear(animated);
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "skipQR" || segue.identifier == "toInspiration"{
        segue.destination.presentationController?.delegate = (self as UIAdaptivePresentationControllerDelegate)
        }
        if isSimulationEnv() {
            return;
        }
        if qrReaderSubViewWrapper.next is QrViewController {
            (qrReaderSubViewWrapper.next as! QrViewController).stopScanner()
        }
    }
    
    func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
        print("dismiss")
        
        if isSimulationEnv() {
            skipQR()
            return;
        }
        print("next", qrReaderSubViewWrapper.next)
        if qrReaderSubViewWrapper.next is QrViewController {
            print("is QrViewController")
            (qrReaderSubViewWrapper.next as! QrViewController).startScanner()
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
