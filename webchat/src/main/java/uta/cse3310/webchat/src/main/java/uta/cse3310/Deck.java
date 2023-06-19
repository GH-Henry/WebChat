/*
 * Decided to use a stack to allow "dealing" of cards in deck
 * 
 * I think using pop to delete cards when dealt is a good strategy
 */

package uta.cse3310;

import java.util.Collections;
import java.util.Stack;

public class Deck {
    private Stack <Card> deck = new Stack<>();

    public Deck() {
    //iteratoring through suits and ranks to create deck of cards
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                Card card = new Card(suit, rank);
                deck.push(card);
            }
        }
    }

    //used to shuffle the deck when called
    public void shuffle() {
        try {
            Collections.shuffle(deck);
        }

        catch (IndexOutOfBoundsException deal) {
            deal.printStackTrace();
        }
    }

    //used by dealer to distribute cards
    public Card deal() {
        if (isEmpty() == true)
            throw new IndexOutOfBoundsException("No cards in deck");
        return deck.pop();
    }

    //test if deck has cards
    public boolean isEmpty() {
        if (deck.empty() == true)
            return true;
        else
            return false;
    }
}