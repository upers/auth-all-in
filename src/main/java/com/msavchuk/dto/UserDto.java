package com.msavchuk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.msavchuk.persistence.model.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @NotNull
    @Size(min = 4, message = "Length must be greater than 4")
    @Getter @Setter
    private String firstName;

    @NotNull
    @Size(min = 4, message = "Length must be greater than 4\"")
    @Getter @Setter
    private String lastName;

    @Getter @Setter
    private String email;

    @Getter @Setter
    private String password;

    @NotNull
    @Getter @Setter
    private Boolean enabled;

    @NotNull
    @Getter @Setter
    private Boolean isUsing2FA;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Getter @Setter
    private Set<Role> roles;

    public UserDto() {
        super();
        this.enabled = false;
        this.isUsing2FA = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((email == null) ? 0 : email.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserDto user = (UserDto) obj;
        return email.equals(user.email);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [id=").append(id).append(", firstName=").append(firstName).append(", lastName=")
               .append(lastName).append(", email=").append(email).append(", password=").append(password)
               .append(", enabled=").append(enabled).append(", isUsing2FA=")
               .append(isUsing2FA).append(", roles=").append(roles).append("]");
        return builder.toString();
    }

}