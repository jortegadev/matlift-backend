package com.matlift;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;

public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    static {
        POSTGRES.start();
    }

    @Autowired
    private EntityManager entityManager;

    protected UUID createTestUser() {
        UUID userId = UUID.randomUUID();

        entityManager.createNativeQuery("insert into users (id, email, password_hash) values (?, ?, ?)")
                .setParameter(1, userId)
                .setParameter(2, userId + "@matlift.test")
                .setParameter(3, "hash")
                .executeUpdate();

        return userId;
    }
}
