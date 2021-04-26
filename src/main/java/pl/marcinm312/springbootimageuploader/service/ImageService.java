package pl.marcinm312.springbootimageuploader.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.Image;
import pl.marcinm312.springbootimageuploader.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.repo.ImageRepo;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

	public List<ImageDto> getAllImagesFromDB() {
		List<ImageDto> imagesDtoList = new ArrayList<>();
		List<Image> imagesList = imageRepo.findAllByOrderByIdDesc();
		for (Image image : imagesList) {
			imagesDtoList.add(convertImageToImageDto(image));
		}
		return imagesDtoList;
	}

	public Image uploadAndSaveImageToDB(InputStream inputStream, AppUser appUser) throws IOException {
		log.info("Starting uploading a file");
		Map uploadResult = cloudinary.uploader().uploadLarge(inputStream, ObjectUtils.emptyMap());
		if (uploadResult != null && uploadResult.containsKey("secure_url")) {
			String uploadedImageUrl = uploadResult.get("secure_url").toString();
			log.info("Image uploaded to Cloudinary server: " + uploadedImageUrl);
			log.info("Saving image in DB: " + uploadedImageUrl);
			return imageRepo.save(new Image(uploadedImageUrl, appUser));
		} else {
			return null;
		}
	}

	public boolean deleteImageFromCloudinaryAndDB(Long imageId) throws Exception {
		Optional<Image> optionalImage = imageRepo.findById(imageId);
		if (optionalImage.isPresent()) {
			Image image = optionalImage.get();
			cloudinary.api().deleteResources(Collections.singletonList(image.getPublicId()), ObjectUtils.emptyMap());
			imageRepo.delete(image);
			return true;
		} else {
			return false;
		}
	}

	private ImageDto convertImageToImageDto(Image image) {
		ImageDto imageDto = new ImageDto();
		imageDto.setId(image.getId());
		imageDto.setImageAddress(image.getImageAddress());
		imageDto.setPublicId(image.getPublicId());
		imageDto.setUsername(image.getUser().getUsername());
		imageDto.setCreatedAt(image.getCreatedAtAsString());
		imageDto.setUpdatedAt(image.getUpdatedAtAsString());
		return imageDto;
	}
}