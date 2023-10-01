package com.pemig.api.loan.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pemig.api.audit.Auditable;
import com.pemig.api.user.model.User;
import com.pemig.api.util.Const;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DB entity class for loans.
 *
 * @author caleb
 * @see LoanDto
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "LOAN_DETAILS")
public class LoanDetails extends Auditable<String> {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional=false, mappedBy="loanDetails")
  private Loan loan;

  @OneToOne
  @JoinColumn(name = "approved_by_id")
  private User approvedBy;

  @Column(name = "approved_date_time", updatable = false, nullable = false)
  @DateTimeFormat(pattern = Const.GLOBAL_DATE_TIME_PATTERN)
  @JsonFormat(pattern = Const.GLOBAL_DATE_TIME_PATTERN)
  @CreatedDate
  protected LocalDateTime approvedDateTime;

  @OneToOne
  @JoinColumn(name = "disbursed_by_id")
  private User disbursedBy;

  @Column(name = "disbursed_date_time", updatable = false, nullable = false)
  @DateTimeFormat(pattern = Const.GLOBAL_DATE_TIME_PATTERN)
  @JsonFormat(pattern = Const.GLOBAL_DATE_TIME_PATTERN)
  @CreatedDate
  protected LocalDateTime disbursedDateTime;

  @OneToOne
  @JoinColumn(name = "rejected_by_id")
  private User rejectedBy;

  @Column(name = "rejected_date_time", updatable = false, nullable = false)
  @DateTimeFormat(pattern = Const.GLOBAL_DATE_TIME_PATTERN)
  @JsonFormat(pattern = Const.GLOBAL_DATE_TIME_PATTERN)
  @CreatedDate
  protected LocalDateTime rejectedDateTime;

  private String nationalIdOrPassportUrl;

  private String businessRegistrationDocumentUrl;

  private String payslipUrl;
  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    LoanDetails that = (LoanDetails) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return getClass().hashCode();
  }
}
