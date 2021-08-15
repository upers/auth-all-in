package com.msavchuk.persistence.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY) // or JsonSerialize.Inclusion.NON_EMPTY
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_sequences")
    @SequenceGenerator(name = "role_sequences", allocationSize = 1)
    @Getter @Setter
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_privileges", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    @Getter @Setter
    private Set<Privilege> privileges;

    @Getter @Setter
    private String name;

    public Role() {
        super();
    }

    public Role(final String name) {
        super();
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        final Role role = (Role) obj;
        if (name == null)
            return false;
        else if (!name.equals(role.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Role [name=").append(name).append("]").append("[id=").append(id).append("]");
        return builder.toString();
    }
}