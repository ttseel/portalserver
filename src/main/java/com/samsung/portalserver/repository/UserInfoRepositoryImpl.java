package com.samsung.portalserver.repository;

import com.samsung.portalserver.domain.UserInfo;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserInfoRepositoryImpl implements UserInfoRepository {

    @Autowired
    private EntityManager em;

    @Override
    public Optional<UserInfo> readUniqueRecord(Long no) {
        Optional<UserInfo> userInfo = Optional.empty();

        List<UserInfo> result = em.createQuery(
                "select ui from UserInfo as ui" + " where ui.no = :no", UserInfo.class)
            .setParameter("no", no).getResultList();
        if (result.size() == 1) {
            userInfo = Optional.of(result.get(0));
        }

        return userInfo;
    }

    @Override
    public Optional<UserInfo> readByIp(String ip) {
        Optional<UserInfo> userInfo = Optional.empty();

        List<UserInfo> result = em.createQuery(
                "select ui from UserInfo as ui" + " where ui.ip = :ip", UserInfo.class)
            .setParameter("ip", ip).getResultList();
        if (result.size() == 1) {
            userInfo = Optional.of(result.get(0));
        }

        return userInfo;
    }

    @Override
    public void save(UserInfo userInfo) {
        em.persist(userInfo);
    }
}
