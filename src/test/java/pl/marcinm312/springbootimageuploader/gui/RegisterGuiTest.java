package pl.marcinm312.springbootimageuploader.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.repo.AppUserRepo;
import pl.marcinm312.springbootimageuploader.service.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


class RegisterGuiTest {

	@Mock
	AppUserRepo appUserRepo;

	@Mock
	PasswordEncoder passwordEncoder;

	@InjectMocks
	UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		given(passwordEncoder.encode(any(CharSequence.class))).willReturn("encodedPassword");
	}

	@Test
	void guiTest_simpleCase_success() {
		given(appUserRepo.findByUsername("hhhhhh")).willReturn(Optional.empty());
		RegisterGui registerGui = new RegisterGui(userService) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("User successfully registered", notificationText);
			}
		};
		registerGui.loginTextField.setValue("hhhhhh");
		registerGui.passwordField.setValue("hhhhhh");
		boolean binderResult = registerGui.binder.isValid();
		Assertions.assertTrue(binderResult);
		registerGui.button.click();
	}

	@Test
	void guiTest_tooShortLoginAndPassword_binderIsNotValid() {
		RegisterGui registerGui = new RegisterGui(userService) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: Check the validation messages on the form", notificationText);
			}
		};
		registerGui.loginTextField.setValue("hh");
		registerGui.passwordField.setValue("hhhhh");
		boolean binderResult = registerGui.binder.isValid();
		Assertions.assertFalse(binderResult);
	}

	@Test
	void guiTest_creatingUserThatAlreadyExists_notificationThatUserExists() {
		given(appUserRepo.findByUsername("hhhhhh")).willReturn(Optional.of(new AppUser()));
		RegisterGui registerGui = new RegisterGui(userService) {
			@Override
			protected void showNotification(String notificationText) {
				Assertions.assertEquals("Error: This user already exists!", notificationText);
			}
		};
		registerGui.loginTextField.setValue("hhhhhh");
		registerGui.passwordField.setValue("hhhhhh");
		boolean binderResult = registerGui.binder.isValid();
		Assertions.assertTrue(binderResult);
		registerGui.button.click();
	}
}