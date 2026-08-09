package com.codemorph.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5174")
public class AuthController {

    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        // Temporary login credentials
        if ("admin".equals(username) && "1234".equals(password)) {

            return Map.of(
                    "success", true,
                    "message", "Login successful"
            );
        }

        return Map.of(
                "success", false,
                "message", "Invalid username or password"
        );
    }
}