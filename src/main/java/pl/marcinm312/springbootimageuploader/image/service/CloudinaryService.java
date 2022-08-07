package pl.marcinm312.springbootimageuploader.image.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
public class CloudinaryService {

	private final Cloudinary cloudinary;

	public CloudinaryService(Environment environment) {

		cloudinary = new Cloudinary(ObjectUtils.asMap(
				"cloud_name", environment.getProperty("cloudinary.cloudNameValue"),
				"api_key", environment.getProperty("cloudinary.apiKeyValue"),
				"api_secret", environment.getProperty("cloudinary.apiSecretValue")));
	}

	public Map uploadImageToCloudinary(InputStream inputStream) throws IOException {
		return cloudinary.uploader().uploadLarge(inputStream, ObjectUtils.emptyMap());
	}

	public boolean deleteImageFromCloudinary(ImageEntity image) throws Exception {

		ApiResponse apiResponse = cloudinary.api().deleteResources(Collections.singletonList(image.getPublicId()), ObjectUtils.emptyMap());
		return checkDeleteFromCloudinaryResult(image, apiResponse);
	}

	public boolean checkIfImageExistsInCloudinary(ImageEntity image) {

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
		}
		log.info("Image with publicId: {} not exists in Cloudinary", publicId);
		return false;
	}

	private boolean checkDeleteFromCloudinaryResult(ImageEntity image, ApiResponse deleteApiResponse) {

		String deleted = "deleted";
		String publicId = image.getPublicId();
		log.info("Checking delete from Cloudinary result for publicId: {}", publicId);
		if (deleteApiResponse.containsKey(deleted)) {
			Map deletedMap = (Map) deleteApiResponse.get(deleted);
			if (deletedMap.containsKey(publicId)) {
				String deleteImageResult = (String) deletedMap.get(publicId);
				if (deleted.equals(deleteImageResult)) {
					log.info("Successfully deleted from Cloudinary image with publicId: {}", publicId);
					return true;
				}
			}
		}
		log.error("Image with publicId: {} has not been deleted", publicId);
		return false;
	}
}
