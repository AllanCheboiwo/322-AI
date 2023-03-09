package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
	
    private String userName = null;
    private String passwd = null;
    
    private int player=0;
    private int turn=1;
    
    private GameState game;
    private AI ai;
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {				 
    	COSC322Test player = new COSC322Test(args[0], args[1]);
    	
    	if(player.getGameGUI() == null) {
    		player.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	player.Go();
    
                	
                }
            });
    	}
    }
	
    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public COSC322Test(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	this.gamegui = new BaseGameGUI(this);
    	//and implement the method getGameGUI() accordingly
    	this.gamegui = new BaseGameGUI(this);
    }
 


    @Override
    public void onLogin() {
    	userName = gameClient.getUserName();
    	if(gamegui != null) {
    	gamegui.setRoomInformation(gameClient.getRoomList());
    	}
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        //This method will be called by the GameClient when it receives a game-related message
        //from the server.

        //For a detailed description of the message types and format,
        //see the method GamePlayer.handleGameMessage() in the game-client-api document.
        switch(messageType) {
        case GameMessage.GAME_STATE_BOARD:
	        this.getGameGUI().setGameState((ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.GAME_STATE));
	        game = new GameState((ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.GAME_STATE));
	        game.printGameState();
	        break;
		case GameMessage.GAME_ACTION_START:
			System.out.println("The game has started!");
			String playerWhite = (String) msgDetails.get(AmazonsGameMessage.PLAYER_WHITE);
	
			if (playerWhite.equals(userName)) {
				game.setPlayer(1);
				play();
			} else {
				game.setPlayer(2);
			}
//			game.setPlayer(player);
//			game.setTurn(1);
//			if(game.getTurn()==player)
//				play();
			break;
       
        case GameMessage.GAME_ACTION_MOVE:
        	System.out.println("Received a move message");
	        this.getGameGUI().updateGameState(msgDetails);
			play(msgDetails);
	    default:
	        	assert(false) :"Unknown message type: "+messageType;
	        	break;
        }
        return true;  	
    }
    
    public void play() {
    	//AI implementation
		//gameClient.sendMoveMessage(queenNew, queenNew, arrow); // send the move to the server
    	ai = new AI(game);
    	ArrayList<int[]> bestMove = ai.minimax(game.getBoardState(), 3,Integer.MIN_VALUE,Integer.MAX_VALUE,player==1);
		ArrayList<Integer> queenCurrent = new ArrayList<Integer>();
		ArrayList<Integer> queenNew = new ArrayList<Integer>();
		ArrayList<Integer> arrow = new ArrayList<Integer>();
		queenCurrent.add(bestMove.get(0)[0]);
		queenCurrent.add(bestMove.get(0)[1]);
		queenNew.add(bestMove.get(1)[0]);
		queenNew.add(bestMove.get(1)[1]);
		arrow.add(bestMove.get(2)[0]);
		arrow.add(bestMove.get(2)[1]);

		gameClient.sendMoveMessage(queenCurrent, queenNew, arrow);
		gamegui.updateGameState(queenCurrent, queenNew, arrow); // update the game state
		game.updateBoardState(queenCurrent, queenNew, arrow);
		System.out.println("Made a move");
		System.out.println(queenCurrent+" "+queenNew+" "+arrow);
		//game.printGameState();
    }
    
    
    public void play(Map<String, Object> msgDetails){
    	//System.out.println("Received a move message");
		ArrayList<Integer> queenCurrent = new ArrayList<Integer>();
		ArrayList<Integer> queenNew = new ArrayList<Integer>();
		ArrayList<Integer> arrow = new ArrayList<Integer>();
		queenCurrent = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
		queenNew = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT);
		arrow = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);
		game.updateBoardState(queenCurrent,queenNew,arrow);
		//System.out.println(queenCurrent+" "+queenNew+" "+arrow);
		//game.printGameState();
		play();
    }
//    public void playTurn(Map<String, Object> msgDetails) {
//		ArrayList<Integer> queenCurrent = new ArrayList<Integer>();
//		ArrayList<Integer> queenNew = new ArrayList<Integer>();
//		ArrayList<Integer> arrow = new ArrayList<Integer>();
//		if (turn == player) {
//			System.out.println("It's my turn");
//			// YOUR AI IMPLEMENTATION GOES HERE
//			// queenCurrent = YOUR CURRENT QUEEN POSITION [row, col]
//			// queenNew = YOUR NEW QUEEN POSITION [row, col]
//			// arrow = YOUR ARROW POSITION [row, col]
//			queenCurrent.add(rng());
//			queenCurrent.add(rng());
//			queenNew.add(rng());
//			queenNew.add(rng());
//			arrow.add(rng());
//			arrow.add(rng());
//			gameClient.sendMoveMessage(queenCurrent,queenNew,arrow);
//			//game.play();
//		} else {
//			queenCurrent = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
//			queenNew = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT);
//			arrow = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);
//		}
//		// gamegui.updateGameState(msgDetails); // update the game state
//		gamegui.updateGameState(queenCurrent, queenNew, arrow); // update the game state
//		gameClient.sendMoveMessage(queenNew, queenNew, arrow); // send the move to the server
//	}

	public int rng() {
		// generate random number from 1 to 10
		Random rand = new Random();
		return rand.nextInt(10) + 1;
	}
    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return this.gamegui;
		 
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
    	gameClient = new GameClient(userName, passwd, this);			
	}
	
 
}//end of class