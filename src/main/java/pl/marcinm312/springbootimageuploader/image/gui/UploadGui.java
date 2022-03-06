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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;
import pl.marcinm312.springbootimageuploader.image.service.ImageService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.service.UserService;

import java.io.IOException;
import java.io.InputStream;

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

	private final transient org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public UploadGui(ImageService imageService, UserService userService) {

		this.imageService = imageService;

		UserEntity user = userService.getUserByUsername(VaadinUtils.getAuthenticatedUserName());
		log.info("user.getUsername()={}", user.getUsername());

		logoutAnchor = new Anchor("../logout", "Log out");
		managementAnchor = new Anchor("../management", "Back to image management");

		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, managementAnchor);

		h1 = new H1("Upload image");

		MemoryBuffer vaadinBuffer = new MemoryBuffer();
		upload = new Upload(vaadinBuffer);

		image = new Image();

		upload.addSucceededListener(event -> {
			try {
				uploadImageAction(user, vaadinBuffer, event);
			} catch (IOException e) {
				log.error("Error occurred during uploading image. [MESSAGE]: {}", e.getMessage());
				VaadinUtils.showNotification("Error occurred: " + e.getMessage());
			}
		});
		add(horizontalMenu, h1, upload, image);
	}

	private void uploadImageAction(UserEntity user, MemoryBuffer vaadinBuffer, SucceededEvent event) throws IOException {
		String fileType = event.getMIMEType();
		if (fileType.startsWith("image")) {
			log.info("Start uploading an image");
			try (InputStream initialStream = vaadinBuffer.getInputStream()) {
				log.info("Get input stream");
				ImageEntity savedImage = imageService.uploadAndSaveImageToDB(initialStream, user);
				if (savedImage != null) {
					String uploadedImageUrl = savedImage.getImageAddress();
					log.info("Image saved in DB: {}", uploadedImageUrl);
					log.info("Loading uploaded image: {}", uploadedImageUrl);
					image.setSrc(uploadedImageUrl);
					image.setAlt(uploadedImageUrl);
					image.setMaxHeight("500px");
					log.info("Image loaded: {}", uploadedImageUrl);
					VaadinUtils.showNotification("Image successfully uploaded");
				} else {
					VaadinUtils.showNotification("Error uploading and saving the image");
				}
			} catch (IOException e) {
				log.error("Error occurred during uploading image. [MESSAGE]: {}", e.getMessage());
				VaadinUtils.showNotification("Error occurred: " + e.getMessage());
			}
		} else {
			log.info("Invalid file type");
			log.info("fileType={}", fileType);
			VaadinUtils.showNotification("Error: Invalid file type");
		}
	}
}
