package bgu.spl;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

import bgu.spl.Bluffer;
import bgu.spl.protocol.*;
import bgu.spl.tokenizer.StringMessage;

public class TBGProtocol implements AsyncServerProtocol<StringMessage> {
	
	private DataSingleton data = DataSingleton.getInstance();
	private String[] supportedGamesList= {"BLUFFER"};
	private boolean shouldClose = false;
	
	
	
	public TBGProtocol() { }
	
// TODO problems -
	
	@Override
	public void processMessage(StringMessage msg, ProtocolCallback<StringMessage> callback)
	{
		
		String callbackMessage;
		String messageType = determineMsgType(msg.getMessage());
		String restOfMsg = getRestOfMsg(msg.getMessage());
		String messageResponse = "";
		Room room = null;
		GamePlayer sender = null;
		
		if (callback != null)
		{
			sender = this.data.getPlayerFromCallback(callback); // fetching who sent the message
		}


		if (sender != null)
		{
			room = sender.getRoom();
		}
	


			


		switch (messageType) {
			case "NICK": messageResponse = "SYSMSG " + messageType + " " + handleNICK(restOfMsg, callback);
			break;
			case "JOIN":  messageResponse = "SYSMSG " + messageType + " " + handleJOIN(restOfMsg, sender); 
			break;
			case "MSG": messageResponse = "SYSMSG " + messageType + " " + handleMSG(restOfMsg, sender, room);
			break;
			case "LISTGAMES": messageResponse = "SYSMSG " + messageType + " " + handleLISTGAMES();
			break;
			case "STARTGAME":  messageResponse = "SYSMSG " + messageType + " " + handleSTARTGAME(restOfMsg, sender, room); 
			break;
			case "TXTRESP":  messageResponse = "SYSMSG " + messageType + " " + handleTXTRESP(restOfMsg, sender, room); 
			break;         
			case "SELECTRESP":  messageResponse = "SYSMSG " + messageType + " " + handleSELECTRESP(restOfMsg, sender, room); 
			break;
			case "BROADCAST": messageResponse = handleBroadcast(restOfMsg,room);
			break;
			case "QUIT": messageResponse = handleQuit(restOfMsg, room, sender);
			break;
			default : messageResponse = "UNIDENTIFIED";
			break;
			
		}
		
		 
		 try {
			 
			if ((callback != null) && (messageResponse != null) )
				{
				StringMessage response = new StringMessage(messageResponse);
				callback.sendMessage(response);
				}
		} catch (IOException e) {
			System.out.println("ERROR sending message from switch");
			e.printStackTrace();
		}
		// TODO handle termination with quit and end of game termination
	}
	















	// returns the type of the message (JOIN,NICK.....)
	private String determineMsgType(String msg)
	{
		
		int i = msg.indexOf(' ');
		if (i>0)
		{
		String word = msg.substring(0, i);
		return word;
		}
		else
			return msg;
			
	}
	
	// returns the arguments of the command.
	private String getRestOfMsg(String msg)
	{
		int i = msg.indexOf(' ');
		if (i>0)
		{
			String rest = msg.substring(i);
			return rest;
		}
		else
			return "";
	}
	
	
	
	// ********** send message to all players in the room ******************
	// will send the message to all room except the sender. unless sender is null.
	private String sendMsgToRoomWithPrefix(String preFix, String msg, GamePlayer sender, Room room)
	{
		ArrayList<GamePlayer> players = room.getRoomPlayers();
		
		try {
		
			for (int i = 0 ; i< players.size() ; i++)
			{
				if (!players.get(i).equals(sender))
					if (sender != null)
					{
						StringMessage response = new StringMessage(preFix + sender.getNick() + " " + msg);
					players.get(i).getCB().sendMessage(response);
					}
					else
					{
						StringMessage response = new StringMessage(preFix + " " + msg);
					players.get(i).getCB().sendMessage(response);
					}

			}
			return "ACCEPTED";
		}	
		catch (IOException e) {
			System.out.println("ERROR sending message to room");
			e.printStackTrace();
			return "REJECTED";
		}

	}
	
	private String sendMsgToRoom(String msg, Room room)
	{
		ArrayList<GamePlayer> players = room.getRoomPlayers();
		
		try {
		
			for (int i = 0 ; i< players.size() ; i++)
			{

				StringMessage response = new StringMessage(msg);
					players.get(i).getCB().sendMessage(response);

			}
			return "ACCEPTED";
		}	
		catch (IOException e) {
			System.out.println("ERROR sending message to room");
			e.printStackTrace();
			return "REJECTED";
		}

	}
	
	// =========================== NICK =========================
	
	private String handleNICK(String name, ProtocolCallback<StringMessage> callback) {
		
		if (!data.playerExists(callback))
		{
			data.addPlayer(callback, name);
			return "ACCEPTED";
		}
		else
		{
			return "REJECTED";
		}
		
		
		
	}
	
	// =========================== JOIN =========================
	
	private String handleJOIN(String roomName, GamePlayer player) // returns the message that should be returned to the client
	{
		
		
		if(!this.data.roomExist(roomName)){ //room doesn not exist

			this.data.createRoom(roomName, player);
			
		}
		Room room = data.returnRoom(roomName);
		if(player.getRoom() != null){ //player is already in a room
			if(this.data.returnRoom(roomName).isOn()){ //room is active
				return "REJECTED";
			}
			else
			{
				player.getRoom().removePlayer(player);
			}

		}
		if(this.data.returnRoom(roomName).isOn()){ //room is active
			return "REJECTED";
		}
		else
		{
			room.addRoomPlayer(player);
			player.setRoom(room);
		}
		
		return "ACCEPTED";
	}
	
	// =========================== MSG =========================

	
	private String handleMSG(String restOfMsg, GamePlayer sender, Room room) {
		
		return sendMsgToRoomWithPrefix("USRMSG",restOfMsg,sender,room);
		
		
	}
	
	// =========================== LIST GAMES =========================
	
	private String handleLISTGAMES() {
		
		return "ACCEPTED" + this.supportedGamesList.toString();
	}
	
	// =========================== START GAME =========================
	
	private String handleSTARTGAME(String restOfMsg, GamePlayer player, Room room) {
		
		String gameName = restOfMsg.replaceAll("\\s+",""); // Removes all unnecessary  spaces and /n
		
		room.startGame(new Bluffer());
		
		if (gameName.equals("BLUFFER"))
		{
			
			room.getTheGame().initialize(room.getNumOfPlayers()); // will load the questions from the data base
			room.getTheGame().start(player,room, this); // will shoot the first question
			return "ACCEPTED";	
		}
		else
		{
			return "REJECTED";
		}
		


		
	}
 
	// =========================== TXT RESP =========================

	private String handleTXTRESP(String restOfMsg, GamePlayer sender, Room room) {
		String resp = restOfMsg.replaceAll("\\s+",""); // Removes all unnecessary  spaces and /n
		resp = resp.toLowerCase();
		room.getTheGame().TXTRESP(resp,sender,room, this);
		return "ACCEPTED";
	}

	// =========================== SELECT RESP =========================

	
	private String handleSELECTRESP(String restOfMsg, GamePlayer sender, Room room) {
		String resp = restOfMsg.replaceAll("\\s+",""); // Removes all unnecessary  spaces and /n
		
		int selection = Integer.parseInt(resp);
		if ((selection > room.getNumOfPlayers() + 1) || (selection < 0))
				return "REJECTED not good number";
		
		room.getTheGame().SELECTRESP(selection,sender,room, this);
		return "ACCEPTED";
	}
	// =========================== Broadcast =========================

	private String handleBroadcast(String msg, Room room) {
		
		this.sendMsgToRoom(msg, room);
		return null;
	}
	
	// =========================== Quit =========================

		private String handleQuit(String msg, Room room, GamePlayer player) {
			
			if (room.isOn())
			{
				return "REJECTED you can't leave when room is on";
			}
			else
			{

				player.setPersonalScore(0);
				player.initRoundScore();
				player.initTotalScore();
				player.setRoom(null);
		        room.removePlayer(player);
		        data.removePlayer(player);
				if(room.getNumOfPlayers() == 0){
					data.removeRoom(room);
				}
				this.connectionTerminated();
				return "ACCEPTED";	

			}
		}
	
		

	
	public boolean isEnd(StringMessage msg)
	{
		return msg.equals("QUIT");
	}



	@Override
	public boolean shouldClose() {
		return shouldClose;
	}

	@Override
	public void connectionTerminated() {
		shouldClose = true;
		
	}
	
	
}