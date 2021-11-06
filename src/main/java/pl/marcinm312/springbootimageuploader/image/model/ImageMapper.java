package pl.marcinm312.springbootimageuploader.image.model;

import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.image.model.dto.ImageDto;

public class ImageMapper {

	private ImageMapper() {

	}

	public static ImageDto convertImageToImageDto(ImageEntity image) {
		ImageDto imageDto = new ImageDto(image.getId());
		imageDto.setImageAddress(image.getImageAddress());
		imageDto.setPublicId(image.getPublicId());
		imageDto.setCreatedAt(image.getCreatedAtAsString());
		imageDto.setUpdatedAt(image.getUpdatedAtAsString());
		UserEntity user = image.getUser();
		if (user != null) {
			imageDto.setUsername(user.getUsername());
		}
		return imageDto;
	}
}
