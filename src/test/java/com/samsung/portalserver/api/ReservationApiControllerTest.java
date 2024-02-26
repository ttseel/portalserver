package com.samsung.portalserver.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import com.samsung.portalserver.TestConfig;
import com.samsung.portalserver.api.dto.NewReservationDto;
import com.samsung.portalserver.api.dto.StatusAndMessageDto;
import com.samsung.portalserver.domain.SimBoard;
import com.samsung.portalserver.service.ReservationService;
import com.samsung.portalserver.service.SimBoardService;
import com.samsung.portalserver.simulation.SimulatorCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class ReservationApiControllerTest {

    @Autowired
    private ReservationApiController reservationApiController;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private SimBoardApiController simBoardApiController;
    @Autowired
    private SimBoardService simBoardService;
    @InjectMocks
    private ReservationApiController mockReservationApiController;
    @Mock
    private ReservationService mockReservationService;

    /**
     * Unit Test
     *
     * @throws Exception
     */
    @DisplayName("New Scenario 예약 테스트")
    @Test
    void reserveNewSceanrioTest() throws Exception {
        // build
        NewReservationDto newReservationDto = buildNewReservationDto();
        String groupPath = mockReservationApiController.makeGroupPath(newReservationDto);
        doReturn(true).when(mockReservationService)
            .saveScenarioFile(groupPath, newReservationDto.getFslFile(),
                newReservationDto.getFssFiles());

        // operate
        StatusAndMessageDto statusAndMessageDto = mockReservationApiController.reserveNewSceanrio(
            newReservationDto);

        // check
        assertTrue(statusAndMessageDto.getStatus());
    }

    private NewReservationDto buildNewReservationDto() {
        String newTester = TestConfig.USER + "_NEW";
        List<String> newFssName = new ArrayList<>();
        newFssName.add(TestConfig.SCENARIO + "_NEW");

        NewReservationDto newReservationDto = new NewReservationDto();
        newReservationDto.setUser(newTester);
        newReservationDto.setSimulator(TestConfig.SIMULATOR);
        newReservationDto.setVersion(TestConfig.VERSION);
        newReservationDto.setFslName(TestConfig.FSL_NAME);
        newReservationDto.setFslFile(null);
        newReservationDto.setFssNameList(newFssName);
        newReservationDto.setFssFiles(null);

        return newReservationDto;
    }


    /**
     * Integeration Test
     *
     * @throws Exception
     */
    @DisplayName("SIM BOARD 예약 취소 테스트")
    @Test
    void cancelReservationTest() throws Exception {
        // build
        long recordNo = getTesterRecordNo();

        // operate
        reservationApiController.cancelReservation(TestConfig.USER, TestConfig.SIMULATOR,
            TestConfig.SCENARIO);

        // check
        assertFalse(simBoardService.readUniqueRecord(recordNo).isPresent());
    }

    private long getTesterRecordNo() {
        long recordNo;
        Optional<SimBoard> simBoard = simBoardService.readUniqueRecord(TestConfig.USER,
            TestConfig.SIMULATOR, TestConfig.SCENARIO);
        if (simBoard.isPresent()) {
            recordNo = simBoard.get().getNo();
        } else {
            throw new IllegalStateException("SIMBOARD에 TESTER 계정이 존재하지 않음");
        }

        return recordNo;
    }

    /**
     * Integeration Test
     *
     * @throws Exception
     */
    @DisplayName("현재 Local에 시뮬레이터 엔진 있는지 체크")
    @Test
    void getAvailableVersionListTest() throws Exception {
        // build
        String simulator = SimulatorCategory.MCPSIM.name();

        // operate
        Map<String, List<String>> versionList = reservationApiController.getVersionList(simulator);

        // check
        assertTrue(versionList.get(simulator).size() > 0);
    }

    @Test
    void validatePossibleToNewReservation() {
        for (SimulatorCategory value : SimulatorCategory.values()) {
            System.out.println(value.name());
        }
    }
}