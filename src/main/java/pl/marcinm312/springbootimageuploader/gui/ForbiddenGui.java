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
	Anchor galleryAnchor;

	private final transient org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	public ForbiddenGui() {

		log.info("authentication.getName()={}", getAuthenticatedUsername());

		h1 = new H1("No permission. This functionality is only available to the system administrator");
		galleryAnchor = new Anchor("../gallery", "Back to gallery");

		add(h1, galleryAnchor);
	}

	String getAuthenticatedUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
}
