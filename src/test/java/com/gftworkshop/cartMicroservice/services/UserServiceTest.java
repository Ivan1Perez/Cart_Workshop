package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.exceptions.ExternalMicroserviceException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UserServiceTest {

    private MockWebServer mockWebServer;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
    }

    @Test
    @DisplayName("When fetching a user by ID, " +
            "then the correct user details are returned")
    void testGetUserById() {
        String userJson = """
                {
                    "id": 100,
                    "email": "john.doe@example.com",
                    "name": "John",
                    "lastName": "Doe",
                    "password": "password123",
                    "fidelityPoints": 1000,
                    "birthDate": "1985-10-15",
                    "phoneNumber": "1234567890",
                    "country": {
                        "name": "USA",
                        "code": "US",
                        "tax": "21.0"
                    }
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(userJson)
                .addHeader("Content-Type", "application/json"));

        User user = userService.getUserById(100L);

        assertEquals(100L,(long) user.getId());
        assertEquals(21.0,user.getCountry().getTax(), 0.001);

    }

    @Test
    @DisplayName("When fetching a non-existent user by ID, then a 404 error is returned")
    void testGetUserByIdNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setBody("Not found")
                .addHeader("Content-Type", "application/json"));

        assertThrows(ExternalMicroserviceException.class, () -> {
            userService.getUserById(1L);
        });
    }


    @Test
    @DisplayName("When fetching a User by ID and an internal server error occurs, then a 500 error is returned")
    void testGetUserByIdServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "application/json"));

        assertThrows(ExternalMicroserviceException.class, () -> {
            userService.getUserById(1L);
        });
    }



    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}