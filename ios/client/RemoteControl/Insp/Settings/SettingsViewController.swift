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

    let rs = RemoteService.shared

    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //Change the selected background view of the cell.
        let (_, action) = settingsList[indexPath.row]

        doAction(action)

        tableView.deselectRow(at: indexPath, animated: true)
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return settingsList.count
    }


    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: settingCellId, for: indexPath)
        let (name, _) = settingsList[indexPath.row]
        cell.textLabel?.text = name
        // Configure the cell...

        return cell
    }

    private func doAction(_ action: SettingsActions) {
        print(action)
        let stbrd = UIStoryboard(name: "SettingsStoryboard", bundle: nil)

        switch action {
        case .finishFight:
            rs.reset()
            if tabBarController != nil {
                tabBarController!.selectedIndex = 0
            }

        case .points: break
        case .priorities:
            if let psvc = stbrd.instantiateViewController(withIdentifier: "PrioritySelectViewController") as? PrioritySelectViewController {
                present(psvc, animated: true, completion: nil)
            }
        case .weapons:
            if let wsvc = stbrd.instantiateViewController(withIdentifier: "WeaponSelectViewController") as? WeaponSelectViewController {
                present(wsvc, animated: true, completion: nil)
            }

        case .restart:
            // restart
            break
        }
    }


    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
