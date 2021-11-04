package pl.marcinm312.springbootimageuploader.image.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.*;
import org.mockito.*;
import pl.marcinm312.springbootimageuploader.user.model.AppUser;
import pl.marcinm312.springbootimageuploader.user.repository.AppUserRepo;
import pl.marcinm312.springbootimageuploader.image.service.ImageService;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

class UploadGuiTest {

	private final UI ui = new UI();

	@Mock
	private AppUserRepo appUserRepo;

	@InjectMocks
	private UserService userService;

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
		AppUser loggedUser = UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
		String oldLogin = loggedUser.getUsername();

		given(VaadinUtils.getAuthenticatedUserName()).willReturn(oldLogin);
		given(appUserRepo.findByUsername(oldLogin)).willReturn(Optional.of(loggedUser));

		UploadGui uploadGui = new UploadGui(imageService, userService);

		long receivedChildrenSize = uploadGui.getChildren().count();
		Assertions.assertEquals(4, receivedChildrenSize);
	}
}
