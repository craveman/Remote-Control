//
//  TimersTableViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 03/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import networking

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
        let (_, action) = timers[indexPath.row]
        
        doAction(action)
        
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return timers.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "timersCell", for: indexPath)
        let (title, _) = timers[indexPath.row]
        cell.textLabel?.text = "\(title)"
        return cell
    }
    
    private func doAction(_ action: TimersActions) {
        let mngr = NetworkManager.shared
        let stbrd = UIStoryboard(name: "TimersStoryboard", bundle: nil)
        print(action)
        switch action {
        case .shortBreak:
            mngr.send(message: Outbound.setTimer(time: shortBreakTimer, mode: .pause))
            backToFight()
        case .fightTimer:
            let vc = stbrd.instantiateViewController(withIdentifier: "Picker") as? FightTimePickerViewController
            if vc != nil {
                (vc! as UIViewController).modalPresentationStyle = .pageSheet
                present(vc!, animated: true, completion: { [weak self] in
                    self?.backToFight()
                })
            }
            
            break
        case .period:
            break
        case .fightTimerSettings:
            break
        case .passiveTimerSettings:
            break
        case .medicineBreak:
            mngr.send(message: Outbound.setTimer(time: medBreakTimer, mode: .medicine))
            backToFight()
        }
    }
    
    private func backToFight() {
        if fightNavigationBack != nil {
            fightNavigationBack!()
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
