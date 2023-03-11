package account.business.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static account.util.enums.Roles.*;

@Entity
@Table(name = "users")
public class User {

	@Id
	@Column
	@GeneratedValue
	private Long id;

	@Column
	private String name;

	@Column
	private String lastname;

	@Column
	private String email;

	@Column
	@JsonIgnore
	private String password;

	@Column
	private ArrayList<String> roles;

	@JsonIgnore
	private int failedAttempt;

	@JsonIgnore
	private boolean accountNonLocked;

	@JsonIgnore
	private Date lockTime;

	public User(){};

	public User(String name, String lastname, String email, String password) {
		this.name = name;
		this.lastname = lastname;
		this.email = email;
		this.password = password;
		roles = new ArrayList<>();
		failedAttempt = 0;
		accountNonLocked = true;
		lockTime = null;
	}

	public Long getId() { return id; }

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setLastname(String lastname){
		this.lastname = lastname;
	}

	public String getLastname(){
		return lastname;
	}

	public void setRole(String role) {
		this.roles.add(0, role);
	}

	public void removeRole(String role) {
		this.roles.removeIf(currentRole -> (Objects.equals(currentRole, role)));
	}

	public ArrayList<String> getRoles() {
		return roles;
	}

	public int getFailedAttempt() {
		return failedAttempt;
	}

	public void setFailedAttempt(int failedAttempt) {
		this.failedAttempt = failedAttempt;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean locked) {
		accountNonLocked = locked;
	}

	public Date getLockTime() {
		return lockTime;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	@JsonIgnore
	public UserData getUserData() {
		return new UserData(id, name, lastname, email, roles);
	}

	public boolean isRoleConflict(String role) {
		if (Objects.equals(role, ADMINISTRATOR.name()) && (getRoles().contains("ROLE_" + USER.name())) || getRoles().contains("ROLE_" + ACCOUNTANT.name())) {
			return true;
		}
		return (Objects.equals(role, USER.name()) || Objects.equals(role, ACCOUNTANT.name()) || Objects.equals(role, AUDITOR.name())) && getRoles().contains("ROLE_" + ADMINISTRATOR.name());
	}

	public static class UserData {
		private final Long id;
		private final String name;
		private final String lastname;
		private final String email;
		private ArrayList<String> roles;

		public UserData(Long id, String name, String lastname, String email, ArrayList<String> roles) {
			this.id = id;
			this.name = name;
			this.lastname = lastname;
			this.email = email;
			this.roles = roles;
		}

		public Long getId() { return id; }

		public String getName() { return name; }

		public String getLastname() { return lastname; }

		public String getEmail() { return email; }

		public ArrayList<String> getRoles() { return roles; }
	}
}
