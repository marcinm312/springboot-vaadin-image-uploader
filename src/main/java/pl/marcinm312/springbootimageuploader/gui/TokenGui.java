package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.exception.TokenNotFoundException;
import pl.marcinm312.springbootimageuploader.service.UserService;

@Route("token")
public class TokenGui extends VerticalLayout {

	H1 h1;
	Anchor anchor;

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public TokenGui(UserService userService) {
		String tokenValue = null;
		anchor = new Anchor("..", "Back to main page");
		try {
			tokenValue = UI.getCurrent().getInternals().getLastHandledLocation().getQueryParameters().getParameters().get("value").get(0);
		} catch (Exception exc) {
			log.error("Error retrieving the value of the token: " + exc.getMessage());
		}

		if(tokenValue != null) {
			try {
				userService.activateUser(tokenValue);
				h1 = new H1("User activated");
			} catch (TokenNotFoundException exc) {
				log.error("Error while activating the user: " + exc.getMessage());
				h1 = new H1(exc.getMessage());
			}
		} else {
			h1 = new H1("Error getting token value");
		}
		add(h1, anchor);
	}
}
