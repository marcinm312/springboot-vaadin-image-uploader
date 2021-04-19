package pl.marcinm312.springbootimageuploader.testdataprovider;

import pl.marcinm312.springbootimageuploader.model.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageDataProvider {

	private static final String IMAGE_URL = "https://res.cloudinary.com/test/image/upload/v1111222233/test123.jpg";

	public static List<Image> prepareExampleImageList() {
		List<Image> images = new ArrayList<>();
		images.add(new Image(1L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator()));
		images.add(new Image(2L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator()));
		images.add(new Image(3L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator()));
		return images;
	}

	public static List<Image> prepareEmptyImageList() {
		return new ArrayList<>();
	}

	public static Image prepareExampleImage() {
		return new Image(1L, IMAGE_URL, UserDataProvider.prepareExampleGoodAdministrator());
	}
}
