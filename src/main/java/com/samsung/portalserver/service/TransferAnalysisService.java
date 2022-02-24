package com.samsung.portalserver.service;

import com.samsung.portalserver.domain.TransferSummary;
import com.samsung.portalserver.repository.TransferRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferAnalysisService {

    private final TransferRepository transferRepository;

    private static final String KEY_DELIMETER = ":";

    public Optional<FromToMatrix> readFromToMatrixBy(String site, String year, String month) {
        Optional<List<TransferSummary>> result = transferRepository.readSummaryBySiteAndYyyymm(site,
            year + month);

        Optional<FromToMatrix> fromToMatrix = Optional.empty();
        if (result.isPresent()) {
            fromToMatrix = convertToFromToMatrix(result);
        }

        return fromToMatrix;
    }

    private Optional<FromToMatrix> convertToFromToMatrix(Optional<List<TransferSummary>> result) {
        Map<String, Boolean> header = createHeader(result);
//        Map<String, Integer> rows = createRows(result);
//        return Optional.ofNullable(new FromToMatrix(header, rows));
        return Optional.empty();
    }

    private Map<String, List<Map<String, Integer>>> createRows(
        Optional<List<TransferSummary>> result, Map<String, Map<String, Boolean>> header) {
        Map<String, List<Map<String, Integer>>> rowsByLine = new ConcurrentHashMap<>();

        result.get().forEach(transferSummary -> {
            if (!rowsByLine.containsKey(transferSummary.getLine())) {
                List<Map<String, Integer>> rows = new ArrayList<>();
            }

            Map<String, String> eachRow = new ConcurrentHashMap<>();
            eachRow.put("FROM_TYPE", transferSummary.getFrom_type());
//            eachRow.put(transferSummary.getTo_type(), transferSummary.)

            header.keySet().forEach(column -> {

            });
//            eachRow.put(transferSummary)
        });

        return rowsByLine;
    }

    private Map<String, Boolean> createHeader(Optional<List<TransferSummary>> result) {
        Map<String, Boolean> header = new ConcurrentHashMap<>();

        result.ifPresent(transferSummaries -> {
            transferSummaries.forEach(transferSummary -> {
                String headerKey = makeKey(transferSummary.getLine(), transferSummary.getTo_type());
                if (!header.containsKey(headerKey)) {
                    header.put(headerKey, true);
                }
            });
        });
        return header;
    }

    private String makeKey(String... inputs) {
        String res = "";
        for (String input : inputs) {
            if (res.equals("")) {
                res += input;
            } else {
                res += KEY_DELIMETER + input;
            }
        }
        return res;
    }

    @Getter
    public static class FromToMatrix {

        private static final String KEY_DELIMETER = TransferAnalysisService.KEY_DELIMETER;
        private Map<String, Boolean> header;
        private Map<String, Integer> rows;

        public FromToMatrix() {
        }

        public FromToMatrix(Map<String, Boolean> header, Map<String, Integer> rows) {
            this.header = header;
            this.rows = rows;
        }
    }
}
