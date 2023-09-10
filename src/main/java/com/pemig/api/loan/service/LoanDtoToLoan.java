package com.pemig.api.loan.service;

import com.pemig.api.loan.model.Loan;
import com.pemig.api.loan.model.LoanDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

/**
 * A {@link Mapper} used by PATCH logic to map the fields of a {@link LoanDto} onto a {@link Loan} just pulled
 * from the database.
 *
 * @author caleb
 */
@Mapper(componentModel = "spring")
@Component
public interface LoanDtoToLoan {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(LoanDto loanDto, @MappingTarget Loan card);
}
