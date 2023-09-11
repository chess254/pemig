package com.pemig.api.account.repository;

import com.pemig.api.account.Account;
import com.pemig.api.loan.repository.LoanQueryParamsRepository;
import com.pemig.api.util.logger.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Logs
public interface AccountRepository
        extends JpaRepository<Account, Long>, LoanQueryParamsRepository {}

