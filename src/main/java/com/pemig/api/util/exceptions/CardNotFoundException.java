package com.pemig.api.util.exceptions;

import lombok.Getter;

/**
 * A {@link RuntimeException} thrown when a card with a given ID cannot be found in the DB.
 * @author caleb
 */
@Getter
public class CardNotFoundException extends RuntimeException {

    private final Long cardId;

    public CardNotFoundException(Long id) {
        super("Loan id: " + id + " not found.");
        this.cardId = id;
    }
}