// swift-tools-version:5.0
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
  name: "core",

  platforms: [
    .macOS(.v10_14),
    .iOS(.v12),
  ],

  products: [
    .library(
      name: "RemoteControl_Network",
      targets: [
        "RemoteControl_Network",
      ]
    ),
    .library(
      name: "RemoteControl_Logging",
      targets: [
        "RemoteControl_Logging",
      ]
    ),
  ],

  dependencies: [
    .package(url: "https://github.com/apple/swift-nio.git", from: "2.6.1"),
    .package(url: "https://github.com/luoxiu/Schedule", from: "2.0.3"),
  ],

  targets: [
    .target(
      name: "RemoteControl_Network",
      dependencies: [
        "NIO",
      ],
      path: "./Sources/Network"
    ),
    .target(
      name: "RemoteControl_Logging",
      dependencies: [],
      path: "./Sources/Logging"
    ),

    .testTarget(
      name: "RemoteControl_NetworkTests",
      dependencies: [
        "SM02Server",
      ],
      path: "./Tests/NetworkTests"
    ),
    .testTarget(
      name: "SM02Server",
      dependencies: [
        "RemoteControl_Logging",
        "RemoteControl_Network",
        "Schedule"
      ]
    ),
  ],

  swiftLanguageVersions: [.v5]
)
