package pl.marcinm312.springbootimageuploader.user.gui;

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
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;
import pl.marcinm312.springbootimageuploader.user.service.UserDetailsServiceImpl;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.user.validator.UserValidator;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class UpdatePasswordGuiTest {

	@Mock
	private UserRepo userRepo;

	@Mock
	private SessionUtils sessionUtils;

	@Spy
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;

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

		UserEntity loggedUser = UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
		String loggedUserUsername = loggedUser.getUsername();
		given(VaadinUtils.getAuthenticatedUserName()).willReturn(loggedUserUsername);
		given(userRepo.findByUsername(loggedUserUsername)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, userDetailsService, userValidator);

		updatePasswordGui.getCurrentPasswordField().setValue(currentPassword);
		updatePasswordGui.getPasswordField().setValue(password);
		updatePasswordGui.getConfirmPasswordField().setValue(confirmPassword);
		updatePasswordGui.getSaveUserButton().click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("User password successfully updated")),
				times(1));
		verify(sessionUtils, times(1)).expireUserSessions(loggedUserUsername, true);
		Assertions.assertEquals("", updatePasswordGui.getCurrentPasswordField().getValue());
		Assertions.assertEquals("", updatePasswordGui.getPasswordField().getValue());
		Assertions.assertEquals("", updatePasswordGui.getConfirmPasswordField().getValue());
	}

	@ParameterizedTest
	@MethodSource("examplesOfUnsuccessfulPasswordChanges")
	void updatePasswordGuiTest_invalidPasswordChange_passwordIsNotChanged(String currentPassword, String password,
																		  String confirmPassword, String errorMessage) {

		UserEntity loggedUser = UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
		String loggedUserUsername = loggedUser.getUsername();
		given(VaadinUtils.getAuthenticatedUserName()).willReturn(loggedUserUsername);
		given(userRepo.findByUsername(loggedUserUsername)).willReturn(Optional.of(loggedUser));

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		UpdatePasswordGui updatePasswordGui = new UpdatePasswordGui(userService, userDetailsService, userValidator);

		updatePasswordGui.getCurrentPasswordField().setValue(currentPassword);
		updatePasswordGui.getPasswordField().setValue(password);
		updatePasswordGui.getConfirmPasswordField().setValue(confirmPassword);
		updatePasswordGui.getSaveUserButton().click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq(errorMessage)), times(1));
		verify(sessionUtils, never()).expireUserSessions(loggedUserUsername, true);
		Assertions.assertEquals("", updatePasswordGui.getCurrentPasswordField().getValue());
		Assertions.assertEquals("", updatePasswordGui.getPasswordField().getValue());
		Assertions.assertEquals("", updatePasswordGui.getConfirmPasswordField().getValue());
	}

	private static Stream<Arguments> examplesOfUnsuccessfulPasswordChanges() {

		return Stream.of(
				Arguments.of("password", "hh2", "hh2",
						"Error: Check the validation messages on the form"),
				Arguments.of("password", "hhhhh2", "hhhhh3",
						"Error: The passwords in both fields must be the same!"),
				Arguments.of("password", "password", "password",
						"Error: The new password must be different from the previous one!"),
				Arguments.of("password2", "hhhhh2", "hhhhh2",
						"Error: The current password is incorrect")
		);
	}
}
