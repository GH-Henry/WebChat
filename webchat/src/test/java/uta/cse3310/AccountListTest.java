package uta.cse3310;

import static org.junit.Assert.*;
import org.junit.*;


/*
 * Unit Test for AccountList
 */
public class AccountListTest {
 
    //Testing creation of AccountList, adding Accounts to the list, and size
    @Test
    public void testAddAccount() {
        Account testUser1 = new Account("User1", "password1");
        Account testUser2 = new Account("User2", "password2");
        Account testUser3 = new Account("User3", "password3");

        AccountList testAccountList = new AccountList();

        testAccountList.addAccount(testUser1);
        testAccountList.addAccount(testUser2);
        testAccountList.addAccount(testUser3);

        assertEquals(testAccountList.sizeAccountList(), 3);
    }

    @Test
    public void testGetAccountfromList() {
        Account testUser1 = new Account("User1", "password1");
        Account testUser2 = new Account("User2", "password2");
        Account testUser3 = new Account("User3", "password3");

        AccountList testAccountList = new AccountList();

        testAccountList.addAccount(testUser1);
        testAccountList.addAccount(testUser2);
        testAccountList.addAccount(testUser3);
    
        Account findUser3 = testAccountList.getAccountfromList(3);
        assertEquals(findUser3, testUser3);
    }
 }
