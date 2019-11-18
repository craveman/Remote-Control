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
    let rs = RemoteService.shared
    
    override func viewDidAppear(_ animated: Bool) {
        
        do {
            print ("set success")
            let scanner = self.getScanner()
            scanner.onSuccess = { [weak self] in
              print ("toInspiration")
              self?.jumpToInspiration()
            }
        } catch {
            print("next is not QrViewController", qrReaderSubViewWrapper.next!)
        }
        
        if isSimulationEnv() {
            skipQR()
        }
        super.viewDidAppear(animated);
    }
    
    private func getScanner() -> QrViewController {
        return qrReaderSubViewWrapper.subviews[0].next as! QrViewController
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
      rs.connection.disconnect()
      if isSimulationEnv() {
        skipQR()
        return;
      }
        
      self.getScanner().startScanner()
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
        print("jumpToInspiration")
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
