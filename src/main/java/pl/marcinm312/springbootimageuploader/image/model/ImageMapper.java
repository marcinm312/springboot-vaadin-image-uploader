package pl.marcinm312.springbootimageuploader.image.model;

import pl.marcinm312.springbootimageuploader.user.model.AppUser;
import pl.marcinm312.springbootimageuploader.image.model.dto.ImageDto;

public class ImageMapper {

	private ImageMapper() {

	}

	public static ImageDto convertImageToImageDto(Image image) {
		ImageDto imageDto = new ImageDto(image.getId());
		imageDto.setImageAddress(image.getImageAddress());
		imageDto.setPublicId(image.getPublicId());
		imageDto.setCreatedAt(image.getCreatedAtAsString());
		imageDto.setUpdatedAt(image.getUpdatedAtAsString());
		AppUser appUser = image.getUser();
		if (appUser != null) {
			imageDto.setUsername(appUser.getUsername());
		}
		return imageDto;
	}
}
