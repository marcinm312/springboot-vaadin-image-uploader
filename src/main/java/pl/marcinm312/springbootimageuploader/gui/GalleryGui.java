package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.marcinm312.springbootimageuploader.model.Image;
import pl.marcinm312.springbootimageuploader.service.ImageService;

import java.util.List;

@Route("gallery")
public class GalleryGui extends VerticalLayout {

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public GalleryGui(ImageService imageService) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("authentication.getName()=" + authentication.getName());

		Anchor logoutAnchor = new Anchor("../logout", "Log out");
		Anchor mainPageAnchor = new Anchor("..", "Back to main page");
		H1 h1 = new H1("Image gallery");

		add(logoutAnchor, mainPageAnchor, h1);

		log.info("Loading all images from DB");
		List<Image> allImagesFromDB = imageService.getAllImagesFromDB();
		log.info("allImagesFromDB.size()=" + allImagesFromDB.size());
		allImagesFromDB.forEach(element -> {
			com.vaadin.flow.component.html.Image image = new com.vaadin.flow.component.html.Image(element.getImageAddress(), "image not found");
			image.setMaxHeight("800px");
			image.setMaxWidth("800px");
			add(image);
		});
		log.info("All images loaded");
	}
}
