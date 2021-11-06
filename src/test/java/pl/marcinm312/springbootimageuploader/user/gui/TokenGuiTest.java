package pl.marcinm312.springbootimageuploader.user.gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.model.TokenEntity;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.TokenDataProvider;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;
import pl.marcinm312.springbootimageuploader.user.repository.TokenRepo;
import pl.marcinm312.springbootimageuploader.user.service.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TokenGuiTest {

	@Mock
	private UserRepo userRepo;

	@Mock
	private TokenRepo tokenRepo;

	@InjectMocks
	private UserService userService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setUp() {
		mockedVaadinUtils = mockStatic(VaadinUtils.class);
		MockitoAnnotations.openMocks(this);

		UserEntity activatedUser = UserDataProvider.prepareExampleActivatedUserWithEncodedPassword();
		given(userRepo.save(any(UserEntity.class))).willReturn(activatedUser);
		doNothing().when(tokenRepo).delete(isA(TokenEntity.class));
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@Test
	void tokenGuiTest_simpleCase_userActivated() {
		TokenEntity foundToken = TokenDataProvider.prepareExampleToken();
		String exampleExistingTokenValue = "123456-123-123-1234";
		given(tokenRepo.findByValue(exampleExistingTokenValue)).willReturn(Optional.of(foundToken));
		given(VaadinUtils.getParamValueFromUrlQuery("value")).willReturn(exampleExistingTokenValue);

		TokenGui tokenGui = new TokenGui(userService);

		String receivedMessage = tokenGui.h1.getText();
		Assertions.assertEquals("User activated", receivedMessage);
		verify(tokenRepo, times(1)).delete(foundToken);
		verify(userRepo, times(1)).save(any(UserEntity.class));
	}

	@Test
	void tokenGuiTest_tokenNotFound_userNotActivated() {
		String exampleNotExistingTokenValue = "000-000-000";
		given(tokenRepo.findByValue(exampleNotExistingTokenValue)).willReturn(Optional.empty());
		given(VaadinUtils.getParamValueFromUrlQuery("value")).willReturn(exampleNotExistingTokenValue);

		TokenGui tokenGui = new TokenGui(userService);

		String receivedMessage = tokenGui.h1.getText();
		Assertions.assertEquals("Token not found", receivedMessage);
		verify(tokenRepo, never()).delete(any(TokenEntity.class));
		verify(userRepo, never()).save(any(UserEntity.class));
	}

	@Test
	void tokenGuiTest_nullTokenValue_userNotActivated() {
		given(VaadinUtils.getParamValueFromUrlQuery("value")).willReturn(null);

		TokenGui tokenGui = new TokenGui(userService);

		String receivedMessage = tokenGui.h1.getText();
		Assertions.assertEquals("Error getting token value", receivedMessage);
		verify(tokenRepo, never()).delete(any(TokenEntity.class));
		verify(userRepo, never()).save(any(UserEntity.class));
	}
}
