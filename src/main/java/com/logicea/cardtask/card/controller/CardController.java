package com.logicea.cardtask.card.controller;

import static com.logicea.cardtask.util.Const.*;

import com.logicea.cardtask.card.model.CardDto;
import com.logicea.cardtask.card.model.CardAssembler;
import com.logicea.cardtask.card.service.CardService;
import com.logicea.cardtask.util.QueryParams;
import com.logicea.cardtask.util.Const;
import com.logicea.cardtask.util.SortingOrder;
import com.logicea.cardtask.util.exceptions.CardNameBlankException;
import com.logicea.cardtask.util.exceptions.CardNameNotValidException;
import com.logicea.cardtask.util.exceptions.WrongSortFieldException;
import com.logicea.cardtask.util.logger.Logs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cardsapi")
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "2. Cards endpoints")
@Validated
@Logs
public class CardController {

  private final CardService cardService;

  private final CardAssembler assembler;

  @Operation(summary = "Add card")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Card created",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema =  @Schema(
                          type = "object",
                          additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                          ref = "#/components/schemas/CardDto"))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Card Name not provided",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
      })
  @PostMapping("/card")
  public ResponseEntity<EntityModel<CardDto>> postCard(@RequestBody @Valid CardDto cardDto)
      throws CardNameNotValidException {
    if (StringUtils.isBlank(cardDto.getName())) {
      throw new CardNameNotValidException();
    }
    return new ResponseEntity<>(
        assembler.toModel(cardService.storeCard(cardDto)), HttpStatus.CREATED);
  }

  @Operation(summary = "Get card by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Card retrieved",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema =  @Schema(
                          type = "object",
                          additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                          ref = "#/components/schemas/CardDto"))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized.",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
      })
  @GetMapping("/card/{id}")
  public ResponseEntity<EntityModel<CardDto>> getCard(@PathVariable Long id) {
    return ResponseEntity.ok(assembler.toModel(cardService.getCard(id)));
  }

  @Operation(summary = "GEt list of cards by specific parameters")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cards retrieved",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(
                          type = "object",
                          additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                          ref = "#/components/schemas/CardDto")))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid sort field",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content = @Content),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content = @Content)
      })
  @GetMapping("/card")
  public ResponseEntity<CollectionModel<EntityModel<CardDto>>> aggregateGetCards(
          @Parameter(
                  name = "filterParams",
                  in = ParameterIn.QUERY,
                  schema =
                  @Schema(
                          type = "object",
                          additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                          ref = "#/components/schemas/FilterMap"),
                  style = ParameterStyle.FORM,
                  explode = Explode.TRUE)
      @RequestParam Map<String, String> filterParams,
      @RequestParam(name = "page", defaultValue = DEFAULT_PAGE_IDX) @Min(0) Integer page,
      @RequestParam(name = "items_in_page", defaultValue = DEFAULT_PAGE_SIZE) @Min(1)
          Integer pageSize,
      @RequestParam(name = "sort_by_field", defaultValue = DEFAULT_SORT_BY_FIELD) @NonNull @NotBlank
          String sortByField,
      @RequestParam(name = "sortingOrder", defaultValue = DEFAULT_SORT_ORDER) @NonNull
          SortingOrder sortingOrder)
      throws WrongSortFieldException {
    List<String> cardFieldNames =
        Arrays.stream(CardDto.class.getDeclaredFields()).map(Field::getName).toList();
    if (!cardFieldNames.contains(sortByField)) {
      throw new WrongSortFieldException(sortByField, cardFieldNames);
    }
    return ResponseEntity.ok(
        assembler.toCollectionModel(
            cardService.getAllCardsByFilter(
                QueryParams.builder()
                    .filterParams(filterParams)
                    .page(page)
                    .pageSize(pageSize)
                    .sortByField(sortByField)
                    .sortingOrder(sortingOrder)
                    .build())));
  }


  @Operation(summary = "Replace a Card")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Card successfully replaced",
            content =
                @Content(
                    mediaType = "application/json",
                    schema =  @Schema(
                            type = "object",
                            additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                            ref = "#/components/schemas/CardDto"))),
        @ApiResponse(
            responseCode = "400",
            description = "Card Name not provided",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
      })
  @PutMapping("/card/{id}")
  public ResponseEntity<EntityModel<CardDto>> putCard(
      @PathVariable Long id, @RequestBody @Valid CardDto cardDto)
      throws CardNameNotValidException {
    if (StringUtils.isBlank(cardDto.getName())) {
      throw new CardNameNotValidException();
    }
    return ResponseEntity.ok(assembler.toModel(cardService.replaceCard(id, cardDto)));
  }

  @Operation(summary = "Update a Card")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Card updated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema =  @Schema(
                            type = "object",
                            additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                            ref = "#/components/schemas/CardDto"))),
        @ApiResponse(
            responseCode = "400",
            description = "Attempted to clear card name.",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
      })
  @PatchMapping("/card/{id}")
  public ResponseEntity<EntityModel<CardDto>> patchCard(
      @PathVariable Long id, @RequestBody @Valid CardDto cardDto)
      throws CardNameBlankException {
    if (StringUtils.isWhitespace(
        cardDto.getName())) {
      throw new CardNameBlankException();
    }
    return ResponseEntity.ok(assembler.toModel(cardService.updateCard(id, cardDto)));
  }


  @Operation(summary = "Delete card by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Card successfully deleted",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
      })
  @DeleteMapping("/card/{id}")
  public ResponseEntity<?> deleteCard(@PathVariable Long id) {
    cardService.deleteCard(id);
    return ResponseEntity.noContent().build();
  }
}
