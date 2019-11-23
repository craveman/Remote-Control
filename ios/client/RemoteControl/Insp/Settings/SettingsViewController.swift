//
//  SettingsViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 04/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//
import UIKit

enum SettingsActions {
    case finishFight
    case restart
    case points
    case priorities
    case weapons
}

let settingsList: [(title: String, action: SettingsActions)] = [
    ("Finish and Reset", .finishFight),
    ("Restart", .restart),
    ("Points", .points),
    ("Priorities", .priorities),
    ("Weapons", .weapons)
]

let settingCellId = "settingsCell"

class SettingsViewController: UITableViewController {

  override func viewDidLoad () {
    super.viewDidLoad()

    // Uncomment the following line to preserve selection between presentations
    // self.clearsSelectionOnViewWillAppear = false

    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem
  }

  override func numberOfSections (in tableView: UITableView) -> Int {
    // #warning Incomplete implementation, return the number of sections
    return 1
  }

  override func tableView (_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    //Change the selected background view of the cell.
    let (_, action) = settingsList[indexPath.row]

    doAction(action)

    tableView.deselectRow(at: indexPath, animated: true)
  }

  override func tableView (_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    // #warning Incomplete implementation, return the number of rows
    return settingsList.count
  }

  override func tableView (_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
    let cell = tableView.dequeueReusableCell(withIdentifier: settingCellId, for: indexPath)
    let (name, _) = settingsList[indexPath.row]
    cell.textLabel?.text = name
    // Configure the cell...

    return cell
  }

  private func doAction (_ action: SettingsActions) {
    let stbrd = UIStoryboard(name: "SettingsStoryboard", bundle: nil)

    switch action {
    case .finishFight:
      rs.competition.reset()
      tabBarController?.selectedIndex = 0
    case .points:
      break
    case .priorities:
      let controller = stbrd.instantiateViewController(withIdentifier: "PrioritySelectViewController")
      if let psvc = controller as? PrioritySelectViewController {
        present(psvc, animated: true, completion: nil)
      }
    case .weapons:
      let controller = stbrd.instantiateViewController(withIdentifier: "WeaponSelectViewController")
      if let wsvc = controller as? WeaponSelectViewController {
          present(wsvc, animated: true, completion: nil)
      }
    case .restart:
      break
    }
  }
}
