package bgu.spl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;

import bgu.spl.DataSingleton;
import bgu.spl.GamePlayer;
import bgu.spl.Room;
import bgu.spl.SupportedGame;
import bgu.spl.tokenizer.StringMessage;





public class Bluffer implements SupportedGame {

	private DataSingleton data;
	private ConcurrentLinkedQueue<BlufferQuestion> questionsQueue;
	private int numOfplayers;
	private BlufferQuestion currQuestion;

	
	
	
	public Bluffer() {

		questionsQueue = new ConcurrentLinkedQueue<BlufferQuestion>();	
	}


	@Override
	public void initialize(int num) {

		data  = DataSingleton.getInstance();
		

		try {

			// =========== fetch questions from json
			Gson gson = new Gson();
			GsonData GsonQ;

		


			BufferedReader br = new BufferedReader(
					new FileReader("/users/studs/bsc/2016/yardenav/workspace/java_workspace/assignment3/assignment3/src/main/java/bgu/spl/bluffer2.json"));
			//convert the json string back to object
			GsonQ = gson.fromJson(br, GsonData.class);
			
			
			// ============ arrange the questions array
			
			for (int i=0 ; i < GsonQ.questions.length ; i++)
			{
				String q = GsonQ.questions[i].questionText;
				String realAnswer = GsonQ.questions[i].realAnswer;

				questionsQueue.add(new BlufferQuestion(q, realAnswer, num));
			}
			
			this.currQuestion = questionsQueue.poll();
			this.numOfplayers = num;
			
		}
		catch (IOException e)
		{
			
		}
		finally
		{

		}
	}

	@Override
	public void start(GamePlayer starter, Room room, TBGProtocol protocol) {
		StringMessage message = new StringMessage("BROADCAST ASKTXT " + currQuestion.getTheQ());
		protocol.processMessage(message, starter.getCB());
		
	}
	
	@Override
	public void TXTRESP(String answer, GamePlayer sender, Room room , TBGProtocol protocol) {
		this.currQuestion.addBluffAnswer(sender, answer);
		System.out.println("from q: " + this.currQuestion.getHowMuchAnswered() + "*from this: " +  this.numOfplayers );
		if (this.currQuestion.getHowMuchAnswered() == this.numOfplayers)
		{
			System.out.println("entered");
			sendSelectionsToPlayers(protocol, sender);
		}
		
	}

	@Override
	public void SELECTRESP(int selection, GamePlayer sender, Room room , TBGProtocol protocol) {
		
				
		StringMessage message;
		
		if (selection == this.currQuestion.getRealAnsIndex())
		{
			sender.setPersonalScore(sender.getPersonalScore() + 10);
			sender.addToRoundScore(10);
		}
		else
		{
			this.currQuestion.creditTheBluffer(selection, sender);
		}
		this.currQuestion.aSelectionWasMade();
		if (this.currQuestion.getHowMuchSelected() == numOfplayers) // all players selected option and all was credited accordingly
		{
			message = new StringMessage("BROADCAST GAMEMSG The correct answer is: " + currQuestion.getRealAnswer());
			protocol.processMessage(message, sender.getCB());
			room.notifyScoreOfRound(); //tells every player how many points he got this round and init the roundscore of the player
			
			this.currQuestion = questionsQueue.poll();
			if (this.currQuestion != null)
			{
				message = new StringMessage("BROADCAST ASKTXT " + currQuestion.getTheQ());
				protocol.processMessage(message, sender.getCB());
			}
			else
			{
				String summary = "BROADCAST Summary: ";
				ArrayList<GamePlayer> players = room.getRoomPlayers();
				for(int i=0; i < room.getNumOfPlayers(); i++){ 
					summary += players.get(i).getNick() + ": " + players.get(i).getPersonalScore() + ", "; 
				}
				message = new StringMessage(summary );
				protocol.processMessage(message , sender.getCB());
				room.closeGame();
			}
		}
	}
	
	private void sendSelectionsToPlayers(TBGProtocol protocol, GamePlayer sender)
	{
		String resp = "";
		ConcurrentHashMap< Integer , PlayerAnswer> map = this.currQuestion.getPlayersAnswers();
		for (int i = 0; i < (numOfplayers + 1); i++)
		{
			resp = resp + i + "." + map.get(i).getAnswer() + " ";
		}
		StringMessage message = new StringMessage("BROADCAST ASKCHOICES " + resp );
		protocol.processMessage(message, sender.getCB());
		
	}


	public int getNumOfplayers() {
		return numOfplayers;
	}

	
	
}
