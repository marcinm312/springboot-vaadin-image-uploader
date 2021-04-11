package pl.marcinm312.springbootimageuploader.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.repo.AppUserRepo;
import pl.marcinm312.springbootimageuploader.service.UserService;
import pl.marcinm312.springbootimageuploader.testdataprovider.UserDataProvider;
import pl.marcinm312.springbootimageuploader.utils.SessionUtils;

import static org.mockito.Mockito.*;


class UpdatePasswordGuiTest {

	@Mock
	AppUserRepo appUserRepo;

	@Mock
	SessionUtils sessionUtils;

	@Spy
	BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void updatePasswordGuiTest_simpleCase_success() {
		String currentPassword = "password";
		String password = "hhhhh2";
		String confirmPassword = "hhhhh2";

		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, passwordEncoder) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("User password successfully updated", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
			}
		};
		updatePasswordGui.currentPasswordField.setValue(currentPassword);
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		updatePasswordGui.button.click();

		verify(sessionUtils, times(1))
				.expireUserSessions(updatePasswordGui.getAuthenticatedUser(userService).getUsername(), true);
	}

	@Test
	void updatePasswordGuiTest_tooShortPassword_binderIsNotValid() {
		String currentPassword = "password";
		String password = "hh2";
		String confirmPassword = "hh2";

		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, passwordEncoder) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: Check the validation messages on the form", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
			}
		};
		updatePasswordGui.currentPasswordField.setValue(currentPassword);
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertFalse(binderResult);

		updatePasswordGui.button.click();

		verify(sessionUtils, never())
				.expireUserSessions(updatePasswordGui.getAuthenticatedUser(userService).getUsername(), true);
	}

	@Test
	void updatePasswordGuiTest_differentPasswordConfirmation_notificationThatPasswordsMustBeTheSame() {
		String currentPassword = "password";
		String password = "hhhhh2";
		String confirmPassword = "hhhhh3";

		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, passwordEncoder) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: The passwords in both fields must be the same!", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
			}
		};
		updatePasswordGui.currentPasswordField.setValue(currentPassword);
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		updatePasswordGui.button.click();

		verify(sessionUtils, never())
				.expireUserSessions(updatePasswordGui.getAuthenticatedUser(userService).getUsername(), true);
	}

	@Test
	void updatePasswordGuiTest_passwordTheSameAsPrevious_notificationThatPasswordsMustBeDifferentAsPrevious() {
		String currentPassword = "password";
		String password = "password";
		String confirmPassword = "password";

		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, passwordEncoder) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: The new password must be different from the previous one!", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
			}
		};
		updatePasswordGui.currentPasswordField.setValue(currentPassword);
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		updatePasswordGui.button.click();

		verify(sessionUtils, never())
				.expireUserSessions(updatePasswordGui.getAuthenticatedUser(userService).getUsername(), true);
	}

	@Test
	void updatePasswordGuiTest_incorrectCurrentPassword_notificationThatCurrentPasswordIsIncorrect() {
		String currentPassword = "password2";
		String password = "hhhhh2";
		String confirmPassword = "hhhhh2";

		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, passwordEncoder) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: The current password is incorrect", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
			}
		};
		updatePasswordGui.currentPasswordField.setValue(currentPassword);
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		updatePasswordGui.button.click();

		verify(sessionUtils, never())
				.expireUserSessions(updatePasswordGui.getAuthenticatedUser(userService).getUsername(), true);
	}
}
