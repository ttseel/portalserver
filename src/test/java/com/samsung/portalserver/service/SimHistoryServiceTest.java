package com.samsung.portalserver.service;

import com.samsung.portalserver.repository.SimHistoryRepositoryImpl;
import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletResponseWrapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SimHistoryServiceTest {

    @Autowired
    SimHistoryService simHistoryService;

    @Test
    void downloadMyHistory() {
//        simHistoryService.downloadMyHistory("testUser", "testSimulator", "testScenario", new Response());
    }
}