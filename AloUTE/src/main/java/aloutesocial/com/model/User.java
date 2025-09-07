package aloutesocial.com.model;

public class User {
	private String username;
	private String email;
	private String hovaten;
	private String sodienthoai;
	private String gioitinh;
	private String ngaysinh;
	public User(String username, String email, String hovaten, String sodienthoai, String gioitinh, String ngaysinh) {
		super();
		this.username = username;
		this.email = email;
		this.hovaten = hovaten;
		this.sodienthoai = sodienthoai;
		this.gioitinh = gioitinh;
		this.ngaysinh = ngaysinh;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHovaten() {
		return hovaten;
	}
	public void setHovaten(String hovaten) {
		this.hovaten = hovaten;
	}
	public String getSodienthoai() {
		return sodienthoai;
	}
	public void setSodienthoai(String sodienthoai) {
		this.sodienthoai = sodienthoai;
	}
	public String getGioitinh() {
		return gioitinh;
	}
	public void setGioitinh(String gioitinh) {
		this.gioitinh = gioitinh;
	}
	public String getNgaysinh() {
		return ngaysinh;
	}
	public void setNgaysinh(String ngaysinh) {
		this.ngaysinh = ngaysinh;
	}

}
