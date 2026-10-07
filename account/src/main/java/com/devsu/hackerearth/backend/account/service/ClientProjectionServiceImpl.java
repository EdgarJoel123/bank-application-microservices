package com.devsu.hackerearth.backend.account.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.devsu.hackerearth.backend.account.model.ClientProjection;
import com.devsu.hackerearth.backend.account.model.dto.ClientProjectionDto;
import com.devsu.hackerearth.backend.account.repository.ClientProjectionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
public class ClientProjectionServiceImpl implements ClientProjectionService {

    private final ClientProjectionRepository clientProjectionRepository;

    public ClientProjectionServiceImpl(
            ClientProjectionRepository clientProjectionRepository) {

        this.clientProjectionRepository = clientProjectionRepository;
    }

    @Override
    @Transactional
    public void upsert(
            Long clientId,
            ClientProjectionDto clientProjectionDto) {

        ClientProjection projection =
                clientProjectionRepository.findById(clientId)
                        .orElseGet(ClientProjection::new);

        updateProjection(
                projection,
                clientId,
                clientProjectionDto
        );

        clientProjectionRepository.save(projection);

        log.info(
                "Proyección de cliente actualizada | clientId={} | name={} | active={}",
                projection.getId(),
                projection.getName(),
                projection.isActive()
        );
    }

    @Override
    @Transactional
    public void delete(Long clientId) {

        clientProjectionRepository.deleteById(clientId);

        log.info(
                "Proyección de cliente eliminada | clientId={}",
                clientId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getClientName(Long clientId) {

        return clientProjectionRepository.findById(clientId)
                .map(ClientProjection::getName);
    }

    // =========================================================
    // MÉTODOS PRIVADOS
    // =========================================================

    private void updateProjection(
            ClientProjection projection,
            Long clientId,
            ClientProjectionDto clientProjectionDto) {

        projection.setId(clientId);
        projection.setName(clientProjectionDto.getName());
        projection.setActive(clientProjectionDto.isActive());
    }
}