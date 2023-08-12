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
import pl.marcinm312.springbootimageuploader.user.model.ActivationTokenEntity;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.repository.ActivationTokenRepo;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.TokenDataProvider;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ActivationTokenGuiTest {

	@Mock
	private UserRepo userRepo;

	@Mock
	private ActivationTokenRepo activationTokenRepo;

	@InjectMocks
	private UserService userService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setUp() {

		mockedVaadinUtils = mockStatic(VaadinUtils.class);
		MockitoAnnotations.openMocks(this);

		UserEntity activatedUser = UserDataProvider.prepareExampleActivatedUserWithEncodedPassword();
		given(userRepo.save(any(UserEntity.class))).willReturn(activatedUser);
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@Test
	void tokenGuiTest_simpleCase_userActivated() {

		ActivationTokenEntity foundToken = TokenDataProvider.prepareExampleToken();
		String exampleExistingTokenValue = "123456-123-123-1234";
		given(activationTokenRepo.findByValue(exampleExistingTokenValue)).willReturn(Optional.of(foundToken));
		given(VaadinUtils.getParamValueFromCurrentUrlQuery("value")).willReturn(exampleExistingTokenValue);

		ActivationTokenGui activationTokenGui = new ActivationTokenGui(userService);

		String receivedMessage = activationTokenGui.h1.getText();
		Assertions.assertEquals("User activated", receivedMessage);
		verify(activationTokenRepo, times(1)).delete(foundToken);
		verify(userRepo, times(1)).save(any(UserEntity.class));
	}

	@Test
	void tokenGuiTest_tokenNotFound_userNotActivated() {

		String exampleNotExistingTokenValue = "000-000-000";
		given(activationTokenRepo.findByValue(exampleNotExistingTokenValue)).willReturn(Optional.empty());
		given(VaadinUtils.getParamValueFromCurrentUrlQuery("value")).willReturn(exampleNotExistingTokenValue);

		ActivationTokenGui activationTokenGui = new ActivationTokenGui(userService);

		String receivedMessage = activationTokenGui.h1.getText();
		Assertions.assertEquals("Token not found", receivedMessage);
		verify(activationTokenRepo, never()).delete(any(ActivationTokenEntity.class));
		verify(userRepo, never()).save(any(UserEntity.class));
	}

	@Test
	void tokenGuiTest_nullTokenValue_userNotActivated() {

		given(VaadinUtils.getParamValueFromCurrentUrlQuery("value")).willReturn(null);

		ActivationTokenGui activationTokenGui = new ActivationTokenGui(userService);

		String receivedMessage = activationTokenGui.h1.getText();
		Assertions.assertEquals("Error getting token value", receivedMessage);
		verify(activationTokenRepo, never()).delete(any(ActivationTokenEntity.class));
		verify(userRepo, never()).save(any(UserEntity.class));
	}
}
