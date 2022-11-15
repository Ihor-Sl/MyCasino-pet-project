package ua.sl.igor.MyCasino.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.sl.igor.MyCasino.domain.enums.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@Entity(name = "casino_player")
public class Player implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotNull(message = "Name required!")
    @Size(min = 2, max = 100, message = "Length must be greater than 2 and lower than 100!")
    private String name;

    @Column(name = "email")
    @NotEmpty(message = "Email required!")
    @Email(message = "Email must be valid!")
    private String email;

    @NotNull(message = "Password required!")
    @Size(min = 8, max = 100, message = "Length must be greater than 8 and lower than 100!")
    @Column(name = "password")
    private String password;

    @Column(name = "balance")
    private long balance;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<RouletteBet> rouletteBets;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    public boolean isAdmin() {
        return role.equals(Role.ROLE_ADMIN);
    }

    @Column(name = "is_account_non_locked")
    private boolean isAccountNonLocked;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
