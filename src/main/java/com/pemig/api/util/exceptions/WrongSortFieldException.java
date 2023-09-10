package com.pemig.api.util.exceptions;

import java.util.List;
import lombok.Getter;

@Getter
public class WrongSortFieldException extends RuntimeException {

  private final String fieldSpecified;
  private final List<String> acceptableFields;

  public WrongSortFieldException(String field, List<String> availableFields) {
    super(
        "Invalid sort: " + field + " specified. \\n Acceptable fields are: " + availableFields.toString() + ".");
    this.fieldSpecified = field;
    this.acceptableFields = List.copyOf(availableFields);
  }
}
