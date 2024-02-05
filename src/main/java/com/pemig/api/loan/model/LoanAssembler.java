package com.pemig.api.loan.model;

import com.pemig.api.loan.controller.LoanController;
import com.pemig.api.util.SortingOrder;
import lombok.NonNull;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static com.pemig.api.util.Const.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LoanAssembler
    implements RepresentationModelAssembler<LoanDto, EntityModel<LoanDto>> {
  @Override
  public @NonNull EntityModel<LoanDto> toModel(@NonNull LoanDto loanDto) {

    return EntityModel.of(
        loanDto,
        linkTo(methodOn(LoanController.class).getLoan(loanDto.getId())).withSelfRel(),
        linkTo(
                methodOn(LoanController.class)
                    .aggregateGetLoans(
//                        Collections.emptyMap(),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortingOrder.ASC))
            .withRel(ALL_LOANS));
  }

  @Override
  public @NonNull CollectionModel<EntityModel<LoanDto>> toCollectionModel(
      @NonNull Iterable<? extends LoanDto> entities) {
    return CollectionModel.of(
        IterableUtils.toList(entities).stream().map(this::toModel).toList(),
        linkTo(
                methodOn(LoanController.class)
                    .aggregateGetLoans(
//                        Collections.emptyMap(),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortingOrder.ASC))
            .withSelfRel());
  }
}
