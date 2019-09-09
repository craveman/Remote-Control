//
//  QRCodeReaderViewContainer.swift
//  RemoveMe
//
//  Created by Artem Labazin on 08/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit


/// The `QRCodeReaderDisplayable` procotol that each view embeded a `QRCodeReaderContainer` must conforms to. It defines the required UI component needed and the mandatory methods.
public protocol QRCodeReaderDisplayable {
  
  /// The view that display video as it is being captured by the camera.
  var cameraView: UIView { get }
  /// A cancel button.
  var cancelButton: UIButton? { get }
  /// A switch camera button.
  var switchCameraButton: UIButton? { get }
  /// A toggle torch button.
  var toggleTorchButton: UIButton? { get }
  /// A guide view upon the camera view
  var overlayView: QRCodeReaderViewOverlay? { get }
  /// Notify the receiver to update its orientation.
  func setNeedsUpdateOrientation()
  
  /**
   Method called by the container to allows you to layout your view properly using the QR code reader builder.
   - Parameter builder: A QR code reader builder.
   */
  func setupComponents (with builder: QRCodeReaderViewControllerBuilder)
}


/// The `QRCodeReaderContainer` structure embed the view displayed by the controller. The embeded view must be conform to the `QRCodeReaderDisplayable` protocol.
public struct QRCodeReaderContainer {
  
  let view: UIView
  let displayable: QRCodeReaderDisplayable
  
  /**
   Creates a QRCode container object that embeds a given displayable view.
   - Parameter displayable: An UIView conforms to the `QRCodeReaderDisplayable` protocol.
   */
  public init<T: QRCodeReaderDisplayable> (displayable: T) where T: UIView {
    self.view = displayable
    self.displayable = displayable
  }
  
  // MARK: - Convenience Methods
  func setupComponents (with builder: QRCodeReaderViewControllerBuilder) {
    displayable.setupComponents(with: builder)
  }
}
