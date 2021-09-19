package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.*;
import org.mockito.*;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.repo.AppUserRepo;
import pl.marcinm312.springbootimageuploader.repo.ImageRepo;
import pl.marcinm312.springbootimageuploader.service.UserService;
import pl.marcinm312.springbootimageuploader.testdataprovider.UserDataProvider;
import pl.marcinm312.springbootimageuploader.utils.SessionUtils;
import pl.marcinm312.springbootimageuploader.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.validator.UserValidator;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class MyProfileGuiTest {

	private final UI ui = new UI();

	@Mock
	private AppUserRepo appUserRepo;

	@Mock
	private ImageRepo imageRepo;

	@Mock
	private SessionUtils sessionUtils;

	@InjectMocks
	private UserService userService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setUp() {
		mockedVaadinUtils = mockStatic(VaadinUtils.class);
		MockitoAnnotations.openMocks(this);

		UI.setCurrent(ui);
		VaadinSession session = Mockito.mock(VaadinSession.class);
		Mockito.when(session.hasLock()).thenReturn(true);
		ui.getInternals().setSession(session);
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
		UI.setCurrent(null);
	}

	@Test
	void myProfileGuiTest_updateUserWithLoginAndEmailChange_success() {
		String newLogin = "hhhhhh";
		String newEmail = "aaa@abc.com";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));
		given(appUserRepo.findByUsername(newLogin)).willReturn(Optional.empty());

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator);

		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);
		Assertions.assertTrue(myProfileGui.loginTextField.isEnabled());

		myProfileGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("User successfully updated")),
				times(1));
		verify(sessionUtils, times(1)).expireUserSessions(oldLogin, true);
		verify(sessionUtils, never()).expireUserSessions(newLogin, true);
	}

	@Test
	void myProfileGuiTest_updateEmptyEmailUserWithLoginAndEmailChange_success() {
		String newLogin = "hhhhhh";
		String newEmail = "aaa@abc.com";

		AppUser loggedUser = UserDataProvider.prepareExampleUserWithNullEmail();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));
		given(appUserRepo.findByUsername(newLogin)).willReturn(Optional.empty());

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator);

		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);
		Assertions.assertTrue(myProfileGui.loginTextField.isEnabled());

		myProfileGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("User successfully updated")),
				times(1));
		verify(sessionUtils, times(1)).expireUserSessions(oldLogin, true);
		verify(sessionUtils, never()).expireUserSessions(newLogin, true);
	}

	@Test
	void myProfileGuiTest_updateUserWithOnlyEmailChange_success() {
		String newEmail = "aaa@abc.com";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator);

		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);
		Assertions.assertTrue(myProfileGui.loginTextField.isEnabled());

		myProfileGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("User successfully updated")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
	}

	@Test
	void myProfileGuiTest_updateAdministratorWithOnlyEmailChange_success() {
		String newEmail = "aaa@abc.com";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodAdministrator();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator);

		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);
		Assertions.assertFalse(myProfileGui.loginTextField.isEnabled());

		myProfileGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("User successfully updated")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
	}

	@Test
	void myProfileGuiTest_updateUserWithTooShortLogin_binderIsNotValid() {
		String newLogin = "hh";
		String newEmail = "aaa@abc.com";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator);

		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertFalse(binderResult);
		Assertions.assertTrue(myProfileGui.loginTextField.isEnabled());

		myProfileGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: Check the validation messages on the form")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
		verify(sessionUtils, never()).expireUserSessions(newLogin, true);
	}

	@Test
	void myProfileGuiTest_stringTrimmerTestInLogin_validationMessage() {
		String newLogin = " hh ";
		String newEmail = "aaa@abc.com";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator);

		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);
		Assertions.assertTrue(myProfileGui.loginTextField.isEnabled());

		myProfileGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: Check the validation messages on the form")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
		verify(sessionUtils, never()).expireUserSessions(newLogin, true);
	}

	@Test
	void myProfileGuiTest_updateUserWithLoginThatAlreadyExists_notificationThatUserExists() {
		String newLogin = "hhhhhh";
		String newPassword = "aaa@abc.com";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));
		given(appUserRepo.findByUsername(newLogin)).willReturn(Optional.of(new AppUser()));

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator);

		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newPassword);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertTrue(binderResult);
		Assertions.assertTrue(myProfileGui.loginTextField.isEnabled());

		myProfileGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: This user already exists!")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
		verify(sessionUtils, never()).expireUserSessions(newLogin, true);
	}

	@Test
	void myProfileGuiTest_updateUserWithInvalidEmail_binderIsNotValid() {
		String newLogin = "hhhhhh";
		String newEmail = "aaaaaaaaaaaaaaaaaa";

		AppUser loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator);

		myProfileGui.loginTextField.setValue(newLogin);
		myProfileGui.emailTextField.setValue(newEmail);
		boolean binderResult = myProfileGui.binder.isValid();

		Assertions.assertFalse(binderResult);
		Assertions.assertTrue(myProfileGui.loginTextField.isEnabled());

		myProfileGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: Check the validation messages on the form")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
		verify(sessionUtils, never()).expireUserSessions(newLogin, true);
	}

	@Test
	void myProfileGuiTest_cancelDeletingUser_userIsNotDeleted() {
		AppUser loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator);

		myProfileGui.deleteUserButton.click();
		myProfileGui.cancelDeleteButton.click();

		verify(appUserRepo, never()).delete(any());
		verify(sessionUtils, never()).expireUserSessions(any(), eq(true));
		verify(imageRepo, never()).deleteUserFromImages(any());
	}

	@Test
	void myProfileGuiTest_confirmDeletingUser_userIsDeleted() {
		AppUser loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator);

		myProfileGui.deleteUserButton.click();
		myProfileGui.confirmDeleteButton.click();

		verify(appUserRepo, times(1)).delete(loggedUser);
		verify(sessionUtils, times(1)).expireUserSessions(oldLogin, true);
		verify(imageRepo, times(1)).deleteUserFromImages(loggedUser);
	}

	@Test
	void myProfileGuiTest_logoutFromOtherDevices_sessionsAreExpired() {
		AppUser loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userValidator);

		myProfileGui.expireSessionsButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("You have been successfully logged out from other devices")),
				times(1));
		verify(sessionUtils, times(1))
				.expireUserSessions(oldLogin, false);
	}
}
