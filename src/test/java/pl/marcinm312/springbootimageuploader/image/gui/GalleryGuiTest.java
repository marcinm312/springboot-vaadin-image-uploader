package pl.marcinm312.springbootimageuploader.image.gui;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.env.Environment;
import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;
import pl.marcinm312.springbootimageuploader.image.repository.ImageRepo;
import pl.marcinm312.springbootimageuploader.image.service.ImageService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.image.testdataprovider.ImageDataProvider;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

class GalleryGuiTest {

	private final UI ui = new UI();

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
		MockitoAnnotations.openMocks(this);

		UI.setCurrent(ui);
		VaadinSession session = Mockito.mock(VaadinSession.class);
		Mockito.when(session.hasLock()).thenReturn(true);
		ui.getInternals().setSession(session);
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
		UI.setCurrent(null);
	}

	@Test
	void galleryGuiTest_simpleCase_success() {
		List<ImageEntity> expectedImageList = ImageDataProvider.prepareExampleImageList();
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
	void galleryGuiTest_imageListWithEmptyUser_success() {
		List<ImageEntity> expectedImageList = ImageDataProvider.prepareImageListWithEmptyUser();
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
		List<ImageEntity> expectedImageList = ImageDataProvider.prepareEmptyImageList();
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
		List<ImageEntity> expectedImageList = ImageDataProvider.prepareExampleImageList();
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
		List<ImageEntity> expectedImageList = ImageDataProvider.prepareEmptyImageList();
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
