package ru.inspirationpoint.remotecontrol.manager.handlers.EthernetCommandsHelpers;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

import ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.EthernetInfoFighter;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.EthernetInfoMain;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FighterData;

import static ru.inspirationpoint.remotecontrol.manager.constants.ISEMIContract.ETH_PRIORITY_LEFT;
import static ru.inspirationpoint.remotecontrol.manager.constants.ISEMIContract.ETH_PRIORITY_RIGHT;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PERSON_TYPE_LEFT;
import static ru.inspirationpoint.remotecontrol.manager.constants.commands.CommandsContract.PERSON_TYPE_RIGHT;

public class EthernetDispHandler {

    private EthernetInfoMain infoMain = new EthernetInfoMain();
    private EthernetInfoFighter infoRight = new EthernetInfoFighter();
    private EthernetInfoFighter infoLeft = new EthernetInfoFighter();
    private FightData ethFightData = new FightData();
    private FighterData ethLeftFighter = new FighterData("", "");
    private FighterData ethRightFighter = new FighterData("", "");

    public void fightInfoReset() {
        infoMain = new EthernetInfoMain();
        infoRight = new EthernetInfoFighter();
        infoLeft = new EthernetInfoFighter();
    }

    public boolean updateFromCommand(String cmd) {
        Log.wtf("DISP CMD", cmd);
        String[] mainParts = TextUtils.split(cmd, "%");
        if (mainParts.length == 4) {
            fightInfoReset();
            String[] infoParts = TextUtils.split(mainParts[0], "\\|");
            String[] rightParts = TextUtils.split(mainParts[1], "\\|");
            String[] leftParts = TextUtils.split(mainParts[2], "\\|");
            Log.wtf("PARTS SIZE", infoParts.length + "||" + rightParts.length + "|" + leftParts.length);
            if (infoParts.length == 19) {
                try {
                    infoMain.setProtocol(infoParts[1]);
//            infoMain.setCompetition(infoParts[2]);
                    infoMain.setPiste(infoParts[3]);
                    infoMain.setCompetition(infoParts[4]);
                    if (!TextUtils.isEmpty(infoParts[5])) {
                        infoMain.setPhase(Integer.parseInt(infoParts[5]));
                    }
                    infoMain.setPoulTab(infoParts[6]);
                    if (!TextUtils.isEmpty(infoParts[7])) {
                        infoMain.setMatchNumber(Integer.parseInt(infoParts[7]));
                    }
                    if (!TextUtils.isEmpty(infoParts[8])) {
                        infoMain.setRound(Integer.parseInt(infoParts[8]));
                    } else return false;
                    infoMain.setTime(infoParts[9]);
                    infoMain.setStopwatch(infoParts[10]);
                    infoMain.setType(infoParts[11]);
                    infoMain.setWeapon(infoParts[12]);
                    infoMain.setPriority(infoParts[13]);

                    ethFightData.setId(infoMain.getCompetition() + "_" + infoMain.getPiste());
                    switch (infoMain.getPriority()) {
                        case ETH_PRIORITY_LEFT:
                            ethFightData.setmPriority(PERSON_TYPE_LEFT);
                            break;
                        case ETH_PRIORITY_RIGHT:
                            ethFightData.setmPriority(PERSON_TYPE_RIGHT);
                            break;
                    }
                    ethFightData.setmCurrentPeriod(infoMain.getRound());
                    int min = Integer.parseInt(infoMain.getStopwatch().split(":")[0]);
                    int sec = Integer.parseInt(infoMain.getStopwatch().split(":")[1]);
                    ethFightData.setmCurrentTime((min*60+sec)*1000);
                } catch (NumberFormatException e) {
                    infoMain = null;
                    return false;
                }
            } else {
                infoMain = null;
                return false;
            }
//            infoMain.setState(infoParts[14]);
            if (rightParts.length == 13) {
                try {
                    infoRight.setId(rightParts[1]);
                    infoRight.setName(rightParts[2]);
                    infoRight.setNation(rightParts[3]);
                    if (!TextUtils.isEmpty(rightParts[4])) {
                        infoRight.setScore(Integer.parseInt(rightParts[4]));
                    } else {
                        return false;
                    }
                    if (infoRight.getName().isEmpty()) {
                        return false;
                    }
                    infoRight.setStatus(rightParts[5]);
                    if (!TextUtils.isEmpty(rightParts[6])) {
                        infoRight.setYellowNum(Integer.parseInt(rightParts[6]));
                    }
                    if (!TextUtils.isEmpty(rightParts[7])) {
                        infoRight.setRedNum(Integer.parseInt(rightParts[7]));
                    }
                    if (!TextUtils.isEmpty(rightParts[8])) {
                        infoRight.setLight(Integer.parseInt(rightParts[8]));
                    }
                    if (!TextUtils.isEmpty(rightParts[9])) {
                        infoRight.setWhiteLight(Integer.parseInt(rightParts[9]));
                    }
                    if (!TextUtils.isEmpty(rightParts[10])) {
                        infoRight.setMedical(Integer.parseInt(rightParts[10]));
                    }
                    infoRight.setReserve(rightParts[11]);

                    ethRightFighter = new FighterData(infoRight.getId(), infoRight.getName());
                    ethRightFighter.setScore(infoRight.getScore());
                    if (infoRight.getRedNum() != 0) {
                        for (int i = 0; i < infoRight.getRedNum(); i++) {
                            ethRightFighter.setCard(CommonConstants.CardStatus.CardStatus_Red);
                        }
                    } else if (infoRight.getYellowNum() != 0) {
                        ethRightFighter.setCard(CommonConstants.CardStatus.CardStatus_Yellow);
                    }
                    ethFightData.setmRightFighterData(ethRightFighter);
                } catch (NumberFormatException e) {
                    infoRight = null;
                    return false;
                }
            } else {
                infoRight = null;
                return false;
            }

            if (leftParts.length == 13) {
                try {
                    infoLeft.setId(leftParts[1]);
                    infoLeft.setName(leftParts[2]);
                    infoLeft.setNation(leftParts[3]);
                    if (!TextUtils.isEmpty(leftParts[4])) {
                        infoLeft.setScore(Integer.parseInt(leftParts[4]));
                    } else {
                        return false;
                    }
                    if (infoLeft.getName().isEmpty()) {
                        return false;
                    }
                    infoLeft.setStatus(leftParts[5]);
                    if (!TextUtils.isEmpty(leftParts[6])) {
                        infoLeft.setYellowNum(Integer.parseInt(leftParts[6]));
                    }
                    if (!TextUtils.isEmpty(leftParts[7])) {
                        infoLeft.setRedNum(Integer.parseInt(leftParts[7]));
                    }
                    if (!TextUtils.isEmpty(leftParts[8])) {
                        infoLeft.setLight(Integer.parseInt(leftParts[8]));
                    }
                    if (!TextUtils.isEmpty(leftParts[9])) {
                        infoLeft.setWhiteLight(Integer.parseInt(leftParts[9]));
                    }
                    if (!TextUtils.isEmpty(leftParts[10])) {
                        infoLeft.setMedical(Integer.parseInt(leftParts[10]));
                    }
                    infoLeft.setReserve(leftParts[11]);

                    ethLeftFighter = new FighterData(infoLeft.getId(), infoLeft.getName());
                    ethLeftFighter.setScore(infoLeft.getScore());
                    if (infoLeft.getRedNum() != 0) {
                        for (int i = 0; i < infoLeft.getRedNum(); i++) {
                            ethLeftFighter.setCard(CommonConstants.CardStatus.CardStatus_Red);
                        }
                    } else if (infoLeft.getYellowNum() != 0) {
                        ethLeftFighter.setCard(CommonConstants.CardStatus.CardStatus_Yellow);
                    }
                    ethFightData.setmLeftFighterData(ethLeftFighter);
                } catch (NumberFormatException e) {
                    infoLeft = null;
                    return false;
                }
            } else {
                infoLeft = null;
                return false;
            }
            return true;
        }
        return false;
    }

    public EthernetInfoMain getInfoMain() {
        return infoMain;
    }

    public EthernetInfoFighter getInfoRight() {
        return infoRight;
    }

    public EthernetInfoFighter getInfoLeft() {
        return infoLeft;
    }

    public FightData getEthFightData() {
        return ethFightData;
    }

    public FighterData getEthLeftFighter() {
        return ethLeftFighter;
    }

    public FighterData getEthRightFighter() {
        return ethRightFighter;
    }
}
