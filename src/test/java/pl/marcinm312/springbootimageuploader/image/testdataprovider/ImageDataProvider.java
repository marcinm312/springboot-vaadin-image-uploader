package pl.marcinm312.springbootimageuploader.image.testdataprovider;

import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageDataProvider {

	private static final String IMAGE_URL = "https://res.cloudinary.com/test/image/upload/v1111222233/test123.jpg";

	public static List<ImageEntity> prepareExampleImageList() {
		List<ImageEntity> images = new ArrayList<>();
		images.add(new ImageEntity(1L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new ImageEntity(2L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new ImageEntity(3L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new ImageEntity(4L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new ImageEntity(5L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new ImageEntity(6L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new ImageEntity(7L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new ImageEntity(8L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new ImageEntity(9L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		return images;
	}

	public static List<ImageEntity> prepareImageListWithEmptyUser() {
		List<ImageEntity> images = new ArrayList<>();
		images.add(new ImageEntity(1L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new ImageEntity(2L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new ImageEntity(3L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new ImageEntity(4L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new ImageEntity(5L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new ImageEntity(6L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new ImageEntity(7L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new ImageEntity(8L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new ImageEntity(9L, IMAGE_URL, null, new Date(), new Date()));
		return images;
	}

	public static List<ImageEntity> prepareEmptyImageList() {
		return new ArrayList<>();
	}

	public static ImageEntity prepareExampleImage() {
		return new ImageEntity(1L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date());
	}
}
