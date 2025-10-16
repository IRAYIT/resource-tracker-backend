package com.ikonicit.resource.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ForgotPasswordDTO {

	@NotBlank(message = "Email must not be empty")
	private String email;
}
