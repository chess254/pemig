package com.logicea.cardtask.util.exceptions;


/**
 * A {@link RuntimeException} instance thrown in POST Card requests when the user has not provided a 
 * name for the card. 
 * 
 * @author caleb
 */
public class CardNameNotValidException extends RuntimeException{
    public CardNameNotValidException(){
        super("Card name/title is required.");
    }
}
