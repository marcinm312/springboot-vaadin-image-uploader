package pl.marcinm312.springbootimageuploader.shared.model;

import pl.marcinm312.springbootimageuploader.user.model.UserEntity;

public interface CommonEntityWithUser extends CommonEntity {

	UserEntity getUser();
}
