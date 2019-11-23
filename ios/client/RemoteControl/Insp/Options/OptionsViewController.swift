//
//  OptionsViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

enum OptionsActions {
    case namesOptions
    case pisteOptions
    case videoReplays
    case timerPassives
    case phrases
    case disconnect

}

let optionsList: [(title: String,action: OptionsActions)] = [
    ("Names", .namesOptions),
    ("Piste", .pisteOptions),
    ("Video Replays", .videoReplays),
    ("Timer passives", .timerPassives),
    ("Phrases", .phrases),
    ("Disconnect", .disconnect),
]

let protoCellName = "options"

class OptionsViewController: UITableViewController {

    var animated = true

    @IBAction func goBack(_ sender: Any) {
        dismiss(animated: self.animated, completion: nil)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.animated = animated
    }

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

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return optionsList.count
    }


    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: protoCellName, for: indexPath)
        let (name, id) = optionsList[indexPath.row]
        cell.textLabel?.text = name
        switch id {
        case .disconnect:
        cell.textLabel?.textColor = self.view.tintColor
        default:
            break
        }
        // Configure the cell...

        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let (_, id) = optionsList[indexPath.row]

        switch id {
        case .disconnect:
            print(id)
            goToRoot()
        default:
            print("section: \(indexPath.section)")
            print("row: \(indexPath.row)")
        }
    }

    private func goToRoot() {
      rs.connection.disconnect()
      performSegue(withIdentifier: "toRoot", sender: self)
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
