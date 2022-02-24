package com.samsung.portalserver.domain;

import lombok.Getter;

public enum SimulatorCategory {
    ALL, AMHS_SIM, MCPSIM, OCS3SIM, OCS4SIM, SeeFlow, REMOTE_SIM, NOT_FOUND;

    public static SimulatorCategory getCategoryByString(String simulator) {
        SimulatorCategory category = NOT_FOUND;
        switch (simulator.toUpperCase()) {
            case "ALL":
                category = ALL;
                break;
            case "AMHS_SIM":
                category = AMHS_SIM;
                break;
            case "MCPSIM":
                category = MCPSIM;
                break;
            case "OCS3SIM":
                category = OCS3SIM;
                break;
            case "OCS4SIM":
                category = OCS4SIM;
                break;
            case "SEEFLOW":
                category = SeeFlow;
                break;
            case "REMOTE_SIM":
                category = REMOTE_SIM;
                break;
            default:
                category = NOT_FOUND;
                break;
        }
        return category;
    }
}
