package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.service.UserService;
import pl.marcinm312.springbootimageuploader.validator.UserValidator;

@Route("myprofile/update")
@StyleSheet("/css/style.css")
public class MyProfileGui extends VerticalLayout {

	BeanValidationBinder<AppUser> binder;
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

		AppUser appUser = getAuthenticatedUser();
		log.info("Old user = {}", appUser);
		String oldLogin = appUser.getUsername();

		deleteDialog = prepareDeleteDialog(appUser);

		binder = new BeanValidationBinder<>(AppUser.class);

		logoutAnchor = new Anchor("../../logout", "Log out");
		logoutAnchor.setTarget("_top");

		galleryAnchor = new Anchor("../../gallery", "Back to gallery");
		galleryAnchor.setTarget("_top");

		updatePasswordAnchor = new Anchor("../../myprofile/updatePassword", "Update my password");
		updatePasswordAnchor.setTarget("_top");

		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, galleryAnchor, updatePasswordAnchor);

		h1 = new H1("Update profile form");
		paragraph = new Paragraph(PARAGRAPH_VALUE);
		paragraph.setClassName("registration");

		loginTextField = new TextField();
		loginTextField.setLabel("Login");
		loginTextField.setValue(appUser.getUsername());
		if ("administrator".equals(appUser.getUsername())) {
			loginTextField.setEnabled(false);
		}
		binder.forField(loginTextField).bind("username");

		emailTextField = new TextField();
		emailTextField.setLabel("Email");
		if (appUser.getEmail() != null) {
			emailTextField.setValue(appUser.getEmail());
		}
		binder.forField(emailTextField).bind("email");

		saveUserButton = new Button("Save");
		saveUserButton.setClassName("updateprofile");
		saveUserButton.addClickListener(event -> updateUser(oldLogin, appUser));

		expireSessionsButton = new Button("Log me out from other devices");
		expireSessionsButton.addClickListener(event -> expireSessions(appUser));

		deleteUserButton = new Button("Delete my account");
		deleteUserButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
		deleteUserButton.addClickListener(event -> deleteDialog.open());

		add(horizontalMenu, h1, paragraph, loginTextField, emailTextField, saveUserButton, expireSessionsButton, deleteUserButton);
	}

	private Dialog prepareDeleteDialog(AppUser appUser) {
		Dialog dialogWindow = new Dialog();
		Text confirmText = new Text("Are you sure you want to delete your user account?");
		confirmDeleteButton = new Button("Confirm", deleteEvent -> deleteUser(appUser));
		cancelDeleteButton = new Button("Cancel", cancelEvent -> deleteDialog.close());
		dialogWindow.add(new VerticalLayout(confirmText, new HorizontalLayout(confirmDeleteButton, cancelDeleteButton)));
		return dialogWindow;
	}

	private void expireSessions(AppUser appUser) {
		userService.expireOtherUserSessions(appUser);
		showNotification("You have been successfully logged out from other devices");
	}

	AppUser getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userService.getUserByAuthentication(authentication);
	}

	private void updateUser(String oldLogin, AppUser appUser) {
		appUser.setUsername(loginTextField.getValue());
		appUser.setEmail(emailTextField.getValue());
		binder.setBean(appUser);
		binder.validate();
		if (binder.isValid()) {
			String validationError = userValidator.validateUserDataUpdate(appUser, oldLogin);
			if (validationError == null) {
				userService.updateUserData(oldLogin, appUser);
				showNotification("User successfully updated");
			} else {
				showNotification(validationError);
			}
		} else {
			showNotification("Error: Check the validation messages on the form");
		}
	}

	private void deleteUser(AppUser appUser) {
		userService.deleteUser(appUser);
	}

	void showNotification(String notificationText) {
		Notification.show(notificationText, 5000, Notification.Position.MIDDLE);
	}
}
