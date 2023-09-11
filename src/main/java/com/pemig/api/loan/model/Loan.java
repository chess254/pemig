package com.pemig.api.loan.model;


import com.pemig.api.audit.Auditable;
import com.pemig.api.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
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
@Table(name = "LOAN")
public class Loan extends Auditable<String> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotBlank
    @Size(max = 50)
    @Column(name = "customer_name", length = 50)
    private String name;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    private BigDecimal principal;


    private float rate;

    private int time;

    @OneToOne
    @JoinColumn(name = "loan_details_id", unique = true, updatable = false)
    private LoanDetails loanDetails;



    @Size(max = 100)
    @Column(name = "description", length = 100)
    private String description;

    @Pattern(
            regexp = "^#[a-fA-F0-9]{6}$|^$",
            message = "Color must start with # and end with exactly 6 hex characters, or be entirely empty.",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    @Column(name = "color", length = 7)
    private String color;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 11)
    private LoanStatus status = LoanStatus.APPLIED;

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

