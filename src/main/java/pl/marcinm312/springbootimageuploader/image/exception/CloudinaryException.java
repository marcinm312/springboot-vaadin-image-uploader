package pl.marcinm312.springbootimageuploader.image.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CloudinaryException extends RuntimeException {

	public CloudinaryException(String message) {
		super(message);
	}
}
