package com.samsung.portalserver.schedule;

import static com.samsung.portalserver.service.FileConstants.DIR_DELIMETER;
import static com.samsung.portalserver.service.FileConstants.SIMULATOR_DIR_PATH;

import com.samsung.portalserver.schedule.job.Job;
import com.samsung.portalserver.schedule.job.ScenarioGroupJob;
import com.samsung.portalserver.service.FileService;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class JobSchedulerImplTest {

    FileService fileService = new FileService();
    @Autowired
    JobSchedulerImpl jobScheduler;

    @Test
    void tryScheduling() {
        jobScheduler.tryScheduling();
    }

    @Test
    void executeJob() throws IOException {
        Job simulationJob = new ScenarioGroupJob(null);
        jobScheduler.executeJob(simulationJob);
    }

    @Test
    void runSimulation() {
        Runtime rt = Runtime.getRuntime();

        try {
            Process p = rt.exec("java -jar " + SIMULATOR_DIR_PATH + DIR_DELIMETER + "mocksim.jar");

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

    @Test
    void modifyFslPathTest() throws IOException, JDOMException {
        // 1-2. 파일 파싱 시
        Document document = new SAXBuilder().build(
            new File("src/main/java/com/samsung/portalserver/simulation/files/Test_22.02.07.fsl"));

        // 2. Root Element (catalog)
        Element rootElement = document.getRootElement();

        // 3. Root Element (book)
        List<Element> scenarioElements = rootElement.getChildren();
        for (Element scenario : scenarioElements) {

            if (scenario.getName().equals("xml-list")) {
                System.out.println(scenario.getValue());
            }
        }
    }

    @Test
    void updateTest() throws Exception {

    }
}