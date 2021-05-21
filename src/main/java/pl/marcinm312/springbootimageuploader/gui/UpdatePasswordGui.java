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
import pl.marcinm312.springbootimageuploader.validator.UserValidator;

@Route("myprofile/updatePassword")
@StyleSheet("/css/style.css")
public class UpdatePasswordGui extends VerticalLayout {

	BeanValidationBinder<AppUser> binder;
	Anchor galleryAnchor;
	H1 h1;
	Paragraph paragraph;
	PasswordField currentPasswordField;
	PasswordField passwordField;
	PasswordField confirmPasswordField;
	Button button;

	static final String PARAGRAPH_VALUE = "After changing your password, you will need to log in again.";

	protected final transient org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public UpdatePasswordGui(UserService userService, UserValidator userValidator) {

		binder = new BeanValidationBinder<>(AppUser.class);

		galleryAnchor = new Anchor("../../gallery", "Back to gallery");
		galleryAnchor.setTarget("_top");
		h1 = new H1("Update password form");
		paragraph = new Paragraph(PARAGRAPH_VALUE);
		paragraph.setClassName("registration");

		currentPasswordField = new PasswordField();
		currentPasswordField.setLabel("Current password");
		currentPasswordField.setRevealButtonVisible(false);
		currentPasswordField.setRequired(true);

		passwordField = new PasswordField();
		passwordField.setLabel("Password");
		passwordField.setRevealButtonVisible(false);
		binder.forField(passwordField).bind("password");

		confirmPasswordField = new PasswordField();
		confirmPasswordField.setLabel("Confirm password");
		confirmPasswordField.setRevealButtonVisible(false);
		confirmPasswordField.setRequired(true);

		button = new Button("Save");
		button.addClickListener(event -> updateUserPassword(userService, userValidator));
		add(galleryAnchor, h1, paragraph, currentPasswordField, passwordField, confirmPasswordField, button);
	}

	protected AppUser getAuthenticatedUser(UserService userService) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userService.getUserByAuthentication(authentication);
	}

	private void updateUserPassword(UserService userService, UserValidator userValidator) {
		AppUser appUser = getAuthenticatedUser(userService);
		log.info("Old user = {}", appUser);
		String validationError = userValidator.validateUserPasswordUpdate(appUser, currentPasswordField.getValue(), passwordField.getValue(), confirmPasswordField.getValue());
		if (validationError == null) {
			appUser.setPassword(passwordField.getValue());
			binder.setBean(appUser);
			binder.validate();
			if (binder.isValid()) {
				userService.updateUserPassword(appUser);
				showNotification("User password successfully updated");
			} else {
				showNotification("Error: Check the validation messages on the form");
			}
		} else {
			showNotification(validationError);
		}
	}

	protected void showNotification(String notificationText) {
		Notification.show(notificationText, 5000, Notification.Position.MIDDLE);
	}
}
