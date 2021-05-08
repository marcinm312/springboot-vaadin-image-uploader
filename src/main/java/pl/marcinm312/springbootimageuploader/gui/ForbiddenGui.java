package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("forbidden")
@StyleSheet("/css/style.css")
public class ForbiddenGui extends VerticalLayout {

	H1 h1;
	Anchor mainPageAnchor;

	protected final transient org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	public ForbiddenGui() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("authentication.getName()={}", authentication.getName());

		h1 = new H1("No permission. This functionality is only available to the system administrator");
		mainPageAnchor = new Anchor("..", "Back to main page");

		add(h1, mainPageAnchor);
	}

}
