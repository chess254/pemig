package com.pemig.api.card.repository;

import com.pemig.api.card.model.Card;
import com.pemig.api.util.QueryParams;
import com.pemig.api.util.logger.Logs;
import java.util.List;

import org.springframework.security.core.userdetails.User;

/**
 * A persistence interface used exclusively to provide a composable interface pattern. 
 * 
 * @author caleb
 * 
 * @see CardQueryParamsRepositoryImpl
 */
@Logs
public interface CardQueryParamsRepository {

  /**
   * Returns a paginated and sorted list of {@link Card} instances according to the specified criteria.
   * @param params An instance of {@link QueryParams} that contains all the information for pagination
   *               and sorting.
   * @param loggedInUser The currently logged in {@link User}.
   *                     
   * @return A list of {@link Card} instances that satisfy the provided criteria.
   */
  List<Card> findCardsByProvidedFilters(QueryParams params, User loggedInUser);
}
