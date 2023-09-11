package com.pemig.api.util.exceptions;


/**
 * A {@link RuntimeException} instance thrown in POST LoanDetails requests when the user has not provided a
 * name for the card. 
 * 
 * @author caleb
 */
public class LoanNameNotValidException extends RuntimeException{
    public LoanNameNotValidException(){
        super("LoanDetails name/title is required.");
    }
}
