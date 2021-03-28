package pl.marcinm312.springbootimageuploader.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "tokens")
public class Token extends AuditModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String value;

	@OneToOne
	private AppUser user;

	public Token() {
	}

	public Token(Long id, String value, AppUser user) {
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

	public AppUser getUser() {
		return user;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Token{" +
				"id=" + id +
				", value='" + value + '\'' +
				", user=" + user +
				'}';
	}
}