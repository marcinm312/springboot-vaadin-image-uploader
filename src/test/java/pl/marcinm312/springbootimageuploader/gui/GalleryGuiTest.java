package pl.marcinm312.springbootimageuploader.gui;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.vaadin.flow.component.html.Anchor;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.core.env.Environment;
import pl.marcinm312.springbootimageuploader.model.image.Image;
import pl.marcinm312.springbootimageuploader.repo.ImageRepo;
import pl.marcinm312.springbootimageuploader.service.ImageService;
import pl.marcinm312.springbootimageuploader.testdataprovider.ImageDataProvider;
import pl.marcinm312.springbootimageuploader.utils.VaadinUtils;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

class GalleryGuiTest {

	@Mock
	private ImageRepo imageRepo;

	@Spy
	private Environment environment;

	@InjectMocks
	private ImageService imageService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setUp() {
		mockedVaadinUtils = mockStatic(VaadinUtils.class);
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void galleryGuiTest_simpleCase_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareExampleImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		given(VaadinUtils.getAuthenticatedUserName()).willReturn("user");
		given(VaadinUtils.isCurrentUserAdmin()).willReturn(false);
		GalleryGui galleryGui = new GalleryGui(imageService);

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
		given(VaadinUtils.getAuthenticatedUserName()).willReturn("user");
		given(VaadinUtils.isCurrentUserAdmin()).willReturn(false);
		GalleryGui galleryGui = new GalleryGui(imageService);

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
		given(VaadinUtils.getAuthenticatedUserName()).willReturn("user");
		given(VaadinUtils.isCurrentUserAdmin()).willReturn(false);
		GalleryGui galleryGui = new GalleryGui(imageService);

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
		given(VaadinUtils.getAuthenticatedUserName()).willReturn("user");
		given(VaadinUtils.isCurrentUserAdmin()).willReturn(false);
		GalleryGui galleryGui = new GalleryGui(imageService);

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
		given(VaadinUtils.getAuthenticatedUserName()).willReturn("administrator");
		given(VaadinUtils.isCurrentUserAdmin()).willReturn(true);
		GalleryGui galleryGui = new GalleryGui(imageService);

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
		given(VaadinUtils.getAuthenticatedUserName()).willReturn("administrator");
		given(VaadinUtils.isCurrentUserAdmin()).willReturn(true);
		GalleryGui galleryGui = new GalleryGui(imageService);

		Carousel carousel = galleryGui.carousel;
		int receivedSize = carousel.getSlides().length;
		Assertions.assertEquals(expectedImageList.size(), receivedSize);

		Assertions.assertTrue(galleryGui.horizontalMenu.getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}
}
