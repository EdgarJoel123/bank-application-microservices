package com.devsu.hackerearth.backend.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.devsu.hackerearth.backend.account.controller.AccountController;
import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.dto.AccountDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;
import com.devsu.hackerearth.backend.account.service.AccountService;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Account microservice tests")
public class sampleTest {

    private static final Long CLIENT_ID = 1L;
    private static final String ACCOUNT_NUMBER = "INT-001";
    private static final String ACCOUNT_TYPE = "savings";

    private static final double INITIAL_AMOUNT = 100.0;
    private static final double DEPOSIT_AMOUNT = 50.0;
    private static final double EXPECTED_BALANCE = 150.0;

    private final AccountService accountService =
            mock(AccountService.class);

    private final AccountController accountController =
            new AccountController(accountService);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Should create an account through the controller")
    void createAccountTest() {

        // Arrange
        AccountDto newAccount =
                new AccountDto(
                        1L,
                        "number",
                        "savings",
                        0.0,
                        true,
                        CLIENT_ID
                );

        AccountDto createdAccount =
                new AccountDto(
                        1L,
                        "number",
                        "savings",
                        0.0,
                        true,
                        CLIENT_ID
                );

        when(accountService.create(newAccount))
                .thenReturn(createdAccount);

        // Act
        ResponseEntity<AccountDto> response =
                accountController.create(newAccount);

        // Assert
        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertEquals(
                createdAccount,
                response.getBody()
        );

        verify(accountService)
                .create(newAccount);
    }

    @Test
    @DisplayName("Should create a deposit and calculate the account balance")
    void createTransactionIntegrationTest() throws Exception {

        // Arrange
        Account account = createAccount();

        account = accountRepository.save(account);

        assertNotNull(account.getId());

        String requestBody =
                buildTransactionRequest(
                        "DEPOSIT",
                        DEPOSIT_AMOUNT,
                        account.getId()
                );

        // Act & Assert
        mockMvc.perform(
                        post("/api/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.balance")
                                .value(EXPECTED_BALANCE)
                );
    }

    // =========================================================
    // TEST DATA
    // =========================================================

    private Account createAccount() {

        Account account = new Account();

        account.setNumber(ACCOUNT_NUMBER);
        account.setType(ACCOUNT_TYPE);
        account.setInitialAmount(INITIAL_AMOUNT);
        account.setActive(true);
        account.setClientId(CLIENT_ID);

        return account;
    }

    private String buildTransactionRequest(
            String type,
            double amount,
            Long accountId) {

        return "{"
                + "\"type\":\"" + type + "\","
                + "\"amount\":" + amount + ","
                + "\"accountId\":" + accountId
                + "}";
    }
}