package pl.marcinm312.springbootimageuploader.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserPasswordUpdate {

	@NotBlank(message = "This field must be completed!")
	private String currentPassword;

	@NotBlank(message = "This field must be completed!")
	@Size(min = 5, message = "This field must contain at least 5 characters")
	private String password;

	@NotBlank(message = "This field must be completed!")
	@Size(min = 5, message = "This field must contain at least 5 characters")
	private String confirmPassword;
}
