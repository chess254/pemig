package com.pemig.api.account.repository;

import com.pemig.api.account.Account;
import com.pemig.api.loan.model.LoanDetails;
import com.pemig.api.util.QueryParams;
import com.pemig.api.util.logger.Logs;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
 * A persistence interface used exclusively to provide a composable interface pattern. 
 * 
 * @author caleb
 * 
 * @see AccountQueryParamsRepositoryImpl
 */
@Logs
public interface AccountQueryParamsRepository {

  /**
   * Returns a paginated and sorted list of {@link LoanDetails} instances according to the specified criteria.
   *
   * @param params       An instance of {@link QueryParams} that contains all the information for pagination
   *                     and sorting.
   * @param loggedInUser The currently logged in {@link User}.
   * @return A list of {@link LoanDetails} instances that satisfy the provided criteria.
   */
  List<Account> findAccountsByProvidedFilters(QueryParams params, User loggedInUser);
}
