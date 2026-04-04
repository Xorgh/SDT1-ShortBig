package dtos;

import entities.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionDTO(
    UUID id,
    String stockSymbol,
    TransactionType type,
    int quantity,
    double pricePerShare,
    double totalAmount,
    double fee,
    LocalDateTime timestamp
) {}