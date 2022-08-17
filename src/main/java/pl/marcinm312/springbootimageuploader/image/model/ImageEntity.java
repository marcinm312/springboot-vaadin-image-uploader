package pl.marcinm312.springbootimageuploader.image.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.io.FilenameUtils;
import pl.marcinm312.springbootimageuploader.shared.model.AuditModel;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@SuperBuilder
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

	public String getPublicId() {
		String[] splitAddress = getImageAddress().split("/");
		String fileName = splitAddress[splitAddress.length - 1];
		return FilenameUtils.removeExtension(fileName);
	}
}
