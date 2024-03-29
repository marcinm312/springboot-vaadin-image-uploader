package pl.marcinm312.springbootimageuploader.user.gui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.exception.TokenNotFoundException;
import pl.marcinm312.springbootimageuploader.user.service.UserService;

@Getter(AccessLevel.PACKAGE)
@Slf4j
@Route("token")
@StyleSheet("/css/style.css")
@PageTitle("User activation")
public class ActivationTokenGui extends VerticalLayout {

	private H1 h1;
	private final Anchor anchor;

	@Autowired
	public ActivationTokenGui(UserService userService) {

		String tokenValue = null;
		anchor = new Anchor("..", "Back to main page");

		try {
			tokenValue = VaadinUtils.getParamValueFromCurrentUrlQuery("value");
		} catch (Exception exc) {
			String errorMessage = String.format("Error retrieving the value of the token: %s", exc.getMessage());
			log.error(errorMessage, exc);
		}

		if(tokenValue != null) {
			try {
				userService.activateUser(tokenValue);
				h1 = new H1("User activated");
			} catch (TokenNotFoundException exc) {
				log.error("Error while activating the user: {}", exc.getMessage());
				h1 = new H1(exc.getMessage());
			}
		} else {
			h1 = new H1("Error getting token value");
		}

		add(h1, anchor);
	}
}
