package pl.marcinm312.springbootimageuploader.model.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.marcinm312.springbootimageuploader.model.Image;
import pl.marcinm312.springbootimageuploader.testdataprovider.ImageDataProvider;
import pl.marcinm312.springbootimageuploader.utils.ConvertUtils;

class ImageDtoTest {

	@Test
	void getCompressedImageAddress_simpleCase_returnAddressToCompressedImage() {
		Image image = ImageDataProvider.prepareExampleImage();
		ImageDto imageDto = ConvertUtils.convertImageToImageDto(image);
		String receivedCompressedImageAddress = imageDto.getCompressedImageAddress(100);
		String expectedCompressedImageAddress = "https://res.cloudinary.com/test/image/upload/h_100,f_auto/q_100/test123.jpg";
		Assertions.assertEquals(expectedCompressedImageAddress, receivedCompressedImageAddress);
	}

	@Test
	void getAutoCompressedImageAddress() {
		Image image = ImageDataProvider.prepareExampleImage();
		ImageDto imageDto = ConvertUtils.convertImageToImageDto(image);
		String receivedCompressedImageAddress = imageDto.getAutoCompressedImageAddress();
		String expectedCompressedImageAddress = "https://res.cloudinary.com/test/image/upload/h_700,f_auto/q_auto:best/test123.jpg";
		Assertions.assertEquals(expectedCompressedImageAddress, receivedCompressedImageAddress);
	}
}