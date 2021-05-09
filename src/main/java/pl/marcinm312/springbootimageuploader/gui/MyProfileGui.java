package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
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

@Route("myprofile/update")
@StyleSheet("/css/style.css")
public class MyProfileGui extends VerticalLayout {

	BeanValidationBinder<AppUser> binder;
	Anchor galleryAnchor;
	H1 h1;
	Paragraph paragraph;
	TextField loginTextField;
	TextField emailTextField;
	Button button;

	static final String PARAGRAPH_VALUE = "After changing your login, you will need to log in again.";

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public MyProfileGui(UserService userService) {

		AppUser appUser = getAuthenticatedUser(userService);
		log.info("Old user = " + appUser.toString());
		String oldLogin = appUser.getUsername();

		binder = new BeanValidationBinder<>(AppUser.class);

		galleryAnchor = new Anchor("../../gallery", "Back to gallery");
		galleryAnchor.setTarget("_top");
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

		button = new Button("Save");
		button.addClickListener(event -> updateUser(userService, oldLogin, appUser));
		add(galleryAnchor, h1, paragraph, loginTextField, emailTextField, button);
	}

	protected AppUser getAuthenticatedUser(UserService userService) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userService.getUserByAuthentication(authentication);
	}

	private void updateUser(UserService userService, String oldLogin, AppUser appUser) {
		appUser.setUsername(loginTextField.getValue());
		appUser.setEmail(emailTextField.getValue());
		binder.setBean(appUser);
		binder.validate();
		if (binder.isValid()) {
			if (!appUser.getUsername().equals(oldLogin)) {
				if (!userService.getUserByUsername(appUser.getUsername()).isPresent()) {
					userService.updateUserData(oldLogin, appUser);
					showNotification("User successfully updated");
				} else {
					showNotification("Error: This user already exists!");
				}
			} else {
				userService.updateUserData(oldLogin, appUser);
				showNotification("User successfully updated");
			}
		} else {
			showNotification("Error: Check the validation messages on the form");
		}
	}

	protected void showNotification(String notificationText) {
		Notification.show(notificationText, 5000, Notification.Position.MIDDLE);
	}
}
