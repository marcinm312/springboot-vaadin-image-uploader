package pl.marcinm312.springbootimageuploader.gui;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.Token;
import pl.marcinm312.springbootimageuploader.repo.AppUserRepo;
import pl.marcinm312.springbootimageuploader.repo.TokenRepo;
import pl.marcinm312.springbootimageuploader.service.MailService;
import pl.marcinm312.springbootimageuploader.service.UserService;
import pl.marcinm312.springbootimageuploader.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.validator.UserValidator;

import javax.mail.MessagingException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class RegisterGuiTest {

	@Mock
	private AppUserRepo appUserRepo;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private MailService mailService;

	@Mock
	private TokenRepo tokenRepo;

	@InjectMocks
	private UserService userService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setUp() throws MessagingException {
		mockedVaadinUtils = mockStatic(VaadinUtils.class);
		MockitoAnnotations.openMocks(this);
		given(passwordEncoder.encode(any(CharSequence.class))).willReturn("encodedPassword");
		doNothing().when(mailService).sendMail(isA(String.class), isA(String.class), isA(String.class), isA(boolean.class));
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@Test
	void registerGuiTest_simpleCase_success() throws MessagingException {
		given(VaadinUtils.getUriString()).willReturn("http://localhost:8080");
		given(appUserRepo.findByUsername("hhhhhh")).willReturn(Optional.empty());
		given(tokenRepo.save(any(Token.class))).willReturn(new Token());

		UserValidator validator = new UserValidator(userService);
		RegisterGui registerGui = new RegisterGui(userService, validator);

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
	void registerGuiTest_creatingUserWithTooShortLoginAndPassword_binderIsNotValid() throws MessagingException {
		UserValidator validator = new UserValidator(userService);
		RegisterGui registerGui = new RegisterGui(userService, validator);

		registerGui.loginTextField.setValue("hh");
		registerGui.passwordField.setValue("hhhhh");
		registerGui.confirmPasswordField.setValue("hhhhh");
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
	void registerGuiTest_stringTrimmerTestInLogin_validationMessage() throws MessagingException {
		UserValidator validator = new UserValidator(userService);
		RegisterGui registerGui = new RegisterGui(userService, validator);

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
	void registerGuiTest_creatingUserThatAlreadyExists_notificationThatUserExists() throws MessagingException {
		given(appUserRepo.findByUsername("hhhhhh")).willReturn(Optional.of(new AppUser()));

		UserValidator validator = new UserValidator(userService);
		RegisterGui registerGui = new RegisterGui(userService, validator);

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
	void registerGuiTest_creatingUserWithInvalidEmail_binderIsNotValid() throws MessagingException {
		given(appUserRepo.findByUsername("hhhhhh")).willReturn(Optional.empty());

		UserValidator validator = new UserValidator(userService);
		RegisterGui registerGui = new RegisterGui(userService, validator);

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
	void registerGuiTest_creatingUserWithDifferentPasswords_notificationThatPasswordsMustBeTheSame() throws MessagingException {
		given(appUserRepo.findByUsername("hhhhhh")).willReturn(Optional.empty());
		given(tokenRepo.save(any(Token.class))).willReturn(new Token());

		UserValidator validator = new UserValidator(userService);
		RegisterGui registerGui = new RegisterGui(userService, validator);

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
