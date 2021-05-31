package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.data.provider.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.env.Environment;
import org.vaadin.klaudeta.PaginatedGrid;
import pl.marcinm312.springbootimageuploader.model.Image;
import pl.marcinm312.springbootimageuploader.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.repo.ImageRepo;
import pl.marcinm312.springbootimageuploader.service.ImageService;
import pl.marcinm312.springbootimageuploader.testdataprovider.ImageDataProvider;

import java.util.List;

import static org.mockito.BDDMockito.given;

class GalleryGuiTest {

	@Mock
	ImageRepo imageRepo;

	@Spy
	Environment environment;

	@InjectMocks
	ImageService imageService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void galleryGuiTest_simpleCase_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareExampleImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryGuiWithModifiedMethod();

		PaginatedGrid<ImageDto> grid = galleryGui.grid;
		int receivedNormalSize = galleryGui.grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(1, receivedNormalSize);

		grid.setPageSize(expectedImageList.size() + 5);
		int receivedSize = grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);

		Assertions.assertFalse(galleryGui.horizontalMenu.getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}

	@Test
	void galleryGuiTest_imageListWithEmptyUser_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareImageListWithEmptyUser();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryGuiWithModifiedMethod();

		PaginatedGrid<ImageDto> grid = galleryGui.grid;
		int receivedNormalSize = galleryGui.grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(1, receivedNormalSize);

		grid.setPageSize(expectedImageList.size() + 5);
		int receivedSize = grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);

		Assertions.assertFalse(galleryGui.horizontalMenu.getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}

	@Test
	void galleryGuiTest_emptyImageList_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareEmptyImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryGuiWithModifiedMethod();

		int receivedSize = galleryGui.grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);

		Assertions.assertFalse(galleryGui.horizontalMenu.getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}

	@Test
	void galleryGuiTest_simpleCaseWithAdmin_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareExampleImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryAdminGuiWithModifiedMethod();

		PaginatedGrid<ImageDto> grid = galleryGui.grid;
		int receivedNormalSize = galleryGui.grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(1, receivedNormalSize);

		grid.setPageSize(expectedImageList.size() + 5);
		int receivedSize = grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);

		Assertions.assertTrue(galleryGui.horizontalMenu.getChildren()
				.anyMatch(t -> ((Anchor) t).getText().equals("Image management")));
	}

	@Test
	void galleryGuiTest_emptyImageListWithAdmin_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareEmptyImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryAdminGuiWithModifiedMethod();

		int receivedSize = galleryGui.grid.getDataProvider().size(new Query<>());
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
