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
    Account testUser = new Account("testUser", "password");

    // When
    String username = testUser.getAccountName();

    //Then
    assertEquals(username, "testUser");
  }

  @Test
  public void testAccountPassword() {
    // Given
    Account testUser = new Account("testUser", "password");

    // When
    String password = testUser.getAccountPassword();

    //Then
    assertEquals(password, "password");
  }
}
