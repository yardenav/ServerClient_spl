package bgu.spl;

public class PlayerAnswer {

	private GamePlayer player;
	private String answer;
	
	public PlayerAnswer(GamePlayer player, String answer) {
		super();
		this.player = player;
		this.answer = answer;
	}

	public GamePlayer getPlayer() {
		return player;
	}

	public String getAnswer() {
		return answer;
	}
	
	
}
