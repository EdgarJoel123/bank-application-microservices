package com.devsu.hackerearth.backend.account.service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.devsu.hackerearth.backend.account.exception.BusinessException;
import com.devsu.hackerearth.backend.account.exception.InsufficientBalanceException;
import com.devsu.hackerearth.backend.account.exception.ResourceNotFoundException;
import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.Transaction;
import com.devsu.hackerearth.backend.account.model.dto.BankStatementDto;
import com.devsu.hackerearth.backend.account.model.dto.TransactionDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;
import com.devsu.hackerearth.backend.account.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private AccountRepository accountRepository;
    private ClientProjectionService clientProjectionService;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired(required = false)
    public void setClientProjectionService(
            ClientProjectionService clientProjectionService) {

        this.clientProjectionService = clientProjectionService;
    }

    // =========================================================
    // CONSULTAS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getAll() {

        return transactionRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDto getById(Long id) {

        Transaction transaction = findTransaction(id);

        return toDto(transaction);
    }

    // =========================================================
    // CREAR TRANSACCIÓN
    // =========================================================

    @Override
    @Transactional
    public TransactionDto create(TransactionDto transactionDto) {

        Account account =
                findAccountForUpdate(transactionDto.getAccountId());

        Transaction transaction =
                toEntity(transactionDto);

        // El ID debe ser generado por la base de datos.
        transaction.setId(null);

        // Se asegura que el movimiento pertenezca a la cuenta bloqueada.
        transaction.setAccountId(account.getId());

        if (transaction.getDate() == null) {
            transaction.setDate(new Date());
        }

        /*
         * El saldo real se calcula posteriormente recorriendo
         * cronológicamente todos los movimientos de la cuenta.
         */
        transaction.setBalance(0.0);

        transaction =
                transactionRepository.saveAndFlush(transaction);

        recalculateBalances(account);

        log.info(
                "Transacción creada | transactionId={} | accountId={} | type={} | amount={} | balance={}",
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalance()
        );

        return toDto(transaction);
    }

    // =========================================================
    // ACTUALIZAR TRANSACCIÓN
    // =========================================================

    @Override
    @Transactional
    public TransactionDto update(TransactionDto transactionDto) {

        Transaction transaction =
                findTransaction(transactionDto.getId());

        Long previousAccountId =
                transaction.getAccountId();

        Long targetAccountId =
                transactionDto.getAccountId() != null
                        ? transactionDto.getAccountId()
                        : previousAccountId;

        Account previousAccount =
                findAccountForUpdate(previousAccountId);

        Account targetAccount =
                previousAccountId.equals(targetAccountId)
                        ? previousAccount
                        : findAccountForUpdate(targetAccountId);

        updateTransaction(
                transaction,
                transactionDto,
                targetAccountId
        );

        transaction =
                transactionRepository.saveAndFlush(transaction);

        /*
         * Siempre se recalcula la cuenta original porque al modificar
         * un movimiento pueden cambiar todos los saldos posteriores.
         */
        recalculateBalances(previousAccount);

        /*
         * Si la transacción cambió de cuenta, también se recalcula
         * el historial de la nueva cuenta.
         */
        if (!previousAccountId.equals(targetAccountId)) {
            recalculateBalances(targetAccount);
        }

        log.info(
                "Transacción actualizada | transactionId={} | previousAccountId={} | accountId={} | amount={} | balance={}",
                transaction.getId(),
                previousAccountId,
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getBalance()
        );

        return toDto(transaction);
    }

    // =========================================================
    // ELIMINAR TRANSACCIÓN
    // =========================================================

    @Override
    @Transactional
    public void deleteById(Long id) {

        Transaction transaction =
                findTransaction(id);

        Account account =
                findAccountForUpdate(transaction.getAccountId());

        Long accountId =
                transaction.getAccountId();

        transactionRepository.delete(transaction);
        transactionRepository.flush();

        /*
         * Al eliminar un movimiento se recalculan todos los
         * saldos posteriores de la cuenta.
         */
        recalculateBalances(account);

        log.info(
                "Transacción eliminada | transactionId={} | accountId={}",
                id,
                accountId
        );
    }

    // =========================================================
    // ESTADO DE CUENTA
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<BankStatementDto> getAllByAccountClientIdAndDateBetween(
            Long clientId,
            Date dateTransactionStart,
            Date dateTransactionEnd) {

        validateDateRange(
                dateTransactionStart,
                dateTransactionEnd
        );

        List<Account> accounts =
                accountRepository.findByClientId(clientId);

        if (accounts.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Account> accountMap =
                accounts.stream()
                        .collect(
                                Collectors.toMap(
                                        Account::getId,
                                        Function.identity()
                                )
                        );

        List<Long> accountIds =
                accounts.stream()
                        .map(Account::getId)
                        .collect(Collectors.toList());

        Date startDate =
                startOfDay(dateTransactionStart);

        Date endDate =
                endOfDay(dateTransactionEnd);

        List<Transaction> transactions =
                transactionRepository
                        .findByAccountIdInAndDateBetweenOrderByDateAscIdAsc(
                                accountIds,
                                startDate,
                                endDate
                        );

        String clientName =
                getClientName(clientId);

        return transactions.stream()
                .map(transaction ->
                        toBankStatementDto(
                                transaction,
                                accountMap.get(transaction.getAccountId()),
                                clientName
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDto getLastByAccountId(Long accountId) {

        return transactionRepository
                .findFirstByAccountIdOrderByDateDescIdDesc(accountId)
                .map(this::toDto)
                .orElse(null);
    }

    // =========================================================
    // REGLAS DE NEGOCIO
    // =========================================================

    private void recalculateBalances(Account account) {

        List<Transaction> transactions =
                transactionRepository
                        .findByAccountIdOrderByDateAscIdAsc(
                                account.getId()
                        );

        double balance =
                account.getInitialAmount();

        for (Transaction transaction : transactions) {

            balance += transaction.getAmount();

            if (balance < 0) {

                log.info(
                        "Movimiento rechazado por saldo insuficiente | accountId={} | amount={} | calculatedBalance={}",
                        account.getId(),
                        transaction.getAmount(),
                        balance
                );

                throw new InsufficientBalanceException();
            }

            transaction.setBalance(balance);
        }

        transactionRepository.saveAll(transactions);
        transactionRepository.flush();
    }

    private void validateDateRange(
            Date dateTransactionStart,
            Date dateTransactionEnd) {

        if (dateTransactionStart == null
                || dateTransactionEnd == null) {

            throw new BusinessException(
                    "Las fechas son obligatorias"
            );
        }

        if (dateTransactionStart.after(dateTransactionEnd)) {

            throw new BusinessException(
                    "La fecha inicial no puede ser posterior a la fecha final"
            );
        }
    }

    // =========================================================
    // BÚSQUEDAS
    // =========================================================

    private Account findAccountForUpdate(Long accountId) {

        if (accountId == null) {

            throw new BusinessException(
                    "La cuenta es obligatoria"
            );
        }

        return accountRepository
                .findByIdForUpdate(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cuenta no encontrada"
                        )
                );
    }

    private Transaction findTransaction(Long id) {

        return transactionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transacción no encontrada"
                        )
                );
    }

    private String getClientName(Long clientId) {

        if (clientProjectionService == null) {
            return String.valueOf(clientId);
        }

        return clientProjectionService
                .getClientName(clientId)
                .orElse(String.valueOf(clientId));
    }

    // =========================================================
    // ACTUALIZACIONES
    // =========================================================

    private void updateTransaction(
            Transaction transaction,
            TransactionDto transactionDto,
            Long targetAccountId) {

        transaction.setAccountId(targetAccountId);
        transaction.setType(transactionDto.getType());
        transaction.setAmount(transactionDto.getAmount());

        if (transactionDto.getDate() != null) {
            transaction.setDate(transactionDto.getDate());
        }
    }

    // =========================================================
    // MAPPERS
    // =========================================================

    private TransactionDto toDto(Transaction transaction) {

        return new TransactionDto(
                transaction.getId(),
                transaction.getDate(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalance(),
                transaction.getAccountId()
        );
    }

    private Transaction toEntity(TransactionDto transactionDto) {

        Transaction transaction =
                new Transaction();

        transaction.setId(transactionDto.getId());
        transaction.setDate(transactionDto.getDate());
        transaction.setType(transactionDto.getType());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setBalance(transactionDto.getBalance());
        transaction.setAccountId(transactionDto.getAccountId());

        return transaction;
    }

    private BankStatementDto toBankStatementDto(
            Transaction transaction,
            Account account,
            String clientName) {

        return new BankStatementDto(
                transaction.getDate(),
                clientName,
                account.getNumber(),
                account.getType(),
                String.valueOf(account.getInitialAmount()),
                account.isActive(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalance()
        );
    }

    // =========================================================
    // FECHAS
    // =========================================================

    private Date startOfDay(Date date) {

        Calendar calendar =
                Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private Date endOfDay(Date date) {

        Calendar calendar =
                Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }
}