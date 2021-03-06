package ru.inspirationpoint.remotecontrol.manager.helpers;

import java.util.ArrayList;
import java.util.HashSet;

import ru.inspirationpoint.remotecontrol.manager.SettingsManager;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightActionData;

import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.METRICS;
import static ru.inspirationpoint.remotecontrol.manager.constants.CommonConstants.PHRASES_METRIC;

public class MetricsHelper {

    public Metric createMetric(String name) {
        ArrayList<FightActionData.ActionType> indicators = new ArrayList<>();
        switch (name) {
            case PHRASES_METRIC:
                indicators.add(FightActionData.ActionType.SetScoreLeft);
                indicators.add(FightActionData.ActionType.SetScoreRight);
                return new Metric(PHRASES_METRIC, indicators);
            default:
                return null;
        }
    }

    public void applyMetrics (HashSet<String> names){
        SettingsManager.setValue(METRICS, names);
    }

    public void resetMetrics() {
        SettingsManager.removeValue(METRICS);
    }

}
