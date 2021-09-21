package pl.marcinm312.springbootimageuploader.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.image.Image;
import pl.marcinm312.springbootimageuploader.model.image.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.repo.ImageRepo;
import pl.marcinm312.springbootimageuploader.model.image.ImageMapper;

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
		List<Image> imagesList = imageRepo.findAllByOrderByIdDesc();
		for (Image image : imagesList) {
			imagesDtoList.add(ImageMapper.convertImageToImageDto(image));
		}
		return imagesDtoList;
	}

	@Transactional
	public Image uploadAndSaveImageToDB(InputStream inputStream, AppUser appUser) throws IOException {
		log.info("Starting uploading a file");
		Map uploadResult = cloudinaryService.uploadImageToCloudinary(inputStream);
		if (uploadResult != null && uploadResult.containsKey("secure_url")) {
			String uploadedImageUrl = uploadResult.get("secure_url").toString();
			log.info("Image uploaded to Cloudinary server: {}", uploadedImageUrl);
			log.info("Saving image in DB: {}", uploadedImageUrl);
			return imageRepo.save(new Image(uploadedImageUrl, appUser));
		} else {
			return null;
		}
	}

	@Transactional
	public DeleteResult deleteImageFromCloudinaryAndDB(Long imageId) {
		log.info("Deleting imageId: {}", imageId);
		Optional<Image> optionalImage = imageRepo.findById(imageId);
		if (optionalImage.isPresent()) {
			Image image = optionalImage.get();
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
