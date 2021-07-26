package pl.marcinm312.springbootimageuploader.gui;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.vaadin.flow.component.html.Anchor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.env.Environment;
import pl.marcinm312.springbootimageuploader.model.Image;
import pl.marcinm312.springbootimageuploader.repo.ImageRepo;
import pl.marcinm312.springbootimageuploader.service.ImageService;
import pl.marcinm312.springbootimageuploader.testdataprovider.ImageDataProvider;

import java.util.List;

import static org.mockito.BDDMockito.given;

class GalleryGuiTest {

	@Mock
	private ImageRepo imageRepo;

	@Spy
	private Environment environment;

	@InjectMocks
	private ImageService imageService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void galleryGuiTest_simpleCase_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareExampleImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryGuiWithModifiedMethod();

		Carousel carousel = galleryGui.carousel;
		int receivedSize = carousel.getSlides().length;
		Assertions.assertEquals(expectedImageList.size(), receivedSize);

		Assertions.assertFalse(galleryGui.horizontalMenu.getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}

	@Test
	void galleryGuiTest_differentCasesOfViewingImages_specificPaginationText() {
		List<Image> expectedImageList = ImageDataProvider.prepareExampleImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryGuiWithModifiedMethod();

		Assertions.assertEquals("Image 1 of 9", galleryGui.paginationText.getText());

		galleryGui.nextImageButton.click();
		Assertions.assertEquals("Image 2 of 9", galleryGui.paginationText.getText());

		galleryGui.prevImageButton.click();
		Assertions.assertEquals("Image 1 of 9", galleryGui.paginationText.getText());

		galleryGui.lastImageButton.click();
		Assertions.assertEquals("Image 9 of 9", galleryGui.paginationText.getText());

		galleryGui.firstImageButton.click();
		Assertions.assertEquals("Image 1 of 9", galleryGui.paginationText.getText());

		galleryGui.prevImageButton.click();
		Assertions.assertEquals("Image 9 of 9", galleryGui.paginationText.getText());

		galleryGui.nextImageButton.click();
		Assertions.assertEquals("Image 1 of 9", galleryGui.paginationText.getText());
	}

	@Test
	void galleryGuiTest_imageListWithEmptyUser_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareImageListWithEmptyUser();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryGuiWithModifiedMethod();

		Carousel carousel = galleryGui.carousel;
		int receivedSize = carousel.getSlides().length;
		Assertions.assertEquals(expectedImageList.size(), receivedSize);

		Assertions.assertFalse(galleryGui.horizontalMenu.getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}

	@Test
	void galleryGuiTest_emptyImageList_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareEmptyImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryGuiWithModifiedMethod();

		Carousel carousel = galleryGui.carousel;
		int receivedSize = carousel.getSlides().length;
		Assertions.assertEquals(expectedImageList.size(), receivedSize);

		Assertions.assertFalse(galleryGui.horizontalMenu.getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}

	@Test
	void galleryGuiTest_simpleCaseWithAdmin_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareExampleImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryAdminGuiWithModifiedMethod();

		Carousel carousel = galleryGui.carousel;
		int receivedSize = carousel.getSlides().length;
		Assertions.assertEquals(expectedImageList.size(), receivedSize);

		Assertions.assertTrue(galleryGui.horizontalMenu.getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}

	@Test
	void galleryGuiTest_emptyImageListWithAdmin_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareEmptyImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryAdminGuiWithModifiedMethod();

		Carousel carousel = galleryGui.carousel;
		int receivedSize = carousel.getSlides().length;
		Assertions.assertEquals(expectedImageList.size(), receivedSize);

		Assertions.assertTrue(galleryGui.horizontalMenu.getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}

	private GalleryGui getGalleryGuiWithModifiedMethod() {
		return new GalleryGui(imageService) {
			@Override
			String getAuthenticationName() {
				return "user";
			}

			@Override
			boolean isUserAdmin() {
				return false;
			}
		};
	}

	private GalleryGui getGalleryAdminGuiWithModifiedMethod() {
		return new GalleryGui(imageService) {
			@Override
			String getAuthenticationName() {
				return "administrator";
			}

			@Override
			boolean isUserAdmin() {
				return true;
			}
		};
	}
}
