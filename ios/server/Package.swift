// swift-tools-version:5.1
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
  name: "sm02",

  dependencies: [
    .package(url: "https://github.com/apple/swift-nio.git", .exact("2.10.1")),
    .package(url: "https://github.com/apple/swift-nio-extras.git", .exact("1.3.2")),
    .package(path: "../client/Sm02Client"),
  ],

  targets: [
    .target(
      name: "sm02",
      dependencies: [
        "NIO",
        "NIOExtras",
        "Sm02Client",
      ]
    ),
    .testTarget(
      name: "sm02Tests",
      dependencies: ["sm02"]
    ),
  ]
)
