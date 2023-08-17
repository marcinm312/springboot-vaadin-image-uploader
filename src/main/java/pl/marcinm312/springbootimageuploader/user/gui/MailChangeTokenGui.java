package pl.marcinm312.springbootimageuploader.user.gui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.exception.TokenNotFoundException;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.service.UserService;

@Getter(AccessLevel.PACKAGE)
@Slf4j
@Route("myprofile/update/confirm")
@StyleSheet("/css/style.css")
@PageTitle("Mail change confirmation")
public class MailChangeTokenGui extends VerticalLayout {

	private final Anchor logoutAnchor;
	private H1 h1;
	private Paragraph paragraph;
	private final Anchor galleryAnchor;

	@Autowired
	public MailChangeTokenGui(UserService userService) {

		String tokenValue = null;
		logoutAnchor = new Anchor("../../../logout", "Log out");
		galleryAnchor = new Anchor("../../../gallery", "Back to gallery");

		try {
			tokenValue = VaadinUtils.getParamValueFromCurrentUrlQuery("value");
		} catch (Exception exc) {
			String errorMessage = String.format("Error retrieving the value of the token: %s", exc.getMessage());
			log.error(errorMessage, exc);
		}

		UserEntity loggedUser = VaadinUtils.getCurrentUser();

		if(tokenValue != null) {
			try {
				UserEntity updatedUser = userService.confirmMailChange(tokenValue, loggedUser);
				h1 = new H1("Your email has been changed");
				paragraph = new Paragraph("Your new email address: " + updatedUser.getEmail());
				add(logoutAnchor, h1, paragraph, galleryAnchor);
			} catch (TokenNotFoundException exc) {
				log.error("Error while confirming mail change: {}", exc.getMessage());
				h1 = new H1(exc.getMessage());
				add(logoutAnchor, h1, galleryAnchor);
			}
		} else {
			h1 = new H1("Error getting token value");
			add(logoutAnchor, h1, galleryAnchor);
		}
	}
}
