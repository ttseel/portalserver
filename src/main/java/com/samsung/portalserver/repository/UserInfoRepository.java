package com.samsung.portalserver.repository;

import com.samsung.portalserver.domain.UserInfo;
import java.util.Optional;

public interface UserInfoRepository {

    Optional<UserInfo> readUniqueRecord(Long no);

    Optional<UserInfo> readByIp(String ip);

    void save(UserInfo userInfo);
}
