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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.model.dto.UserDataUpdate;
import pl.marcinm312.springbootimageuploader.user.service.UserDetailsServiceImpl;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.user.validator.UserValidator;

@Route("myprofile/update")
@StyleSheet("/css/style.css")
@PageTitle("Update profile form")
public class MyProfileGui extends VerticalLayout {

	private final BeanValidationBinder<UserDataUpdate> binder;
	HorizontalLayout horizontalMenu;
	Anchor galleryAnchor;
	Anchor updatePasswordAnchor;
	Anchor logoutAnchor;
	H1 h1;
	Paragraph paragraph;
	Paragraph paragraph2;
	TextField loginTextField;
	EmailField emailTextField;
	Button saveUserButton;
	Button deleteUserButton;

	Dialog deleteDialog;
	Button confirmDeleteButton;
	Button cancelDeleteButton;

	Button expireSessionsButton;

	private final transient UserService userService;
	private final transient UserValidator userValidator;

	private static final String PARAGRAPH_VALUE = "After changing your login, you will need to log in again.";
	private static final String PARAGRAPH_VALUE_2 = "After changing your e-mail address, " +
			"you will receive an e-mail to the new e-mail address. " +
			"The change of e-mail will take place only after clicking on the link in the received e-mail.";

	@Autowired
	public MyProfileGui(UserService userService, UserDetailsServiceImpl userDetailsService, UserValidator userValidator) {

		this.userService = userService;
		this.userValidator = userValidator;

		UserEntity loggedUser = (UserEntity) userDetailsService.loadUserByUsername(VaadinUtils.getAuthenticatedUserName());

		deleteDialog = prepareDeleteDialog(loggedUser);

		binder = new BeanValidationBinder<>(UserDataUpdate.class);

		prepareHorizontalMenu();

		h1 = new H1("Update profile form");
		paragraph = new Paragraph(PARAGRAPH_VALUE);
		paragraph.setClassName("registration");
		paragraph2 = new Paragraph(PARAGRAPH_VALUE_2);
		paragraph2.setClassName("registration");

		prepareUpdateProfileForm(loggedUser);

		expireSessionsButton = new Button("Log me out from other devices");
		expireSessionsButton.addClickListener(event -> expireSessions(loggedUser));

		deleteUserButton = new Button("Delete my account");
		deleteUserButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
		deleteUserButton.addClickListener(event -> deleteDialog.open());

		add(horizontalMenu, h1, paragraph, paragraph2,loginTextField, emailTextField, saveUserButton,
				expireSessionsButton, deleteUserButton);
	}

	private void prepareUpdateProfileForm(UserEntity loggedUser) {

		loginTextField = new TextField();
		loginTextField.setLabel("Login");
		loginTextField.setValue(loggedUser.getUsername());
		if ("admin".equals(loggedUser.getUsername())) {
			loginTextField.setEnabled(false);
		}
		binder.forField(loginTextField).bind("username");

		emailTextField = new EmailField();
		emailTextField.setLabel("Email");
		if (loggedUser.getEmail() != null) {
			emailTextField.setValue(loggedUser.getEmail());
		}
		binder.forField(emailTextField).bind("email");

		saveUserButton = new Button("Save");
		saveUserButton.setClassName("updateprofile");
		saveUserButton.addClickListener(event -> updateUser(loggedUser));
	}

	private void prepareHorizontalMenu() {

		logoutAnchor = new Anchor("../../logout", "Log out");
		galleryAnchor = new Anchor("../../gallery", "Back to gallery");
		updatePasswordAnchor = new Anchor("../../myprofile/updatePassword", "Update my password");

		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, galleryAnchor, updatePasswordAnchor);
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

	private void updateUser(UserEntity loggedUser) {

		String username = loginTextField.getValue().trim();
		String email = emailTextField.getValue().trim();
		UserDataUpdate userDataUpdate = new UserDataUpdate(username, email);
		binder.setBean(userDataUpdate);
		binder.validate();
		if (binder.isValid()) {
			String validationError = userValidator.validateUserDataUpdate(userDataUpdate, loggedUser);
			if (validationError == null) {
				userService.updateUserData(userDataUpdate, loggedUser);
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
