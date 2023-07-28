package uta.cse3310;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for EventMessage.
 */
public class EventMessageTest {
  @Test
  public void testEventMessageType() {
    // Given
    EventMessage testMessageType = new EventMessage("test", getClass());

    // When
    String type = testMessageType.type;

    //Then
    assertEquals(type, "test");
  }
}
