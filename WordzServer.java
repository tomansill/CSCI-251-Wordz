import java.net.InetSocketAddress;
import java.net.DatagramSocket;
/** Wordz Server Main Program - This program will open a mailbox and wait
 *	for connections and it will create Game model when a client is connected
 *
 *	Course: CSCI-251-01
 *	@author Thomas Ansill
 */
public class WordzServer{
	/** The main method - It will check for all required arguments and print an
	 *  error message if any is missing, then it will open a server socket and
	 *	run a GUI program to initate the game 
	 *	@param args Commandline arguments broken up in array of Strings, separated by whitespace
	 */
	public static void main(String[] args){
		//Check number of arguments
		if(args.length != 2){	
			System.err.println("Usage: java WordzServer <host> <port>");
			System.exit(1);
		}
		
		//Record host
		String host = args[0];
		
		//Record port
		int port = 0;
		
		//Check if port is an integer
		if(!args[1].matches("\\d+")){
			System.err.println("port is not an integer!");
			System.exit(1);
		}
		port = Integer.parseInt(args[1]);
		if(port < 0){
			System.err.println("port cannot be below zero!");
			System.exit(1);
		}
		
		//Set up the mailbox
		try{
			DatagramSocket mailbox = new DatagramSocket(new InetSocketAddress(host, port));
			WordzMailboxManager manager = new WordzMailboxManager(mailbox);
			while(true){
				manager.receiveMessage();	
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}
}
