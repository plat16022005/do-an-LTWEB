package aloutesocial.com.model;

public class Account {
	private String user_name;
	private String pass_word;
	private String role;
	private String status;
	public Account(String user_name, String pass_word, String role, String status) {
		super();
		this.user_name = user_name;
		this.pass_word = pass_word;
		this.role = role;
		this.status = status;
	}

	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getPass_word() {
		return pass_word;
	}
	public void setPass_word(String pass_word) {
		this.pass_word = pass_word;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
