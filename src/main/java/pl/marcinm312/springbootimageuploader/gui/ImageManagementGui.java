package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.klaudeta.PaginatedGrid;
import pl.marcinm312.springbootimageuploader.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.service.ImageService;

import java.util.List;

@Route("management")
@StyleSheet("/css/style.css")
public class ImageManagementGui extends VerticalLayout {

	HorizontalLayout horizontalMenu;
	Anchor logoutAnchor;
	Anchor galleryAnchor;
	Anchor uploadAnchor;
	H1 h1;
	PaginatedGrid<ImageDto> grid;

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public ImageManagementGui(ImageService imageService) {

		log.info("authentication.getName()=" + getAuthenticationName());

		logoutAnchor = new Anchor("../logout", "Log out");
		galleryAnchor = new Anchor("../gallery", "Back to gallery");
		uploadAnchor = new Anchor("../upload", "Upload image");
		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, galleryAnchor, uploadAnchor);

		h1 = new H1("Image management");

		log.info("Loading all images from DB");
		List<ImageDto> allImagesFromDB = imageService.getAllImagesFromDB();
		log.info("allImagesFromDB.size()=" + allImagesFromDB.size());
		grid = new PaginatedGrid<>(ImageDto.class);
		grid.setItems(allImagesFromDB);
		grid.setColumns("id", "publicId", "createdAt", "username");
		grid.addColumn(new ComponentRenderer<>(imageDto -> new Anchor(imageDto.getImageAddress(), imageDto.getImageAddress()))).setHeader("Image link");
		grid.addColumn(new ComponentRenderer<>(imageDto -> {
			Image image = new Image(imageDto.getImageAddress(), imageDto.getImageAddress());
			image.setHeight("100px");
			return image;
		})).setHeader("Miniature");
		grid.addColumn(new ComponentRenderer<>(imageDto -> {
			Button deleteButton = new Button("Delete");
			//deleteButton.addClickListener(event -> );
			return deleteButton;
		})).setHeader("Actions");
		grid.setPageSize(5);
		grid.setPaginationLocation(PaginatedGrid.PaginationLocation.BOTTOM);
		grid.setPaginatorSize(3);
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.setHeightByRows(true);
		grid.setPaginatorTexts("Page", "of");
		log.info("All images loaded");

		add(horizontalMenu, h1, grid);
	}

	protected String getAuthenticationName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
}
