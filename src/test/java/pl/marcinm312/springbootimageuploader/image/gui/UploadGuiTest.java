package pl.marcinm312.springbootimageuploader.image.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.marcinm312.springbootimageuploader.image.service.ImageService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

class UploadGuiTest {

	private final UI ui = new UI();

	@Mock
	private UserRepo userRepo;

	@Mock
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
	void uploadGuiTest_initView_success() {

		UserEntity loggedUser = UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
		String login = loggedUser.getUsername();
		given(VaadinUtils.getAuthenticatedUserName()).willReturn(login);
		given(VaadinUtils.getCurrentUser()).willReturn(loggedUser);
		given(userRepo.findByUsername(login)).willReturn(Optional.of(loggedUser));

		UploadGui uploadGui = new UploadGui(imageService);

		long receivedChildrenSize = uploadGui.getChildren().count();
		Assertions.assertEquals(4, receivedChildrenSize);
	}
}
