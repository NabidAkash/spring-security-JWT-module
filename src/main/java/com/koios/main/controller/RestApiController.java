package com.koios.main.controller;

import com.koios.main.model.Authority;
import com.koios.main.model.RefreshToken;
import com.koios.main.model.User;
import com.koios.main.service.RefreshTokenService;
import com.koios.main.service.UserService;
import com.koios.main.util.JwtUtil;
import io.micrometer.common.util.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class RestApiController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public RestApiController(JwtUtil jwtUtil, UserService userService, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/user/register")
    public String userRegistration(@RequestBody User user) {
        User existingUser = userService.findUserByUsername(user.getUsername());
        if (existingUser != null) {
            return "User already exists";
        }
        userService.saveUser(user);
        return "User register Successful.";
    }

    @PostMapping("/admin/register")
    public String adminRegistration(@RequestBody User user) {
        User existingUser = userService.findUserByUsername(user.getUsername());
        if (existingUser != null) {
            return "Admin already exists";
        }
        userService.saveUser(user);
        return "Admin register Successful.";
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        try {
            //Check if username and password is correct
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User existingUser = userService.findUserByUsername(user.getUsername());

            //Create and return Jwt Token
            Map<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("jwtToken", jwtUtil.generateToken(userDetails));
            tokenMap.put("refreshToken",refreshTokenService.createRefreshToken(existingUser).getRefreshToken());
            return tokenMap;
        } catch (Exception ex) {
            return Map.of("Error", "Invalid Credentials");
        }
    }

    @PostMapping("/refresh")
    public String refreshToken(@RequestBody Map<String, Object> token) {
        String refreshToken = token.get("refreshToken").toString();

        if(StringUtils.isNotBlank(refreshToken)) {
            RefreshToken existingRefreshToken = refreshTokenService.findByRefreshToken(refreshToken);

            if(existingRefreshToken != null && refreshTokenService.isRefreshTokenValid(existingRefreshToken)) {
                User user = existingRefreshToken.getUser();
                Set<GrantedAuthority> authorities = new HashSet<>();
                for (Authority authority : user.getAuthorities()) {
                    authorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
                }
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        user.getUsername(), "", authorities);
                return jwtUtil.generateToken(userDetails);
            }
        }
        return "Invalid refresh token";
    }


}
