//
//  QRReaderViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 23/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//
import AVFoundation
import UIKit

let inspirationSegue = "toInspiration";
let noAccess = "noAccess";

class QRReaderViewController: UIViewController, AVCaptureMetadataOutputObjectsDelegate {
    var captureSession: AVCaptureSession!
    var previewLayer: AVCaptureVideoPreviewLayer!
    var _access = false;
    var hasAccess: Bool {
        get {
            return _access;
        }
        set (newValue) {
            _access = newValue;
            if (!newValue) {
                jumpToNoAccess()
                return;
            }
            self.initCapture()
            DispatchQueue.global(qos: .utility).async {
                self.captureSession.startRunning()
            }
        }
    }
    @IBOutlet weak var cameraPreview: UIView!
    
    @IBAction func connectedComplete(_ sender: Any) {
        DispatchQueue.global(qos: .background).async {
            if (self.captureSession?.isRunning == true) {
                self.captureSession.stopRunning()
            }
        }
       
        jumpToInspitation();
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
//        view.backgroundColor = UIColor.black
        checkPermissions();
    }
    
    private func checkPermissions() {
            AVCaptureDevice.requestAccess(for: AVMediaType.video) { response in
            if response {
                //access granted
                print("granted")
                self.hasAccess = true
            } else {
                print("no access")
                self.hasAccess = false
            }
        }
    }
    
    private func initCapture() {
        captureSession = AVCaptureSession()
        
        guard let videoCaptureDevice = AVCaptureDevice.default(for: .video) else { return }
        let videoInput: AVCaptureDeviceInput
        
        do {
            videoInput = try AVCaptureDeviceInput(device: videoCaptureDevice)
        } catch {
            print("no input")
            return
        }
        
        if (captureSession.canAddInput(videoInput)) {
            captureSession.addInput(videoInput)
        } else {
            failed()
            return
        }
        
        let metadataOutput = AVCaptureMetadataOutput()
        
        if (captureSession.canAddOutput(metadataOutput)) {
            captureSession.addOutput(metadataOutput)
            
            metadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
            metadataOutput.metadataObjectTypes = [.qr]
        } else {
            failed()
            return
        }
        
        previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        previewLayer.frame = (cameraPreview?.layer.bounds)!
        previewLayer.videoGravity = .resizeAspectFill
        cameraPreview?.layer.addSublayer(previewLayer)
    }
    
    func failed() {
        let ac = UIAlertController(title: "Scanning not supported", message: "Your device does not support scanning a code from an item. Please use a device with a camera.", preferredStyle: .alert)
        ac.addAction(UIAlertAction(title: "OK", style: .default))
        present(ac, animated: true)
        captureSession = nil
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if (captureSession?.isRunning == false) {
            captureSession.startRunning()
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        DispatchQueue.global(qos: .background).async {
            if (self.captureSession?.isRunning == true) {
                self.captureSession.stopRunning()
            }
        }
    }
    
    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        captureSession.stopRunning()
        
        if let metadataObject = metadataObjects.first {
            guard let readableObject = metadataObject as? AVMetadataMachineReadableCodeObject else { return }
            guard let stringValue = readableObject.stringValue else { return }
            AudioServicesPlaySystemSound(SystemSoundID(kSystemSoundID_Vibrate))
            found(code: stringValue)
        }
        
        dismiss(animated: true)
    }
    
    func found(code: String) {
        print(code)
        jumpToInspitation();
    }
    
    override var prefersStatusBarHidden: Bool {
        return false
    }
    
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return .portrait
    }
    
    private func jumpToInspitation() {
        DispatchQueue.main.async {
            self.performSegue(withIdentifier: inspirationSegue, sender: self)
        }
    }
    
    
    private func jumpToNoAccess() {
        DispatchQueue.main.async {
            self.performSegue(withIdentifier: noAccess, sender: self)
        }
    }

}
