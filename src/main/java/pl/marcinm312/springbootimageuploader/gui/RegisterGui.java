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

	@Autowired
	public RegisterGui(UserService userService) {
		BeanValidationBinder<AppUser> binder = new BeanValidationBinder<>(AppUser.class);

		Anchor mainPageAnchor = new Anchor("..", "Back to main page");
		H1 h1 = new H1("Registration form");

		TextField loginTextField = new TextField();
		loginTextField.setLabel("Login");
		binder.forField(loginTextField).bind("username");

		PasswordField passwordField = new PasswordField();
		passwordField.setLabel("Password");
		passwordField.setRevealButtonVisible(false);
		binder.forField(passwordField).bind("password");

		Button button = new Button("Register!");
		button.addClickListener(event -> {
			String username = loginTextField.getValue();
			String password = passwordField.getValue();
			AppUser appUser = new AppUser(username, password, "ROLE_USER");
			binder.setBean(appUser);
			binder.validate();
			if (binder.isValid()) {
				if (!userService.getUserByUsername(username).isPresent()) {
					userService.createUser(appUser);
					Notification.show("User successfully registered", 5000, Notification.Position.MIDDLE);
				} else {
					Notification.show("Error: This user already exists!", 5000, Notification.Position.MIDDLE);
				}
			} else {
				Notification.show("Error: Check the validation messages on the form", 5000, Notification.Position.MIDDLE);
			}
		});
		add(mainPageAnchor, h1, loginTextField, passwordField, button);
	}
}
