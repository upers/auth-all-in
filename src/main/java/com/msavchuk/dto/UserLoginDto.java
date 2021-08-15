package com.msavchuk.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto {
	@Getter @Setter
	private String username;
	@Getter @Setter
	private String password;

}
