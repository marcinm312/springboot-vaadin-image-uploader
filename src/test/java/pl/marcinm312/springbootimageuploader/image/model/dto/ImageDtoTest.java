package pl.marcinm312.springbootimageuploader.image.model.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.marcinm312.springbootimageuploader.image.model.Image;
import pl.marcinm312.springbootimageuploader.image.model.ImageMapper;
import pl.marcinm312.springbootimageuploader.image.testdataprovider.ImageDataProvider;

class ImageDtoTest {

	@Test
	void getCompressedImageAddress_simpleCase_returnAddressToCompressedImage() {
		Image image = ImageDataProvider.prepareExampleImage();
		ImageDto imageDto = ImageMapper.convertImageToImageDto(image);
		String receivedCompressedImageAddress = imageDto.getCompressedImageAddress(100);
		String expectedCompressedImageAddress = "https://res.cloudinary.com/test/image/upload/h_100,f_auto/q_100/test123.jpg";
		Assertions.assertEquals(expectedCompressedImageAddress, receivedCompressedImageAddress);
	}

	@Test
	void getAutoCompressedImageAddress() {
		Image image = ImageDataProvider.prepareExampleImage();
		ImageDto imageDto = ImageMapper.convertImageToImageDto(image);
		String receivedCompressedImageAddress = imageDto.getAutoCompressedImageAddress();
		String expectedCompressedImageAddress = "https://res.cloudinary.com/test/image/upload/h_700,f_auto/q_auto:best/test123.jpg";
		Assertions.assertEquals(expectedCompressedImageAddress, receivedCompressedImageAddress);
	}

	@Test
	void equalsHashCode_differentCases() {
		EqualsVerifier.forClass(ImageDto.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
				.verify();
	}
}