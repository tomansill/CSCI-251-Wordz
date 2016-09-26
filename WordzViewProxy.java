import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/** Wordz View Proxy - Communicates to the client's view 
 *
 *	Course: CSCI-251-01
 *	@author Thomas Ansill
 */
public class WordzViewProxy implements WordzModelListener{
	/** Datagram socket or "mailbox */
	private DatagramSocket mailbox;
	/** Client's address to send packets to */
	private SocketAddress clientAddress;
	/** view listener object */
	private WordzViewListener viewListener;

	/** Constructor for View Proxy 
	 *	@param mailbox Mailbox to receive messages from
	 *	@param clientAddress address of client to send messages to
	 */
	public WordzViewProxy(DatagramSocket mailbox, SocketAddress clientAddress){
		this.mailbox = mailbox;
		this.clientAddress = clientAddress;
	}

	/** Sets the view listener
	 *	@param viewListener Object
	 */
	public void setViewListener(WordzViewListener viewListener){
		this.viewListener = viewListener;
	}
	
	/** id method - sends id command 
	 *	@param id Id number
	 */
	public void id(int id){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			out.writeByte('i');
			out.writeInt(id);
			out.close();
			byte[] payload = baos.toByteArray();
			mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));	
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	/** name method - sends name command 
	 *	@param id Id number
	 *	@param name Player name
	 */
	public void name(int id, String name){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			out.writeByte('n');
			out.writeInt(id);
			out.writeUTF(name);
			out.close();
			byte[] payload = baos.toByteArray();
			mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));	
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	/** score method - sends score command 
	 *	@param id Id number
	 *	@param score player's score
	 */
	public void score(int id, int score){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			out.writeByte('s');
			out.writeInt(id);
			out.writeInt(score);
			out.close();
			byte[] payload = baos.toByteArray();
			mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));	
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	/** available method - sends available command 
	 *	@param row Row of the board
	 *	@param column Column of the board
	 *	@param letter Letter on the board
	 */
	public void available(int row, int column, char letter){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			out.writeByte('a');
			out.writeInt(row);
			out.writeInt(column);
			out.writeByte(letter);
			out.close();
			byte[] payload = baos.toByteArray();
			mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));	
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	/** chosen method - sends chosen command 
	 *	@param row Row of the board
	 *	@param column Column of the board
	 */
	public void chosen(int row, int column){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			out.writeByte('c');
			out.writeInt(row);
			out.writeInt(column);
			out.close();
			byte[] payload = baos.toByteArray();
			mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));	
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	/** id method - sends id command 
	 *	@param id Id number
	 */
	public void turn(int id){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			out.writeByte('t');
			out.writeInt(id);
			out.close();
			byte[] payload = baos.toByteArray();
			mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));	
		}catch(Exception e){
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
			mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));	
		}catch(Exception e){
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

	/** Processes the packet and triggers approparite methods 
	 *	@param datagram Packet
	 *	@return If true, disconnect the client from the server, otherwise do nothing
	 */
	public boolean process(DatagramPacket datagram){
		boolean discard = false;
		try{
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(datagram.getData(), 0, datagram.getLength()));
			byte b = in.readByte();
			switch(b){
				case 'j': //Format: j <name>
					String name = in.readUTF();
					viewListener.join(WordzViewProxy.this, name);	
					break;
				case 'l': //Format: letter <r> <c>
						int r = in.readInt();
						int c = in.readInt();
						viewListener.letter(r, c);	
					break;
				case 'o': //Format: o
					viewListener.ok();
					break;
				case 'q': //Format: q
					viewListener.quit();
					discard = true;
					break;
				default:
					errorMessage();
					break;
			}
		}catch(Exception e){
			errorMessage("bad message from mailbox");
		}
		return discard;
	}
}
