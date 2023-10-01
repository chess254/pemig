package com.pemig.api.util;


import com.pemig.api.card.model.CardDto;
import com.pemig.api.card.model.Card;
import com.pemig.api.loan.model.Loan;
import com.pemig.api.loan.model.LoanDetails;
import com.pemig.api.loan.model.LoanDto;

public final class Utils {
    
    private Utils(){}

    public static CardDto fromCardEntityToCardDto(Card card) {
        return CardDto.builder()
                .id(card.getId())
                .description(card.getDescription())
                .name(card.getName())
                .color(card.getColor())
                .status(card.getStatus())
                .created(card.getCreatedDateTime())
                .createdBy(card.getCreatedBy())
                .modifiedBy(card.getLastModifiedBy())
                .modified(card.getLastModifiedDateTime())
                .build();
    }

    public static LoanDto fromLoanEntityToLoanDto(Loan loan) {
        return LoanDto.builder()
                .id(loan.getId())
                .customerName(loan.getCustomerName())
                .name(loan.getName())
                .customerId(String.valueOf(loan.getCustomer().getId()))
                .principal(loan.getPrincipal())
                .rate(loan.getRate())
                .time(loan.getTime())
                .loanDetailsId(loan.getLoanDetails().getId())
                .description(loan.getDescription())
                .status(loan.getStatus())
                .created(loan.getCreatedDateTime())
                .createdBy(loan.getCreatedBy())
                .modifiedBy(loan.getLastModifiedBy())
                .modified(loan.getLastModifiedDateTime())
                .build();
    }
}
