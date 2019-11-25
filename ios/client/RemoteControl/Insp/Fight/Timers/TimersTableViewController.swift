//
//  TimersTableViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 03/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

enum TimersActions {

  case shortBreak
  case fightTimer
  case period
  case fightTimerSettings
  case passiveTimerSettings
  case medicineBreak
}

let timers: [(title: String, action: TimersActions)] = [
  ("1 minute break", .shortBreak),
  ("Fight timer", .fightTimer),
  ("Period", .period),
  ("Fight timer settings", .fightTimerSettings),
  ("Passive timer settings", .passiveTimerSettings),
  ("Medicine break", .medicineBreak)
]

let shortBreakTimer = UInt32(60);
let medBreakTimer = UInt32(5 * 60);

class TimersTableViewController: UITableViewController {

  var fightNavigationBack: (() -> Void)? = nil;

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
    let (_, action) = timers[indexPath.row]

    doAction(action)

    tableView.deselectRow(at: indexPath, animated: true)
  }

  override func tableView (_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    // #warning Incomplete implementation, return the number of rows
    return timers.count
  }

  override func tableView (_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
    let cell = tableView.dequeueReusableCell(withIdentifier: "timersCell", for: indexPath)
    let (title, _) = timers[indexPath.row]
    cell.textLabel?.text = "\(title)"
    return cell
  }

  private func doAction (_ action: TimersActions) {
    let stbrd = UIStoryboard(name: "TimersStoryboard", bundle: nil)
    print(action)
    switch action {
    case .shortBreak:
      rs.setTimer(time: shortBreakTimer, mode: .pause)
      backToFight()
    case .fightTimer:
      let vc = stbrd.instantiateViewController(withIdentifier: "Picker") as? FightTimePickerViewController
      if vc != nil {
          (vc! as UIViewController).modalPresentationStyle = .pageSheet
          present(vc!, animated: true, completion: { [weak self] in
              self?.backToFight()
          })
      }
    case .period:
      break
    case .fightTimerSettings:
      break
    case .passiveTimerSettings:
      break
    case .medicineBreak:
      rs.setTimer(time: medBreakTimer, mode: .medicine)
      backToFight()
    }
  }

  private func backToFight () {
    if fightNavigationBack != nil {
      fightNavigationBack!()
    }
  }
}
