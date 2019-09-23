//
//  QRCodeReaderView.swift
//  RemoveMe
//
//  Created by Artem Labazin on 08/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

final public class QRCodeReaderView: UIView, QRCodeReaderDisplayable {
  
  public lazy var overlayView: QRCodeReaderViewOverlay? = {
    let ov = ReaderOverlayView()
  
    ov.backgroundColor = .clear
    ov.clipsToBounds = true
    ov.translatesAutoresizingMaskIntoConstraints = false
  
    return ov
  }()
    
  public let cameraView: UIView = {
    let cv = UIView()
    
    cv.clipsToBounds = true
    cv.translatesAutoresizingMaskIntoConstraints = false
    
    return cv
  }()
    
  public lazy var cancelButton: UIButton? = {
    let cb = UIButton()
    
    cb.translatesAutoresizingMaskIntoConstraints = false
    cb.setTitleColor(.gray, for: .highlighted)
    
    return cb
  }()
    
  public lazy var switchCameraButton: UIButton? = {
    let scb = SwitchCameraButton()
    
    scb.translatesAutoresizingMaskIntoConstraints = false
    
    return scb
  }()
    
  public lazy var toggleTorchButton: UIButton? = {
    let ttb = ToggleTorchButton()
    
    ttb.translatesAutoresizingMaskIntoConstraints = false
    
    return ttb
  }()
    
  private weak var reader: QRCodeReader?
    
  public func setupComponents (with builder: QRCodeReaderViewControllerBuilder) {
    self.reader = builder.reader
    reader?.lifeCycleDelegate = self
        
    addComponents()
        
    cancelButton?.isHidden = !builder.showCancelButton
    switchCameraButton?.isHidden = !builder.showSwitchCameraButton
    toggleTorchButton?.isHidden = !builder.showTorchButton
    overlayView?.isHidden = !builder.showOverlayView
        
    guard let cb = cancelButton,
          let scb = switchCameraButton,
          let ttb = toggleTorchButton,
          let ov = overlayView
    else {
      return
    }
        
    let views = ["cv": cameraView, "ov": ov, "cb": cb, "scb": scb, "ttb": ttb]
        
    addConstraints(NSLayoutConstraint.constraints(
      withVisualFormat: "H:|[cv]|",
      options: [],
      metrics: nil,
      views: views
    ))
        
    if builder.showCancelButton {
      addConstraints(NSLayoutConstraint.constraints(
        withVisualFormat: "V:|[cv][cb(40)]|",
        options: [],
        metrics: nil,
        views: views
      ))
      addConstraints(NSLayoutConstraint.constraints(
        withVisualFormat: "H:|-[cb]-|",
        options: [],
        metrics: nil,
        views: views
      ))
    } else {
      addConstraints(NSLayoutConstraint.constraints(
        withVisualFormat: "V:|[cv]|",
        options: [],
        metrics: nil,
        views: views
      ))
    }
    
    if builder.showSwitchCameraButton {
      addConstraints(NSLayoutConstraint.constraints(
        withVisualFormat: "V:|-[scb(50)]",
        options: [],
        metrics: nil,
        views: views
      ))
      addConstraints(NSLayoutConstraint.constraints(
        withVisualFormat: "H:[scb(70)]|",
        options: [],
        metrics: nil,
        views: views
      ))
    }
    
    if builder.showTorchButton {
      addConstraints(NSLayoutConstraint.constraints(
        withVisualFormat: "V:|-[ttb(50)]",
        options: [],
        metrics: nil,
        views: views
      ))
      addConstraints(NSLayoutConstraint.constraints(
        withVisualFormat: "H:|[ttb(70)]",
        options: [],
        metrics: nil,
        views: views
      ))
    }
        
    for attribute in Array<NSLayoutConstraint.Attribute>([.left, .top, .right, .bottom]) {
      addConstraint(NSLayoutConstraint(
        item: ov,
        attribute: attribute,
        relatedBy: .equal,
        toItem: cameraView,
        attribute: attribute,
        multiplier: 1,
        constant: 0
      ))
    }
        
    if let readerOverlayView = overlayView as? ReaderOverlayView {
      readerOverlayView.rectOfInterest = builder.rectOfInterest
    }
  }
    
  public override func layoutSubviews () {
    super.layoutSubviews()
    
    reader?.previewLayer.frame = bounds
  }
    
  // MARK: - Scan Result Indication
  func startTimerForBorderReset () {
    DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(1)) {
      self.overlayView?.setState(.normal)
    }
  }
    
  func addRedBorder () {
    self.startTimerForBorderReset()
    
    self.overlayView?.setState(.wrong)
  }
    
  func addGreenBorder () {
    self.startTimerForBorderReset()
    
    self.overlayView?.setState(.valid)
  }
    
  @objc public func setNeedsUpdateOrientation () {
    setNeedsDisplay()
  
    overlayView?.setNeedsDisplay()
  
    if let connection = reader?.previewLayer.connection,
       connection.isVideoOrientationSupported
    {
      let application = UIApplication.shared
      let orientation = UIDevice.current.orientation
      let supportedInterfaceOrientations = application.supportedInterfaceOrientations(for: application.keyWindow)
      
      connection.videoOrientation = QRCodeReader.videoOrientation(
        deviceOrientation: orientation,
        withSupportedOrientations: supportedInterfaceOrientations,
        fallbackOrientation: connection.videoOrientation
      )
    }
  }
    
  // MARK: - Convenience Methods
  private func addComponents () {
    #if swift(>=4.2)
    let notificationName = UIDevice.orientationDidChangeNotification
    #else
    let notificationName = NSNotification.Name.UIDeviceOrientationDidChange
    #endif
    
    NotificationCenter.default.addObserver(
      self,
      selector: #selector(self.setNeedsUpdateOrientation),
      name: notificationName,
      object: nil
    )
    
    addSubview(cameraView)
    
    if let ov = overlayView {
      addSubview(ov)
    }
    
    if let scb = switchCameraButton {
      addSubview(scb)
    }
    
    if let ttb = toggleTorchButton {
      addSubview(ttb)
    }
    
    if let cb = cancelButton {
      addSubview(cb)
    }
    
    if let reader = reader {
      cameraView.layer.insertSublayer(reader.previewLayer, at: 0)
      setNeedsUpdateOrientation()
    }
  }
}


extension QRCodeReaderView: QRCodeReaderLifeCycleDelegate {
  
  func readerDidStartScanning () {
    setNeedsUpdateOrientation()
  }
    
  func readerDidStopScanning () {}
}
