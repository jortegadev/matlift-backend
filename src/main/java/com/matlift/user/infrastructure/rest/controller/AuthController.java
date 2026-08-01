package com.matlift.user.infrastructure.rest.controller;

import com.matlift.user.domain.model.AccessToken;
import com.matlift.user.domain.port.in.AuthenticateUserUseCase;
import com.matlift.user.infrastructure.rest.dto.LoginRequest;
import com.matlift.user.infrastructure.rest.dto.LoginResponse;
import com.matlift.user.infrastructure.rest.mapper.AuthRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final AuthRestMapper mapper;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase, AuthRestMapper mapper) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AccessToken accessToken = authenticateUserUseCase.execute(mapper.toCommand(request));

        return ResponseEntity.ok(mapper.toResponse(accessToken));
    }
}
