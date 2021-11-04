package pl.marcinm312.springbootimageuploader.image.gui;

import com.vaadin.flow.data.provider.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.env.Environment;
import org.vaadin.klaudeta.PaginatedGrid;
import pl.marcinm312.springbootimageuploader.image.model.Image;
import pl.marcinm312.springbootimageuploader.image.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.image.repository.ImageRepo;
import pl.marcinm312.springbootimageuploader.image.service.ImageService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.image.testdataprovider.ImageDataProvider;

import java.util.List;

import static org.mockito.BDDMockito.given;

class ImageManagementGuiTest {

	@Mock
	private ImageRepo imageRepo;

	@Spy
	private Environment environment;

	@InjectMocks
	private ImageService imageService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setup() {
		mockedVaadinUtils = Mockito.mockStatic(VaadinUtils.class);
		given(VaadinUtils.getAuthenticatedUserName()).willReturn("administrator");
		MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@Test
	void imageManagementGuiTest_simpleCase_success() {
		List<Image> expectedImageList = ImageDataProvider.prepareExampleImageList();
		given(imageRepo.findAllByOrderByIdDesc()).willReturn(expectedImageList);
		ImageManagementGui imageManagementGui = new ImageManagementGui(imageService);

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
		ImageManagementGui imageManagementGui = new ImageManagementGui(imageService);

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
		ImageManagementGui imageManagementGui = new ImageManagementGui(imageService);

		int receivedSize = imageManagementGui.grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedImageList.size(), receivedSize);
	}
}
