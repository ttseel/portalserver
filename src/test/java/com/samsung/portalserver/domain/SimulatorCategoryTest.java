package com.samsung.portalserver.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimulatorCategoryTest {

    @Test
    void getSimulationNameTest() {
        assertThat(SimulatorCategory.MCPSIM.name()).isEqualTo("MCPSIM");
        assertThat(SimulatorCategory.SeeFlow.name()).isEqualTo("SeeFlow");
        assertThat(SimulatorCategory.REMOTE_SIM.name()).isEqualTo("REMOTE_SIM");
    }
}