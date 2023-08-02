package uta.cse3310;

import java.util.ArrayList;

public class AccountList {
    ArrayList <Account> accountList = new ArrayList<>();

    public void addAccount (Account account) {
        accountList.add(account);
    }

    public Account getAccountfromList (int i) {
        if ((accountList.size() > i) && (i > 0)) {
            return accountList.get(i - 1);
        }

        else {
            System.out.printf("Cannot find account at index: " + i);
            return null;
        }
    }
    public void printList () {
        System.out.println("Accounts: ");
        for (int i = 0; i < accountList.size(); i++) {
            System.out.println(accountList.get(i).toString() + " ");
        }
    }
}
