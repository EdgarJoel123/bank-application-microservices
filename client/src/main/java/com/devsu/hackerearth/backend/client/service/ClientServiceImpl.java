package com.devsu.hackerearth.backend.client.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsu.hackerearth.backend.client.exception.ResourceNotFoundException;
import com.devsu.hackerearth.backend.client.integration.ClientEventPublisher;
import com.devsu.hackerearth.backend.client.model.Client;
import com.devsu.hackerearth.backend.client.model.dto.ClientDto;
import com.devsu.hackerearth.backend.client.model.dto.PartialClientDto;
import com.devsu.hackerearth.backend.client.repository.ClientRepository;


@Service
public class ClientServiceImpl implements ClientService {

	private final ClientRepository clientRepository;
	private ClientEventPublisher clientEventPublisher;

	public ClientServiceImpl(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> getAll() {
        return clientRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

	
    @Override
    @Transactional(readOnly = true)
    public ClientDto getById(Long id) {
        return toDto(findClient(id));
    }


    @Override
    @Transactional
    public ClientDto create(ClientDto clientDto) {
        Client client = toEntity(clientDto);
        client.setId(null);

        ClientDto createdClient = toDto(clientRepository.save(client));
        publishUpsert(createdClient);
        return createdClient;
    }


	@Override
    @Transactional
    public ClientDto update(ClientDto clientDto) {
        Client client = findClient(clientDto.getId());

        client.setDni(clientDto.getDni());
        client.setName(clientDto.getName());
        client.setPassword(clientDto.getPassword());
        client.setGender(clientDto.getGender());
        client.setAge(clientDto.getAge());
        client.setAddress(clientDto.getAddress());
        client.setPhone(clientDto.getPhone());
        client.setActive(clientDto.isActive());

        ClientDto updatedClient = toDto(clientRepository.save(client));
        publishUpsert(updatedClient);
        return updatedClient;
    }


	@Override
    @Transactional
    public ClientDto partialUpdate(Long id, PartialClientDto partialClientDto) {
        Client client = findClient(id);
        client.setActive(partialClientDto.isActive());

        ClientDto updatedClient = toDto(clientRepository.save(client));
        publishUpsert(updatedClient);
        return updatedClient;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Client client = findClient(id);
        clientRepository.delete(client);

        if (clientEventPublisher != null) {
            clientEventPublisher.publishDelete(id);
        }
    }

	private Client findClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    private void publishUpsert(ClientDto clientDto) {
        if (clientEventPublisher != null) {
            clientEventPublisher.publishUpsert(clientDto);
        }
    }

    private ClientDto toDto(Client client) {
        return new ClientDto(
                client.getId(),
                client.getDni(),
                client.getName(),
                client.getPassword(),
                client.getGender(),
                client.getAge(),
                client.getAddress(),
                client.getPhone(),
                client.isActive());
    }

    private Client toEntity(ClientDto clientDto) {
        Client client = new Client();
        client.setId(clientDto.getId());
        client.setDni(clientDto.getDni());
        client.setName(clientDto.getName());
        client.setPassword(clientDto.getPassword());
        client.setGender(clientDto.getGender());
        client.setAge(clientDto.getAge());
        client.setAddress(clientDto.getAddress());
        client.setPhone(clientDto.getPhone());
        client.setActive(clientDto.isActive());
        return client;
    }
}
