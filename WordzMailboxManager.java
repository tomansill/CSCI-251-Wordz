import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;

/** Wordz Mailbox Manager Class - This class manages all incoming UDP  
 *	packets and saves the clientaddress then forwards packet to proxy. 
 *	Course: CSCI-251-01
 *	@author Thomas Ansill
 */

public class WordzMailboxManager{
	/** DatagramSocket or "Mailbox" */
	private DatagramSocket mailbox;
	/** Map for client addresses and proxies */
	private HashMap<SocketAddress, WordzViewProxy> proxyMap = new HashMap<SocketAddress, WordzViewProxy>(); 
	/** location for packet payload */
	private byte[] payload = new byte[1024]; /* Arbitary number of length */
	/** Session Manager */
	private WordzSessionManager sm = new WordzSessionManager();
	/** Constructor for Mailbox Manager
	 *	@param mailbox Datagram Socket or "Mailbox"
	 */
	public WordzMailboxManager(DatagramSocket mailbox){
		this.mailbox = mailbox;
	}
	/** Receives messages from the mailbox and process them */
	public void receiveMessage(){
		try{
			DatagramPacket packet = new DatagramPacket(payload, payload.length);
			mailbox.receive(packet);
			SocketAddress clientAddress = packet.getSocketAddress();
			WordzViewProxy proxy = proxyMap.get(clientAddress);
			if(proxy == null){
				proxy = new WordzViewProxy(mailbox, clientAddress);
				proxy.setViewListener(sm);
				proxyMap.put(clientAddress, proxy);
			}
			if(proxy.process(packet)){ //Discard if true
				proxyMap.remove(clientAddress);
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}
}
