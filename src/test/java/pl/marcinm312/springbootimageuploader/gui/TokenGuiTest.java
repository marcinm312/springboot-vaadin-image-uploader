package pl.marcinm312.springbootimageuploader.gui;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.Token;
import pl.marcinm312.springbootimageuploader.repo.AppUserRepo;
import pl.marcinm312.springbootimageuploader.repo.TokenRepo;
import pl.marcinm312.springbootimageuploader.service.UserService;
import pl.marcinm312.springbootimageuploader.testdataprovider.TokenDataProvider;
import pl.marcinm312.springbootimageuploader.testdataprovider.UserDataProvider;
import pl.marcinm312.springbootimageuploader.utils.VaadinUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TokenGuiTest {

	@Mock
	private AppUserRepo appUserRepo;

	@Mock
	private TokenRepo tokenRepo;

	@InjectMocks
	private UserService userService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setUp() {
		mockedVaadinUtils = mockStatic(VaadinUtils.class);
		MockitoAnnotations.openMocks(this);

		AppUser activatedAppUser = UserDataProvider.prepareExampleActivatedUserWithEncodedPassword();
		given(appUserRepo.save(any(AppUser.class))).willReturn(activatedAppUser);
		doNothing().when(tokenRepo).delete(isA(Token.class));
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@Test
	void tokenGuiTest_simpleCase_userActivated() {
		Token foundToken = TokenDataProvider.prepareExampleToken();
		String exampleExistingTokenValue = "123456-123-123-1234";
		given(tokenRepo.findByValue(exampleExistingTokenValue)).willReturn(Optional.of(foundToken));
		given(VaadinUtils.getParamValueFromUrlQuery("value")).willReturn(exampleExistingTokenValue);

		TokenGui tokenGui = new TokenGui(userService);

		String receivedMessage = tokenGui.h1.getText();
		Assertions.assertEquals("User activated", receivedMessage);
		verify(tokenRepo, times(1)).delete(foundToken);
		verify(appUserRepo, times(1)).save(any(AppUser.class));
	}

	@Test
	void tokenGuiTest_tokenNotFound_userNotActivated() {
		String exampleNotExistingTokenValue = "000-000-000";
		given(tokenRepo.findByValue(exampleNotExistingTokenValue)).willReturn(Optional.empty());
		given(VaadinUtils.getParamValueFromUrlQuery("value")).willReturn(exampleNotExistingTokenValue);

		TokenGui tokenGui = new TokenGui(userService);

		String receivedMessage = tokenGui.h1.getText();
		Assertions.assertEquals("Token not found", receivedMessage);
		verify(tokenRepo, never()).delete(any(Token.class));
		verify(appUserRepo, never()).save(any(AppUser.class));
	}

	@Test
	void tokenGuiTest_nullTokenValue_userNotActivated() {
		given(VaadinUtils.getParamValueFromUrlQuery("value")).willReturn(null);

		TokenGui tokenGui = new TokenGui(userService);

		String receivedMessage = tokenGui.h1.getText();
		Assertions.assertEquals("Error getting token value", receivedMessage);
		verify(tokenRepo, never()).delete(any(Token.class));
		verify(appUserRepo, never()).save(any(AppUser.class));
	}
}
