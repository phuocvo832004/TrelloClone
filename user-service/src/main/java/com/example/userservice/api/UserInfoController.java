package com.example.userservice.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserInfoController {
    @GetMapping("/me")
    public OidcUser getUserInfo(@AuthenticationPrincipal OidcUser user) {
        return user;
    }
}
