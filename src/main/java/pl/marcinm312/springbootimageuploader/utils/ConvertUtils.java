package pl.marcinm312.springbootimageuploader.utils;

import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.Image;
import pl.marcinm312.springbootimageuploader.model.dto.ImageDto;

public class ConvertUtils {

	private ConvertUtils() {

	}

	public static ImageDto convertImageToImageDto(Image image) {
		ImageDto imageDto = new ImageDto();
		imageDto.setId(image.getId());
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
