//
//  QRCodeReaderResult.swift
//  RemoveMe
//
//  Created by Artem Labazin on 08/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

/**
 The result of the scan with its content value and the corresponding metadata type.
 */
public struct QRCodeReaderResult {
  
  /**
   The error corrected data decoded into a human-readable string.
   */
  public let value: String
  
  /**
   The type of the metadata.
   */
  public let metadataType: String
}
