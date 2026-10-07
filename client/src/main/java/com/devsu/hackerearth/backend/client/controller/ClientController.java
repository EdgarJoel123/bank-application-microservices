package com.devsu.hackerearth.backend.client.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsu.hackerearth.backend.client.model.dto.ClientDto;
import com.devsu.hackerearth.backend.client.model.dto.PartialClientDto;
import com.devsu.hackerearth.backend.client.service.ClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(
        value = {"/api/clients", "/api/clientes"},
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Clients",
        description = "Operaciones para la gestión de clientes"
)
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Get all clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ClientDto>> getAll() {
        return ResponseEntity.ok(clientService.getAll());
    }

    @Operation(summary = "Get client by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> get(@PathVariable Long id) {

        ClientDto client = clientService.getById(id);

        if (client == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(client);
    }

    @Operation(summary = "Create client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "409", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDto> create(
            @Valid @RequestBody ClientDto clientDto) {

        ClientDto createdClient = clientService.create(clientDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdClient);
    }

    @Operation(summary = "Update client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ClientDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ClientDto clientDto) {
    
        ClientDto existingClient = clientService.getById(id);
    
        if (existingClient == null) {
            return ResponseEntity.notFound().build();
        }
    
        clientDto.setId(id);
    
        ClientDto updatedClient = clientService.update(clientDto);
    
        return ResponseEntity.ok(
                updatedClient != null ? updatedClient : clientDto
        );
    }

    @Operation(summary = "Update client status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @PatchMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ClientDto> partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody PartialClientDto partialClientDto) {

        ClientDto existingClient = clientService.getById(id);

        if (existingClient == null) {
            return ResponseEntity.notFound().build();
        }

        ClientDto updatedClient =
                clientService.partialUpdate(id, partialClientDto);

        return ResponseEntity.ok(updatedClient);
    }

    @Operation(summary = "Delete client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        ClientDto existingClient = clientService.getById(id);

        if (existingClient == null) {
            return ResponseEntity.notFound().build();
        }

        clientService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}