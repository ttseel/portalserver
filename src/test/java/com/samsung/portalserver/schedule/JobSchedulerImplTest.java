package com.samsung.portalserver.schedule;

import com.samsung.portalserver.schedule.job.Job;
import com.samsung.portalserver.schedule.job.SimulationJob;
import com.samsung.portalserver.service.FileService;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

@SpringBootTest
class JobSchedulerImplTest {

    FileService fileService = new FileService();
    //    JobSchedulerImpl jobScheduler = new JobSchedulerImpl();
    @Autowired
    JobSchedulerImpl jobScheduler;

    @Test
    void tryScheduling() {
        jobScheduler.tryScheduling();
    }

    @Test
    void executeJob() throws IOException {
        Job simulationJob = new SimulationJob(null);
        jobScheduler.executeJob(simulationJob);
    }

    @Test
    void runSimulation() {
        Runtime rt = Runtime.getRuntime();

        try {
//            Process p = rt.exec("jps -l");
            Process p = rt.exec(
                "java -jar " + FileService.SIMULATOR_DIR_PATH + FileService.DIR_DELIMETER
                    + "mocksim.jar");

            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while (true) {
                String s = br.readLine();
                if (s == null) {
                    break;
                }
                System.out.println(s);
            }

            //taskkill /f /pid [pid number]

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}