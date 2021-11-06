package pl.marcinm312.springbootimageuploader.image.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;
import pl.marcinm312.springbootimageuploader.image.model.ImageMapper;
import pl.marcinm312.springbootimageuploader.image.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.image.repository.ImageRepo;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ImageService {

	private final ImageRepo imageRepo;
	private final CloudinaryService cloudinaryService;

	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public ImageService(ImageRepo imageRepo, CloudinaryService cloudinaryService) {
		this.imageRepo = imageRepo;
		this.cloudinaryService = cloudinaryService;
	}

	public List<ImageDto> getAllImagesFromDB() {
		List<ImageDto> imagesDtoList = new ArrayList<>();
		List<ImageEntity> imagesList = imageRepo.findAllByOrderByIdDesc();
		for (ImageEntity image : imagesList) {
			imagesDtoList.add(ImageMapper.convertImageToImageDto(image));
		}
		return imagesDtoList;
	}

	@Transactional
	public ImageEntity uploadAndSaveImageToDB(InputStream inputStream, UserEntity user) throws IOException {
		log.info("Starting uploading a file");
		Map uploadResult = cloudinaryService.uploadImageToCloudinary(inputStream);
		if (uploadResult != null && uploadResult.containsKey("secure_url")) {
			String uploadedImageUrl = uploadResult.get("secure_url").toString();
			log.info("Image uploaded to Cloudinary server: {}", uploadedImageUrl);
			log.info("Saving image in DB: {}", uploadedImageUrl);
			return imageRepo.save(new ImageEntity(uploadedImageUrl, user));
		} else {
			return null;
		}
	}

	@Transactional
	public DeleteResult deleteImageFromCloudinaryAndDB(Long imageId) {
		log.info("Deleting imageId: {}", imageId);
		Optional<ImageEntity> optionalImage = imageRepo.findById(imageId);
		if (optionalImage.isPresent()) {
			ImageEntity image = optionalImage.get();
			try {
				boolean imageExistsInCloudinary = cloudinaryService.checkIfImageExistsInCloudinary(image);
				boolean deleteFromCloudinaryResult = false;
				if (imageExistsInCloudinary) {
					deleteFromCloudinaryResult = cloudinaryService.deleteImageFromCloudinary(image);
				}
				if (!imageExistsInCloudinary || deleteFromCloudinaryResult) {
					imageRepo.delete(image);
					return DeleteResult.DELETED;
				}
			} catch (Exception e) {
				log.error("Error occurred during deleting imageId: {} [MESSAGE]: {}", imageId, e.getMessage());
			}
		} else {
			return DeleteResult.NOT_EXISTS_IN_DB;
		}
		return DeleteResult.ERROR;
	}

	public enum DeleteResult {
		DELETED, NOT_EXISTS_IN_DB, ERROR
	}
}
