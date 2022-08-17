package pl.marcinm312.springbootimageuploader.image.testdataprovider;

import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageDataProvider {

	private static final String IMAGE_URL = "https://res.cloudinary.com/test/image/upload/v1111222233/test123.jpg";

	public static List<ImageEntity> prepareExampleImageList() {
		List<ImageEntity> images = new ArrayList<>();
		images.add(buildImageEntity(1L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(buildImageEntity(2L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(buildImageEntity(3L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(buildImageEntity(4L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(buildImageEntity(5L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(buildImageEntity(6L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(buildImageEntity(7L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(buildImageEntity(8L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(buildImageEntity(9L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		return images;
	}

	public static List<ImageEntity> prepareImageListWithEmptyUser() {
		List<ImageEntity> images = new ArrayList<>();
		images.add(buildImageEntity(1L, IMAGE_URL, null, new Date(), new Date()));
		images.add(buildImageEntity(2L, IMAGE_URL, null, new Date(), new Date()));
		images.add(buildImageEntity(3L, IMAGE_URL, null, new Date(), new Date()));
		images.add(buildImageEntity(4L, IMAGE_URL, null, new Date(), new Date()));
		images.add(buildImageEntity(5L, IMAGE_URL, null, new Date(), new Date()));
		images.add(buildImageEntity(6L, IMAGE_URL, null, new Date(), new Date()));
		images.add(buildImageEntity(7L, IMAGE_URL, null, new Date(), new Date()));
		images.add(buildImageEntity(8L, IMAGE_URL, null, new Date(), new Date()));
		images.add(buildImageEntity(9L, IMAGE_URL, null, new Date(), new Date()));
		return images;
	}

	public static List<ImageEntity> prepareEmptyImageList() {
		return new ArrayList<>();
	}

	public static ImageEntity prepareExampleImage() {
		return buildImageEntity(1L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date());
	}

	private static ImageEntity buildImageEntity(Long id, String imageAddress, UserEntity user, Date createdAt, Date updatedAt) {
		return ImageEntity.builder()
				.id(id)
				.imageAddress(imageAddress)
				.user(user)
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.build();
	}
}
