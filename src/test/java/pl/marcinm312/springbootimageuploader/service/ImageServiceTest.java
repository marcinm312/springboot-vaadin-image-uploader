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
	void setup() {
		MockitoAnnotations.openMocks(this);

		Image image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.save(any(Image.class))).willReturn(image);
		doNothing().when(imageRepo).delete(isA(Image.class));
	}

	@Test
	void deleteImageFromCloudinaryAndDBTest_simpleCase_success() throws Exception {
		Image image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.findById(image.getId())).willReturn(Optional.of(image));
		given(cloudinaryService.checkIfImageExistsInCloudinary(image)).willReturn(true);
		given(cloudinaryService.deleteImageFromCloudinary(image)).willReturn(true);

		boolean deleteResult = imageService.deleteImageFromCloudinaryAndDB(image.getId());

		Assertions.assertTrue(deleteResult);
		verify(imageRepo, times(1)).delete(image);
		verify(cloudinaryService, times(1)).deleteImageFromCloudinary(image);
	}

	@Test
	void deleteImageFromCloudinaryAndDBTest_imageNotExistsInDB_imageNotDeleted() throws Exception {
		Image image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.findById(image.getId())).willReturn(Optional.empty());

		boolean deleteResult = imageService.deleteImageFromCloudinaryAndDB(image.getId());

		Assertions.assertFalse(deleteResult);
		verify(imageRepo, never()).delete(image);
		verify(cloudinaryService, never()).deleteImageFromCloudinary(image);
	}

	@Test
	void deleteImageFromCloudinaryAndDBTest_imageExistsInDBButNotExistsInCloudinary_success() throws Exception {
		Image image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.findById(image.getId())).willReturn(Optional.of(image));
		given(cloudinaryService.checkIfImageExistsInCloudinary(image)).willReturn(false);

		boolean deleteResult = imageService.deleteImageFromCloudinaryAndDB(image.getId());

		Assertions.assertTrue(deleteResult);
		verify(imageRepo, times(1)).delete(image);
		verify(cloudinaryService, never()).deleteImageFromCloudinary(image);
	}

	@Test
	void deleteImageFromCloudinaryAndDBTest_errorWhileDeletingImageFromCloudinary_imageNotDeletedFromDB() throws Exception {
		Image image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.findById(image.getId())).willReturn(Optional.of(image));
		given(cloudinaryService.checkIfImageExistsInCloudinary(image)).willReturn(true);
		given(cloudinaryService.deleteImageFromCloudinary(image)).willReturn(false);

		boolean deleteResult = imageService.deleteImageFromCloudinaryAndDB(image.getId());

		Assertions.assertFalse(deleteResult);
		verify(imageRepo, never()).delete(image);
		verify(cloudinaryService, times(1)).deleteImageFromCloudinary(image);
	}
}