package com.samsung.portalserver.api;

import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.repository.SimBoardRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TrafficTest {

    @Autowired
    SimBoardRepository simBoardRepository;

    @Test
    void manySelectTest() {

        while (true) {
            Optional<List<SimBoard>> simBoards = simBoardRepository.readByUserAndSimulator("ADMIN",
                "MCPSIM");
            System.out.println(simBoards.get().get(0).getUser());
        }
    }
}
