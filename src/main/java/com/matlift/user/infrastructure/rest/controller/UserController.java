package com.matlift.user.infrastructure.rest.controller;

import com.matlift.user.domain.model.User;
import com.matlift.user.domain.port.in.CreateUserUseCase;
import com.matlift.user.infrastructure.rest.dto.CreateUserRequest;
import com.matlift.user.infrastructure.rest.dto.UserResponse;
import com.matlift.user.infrastructure.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UserRestMapper mapper;

    public UserController(CreateUserUseCase createUserUseCase, UserRestMapper mapper) {
        this.createUserUseCase = createUserUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User createdUser = createUserUseCase.execute(mapper.toCommand(request));

        return new ResponseEntity<>(mapper.toResponse(createdUser), HttpStatus.CREATED);
    }
}
