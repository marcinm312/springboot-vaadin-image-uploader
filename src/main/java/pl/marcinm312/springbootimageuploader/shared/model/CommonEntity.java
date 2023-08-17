package pl.marcinm312.springbootimageuploader.shared.model;

import java.time.LocalDateTime;

public interface CommonEntity {

	Long getId();
	LocalDateTime getCreatedAt();
	LocalDateTime getUpdatedAt();
}
