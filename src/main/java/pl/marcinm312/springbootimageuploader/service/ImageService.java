package pl.marcinm312.springbootimageuploader.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.Image;
import pl.marcinm312.springbootimageuploader.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.repo.ImageRepo;
import pl.marcinm312.springbootimageuploader.utils.ConvertUtils;

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

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public ImageService(ImageRepo imageRepo, CloudinaryService cloudinaryService) {
		this.imageRepo = imageRepo;
		this.cloudinaryService = cloudinaryService;
	}

	public List<ImageDto> getAllImagesFromDB() {
		List<ImageDto> imagesDtoList = new ArrayList<>();
		List<Image> imagesList = imageRepo.findAllByOrderByIdDesc();
		for (Image image : imagesList) {
			imagesDtoList.add(ConvertUtils.convertImageToImageDto(image));
		}
		return imagesDtoList;
	}

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

	public boolean deleteImageFromCloudinaryAndDB(Long imageId) {
		log.info("Deleting imageId: {}", imageId);
		try {
			Optional<Image> optionalImage = imageRepo.findById(imageId);
			if (optionalImage.isPresent()) {
				Image image = optionalImage.get();
				boolean imageExists = cloudinaryService.checkIfImageExistsInCloudinary(image);
				if (imageExists) {
					boolean deleteResult = cloudinaryService.deleteImageFromCloudinary(image);
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
}
