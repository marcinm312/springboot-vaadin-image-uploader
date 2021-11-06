package pl.marcinm312.springbootimageuploader.user.gui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.validator.UserValidator;

@Route("myprofile/update")
@StyleSheet("/css/style.css")
@PageTitle("Update profile form")
public class MyProfileGui extends VerticalLayout {

	BeanValidationBinder<UserEntity> binder;
	HorizontalLayout horizontalMenu;
	Anchor galleryAnchor;
	Anchor updatePasswordAnchor;
	Anchor logoutAnchor;
	H1 h1;
	Paragraph paragraph;
	TextField loginTextField;
	TextField emailTextField;
	Button saveUserButton;
	Button deleteUserButton;

	Dialog deleteDialog;
	Button confirmDeleteButton;
	Button cancelDeleteButton;

	Button expireSessionsButton;

	private final transient UserService userService;
	private final transient UserValidator userValidator;

	private static final String PARAGRAPH_VALUE = "After changing your login, you will need to log in again.";

	private final transient org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public MyProfileGui(UserService userService, UserValidator userValidator) {

		this.userService = userService;
		this.userValidator = userValidator;

		UserEntity user = userService.getUserByUsername(VaadinUtils.getAuthenticatedUserName());
		String oldLogin = user.getUsername();

		deleteDialog = prepareDeleteDialog(user);

		binder = new BeanValidationBinder<>(UserEntity.class);

		logoutAnchor = new Anchor("../../logout", "Log out");
		galleryAnchor = new Anchor("../../gallery", "Back to gallery");
		updatePasswordAnchor = new Anchor("../../myprofile/updatePassword", "Update my password");

		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, galleryAnchor, updatePasswordAnchor);

		h1 = new H1("Update profile form");
		paragraph = new Paragraph(PARAGRAPH_VALUE);
		paragraph.setClassName("registration");

		loginTextField = new TextField();
		loginTextField.setLabel("Login");
		loginTextField.setValue(user.getUsername());
		if ("administrator".equals(user.getUsername())) {
			loginTextField.setEnabled(false);
		}
		binder.forField(loginTextField).bind("username");

		emailTextField = new TextField();
		emailTextField.setLabel("Email");
		if (user.getEmail() != null) {
			emailTextField.setValue(user.getEmail());
		}
		binder.forField(emailTextField).bind("email");

		saveUserButton = new Button("Save");
		saveUserButton.setClassName("updateprofile");
		saveUserButton.addClickListener(event -> updateUser(oldLogin, user));

		expireSessionsButton = new Button("Log me out from other devices");
		expireSessionsButton.addClickListener(event -> expireSessions(user));

		deleteUserButton = new Button("Delete my account");
		deleteUserButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
		deleteUserButton.addClickListener(event -> deleteDialog.open());

		add(horizontalMenu, h1, paragraph, loginTextField, emailTextField, saveUserButton, expireSessionsButton, deleteUserButton);
	}

	private Dialog prepareDeleteDialog(UserEntity user) {
		Dialog dialogWindow = new Dialog();
		Text confirmText = new Text("Are you sure you want to delete your user account?");
		confirmDeleteButton = new Button("Confirm", deleteEvent -> deleteUser(user));
		cancelDeleteButton = new Button("Cancel", cancelEvent -> deleteDialog.close());
		dialogWindow.add(new VerticalLayout(confirmText, new HorizontalLayout(confirmDeleteButton, cancelDeleteButton)));
		return dialogWindow;
	}

	private void expireSessions(UserEntity user) {
		userService.expireOtherUserSessions(user);
		VaadinUtils.showNotification("You have been successfully logged out from other devices");
	}

	private void updateUser(String oldLogin, UserEntity user) {
		log.info("Old user = {}", user);
		user.setUsername(loginTextField.getValue().trim());
		user.setEmail(emailTextField.getValue().trim());
		binder.setBean(user);
		binder.validate();
		if (binder.isValid()) {
			String validationError = userValidator.validateUserDataUpdate(user, oldLogin);
			if (validationError == null) {
				userService.updateUserData(oldLogin, user);
				VaadinUtils.showNotification("User successfully updated");
			} else {
				VaadinUtils.showNotification(validationError);
			}
		} else {
			VaadinUtils.showNotification("Error: Check the validation messages on the form");
		}
	}

	private void deleteUser(UserEntity user) {
		userService.deleteUser(user);
	}
}
