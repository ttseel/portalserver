package com.samsung.portalserver.api;

import com.samsung.portalserver.api.dto.UserNameAndIPDto;
import com.samsung.portalserver.domain.UserInfo;
import com.samsung.portalserver.service.AuthService;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthApiController {

    @Autowired
    private AuthService authService;

    @GetMapping("/api/auth/user-name")
    public UserNameAndIPDto readUserNameByIp(HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        UserNameAndIPDto userNameAndIPDto = new UserNameAndIPDto(ip);

        Optional<UserInfo> result = authService.readByIp(ip);
        result.ifPresent(uInfo -> {
            userNameAndIPDto.setUserName(uInfo.getName());
        });

        return userNameAndIPDto;
    }

    @PostMapping("/api/auth/user-name/update")
    public boolean updateUserName(@RequestParam("newName") String newName,
        @RequestParam("ip") String ip) {
        boolean changeResult = false;

        authService.updateUserName(newName, ip);
        Optional<UserInfo> updateResult = authService.readByIp(ip);
        if (checkUpdateSuccess(newName, updateResult)) {
            changeResult = true;
        }

        return changeResult;
    }

    private boolean checkUpdateSuccess(String newName, Optional<UserInfo> updateResult) {
        return updateResult.isPresent() && updateResult.get().getName().equals(newName);
    }
}
