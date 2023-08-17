package pl.marcinm312.springbootimageuploader.user.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.model.dto.UserCreate;
import pl.marcinm312.springbootimageuploader.user.model.dto.UserDataUpdate;
import pl.marcinm312.springbootimageuploader.user.model.dto.UserPasswordUpdate;
import pl.marcinm312.springbootimageuploader.user.service.UserService;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserValidator {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	public String validateUserRegistration(UserCreate user) {

		String username = user.getUsername();
		String password = user.getPassword();
		String confirmPassword = user.getConfirmPassword();

		Optional<UserEntity> optionalUser = userService.getUserByUsername(username);
		if (optionalUser.isPresent()) {
			return "Error: This user already exists!";
		}

		if (!password.equals(confirmPassword)) {
			return "Error: The passwords in both fields must be the same!";
		}

		return null;
	}

	public String validateUserDataUpdate(UserDataUpdate userDataUpdate, UserEntity loggedUser) {

		String newUsername = userDataUpdate.getUsername();
		String loggedUserLogin = loggedUser.getUsername();

		if (!loggedUserLogin.equals(newUsername) && userService.getUserByUsername(newUsername).isPresent()) {
			return "Error: This user already exists!";
		}
		return null;
	}

	public String validateUserPasswordUpdate(UserPasswordUpdate userPasswordUpdate, UserEntity loggedUser) {

		String currentPassword = userPasswordUpdate.getCurrentPassword();
		String password = userPasswordUpdate.getPassword();
		String confirmPassword = userPasswordUpdate.getConfirmPassword();

		if (!passwordEncoder.matches(currentPassword, loggedUser.getPassword())) {
			return "Error: The current password is incorrect";
		}
		if (currentPassword.equals(password)) {
			return "Error: The new password must be different from the previous one!";
		}
		if (!password.equals(confirmPassword)) {
			return "Error: The passwords in both fields must be the same!";
		}
		return null;
	}
}
