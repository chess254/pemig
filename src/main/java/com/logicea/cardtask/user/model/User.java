package com.logicea.cardtask.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

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
@Table( name = "`USER`")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

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
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    User user = (User) o;
    return getId() != null && Objects.equals(getId(), user.getId());
  }

  @Override
  public final int hashCode() {
    return getClass().hashCode();
  }
}
