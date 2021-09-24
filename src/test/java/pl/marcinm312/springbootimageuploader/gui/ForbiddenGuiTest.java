package pl.marcinm312.springbootimageuploader.gui;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import pl.marcinm312.springbootimageuploader.utils.VaadinUtils;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

class ForbiddenGuiTest {

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setUp() {
		mockedVaadinUtils = mockStatic(VaadinUtils.class);
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@Test
	void forbiddenGuiTest_simpleCase_success() {

		given(VaadinUtils.getAuthenticatedUserName()).willReturn("username");
		ForbiddenGui forbiddenGui = new ForbiddenGui();
		long receivedChildrenSize = forbiddenGui.getChildren().count();
		Assertions.assertEquals(2, receivedChildrenSize);
	}
}
