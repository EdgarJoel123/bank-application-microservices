package com.devsu.hackerearth.backend.account.controller;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devsu.hackerearth.backend.account.model.dto.BankStatementDto;
import com.devsu.hackerearth.backend.account.model.dto.TransactionDto;
import com.devsu.hackerearth.backend.account.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(
        value = {"/api/transactions", "/api/transacciones"},
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Transactions",
        description = "Operaciones para la gestión de transacciones y estados de cuenta"
)
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Get all transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAll() {
        List<TransactionDto> response = transactionService.getAll();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get transaction by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> get(@PathVariable Long id) {
        TransactionDto response = transactionService.getById(id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Saldo no disponible", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDto> create(
            @Valid @RequestBody TransactionDto transactionDto) {

        TransactionDto response = transactionService.create(transactionDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Update transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Saldo no disponible", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TransactionDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDto transactionDto) {

        transactionDto.setId(id);

        TransactionDto response =
                transactionService.update(transactionDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get bank statement by client and date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Invalid date range", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @GetMapping("/clients/{clientId}/report")
    public ResponseEntity<List<BankStatementDto>> report(
            @PathVariable Long clientId,
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date dateTransactionStart,
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date dateTransactionEnd) {

        List<BankStatementDto> response =
                transactionService.getAllByAccountClientIdAndDateBetween(
                        clientId,
                        dateTransactionStart,
                        dateTransactionEnd);

        return ResponseEntity.ok(response);
    }
}