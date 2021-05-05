package pl.marcinm312.springbootimageuploader.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.marcinm312.springbootimageuploader.testdataprovider.ImageDataProvider;

class ImageTest {

	@Test
	void getPublicId_simpleCase_extractedPublicIdFromUrl() {
		Image image = ImageDataProvider.prepareExampleImage();
		String receivedPublicId = image.getPublicId();
		String expectedPublicId = "test123";
		Assertions.assertEquals(expectedPublicId, receivedPublicId);
	}
}