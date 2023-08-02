package uta.cse3310;

public class Account {

  private String username;
  private String password;
  private int id;

  Account(String _username, String _password, int _id) {
    username = _username;
    password = _password;
    id = _id;
  }

  public String getAccountName() {
    return username;
  }

  public String getAccountPassword() {
    return password;
  }

  public int getID() {
    return id;
  }

  @Override
  public String toString() {
    return username;
  }

}
