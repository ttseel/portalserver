package com.samsung.portalserver.service;

import com.samsung.portalserver.domain.UserInfo;
import com.samsung.portalserver.repository.UserInfoRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    public Optional<UserInfo> readUniqueRecord(Long no) {
        return userInfoRepository.readUniqueRecord(no);
    }

    public Optional<UserInfo> readByIp(String ip) {
        return userInfoRepository.readByIp(ip);
    }

    public void updateUserName(String newName, String ip) {
        Optional<UserInfo> result = readByIp(ip);
        if (result.isPresent()) {
            result.get().setName(newName);
            userInfoRepository.save(result.get());
        } else {
            UserInfo userInfo = new UserInfo();
            userInfo.setName(newName);
            userInfo.setIp(ip);
            userInfoRepository.save(userInfo);
        }
    }
}
