package pl.marcinm312.springbootimageuploader.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
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
			log.info("Image uploaded to Cloudinary server: {}", uploadedImageUrl);
			log.info("Saving image in DB: {}", uploadedImageUrl);
			return imageRepo.save(new Image(uploadedImageUrl, appUser));
		} else {
			return null;
		}
	}

	public boolean deleteImageFromCloudinaryAndDB(Long imageId) {
		log.info("Deleting imageId: {}", imageId);
		try {
			Optional<Image> optionalImage = imageRepo.findById(imageId);
			if (optionalImage.isPresent()) {
				Image image = optionalImage.get();
				boolean imageExists = checkIfImageExistsInCloudinary(image);
				if (imageExists) {
					ApiResponse deleteApiResponse = cloudinary.api().deleteResources(Collections.singletonList(image.getPublicId()), ObjectUtils.emptyMap());
					boolean deleteResult = checkDeleteFromCloudinaryResult(image, deleteApiResponse);
					if (deleteResult) {
						imageRepo.delete(image);
						return true;
					}
				} else {
					imageRepo.delete(image);
					return true;
				}
			}
		} catch (Exception e) {
			log.error("Error occurred during deleting imageId: {} [MESSAGE]: {}", imageId, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	private boolean checkIfImageExistsInCloudinary(Image image) {
		String publicId = image.getPublicId();
		log.info("Checking if image exists in Cloudinary. publicId: {}", publicId);
		try {
			ApiResponse apiResponse = cloudinary.api().resourcesByIds(Collections.singletonList(publicId), ObjectUtils.emptyMap());
			if (apiResponse.containsKey("resources")) {
				List<HashMap> listFromApi = (ArrayList<HashMap>) apiResponse.get("resources");
				if (!listFromApi.isEmpty()) {
					HashMap firstElement = listFromApi.get(0);
					if (firstElement.containsKey("public_id") && publicId.equals(firstElement.get("public_id"))) {
						log.info("Image with publicId: {} exists in Cloudinary", publicId);
						return true;
					}
				}
			}
		} catch (Exception e) {
			log.error("Error occurred during checking if image exists in Cloudinary publicId: {} [MESSAGE]: {}", publicId, e.getMessage());
			e.printStackTrace();
		}
		log.info("Image with publicId: {} not exists in Cloudinary", publicId);
		return false;
	}

	private boolean checkDeleteFromCloudinaryResult(Image image, ApiResponse deleteApiResponse) {
		String publicId = image.getPublicId();
		log.info("Checking delete from Cloudinary result for publicId: {}", publicId);
		if (deleteApiResponse.containsKey("deleted")) {
			Map deletedMap = (Map) deleteApiResponse.get("deleted");
			if (deletedMap.containsKey(publicId)) {
				String deleteImageResult = (String) deletedMap.get(publicId);
				if ("deleted".equals(deleteImageResult)) {
					log.info("Successfully deleted from Cloudinary image with publicId: {}", publicId);
					return true;
				}
			}
		}
		log.error("Image with publicId: {} has not been deleted", publicId);
		return false;
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
