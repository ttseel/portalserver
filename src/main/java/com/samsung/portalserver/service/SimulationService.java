package com.samsung.portalserver.service;

import com.samsung.portalserver.api.dto.UniqueSimulationRecordDto;
import com.samsung.portalserver.schedule.ProgressMonitor;
import com.samsung.portalserver.schedule.SimulationProgressMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SimulationService {

    @Autowired
    private ProgressMonitor progressMonitor;

    public boolean stopSimulation(UniqueSimulationRecordDto dto) {
        return ((SimulationProgressMonitor) progressMonitor).stopSimulation(dto);
    }
}
