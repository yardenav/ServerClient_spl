package bgu.spl;

import java.io.IOException;
import java.util.ArrayList;

import bgu.spl.tokenizer.StringMessage;

public class Room {
	
	private ArrayList<GamePlayer> roomPlayers;
	private boolean isOn;
	private String name;
	private SupportedGame theGame;
	
	
	public Room(GamePlayer admin, String name) {
	
		this.roomPlayers = new ArrayList<GamePlayer>();
		this.isOn = false;
		this.name = name;
	}
	
public ArrayList<GamePlayer> getRoomPlayers() {
		
		return roomPlayers;
	}
	public void addRoomPlayer(GamePlayer gamer) {
		this.roomPlayers.add(gamer);
		System.out.println("num of players: " + roomPlayers.size());
	}
	
	public void removePlayer(GamePlayer player){
		this.roomPlayers.remove(player);
	}
	
	public boolean isOn() {
		return isOn;
	}
	public void startGame(SupportedGame game) {
		this.isOn = true;
		theGame = game;
	}
	
	public void closeGame() {
		this.isOn = false;
	}

	public SupportedGame getTheGame() {
		return theGame;
	}
	
	
	public int getNumOfPlayers()
	{
		return this.roomPlayers.size();
	}

	public void notifyScoreOfRound() {
		for (int i=0 ; i < this.roomPlayers.size() ; i++)
		{
			try {
				StringMessage message = new StringMessage("You got " + roomPlayers.get(i).getRoundScore() + " points this round!" );
				roomPlayers.get(i).getCB().sendMessage(message);
				roomPlayers.get(i).initRoundScore();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

}
