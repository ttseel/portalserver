package com.samsung.portalserver.repository;

import com.samsung.portalserver.domain.TransferSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class TransferRepositoryImpl implements TransferRepository {
    private final EntityManager em;

    @Autowired
    public TransferRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<List<TransferSummary>> readSummaryBySiteAndYyyymm(String site, String yyyymm) {
        List<TransferSummary> result = em.createQuery("select ts from TransferSummary as ts where ts.site = :site and ts.yyyymmdd like concat(:yyyymm, '%')", TransferSummary.class)
                .setParameter("site", site)
                .setParameter("yyyymm", yyyymm)
                .getResultList();

        return Optional.ofNullable(result);
    }
}
