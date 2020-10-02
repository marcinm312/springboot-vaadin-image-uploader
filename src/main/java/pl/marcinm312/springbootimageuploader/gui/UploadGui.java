package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.marcinm312.springbootimageuploader.service.ImageService;

import java.io.*;

@Route("upload")
public class UploadGui extends VerticalLayout {

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public UploadGui(ImageService imageService) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("authentication.getName()=" + authentication.getName());

		Anchor logoutAnchor = new Anchor("../logout", "Log out");
		add(logoutAnchor);

		Anchor mainPageAnchor = new Anchor("..", "Back to main page");
		add(mainPageAnchor);

		H1 h1 = new H1("Upload image");
		add(h1);

		MemoryBuffer vaadinBuffer = new MemoryBuffer();
		Upload upload = new Upload();

		upload.setReceiver(vaadinBuffer);
		upload.addSucceededListener(event -> {
			log.info("Start uploading an image");
			InputStream initialStream = vaadinBuffer.getInputStream();
			log.info("Get input stream");
			int cursor;
			try {
				File targetFile = new File("files/" + event.getFileName());
				log.info("Start creating temp file");
				OutputStream outputStream = new FileOutputStream(targetFile);
				while ((cursor = initialStream.read()) != -1) {
					outputStream.write(cursor);
				}
				outputStream.close();
				initialStream.close();
				log.info("Temp file created");
				String uploadedImageUrl = imageService.uploadFile(targetFile.getAbsolutePath());
				log.info("Image uploaded to Cloudinary server: " + uploadedImageUrl);
				imageService.saveFileToDB(uploadedImageUrl);
				log.info("Image saved in DB: " + uploadedImageUrl);
				targetFile.delete();
				log.info("Temp file deleted");
				log.info("Loading uploaded image:" + uploadedImageUrl);
				Image image = new Image(uploadedImageUrl, "image not found");
				image.setMaxHeight("800px");
				image.setMaxWidth("800px");
				add(image);
				log.info("Image loaded: " + uploadedImageUrl);
				Notification.show("Image successfully uploaded", 5000, Notification.Position.MIDDLE);
			} catch (IOException e) {
				e.printStackTrace();
				Notification.show("Error occurred: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
			}
		});
		add(upload);
	}
}
