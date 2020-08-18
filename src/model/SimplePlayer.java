package model;

import model.interfaces.Player;
import model.interfaces.PlayingCard;

public class SimplePlayer implements Player
{
    private final String ID;
    private String playerName;
    private int points;
    private int bet;
    private int result;

    public SimplePlayer(String id, String playerName, int initialPoints)
    {
        ID = id;
        this.playerName = playerName;
        this.points = initialPoints;
    }

    @Override
    public String getPlayerId()
    {
        return ID;
    }

    @Override
    public String getPlayerName()
    {
        return playerName;
    }

    @Override
    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    @Override
    public int getPoints()
    {
        return points;
    }

    @Override
    public void setPoints(int points)
    {
        this.points = points;
    }

    @Override
    public int getBet()
    {
        return bet;
    }

    @Override
    public boolean setBet(int bet)
    {
        // place the bet if player has sufficient points to bet
        if (bet > 0 && points > bet)
        {
            this.bet = bet;
            return true;
        }
        else if (bet == 0) resetBet();

        return false;
    }

    @Override
    public void resetBet()
    {
        bet = 0;
    }

    @Override
    public int getResult()
    {
        return result;
    }

    @Override
    public void setResult(int result)
    {
        this.result = result;
    }

    @Override
    public boolean equals(Player player)
    {
        // equal if both player have the same id
        return ID.equals(player.getPlayerId());
    }

    @Override
    public boolean equals(Object player)
    {
        // cast and call to the type checked method
        if (player instanceof Player)
            return equals((SimplePlayer) player);

        return false;
    }

    @Override
    public int hashCode()
    {
        // hashcode generated based on the player's unique attribute
        return ID.hashCode();
    }

    @Override
    public int compareTo(Player player)
    {
        // compare players based on the players' IDs
        return player.getPlayerId().compareTo(ID);
    }

    @Override
    public String toString()
    {
        return String.format("Player: ID: %s, Name: %s, Bet: %d, Points: %d, Result: %d",
                ID, playerName, bet, points, result);
    }
}