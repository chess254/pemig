package com.logicea.cardtask.util;

import java.util.Map;
import java.util.function.Predicate;
import lombok.Builder;
import lombok.Getter;

/**
 * A container for parameters used in pagination / sorting / filter aggregate GET queries.
 * 
 * @author caleb
 */
@Builder
@Getter
public class QueryParams {
  private Integer page;
  private Integer pageSize;
  private String sortByField;
  private SortingOrder sortingOrder;
  private Map<String, String> filterParams;
  private Predicate<?> predicate;
}
