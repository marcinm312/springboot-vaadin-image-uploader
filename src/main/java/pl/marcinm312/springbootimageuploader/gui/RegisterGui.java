package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.service.UserService;

@Route("register")
public class RegisterGui extends VerticalLayout {

	BeanValidationBinder<AppUser> binder;
	Anchor mainPageAnchor;
	H1 h1;
	TextField loginTextField;
	PasswordField passwordField;
	Button button;

	@Autowired
	public RegisterGui(UserService userService) {
		binder = new BeanValidationBinder<>(AppUser.class);

		mainPageAnchor = new Anchor("..", "Back to main page");
		h1 = new H1("Registration form");

		loginTextField = new TextField();
		loginTextField.setLabel("Login");
		binder.forField(loginTextField).bind("username");

		passwordField = new PasswordField();
		passwordField.setLabel("Password");
		passwordField.setRevealButtonVisible(false);
		binder.forField(passwordField).bind("password");

		button = new Button("Register!");
		button.addClickListener(event -> {
			String username = loginTextField.getValue();
			String password = passwordField.getValue();
			AppUser appUser = new AppUser(username, password, "ROLE_USER");
			binder.setBean(appUser);
			binder.validate();
			if (binder.isValid()) {
				if (!userService.getUserByUsername(username).isPresent()) {
					userService.createUser(appUser);
					showNotification("User successfully registered");
				} else {
					showNotification("Error: This user already exists!");
				}
			} else {
				showNotification("Error: Check the validation messages on the form");
			}
		});
		add(mainPageAnchor, h1, loginTextField, passwordField, button);
	}

	protected void showNotification(String notificationText) {
		Notification.show(notificationText, 5000, Notification.Position.MIDDLE);
	}
}
