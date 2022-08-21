package pl.marcinm312.springbootimageuploader.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.marcinm312.springbootimageuploader.image.exception.CloudinaryException;
import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;
import pl.marcinm312.springbootimageuploader.image.model.ImageMapper;
import pl.marcinm312.springbootimageuploader.image.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.image.repository.ImageRepo;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ImageService {

	private final ImageRepo imageRepo;
	private final CloudinaryService cloudinaryService;

	public List<ImageDto> getAllImagesFromDB() {

		List<ImageEntity> imagesList = imageRepo.findAllByOrderByIdDesc();
		return ImageMapper.convertImageEntityListToImageDtoList(imagesList);
	}

	@Transactional
	public ImageEntity uploadAndSaveImageToDB(InputStream inputStream, UserEntity user) throws IOException,
			CloudinaryException {

		log.info("Starting uploading a file");
		String uploadedImageUrl = cloudinaryService.uploadImageToCloudinary(inputStream);
		log.info("Image uploaded to Cloudinary server: {}", uploadedImageUrl);
		log.info("Saving image in DB: {}", uploadedImageUrl);
		return imageRepo.save(new ImageEntity(uploadedImageUrl, user));
	}

	@Transactional
	public DeleteResult deleteImageFromCloudinaryAndDB(Long imageId) throws Exception {

		log.info("Deleting imageId: {}", imageId);
		Optional<ImageEntity> optionalImage = imageRepo.findById(imageId);
		if (optionalImage.isEmpty()) {
			return DeleteResult.NOT_EXISTS_IN_DB;
		}
		ImageEntity image = optionalImage.get();

		boolean imageExistsInCloudinary = cloudinaryService.checkIfImageExistsInCloudinary(image);
		boolean deleteFromCloudinaryResult = false;
		if (imageExistsInCloudinary) {
			deleteFromCloudinaryResult = cloudinaryService.deleteImageFromCloudinary(image);
		}
		if (!imageExistsInCloudinary || deleteFromCloudinaryResult) {
			imageRepo.delete(image);
			return DeleteResult.DELETED;
		}
		return DeleteResult.ERROR;
	}

	public enum DeleteResult {
		DELETED, NOT_EXISTS_IN_DB, ERROR
	}
}
