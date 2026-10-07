package com.devsu.hackerearth.backend.client.integration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.devsu.hackerearth.backend.client.model.dto.ClientDto;

@Component
public class ClientEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ClientEventPublisher.class);

    private final RestTemplate restTemplate;

    @Value("${account.service.url:http://localhost:8000}")
    private String accountServiceUrl;

    public ClientEventPublisher(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async("clientEventExecutor")
    public void publishUpsert(ClientDto clientDto) {
        try {
            restTemplate.put(
                    accountServiceUrl + "/internal/clients/" + clientDto.getId(),
                    clientDto);
        } catch (RestClientException exception) {
            log.warn("No fue posible propagar asincrónicamente el cliente {} al microservicio account: {}",
                    clientDto.getId(), exception.getMessage());
        }
    }

    @Async("clientEventExecutor")
    public void publishDelete(Long clientId) {
        try {
            restTemplate.delete(accountServiceUrl + "/internal/clients/" + clientId);
        } catch (RestClientException exception) {
            log.warn("No fue posible propagar asincrónicamente la eliminación del cliente {}: {}",
                    clientId, exception.getMessage());
        }
    }
}
