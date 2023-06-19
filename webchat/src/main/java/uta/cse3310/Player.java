/*
 * Player has getters and constructor to create a hand, chips,
 * and a value (of the cards in hand). 
 */

package uta.cse3310;

public class Player {
    private Hand hand; //hand with 5 cards
    private int chips; //chips left
    private String name; //name of player

    public Player (Hand hand, int chips, String name) {
        this.hand = hand;
        this.chips = chips;
        this.name = name;
    }

    public int getChips() {
        return chips;
    }

    public Hand getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " + chips + " " + hand;
    }
}