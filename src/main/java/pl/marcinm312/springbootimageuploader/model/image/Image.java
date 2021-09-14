package pl.marcinm312.springbootimageuploader.model.image;

import org.apache.commons.io.FilenameUtils;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.AuditModel;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Image extends AuditModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String imageAddress;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private AppUser appUser;

	public Image(String imageAddress, AppUser appUser) {
		this.imageAddress = imageAddress;
		this.appUser = appUser;
	}

	public Image(Long id, String imageAddress, AppUser appUser, Date createdAt, Date updatedAt) {
		this.id = id;
		this.imageAddress = imageAddress;
		this.appUser = appUser;
		setCreatedAt(createdAt);
		setUpdatedAt(updatedAt);
	}

	public Image() {
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

	public AppUser getUser() {
		return appUser;
	}

	public void setUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public String getPublicId() {
		String[] splittedAddress = getImageAddress().split("/");
		String fileName = splittedAddress[splittedAddress.length - 1];
		return FilenameUtils.removeExtension(fileName);
	}
}