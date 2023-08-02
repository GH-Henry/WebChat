package uta.cse3310;

import static org.junit.Assert.assertEquals;

import org.junit.*;

/**
 * Unit test for Account.
 */
public class AccountTest {
  @Test
  public void testAccountName() {
    // Given
    Account testUser = new Account("testUser", "password", 1);

    // When
    String username = testUser.getAccountName();

    //Then
    assertEquals(username, "testUser");
  }

  @Test
  public void testAccountPassword() {
    // Given
    Account testUser = new Account("testUser", "password", 1);

    // When
    String password = testUser.getAccountPassword();

    //Then
    assertEquals(password, "password");
  }

  @Test
  public void testAccountID() {
    // Given
    Account testUser = new Account("testUser", "password", 1);

    // When
    int id = testUser.getID();

    //Then
    assertEquals(id, 1);
  }
}
