package com.pemig.api.loan.repository;

import com.pemig.api.loan.model.Loan;
import com.pemig.api.util.QueryParams;
import com.pemig.api.util.logger.Logs;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
 * A persistence interface used exclusively to provide a composable interface pattern. 
 * 
 * @author caleb
 * 
 * @see LoanQueryParamsRepositoryImpl
 */
@Logs
public interface LoanQueryParamsRepository {

  /**
   * Returns a paginated and sorted list of {@link Loan} instances according to the specified criteria.
   * @param params An instance of {@link QueryParams} that contains all the information for pagination
   *               and sorting.
   * @param loggedInUser The currently logged in {@link User}.
   *                     
   * @return A list of {@link Loan} instances that satisfy the provided criteria.
   */
  List<Loan> findLoansByProvidedFilters(QueryParams params, User loggedInUser);
}
