package pl.marcinm312.springbootimageuploader.image.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;
import pl.marcinm312.springbootimageuploader.image.repository.ImageRepo;
import pl.marcinm312.springbootimageuploader.image.service.ImageService;
import pl.marcinm312.springbootimageuploader.image.testdataprovider.ImageDataProvider;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

class GalleryGuiTest {

	private final UI ui = new UI();

	@Mock
	private ImageRepo imageRepo;

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

	@ParameterizedTest
	@MethodSource("examplesOfImagesLists")
	void galleryGuiTest_commonUser_success(List<ImageEntity> expectedImageList) {

		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		given(VaadinUtils.getAuthenticatedUserName()).willReturn("user");
		given(VaadinUtils.isCurrentUserAdmin()).willReturn(false);

		GalleryGui galleryGui = new GalleryGui(imageService);

		int receivedSize = galleryGui.getCarousel().getSlides().length;
		Assertions.assertEquals(expectedImageList.size(), receivedSize);
		Assertions.assertFalse(galleryGui.getHorizontalMenu().getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}

	private static Stream<Arguments> examplesOfImagesLists() {

		return Stream.of(
			Arguments.of(ImageDataProvider.prepareExampleImageList()),
			Arguments.of(ImageDataProvider.prepareImageListWithEmptyUser()),
			Arguments.of(ImageDataProvider.prepareEmptyImageList())
		);
	}

	@ParameterizedTest
	@MethodSource("examplesOfImagesLists")
	void galleryGuiTest_adminUser_success(List<ImageEntity> expectedImageList) {

		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		given(VaadinUtils.getAuthenticatedUserName()).willReturn("admin");
		given(VaadinUtils.isCurrentUserAdmin()).willReturn(true);

		GalleryGui galleryGui = new GalleryGui(imageService);

		int receivedSize = galleryGui.getCarousel().getSlides().length;
		Assertions.assertEquals(expectedImageList.size(), receivedSize);
		Assertions.assertTrue(galleryGui.getHorizontalMenu().getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}
}
