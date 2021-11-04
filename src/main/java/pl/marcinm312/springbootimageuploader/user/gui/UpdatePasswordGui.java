package pl.marcinm312.springbootimageuploader.user.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.user.model.AppUser;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.validator.UserValidator;

@Route("myprofile/updatePassword")
@StyleSheet("/css/style.css")
@PageTitle("Update password form")
public class UpdatePasswordGui extends VerticalLayout {

	BeanValidationBinder<AppUser> binder;
	HorizontalLayout horizontalMenu;
	Anchor logoutAnchor;
	Anchor myProfileAnchor;
	H1 h1;
	Paragraph paragraph;
	PasswordField currentPasswordField;
	PasswordField passwordField;
	PasswordField confirmPasswordField;
	Button saveUserButton;

	private final transient UserService userService;
	private final transient UserValidator userValidator;

	private static final String PARAGRAPH_VALUE = "After changing your password, you will need to log in again.";

	private final transient org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public UpdatePasswordGui(UserService userService, UserValidator userValidator) {

		this.userService = userService;
		this.userValidator = userValidator;

		binder = new BeanValidationBinder<>(AppUser.class);

		myProfileAnchor = new Anchor("../../myprofile/update", "Back to my profile");
		logoutAnchor = new Anchor("../../logout", "Log out");

		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, myProfileAnchor);

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

		saveUserButton = new Button("Save");
		saveUserButton.addClickListener(event -> updateUserPassword());
		add(horizontalMenu, h1, paragraph, currentPasswordField, passwordField, confirmPasswordField, saveUserButton);
	}

	private void updateUserPassword() {
		AppUser appUser = userService.getUserByUsername(VaadinUtils.getAuthenticatedUserName());
		log.info("Old user = {}", appUser);
		String validationError = userValidator.validateUserPasswordUpdate(appUser, currentPasswordField.getValue(), passwordField.getValue(), confirmPasswordField.getValue());
		if (validationError == null) {
			appUser.setPassword(passwordField.getValue());
			binder.setBean(appUser);
			binder.validate();
			if (binder.isValid()) {
				userService.updateUserPassword(appUser);
				VaadinUtils.showNotification("User password successfully updated");
			} else {
				clearPasswordFieldsValues();
				VaadinUtils.showNotification("Error: Check the validation messages on the form");
			}
		} else {
			clearPasswordFieldsValues();
			VaadinUtils.showNotification(validationError);
		}
	}

	private void clearPasswordFieldsValues() {
		currentPasswordField.setValue("");
		passwordField.setValue("");
		confirmPasswordField.setValue("");
	}
}
