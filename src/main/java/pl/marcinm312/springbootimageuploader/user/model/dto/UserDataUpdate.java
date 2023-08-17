package pl.marcinm312.springbootimageuploader.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDataUpdate {

	@NotBlank(message = "This field must be completed!")
	@Size(min = 3, max = 50, message = "This field must contain between 3 and 50 characters")
	private String username;

	@NotBlank(message = "This field must be completed!")
	@Email(message = "Incorrect email address!")
	private String email;

	@Override
	public String toString() {
		return "UserDataUpdate{" +
				"username='" + username + '\'' +
				'}';
	}
}
