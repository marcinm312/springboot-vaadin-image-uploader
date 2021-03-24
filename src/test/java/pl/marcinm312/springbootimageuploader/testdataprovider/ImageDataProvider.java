package pl.marcinm312.springbootimageuploader.testdataprovider;

import pl.marcinm312.springbootimageuploader.model.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageDataProvider {

	private static final String IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Stary_Rynek_Domki_1.JPG/800px-Stary_Rynek_Domki_1.JPG";

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
}
