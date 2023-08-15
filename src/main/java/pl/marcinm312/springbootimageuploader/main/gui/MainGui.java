package pl.marcinm312.springbootimageuploader.main.gui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PACKAGE)
@Route("")
@StyleSheet("/css/style.css")
@PageTitle("Main page")
public class MainGui extends VerticalLayout {

	private final Anchor registerAnchor;
	private final Anchor galleryAnchor;

	public MainGui() {

		registerAnchor = new Anchor("/register", "Create new account");
		galleryAnchor = new Anchor("/gallery", "Gallery");

		add(registerAnchor, galleryAnchor);
	}
}
