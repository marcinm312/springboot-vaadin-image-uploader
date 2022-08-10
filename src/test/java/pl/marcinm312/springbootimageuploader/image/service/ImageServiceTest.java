package pl.marcinm312.springbootimageuploader.image.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;
import pl.marcinm312.springbootimageuploader.image.repository.ImageRepo;
import pl.marcinm312.springbootimageuploader.image.testdataprovider.ImageDataProvider;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ImageServiceTest {

	@Mock
	private CloudinaryService cloudinaryService;

	@Mock
	private ImageRepo imageRepo;

	@InjectMocks
	private ImageService imageService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);

		ImageEntity image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.save(any(ImageEntity.class))).willReturn(image);
		doNothing().when(imageRepo).delete(isA(ImageEntity.class));
	}

	@Test
	void deleteImageFromCloudinaryAndDBTest_simpleCase_success() throws Exception {
		ImageEntity image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.findById(image.getId())).willReturn(Optional.of(image));
		given(cloudinaryService.checkIfImageExistsInCloudinary(image)).willReturn(true);
		given(cloudinaryService.deleteImageFromCloudinary(image)).willReturn(true);

		ImageService.DeleteResult deleteResult = imageService.deleteImageFromCloudinaryAndDB(image.getId());

		Assertions.assertEquals(ImageService.DeleteResult.DELETED, deleteResult);
		verify(imageRepo, times(1)).delete(image);
		verify(cloudinaryService, times(1)).deleteImageFromCloudinary(image);
	}

	@Test
	void deleteImageFromCloudinaryAndDBTest_imageNotExistsInDB_infoThatImageNotExistsInDB() throws Exception {
		ImageEntity image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.findById(image.getId())).willReturn(Optional.empty());

		ImageService.DeleteResult deleteResult = imageService.deleteImageFromCloudinaryAndDB(image.getId());

		Assertions.assertEquals(ImageService.DeleteResult.NOT_EXISTS_IN_DB, deleteResult);
		verify(imageRepo, never()).delete(image);
		verify(cloudinaryService, never()).deleteImageFromCloudinary(image);
	}

	@Test
	void deleteImageFromCloudinaryAndDBTest_imageExistsInDBButNotExistsInCloudinary_success() throws Exception {
		ImageEntity image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.findById(image.getId())).willReturn(Optional.of(image));
		given(cloudinaryService.checkIfImageExistsInCloudinary(image)).willReturn(false);

		ImageService.DeleteResult deleteResult = imageService.deleteImageFromCloudinaryAndDB(image.getId());

		Assertions.assertEquals(ImageService.DeleteResult.DELETED, deleteResult);
		verify(imageRepo, times(1)).delete(image);
		verify(cloudinaryService, never()).deleteImageFromCloudinary(image);
	}

	@Test
	void deleteImageFromCloudinaryAndDBTest_imageNotDeletedFromCloudinary_imageNotDeletedFromDB() throws Exception {
		ImageEntity image = ImageDataProvider.prepareExampleImage();

		given(imageRepo.findById(image.getId())).willReturn(Optional.of(image));
		given(cloudinaryService.checkIfImageExistsInCloudinary(image)).willReturn(true);
		given(cloudinaryService.deleteImageFromCloudinary(image)).willReturn(false);

		ImageService.DeleteResult deleteResult = imageService.deleteImageFromCloudinaryAndDB(image.getId());

		Assertions.assertEquals(ImageService.DeleteResult.ERROR, deleteResult);
		verify(imageRepo, never()).delete(image);
		verify(cloudinaryService, times(1)).deleteImageFromCloudinary(image);
	}

	@Test()
	void deleteImageFromCloudinaryAndDBTest_errorWhileDeletingImageFromCloudinary_imageNotDeletedFromDB() throws Exception {
		ImageEntity image = ImageDataProvider.prepareExampleImage();
		String errorMessage = "Connection error";

		given(imageRepo.findById(image.getId())).willReturn(Optional.of(image));
		given(cloudinaryService.checkIfImageExistsInCloudinary(image)).willReturn(true);
		given(cloudinaryService.deleteImageFromCloudinary(image)).willThrow(new IOException(errorMessage));

		try {
			imageService.deleteImageFromCloudinaryAndDB(image.getId());
			Assertions.fail();
		} catch (Exception e) {
			Assertions.assertEquals(IOException.class, e.getClass());
			Assertions.assertEquals(errorMessage, e.getMessage());
		}

		verify(imageRepo, never()).delete(image);
		verify(cloudinaryService, times(1)).checkIfImageExistsInCloudinary(image);
		verify(cloudinaryService, times(1)).deleteImageFromCloudinary(image);
	}

	@Test()
	void deleteImageFromCloudinaryAndDBTest_errorWhileCheckingImageInCloudinary_imageNotDeletedFromDB() throws Exception {
		ImageEntity image = ImageDataProvider.prepareExampleImage();
		String errorMessage = "Connection error";

		given(imageRepo.findById(image.getId())).willReturn(Optional.of(image));
		given(cloudinaryService.checkIfImageExistsInCloudinary(image)).willThrow(new IOException(errorMessage));

		try {
			imageService.deleteImageFromCloudinaryAndDB(image.getId());
			Assertions.fail();
		} catch (Exception e) {
			Assertions.assertEquals(IOException.class, e.getClass());
			Assertions.assertEquals(errorMessage, e.getMessage());
		}

		verify(imageRepo, never()).delete(image);
		verify(cloudinaryService, times(1)).checkIfImageExistsInCloudinary(image);
		verify(cloudinaryService, never()).deleteImageFromCloudinary(image);
	}
}
