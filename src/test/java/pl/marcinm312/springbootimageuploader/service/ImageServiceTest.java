package pl.marcinm312.springbootimageuploader.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.marcinm312.springbootimageuploader.model.Image;
import pl.marcinm312.springbootimageuploader.repo.ImageRepo;
import pl.marcinm312.springbootimageuploader.testdataprovider.ImageDataProvider;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ImageServiceTest {

	@Mock
	CloudinaryService cloudinaryService;

	@Mock
	ImageRepo imageRepo;

	@InjectMocks
	ImageService imageService;

	@BeforeEach
	void setup() throws Exception {
		MockitoAnnotations.openMocks(this);

		Image image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.save(any(Image.class))).willReturn(ImageDataProvider.prepareExampleImage());
		doNothing().when(imageRepo).delete(isA(Image.class));
		given(cloudinaryService.deleteImageFromCloudinary(image)).willReturn(null);
	}

	@Test
	void deleteImageFromCloudinaryAndDBTest_simpleCase_success() throws Exception {
		Image image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.findById(image.getId())).willReturn(Optional.of(image));
		given(cloudinaryService.checkIfImageExistsInCloudinary(image)).willReturn(true);
		given(cloudinaryService.checkDeleteFromCloudinaryResult(eq(image), isNull())).willReturn(true);

		boolean deleteResult = imageService.deleteImageFromCloudinaryAndDB(image.getId());

		Assertions.assertTrue(deleteResult);
		verify(imageRepo, times(1)).delete(image);
		verify(cloudinaryService, times(1)).deleteImageFromCloudinary(image);
	}
}