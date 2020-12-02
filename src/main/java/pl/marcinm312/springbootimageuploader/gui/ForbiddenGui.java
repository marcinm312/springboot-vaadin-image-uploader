package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("forbidden")
public class ForbiddenGui extends VerticalLayout {

	H1 h1;
	Anchor mainPageAnchor;

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	public ForbiddenGui() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("authentication.getName()=" + authentication.getName());

		h1 = new H1("No permission. Only the administrator can add new photos");
		mainPageAnchor = new Anchor("..", "Back to main page");

		add(h1, mainPageAnchor);
	}

}
