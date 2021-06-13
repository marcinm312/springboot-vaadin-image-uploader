package pl.marcinm312.springbootimageuploader.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.service.ImageService;
import pl.marcinm312.springbootimageuploader.service.UserService;
import pl.marcinm312.springbootimageuploader.testdataprovider.UserDataProvider;

class UploadGuiTest {

	private final UI ui = new UI();

	@Mock
	private ImageService imageService;

	@Mock
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		UI.setCurrent(ui);
		VaadinSession session = Mockito.mock(VaadinSession.class);
		Mockito.when(session.hasLock()).thenReturn(true);
		ui.getInternals().setSession(session);
	}

	@AfterEach
	void tearDown() {
		UI.setCurrent(null);
	}

	@Test
	void uploadGuiTest_initView_success() {

		UploadGui uploadGui = new UploadGui(imageService, userService) {
			@Override
			AppUser getAuthenticatedUser() {
				return UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
			}
		};
		long receivedChildrenSize = uploadGui.getChildren().count();
		Assertions.assertEquals(4, receivedChildrenSize);
	}
}