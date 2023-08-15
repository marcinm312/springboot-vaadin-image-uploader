package pl.marcinm312.springbootimageuploader.image.gui;

import com.vaadin.flow.data.provider.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.vaadin.klaudeta.PaginatedGrid;
import pl.marcinm312.springbootimageuploader.image.model.ImageEntity;
import pl.marcinm312.springbootimageuploader.image.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.image.repository.ImageRepo;
import pl.marcinm312.springbootimageuploader.image.service.ImageService;
import pl.marcinm312.springbootimageuploader.image.testdataprovider.ImageDataProvider;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;

class ImageManagementGuiTest {

	@Mock
	private ImageRepo imageRepo;

	@InjectMocks
	private ImageService imageService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setup() {

		mockedVaadinUtils = Mockito.mockStatic(VaadinUtils.class);
		given(VaadinUtils.getAuthenticatedUserName()).willReturn("admin");
		MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@ParameterizedTest
	@MethodSource("examplesOfImagesLists")
	void imageManagementGuiTest_notEmptyImagesList_success(List<ImageEntity> expectedImageList) {

		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);

		ImageManagementGui imageManagementGui = new ImageManagementGui(imageService);

		PaginatedGrid<ImageDto> grid = imageManagementGui.getGrid();
		int receivedNormalSize = imageManagementGui.getGrid().getDataProvider().size(new Query<>());
		Assertions.assertEquals(5, receivedNormalSize);

		grid.setPageSize(expectedImageList.size() + 5);
		int receivedSize = grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);
	}

	private static Stream<Arguments> examplesOfImagesLists() {

		return Stream.of(
				Arguments.of(ImageDataProvider.prepareExampleImageList()),
				Arguments.of(ImageDataProvider.prepareImageListWithEmptyUser())
		);
	}

	@Test
	void imageManagementGuiTest_emptyImageList_success() {

		List<ImageEntity> expectedImageList = ImageDataProvider.prepareEmptyImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);

		ImageManagementGui imageManagementGui = new ImageManagementGui(imageService);

		int receivedSize = imageManagementGui.getGrid().getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);
	}
}
