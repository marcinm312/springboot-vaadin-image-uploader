package pl.marcinm312.springbootimageuploader.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TokenNotFoundException extends RuntimeException {

	public TokenNotFoundException() {
		super("Token not found");
	}
}
