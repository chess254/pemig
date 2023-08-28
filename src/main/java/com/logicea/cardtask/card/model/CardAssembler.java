package com.logicea.cardtask.card.model;

import static com.logicea.cardtask.util.Const.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.logicea.cardtask.card.controller.CardController;
import com.logicea.cardtask.util.SortingOrder;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class CardAssembler
    implements RepresentationModelAssembler<CardDto, EntityModel<CardDto>> {
  @Override
  public @NonNull EntityModel<CardDto> toModel(@NonNull CardDto cardDto) {

    return EntityModel.of(
        cardDto,
        linkTo(methodOn(CardController.class).getCard(cardDto.getId())).withSelfRel(),
        linkTo(
                methodOn(CardController.class)
                    .aggregateGetCards(
                        Collections.emptyMap(),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortingOrder.ASC))
            .withRel(ALL_CARDS));
  }

  @Override
  public @NonNull CollectionModel<EntityModel<CardDto>> toCollectionModel(
      @NonNull Iterable<? extends CardDto> entities) {
    return CollectionModel.of(
        IterableUtils.toList(entities).stream().map(this::toModel).collect(Collectors.toList()),
        linkTo(
                methodOn(CardController.class)
                    .aggregateGetCards(
                        Collections.emptyMap(),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortingOrder.ASC))
            .withSelfRel());
  }
}