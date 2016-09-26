import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
/** Wordz Model Proxy - Proxy for server communication with model
 *
 *	Course: CSCI-251-01
 *	@author Thomas Ansill
 */
public class WordzModelProxy implements WordzViewListener{
	/** Datagram Socket or "Mailbox"*/
	private DatagramSocket mailbox;
	/** Socket address to send packet to*/
	private SocketAddress destination;
	/** Listener to send methods to*/
	private WordzModelListener modelListener;
	
	/** Wordz Model Proxy constructor
	 *	@param mailbox Receiver for UDP packets
	 *	@param destination Address to send packets to
	 */
	public WordzModelProxy(DatagramSocket mailbox, SocketAddress destination){
		this.mailbox = mailbox;
		this.destination = destination;
	}
	/** Sets model listener to send packets toi
	 *	@param modelListener Model object
	 */	
	public void setModelListener(WordzModelListener modelListener){
		this.modelListener = modelListener;
		new ReaderThread().start();
	}
	/**	join method - sends join command
	 *	@param proxy View Proxy
	 *	@param name Player name
	 */	
	public void join(WordzViewProxy proxy, String name){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			out.writeByte('j');
			out.writeUTF(name);
			out.close();
			byte[] payload = baos.toByteArray();
			mailbox.send(new DatagramPacket(payload, payload.length, destination));
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
	}
	/** letter method - sends letter command
	 *	@param r Row of the board
	 *	@param c Column of the board
	 */	
	public void letter(int r, int c){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			out.writeByte('l');
			out.writeInt(r);
			out.writeInt(c);
			out.close();
			byte[] payload = baos.toByteArray();
			mailbox.send(new DatagramPacket(payload, payload.length, destination));
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
	}
	/** ok method - sends ok command */	
	public void ok(){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			out.writeByte('o');
			out.close();
			byte[] payload = baos.toByteArray();
			mailbox.send(new DatagramPacket(payload, payload.length, destination));
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
	}
	
	/** quit method - sends quit command */	
	public void quit(){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			out.writeByte('q');
			out.close();
			byte[] payload = baos.toByteArray();
			mailbox.send(new DatagramPacket(payload, payload.length, destination));
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
	}
	
	/** Empty error message method */
	private void errorMessage(){
		errorMessage("bad message from mailbox");
	}

	/** Error message method
	 *	@param description Describes the nature of error
	 */
	private void errorMessage(String description){
		try{	
			mailbox.close();
		}catch(Exception e){}
		System.err.println("Error: " + description);
		System.exit(1);
	}

	/**	Thread that reads feed coming from the server and communicates with the
	 *	Client GUI.
	 */
	private class ReaderThread extends Thread{
		/** Thread run method */
		public void run(){
			boolean run = true;
			byte[] payload = new byte[1024]; /* Messages can't be more than 1024? */
			try{
				while (true){
					DatagramPacket packet = new DatagramPacket(payload, payload.length);
					mailbox.receive(packet);
					DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload, 0, packet.getLength()));
					byte b = in.readByte();
					int id,r,c;
					switch(b){
						case 'i': //Format: i <i>
							modelListener.id(in.readInt());	
							break;
						case 'n': //Format: n <i> <n>
							id = in.readInt();
							String name = in.readUTF();
							modelListener.name(id, name);	
							break;
						case 's': //Format: s <i> <s>
							id = in.readInt();
							int score = in.readInt();
							modelListener.score(id, score);	
							break;
						case 'a': //Format: a <r> <c> <l>
							r = in.readInt();
							c = in.readInt();
							char l = (char) in.readByte();
							modelListener.available(r,c,l);	
							break;
						case 'c': //Format: c <r> <c>
							r = in.readInt();
							c = in.readInt();
							modelListener.chosen(r,c);	
							break;
						case 't': //Format: t <i>
							id = in.readInt();
							modelListener.turn(id);	
							break;
						case 'q': //Format: q
							modelListener.quit();
							run = false;
							break;
						default:
							errorMessage();
							break;
					}
				}
			}catch(Exception e){
				errorMessage("Failed to read the data from mailbox");
				e.printStackTrace (System.err);
			}finally{
				mailbox.close();
			}
		}
	}
}
