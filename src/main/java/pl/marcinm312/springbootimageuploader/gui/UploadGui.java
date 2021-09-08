package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.service.ImageService;
import pl.marcinm312.springbootimageuploader.service.UserService;

import java.io.IOException;
import java.io.InputStream;

@Route("upload")
@StyleSheet("/css/style.css")
public class UploadGui extends VerticalLayout {

	Anchor logoutAnchor;
	Anchor managementAnchor;
	HorizontalLayout horizontalMenu;
	H1 h1;
	Upload upload;
	Image image;

	private final transient ImageService imageService;
	private final transient UserService userService;

	private final transient org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public UploadGui(ImageService imageService, UserService userService) {

		this.imageService = imageService;
		this.userService = userService;

		AppUser appUser = getAuthenticatedUser();
		log.info("appUser.getUsername()={}", appUser.getUsername());

		logoutAnchor = new Anchor("../logout", "Log out");
		managementAnchor = new Anchor("../management", "Back to image management");

		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, managementAnchor);

		h1 = new H1("Upload image");

		MemoryBuffer vaadinBuffer = new MemoryBuffer();
		upload = new Upload(vaadinBuffer);

		image = new Image();

		upload.addSucceededListener(event -> uploadImageAction(appUser, vaadinBuffer, event));
		add(horizontalMenu, h1, upload, image);
	}

	private void uploadImageAction(AppUser appUser, MemoryBuffer vaadinBuffer, SucceededEvent event) {
		String fileType = event.getMIMEType();
		if (fileType.startsWith("image")) {
			log.info("Start uploading an image");
			InputStream initialStream = vaadinBuffer.getInputStream();
			log.info("Get input stream");
			try {
				pl.marcinm312.springbootimageuploader.model.image.Image savedImage = imageService.uploadAndSaveImageToDB(initialStream, appUser);
				if (savedImage != null) {
					String uploadedImageUrl = savedImage.getImageAddress();
					log.info("Image saved in DB: {}", uploadedImageUrl);
					log.info("Loading uploaded image: {}", uploadedImageUrl);
					image.setSrc(uploadedImageUrl);
					image.setAlt(uploadedImageUrl);
					image.setMaxHeight("500px");
					log.info("Image loaded: {}", uploadedImageUrl);
					showNotification("Image successfully uploaded");
				} else {
					showNotification("Error uploading and saving the image");
				}
			} catch (IOException e) {
				log.error("Error occurred during uploading image. [MESSAGE]: {}", e.getMessage());
				showNotification("Error occurred: " + e.getMessage());
			}
		} else {
			log.info("Invalid file type");
			log.info("fileType={}", fileType);
			showNotification("Error: Invalid file type");
		}
	}

	void showNotification(String notificationText) {
		Notification.show(notificationText, 5000, Notification.Position.MIDDLE);
	}

	AppUser getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userService.getUserByAuthentication(authentication);
	}
}
