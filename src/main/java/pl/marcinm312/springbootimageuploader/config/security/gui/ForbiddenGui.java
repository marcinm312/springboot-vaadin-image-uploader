package pl.marcinm312.springbootimageuploader.config.security.gui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;

@Slf4j
@Route("forbidden")
@StyleSheet("/css/style.css")
@PageTitle("No permission")
public class ForbiddenGui extends VerticalLayout {

	H1 h1;
	Anchor galleryAnchor;

	public ForbiddenGui() {

		log.info("authentication.getName()={}", VaadinUtils.getAuthenticatedUserName());

		h1 = new H1("No permission. This functionality is only available to the system administrator");
		galleryAnchor = new Anchor("../gallery", "Back to gallery");

		add(h1, galleryAnchor);
	}
}
