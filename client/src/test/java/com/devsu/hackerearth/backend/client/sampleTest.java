package com.devsu.hackerearth.backend.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.devsu.hackerearth.backend.client.controller.ClientController;
import com.devsu.hackerearth.backend.client.model.Client;
import com.devsu.hackerearth.backend.client.model.dto.ClientDto;
import com.devsu.hackerearth.backend.client.service.ClientService;

@SpringBootTest
@DisplayName("Client microservice tests")
public class sampleTest {

    private static final Long CLIENT_ID = 1L;
    private static final String DNI = "0102030405";
    private static final String NAME = "Cliente Prueba";
    private static final String PASSWORD = "secret";
    private static final String GENDER = "M";
    private static final int AGE = 30;
    private static final String ADDRESS = "Ambato";
    private static final String PHONE = "0999999999";

    private final ClientService clientService =
            mock(ClientService.class);

    private final ClientController clientController =
            new ClientController(clientService);

    @Test
    @DisplayName("Should create a client through the controller")
    void createClientTest() {

        // Arrange
        ClientDto newClient =
                new ClientDto(
                        1L,
                        "Dni",
                        "Name",
                        "Password",
                        "Gender",
                        1,
                        "Address",
                        "9999999999",
                        true
                );

        ClientDto createdClient =
                new ClientDto(
                        1L,
                        "Dni",
                        "Name",
                        "Password",
                        "Gender",
                        1,
                        "Address",
                        "9999999999",
                        true
                );

        when(clientService.create(newClient))
                .thenReturn(createdClient);

        // Act
        ResponseEntity<ClientDto> response =
                clientController.create(newClient);

        // Assert
        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertEquals(
                createdClient,
                response.getBody()
        );

        verify(clientService)
                .create(newClient);
    }

    @Test
    @DisplayName("Should correctly represent the client domain")
    void clientDomainTest() {

        // Arrange
        Client client = createClient();

        // Assert
        assertEquals(CLIENT_ID, client.getId());
        assertEquals(DNI, client.getDni());
        assertEquals(NAME, client.getName());
        assertTrue(client.isActive());
    }

    // =========================================================
    // TEST DATA
    // =========================================================

    private Client createClient() {

        Client client = new Client();

        client.setId(CLIENT_ID);
        client.setDni(DNI);
        client.setName(NAME);
        client.setPassword(PASSWORD);
        client.setGender(GENDER);
        client.setAge(AGE);
        client.setAddress(ADDRESS);
        client.setPhone(PHONE);
        client.setActive(true);

        return client;
    }
}