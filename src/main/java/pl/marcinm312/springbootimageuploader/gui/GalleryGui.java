package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.klaudeta.PaginatedGrid;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.service.ImageService;

import java.util.List;

@Route("gallery")
@StyleSheet("/css/style.css")
public class GalleryGui extends VerticalLayout {

	HorizontalLayout horizontalMenu;
	Anchor logoutAnchor;
	Anchor managementAnchor;
	Anchor myProfileAnchor;
	H1 h1;
	PaginatedGrid<ImageDto> grid;

	private final transient org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public GalleryGui(ImageService imageService) {

		log.info("authentication.getName()={}", getAuthenticationName());

		logoutAnchor = new Anchor("../logout", "Log out");
		logoutAnchor.setTarget("_top");
		managementAnchor = new Anchor("../management", "Image management");
		managementAnchor.setTarget("_top");
		myProfileAnchor = new Anchor("../myprofile/update", "My profile");
		myProfileAnchor.setTarget("_top");
		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, myProfileAnchor);
		if (isUserAdmin()) {
			horizontalMenu.add(managementAnchor);
		}

		h1 = new H1("Image gallery");

		log.info("Loading all images from DB");
		List<ImageDto> allImagesFromDB = imageService.getAllImagesFromDB();
		log.info("allImagesFromDB.size()={}", allImagesFromDB.size());
		grid = new PaginatedGrid<>(ImageDto.class);
		grid.setItems(allImagesFromDB);
		grid.removeAllColumns();
		grid.addColumn(new ComponentRenderer<>(image -> {
			com.vaadin.flow.component.html.Image vaadinImage = new com.vaadin.flow.component.html.Image(image.getImageAddress(), image.getImageAddress());
			vaadinImage.setMaxHeight("500px");
			return vaadinImage;
		})).setHeader("Image").getElement().setProperty("min-width", "820px");
		grid.setPageSize(1);
		grid.setPaginationLocation(PaginatedGrid.PaginationLocation.TOP);
		grid.setPaginatorSize(0);
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.setPaginatorTexts("Image", "of");
		log.info("All images loaded");

		add(horizontalMenu, h1, grid);
	}

	String getAuthenticationName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}

	boolean isUserAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return "ROLE_ADMIN".equals(((AppUser) authentication.getPrincipal()).getRole());
	}
}
