package com.pemig.api.loan.model;


import com.pemig.api.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

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
@Table(name = "loan_package")
public class LoanPackage extends Auditable<String> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotBlank
    @Size(max = 50)
    private Integer duration;

    private float rate;
    private Integer minimum, maximum;
    private String description;

}

