package pl.marcinm312.springbootimageuploader.image.gui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;
import pl.marcinm312.springbootimageuploader.image.service.ImageService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.service.UserService;

import java.io.InputStream;

@Slf4j
@Route("upload")
@StyleSheet("/css/style.css")
@PageTitle("Upload image")
//@DeclareRoles("ADMIN")
//@RolesAllowed("ADMIN")
//@Secured("ADMIN")
public class UploadGui extends VerticalLayout {

	Anchor logoutAnchor;
	Anchor managementAnchor;
	HorizontalLayout horizontalMenu;

	H1 h1;

	Upload upload;
	Image image;

	private final transient ImageService imageService;
	private final UserEntity user;

	@Autowired
	public UploadGui(ImageService imageService, UserService userService) {

		this.imageService = imageService;

		user = userService.getUserByUsername(VaadinUtils.getAuthenticatedUserName());
		log.info("user.getUsername()={}", user.getUsername());

		prepareHorizontalMenu();
		h1 = new H1("Upload image");
		prepareUploadButton();
		image = new Image();
		image.setMaxHeight("500px");

		add(horizontalMenu, h1, upload, image);
	}

	private void prepareUploadButton() {

		MemoryBuffer vaadinBuffer = new MemoryBuffer();
		upload = new Upload(vaadinBuffer);
		upload.addSucceededListener(event -> uploadImageAction(vaadinBuffer, event));
	}

	private void prepareHorizontalMenu() {

		logoutAnchor = new Anchor("../logout", "Log out");
		managementAnchor = new Anchor("../management", "Back to image management");
		horizontalMenu = new HorizontalLayout(logoutAnchor, managementAnchor);
	}

	private void uploadImageAction(MemoryBuffer vaadinBuffer, SucceededEvent event) {

		String fileType = event.getMIMEType();
		if (fileType.startsWith("image")) {
			log.info("Start uploading an image");
			try (InputStream inputStream = vaadinBuffer.getInputStream()) {
				log.info("Get input stream");
				ImageEntity savedImage = imageService.uploadAndSaveImageToDB(inputStream, user);
				if (savedImage != null) {
					showUploadedImage(savedImage);
					VaadinUtils.showNotification("Image successfully uploaded");
				} else {
					VaadinUtils.showNotification("Error uploading and saving the image");
				}
			} catch (Exception e) {
				log.error("Error occurred during uploading image. [MESSAGE]: {}", e.getMessage());
				VaadinUtils.showNotification("Error occurred: " + e.getMessage());
			}
		} else {
			log.info("Invalid file type");
			log.info("fileType={}", fileType);
			VaadinUtils.showNotification("Error: Invalid file type");
		}
	}

	private void showUploadedImage(ImageEntity savedImage) {
		
		String uploadedImageUrl = savedImage.getImageAddress();
		log.info("Image saved in DB: {}", uploadedImageUrl);
		log.info("Loading uploaded image: {}", uploadedImageUrl);
		image.setSrc(uploadedImageUrl);
		image.setAlt(uploadedImageUrl);
		log.info("Image loaded: {}", uploadedImageUrl);
	}
}
