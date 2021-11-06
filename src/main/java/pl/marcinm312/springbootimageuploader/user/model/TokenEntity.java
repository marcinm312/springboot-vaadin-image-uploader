package pl.marcinm312.springbootimageuploader.user.model;

import pl.marcinm312.springbootimageuploader.shared.model.AuditModel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "tokens")
public class TokenEntity extends AuditModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String value;

	@OneToOne
	private UserEntity user;

	public TokenEntity() {
	}

	public TokenEntity(Long id, String value, UserEntity user) {
		this.id = id;
		this.value = value;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "TokenEntity{" +
				"id=" + id +
				", value='" + value + '\'' +
				", user=" + user +
				'}';
	}
}
