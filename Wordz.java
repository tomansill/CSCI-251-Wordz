import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.DatagramSocket;

/** Wordz Client Main Program - This program will open a socket and connect to
 *	Wordz Server and it will display GUI and update the GUI with communication
 *	from the server and GUI will send updates to server.
 *
 *	Course: CSCI-251-01
 *	@author Thomas Ansill
 */
public class Wordz{
	/** The main method - It will check for all required arguments and print an
	 *  error message if any is missing, then it will open a server socket and
	 *	run a GUI program to initate the game 
	 *	@param args Commandline arguments broken up in array of Strings, separated by whitespace
	 */
	public static void main(String[] args){
		//Check number of arguments
		if(args.length != 5){	
			System.err.println("Usage: java WordzClient <serverhost> <serverport> <clienthost> <clientport> <playername>");
			System.exit(1);
		}
		
		//Record serverhost
		String serverhost = args[0];
		
		//Record serverport
		int serverport = 0;
		
		//Check if server port is an integer
		if(!args[1].matches("\\d+")){
			System.err.println("server port is not an integer!");
			System.exit(1);
		}
		serverport = Integer.parseInt(args[1]);
		if(serverport < 0){
			System.err.println("server port cannot be below zero!");
			System.exit(1);
		}
		
		//Record clienthost
		String clienthost = args[2];
		
		//Record clientport
		int clientport = 0;
		
		//Check if client port is an integer
		if(!args[3].matches("\\d+")){
			System.err.println("client port is not an integer!");
			System.exit(1);
		}
		clientport = Integer.parseInt(args[3]);
		if(clientport < 0){
			System.err.println("client port cannot be below zero!");
			System.exit(1);
		}
		//Record player name
		String playername = args[4];
		
		//Create the view
		WordzView view = WordzView.create(playername);
		
		//Create datagram mailbox
		DatagramSocket mailbox = null;
		try{
			mailbox = new DatagramSocket(new InetSocketAddress(clienthost, clientport));
		}catch(Exception e){
			System.err.println("Cannot create the client UDP datagram service");
		}
		
		//Create model proxy
		try{
			final WordzModelProxy proxy = new WordzModelProxy(mailbox, new InetSocketAddress(serverhost, serverport));
			view.setViewListener(proxy);
			proxy.setModelListener(view);
			Runtime.getRuntime().addShutdownHook (new Thread(){
				public void run(){
					try { proxy.quit(); }
					catch (Exception exc) {}
				}
			});
			proxy.join(null, playername);
		}catch(Exception e){
			System.err.println("Cannot create the server UDP datagram service");
		}
	}
}
