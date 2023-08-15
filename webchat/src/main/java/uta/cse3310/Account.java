package uta.cse3310;

public class Account {

  private int uuid;
  private String username;
  private String password;

  Account(int _uuid, String _username, String _password) {
    uuid = _uuid;
    username = _username;
    password = _password;
  }

  public int getAccountUUID() {
    return uuid;
  }

  public String getAccountName() {
    return username;
  }

  public String getAccountPassword() {
    return password;
  }

  @Override
  public String toString() {
    return username;
  }

}
