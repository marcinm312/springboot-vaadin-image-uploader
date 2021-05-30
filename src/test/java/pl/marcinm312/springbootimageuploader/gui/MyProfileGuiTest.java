package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.repo.AppUserRepo;
import pl.marcinm312.springbootimageuploader.repo.ImageRepo;
import pl.marcinm312.springbootimageuploader.service.UserService;
import pl.marcinm312.springbootimageuploader.testdataprovider.UserDataProvider;
import pl.marcinm312.springbootimageuploader.utils.SessionUtils;
import pl.marcinm312.springbootimageuploader.validator.UserValidator;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class MyProfileGuiTest {

	private final UI ui = new UI();

	@Mock
	AppUserRepo appUserRepo;

	@Mock
	ImageRepo imageRepo;

	@Mock
	SessionUtils sessionUtils;

	@InjectMocks
	UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		UI.setCurrent(ui);
		VaadinSession session = Mockito.mock(VaadinSession.class);
		Mockito.when(session.hasLock()).thenReturn(true);
		ui.getInternals().setSession(session);
	}

	@AfterEach
	void tearDown() {
		UI.setCurrent(null);
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
			protected AppUser getAuthenticatedUser() {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		myProfileGui.saveUserButton.click();

		verify(sessionUtils, times(1))
				.expireUserSessions(myProfileGui.getAuthenticatedUser().getUsername(), true);
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
			protected AppUser getAuthenticatedUser() {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		myProfileGui.saveUserButton.click();

		verify(sessionUtils, never())
				.expireUserSessions(myProfileGui.getAuthenticatedUser().getUsername(), true);
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
			protected AppUser getAuthenticatedUser() {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertFalse(binderResult);

		myProfileGui.saveUserButton.click();

		verify(sessionUtils, never())
				.expireUserSessions(myProfileGui.getAuthenticatedUser().getUsername(), true);
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
			protected AppUser getAuthenticatedUser() {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newPassword);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		myProfileGui.saveUserButton.click();

		verify(sessionUtils, never())
				.expireUserSessions(myProfileGui.getAuthenticatedUser().getUsername(), true);
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
			protected AppUser getAuthenticatedUser() {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};
		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertFalse(binderResult);

		myProfileGui.saveUserButton.click();

		verify(sessionUtils, never())
				.expireUserSessions(myProfileGui.getAuthenticatedUser().getUsername(), true);
		verify(sessionUtils, never())
				.expireUserSessions(newLogin, true);
	}

	@Test
	void myProfileGuiTest_cancelDeletingUser_userIsNotDeleted() {

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator) {
			@Override
			protected AppUser getAuthenticatedUser() {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};

		myProfileGui.deleteUserButton.click();
		myProfileGui.cancelDeleteButton.click();
		verify(appUserRepo, never())
				.delete(any());
		verify(sessionUtils, never())
				.expireUserSessions(any(), eq(true));
		verify(imageRepo, never())
				.deleteUserFromImages(any());
	}

	@Test
	void myProfileGuiTest_confirmDeletingUser_userIsDeleted() {

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator) {
			@Override
			protected AppUser getAuthenticatedUser() {
				return UserDataProvider.prepareExampleGoodUser();
			}
		};

		AppUser appUser = myProfileGui.getAuthenticatedUser();

		myProfileGui.deleteUserButton.click();
		myProfileGui.confirmDeleteButton.click();
		verify(appUserRepo, times(1))
				.delete(appUser);
		verify(sessionUtils, times(1))
				.expireUserSessions(appUser.getUsername(), true);
		verify(imageRepo, times(1))
				.deleteUserFromImages(appUser);
	}
}
