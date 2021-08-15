package com.msavchuk.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "user_account")
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequences")
    @SequenceGenerator(name = "user_sequences", allocationSize = 1)
    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String firstName;

    @Getter @Setter
    private String lastName;

    @Getter @Setter
    private String email;

    @Column(length = 60)
    @Getter @Setter
    private String password;

    @Getter @Setter
    private boolean enabled;

    @Getter @Setter
    private boolean isUsing2FA;

    @Getter @Setter
    private Date registrationTime;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Getter @Setter
    private Set<Role> roles;

    public User() {
        super();
        this.enabled = false;
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
        final User user = (User) obj;
        if (!email.equals(user.email))
            return false;

        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [id=").append(id).append(", firstName=").append(firstName).append(", lastName=").append(lastName).append(", email=").append(email).append(", password=").append(password).append(", enabled=").append(enabled).append(", isUsing2FA=")
                .append(isUsing2FA).append(", roles=").append(roles).append("]");
        return builder.toString();
    }

}