package com.pemig.api.loan.service;

import com.pemig.api.loan.model.Loan;
import com.pemig.api.loan.model.LoanDto;
import com.pemig.api.loan.model.LoanStatus;
import com.pemig.api.loan.repository.LoanRepository;
import com.pemig.api.user.service.AuthCheck;
import com.pemig.api.util.QueryParams;
import com.pemig.api.util.Utils;
import com.pemig.api.util.exceptions.LoanNotFoundException;
import com.pemig.api.util.exceptions.UnauthorizedException;
import com.pemig.api.util.logger.Logs;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pemig.api.util.Const.CREATING_USER_FILTER_STRING;
import static com.pemig.api.util.Utils.fromLoanEntityToLoanDto;

@Service
@RequiredArgsConstructor
@Logs
public class LoanService {

  private final LoanRepository loanRepository;
  private final AuthCheck authCheck;
  private final LoanDtoToLoan loanDtoToLoan;

  @Transactional(readOnly = true)
  public LoanDto getLoan(Long id) throws LoanNotFoundException, UnauthorizedException {
    Optional<Loan> loan = loanRepository.findById(id);
    if (loan.isEmpty()) {
      throw new LoanNotFoundException(id);
    }
    User loggedInUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!authCheck.userHasAccessToLoan(loggedInUser, loan.get())) {
      throw new UnauthorizedException(loggedInUser.getUsername());
    }
    return fromLoanEntityToLoanDto(loan.get());
  }

  @Transactional
  public LoanDto replaceLoan(Long id, LoanDto loanDto)
      throws LoanNotFoundException, UnauthorizedException {
    Optional<Loan> loanOptional = loanRepository.findById(id);
    if (loanOptional.isEmpty()) {
      throw new LoanNotFoundException(id);
    }
    User loggedInUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!authCheck.userHasAccessToLoan(loggedInUser, loanOptional.get())) {
      throw new UnauthorizedException(loggedInUser.getUsername());
    }
    Loan oldLoan = loanOptional.get();
    LocalDateTime createdDateTime = oldLoan.getCreatedDateTime();
    String createdBy = oldLoan.getCreatedBy();
    Loan newLoan =
        loanRepository.save(
            Loan.builder()
                .id(oldLoan.getId())
                .name(loanDto.getName())
                .description(loanDto.getDescription())
                .status(Optional.ofNullable(loanDto.getStatus()).orElse(LoanStatus.APPLIED))
                .build());
    newLoan.setCreatedDateTime(createdDateTime);
    newLoan.setCreatedBy(createdBy);
    newLoan.setLastModifiedDateTime(LocalDateTime.now());
    newLoan.setLastModifiedBy(loggedInUser.getUsername());
    return fromLoanEntityToLoanDto(newLoan);
  }

  @Transactional
  public LoanDto updateLoan(Long id, LoanDto cardDto){
    Optional<Loan> loanOptional = loanRepository.findById(id);
    if(loanOptional.isEmpty()){
      throw new LoanNotFoundException(id);
    }
    User loggedInUser =
            (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!authCheck.userHasAccessToLoan(loggedInUser, loanOptional.get())) {
      throw new UnauthorizedException(loggedInUser.getUsername());
    }
    Loan loan = loanOptional.get();
    LocalDateTime createdDateTime = loan.getCreatedDateTime();
    String createdBy = loan.getCreatedBy();
    loanDtoToLoan.updateEntityFromDto(cardDto, loan);
    Loan patchedLoan = loanRepository.save(Loan.builder()
            .id(loan.getId())
            .name(loan.getName())
            .description(loan.getDescription())
            .status(loan.getStatus())
            .build());
    patchedLoan.setCreatedDateTime(createdDateTime);
    patchedLoan.setCreatedBy(createdBy);
    patchedLoan.setLastModifiedDateTime(LocalDateTime.now());
    patchedLoan.setLastModifiedBy(loggedInUser.getUsername());
    return fromLoanEntityToLoanDto(patchedLoan);
  }

  @Transactional
  public LoanDto storeLoan(LoanDto loanDto) {
    Loan storedLoan =
        loanRepository.save(
            Loan.builder()
                .name(loanDto.getName())
                .description(loanDto.getDescription())
                .build());
    return fromLoanEntityToLoanDto(storedLoan);
  }


  @Transactional(readOnly = true)
  public List<LoanDto> getAllLoansByFilter(QueryParams params)
      throws UnauthorizedException {
    User loggedInUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!authCheck.userIsAdmin(loggedInUser)
//        && filterParamsIncludeOtherMemberCards(loggedInUser, params.getFilterParams())
      ) {
      throw new UnauthorizedException(loggedInUser.getUsername());
    }
//    return loanRepository.findLoansByProvidedFilters(params, loggedInUser).stream()
//        .map(Utils::fromLoanEntityToLoanDto)
//        .collect(Collectors.toList());
    return loanRepository.findAll().stream()
        .map(Utils::fromLoanEntityToLoanDto)
        .collect(Collectors.toList());
  }

  private boolean filterParamsIncludeOtherMemberCards(User user, Map<String, String> filterParams) {
    if (!filterParams.containsKey(CREATING_USER_FILTER_STRING)) {
      return false;
    }
    return !filterParams.get(CREATING_USER_FILTER_STRING).equals(user.getUsername());
  }

  @Transactional
  public void deleteLoan(Long id) throws LoanNotFoundException, UnauthorizedException {
    Optional<Loan> loan = loanRepository.findById(id);
    if (loan.isEmpty()) {
      throw new LoanNotFoundException(id);
    }
    User loggedInUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    if (!authCheck.userHasAccessToLoan(loggedInUser, loan.get())) {
//      throw new UnauthorizedException(loggedInUser.getUsername());
//    }

    //only super admin can delete loan, and it should be a soft deleted
      if (!authCheck.userIsAdmin(loggedInUser)) {
      throw new UnauthorizedException(loggedInUser.getUsername());
    }
      //TODO make this soft-delete
    loanRepository.deleteById(id);
  }
}
