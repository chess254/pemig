package com.pemig.api.loan.repository;

import com.pemig.api.loan.model.LoanDetails;
import com.pemig.api.util.logger.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Logs
public interface LoanDetailsRepository extends JpaRepository<LoanDetails, Long> {}
