package pl.marcinm312.springbootimageuploader.user.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.marcinm312.springbootimageuploader.config.security.utils.SessionUtils;
import pl.marcinm312.springbootimageuploader.image.repository.ImageRepo;
import pl.marcinm312.springbootimageuploader.shared.mail.MailService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.repository.MailChangeTokenRepo;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;
import pl.marcinm312.springbootimageuploader.user.service.UserDetailsServiceImpl;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;
import pl.marcinm312.springbootimageuploader.user.validator.UserValidator;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class MyProfileGuiTest {

	private final UI ui = new UI();

	@Mock
	private UserRepo userRepo;

	@Mock
	private ImageRepo imageRepo;

	@Mock
	private SessionUtils sessionUtils;

	@Mock
	private MailChangeTokenRepo mailChangeTokenRepo;

	@Mock
	private MailService mailService;

	@InjectMocks
	private UserService userService;

	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;

	@Spy
	private BCryptPasswordEncoder passwordEncoder;

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

	@ParameterizedTest
	@MethodSource("examplesOfLoginAndEmailChanges")
	void myProfileGuiTest_updateUserWithLoginAndEmailChange_success(UserEntity loggedUser) {

		String newLogin = "hhhhhh";
		String newEmail = "aaa@abc.com";
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(userRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));
		given(userRepo.findByUsername(newLogin)).willReturn(Optional.empty());

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userDetailsService, userValidator);

		Assertions.assertTrue(myProfileGui.getLoginTextField().isEnabled());

		myProfileGui.getLoginTextField().setValue(newLogin);
		myProfileGui.getEmailTextField().setValue(newEmail);
		myProfileGui.getSaveUserButton().click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("User successfully updated")),
				times(1));
		verify(sessionUtils, times(1)).expireUserSessions(oldLogin, true);
		verify(sessionUtils, never()).expireUserSessions(newLogin, true);
		verify(mailService, times(1)).sendMail(eq(loggedUser.getEmail()), any(String.class),
				any(String.class), eq(true));
	}

	@Test
	void myProfileGuiTest_updateUserWithOnlyLoginChange_success() {

		String newLogin = "hhhhhh";
		UserEntity loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(userRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));
		given(userRepo.findByUsername(newLogin)).willReturn(Optional.empty());

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userDetailsService, userValidator);

		Assertions.assertTrue(myProfileGui.getLoginTextField().isEnabled());

		myProfileGui.getLoginTextField().setValue(newLogin);
		myProfileGui.getSaveUserButton().click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("User successfully updated")),
				times(1));
		verify(sessionUtils, times(1)).expireUserSessions(oldLogin, true);
		verify(sessionUtils, never()).expireUserSessions(newLogin, true);
		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class),
				eq(true));
	}

	private static Stream<Arguments> examplesOfLoginAndEmailChanges() {

		return Stream.of(
				Arguments.of(UserDataProvider.prepareExampleGoodUser()),
				Arguments.of(UserDataProvider.prepareExampleUserWithNullEmail())
		);
	}

	@ParameterizedTest
	@MethodSource("examplesOfEmailChanges")
	void myProfileGuiTest_updateUserWithOnlyEmailChange_success(UserEntity loggedUser) {

		String newEmail = "aaa@abc.com";
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(userRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userDetailsService, userValidator);

		if ("admin".equals(oldLogin)) {
			Assertions.assertFalse(myProfileGui.getLoginTextField().isEnabled());
		} else {
			Assertions.assertTrue(myProfileGui.getLoginTextField().isEnabled());
		}

		myProfileGui.getEmailTextField().setValue(newEmail);
		myProfileGui.getSaveUserButton().click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("User successfully updated")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
		verify(mailService, times(1)).sendMail(eq(loggedUser.getEmail()), any(String.class),
				any(String.class), eq(true));
	}

	private static Stream<Arguments> examplesOfEmailChanges() {

		return Stream.of(
				Arguments.of(UserDataProvider.prepareExampleGoodUser()),
				Arguments.of(UserDataProvider.prepareExampleGoodAdministrator())
		);
	}

	@ParameterizedTest
	@MethodSource("examplesOfInvalidUserUpdates")
	void myProfileGuiTest_invalidUser_userIsNotUpdated(String newLogin, String newEmail) {

		UserEntity loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(userRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userDetailsService, userValidator);

		myProfileGui.getLoginTextField().setValue(newLogin);
		myProfileGui.getEmailTextField().setValue(newEmail);

		Assertions.assertTrue(myProfileGui.getLoginTextField().isEnabled());

		myProfileGui.getSaveUserButton().click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: Check the validation messages on the form")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
		verify(sessionUtils, never()).expireUserSessions(newLogin, true);
		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class),
				eq(true));
	}

	@Test
	void myProfileGuiTest_updateUserWithLoginThatAlreadyExists_notificationThatUserExists() {

		String newLogin = "hhhhhh";
		String newEmail = "aaa@abc.com";

		UserEntity loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(userRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));
		given(userRepo.findByUsername(newLogin)).willReturn(Optional.of(new UserEntity()));

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userDetailsService, userValidator);

		myProfileGui.getLoginTextField().setValue(newLogin);
		myProfileGui.getEmailTextField().setValue(newEmail);

		Assertions.assertTrue(myProfileGui.getLoginTextField().isEnabled());

		myProfileGui.getSaveUserButton().click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: This user already exists!")),
				times(1));
		verify(sessionUtils, never()).expireUserSessions(oldLogin, true);
		verify(sessionUtils, never()).expireUserSessions(newLogin, true);
		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class),
				eq(true));
	}

	private static Stream<Arguments> examplesOfInvalidUserUpdates() {

		return Stream.of(
				Arguments.of("hh", "aaa@abc.com"),
				Arguments.of(" hh ", "aaa@abc.com"),
				Arguments.of("hhhhhh", "aaaaaaaaaaaaaaaaaa")
		);
	}

	@Test
	void myProfileGuiTest_cancelDeletingUser_userIsNotDeleted() {

		UserEntity loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(userRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userDetailsService, userValidator);

		myProfileGui.getDeleteUserButton().click();
		myProfileGui.getCancelDeleteButton().click();

		verify(userRepo, never()).delete(any());
		verify(sessionUtils, never()).expireUserSessions(any(), eq(true));
		verify(imageRepo, never()).deleteUserFromImages(any());
	}

	@Test
	void myProfileGuiTest_confirmDeletingUser_userIsDeleted() {

		UserEntity loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(userRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userDetailsService, userValidator);

		myProfileGui.getDeleteUserButton().click();
		myProfileGui.getConfirmDeleteButton().click();

		verify(userRepo, times(1)).delete(loggedUser);
		verify(sessionUtils, times(1)).expireUserSessions(oldLogin, true);
		verify(imageRepo, times(1)).deleteUserFromImages(loggedUser);
	}

	@Test
	void myProfileGuiTest_logoutFromOtherDevices_sessionsAreExpired() {

		UserEntity loggedUser = UserDataProvider.prepareExampleGoodUser();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(userRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		MyProfileGui myProfileGui = new MyProfileGui(userService, userDetailsService, userValidator);

		myProfileGui.getExpireSessionsButton().click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("You have been successfully logged out from other devices")),
				times(1));
		verify(sessionUtils, times(1))
				.expireUserSessions(oldLogin, false);
	}
}
