package pl.marcinm312.springbootimageuploader.user.gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.marcinm312.springbootimageuploader.config.security.utils.SessionUtils;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;
import pl.marcinm312.springbootimageuploader.user.model.AppUser;
import pl.marcinm312.springbootimageuploader.user.repository.AppUserRepo;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.user.validator.UserValidator;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class UpdatePasswordGuiTest {

	@Mock
	private AppUserRepo appUserRepo;

	@Mock
	private SessionUtils sessionUtils;

	@Spy
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setUp() {
		mockedVaadinUtils = mockStatic(VaadinUtils.class);
		MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@Test
	void updatePasswordGuiTest_simpleCase_success() {
		String currentPassword = "password";
		String password = "hhhhh2";
		String confirmPassword = "hhhhh2";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, userValidator);

		updatePasswordGui.currentPasswordField.setValue(currentPassword);
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		updatePasswordGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("User password successfully updated")),
				times(1));
		verify(sessionUtils, times(1)).expireUserSessions(oldLogin, true);
	}

	@Test
	void updatePasswordGuiTest_tooShortPassword_binderIsNotValid() {
		String currentPassword = "password";
		String password = "hh2";
		String confirmPassword = "hh2";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, userValidator);

		updatePasswordGui.currentPasswordField.setValue(currentPassword);
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertFalse(binderResult);

		updatePasswordGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: Check the validation messages on the form")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
		Assertions.assertEquals("", updatePasswordGui.currentPasswordField.getValue());
		Assertions.assertEquals("", updatePasswordGui.passwordField.getValue());
		Assertions.assertEquals("", updatePasswordGui.confirmPasswordField.getValue());
	}

	@Test
	void updatePasswordGuiTest_differentPasswordConfirmation_notificationThatPasswordsMustBeTheSame() {
		String currentPassword = "password";
		String password = "hhhhh2";
		String confirmPassword = "hhhhh3";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, userValidator);

		updatePasswordGui.currentPasswordField.setValue(currentPassword);
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		updatePasswordGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: The passwords in both fields must be the same!")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
		Assertions.assertEquals("", updatePasswordGui.currentPasswordField.getValue());
		Assertions.assertEquals("", updatePasswordGui.passwordField.getValue());
		Assertions.assertEquals("", updatePasswordGui.confirmPasswordField.getValue());
	}

	@Test
	void updatePasswordGuiTest_passwordTheSameAsPrevious_notificationThatPasswordsMustBeDifferentAsPrevious() {
		String currentPassword = "password";
		String password = "password";
		String confirmPassword = "password";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, userValidator);

		updatePasswordGui.currentPasswordField.setValue(currentPassword);
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		updatePasswordGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: The new password must be different from the previous one!")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
		Assertions.assertEquals("", updatePasswordGui.currentPasswordField.getValue());
		Assertions.assertEquals("", updatePasswordGui.passwordField.getValue());
		Assertions.assertEquals("", updatePasswordGui.confirmPasswordField.getValue());
	}

	@Test
	void updatePasswordGuiTest_incorrectCurrentPassword_notificationThatCurrentPasswordIsIncorrect() {
		String currentPassword = "password2";
		String password = "hhhhh2";
		String confirmPassword = "hhhhh2";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, userValidator);

		updatePasswordGui.currentPasswordField.setValue(currentPassword);
		updatePasswordGui.passwordField.setValue(password);
		updatePasswordGui.confirmPasswordField.setValue(confirmPassword);
		boolean binderResult = updatePasswordGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		updatePasswordGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: The current password is incorrect")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
		Assertions.assertEquals("", updatePasswordGui.currentPasswordField.getValue());
		Assertions.assertEquals("", updatePasswordGui.passwordField.getValue());
		Assertions.assertEquals("", updatePasswordGui.confirmPasswordField.getValue());
	}
}
