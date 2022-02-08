package com.samsung.portalserver.repository;

import com.samsung.portalserver.domain.TransferSummary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TransferRepository {
    Optional<List<TransferSummary>> readSummaryBySiteAndYyyymm(String site, String yyyymm);

}
