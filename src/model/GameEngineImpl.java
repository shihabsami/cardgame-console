package model;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Deque;
import java.util.Collection;
import java.util.Collections;

import model.interfaces.GameEngine;
import model.interfaces.Player;
import model.interfaces.PlayingCard;
import view.interfaces.GameEngineCallback;

public class GameEngineImpl implements GameEngine
{
    private final Map<String, Player> PLAYERS = new HashMap<>();
    private final Deque<PlayingCard> DECK = getShuffledHalfDeck();
    private final List<GameEngineCallback> CALLBACKS = new LinkedList<>();

    @Override
    public void dealPlayer(Player player, int delay) throws IllegalArgumentException
    {
        if (!PLAYERS.containsKey(player.getPlayerId()) || delay < 0 || delay > 1000)
            throw new IllegalArgumentException();

        PlayingCard card;
        int playerPoints = 0;

        while (playerPoints < BUST_LEVEL)
        {
            // get a card from the top of the deck
            card = dealCard(delay);
            playerPoints += card.getScore();

            // log the events of this round
            logger(player, card, playerPoints);

            // if the card causes the player to bust
            if (playerPoints > BUST_LEVEL)
            {
                playerPoints -= card.getScore();
                break;
            }
        }

        player.setResult(playerPoints);

        // log the player's results of the round
        for (GameEngineCallback callback : CALLBACKS)
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
            logger(card, housePoints);

            // if the card causes the house to bust
            if (housePoints > BUST_LEVEL)
            {
                housePoints -= card.getScore();
                break;
            }
        }

        // determine the win/loss of players and update attributes accordingly
        for (Player player : PLAYERS.values())
            applyWinLoss(player, housePoints);

        // log final results once round ends
        for (GameEngineCallback callback : CALLBACKS)
            callback.houseResult(housePoints, this);
    }

    // private method to deal a card from the deck
    private PlayingCard dealCard(int delay)
    {
        // the delay for dealing a card
        try
        {
            Thread.sleep(delay);
        }
        catch (InterruptedException exception)
        {
            exception.printStackTrace();
        }
        return DECK.pop();
    }

    // private method to log the player's round events
    private void logger(Player player, PlayingCard card, int playerPoints)
    {
        if (playerPoints > BUST_LEVEL)
        {
            // log the details of the card that caused the bust
            for (GameEngineCallback callback : CALLBACKS)
                callback.bustCard(player, card, this);
        }
        else
        {
            // log the details of the dealt card
            for (GameEngineCallback callback : CALLBACKS)
                callback.nextCard(player, card, this);
        }
    }

    // an overload of the previous logger method to log the house's round events
    private void logger(PlayingCard card, int housePoints)
    {
        // if the card causes the house to bust
        if (housePoints > BUST_LEVEL)
        {
            // log the details of the card that caused the bust
            for (GameEngineCallback callback : CALLBACKS)
                callback.houseBustCard(card, this);
        }
        else
        {
            // log the details of the dealt card
            for (GameEngineCallback callback : CALLBACKS)
                callback.nextHouseCard(card, this);
        }
    }

    @Override
    public void applyWinLoss(Player player, int houseResult)
    {
        // compare player and house's points
        if (player.getResult() > houseResult)
            player.setPoints(player.getPoints() + player.getBet());

        else if (player.getResult() < houseResult)
            player.setPoints(player.getPoints() - player.getBet());
    }

    @Override
    public void addPlayer(Player player)
    {
        // if player with the same id exist, then replace the previous player
        if (PLAYERS.containsKey(player.getPlayerId()))
            PLAYERS.replace(player.getPlayerId(), player);

        else
            PLAYERS.put(player.getPlayerId(), player);
    }

    @Override
    public Player getPlayer(String id)
    {
        // return if the player exists in the collection
        if (PLAYERS.containsKey(id))
            return PLAYERS.get(id);

        return null;
    }

    @Override
    public boolean removePlayer(Player player)
    {
        // remove if the player exists in the collection
        if (PLAYERS.containsKey(player.getPlayerId()))
        {
            PLAYERS.remove(player.getPlayerId());
            return true;
        }
        return false;
    }

    @Override
    public boolean placeBet(Player player, int bet)
    {
        if (!PLAYERS.containsKey(player.getPlayerId()))
            throw new IllegalArgumentException();

        // reset player's previous bet before placing a new bet
        player.resetBet();
        return player.setBet(bet);
    }

    @Override
    public void addGameEngineCallback(GameEngineCallback gameEngineCallback)
    {
        // add game engine callback
        CALLBACKS.add(gameEngineCallback);
    }

    @Override
    public boolean removeGameEngineCallback(GameEngineCallback gameEngineCallback)
    {
        // remove game engine callback if it exists in the collection
        if (CALLBACKS.contains(gameEngineCallback))
        {
            CALLBACKS.remove(gameEngineCallback);
            return true;
        }
        return false;
    }

    @Override
    public Collection<Player> getAllPlayers()
    {
        // the collection containing all the players
        List<Player> players = new LinkedList<>(PLAYERS.values());

        // sort player collection by player id
        players.sort(Collections.reverseOrder());
        return Collections.unmodifiableCollection(players);
    }

    @Override
    public Deque<PlayingCard> getShuffledHalfDeck()
    {
        List<PlayingCard> deck = new LinkedList<>();
        PlayingCard.Suit[] suits = PlayingCard.Suit.values();
        PlayingCard.Value[] values = PlayingCard.Value.values();

        // populate the deck of cards
        for (PlayingCard.Suit suit : suits)
        {
            for (PlayingCard.Value value : values)
            {
                // points for ace = 11, king, queen, jack = 10, rest having their face value
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