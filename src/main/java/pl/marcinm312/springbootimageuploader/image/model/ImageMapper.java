package pl.marcinm312.springbootimageuploader.image.model;

import pl.marcinm312.springbootimageuploader.image.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ImageMapper {

	private ImageMapper() {

	}

	public static ImageDto convertImageEntityToImageDto(ImageEntity image) {
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

	public static List<ImageDto> convertImageEntityListToImageDtoList(List<ImageEntity> imageList) {
		return imageList.stream().map(ImageMapper::convertImageEntityToImageDto).collect(Collectors.toList());
	}
}
