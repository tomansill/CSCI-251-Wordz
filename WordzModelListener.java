import java.io.IOException;

/** Wordz Model Listener Interface
 *	Course: CSCI-251-01
 *	@author Thomas Ansill
 */
public interface WordzModelListener{
	/** Server assigned identifier for client
	 *	@param id Identification number
	 */
	public void id(int id);
	/** Server assigned name for client based on identification number
	 *	@param id Identification number of whose name
	 *	@param name Name of the identification number
	 */
	public void name(int id, String name);
	/** Server assigned score for client based on identification number
	 *	@param id Identification number of whose name
	 *	@param score Score amount for identification number
	 */
	public void score(int id, int score);
	/** Server-assigned letter for letterbuttons
	 *	@param row Row number on the board
	 *	@param column Column number on the board
	 *	@param letter Letter of that button on the board
	 */
	public void available(int row, int column, char letter);
	/** Action of choosing a button on the board and updates the wordfield
	 *	@param row Row number on the board
	 *	@param column Column number on the board
	 */
	public void chosen(int row, int column);
	/** Action of assigning turn for client
	 *	@param id Identification number
	 */
	public void turn(int id);
	/** Action of quitting for client */
	public void quit();
}
