// swift-tools-version:5.0
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
  name: "core",

  platforms: [
    .macOS(.v10_12),
    .iOS(.v10),
  ],

  products: [
    .library(
      name: "Network",
      targets: ["Network"]
    ),
    .library(
      name: "Logging",
      targets: ["Logging"]
    ),
  ],

  dependencies: [
    .package(url: "https://github.com/apple/swift-nio.git", from: "2.6.1"),
  ],

  targets: [
    .target(
      name: "Network",
      dependencies: [
        "NIO"
      ]
    ),
    .target(
      name: "Logging",
      dependencies: []
    ),

    .testTarget(
      name: "NetworkTests",
      dependencies: [
        "Network",
        "Logging"
      ]
    ),
  ],

  swiftLanguageVersions: [.v5]
)
