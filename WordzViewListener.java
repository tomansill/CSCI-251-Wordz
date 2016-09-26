/** Wordz View Listener - Sends Client messages to the server
 *	
 *	Course: CSCI-251-01
 *	@author Thomas Ansill
 */
public interface WordzViewListener{
	/**	Sends a message to server when a player joins a game
	 *	@param proxy Proxy of View
	 *	@param name Name of the player
	 */
	public void join(WordzViewProxy proxy, String name);
	/**	Sends a message to server when a player clicks a letter button
	 *	@param r Row number of the button being clicked on
	 *	@param c Column number of the button being clicked on
	 */
	public void letter(int r, int c);
	/**	Sends a message to server when a player clicks on ok button */
	public void ok();
	/**	Sends a message to server when a player exits game */
	public void quit();
}
