package model;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Deque;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import model.interfaces.GameEngine;
import model.interfaces.Player;
import model.interfaces.PlayingCard;
import view.interfaces.GameEngineCallback;

public class GameEngineImpl implements GameEngine
{
    private Deque<PlayingCard> deck;
    private Map<String, Player> players = new TreeMap<>();
    private List<GameEngineCallback> callbacks = new LinkedList<>();

    public GameEngineImpl()
    {
        // Initialise the deck of cards
        this.deck = getShuffledHalfDeck();
    }

    @Override
    public void dealPlayer(Player player, int delay) throws IllegalArgumentException
    {
        if (delay < 0 || delay > 1000)
            throw new IllegalArgumentException();

        // prevent dealing players who do not exist in the collection or hasn't placed a bet
        if (!players.containsValue(player) || player.getBet() == 0)
            return;

        PlayingCard card;
        int playerPoints = 0;

        while (playerPoints < BUST_LEVEL)
        {
            // get a card from the top of the deck
            card = dealCard(delay);
            playerPoints += card.getScore();

            // log the events of this round
            logPlayer(player, card, playerPoints);

            // if the card causes the player to bust
            if (playerPoints > BUST_LEVEL)
            {
                playerPoints -= card.getScore();
                break;
            }
        }

        // update the result of the player's most recent hand
        player.setResult(playerPoints);

        // log the player's results of the round
        for (GameEngineCallback callback : callbacks)
            callback.result(player, playerPoints, this);
    }

    @Override
    public void dealHouse(int delay) throws IllegalArgumentException
    {
        if (delay < 0)
            throw new IllegalArgumentException();

        PlayingCard card;
        int housePoints = 0;

        while (housePoints < BUST_LEVEL)
        {
            // get a card from the top of the deck
            card = dealCard(delay);
            housePoints += card.getScore();

            // log the events of this round
            logHouse(card, housePoints);

            // if the card causes the house to bust
            if (housePoints > BUST_LEVEL)
            {
                housePoints -= card.getScore();
                break;
            }
        }

        // determine the win/loss of players and update attributes accordingly
        for (Player player : players.values())
            applyWinLoss(player, housePoints);

        // log final results once round ends
        for (GameEngineCallback callback : callbacks)
            callback.houseResult(housePoints, this);

        // reset players' previous bet for next round
        for (Player player : players.values())
            player.resetBet();

        // reset the deck of cards once round ends
        deck = getShuffledHalfDeck();
    }

    /**
     * Utility method to deal a card from the deck.
     *
     * @param delay - the delay in between dealing a card
     * @return a PlayingCard removed from the top of the deck
     */
    private PlayingCard dealCard(int delay)
    {
        PlayingCard card = null;

        try
        {
            Thread.sleep(delay);
            card = deck.pop();
        }
        catch (InterruptedException exception)
        {
            exception.printStackTrace();
        }
        catch (NoSuchElementException exception)
        {
            // Get a new deck if the current deck runs out of cards
            deck = getShuffledHalfDeck();
            card = deck.pop();
        }

        return card;
    }

    /** Utility method to log the player's round events.
     *
     * @param player - the Player to whom the card is dealt
     * @param card - the dealt PlayingCard
     * @param playerPoints - the number of points the player obtained from the round
     */
    private void logPlayer(Player player, PlayingCard card, int playerPoints)
    {
        for (GameEngineCallback callback : callbacks)
        {
            if (playerPoints > BUST_LEVEL)
                // log the details of the card that caused the bust
                callback.bustCard(player, card, this);
            else
                // log the details of the dealt card
                callback.nextCard(player, card, this);
        }
    }

    /**
     * House's version of the logger method to log the house's round events.
     *
     * @param card - the dealt PlayingCard
     * @param housePoints - the number of points the house obtained from the round
     */
    private void logHouse(PlayingCard card, int housePoints)
    {
        for (GameEngineCallback callback : callbacks)
        {
            if (housePoints > BUST_LEVEL)
                // log the details of the card that caused the bust
                callback.houseBustCard(card, this);
            else
                // log the details of the dealt card
                callback.nextHouseCard(card, this);
        }
    }

    @Override
    public void applyWinLoss(Player player, int houseResult)
    {
        // compare the points of player and house
        if (player.getResult() > houseResult)
            player.setPoints(player.getPoints() + player.getBet());
        else if (player.getResult() < houseResult)
            player.setPoints(player.getPoints() - player.getBet());
    }

    @Override
    public void addPlayer(Player player)
    {
        // if player with the same id exists, then replace the previous player
        if (players.containsKey(player.getPlayerId()))
            players.replace(player.getPlayerId(), player);
        else
            players.put(player.getPlayerId(), player);
    }

    @Override
    public Player getPlayer(String id)
    {
        // if the player exists in the collection
        return players.getOrDefault(id, null);
    }

    @Override
    public boolean removePlayer(Player player)
    {
        // remove if the player exists in the collection
        if (players.containsKey(player.getPlayerId()))
        {
            players.remove(player.getPlayerId());
            return true;
        }
        return false;
    }

    @Override
    public boolean placeBet(Player player, int bet)
    {
        return player.setBet(bet);
    }

    @Override
    public void addGameEngineCallback(GameEngineCallback gameEngineCallback)
    {
        // add game engine callback
        callbacks.add(gameEngineCallback);
    }

    @Override
    public boolean removeGameEngineCallback(GameEngineCallback gameEngineCallback)
    {
        // remove game engine callback if it exists in the collection
        if (callbacks.contains(gameEngineCallback))
        {
            callbacks.remove(gameEngineCallback);
            return true;
        }
        return false;
    }

    @Override
    public Collection<Player> getAllPlayers()
    {
        // the collection containing all the players
        return Collections.unmodifiableCollection(players.values());
    }

    @Override
    public Deque<PlayingCard> getShuffledHalfDeck()
    {
        List<PlayingCard> deck = new LinkedList<>();

        // nested loops to populate the deck of cards
        for (PlayingCard.Suit suit : PlayingCard.Suit.values())
        {
            for (PlayingCard.Value value : PlayingCard.Value.values())
            {
                // points for ace = 11, king, queen, jack = 10, rest of their face value
                switch (value)
                {
                    case ACE:
                        deck.add(new PlayingCardImpl(suit, value, 11));
                        break;
                    case EIGHT:
                        deck.add(new PlayingCardImpl(suit, value, 8));
                        break;
                    case NINE:
                        deck.add(new PlayingCardImpl(suit, value, 9));
                        break;
                    default:
                        deck.add(new PlayingCardImpl(suit, value, 10));
                        break;
                }
            }
        }

        // shuffle the deck
        Collections.shuffle(deck);
        return new LinkedList<>(deck);
    }
}