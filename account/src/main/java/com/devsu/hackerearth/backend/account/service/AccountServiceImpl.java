package com.devsu.hackerearth.backend.account.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.devsu.hackerearth.backend.account.exception.ResourceNotFoundException;
import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.dto.AccountDto;
import com.devsu.hackerearth.backend.account.model.dto.PartialAccountDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAll() {

        return accountRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getById(Long id) {

        Account account = findAccount(id);

        return toDto(account);
    }

    @Override
    @Transactional
    public AccountDto create(AccountDto accountDto) {

        Account account = toEntity(accountDto);

        // El ID debe ser generado por la base de datos
        account.setId(null);

        Account savedAccount = accountRepository.save(account);

        log.info(
                "Cuenta creada | accountId={} | number={} | clientId={}",
                savedAccount.getId(),
                savedAccount.getNumber(),
                savedAccount.getClientId()
        );

        return toDto(savedAccount);
    }

    @Override
    @Transactional
    public AccountDto update(AccountDto accountDto) {

        Account account = findAccount(accountDto.getId());

        updateAccount(account, accountDto);

        Account updatedAccount = accountRepository.save(account);

        log.info(
                "Cuenta actualizada | accountId={} | number={} | clientId={}",
                updatedAccount.getId(),
                updatedAccount.getNumber(),
                updatedAccount.getClientId()
        );

        return toDto(updatedAccount);
    }

    @Override
    @Transactional
    public AccountDto partialUpdate(
            Long id,
            PartialAccountDto partialAccountDto) {

        Account account = findAccount(id);

        account.setActive(partialAccountDto.isActive());

        Account updatedAccount = accountRepository.save(account);

        log.info(
                "Estado de cuenta actualizado | accountId={} | active={}",
                updatedAccount.getId(),
                updatedAccount.isActive()
        );

        return toDto(updatedAccount);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        Account account = findAccount(id);

        accountRepository.delete(account);

        log.info(
                "Cuenta eliminada | accountId={} | number={}",
                account.getId(),
                account.getNumber()
        );
    }

    // =========================================================
    // MÉTODOS PRIVADOS
    // =========================================================

    private Account findAccount(Long id) {

        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cuenta no encontrada"
                        )
                );
    }

    private void updateAccount(
            Account account,
            AccountDto accountDto) {

        account.setNumber(accountDto.getNumber());
        account.setType(accountDto.getType());
        account.setInitialAmount(accountDto.getInitialAmount());
        account.setActive(accountDto.isActive());
        account.setClientId(accountDto.getClientId());
    }

    private AccountDto toDto(Account account) {

        return new AccountDto(
                account.getId(),
                account.getNumber(),
                account.getType(),
                account.getInitialAmount(),
                account.isActive(),
                account.getClientId()
        );
    }

    private Account toEntity(AccountDto accountDto) {

        Account account = new Account();

        account.setId(accountDto.getId());
        account.setNumber(accountDto.getNumber());
        account.setType(accountDto.getType());
        account.setInitialAmount(accountDto.getInitialAmount());
        account.setActive(accountDto.isActive());
        account.setClientId(accountDto.getClientId());

        return account;
    }
}