package com.logicea.cardtask.card.service;

import com.logicea.cardtask.card.model.Card;
import com.logicea.cardtask.card.model.CardDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

/**
 * A {@link Mapper} used by PATCH logic to map the fields of a {@link CardDto} onto a {@link Card} just pulled
 * from the database.
 *
 * @author caleb
 */
@Mapper(componentModel = "spring")
@Component
public interface CardDtoToCard {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CardDto cardDto, @MappingTarget Card card);
}
