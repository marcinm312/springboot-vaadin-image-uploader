package pl.marcinm312.springbootimageuploader.shared.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.model.enums.Role;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VaadinUtils {

	public static String getUriString() {
		VaadinServletRequest request = (VaadinServletRequest) VaadinService.getCurrentRequest();
		return request.getRequestURL().toString();
	}

	public static String getAuthenticatedUserName() {
		Authentication authentication = getCurrentAuthentication();
		return authentication.getName();
	}

	public static UserEntity getCurrentUser() {
		Authentication authentication = getCurrentAuthentication();
		return (UserEntity) authentication.getPrincipal();
	}

	public static boolean isCurrentUserAdmin() {
		return Role.ROLE_ADMIN == getCurrentUser().getRole();
	}

	public static void showNotification(String notificationText) {
		Notification.show(notificationText, 5000, Notification.Position.MIDDLE);
	}

	public static String getParamValueFromUrlQuery(String queryParamName) {
		return UI.getCurrent().getInternals().getLastHandledLocation().getQueryParameters().getParameters()
				.get(queryParamName).get(0);
	}

	private static Authentication getCurrentAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
}
