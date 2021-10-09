package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@StyleSheet("/css/style.css")
@PageTitle("Main page")
public class MainGui extends VerticalLayout {

	Anchor registerAnchor;
	Anchor galleryAnchor;

	public MainGui() {

		registerAnchor = new Anchor("/register", "Create new account");
		galleryAnchor = new Anchor("/gallery", "Gallery");

		add(registerAnchor, galleryAnchor);
	}
}
