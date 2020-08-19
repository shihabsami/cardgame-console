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
    public String toString()
    {
        return String.format("Suit: %s, Value: %s, Score: %d",
                toTitleCase(SUIT), toTitleCase(VALUE), SCORE);
    }

    @Override
    public boolean equals(PlayingCard card)
    {
        // equal if both cards have the same suit and face value
        return (VALUE == card.getValue() && SUIT == card.getSuit());
    }

    @Override
    public boolean equals(Object card)
    {
        // cast the object and call through to the type checked method
        if (card instanceof PlayingCard)
            return equals((PlayingCard) card);

        return false;
    }

    @Override
    public int hashCode()
    {
        // hashcode generated based on the card's suit and face value attributes
        return SUIT.hashCode() + VALUE.hashCode();
    }

    /**
     * Utility method to retrieve a card's suit in title case.
     *
     * @param suit - enum constant of type Suit
     * @return a title cased String representation of the PlayingCard's suit enum
     */
    private String toTitleCase(PlayingCard.Suit suit)
    {
        return suit.name().charAt(0) + suit.name().substring(1).toLowerCase();
    }

    /**
     * Utility method to retrieve a card's face value in title case.
     *
     * @param value - enum constant of type Value
     * @return a title cased String representation of the PlayingCard's value enum
     */
    private String toTitleCase(PlayingCard.Value value)
    {
        return value.name().charAt(0) + value.name().substring(1).toLowerCase();
    }
}