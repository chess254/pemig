package com.pemig.api.loan.service;

import com.pemig.api.loan.model.Loan;
import com.pemig.api.loan.model.LoanDetails;
import com.pemig.api.loan.model.LoanDto;
import com.pemig.api.loan.model.LoanStatus;
import com.pemig.api.loan.repository.LoanDetailsRepository;
import com.pemig.api.loan.repository.LoanRepository;
import com.pemig.api.user.repository.UserRepository;
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
  private final LoanDetailsRepository loanDetailsRepository;
  private final AuthCheck authCheck;
  private final LoanDtoToLoan loanDtoToLoan;
  private final UserRepository userRepository;

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
    //save new loan with status applies
    User loggedInUser =
            (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String userEmail = loggedInUser.getUsername();
    Optional<com.pemig.api.user.model.User> user = userRepository.findByEmail(userEmail);
    if(user.isPresent()) {
      loanDto.setCustomerId(String.valueOf(user.get().getId()));
      //set initial loan status to APPLIED
      loanDto.setStatus(LoanStatus.APPLIED);
    }

    //TODO: sort out duplicate entry exception handling, try-catch
    //TODO find best way of choosing client when applying, either through logged in user of by supplying custoomer id or both depending on who is making the application.
    Loan storedLoan =
        loanRepository.save(
            Loan.builder()
              .customerName(user.get().getFirstName() + " " + user.get().getMiddleName() + " " + user.get().getLastName())
              .name(loanDto.getName())
              .customer(user.get())
              .principal(loanDto.getPrincipal())
              .rate(loanDto.getRate())
              .time(loanDto.getTime())
              .loanDetails(loanDetailsRepository.save(
                LoanDetails.builder()
                  .businessRegistrationDocumentUrl( loanDto.getBusinessRegDocUrl() )
                  .nationalIdOrPassportUrl(loanDto.getIdOrPassportUrl())
                  .payslipUrl(loanDto.getPayslipUrl())
                  .build())
              )
              .description(loanDto.getDescription())
              .build()
        );
    //TODO: publish an event to notify loan agent of loan application. include loan id
    return fromLoanEntityToLoanDto(storedLoan);
  }


  @Transactional(readOnly = true)
  public List<LoanDto> getAllLoansByFilter(QueryParams params)
      throws UnauthorizedException {
    User loggedInUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!authCheck.userIsAdmin(loggedInUser)
//        && filterParamsIncludeOtherMemberCards(loggedInUser, params.getFilterParams())
      )

      if(authCheck.userIsClient(loggedInUser)){
        return loanRepository.findLoansByProvidedFilters(params, loggedInUser).stream()
                .map(Utils::fromLoanEntityToLoanDto).toList();
      }
//    return loanRepository.findLoansByProvidedFilters(params, loggedInUser).stream()
//        .map(Utils::fromLoanEntityToLoanDto)
//        .collect(Collectors.toList());
    return loanRepository.findAll().stream()
        .map(Utils::fromLoanEntityToLoanDto)
        .toList();
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
