package uta.cse3310;

import java.util.ArrayList;

public class AccountList {
    private ArrayList <Account> accountList = new ArrayList<>();

    AccountList(){}

    //Adding Account to AccountList
    public void addAccount(Account account) {
        accountList.add(account);
    }

    //Retrieving Account from AccountList
    public Account getAccountfromList(int i) {
        if (accountList.size() >= i) {
            return accountList.get(i - 1);
        }

        else {
            System.out.printf("Cannot find account at index: " + i);
            return null;
        }
    }

    //Size of AccountList
    public int sizeAccountList() {
        return accountList.size();
    }

    //Print AccountList
    public void printList() {
        System.out.println("Accounts: ");
        for (int i = 0; i < accountList.size(); i++) {
            System.out.println(accountList.get(i).toString() + " ");
        }
    }
}
