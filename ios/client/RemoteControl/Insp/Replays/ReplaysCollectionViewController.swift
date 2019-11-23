//
//  ReplaysCollectionViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 03/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

private let reuseIdentifier = "replay"

class ReplaysCollectionViewController: UICollectionViewController {

  override func viewDidLoad () {
    super.viewDidLoad()

    // Uncomment the following line to preserve selection between presentations
    // self.clearsSelectionOnViewWillAppear = false

    // Register cell classes
    collectionView!.register(UICollectionViewCell.self, forCellWithReuseIdentifier: reuseIdentifier)

    // Do any additional setup after loading the view.
  }

  override func numberOfSections (in collectionView: UICollectionView) -> Int {
    // #warning Incomplete implementation, return the number of sections
    return 1
  }


  override func collectionView (_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
    // #warning Incomplete implementation, return the number of items
    return 1
  }

  override func collectionView (_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
    return collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath)
  }
}
