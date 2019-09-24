// swift-tools-version:5.1
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
  name: "sm02",

  dependencies: [],

  targets: [
    .target(
      name: "sm02",
      dependencies: []
    ),
    .testTarget(
      name: "sm02Tests",
      dependencies: ["sm02"]
    ),
  ]
)
