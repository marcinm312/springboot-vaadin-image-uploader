package pl.marcinm312.springbootimageuploader.image.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.marcinm312.springbootimageuploader.image.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageMapper {

	public static ImageDto convertImageEntityToImageDto(ImageEntity image) {

		UserEntity user = image.getUser();
		String username = user != null ? user.getUsername() : null;
		return ImageDto.builder()
				.id(image.getId())
				.imageAddress(image.getImageAddress())
				.publicId(image.getPublicId())
				.createdAt(getDateAsString(image.getCreatedAt()))
				.updatedAt(getDateAsString(image.getUpdatedAt()))
				.username(username)
				.build();
	}

	public static List<ImageDto> convertImageEntityListToImageDtoList(List<ImageEntity> imageList) {
		return imageList.stream().map(ImageMapper::convertImageEntityToImageDto).collect(Collectors.toList());
	}

	private static String getDateAsString(LocalDateTime date) {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(date);
	}
}
