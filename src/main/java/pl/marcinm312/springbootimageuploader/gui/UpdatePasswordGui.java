package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.service.UserService;

@Route("myprofile/updatePassword")
@StyleSheet("/css/style.css")
public class UpdatePasswordGui extends VerticalLayout {

	BeanValidationBinder<AppUser> binder;
	Anchor galleryAnchor;
	H1 h1;
	Paragraph paragraph;
	PasswordField passwordField;
	PasswordField confirmPasswordField;
	Button button;

	static final String PARAGRAPH_VALUE = "After changing your password, you will need to log in again.";

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public UpdatePasswordGui(UserService userService) {

		AppUser appUser = getAuthenticatedUser(userService);
		log.info("Old user = " + appUser.toString());

		binder = new BeanValidationBinder<>(AppUser.class);

		galleryAnchor = new Anchor("../../gallery", "Back to gallery");
		h1 = new H1("Update user password form");
		paragraph = new Paragraph(PARAGRAPH_VALUE);
		paragraph.setClassName("registration");

		passwordField = new PasswordField();
		passwordField.setLabel("Password");
		passwordField.setRevealButtonVisible(false);
		binder.forField(passwordField).bind("password");

		confirmPasswordField = new PasswordField();
		confirmPasswordField.setLabel("Confirm password");
		confirmPasswordField.setRevealButtonVisible(false);
		confirmPasswordField.setRequired(true);

		button = new Button("Save");
		button.addClickListener(event -> updateUser(userService, appUser));
		add(galleryAnchor, h1, paragraph, passwordField, confirmPasswordField, button);
	}

	protected AppUser getAuthenticatedUser(UserService userService) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userService.getUserByAuthentication(authentication);
	}

	private void updateUser(UserService userService, AppUser appUser) {
		appUser.setPassword(passwordField.getValue());
		binder.setBean(appUser);
		binder.validate();
		if (binder.isValid()) {
			if (passwordField.getValue().equals(confirmPasswordField.getValue())) {
				userService.updateUserPassword(appUser);
				showNotification("User password successfully updated");
			} else {
				showNotification("Error: The passwords in both fields must be the same!");
			}
		} else {
			showNotification("Error: Check the validation messages on the form");
		}
	}

	protected void showNotification(String notificationText) {
		Notification.show(notificationText, 5000, Notification.Position.MIDDLE);
	}
}
