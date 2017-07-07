package bgu.spl;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.GamePlayer;
import bgu.spl.Room;

public class BlufferQuestion {

	private String theQ;
	private String realAnswer;
	private ConcurrentHashMap< Integer , PlayerAnswer> playersAnswers;
	private int realAnsIndex;
	private int numberOfPlayers;
	private int howMuchAnswered = 0;
	private int howMuchSelected = 0;
	
	
	public BlufferQuestion(String theQ, String realAnswer,int numberOfPlayers) {
		
		this.theQ = theQ;
		this.realAnswer = realAnswer;
		this.playersAnswers = new ConcurrentHashMap<Integer,PlayerAnswer>();
		Random rand = new Random();
		realAnsIndex = rand.nextInt(numberOfPlayers) ;
		playersAnswers.put(realAnsIndex, new PlayerAnswer(null, realAnswer));
		this.numberOfPlayers = numberOfPlayers;
		 
	}

	public String getTheQ() {
		return theQ;
	}
	
	public void addBluffAnswer(GamePlayer player, String ans)
	{
		if (howMuchAnswered != realAnsIndex)
		{
			playersAnswers.put(howMuchAnswered,new PlayerAnswer(player, ans));
			
		}
		else
		{
			playersAnswers.put(numberOfPlayers,new PlayerAnswer(player, ans));
		}
		howMuchAnswered++;	
	}
	
	public int getHowMuchAnswered()
	{
		return howMuchAnswered;
	}
	
	public void aPlayerSelected()
	{
		howMuchSelected++;
	}
	
	public int getHowMuchSelected()
	{
		return howMuchSelected;
	}

	public ConcurrentHashMap<Integer, PlayerAnswer> getPlayersAnswers() {
		return playersAnswers;
	}

	public int getRealAnsIndex() {
		return realAnsIndex;
	}

	public void creditTheBluffer(int selection, GamePlayer sender) {
		GamePlayer creditedPlayer = this.playersAnswers.get(selection).getPlayer() ;
		int oldScore = creditedPlayer.getPersonalScore();
		
		if (creditedPlayer != sender) // making sure the player didnt chose his own bluff
			{
				creditedPlayer.setPersonalScore(oldScore + 5);
				creditedPlayer.addToRoundScore(5);
			}
		
	}

	public void aSelectionWasMade() {
		howMuchSelected++;
	}

	public String getRealAnswer() {
		return realAnswer;
	}
	
	
}
