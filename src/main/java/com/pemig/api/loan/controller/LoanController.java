package com.pemig.api.loan.controller;

import com.pemig.api.loan.model.LoanAssembler;
import com.pemig.api.loan.model.LoanDto;
import com.pemig.api.loan.service.LoanService;
import com.pemig.api.util.QueryParams;
import com.pemig.api.util.SortingOrder;
import com.pemig.api.util.exceptions.LoanNameBlankException;
import com.pemig.api.util.exceptions.LoanNameNotValidException;
import com.pemig.api.util.exceptions.WrongSortFieldException;
import com.pemig.api.util.logger.Logs;
import com.pemig.api.util.Const;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/pemig")
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "2. Loan endpoints")
@Validated
@Logs
public class LoanController {

  private final LoanService loanService;

  private final LoanAssembler assembler;

  @Operation(summary = "Add Loan")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Loan created",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema =  @Schema(
                          type = "object",
                          additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                          ref = "#/components/schemas/LoanDto"))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Loan Name not provided",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
      })
  @PostMapping("/loan")
  public ResponseEntity<EntityModel<LoanDto>> postLoan(@RequestBody @Valid LoanDto loanDto)
      throws LoanNameNotValidException {
    if (StringUtils.isBlank(loanDto.getName())) {
      throw new LoanNameNotValidException();
    }
    return new ResponseEntity<>(
        assembler.toModel(loanService.storeLoan(loanDto)), HttpStatus.CREATED);
  }

  @Operation(summary = "Get loan by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Loan retrieved",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema =  @Schema(
                          type = "object",
                          additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                          ref = "#/components/schemas/LoanDto"))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized.",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Loan not found", content = @Content)
      })
  @GetMapping("/loan/{id}")
  public ResponseEntity<EntityModel<LoanDto>> getLoan(@PathVariable Long id) {
    return ResponseEntity.ok(assembler.toModel(loanService.getLoan(id)));
  }

  @Operation(summary = "GEt list of loans by specific parameters")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Loans retrieved",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(
                          type = "object",
                          additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                          ref = "#/components/schemas/LoanDto")))
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
  @GetMapping("/loan")
  public ResponseEntity<CollectionModel<EntityModel<LoanDto>>> aggregateGetLoans(
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
      @RequestParam(name = "page", defaultValue = Const.DEFAULT_PAGE_IDX) @Min(0) Integer page,
      @RequestParam(name = "items_in_page", defaultValue = Const.DEFAULT_PAGE_SIZE) @Min(1)
          Integer pageSize,
      @RequestParam(name = "sort_by_field", defaultValue = Const.DEFAULT_SORT_BY_FIELD) @NonNull @NotBlank
          String sortByField,
      @RequestParam(name = "sortingOrder", defaultValue = Const.DEFAULT_SORT_ORDER) @NonNull
          SortingOrder sortingOrder)
      throws WrongSortFieldException {
    List<String> loanFieldNames =
        Arrays.stream(LoanDto.class.getDeclaredFields()).map(Field::getName).toList();
    if (!loanFieldNames.contains(sortByField)) {
      throw new WrongSortFieldException(sortByField, loanFieldNames);
    }
    return ResponseEntity.ok(
        assembler.toCollectionModel(
            loanService.getAllLoansByFilter(
                QueryParams.builder()
                    .filterParams(filterParams)
                    .page(page)
                    .pageSize(pageSize)
                    .sortByField(sortByField)
                    .sortingOrder(sortingOrder)
                    .build())));
  }


  @Operation(summary = "Replace a Loan")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Loan successfully replaced",
            content =
                @Content(
                    mediaType = "application/json",
                    schema =  @Schema(
                            type = "object",
                            additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                            ref = "#/components/schemas/LoanDto"))),
        @ApiResponse(
            responseCode = "400",
            description = "Loan Name not provided",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Loan not found", content = @Content)
      })
  @PutMapping("/loan/{id}")
  public ResponseEntity<EntityModel<LoanDto>> putLoan(
      @PathVariable Long id, @RequestBody @Valid LoanDto loanDto)
      throws LoanNameNotValidException {
    if (StringUtils.isBlank(loanDto.getName())) {
      throw new LoanNameNotValidException();
    }
    return ResponseEntity.ok(assembler.toModel(loanService.replaceLoan(id, loanDto)));
  }

  @Operation(summary = "Update a Loan")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Loan updated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema =  @Schema(
                            type = "object",
                            additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                            ref = "#/components/schemas/LoanDto"))),
        @ApiResponse(
            responseCode = "400",
            description = "Attempted to clear loan name.",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Loan not found", content = @Content)
      })
  @PatchMapping("/loan/{id}")
  public ResponseEntity<EntityModel<LoanDto>> patchLoan(
      @PathVariable Long id, @RequestBody @Valid LoanDto loanDto)
      throws LoanNameBlankException {
    if (StringUtils.isWhitespace(
        loanDto.getName())) {
      throw new LoanNameBlankException();
    }
    return ResponseEntity.ok(assembler.toModel(loanService.updateLoan(id, loanDto)));
  }


  @Operation(summary = "Delete loan by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Loan successfully deleted",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Loan not found", content = @Content)
      })
  @DeleteMapping("/loan/{id}")
  public ResponseEntity<?> deleteLoan(@PathVariable Long id) {
    loanService.deleteLoan(id);
    return ResponseEntity.noContent().build();
  }
}
