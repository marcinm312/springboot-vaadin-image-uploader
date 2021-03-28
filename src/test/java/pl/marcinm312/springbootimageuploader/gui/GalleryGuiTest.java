package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.data.provider.Query;
import org.springframework.core.env.Environment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.vaadin.klaudeta.PaginatedGrid;
import pl.marcinm312.springbootimageuploader.model.Image;
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

		PaginatedGrid<Image> grid = galleryGui.grid;
		int receivedNormalSize = galleryGui.grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(1, receivedNormalSize);

		grid.setPageSize(expectedImageList.size() + 5);
		int receivedSize = grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);
	}

	@Test
	void galleryGuiTest_emptyImageList_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareEmptyImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		GalleryGui galleryGui = getGalleryGuiWithModifiedMethod();

		int receivedSize = galleryGui.grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);
	}

	private GalleryGui getGalleryGuiWithModifiedMethod() {
		return new GalleryGui(imageService) {
			@Override
			protected String getAuthenticationName() {
				return "user";
			}

			@Override
			protected boolean isUserAdmin() {
				return false;
			}
		};
	}
}
