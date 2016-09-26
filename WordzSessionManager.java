import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

/** Wordz Session Manager - This class will manage clients and pairs
 *	them to a game model. 
 *
 *	Course: CSCI-251-01
 *	@author Thomas Ansill
 */
public class WordzSessionManager implements WordzViewListener{
	/** sessions map */
	private HashMap<String, WordzModel> sessions = new HashMap<String, WordzModel>();
	/** incomplete session variable */
	private String incompleteSession = null;

	/** simple constructor for session manager */
	public WordzSessionManager(){}

	/** join method - handles player join, pairs up to correct model or create new
	 *	@param proxy Player's proxy
	 *	@param name Player's name
	 */
	public synchronized void join(WordzViewProxy proxy, String name){
		if(incompleteSession == null){
			WordzModel model = new WordzModel(WordzSessionManager.this);
			sessions.put(name, model);	
			incompleteSession = name;
			model.addModelListener(proxy, name);
			proxy.setViewListener(model);
		}else{
			WordzModel model = sessions.get(incompleteSession);
			sessions.put(name, model);
			incompleteSession = null;
			model.addModelListener(proxy, name);
			proxy.setViewListener(model);
		}
	}
	/** letter method - not used here - used in WordzModel*/
	public synchronized void letter(int r, int c){

	}

	/** ok method - not used here - used in WordzModel*/
	public synchronized void ok(){

	}

	/** quit method - not used here - used in WordzModel*/
	public synchronized void quit(){
	}
	
	/** Terminates the model and removes clients from the map when players quit the game 
	 *	@param playerone Player number one
	 *	@param playertwo Player number two - may be null
	 */
	public synchronized void terminateModel(String playerone, String playertwo){
		sessions.remove(playerone);
		if(playertwo != null) sessions.remove(playertwo);
		if(playerone.equals(incompleteSession)) incompleteSession = null;
	}
}
