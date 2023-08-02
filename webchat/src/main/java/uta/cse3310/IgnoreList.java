package uta.cse3310;

import java.util.ArrayList;

public class IgnoreList {

    //[0] first account is the account [1 - #] followed by chosen accounts to ignore.
    ArrayList <Account> ignoreList = new ArrayList<>();
    
    public void addIgnoredAccount (Account account) {
        ignoreList.add(account);
    } 
}
