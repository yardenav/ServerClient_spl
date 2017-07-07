package bgu.spl;

import bgu.spl.tokenizer.StringMessage;

public class GamePlayer {

	private String nick;
	private Room room;
	private ProtocolCallback<StringMessage> cb;
	private int personalScore; //TODO remember to put 0 here after end of a game
	private int roundScore = 0;


	public GamePlayer(String nick,ProtocolCallback<StringMessage> cb2 ) 
	{
		super();
		this.nick = nick;
		this.room = null;
		this.cb = cb2;
		personalScore = 0;
	}

	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}
	public String getNick() {
		return nick;
	}

	public ProtocolCallback<StringMessage>  getCB() {
		return cb;
		
	}

	public int getPersonalScore() {
		return personalScore;
	}

	public void setPersonalScore(int personalScore) {
		this.personalScore = personalScore;
	}

	public int getRoundScore() {
		return roundScore;
	}

	public void addToRoundScore(int score) {
		this.roundScore = this.roundScore + score;
	}

	public void initRoundScore() {
		this.roundScore = 0;
	}
	
	public void initTotalScore() {
		this.personalScore = 0;
	}
	
	
}
