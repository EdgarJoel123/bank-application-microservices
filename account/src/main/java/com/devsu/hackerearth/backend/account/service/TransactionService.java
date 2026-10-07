package com.devsu.hackerearth.backend.account.service;

import java.util.Date;
import java.util.List;

import com.devsu.hackerearth.backend.account.model.dto.BankStatementDto;
import com.devsu.hackerearth.backend.account.model.dto.TransactionDto;

public interface TransactionService {

    List<TransactionDto> getAll();
    TransactionDto getById(Long id);
    TransactionDto create(TransactionDto transactionDto);
    TransactionDto update(TransactionDto transactionDto);
    void deleteById(Long id);

    List<BankStatementDto> getAllByAccountClientIdAndDateBetween(
            Long clientId,
            Date dateTransactionStart,
            Date dateTransactionEnd);

    TransactionDto getLastByAccountId(Long accountId);
}
