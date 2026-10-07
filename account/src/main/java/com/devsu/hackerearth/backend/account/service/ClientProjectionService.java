package com.devsu.hackerearth.backend.account.service;

import java.util.Optional;

import com.devsu.hackerearth.backend.account.model.dto.ClientProjectionDto;

public interface ClientProjectionService {

    void upsert(Long clientId, ClientProjectionDto clientProjectionDto);
    void delete(Long clientId);
    Optional<String> getClientName(Long clientId);
}
