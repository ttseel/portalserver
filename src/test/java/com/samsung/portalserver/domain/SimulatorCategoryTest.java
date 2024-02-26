package com.samsung.portalserver.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.samsung.portalserver.simulation.SimulatorCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimulatorCategoryTest {

    @DisplayName("시뮬레이터 엔진 이름 체크")
    @Test
    void getSimulationNameTest() {
        assertThat(SimulatorCategory.MCPSIM.name()).isEqualTo("MCPSIM");
        assertThat(SimulatorCategory.SeeFlow.name()).isEqualTo("SeeFlow");
        assertThat(SimulatorCategory.REMOTE_SIM.name()).isEqualTo("REMOTE_SIM");
    }
}