package model;

import model.interfaces.PlayingCard;

public class PlayingCardImpl implements PlayingCard
{
    private final Suit SUIT;
    private final Value VALUE;
    private final int SCORE;

    public PlayingCardImpl(Suit suit, Value value, int score)
    {
        SUIT = suit;
        VALUE = value;
        SCORE = score;
    }

    @Override
    public Suit getSuit()
    {
        return SUIT;
    }

    @Override
    public Value getValue()
    {
        return VALUE;
    }

    @Override
    public int getScore()
    {
        return SCORE;
    }

    @Override
    public boolean equals(PlayingCard card)
    {
        // equal if both cards have the same suit and value
        return (VALUE == card.getValue() && SUIT == card.getSuit());
    }

    @Override
    public boolean equals(Object card)
    {
        // cast and call the type checked method
        return equals((PlayingCard) card);
    }

    @Override
    public int hashCode()
    {
        // hashcode generated based on the card's unique attributes
        return SUIT.hashCode() + VALUE.hashCode();
    }

    @Override
    public String toString()
    {
        return String.format("Suit: %s, Value: %s, Score: %d",
                SUIT, VALUE, SCORE);
    }
}