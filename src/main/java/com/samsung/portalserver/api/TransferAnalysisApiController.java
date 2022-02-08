package com.samsung.portalserver.api;

import com.samsung.portalserver.domain.TransferSummary;
import com.samsung.portalserver.service.TransferAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TransferAnalysisApiController {

    private final TransferAnalysisService transferAnalysisService;

    @GetMapping("/tranalysis/fromto/{site}/{year}/{month}")
    public Optional<List<TransferSummary>> readFromToAnalysis(@PathVariable("site") String site,
                                                              @PathVariable("year") String year,
                                                              @PathVariable("month") String month ) {

        System.out.println(String.format("Request API: readFromToAnalysis, Input Params: %s, %s, %s", site, year, month));

        Optional<TransferAnalysisService.FromToMatrix> fromToMatrix = transferAnalysisService.readFromToMatrixBy(site, year, month);


        return null;
    }

    static class FromToMatrixDto<T> {
        private List<LineMatrix> data;

        static class LineMatrix {
            private String line;
            private List<String> header;
            private List<String> rows;
        }
    }
}
