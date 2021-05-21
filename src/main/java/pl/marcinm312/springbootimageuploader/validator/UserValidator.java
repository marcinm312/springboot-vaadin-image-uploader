package pl.marcinm312.springbootimageuploader.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.service.UserService;

import java.util.Optional;

@Component
public class UserValidator {

	private final UserService userService;

	@Autowired
	public UserValidator(UserService userService) {
		this.userService = userService;
	}

	public String validateUserRegistration(AppUser appUser, String confirmPasswordValue) {

		String username = appUser.getUsername();
		Optional<AppUser> optionalUser = userService.getUserByUsername(username);
		if (optionalUser.isPresent()) {
			return "Error: This user already exists!";
		}
		if (!confirmPasswordValue.equals(appUser.getPassword())) {
			return "Error: The passwords in both fields must be the same!";
		}
		return null;
	}

	public String validateUserDataUpdate(AppUser appUser, String oldLogin) {

		if (!appUser.getUsername().equals(oldLogin) && userService.getUserByUsername(appUser.getUsername()).isPresent()) {
			return "Error: This user already exists!";
		}
		return null;
	}

	public String validateUserPasswordUpdate(AppUser appUser, String currentPasswordEntered, String newPassword, String confirmPasswordValue) {

		if (!userService.isPasswordCorrect(appUser, currentPasswordEntered)) {
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
