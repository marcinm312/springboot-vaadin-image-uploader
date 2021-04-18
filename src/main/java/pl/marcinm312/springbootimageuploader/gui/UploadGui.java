package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

import java.io.*;

@Route("upload")
@StyleSheet("/css/style.css")
public class UploadGui extends VerticalLayout {

	Anchor logoutAnchor;
	Anchor galleryAnchor;
	HorizontalLayout horizontalMenu;
	H1 h1;
	Upload upload;
	Image image;

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public UploadGui(ImageService imageService, UserService userService) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("authentication.getName()=" + authentication.getName());

		logoutAnchor = new Anchor("../logout", "Log out");
		galleryAnchor = new Anchor("../gallery", "Back to gallery");
		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, galleryAnchor);

		h1 = new H1("Upload image");

		MemoryBuffer vaadinBuffer = new MemoryBuffer();
		upload = new Upload(vaadinBuffer);

		image = new Image();

		upload.addSucceededListener(event -> uploadImageAction(imageService, userService, authentication, vaadinBuffer, event));
		add(horizontalMenu, h1, upload, image);
	}

	private void uploadImageAction(ImageService imageService, UserService userService, Authentication authentication, MemoryBuffer vaadinBuffer, com.vaadin.flow.component.upload.SucceededEvent event) {
		String fileType = event.getMIMEType();
		if (fileType.startsWith("image")) {
			log.info("Start uploading an image");
			InputStream initialStream = vaadinBuffer.getInputStream();
			log.info("Get input stream");
			try {
				AppUser appUser = userService.getUserByAuthentication(authentication);
				pl.marcinm312.springbootimageuploader.model.Image savedImage = imageService.uploadAndSaveImageToDB(initialStream, appUser);
				if (savedImage != null) {
					String uploadedImageUrl = savedImage.getImageAddress();
					log.info("Image saved in DB: " + uploadedImageUrl);
					log.info("Loading uploaded image:" + uploadedImageUrl);
					image.setSrc(uploadedImageUrl);
					image.setAlt(uploadedImageUrl);
					image.setMaxHeight("500px");
					log.info("Image loaded: " + uploadedImageUrl);
					Notification.show("Image successfully uploaded", 5000, Notification.Position.MIDDLE);
				} else {
					Notification.show("Error uploading and saving the image", 5000, Notification.Position.MIDDLE);
				}
			} catch (IOException e) {
				e.printStackTrace();
				Notification.show("Error occurred: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
			}
		} else {
			log.info("Invalid file type");
			log.info("fileType=" + fileType);
			Notification.show("Error: Invalid file type", 5000, Notification.Position.MIDDLE);
		}
	}
}
