package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class MainGui extends VerticalLayout {

	public MainGui() {

		Anchor registerAnchor = new Anchor("/register", "Create new account");
		add(registerAnchor);
		Anchor galleryAnchor = new Anchor("/gallery", "Gallery");
		add(galleryAnchor);
		Anchor uploadAnchor = new Anchor("/upload", "Upload image");
		add(uploadAnchor);
	}
}
