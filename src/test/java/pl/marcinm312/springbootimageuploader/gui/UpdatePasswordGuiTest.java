package pl.marcinm312.springbootimageuploader.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.repo.AppUserRepo;
import pl.marcinm312.springbootimageuploader.service.UserService;
import pl.marcinm312.springbootimageuploader.testdataprovider.UserDataProvider;
import pl.marcinm312.springbootimageuploader.utils.SessionUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class UpdatePasswordGuiTest {

	@Mock
	AppUserRepo appUserRepo;

	@Mock
	SessionUtils sessionUtils;

	@Mock
	PasswordEncoder passwordEncoder;

	@InjectMocks
	UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		given(passwordEncoder.encode(any(CharSequence.class))).willReturn("encodedPassword");
	}

	@Test
	void updatePasswordGuiTest_simpleCase_success() {
		String password = "hhhhh2";
		String confirmPassword = "hhhhh2";

		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, passwordEncoder) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("User password successfully updated", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
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
		String password = "hh2";
		String confirmPassword = "hh2";

		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, passwordEncoder) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: Check the validation messages on the form", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertFalse(binderResult);

		updatePasswordGui.button.click();

		verify(sessionUtils, never())
				.expireUserSessions(updatePasswordGui.getAuthenticatedUser(userService).getUsername(), true);
	}

	@Test
	void updatePasswordGuiTest_differentPasswords_notificationThatPasswordsMustBeTheSame() {
		String password = "hhhhh2";
		String confirmPassword = "hhhhh3";

		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, passwordEncoder) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: The passwords in both fields must be the same!", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		updatePasswordGui.button.click();

		verify(sessionUtils, never())
				.expireUserSessions(updatePasswordGui.getAuthenticatedUser(userService).getUsername(), true);
	}
}
