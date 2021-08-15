package com.msavchuk.security.jwt.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Objects;

public class JwtGrantedAuthority implements GrantedAuthority {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final String authority;
	
	@JsonCreator
	public JwtGrantedAuthority(@JsonProperty("authority") String authority) {
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

	@Override
	public String toString() {
		return "[authority=" + authority + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof JwtGrantedAuthority)) return false;
		JwtGrantedAuthority that = (JwtGrantedAuthority) o;
		return Objects.equals(authority, that.authority);
	}

	@Override
	public int hashCode() {
		if (authority == null)
			return 31;

		return authority.hashCode();
	}
}
