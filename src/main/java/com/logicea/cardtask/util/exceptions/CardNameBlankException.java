package com.logicea.cardtask.util.exceptions;

/**
 * A {@link RuntimeException} thrown by PATCH requests when the user tries to update the name of a card
 * to the empty string. We do not allow that.
 *
 * @author caleb
 */
public class CardNameBlankException extends RuntimeException{

    public CardNameBlankException(){
        super("Card name should be provided and should not be blank.");
    }
}
