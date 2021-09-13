package pl.marcinm312.springbootimageuploader.model.image.dto;

public class ImageDto {

	private final Long id;
	private String imageAddress;
	private String publicId;
	private String username;
	private String createdAt;
	private String updatedAt;

	public ImageDto(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getImageAddress() {
		return imageAddress;
	}

	public void setImageAddress(String imageAddress) {
		this.imageAddress = imageAddress;
	}

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getCompressedImageAddress(int imageHeight) {
		String[] splittedAddress = getImageAddress().split("/");
		String target = splittedAddress[splittedAddress.length - 2];
		return getImageAddress().replace(target, "h_" + imageHeight + ",f_auto/q_100");
	}

	public String getAutoCompressedImageAddress() {
		String[] splittedAddress = getImageAddress().split("/");
		String target = splittedAddress[splittedAddress.length - 2];
		return getImageAddress().replace(target, "h_700,f_auto/q_auto:best");
	}

	@Override
	public String toString() {
		return "ImageDto{" +
				"id=" + id +
				", imageAddress='" + imageAddress + '\'' +
				", publicId='" + publicId + '\'' +
				", username='" + username + '\'' +
				", createdAt='" + createdAt + '\'' +
				", updatedAt='" + updatedAt + '\'' +
				'}';
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
