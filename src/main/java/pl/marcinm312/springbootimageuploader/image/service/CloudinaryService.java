package pl.marcinm312.springbootimageuploader.image.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.marcinm312.springbootimageuploader.image.exception.CloudinaryException;
import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
public class CloudinaryService {

	private final Cloudinary cloudinary;

	private static final String RESOURCES_KEY = "resources";
	private static final String DELETED_KEY = "deleted";

	public CloudinaryService(Environment environment) {

		cloudinary = new Cloudinary(ObjectUtils.asMap(
				"cloud_name", environment.getProperty("cloudinary.cloudNameValue"),
				"api_key", environment.getProperty("cloudinary.apiKeyValue"),
				"api_secret", environment.getProperty("cloudinary.apiSecretValue")));
	}

	public String uploadImageToCloudinary(InputStream inputStream) throws IOException, CloudinaryException {

		Map<?, ?> uploadResult = cloudinary.uploader().uploadLarge(inputStream, ObjectUtils.emptyMap());
		if (uploadResult == null || !uploadResult.containsKey("secure_url")) {
			String errorMessage = "Failed to upload the file to Cloudinary";
			log.error(errorMessage);
			throw new CloudinaryException(errorMessage);
		}
		return uploadResult.get("secure_url").toString();
	}

	public boolean deleteImageFromCloudinary(ImageEntity image) throws Exception {

		ApiResponse apiResponse = cloudinary.api().deleteResources(Collections.singletonList(image.getPublicId()), ObjectUtils.emptyMap());
		return checkDeleteFromCloudinaryResult(image, apiResponse);
	}

	public boolean checkIfImageExistsInCloudinary(ImageEntity image) throws Exception {

		String publicId = image.getPublicId();
		log.info("Checking if image exists in Cloudinary. publicId: {}", publicId);
		String commonErrorMessage = "Image with publicId: " + publicId + " does not exist in Cloudinary";
		ApiResponse apiResponse = cloudinary.api().resourcesByIds(Collections.singletonList(publicId), ObjectUtils.emptyMap());

		if (apiResponse == null || !apiResponse.containsKey(RESOURCES_KEY)) {
			String errorMessage = "Response from Cloudinary does not contain the '" + RESOURCES_KEY + "' key!";
			log.error(commonErrorMessage);
			log.error(errorMessage);
			throw new CloudinaryException(errorMessage);
		}

		Object resourcesObject = apiResponse.get(RESOURCES_KEY);
		if (!(resourcesObject instanceof List<?> listFromApi)) {
			String errorMessage = "Object with '" + RESOURCES_KEY + "' key is not a List";
			log.error(commonErrorMessage);
			log.error(errorMessage);
			log.error("resourcesObject type: {}", resourcesObject.getClass().getName());
			throw new CloudinaryException(errorMessage);
		}

		if (listFromApi.isEmpty()) {
			String errorMessage = "Resources list from Cloudinary response is empty";
			log.error(commonErrorMessage);
			log.error(errorMessage);
			return false;
		}

		Object firstElementFromResourcesList = listFromApi.get(0);
		if (!(firstElementFromResourcesList instanceof Map<?, ?> mapFromResourcesList)) {
			String errorMessage = "First element from resources list is not a Map";
			log.error(commonErrorMessage);
			log.error(errorMessage);
			log.error("firstElementFromResourcesList type: {}", firstElementFromResourcesList.getClass().getName());
			return false;
		}

		if (!mapFromResourcesList.containsKey("public_id") || !publicId.equals(mapFromResourcesList.get("public_id"))) {
			log.info(commonErrorMessage);
			return false;
		}

		log.info("Image with publicId: {} exists in Cloudinary", publicId);
		return true;
	}

	private boolean checkDeleteFromCloudinaryResult(ImageEntity image, ApiResponse deleteApiResponse) {

		String publicId = image.getPublicId();
		log.info("Checking delete from Cloudinary result for publicId: {}", publicId);
		String commonErrorMessage = "Image with publicId: " + publicId + " has not been deleted from Cloudinary";

		if (deleteApiResponse == null || !deleteApiResponse.containsKey(DELETED_KEY)) {
			String errorMessage = "Response from Cloudinary does not contain '" + DELETED_KEY + "' key";
			log.error(commonErrorMessage);
			log.error(errorMessage);
			return false;
		}

		Map<?, ?> deletedMap = (Map<?, ?>) deleteApiResponse.get(DELETED_KEY);
		if (!deletedMap.containsKey(publicId)) {
			String errorMessage = "Deleted object from Cloudinary does not contain '" + publicId + "' key";
			log.error(commonErrorMessage);
			log.error(errorMessage);
			return false;
		}

		String deleteImageResult = (String) deletedMap.get(publicId);
		if (!DELETED_KEY.equals(deleteImageResult)) {
			log.error(commonErrorMessage);
			return false;
		}

		log.info("Successfully deleted from Cloudinary image with publicId: {}", publicId);
		return true;
	}
}
