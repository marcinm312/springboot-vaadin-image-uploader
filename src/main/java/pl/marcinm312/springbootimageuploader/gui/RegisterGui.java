package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.service.UserService;

@Route("register")
public class RegisterGui extends VerticalLayout {

	BeanValidationBinder<AppUser> binder;
	Anchor mainPageAnchor;
	H1 h1;
	Paragraph paragraph;
	TextField loginTextField;
	PasswordField passwordField;
	PasswordField confirmPasswordField;
	TextField emailTextField;
	Button button;

	static final String PARAGRAPH_VALUE = "After registration, you will receive an email that will enable you to activate your account. It is not possible to log in without activating the account. ";

	@Autowired
	public RegisterGui(UserService userService) {
		binder = new BeanValidationBinder<>(AppUser.class);

		mainPageAnchor = new Anchor("..", "Back to main page");
		h1 = new H1("Registration form");
		paragraph = new Paragraph(PARAGRAPH_VALUE);

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

		emailTextField = new TextField();
		emailTextField.setLabel("Email");
		binder.forField(emailTextField).bind("email");

		button = new Button("Register!");
		button.addClickListener(event -> createUser(userService));
		add(mainPageAnchor, h1, paragraph, loginTextField, passwordField, confirmPasswordField, emailTextField, button);
	}

	private void createUser(UserService userService) {
		String username = loginTextField.getValue();
		String password = passwordField.getValue();
		String email = emailTextField.getValue();
		AppUser appUser = new AppUser(username, password, "ROLE_USER", email);
		binder.setBean(appUser);
		binder.validate();
		if (binder.isValid()) {
			if (!userService.getUserByUsername(username).isPresent()) {
				if (passwordField.getValue().equals(confirmPasswordField.getValue())) {
					String uriString = getUriString();
					userService.createUser(appUser, false, uriString);
					showNotification("User successfully registered");
				} else {
					showNotification("Error: The passwords in both fields must be the same!");
				}
			} else {
				showNotification("Error: This user already exists!");
			}
		} else {
			showNotification("Error: Check the validation messages on the form");
		}
	}

	protected String getUriString() {
		VaadinServletRequest request = (VaadinServletRequest) VaadinService.getCurrentRequest();
		return request.getRequestURL().toString().replace("/register", "");
	}

	protected void showNotification(String notificationText) {
		Notification.show(notificationText, 5000, Notification.Position.MIDDLE);
	}
}
