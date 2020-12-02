package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class MainGui extends VerticalLayout {

	Anchor registerAnchor;
	Anchor galleryAnchor;
	Anchor uploadAnchor;

	public MainGui() {

		registerAnchor = new Anchor("/register", "Create new account");
		galleryAnchor = new Anchor("/gallery", "Gallery");
		uploadAnchor = new Anchor("/upload", "Upload image");

		add(registerAnchor, galleryAnchor, uploadAnchor);
	}
}
