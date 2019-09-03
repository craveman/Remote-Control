//
//  NetworkSelectController.swift
//  Insp1
//
//  Created by Sergei Andreev on 10/08/2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SystemConfiguration.CaptiveNetwork
import UIKit
import NotificationCenter

let cellProtoId = "networkName"

var networksList = ["SM 156.02", "SM 156.03", "SM 156.04"]
let currentSsid: String? = networksList[1];

class NetworkSelectController: UITableViewController {

    @IBOutlet weak var navBar: UINavigationBar!
    @IBOutlet weak var skipButton: UIBarButtonItem!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print(currentSSIDs(), currentSsid ?? "-");
        addSkipButtonIfConneted()
//        self.tabBarController?.navigationItem.hidesBackButton = true
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        if currentSsid != nil {
            return 2;
        }
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        if currentSsid != nil {
            if section == 1 {
                return networksList.count - 1;
            }
            
            return 1;
        }
        
        return networksList.count
    }
    
    override func tableView(_ tv: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tv.dequeueReusableCell(withIdentifier: cellProtoId, for: indexPath)
        let section = indexPath.section;
        cell.textLabel?.isEnabled = true;
        if currentSsid != nil {
            
            let rest = networksList.filter { $0 != currentSsid! }
            
            if section == 1 {
                cell.textLabel?.text = rest[indexPath.row]
                return cell;
            }
            
            cell.textLabel?.text = currentSsid!
            cell.textLabel?.isEnabled = false;
            return cell;
        }
        
       cell.textLabel?.text = networksList[indexPath.row]
        
        return cell;
    }

    /*
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "reuseIdentifier", for: indexPath)

        // Configure the cell...

        return cell
    }
    */

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
    func currentSSIDs() -> [String] {
        guard let interfaceNames = CNCopySupportedInterfaces() as? [String] else {
            return []
        }
        return interfaceNames.compactMap { name in
            guard let info = CNCopyCurrentNetworkInfo(name as CFString) as? [String:AnyObject] else {
                return nil
            }
            guard let ssid = info[kCNNetworkInfoKeySSID as String] as? String else {
                return nil
            }
            return ssid
        }
    }
    
    func addSkipButtonIfConneted() -> Void {
        var ctrlItms: [UIBarButtonItem] = []
        if currentSsid != nil {
            ctrlItms = [skipButton]
        }
         self.navBar.items?.first?.leftBarButtonItems = ctrlItms
    }

}
