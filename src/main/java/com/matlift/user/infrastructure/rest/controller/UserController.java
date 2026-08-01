package com.matlift.user.infrastructure.rest.controller;

import com.matlift.user.domain.model.User;
import com.matlift.user.domain.port.in.CreateUserUseCase;
import com.matlift.user.domain.port.in.GetCurrentUserUseCase;
import com.matlift.user.infrastructure.rest.dto.CreateUserRequest;
import com.matlift.user.infrastructure.rest.dto.UserResponse;
import com.matlift.user.infrastructure.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UserRestMapper mapper;

    public UserController(CreateUserUseCase createUserUseCase,
                          GetCurrentUserUseCase getCurrentUserUseCase,
                          UserRestMapper mapper) {
        this.createUserUseCase = createUserUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User createdUser = createUserUseCase.execute(mapper.toCommand(request));

        return new ResponseEntity<>(mapper.toResponse(createdUser), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {
        User user = getCurrentUserUseCase.execute(UUID.fromString(principal.getName()));

        return ResponseEntity.ok(mapper.toResponse(user));
    }
}
