package pl.marcinm312.springbootimageuploader.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.repo.AppUserRepo;
import pl.marcinm312.springbootimageuploader.service.UserService;
import pl.marcinm312.springbootimageuploader.testdataprovider.UserDataProvider;
import pl.marcinm312.springbootimageuploader.utils.SessionUtils;
import pl.marcinm312.springbootimageuploader.validator.UserValidator;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class MyProfileGuiTest {

	@Mock
	AppUserRepo appUserRepo;

	@Mock
	SessionUtils sessionUtils;

	@InjectMocks
	UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void myProfileGuiTest_updateUserWithLoginAndEmailChange_success() {
		String newLogin = "hhhhhh";
		String newEmail = "aaa@abc.com";
		given(appUserRepo.findByUsername(newLogin)).willReturn(Optional.empty());

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("User successfully updated", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		myProfileGui.button.click();

		verify(sessionUtils, times(1))
				.expireUserSessions(myProfileGui.getAuthenticatedUser(userService).getUsername(), true);
		verify(sessionUtils, times(1))
				.expireUserSessions(newLogin, true);
	}

	@Test
	void myProfileGuiTest_updateUserWithOnlyEmailChange_success() {
		String newEmail = "aaa@abc.com";

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("User successfully updated", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		myProfileGui.button.click();

		verify(sessionUtils, never())
				.expireUserSessions(myProfileGui.getAuthenticatedUser(userService).getUsername(), true);
	}

	@Test
	void myProfileGuiTest_updateUserWithTooShortLogin_binderIsNotValid() {
		String newLogin = "hh";
		String newEmail = "aaa@abc.com";

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: Check the validation messages on the form", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertFalse(binderResult);

		myProfileGui.button.click();

		verify(sessionUtils, never())
				.expireUserSessions(myProfileGui.getAuthenticatedUser(userService).getUsername(), true);
		verify(sessionUtils, never())
				.expireUserSessions(newLogin, true);
	}

	@Test
	void myProfileGuiTest_updateUserWithLoginThatAlreadyExists_notificationThatUserExists() {
		String newLogin = "hhhhhh";
		String newPassword = "aaa@abc.com";
		given(appUserRepo.findByUsername(newLogin)).willReturn(Optional.of(new AppUser()));

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: This user already exists!", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newPassword);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		myProfileGui.button.click();

		verify(sessionUtils, never())
				.expireUserSessions(myProfileGui.getAuthenticatedUser(userService).getUsername(), true);
		verify(sessionUtils, never())
				.expireUserSessions(newLogin, true);
	}

	@Test
	void myProfileGuiTest_updateUserWithInvalidEmail_binderIsNotValid() {
		String newLogin = "hhhhhh";
		String newEmail = "aaaaaaaaaaaaaaaaaa";

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: Check the validation messages on the form", notificationText);
			}

			@Override
			protected AppUser getAuthenticatedUser(UserService userService) {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertFalse(binderResult);

		myProfileGui.button.click();

		verify(sessionUtils, never())
				.expireUserSessions(myProfileGui.getAuthenticatedUser(userService).getUsername(), true);
		verify(sessionUtils, never())
				.expireUserSessions(newLogin, true);
	}
}
