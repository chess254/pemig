package com.pemig.api.util.exceptions;

/**
 * A {@link RuntimeException} thrown by PATCH requests when the user tries to update the name of a card
 * to the empty string. We do not allow that.
 *
 * @author caleb
 */
public class LoanNameBlankException extends RuntimeException{

    public LoanNameBlankException(){
        super("LoanDetails name should be provided and should not be blank.");
    }
}
