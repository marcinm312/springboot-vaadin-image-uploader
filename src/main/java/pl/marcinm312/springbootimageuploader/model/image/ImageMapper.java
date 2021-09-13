package pl.marcinm312.springbootimageuploader.model.image;

import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.image.dto.ImageDto;

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
