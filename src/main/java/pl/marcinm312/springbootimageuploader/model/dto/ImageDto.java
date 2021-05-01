package pl.marcinm312.springbootimageuploader.model.dto;

public class ImageDto {

	private Long id;
	private String imageAddress;
	private String publicId;
	private String username;
	private String createdAt;
	private String updatedAt;

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
}
