package pl.marcinm312.springbootimageuploader.gui;

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

class ImageManagementGuiTest {

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
	void imageManagementGuiTest_simpleCase_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareExampleImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		ImageManagementGui imageManagementGui = getImageManagementGuiWithModifiedMethod();

		PaginatedGrid<ImageDto> grid = imageManagementGui.grid;
		int receivedNormalSize = imageManagementGui.grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(5, receivedNormalSize);

		grid.setPageSize(expectedImageList.size() + 5);
		int receivedSize = grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);
	}

	@Test
	void imageManagementGuiTest_imageListWithEmptyUser_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareImageListWithEmptyUser();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		ImageManagementGui imageManagementGui = getImageManagementGuiWithModifiedMethod();

		PaginatedGrid<ImageDto> grid = imageManagementGui.grid;
		int receivedNormalSize = imageManagementGui.grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(5, receivedNormalSize);

		grid.setPageSize(expectedImageList.size() + 5);
		int receivedSize = grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);
	}

	@Test
	void imageManagementGuiTest_emptyImageList_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareEmptyImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		ImageManagementGui imageManagementGui = getImageManagementGuiWithModifiedMethod();

		int receivedSize = imageManagementGui.grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);
	}

	private ImageManagementGui getImageManagementGuiWithModifiedMethod() {
		return new ImageManagementGui(imageService) {
			@Override
			String getAuthenticationName() {
				return "administrator";
			}
		};
	}
}