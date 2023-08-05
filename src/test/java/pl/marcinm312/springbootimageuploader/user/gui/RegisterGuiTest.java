package pl.marcinm312.springbootimageuploader.user.gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.marcinm312.springbootimageuploader.shared.mail.MailService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.model.ActivationTokenEntity;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.repository.TokenRepo;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.user.validator.UserValidator;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
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
	private TokenRepo tokenRepo;

	@InjectMocks
	private UserService userService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setUp() {
		mockedVaadinUtils = mockStatic(VaadinUtils.class);
		MockitoAnnotations.openMocks(this);
		doNothing().when(mailService).sendMail(isA(String.class), isA(String.class), isA(String.class), isA(boolean.class));
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@Test
	void registerGuiTest_simpleCase_success() {
		given(VaadinUtils.getUriString()).willReturn("http://localhost:8080");
		given(userRepo.findByUsername("hhhhhh")).willReturn(Optional.empty());
		given(tokenRepo.save(any(ActivationTokenEntity.class))).willReturn(new ActivationTokenEntity());

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		RegisterGui registerGui = new RegisterGui(userService, userValidator);

		registerGui.loginTextField.setValue("hhhhhh");
		registerGui.passwordField.setValue("hhhhhh");
		registerGui.confirmPasswordField.setValue("hhhhhh");
		registerGui.emailTextField.setValue("aaa@abc.com");
		boolean binderResult = registerGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		registerGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("User successfully registered")),
				times(1));
		verify(mailService, times(1)).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
	}

	@Test
	void registerGuiTest_creatingUserWithTooShortLoginAndPassword_binderIsNotValid() {
		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		RegisterGui registerGui = new RegisterGui(userService, userValidator);

		registerGui.loginTextField.setValue("hh");
		registerGui.passwordField.setValue("hhhh");
		registerGui.confirmPasswordField.setValue("hhhh");
		registerGui.emailTextField.setValue("aaa@abc.com");
		boolean binderResult = registerGui.binder.isValid();

		Assertions.assertFalse(binderResult);

		registerGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: Check the validation messages on the form")),
				times(1));
		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
		Assertions.assertEquals("", registerGui.passwordField.getValue());
		Assertions.assertEquals("", registerGui.confirmPasswordField.getValue());
	}

	@Test
	void registerGuiTest_stringTrimmerTestInLogin_validationMessage() {
		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		RegisterGui registerGui = new RegisterGui(userService, userValidator);

		registerGui.loginTextField.setValue(" hh ");
		registerGui.passwordField.setValue("hhhhhhhh");
		registerGui.confirmPasswordField.setValue("hhhhhhhh");
		registerGui.emailTextField.setValue("aaa@abc.com");
		boolean binderResult = registerGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		registerGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: Check the validation messages on the form")),
				times(1));
		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
		Assertions.assertEquals("", registerGui.passwordField.getValue());
		Assertions.assertEquals("", registerGui.confirmPasswordField.getValue());
	}

	@Test
	void registerGuiTest_creatingUserThatAlreadyExists_notificationThatUserExists() {
		given(userRepo.findByUsername("hhhhhh")).willReturn(Optional.of(new UserEntity()));

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		RegisterGui registerGui = new RegisterGui(userService, userValidator);

		registerGui.loginTextField.setValue("hhhhhh");
		registerGui.passwordField.setValue("hhhhhh");
		registerGui.confirmPasswordField.setValue("hhhhhh");
		registerGui.emailTextField.setValue("aaa@abc.com");
		boolean binderResult = registerGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		registerGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: This user already exists!")),
				times(1));
		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
		Assertions.assertEquals("", registerGui.passwordField.getValue());
		Assertions.assertEquals("", registerGui.confirmPasswordField.getValue());
	}

	@Test
	void registerGuiTest_creatingUserWithInvalidEmail_binderIsNotValid() {
		given(userRepo.findByUsername("hhhhhh")).willReturn(Optional.empty());

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		RegisterGui registerGui = new RegisterGui(userService, userValidator);

		registerGui.loginTextField.setValue("hhhhhh");
		registerGui.passwordField.setValue("hhhhhh");
		registerGui.confirmPasswordField.setValue("hhhhhh");
		registerGui.emailTextField.setValue("aaaaaaaaaaaaaaaaaa");
		boolean binderResult = registerGui.binder.isValid();

		Assertions.assertFalse(binderResult);

		registerGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: Check the validation messages on the form")),
				times(1));
		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
		Assertions.assertEquals("", registerGui.passwordField.getValue());
		Assertions.assertEquals("", registerGui.confirmPasswordField.getValue());
	}

	@Test
	void registerGuiTest_creatingUserWithDifferentPasswords_notificationThatPasswordsMustBeTheSame() {
		given(userRepo.findByUsername("hhhhhh")).willReturn(Optional.empty());
		given(tokenRepo.save(any(ActivationTokenEntity.class))).willReturn(new ActivationTokenEntity());

		UserValidator userValidator = new UserValidator(userService, passwordEncoder);
		RegisterGui registerGui = new RegisterGui(userService, userValidator);

		registerGui.loginTextField.setValue("hhhhhh");
		registerGui.passwordField.setValue("hhhhhh");
		registerGui.confirmPasswordField.setValue("aaaaaaaaaa");
		registerGui.emailTextField.setValue("aaa@abc.com");
		boolean binderResult = registerGui.binder.isValid();

		Assertions.assertTrue(binderResult);

		registerGui.saveUserButton.click();

		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq("Error: The passwords in both fields must be the same!")),
				times(1));
		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
		Assertions.assertEquals("", registerGui.passwordField.getValue());
		Assertions.assertEquals("", registerGui.confirmPasswordField.getValue());
	}
}
