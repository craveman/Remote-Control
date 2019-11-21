//
//  ViewController.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import QRCodeReader
import Sm02Client


class QrViewController: UIViewController {

  let rs = RemoteService.shared

  @IBOutlet weak var previewView: QRCodeReaderView! {
    didSet {
      previewView.setupComponents(with: QRCodeReaderViewControllerBuilder {
        $0.reader = reader
        $0.showTorchButton = false
        $0.showSwitchCameraButton = false
        $0.showCancelButton = false
        $0.showOverlayView = true
        $0.rectOfInterest = CGRect(x: 0.2, y: 0.2, width: 0.6, height: 0.6)
      })
    }
  }

  lazy var reader: QRCodeReader = QRCodeReader()
  var openedAlertCallback: (() -> Void)?
  var onSuccess: (() -> Void) = { print ("Not defined success action") }

  override func viewDidAppear (_ animated: Bool) {
    super.viewDidAppear(animated)
    print("QrViewController::viewDidAppear")
    startScanner()
  }

  public func stopScanner() {
    DispatchQueue.main.async {
      self.reader.stopScanning()
    }
  }

  public func startScanner() {
    print("startScanner")
    guard checkScanPermissions(), !reader.isRunning else {
      return
    }
    print("QrViewController::viewDidAppear::passedGuard")

    reader.didFindCode = { [weak self] (result) in
      print("Completion with result: \(result.value) of type \(result.metadataType)")
      guard let self = self else {
        print("guard self = self")
        return
      }
      self.reader.stopScanning()

      switch RemoteAddress.parse(url: result.value) {
      case .success(let access):
        self.process(success: access)
      case .failure(let reason):
        self.process(error: reason)
      }
    }

    reader.startScanning()
  }

  override func viewWillDisappear (_ animated: Bool) {
    if openedAlertCallback != nil {
      openedAlertCallback!()
      openedAlertCallback = nil
    }
    super.viewWillDisappear(animated)
  }

  override func viewDidDisappear (_ animated: Bool) {
    super.viewDidDisappear(animated)
    DispatchQueue.main.async {
      self.reader.stopScanning()
    }
  }

  private func process (success remote: RemoteAddress) {
    let alert = UIAlertController(
      title: "QR-код распознан",
      message: "\(remote)",
      preferredStyle: .alert
    )
    print("remoteServer: \(remote)")

    print("addAction")

    alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: { [weak self] (action) in
      print("connecting to \(remote)")
      switch self?.rs.connection.connect(to: remote) {
      case .success(_):
        print("self?.onSuccess")
        self?.onSuccess()
      case .failure(ConnectionError.connectionTimeout(let timeout)):
        let message = "Неудалось подключиться к \(remote.ip). Таймаут на подключение (\(timeout) сек) иссяк"
        self?.showError(text: message)
        case .failure(_):
        print("Another connections error")
      default:
        print("Smth happened")
      }
      print("finished connecting")
    }))
    print("present")
    present(alert, animated: true, completion: nil)
  }

  private func showError (text: String) {
    let alert = UIAlertController(
      title: "Ошибка соединения",
      message: "\(text)",
      preferredStyle: .alert
    )
    alert.addAction(UIAlertAction(title: "Попробовать снова", style: .cancel, handler: { [weak self] (action) in
      self?.reader.startScanning()
    }))
    present(alert, animated: true, completion: nil)
  }

  private func process (error: RemoteAddress.ParsingError) {
    let alert = UIAlertController(
      title: "QR-код не распознан",
      message: "\(error)",
      preferredStyle: .alert
    )

    alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: { [weak self] (action) in
      self?.reader.startScanning()
    }))

    present(alert, animated: true, completion: nil)
  }

  private func bindAlert(_ alert: UIAlertController?) {
    openedAlertCallback = {
      alert?.dismiss(animated: false)
    }
  }

  private func checkScanPermissions () -> Bool {
    do {
      return try QRCodeReader.supportsMetadataObjectTypes()
    } catch let error as NSError {
      let alert: UIAlertController

      switch error.code {
      case -11852:
        alert = UIAlertController(title: "Error", message: "This app is not authorized to use Back Camera.", preferredStyle: .alert)

        alert.addAction(UIAlertAction(title: "Setting", style: .default, handler: { (_) in
          DispatchQueue.main.async {
            if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
              UIApplication.shared.open(settingsURL)
            }
          }
        }))

        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
      default:
        alert = UIAlertController(title: "Error", message: "Reader not supported by the current device", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: nil))
        self.bindAlert(alert)
      }

      present(alert, animated: true, completion: nil)
      return false
    }
  }
}
