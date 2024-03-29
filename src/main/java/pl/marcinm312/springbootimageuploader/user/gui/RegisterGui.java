package pl.marcinm312.springbootimageuploader.user.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.model.dto.UserCreate;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.user.validator.UserValidator;

@Getter(AccessLevel.PACKAGE)
@Slf4j
@Route("register")
@StyleSheet("/css/style.css")
@PageTitle("Registration form")
public class RegisterGui extends VerticalLayout {

	private final BeanValidationBinder<UserCreate> binder;

	private final Anchor mainPageAnchor;
	private final H1 h1;
	private final Paragraph paragraph;
	private TextField loginTextField;
	private PasswordField passwordField;
	private PasswordField confirmPasswordField;
	private EmailField emailTextField;
	private Button saveUserButton;

	private final transient UserService userService;
	private final transient UserValidator userValidator;

	private static final String PARAGRAPH_VALUE = "After registration, you will receive an email that will enable you to activate your account. It is not possible to log in without activating the account. ";

	@Autowired
	public RegisterGui(UserService userService, UserValidator userValidator) {

		this.userService = userService;
		this.userValidator = userValidator;

		binder = new BeanValidationBinder<>(UserCreate.class);

		mainPageAnchor = new Anchor("..", "Back to main page");
		h1 = new H1("Registration form");
		paragraph = new Paragraph(PARAGRAPH_VALUE);
		paragraph.setClassName("registration");

		prepareRegistrationForm();

		add(mainPageAnchor, h1, paragraph, loginTextField, passwordField, confirmPasswordField, emailTextField, saveUserButton);
	}

	private void prepareRegistrationForm() {

		loginTextField = new TextField();
		loginTextField.setLabel("Login");
		binder.forField(loginTextField).bind("username");

		passwordField = new PasswordField();
		passwordField.setLabel("Password");
		passwordField.setRevealButtonVisible(false);
		binder.forField(passwordField).bind("password");

		confirmPasswordField = new PasswordField();
		confirmPasswordField.setLabel("Confirm password");
		confirmPasswordField.setRevealButtonVisible(false);
		confirmPasswordField.setRequired(true);

		emailTextField = new EmailField();
		emailTextField.setLabel("Email");
		binder.forField(emailTextField).bind("email");

		saveUserButton = new Button("Register!");
		saveUserButton.addClickListener(event -> createUser());
	}

	private void createUser() {

		String username = loginTextField.getValue().trim();
		String password = passwordField.getValue();
		String confirmPassword = confirmPasswordField.getValue();
		String email = emailTextField.getValue().trim();
		UserCreate userCreate = new UserCreate(username, password, confirmPassword, email);
		binder.setBean(userCreate);
		binder.validate();
		if (binder.isValid()) {
			String validationError = userValidator.validateUserRegistration(userCreate);
			if (validationError == null) {
				try {
					userService.createUser(userCreate);
					VaadinUtils.showNotification("User successfully registered");
				} catch (Exception e) {
					String errorMessage = String.format("An error occurred while registering the user: %s", e.getMessage());
					log.error(errorMessage, e);
					VaadinUtils.showNotification(errorMessage);
				}
			} else {
				clearPasswordFieldsValues();
				VaadinUtils.showNotification(validationError);
			}
		} else {
			clearPasswordFieldsValues();
			VaadinUtils.showNotification("Error: Check the validation messages on the form");
		}
	}

	private void clearPasswordFieldsValues() {
		passwordField.setValue("");
		confirmPasswordField.setValue("");
	}
}
