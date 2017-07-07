package bgu.spl;

import java.io.IOException;

public interface SupportedGame {

	/**
	* @param 
	* @throws 
	* 
	* Starts the game
	*/
	void start(GamePlayer player, Room room, TBGProtocol protocol);
	
	void initialize(int numOfPlayers);

	void TXTRESP(String restOfMsg, GamePlayer sender, Room room, TBGProtocol protocol);

	void SELECTRESP(int selection, GamePlayer sender, Room room, TBGProtocol protocol);
}
