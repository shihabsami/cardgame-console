package model;

import model.interfaces.Player;

public class SimplePlayer implements Player
{
    private String id;
    private String playerName;
    private int points;
    private int bet;
    private int result;

    public SimplePlayer(String id, String playerName, int initialPoints)
    {
        this.id = id;
        this.playerName = playerName;
        this.points = initialPoints;
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
    public String getPlayerId()
    {
        return id;
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
    public int getBet()
    {
        return bet;
    }

    @Override
    public void resetBet()
    {
        // reset the bet to 0
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
        // equal if both players have the same id
        return id.equals(player.getPlayerId());
    }

    @Override
    public boolean equals(Object player)
    {
        // cast the object and call through to the type checked method
        if (player instanceof Player)
            return equals((SimplePlayer) player);

        return false;
    }

    @Override
    public int hashCode()
    {
        // hashcode generated based on the player's id attribute
        return id.hashCode();
    }

    @Override
    public int compareTo(Player player)
    {
        // compare players based on id
        return player.getPlayerId().compareTo(id);
    }

    @Override
    public String toString()
    {
        return String.format("Player: id=%s, name=%s, bet=%d, points=%d, RESULT .. %d",
                id, playerName, bet, points, result);
    }
}