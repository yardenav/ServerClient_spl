package bgu.spl;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.tokenizer.StringMessage;

public class DataSingleton {
		
		private ConcurrentHashMap<String, Room> roomsMap;
		private ConcurrentHashMap< ProtocolCallback<StringMessage> , GamePlayer> playersMap;
		

		
		private static class SingletonHolder {
		        private static DataSingleton instance = new DataSingleton();
		    }
		
		public static DataSingleton getInstance() {
		      return SingletonHolder.instance;
		  }

		public DataSingleton() {
			
			this.roomsMap = new ConcurrentHashMap<String,Room>();
			this.playersMap = new  ConcurrentHashMap< ProtocolCallback<StringMessage> , GamePlayer>();
		}
		
		
		public GamePlayer getPlayerFromCallback(ProtocolCallback<StringMessage> callback) // gets the name\NICK of the client according to his callback. 
		{
			
			return playersMap.get(callback);
		}
		
		public void startGame()
		{
			// TODO dont forget to update the roomsMap that the room is on play.
		}
		
		public void addPlayer(ProtocolCallback<StringMessage> callback, String name)
		{
			playersMap.put(callback, new GamePlayer(name,callback));
		}

		public boolean playerExists(ProtocolCallback<StringMessage> cb) {

			return playersMap.containsKey(cb);
		}
		
		public void createRoom(String roomName, GamePlayer admin){
			this.roomsMap.put(roomName, new Room(admin, roomName));
		}
		
		public boolean roomExist(String roomName){
			return this.roomsMap.containsKey(roomName);
			
		}
		
		public void removePlayer(GamePlayer player){
			this.playersMap.remove(player);
			
			
		}
		
		public void removeRoom(Room room){
			this.roomsMap.remove(room);
			
			
		}
		
		public Room returnRoom(String room){
			
				return this.roomsMap.get(room);

		}
}
