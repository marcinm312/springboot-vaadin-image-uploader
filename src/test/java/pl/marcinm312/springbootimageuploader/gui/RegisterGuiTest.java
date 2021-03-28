package pl.marcinm312.springbootimageuploader.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.Token;
import pl.marcinm312.springbootimageuploader.repo.AppUserRepo;
import pl.marcinm312.springbootimageuploader.repo.TokenRepo;
import pl.marcinm312.springbootimageuploader.service.MailService;
import pl.marcinm312.springbootimageuploader.service.UserService;

import javax.mail.MessagingException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class RegisterGuiTest {

	@Mock
	AppUserRepo appUserRepo;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	MailService mailService;

	@Mock
	TokenRepo tokenRepo;

	@InjectMocks
	UserService userService;

	@BeforeEach
	void setUp() throws MessagingException {
		MockitoAnnotations.openMocks(this);
		given(passwordEncoder.encode(any(CharSequence.class))).willReturn("encodedPassword");
		doNothing().when(mailService).sendMail(isA(String.class), isA(String.class), isA(String.class), isA(boolean.class));
	}

	@Test
	void registerGuiTest_simpleCase_success() throws MessagingException {
		given(appUserRepo.findByUsername("hhhhhh")).willReturn(Optional.empty());
		given(tokenRepo.save(any(Token.class))).willReturn(new Token());
		RegisterGui registerGui = new RegisterGui(userService) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("User successfully registered", notificationText);
			}

			@Override
			protected String getUriString() {
				return "http://localhost:8080";
			}
		};
		registerGui.loginTextField.setValue("hhhhhh");
		registerGui.passwordField.setValue("hhhhhh");
		registerGui.confirmPasswordField.setValue("hhhhhh");
		registerGui.emailTextField.setValue("aaa@abc.com");
		boolean binderResult = registerGui.binder.isValid();
		Assertions.assertTrue(binderResult);
		registerGui.button.click();

		verify(mailService, times(1)).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
	}

	@Test
	void registerGuiTest_creatingUserWithTooShortLoginAndPassword_binderIsNotValid() throws MessagingException {
		RegisterGui registerGui = new RegisterGui(userService) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: Check the validation messages on the form", notificationText);
			}
		};
		registerGui.loginTextField.setValue("hh");
		registerGui.passwordField.setValue("hhhhh");
		registerGui.confirmPasswordField.setValue("hhhhh");
		registerGui.emailTextField.setValue("aaa@abc.com");
		boolean binderResult = registerGui.binder.isValid();
		Assertions.assertFalse(binderResult);
		registerGui.button.click();

		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
	}

	@Test
	void registerGuiTest_creatingUserThatAlreadyExists_notificationThatUserExists() throws MessagingException {
		given(appUserRepo.findByUsername("hhhhhh")).willReturn(Optional.of(new AppUser()));
		RegisterGui registerGui = new RegisterGui(userService) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: This user already exists!", notificationText);
			}
		};
		registerGui.loginTextField.setValue("hhhhhh");
		registerGui.passwordField.setValue("hhhhhh");
		registerGui.confirmPasswordField.setValue("hhhhhh");
		registerGui.emailTextField.setValue("aaa@abc.com");
		boolean binderResult = registerGui.binder.isValid();
		Assertions.assertTrue(binderResult);
		registerGui.button.click();

		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
	}

	@Test
	void registerGuiTest_creatingUserWithInvalidEmail_binderIsNotValid() throws MessagingException {
		given(appUserRepo.findByUsername("hhhhhh")).willReturn(Optional.empty());
		RegisterGui registerGui = new RegisterGui(userService) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: Check the validation messages on the form", notificationText);
			}
		};
		registerGui.loginTextField.setValue("hhhhhh");
		registerGui.passwordField.setValue("hhhhhh");
		registerGui.confirmPasswordField.setValue("hhhhhh");
		registerGui.emailTextField.setValue("aaaaaaaaaaaaaaaaaa");
		boolean binderResult = registerGui.binder.isValid();
		Assertions.assertFalse(binderResult);
		registerGui.button.click();

		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
	}

	@Test
	void registerGuiTest_creatingUserWithDifferentPasswords_notificationThatPasswordsMustBeTheSame() throws MessagingException {
		given(appUserRepo.findByUsername("hhhhhh")).willReturn(Optional.empty());
		given(tokenRepo.save(any(Token.class))).willReturn(new Token());
		RegisterGui registerGui = new RegisterGui(userService) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: The passwords in both fields must be the same!", notificationText);
			}

			@Override
			protected String getUriString() {
				return "http://localhost:8080";
			}
		};
		registerGui.loginTextField.setValue("hhhhhh");
		registerGui.passwordField.setValue("hhhhhh");
		registerGui.confirmPasswordField.setValue("aaaaaaaaaa");
		registerGui.emailTextField.setValue("aaa@abc.com");
		boolean binderResult = registerGui.binder.isValid();
		Assertions.assertTrue(binderResult);
		registerGui.button.click();

		verify(mailService, never()).sendMail(any(String.class), any(String.class), any(String.class), eq(true));
	}
}