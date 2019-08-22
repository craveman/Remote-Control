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
      name: "networking",
      targets: [
        "networking",
      ]
    ),
    .library(
      name: "logging",
      targets: [
        "logging",
      ]
    ),
    .library(
      name: "sm02",
      targets: [
        "sm02",
      ]
    ),
  ],

  dependencies: [
    .package(url: "https://github.com/apple/swift-nio.git", .exact("2.7.0")),
    .package(url: "https://github.com/apple/swift-nio-extras.git", .exact("1.2.0")),
  ],

  targets: [
    .target(
      name: "networking",
      dependencies: [
        "NIO",
        "NIOExtras"
      ]
    ),
    .target(
      name: "logging",
      dependencies: []
    ),
    .target(
      name: "sm02",
      dependencies: [
        "logging",
        "networking"
      ]
    ),

    .testTarget(
      name: "networking_tests",
      dependencies: [
        "sm02",
      ],
      path: "./Tests/networking"
    ),
    .testTarget(
      name: "sm02_tests",
      dependencies: [
        "sm02",
        "NIOTestUtils"
      ],
      path: "./Tests/sm02"
    ),
  ],

  swiftLanguageVersions: [.v5]
)
