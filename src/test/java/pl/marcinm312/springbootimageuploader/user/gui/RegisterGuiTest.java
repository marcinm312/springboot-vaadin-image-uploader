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
import pl.marcinm312.springbootimageuploader.shared.mail.MailService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.model.ActivationTokenEntity;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.repository.ActivationTokenRepo;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.user.validator.UserValidator;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class RegisterGuiTest {

	@Mock
	private UserRepo userRepo;

	@Spy
	private BCryptPasswordEncoder passwordEncoder;

	@Mock
	private MailService mailService;

	@Mock
	private ActivationTokenRepo activationTokenRepo;

	@InjectMocks
	private UserService userService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setUp() {

		mockedVaadinUtils = mockStatic(VaadinUtils.class);
		MockitoAnnotations.openMocks(this);

		given(VaadinUtils.getApplicationUrl()).willReturn("http://localhost:8080/");
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@Test
	void registerGuiTest_simpleCase_success() {

		given(userRepo.findByUsername("hhhhhh")).willReturn(Optional.empty());
		given(activationTokenRepo.save(any(ActivationTokenEntity.class))).willReturn(new ActivationTokenEntity());

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		RegisterGui registerGui = new RegisterGui(userService, userValidator);

		registerGui.getLoginTextField().setValue("hhhhhh");
		registerGui.getPasswordField().setValue("hhhhhh");
		registerGui.getConfirmPasswordField().setValue("hhhhhh");
		registerGui.getEmailTextField().setValue("aaa@abc.com");

		registerGui.getSaveUserButton().click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("User successfully registered")),
				times(1));
		verify(mailService, times(1)).sendMail(eq("aaa@abc.com"), any(String.class), any(String.class), eq(true));
	}

	@ParameterizedTest
	@MethodSource("examplesOfInvalidUsers")
	void registerGuiTest_invalidUser_userIsNotRegistered(String login, String password, String confirmPassword,
														 String email, UserEntity foundUserWithTheSameLogin,
														 String errorMessage) {

		given(userRepo.findByUsername(login)).willReturn(Optional.ofNullable(foundUserWithTheSameLogin));

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		RegisterGui registerGui = new RegisterGui(userService, userValidator);

		registerGui.getLoginTextField().setValue(login);
		registerGui.getPasswordField().setValue(password);
		registerGui.getConfirmPasswordField().setValue(confirmPassword);
		registerGui.getEmailTextField().setValue(email);

		registerGui.getSaveUserButton().click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq(errorMessage)),
				times(1));
		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
		Assertions.assertEquals("", registerGui.getPasswordField().getValue());
		Assertions.assertEquals("", registerGui.getConfirmPasswordField().getValue());
	}

	private static Stream<Arguments> examplesOfInvalidUsers() {

		return Stream.of(
				Arguments.of("hh", "hhhh", "hhhh", "aaa@abc.com", null,
						"Error: Check the validation messages on the form"),
				Arguments.of(" hh ", "hhhhhhhh", "hhhhhhhh", "aaa@abc.com", null,
						"Error: Check the validation messages on the form"),
				Arguments.of("hhhhhh", "hhhhhh", "hhhhhh", "aaa@abc.com", new UserEntity(),
						"Error: This user already exists!"),
				Arguments.of("hhhhhh", "hhhhhh", "hhhhhh", "aaaaaaaaaaaaaaaaaa", null,
						"Error: Check the validation messages on the form"),
				Arguments.of("hhhhhh", "hhhhhh", "aaaaaaaaaa", "aaa@abc.com", null,
						"Error: The passwords in both fields must be the same!")
		);
	}
}
