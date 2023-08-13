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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.model.dto.UserPasswordUpdate;
import pl.marcinm312.springbootimageuploader.user.service.UserDetailsServiceImpl;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.user.validator.UserValidator;

@Slf4j
@Route("myprofile/updatePassword")
@StyleSheet("/css/style.css")
@PageTitle("Update password form")
public class UpdatePasswordGui extends VerticalLayout {

	private final BeanValidationBinder<UserPasswordUpdate> binder;
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
	private final transient UserDetailsServiceImpl userDetailsService;
	private final transient UserValidator userValidator;

	private static final String PARAGRAPH_VALUE = "After changing your password, you will need to log in again.";

	@Autowired
	public UpdatePasswordGui(UserService userService, UserDetailsServiceImpl userDetailsService, UserValidator userValidator) {

		this.userService = userService;
		this.userDetailsService = userDetailsService;
		this.userValidator = userValidator;

		binder = new BeanValidationBinder<>(UserPasswordUpdate.class);

		prepareHorizontalMenu();

		h1 = new H1("Update password form");
		paragraph = new Paragraph(PARAGRAPH_VALUE);
		paragraph.setClassName("registration");

		prepareUpdatePasswordForm();

		add(horizontalMenu, h1, paragraph, currentPasswordField, passwordField, confirmPasswordField, saveUserButton);
	}

	private void prepareUpdatePasswordForm() {

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
	}

	private void prepareHorizontalMenu() {

		myProfileAnchor = new Anchor("../../myprofile/update", "Back to my profile");
		logoutAnchor = new Anchor("../../logout", "Log out");

		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, myProfileAnchor);
	}

	private void updateUserPassword() {

		UserEntity loggedUser = (UserEntity) userDetailsService.loadUserByUsername(VaadinUtils.getAuthenticatedUserName());
		String currentPassword = currentPasswordField.getValue();
		String password = passwordField.getValue();
		String confirmPassword = confirmPasswordField.getValue();
		UserPasswordUpdate userPasswordUpdate = new UserPasswordUpdate(currentPassword, password, confirmPassword);
		binder.setBean(userPasswordUpdate);
		binder.validate();
		if (binder.isValid()) {
			String validationError = userValidator.validateUserPasswordUpdate(userPasswordUpdate, loggedUser);
			if (validationError == null) {
				userService.updateUserPassword(userPasswordUpdate, loggedUser);
				VaadinUtils.showNotification("User password successfully updated");
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

		currentPasswordField.setValue("");
		passwordField.setValue("");
		confirmPasswordField.setValue("");
	}
}
