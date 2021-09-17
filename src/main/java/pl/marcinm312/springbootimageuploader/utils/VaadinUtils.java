package pl.marcinm312.springbootimageuploader.utils;

import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;

public class VaadinUtils {

	private VaadinUtils() {

	}

	public static String getUriString() {
		VaadinServletRequest request = (VaadinServletRequest) VaadinService.getCurrentRequest();
		return request.getRequestURL().toString().replace("/register", "");
	}
}
