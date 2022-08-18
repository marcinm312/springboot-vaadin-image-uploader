package pl.marcinm312.springbootimageuploader.image.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageDto {

	private Long id;
	private String imageAddress;
	private String publicId;
	private String username;
	private String createdAt;
	private String updatedAt;

	public String getCompressedImageAddress(int imageHeight) {
		String[] splitAddress = getImageAddress().split("/");
		String target = splitAddress[splitAddress.length - 2];
		return getImageAddress().replace(target, "h_" + imageHeight + ",f_auto/q_100");
	}

	public String getAutoCompressedImageAddress() {
		String[] splitAddress = getImageAddress().split("/");
		String target = splitAddress[splitAddress.length - 2];
		return getImageAddress().replace(target, "h_700,f_auto/q_auto:best");
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ImageDto)) return false;

		ImageDto imageDto = (ImageDto) o;

		if (getId() != null ? !getId().equals(imageDto.getId()) : imageDto.getId() != null) return false;
		if (getImageAddress() != null ? !getImageAddress().equals(imageDto.getImageAddress()) : imageDto.getImageAddress() != null)
			return false;
		if (getPublicId() != null ? !getPublicId().equals(imageDto.getPublicId()) : imageDto.getPublicId() != null)
			return false;
		if (getUsername() != null ? !getUsername().equals(imageDto.getUsername()) : imageDto.getUsername() != null)
			return false;
		return getCreatedAt() != null ? getCreatedAt().equals(imageDto.getCreatedAt()) : imageDto.getCreatedAt() == null;
	}

	@Override
	public final int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + (getImageAddress() != null ? getImageAddress().hashCode() : 0);
		result = 31 * result + (getPublicId() != null ? getPublicId().hashCode() : 0);
		result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
		result = 31 * result + (getCreatedAt() != null ? getCreatedAt().hashCode() : 0);
		return result;
	}
}
