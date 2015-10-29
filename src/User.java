
public class User {
	private String userName;
	public String getUserName() {
		return userName;
	}

	public String getPassWord() {
		return passWord;
	}

	private String passWord;
	
	public User(String userName, String passWord){
		this.userName = userName;
		this.passWord = passWord;
	}

}
