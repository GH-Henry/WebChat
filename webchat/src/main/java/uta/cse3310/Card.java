/*
* Cards consist of a suit and a rank both are enums
*/
package uta.cse3310;

public class Card {
    public static char[] toString;
    private Suit suit;
    private Rank rank;
   
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return String.valueOf(new Card(suit, rank));
    }
}
