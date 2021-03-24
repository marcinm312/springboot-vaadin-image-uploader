package pl.marcinm312.springbootimageuploader.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.Image;
import pl.marcinm312.springbootimageuploader.repo.ImageRepo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class ImageService {

	private final Cloudinary cloudinary;
	private final ImageRepo imageRepo;

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public ImageService(ImageRepo imageRepo, Environment environment) {
		this.imageRepo = imageRepo;
		cloudinary = new Cloudinary(ObjectUtils.asMap(
				"cloud_name", environment.getProperty("cloudinary.cloudNameValue"),
				"api_key", environment.getProperty("cloudinary.apiKeyValue"),
				"api_secret", environment.getProperty("cloudinary.apiSecretValue")));
	}

	public List<Image> getAllImagesFromDB() {
		return imageRepo.findAllByOrderByIdDesc();
	}

	public String uploadFile(InputStream inputStream) throws IOException {
		Map uploadResult = cloudinary.uploader().uploadLarge(inputStream, ObjectUtils.emptyMap());
		assert uploadResult != null;
		return uploadResult.get("secure_url").toString();
	}

	public Image saveFileToDB(String fileUrl, AppUser appUser) {
		log.info("Saving image in DB: " + fileUrl);
		return imageRepo.save(new Image(fileUrl, appUser));
	}
}