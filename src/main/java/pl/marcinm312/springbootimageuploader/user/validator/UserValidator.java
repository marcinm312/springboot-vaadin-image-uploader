package pl.marcinm312.springbootimageuploader.user.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.service.UserService;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserValidator {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	public String validateUserRegistration(UserEntity user, String confirmPasswordValue) {

		String username = user.getUsername();
		Optional<UserEntity> optionalUser = userService.getOptionalUserByUsername(username);
		if (optionalUser.isPresent()) {
			return "Error: This user already exists!";
		}
		if (!confirmPasswordValue.equals(user.getPassword())) {
			return "Error: The passwords in both fields must be the same!";
		}
		return null;
	}

	public String validateUserDataUpdate(UserEntity newUser, String oldLogin) {

		if (!newUser.getUsername().equals(oldLogin) && userService.getOptionalUserByUsername(newUser.getUsername()).isPresent()) {
			return "Error: This user already exists!";
		}
		return null;
	}

	public String validateUserPasswordUpdate(UserEntity oldUser, String currentPasswordEntered, String newPassword, String confirmPasswordValue) {

		if (currentPasswordEntered == null || currentPasswordEntered.isEmpty() ||
				!passwordEncoder.matches(currentPasswordEntered, oldUser.getPassword())) {
			return "Error: The current password is incorrect";
		}
		if (!confirmPasswordValue.equals(newPassword)) {
			return "Error: The passwords in both fields must be the same!";
		}
		if (currentPasswordEntered.equals(newPassword)) {
			return "Error: The new password must be different from the previous one!";
		}
		return null;
	}
}
