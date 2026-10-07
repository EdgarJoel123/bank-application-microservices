package com.devsu.hackerearth.backend.account.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsu.hackerearth.backend.account.model.dto.ClientProjectionDto;
import com.devsu.hackerearth.backend.account.service.ClientProjectionService;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
@RequestMapping("/internal/clients")
public class ClientProjectionController {

    private final ClientProjectionService clientProjectionService;

    public ClientProjectionController(ClientProjectionService clientProjectionService) {
        this.clientProjectionService = clientProjectionService;
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<Void> upsert(
            @PathVariable Long clientId,
            @RequestBody ClientProjectionDto clientProjectionDto) {

        clientProjectionService.upsert(clientId, clientProjectionDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> delete(@PathVariable Long clientId) {

        clientProjectionService.delete(clientId);

        return ResponseEntity.noContent().build();
    }
}