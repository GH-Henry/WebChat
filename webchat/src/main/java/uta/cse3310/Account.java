package uta.cse3310;


public class Account {

  private String username;
  private String password;

  Account(String _username, String _password) {
    username = _username;
    password = _password;
  }

  public String getAccountName()    {   return username;  }
  public String getAccountPassword()   {   return password;     }

  @Override
  public String toString() {
    return username;
  }
  
}
