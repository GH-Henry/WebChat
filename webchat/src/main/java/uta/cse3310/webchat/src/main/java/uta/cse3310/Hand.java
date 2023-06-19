/*
 * Using ArrayList to represent a hand for the
 * ease of clearing and add to the hand as cards are dealt
 */

package uta.cse3310;
import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand;
    
    public Hand () {
        hand = new ArrayList<Card>();
    }

    //card drawn by dealer given to player's hand
    public void draw (Card card) {
        hand.add(card);
    }

    //called when player folds - clears hand of cards resulting in a value of 0
    public void fold () {
        hand.clear();
    }   
}