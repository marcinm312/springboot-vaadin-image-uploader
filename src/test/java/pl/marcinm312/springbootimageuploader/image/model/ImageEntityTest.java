package pl.marcinm312.springbootimageuploader.image.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.marcinm312.springbootimageuploader.image.testdataprovider.ImageDataProvider;

class ImageEntityTest {

	@Test
	void getPublicId_simpleCase_extractedPublicIdFromUrl() {

		ImageEntity image = ImageDataProvider.prepareExampleImage();
		String receivedPublicId = image.getPublicId();
		String expectedPublicId = "test123";
		Assertions.assertEquals(expectedPublicId, receivedPublicId);
	}
}
