/*
 *  Decided an enum was the best route here to store
 *  a value to the face cards for easy reference later
 *
 *  MIGHT CHANGE FORMAT LATER! :)
 */
package uta.cse3310;

enum Rank {
    TWO(2), 
    THREE(3), 
    FOUR(4), 
    FIVE(5), 
    SIX(6), 
    SEVEN(7), 
    EIGHT(8), 
    NINE(9), 
    TEN(10),
    JACK(11), 
    QUEEN(12), 
    KING(13), 
    ACE(14);

    private final int value;
    Rank(final int newValue) {
        value = newValue;
    }

    public int getValue() {return value;}
}
