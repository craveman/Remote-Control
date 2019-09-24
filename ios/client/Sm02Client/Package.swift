// swift-tools-version:5.1
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
  name: "Sm02Client",

  products: [
    .library(
      name: "Sm02Client",
      targets: ["Sm02Client"]
    ),
  ],

  dependencies: [
    .package(url: "https://github.com/apple/swift-nio.git", .exact("2.8.0")),
    .package(url: "https://github.com/apple/swift-nio-extras.git", .exact("1.2.0")),
  ],

  targets: [
    .target(
      name: "Sm02Client",
      dependencies: [
        "NIO",
        "NIOExtras",
      ]
    ),
    .testTarget(
      name: "Sm02ClientTests",
      dependencies: ["Sm02Client"]
    ),
  ]
)
