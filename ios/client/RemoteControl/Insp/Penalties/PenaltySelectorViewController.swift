//
//  PenaltySelectorViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 04/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import Sm02Client

class PenaltySelectorViewController: UIViewController {
    private var maxIncreaseLevel = 0;

    private var currentPenaltyCard: StatusCard?
    private var enabledCardsList: [StatusCard] = [];

    var penaltyType: PenaltiesTypes = .basic {
        didSet(type) {
            updateList()
            updateView()

        }
    }

    var personType: PersonType = .none;

    @IBOutlet weak var blackCard: UIButton! {
        didSet {
            self.blackCard?.layer.cornerRadius = UIGlobals.cardCornerRadius
        }
    }

    @IBOutlet weak var resetPenaltyCard: UIButton! {
        didSet {
            self.resetPenaltyCard?.layer.cornerRadius = UIGlobals.cardCornerRadius
            self.resetPenaltyCard?.layer.borderColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1)
            self.resetPenaltyCard?.layer.borderWidth = CGFloat(1)
        }
    }

    @IBOutlet weak var increaseCard: UIButton! {
        didSet {
            self.increaseCard?.layer.cornerRadius = UIGlobals.cardCornerRadius
        }
    }

    @IBAction func blackSelected(_ sender: UIButton) {
        if currentPenaltyCard == enabledCardsList.last {
            return
        }
        currentPenaltyCard = enabledCardsList.last
        print("black penalty")
        remoteAction(currentPenaltyCard!)
        UITools.disableButtonForTime(sender)
    }

    @IBAction func resetPenalty(_ sender: UIButton) {
        print("reset")
        currentPenaltyCard = enabledCardsList.first
        remoteAction(currentPenaltyCard!)
        UITools.disableButtonForTime(sender)
    }

    @IBAction func increaseSelected(_ sender: UIButton) {

        if currentPenaltyCard == nil || isBlackActive() {
            return
        }
        print("penalty \(sender.isEnabled)")
        let penaltyIndex = enabledCardsList.firstIndex(of: currentPenaltyCard!) ?? -1
        if penaltyIndex < maxIncreaseLevel - 1 {
            currentPenaltyCard = enabledCardsList[penaltyIndex + 1]
        }
        remoteAction(currentPenaltyCard!)
        UITools.disableButtonForTime(sender)
    }


    override func viewDidLoad() {
        super.viewDidLoad()

        updateView()
        // Do any additional setup after loading the view.
    }

    private func updateList() -> Void {
        switch penaltyType {
        case .basic:
            enabledCardsList = [.none, .yellow, .red, .black]
        case .passive:
            enabledCardsList = [.passiveNone, .passiveYellow, .passiveRed, .passiveBlack]
        }
        currentPenaltyCard = enabledCardsList.first
        maxIncreaseLevel = enabledCardsList.count - 1

    }

    private func updateView() {
        if currentPenaltyCard != nil {
            let penaltyIndex = enabledCardsList.firstIndex(of: currentPenaltyCard!) ?? -1
            blackCard?.backgroundColor = isBlackActive() ? #colorLiteral(red: 0.2549019754, green: 0.2745098174, blue: 0.3019607961, alpha: 1) : #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1)
            increaseCard?.backgroundColor = isBlackActive() ? #colorLiteral(red: 0.8039215803, green: 0.8039215803, blue: 0.8039215803, alpha: 1) : penaltyIndex == 0 ? #colorLiteral(red: 0.9994240403, green: 0.9855536819, blue: 0, alpha: 1) : #colorLiteral(red: 1, green: 0.1491314173, blue: 0, alpha: 1);
            increaseCard?.isEnabled = !isBlackActive() && penaltyIndex <= maxIncreaseLevel

        }

        setCardsTitles()
    }

    private func setCardsTitles() {

        let markedCards = [increaseCard, blackCard]
        var title = "-", hidden = true;

        switch penaltyType {
        case .passive: title = "P"; hidden = false
        case .basic: title = ""; hidden = false
        }
        markedCards.forEach { btn in
            if btn != nil {
                btn!.setTitle(title, for: .normal)
                btn!.isHidden = hidden
            }

        }
    }

    private func remoteAction(_ card: StatusCard) {
        print("send: \(currentPenaltyCard!) \(personType)")
        let _ = Outbound.setCard(person: personType, status: card)
        updateView()
    }

    private func isBlackActive() -> Bool {
        return currentPenaltyCard == enabledCardsList.last
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
