package pl.marcinm312.springbootimageuploader.image.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.io.FilenameUtils;
import pl.marcinm312.springbootimageuploader.shared.model.AuditModel;
import pl.marcinm312.springbootimageuploader.shared.model.CommonEntityWithUser;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@SuperBuilder
@Entity
@Table(name = "image")
public class ImageEntity extends AuditModel implements CommonEntityWithUser {

	@Id
	@GeneratedValue(generator = "image_generator")
	@SequenceGenerator(name = "image_generator", sequenceName = "image_id_seq", allocationSize = 1)
	private Long id;

	private String imageAddress;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@ToString.Exclude
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
