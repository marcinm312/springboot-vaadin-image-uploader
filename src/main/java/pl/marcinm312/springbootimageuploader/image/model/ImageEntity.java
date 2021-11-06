package pl.marcinm312.springbootimageuploader.image.model;

import org.apache.commons.io.FilenameUtils;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.shared.model.AuditModel;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "image")
public class ImageEntity extends AuditModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String imageAddress;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	public ImageEntity(String imageAddress, UserEntity user) {
		this.imageAddress = imageAddress;
		this.user = user;
	}

	public ImageEntity(Long id, String imageAddress, UserEntity user, Date createdAt, Date updatedAt) {
		this.id = id;
		this.imageAddress = imageAddress;
		this.user = user;
		setCreatedAt(createdAt);
		setUpdatedAt(updatedAt);
	}

	public ImageEntity() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getImageAddress() {
		return imageAddress;
	}

	public void setImageAddress(String imageAddress) {
		this.imageAddress = imageAddress;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public String getPublicId() {
		String[] splittedAddress = getImageAddress().split("/");
		String fileName = splittedAddress[splittedAddress.length - 1];
		return FilenameUtils.removeExtension(fileName);
	}
}
