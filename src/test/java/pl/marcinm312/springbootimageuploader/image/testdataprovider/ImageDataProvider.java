package pl.marcinm312.springbootimageuploader.image.testdataprovider;

import pl.marcinm312.springbootimageuploader.image.model.Image;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageDataProvider {

	private static final String IMAGE_URL = "https://res.cloudinary.com/test/image/upload/v1111222233/test123.jpg";

	public static List<Image> prepareExampleImageList() {
		List<Image> images = new ArrayList<>();
		images.add(new Image(1L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new Image(2L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new Image(3L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new Image(4L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new Image(5L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new Image(6L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new Image(7L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new Image(8L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		images.add(new Image(9L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date()));
		return images;
	}

	public static List<Image> prepareImageListWithEmptyUser() {
		List<Image> images = new ArrayList<>();
		images.add(new Image(1L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new Image(2L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new Image(3L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new Image(4L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new Image(5L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new Image(6L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new Image(7L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new Image(8L, IMAGE_URL, null, new Date(), new Date()));
		images.add(new Image(9L, IMAGE_URL, null, new Date(), new Date()));
		return images;
	}

	public static List<Image> prepareEmptyImageList() {
		return new ArrayList<>();
	}

	public static Image prepareExampleImage() {
		return new Image(1L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator(), new Date(), new Date());
	}
}
