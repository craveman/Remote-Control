//
//  LanOptionsTableViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 28.11.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import UIKit

protocol LanOptionsSelector {
  
  func setOptions(_ list: [LanConfigReaderOption]) -> Void
  
  func onOptionSelected(_ fn: @escaping (LanConfigReaderOption) -> Void) -> Void
}

class LanOptionsTableViewController: UITableViewController, LanOptionsSelector {
  private var optionsUpdateTmt: Timer?
  func setOptions(_ list: [LanConfigReaderOption]) {
    nextListComming = true
    optionsUpdateTmt?.invalidate()
    self.tableView.reloadData()
    self.view.setNeedsLayout()
    self.nextOptions = list
//    print("LanOptionsTableViewController setOptions", list)
    optionsUpdateTmt = withDelay({
      self.options = self.nextOptions
      self.nextOptions = []
      self.nextListComming = false
      self.tableView.showsVerticalScrollIndicator = false
      if (self.tableView.visibleSize.height < self.tableView.contentSize.height) {
        self.tableView.showsVerticalScrollIndicator = true
        self.tableView.flashScrollIndicators()
      }
      
      self.tableView.reloadData()
      self.view.setNeedsLayout()
    }, 0.75)
//    print("LanOptionsTableViewController options: ", list)
  }
  
  func onOptionSelected(_ fn: @escaping (LanConfigReaderOption) -> Void) {
    self.selectHandler = fn
  }
  
  var selectHandler: (LanConfigReaderOption) -> Void = {_ in }
  var lastSelected: LanConfigReaderOption? {
    didSet {
      self.tableView.reloadData()
      self.view.setNeedsLayout()
    }
  }
  var options: [LanConfigReaderOption] = []
  var nextOptions: [LanConfigReaderOption] = []
  var nextListComming = false

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
      return options.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "lanOptionReuseIdentifier", for: indexPath)
      guard options.count > indexPath.row else { return cell }
      guard let label = cell.contentView.subviews[0] as? UILabel else {
        return cell
      }
      let opt = options[indexPath.row]
      let nextOption: LanConfigReaderOption? = nextOptions.count > indexPath.row ? nextOptions[indexPath.row] : nil
//      print(nextOptions.count, indexPath.row) // nextOptions.count > indexPath.row ? nextOptions[indexPath.row] : nil
      let willChange = self.nextListComming && nextOption?.address != opt.address
      let isSelected = self.lastSelected != nil ? opt == self.lastSelected! : false
      label.text = opt.name
      label.textColor = UIColor.black
      cell.isUserInteractionEnabled = !opt.busy
      cell.backgroundColor = opt.busy ? UIColor.systemPink : UIColor.clear
      if isSelected {
        cell.backgroundColor = UIColor.gray
      }
      if willChange {
        label.textColor = UIColor.systemGray2
        cell.isUserInteractionEnabled = false
        cell.backgroundColor = UIColor.systemYellow
      }
//      cell = "It's a cell"
        // Configure the cell...

        return cell
    }
  
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
      if (self.lastSelected != nil) {
        return
      }
      guard tableView.cellForRow(at: indexPath) != nil else { return }
      guard options.count > indexPath.row else { return }
      
      let opt = options[indexPath.row]
      self.selectHandler(opt)
      self.lastSelected = opt
      
      withDelay({
        self.lastSelected = nil
        self.tableView.deselectRow(at: indexPath, animated: true)
      }, 2)
      
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
