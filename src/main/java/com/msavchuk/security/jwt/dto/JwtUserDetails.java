package com.msavchuk.security.jwt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.security.core.userdetails.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtUserDetails {

    protected Long id;

    protected String email;

    protected Set<JwtGrantedAuthority> authorities;

    protected boolean accountNonExpired;

    protected boolean accountNonLocked;

    protected boolean credentialsNonExpired;

    protected boolean enabled;

    protected String salt;

    protected JwtOrganizationDetails organization;

    protected String identity;

    protected Integer identityType;

    public JwtUserDetails() {
    }

    public JwtUserDetails(User user) {
        this.email = user.getUsername();
        this.accountNonExpired = user.isAccountNonExpired();
        this.accountNonLocked = user.isAccountNonLocked();
        this.credentialsNonExpired = user.isCredentialsNonExpired();
        this.enabled = user.isEnabled();
        this.authorities = user.getAuthorities().stream().map(grand -> new JwtGrantedAuthority(grand.getAuthority()))
                .collect(Collectors.toSet());
        this.salt = UUID.randomUUID().toString();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<JwtGrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<JwtGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public JwtOrganizationDetails getOrganization() {
        return organization;
    }

    public void setOrganization(JwtOrganizationDetails organization) {
        this.organization = organization;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Integer getIdentityType() {
        return identityType;
    }

    public void setIdentityType(Integer identityType) {
        this.identityType = identityType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long expertId) {
        this.id = expertId;
    }

    @Override public String toString() {
        return "JwtUserDetails{" +
                "id=" + id +
                ", username='" + email + '\'' +
                ", authorities=" + authorities +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", enabled=" + enabled +
                ", salt='" + salt + '\'' +
                ", identity=" + identity +
                ", identityType=" + identityType +
                '}';
    }
}
