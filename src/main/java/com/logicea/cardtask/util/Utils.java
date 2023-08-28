package com.logicea.cardtask.util;


import com.logicea.cardtask.card.model.CardDto;
import com.logicea.cardtask.card.model.Card;

public final class Utils {
    
    private Utils(){}

    public static CardDto fromCardEntityToCardDto(Card card) {
        return CardDto.builder()
                .id(card.getId())
                .description(card.getDescription())
                .name(card.getName())
                .color(card.getColor())
                .status(card.getStatus())
                .created(card.getCreatedDateTime())
                .createdBy(card.getCreatedBy())
                .modifiedBy(card.getLastModifiedBy())
                .modified(card.getLastModifiedDateTime())
                .build();
    }
}
