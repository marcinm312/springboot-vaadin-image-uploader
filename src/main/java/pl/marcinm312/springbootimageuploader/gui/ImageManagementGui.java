package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
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

	static final int IMAGE_HEIGHT = 100;

	protected final transient org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public ImageManagementGui(ImageService imageService) {

		log.info("authentication.getName()={}", getAuthenticationName());

		logoutAnchor = new Anchor("../logout", "Log out");
		galleryAnchor = new Anchor("../gallery", "Back to gallery");
		uploadAnchor = new Anchor("../upload", "Upload image");
		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, galleryAnchor, uploadAnchor);

		h1 = new H1("Image management");

		log.info("Loading all images from DB");
		List<ImageDto> allImagesFromDB = imageService.getAllImagesFromDB();
		log.info("allImagesFromDB.size()={}", allImagesFromDB.size());
		grid = new PaginatedGrid<>(ImageDto.class);
		int pageSize = 5;
		grid.setItems(allImagesFromDB);
		grid.setColumns("id", "publicId", "createdAt", "username");
		grid.addColumn(new ComponentRenderer<>(imageDto -> new Anchor(imageDto.getImageAddress(), imageDto.getImageAddress())))
				.setHeader("Image link").setKey("image_link");
		grid.addColumn(new ComponentRenderer<>(imageDto -> {
			Image image = new Image(imageDto.getCompressedImageAddress(IMAGE_HEIGHT), imageDto.getCompressedImageAddress(IMAGE_HEIGHT));
			image.setHeight(IMAGE_HEIGHT + "px");
			return image;
		})).setHeader("Miniature");
		grid.addColumn(new ComponentRenderer<>(imageDto -> {
			Button deleteButton = new Button("Delete");
			deleteButton.addClickListener(openDialogEvent -> openDialogEvent(imageService, pageSize, imageDto));
			return deleteButton;
		})).setHeader("Actions");
		List<Grid.Column<ImageDto>> gridColumns = grid.getColumns();
		for (Grid.Column<ImageDto> column : gridColumns) {
			if (!"image_link".equals(column.getKey())) {
				column.setAutoWidth(true);
			}
		}
		grid.setPageSize(pageSize);
		grid.setPaginationLocation(PaginatedGrid.PaginationLocation.BOTTOM);
		grid.setPaginatorSize(3);
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.setHeightByRows(true);
		grid.setPaginatorTexts("Page", "of");
		log.info("All images loaded");

		add(horizontalMenu, h1, grid);
	}

	private void openDialogEvent(ImageService imageService, int pageSize, ImageDto imageDto) {
		Dialog dialog = new Dialog();
		Text text = new Text("Are you sure you want to delete this image?");
		Image image = new Image(imageDto.getCompressedImageAddress(IMAGE_HEIGHT), imageDto.getCompressedImageAddress(IMAGE_HEIGHT));
		image.setHeight("100px");
		Button confirmButton = new Button("Confirm", deleteEvent -> deleteEvent(imageService, pageSize, imageDto, dialog));
		Button cancelButton = new Button("Cancel", cancelEvent -> dialog.close());
		dialog.add(new VerticalLayout(text, image, new HorizontalLayout(confirmButton, cancelButton)));
		dialog.open();
	}

	private void deleteEvent(ImageService imageService, int pageSize, ImageDto imageDto, Dialog dialog) {
		boolean deleteResult = imageService.deleteImageFromCloudinaryAndDB(imageDto.getId());
		if (deleteResult) {
			int pageNumber = grid.getPage();
			log.info("Loading all images from DB");
			List<ImageDto> allImagesFromDBAfterDelete = imageService.getAllImagesFromDB();
			int sizeOfListAfterDeletingOfImage = allImagesFromDBAfterDelete.size();
			log.info("sizeOfListAfterDeletingOfImage={}", sizeOfListAfterDeletingOfImage);
			grid.setItems(allImagesFromDBAfterDelete);
			grid.refreshPaginator();
			if ((pageNumber - 1) == ((double) sizeOfListAfterDeletingOfImage / (double) pageSize)) {
				grid.setPage(pageNumber - 1);
			} else {
				grid.setPage(pageNumber);
			}
			showNotification("Image successfully deleted");
		} else {
			showNotification("The image has not been deleted");
		}
		dialog.close();
	}

	protected String getAuthenticationName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}

	protected void showNotification(String notificationText) {
		Notification.show(notificationText, 5000, Notification.Position.MIDDLE);
	}
}
