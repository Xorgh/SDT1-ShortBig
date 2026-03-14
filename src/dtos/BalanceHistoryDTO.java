package dtos;

import entities.TransactionType;

import java.time.LocalDateTime;

public record BalanceHistoryDTO(
    LocalDateTime timestamp,
    TransactionType type,
    double amount,
    double balanceAfter
) {}
