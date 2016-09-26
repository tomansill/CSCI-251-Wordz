import java.util.LinkedList;
import java.util.Queue;
/** Wordz Model - Game Model for Wordz
 *	Course: CSCI-251-01
 *	@author Thomas Ansill
 */
public class WordzModel implements WordzViewListener{
	/** board containing letters */
	private char[][] board = new char[4][4];
	/** Letter die on each board */
	private final char[][][] possible =  {{{'T','A','O','T','O','W'},{'V','R','H','T','W','E'},{'V','E','R','L','D','Y'},{'G','E','W','H','N','E'}},
																				{{'N','E','I','S','E','U'},{'M','I','O','T','C','U'},{'Y','S','D','I','T','T'},{'E','S','T','I','S','O'}},
																				{{'H','Q','U','M','I','N'},{'R','N','Z','N','L','H'},{'A','N','A','E','E','G'},{'J','A','O','B','O','B'}},
																				{{'R','L','T','Y','E','T'},{'D','E','X','L','I','R'},{'F','F','S','K','A','P'},{'A','O','S','C','P','H'}}};
	/** List of listeners */
	private LinkedList<WordzModelListener> listeners = new LinkedList<WordzModelListener>();
	/** Queue of letter choices the player made */
	private Queue<Character> choice = new LinkedList<Character>();
	/** players' names */
	private String names[] = new String[2];
	/** players' scores */
	private int score[] = new int[2];
	/** turn indicator */
	private int turn = 0;
	/** Pointer to SessionManager so gamemodel can be closed properly when player quits */
	private WordzSessionManager sm;
	
	/** Constructor for Wordz Model
	 *	@param sm Pointer to Session Manager so Model can signal manager to clean up
	 */
	public WordzModel(WordzSessionManager sm){
		this.sm = sm;
		//Construct board
		for(int r = 0; r < 4; r++){
			for(int c = 0; c < 4; c++){
				int random = (int)(Math.random() * 6);
				if(random == 6) random = 5; //Perhaps won't happen but to just be sure
				board[r][c] = possible[r][c][random];
			}
		}
	}
	/** Adds modellistener to the model 
	 *	@param model Listener
	 *	@param name Player name
	 */
	public synchronized void addModelListener(WordzModelListener model, String name){
		if(names[0] != null){
			names[1] = name;
			model.id(1);
		}
		if(names[0] == null){
			names[0] = name;
			model.id(0);
		}
		listeners.add(model);
		//Send out model 
		if(names[0] != null && names[1] != null){
			score[0] = 0;
			score[1] = 0;	
			for(int r = 0; r < 4; r++){
				for(int c = 0; c < 4; c++){
					for(WordzModelListener listener : listeners){
						listener.available(r,c,board[r][c]);		
					}
				}
			}
			for(WordzModelListener listener : listeners){
				listener.name(0, names[0]);		
				listener.name(1, names[1]);		
				listener.score(0, score[0]);		
				listener.score(1, score[1]);		
				listener.turn(turn);
			}
		}
	}
	
	/** join method - not used. SessionManager will use it
	 *	@param proxy Proxy to attach to
	 *	@param name player name
	 */
	public void join(WordzViewProxy proxy, String name){
		
	}
	/** letter method - change model when player chooses a letter
	 *	@param r Row of the game board
	 *	@param c Column of the game board
	 */
	public void letter(int r, int c){
		if(names[0] != null && names[1] != null){
			choice.offer(board[r][c]);
			for(WordzModelListener listener : listeners){
				listener.chosen(r,c);		
			}
		}
	}
	/** ok method - indicates that player has completed the choosing and 
	 *	model will update score and provide new board letters and switch turn.
	 */
	public void ok(){
		while(!choice.isEmpty()){
			char letter = choice.poll();
			if(letter == 'Q') score[turn] += 2;
			else score[turn]++;
		}
		if(turn == 0) turn = 1;
		else turn = 0;
		//Randomize the board
		for(int r = 0; r < 4; r++){
			for(int c = 0; c < 4; c++){
				int random = (int)(Math.random() * 6);
				if(random == 6) random = 5; //Perhaps won't happen but to just be sure
				board[r][c] = possible[r][c][random];
				for(WordzModelListener listener : listeners){
					listener.available(r,c,possible[r][c][random]);
				}
			}
		}
		for(WordzModelListener listener : listeners){
			listener.score(0, score[0]);
			listener.score(1, score[1]);
			listener.turn(turn);
		}
	}
	/** quit method - player quits a game, lets other player know and destroy the model */
	public void quit(){
		for(WordzModelListener listener : listeners){
			listener.quit();
		}
		sm.terminateModel(names[0], names[1]);
	}
}
