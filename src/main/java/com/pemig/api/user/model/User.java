package com.pemig.api.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pemig.api.audit.Auditable;
import com.pemig.api.util.Const;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Database object for application users.
 *
 * @author caleb
 * @see UserDto
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`USER`")
public class User extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    @NonNull
    @NotBlank
    private String firstName;

    @Column(name = "middle_name")
    @NonNull
    @NotBlank
    private String middleName;

    @Column(name = "last_name")
    @NonNull
    @NotBlank
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    @DateTimeFormat(pattern = Const.GLOBAL_DATE_TIME_PATTERN)
    @JsonFormat(pattern = Const.GLOBAL_DATE_TIME_PATTERN)
    private LocalDateTime birthDate;

    @Column(name = "id_number", unique = true)
    @NonNull
    @NotBlank
    private String idNo;

    private String county;

    @Column(unique = true, name = "phone_number")
    @Size(min = 10, max = 10)
    private String phoneNumber;

    private String profilePic;

    @Column(name = "description", length = 100)
    @Size(max = 100)
    private String description;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "primary_account_number")
    private long primaryAccountNumber;

    @Column(name = "secondary_account_number")
    private long secondaryAccountNumber;

    @Column(name = "loan_account_number")
    private long loanAccountNumber;

    private boolean verified;


    @Column(name = "email_address", unique = true)
    @NonNull
    @Email
    @NotBlank
    private String email;

    @Column(name = "password")
    @JsonIgnore
    @ToString.Exclude
    @NonNull
    @NotBlank
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @NonNull
    private Role role;

    public User(@NonNull String email, @NonNull String password, @NonNull Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}
