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
import pl.marcinm312.springbootimageuploader.user.model.MailChangeTokenEntity;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.repository.MailChangeTokenRepo;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;
import pl.marcinm312.springbootimageuploader.user.service.UserService;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.MailChangeTokenDataProvider;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class MailChangeTokenGuiTest {

	@Mock
	private UserRepo userRepo;

	@Mock
	private MailChangeTokenRepo mailChangeTokenRepo;

	@InjectMocks
	private UserService userService;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	private final UserEntity loggedUser = UserDataProvider.prepareExampleGoodUserWithEncodedPassword();

	@BeforeEach
	void setUp() {

		mockedVaadinUtils = mockStatic(VaadinUtils.class);
		MockitoAnnotations.openMocks(this);

		given(VaadinUtils.getCurrentUser()).willReturn(loggedUser);
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@Test
	void mailChangeTokenGuiTest_simpleCase_userActivated() {

		MailChangeTokenEntity foundToken = MailChangeTokenDataProvider.prepareExampleToken();
		String exampleExistingTokenValue = "123456-123-123-1234";
		given(mailChangeTokenRepo.findByValueAndUsername(exampleExistingTokenValue, loggedUser.getUsername()))
				.willReturn(Optional.of(foundToken));
		given(VaadinUtils.getParamValueFromCurrentUrlQuery("value")).willReturn(exampleExistingTokenValue);
		loggedUser.setEmail(foundToken.getNewEmail());
		given(userRepo.save(any(UserEntity.class))).willReturn(loggedUser);

		MailChangeTokenGui mailChangeTokenGui = new MailChangeTokenGui(userService);

		String receivedMessage = mailChangeTokenGui.getH1().getText();
		Assertions.assertEquals("Your email has been changed", receivedMessage);
		String receivedParagraph = mailChangeTokenGui.getParagraph().getText();
		Assertions.assertEquals("Your new email address: " + foundToken.getNewEmail(), receivedParagraph);
		verify(mailChangeTokenRepo, times(1)).deleteByUser(any(UserEntity.class));
		verify(userRepo, times(1)).save(any(UserEntity.class));
	}

	@Test
	void mailChangeTokenGuiTest_tokenNotFound_userNotActivated() {

		String exampleNotExistingTokenValue = "000-000-000";
		given(mailChangeTokenRepo.findByValueAndUsername(exampleNotExistingTokenValue, loggedUser.getUsername()))
				.willReturn(Optional.empty());
		given(VaadinUtils.getParamValueFromCurrentUrlQuery("value")).willReturn(exampleNotExistingTokenValue);

		MailChangeTokenGui mailChangeTokenGui = new MailChangeTokenGui(userService);

		String receivedMessage = mailChangeTokenGui.getH1().getText();
		Assertions.assertEquals("Token not found", receivedMessage);
		verify(mailChangeTokenRepo, never()).deleteByUser(any(UserEntity.class));
		verify(userRepo, never()).save(any(UserEntity.class));
	}

	@Test
	void mailChangeTokenGuiTest_nullTokenValue_userNotActivated() {

		given(VaadinUtils.getParamValueFromCurrentUrlQuery("value")).willReturn(null);

		MailChangeTokenGui mailChangeTokenGui = new MailChangeTokenGui(userService);

		String receivedMessage = mailChangeTokenGui.getH1().getText();
		Assertions.assertEquals("Error getting token value", receivedMessage);
		verify(mailChangeTokenRepo, never()).deleteByUser(any(UserEntity.class));
		verify(userRepo, never()).save(any(UserEntity.class));
	}

	@Test
	void mailChangeTokenGuiTest_tokenValueException_userNotActivated() {

		doThrow(new RuntimeException("Exception")).when(VaadinUtils.class);

		MailChangeTokenGui mailChangeTokenGui = new MailChangeTokenGui(userService);

		String receivedMessage = mailChangeTokenGui.getH1().getText();
		Assertions.assertEquals("Error getting token value", receivedMessage);
		verify(mailChangeTokenRepo, never()).deleteByUser(any(UserEntity.class));
		verify(userRepo, never()).save(any(UserEntity.class));
	}
}
