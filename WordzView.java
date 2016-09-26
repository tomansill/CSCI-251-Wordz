import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Class WordzView provides the user interface for the Wordz network game.
 *
 *	Course: CSCI-251-01
 *	@author  Thomas Ansill
 */
public class WordzView implements WordzModelListener{
	private static final int GAP = 10;
	private static final int COLS = 16;
	private static final Dimension DIM = new Dimension (40, 40);
	private static final Insets INSETS = new Insets (0, 0, 0, 0);
	private static final Font FONT = new Font ("sans-serif", Font.BOLD, 18);

	/**
	 * Class LetterButton provides a button labeled with a letter.
	 * To enable the button, call setEnabled (true).
	 * To disable the button, call setEnabled (false).
	 * To show the button, call setVisible (true).
	 * To hide the button, call setVisible (false).
	 */
	private class LetterButton
		extends JButton
		{
		private int row;
		private int col;
		private String letters;

		/**
		 * Construct a new letter button.
		 *
		 * @param  r  Button's row.
		 * @param  c  Button's column.
		 */
		public LetterButton
			(int r,
			 int c)
			{
			super ("");
			row = r;
			col = c;
			setEnabled (false);
			setVisible (true);
			setMinimumSize (DIM);
			setMaximumSize (DIM);
			setPreferredSize (DIM);
			setMargin (INSETS);
			setFont (FONT);

			// Clicking the letter button executes this listener:
			addActionListener (new ActionListener()
				{
				public void actionPerformed (ActionEvent e)
					{
						onLetterButton(row, col);
					}
				});
			}

		/**
		 * Set the letter displayed on this letter button.
		 *
		 * @param  letter  Letter ('A' through 'Z').
		 */
		public void setLetter
			(char letter)
			{
			letters = letter == 'Q' ? "QU" : ""+letter;
			setText (letters);
			}

		/**
		 * Get the letter or letters displayed on this letter button.
		 *
		 * @return  Letter(s).
		 */
		public String getLetters()
			{
			return letters;
			}

		}

	/**
	 * User interface widgets.
	 */
	private JFrame frame;
	private String myName;
	private LetterButton[][] letterButton;
	private JTextField myScoreField;
	private JTextField theirScoreField;
	private JTextField wordField;
	private JButton okButton;

	/**
	 * Construct a new WordzView object.
	 *
	 * @param  myName  Player's name.
	 */
	private WordzView
		(String myName)
		{
		frame = new JFrame ("Wordz -- " + myName);
		this.myName = myName;

		JPanel panel = new JPanel();
		frame.add (panel);
		panel.setLayout (new BoxLayout (panel, BoxLayout.X_AXIS));
		panel.setBorder (BorderFactory.createEmptyBorder (GAP, GAP, GAP, GAP));

		JPanel panel_a = new JPanel();
		panel.add (panel_a);
		panel_a.setLayout (new GridLayout (4, 4));
		panel_a.setAlignmentY (0.0f);
		letterButton = new LetterButton [4] [4];
		for (int row = 0; row < 4; ++ row)
			for (int col = 0; col < 4; ++ col)
				panel_a.add
					(letterButton[row][col] = new LetterButton (row, col));

		panel.add (Box.createHorizontalStrut (GAP));
		JPanel panel_b = new JPanel();
		panel.add (panel_b);
		panel_b.setLayout (new BoxLayout (panel_b, BoxLayout.Y_AXIS));
		panel_b.setAlignmentY (0.0f);
		panel_b.add (myScoreField = new JTextField (COLS));
		myScoreField.setAlignmentX (0.5f);
		myScoreField.setEditable (false);
		myScoreField.setMaximumSize (myScoreField.getPreferredSize());
		myScoreField.setAlignmentX (0.0f);
		panel_b.add (Box.createRigidArea (new Dimension (0, GAP)));
		panel_b.add (theirScoreField = new JTextField (COLS));
		theirScoreField.setAlignmentX (0.5f);
		theirScoreField.setEditable (false);
		theirScoreField.setMaximumSize (theirScoreField.getPreferredSize());
		theirScoreField.setAlignmentX (0.0f);
		panel_b.add (Box.createVerticalGlue());

		JPanel panel_c = new JPanel();
		panel_b.add (panel_c);
		panel_c.setLayout (new BoxLayout (panel_c, BoxLayout.X_AXIS));
		panel_c.setAlignmentX (0.0f);
		panel_c.add (wordField = new JTextField (COLS));
		wordField.setAlignmentY (0.5f);
		wordField.setEditable (false);
		wordField.setMaximumSize (wordField.getPreferredSize());
		panel_c.add (Box.createRigidArea (new Dimension (GAP, 0)));
		panel_c.add (okButton = new JButton ("OK"));
		okButton.setAlignmentX (0.5f);
		okButton.setMaximumSize (okButton.getPreferredSize());
		okButton.setEnabled (false);

		// Clicking the OK button executes this listener:
		okButton.addActionListener (new ActionListener()
			{
			public void actionPerformed (ActionEvent e)
				{
					ok();
				}
			});

		// Closing the window executes this listener:
		frame.addWindowListener (new WindowAdapter()
			{
			public void windowClosing (WindowEvent e)
				{
					quit();
				}
			});

		frame.pack();
		frame.setVisible (true);
		}

	/**
	 * An object holding a reference to a Wordz UI.
	 */
	private static class WordzViewRef
		{
		public WordzView view;
		}

	/**
	 * Construct a new WordzView object.
	 *
	 * @param  myName  Player's name.
	 *
	 * @return  New WordzView object.
	 */
	public static WordzView create
		(final String myName)
		{
		final WordzViewRef ref = new WordzViewRef();
		onSwingThreadDo (new Runnable()
			{
			public void run()
				{
				ref.view = new WordzView (myName);
				}
			});
		return ref.view;
		}

	/**
	 * Execute the given runnable object on the Swing thread.
	 */
	private static void onSwingThreadDo
		(Runnable task)
		{
		try
			{
			SwingUtilities.invokeAndWait (task);
			}
		catch (Throwable exc)
			{
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}
	//View variables
	/** WordzViewListener for Wordz Model */
	private WordzViewListener viewListener;
	/** Opponent's name */
	private String theirName;
	/** client's identification number */
	private int id;
	/** wordfield as a string */
	private StringBuilder wordfield = new StringBuilder(); 
	/** turn indicatior */
	private boolean myTurn = false;
	
	/** binds Wordz View listener */
	public synchronized void setViewListener(WordzViewListener viewListener){
		this.viewListener = viewListener;
	}
	/** Server assigned identifier for client
	 *	@param idn Identification number
	 */
	public synchronized void id(final int idn){
		onSwingThreadDo(new Runnable(){
			public void run(){
				id = idn;
			}
		});
	}
	/** Server assigned name for client based on identification number
	 *	@param idn Identification number of whose name
	 *	@param name Name of the identification number
	 */
	public synchronized void name(final int idn, final String name){
		onSwingThreadDo(new Runnable(){
			public void run(){
				if(id == idn) myName = name;
				else theirName = name;
			}
		});
	}
	/** Server assigned score for client based on identification number
	 *	@param idn Identification number of whose name
	 *	@param score Score amount for identification number
	 */
	public synchronized void score(final int idn, final int score){
		onSwingThreadDo(new Runnable(){
			public void run(){
				if(idn == id){
					myScoreField.setText(myName + " = " + score);
				}else{
					theirScoreField.setText(theirName + " = " + score);
				}
			}
		});
	}
	/** Server-assigned letter for letterbuttons
	 *	@param row Row number on the board
	 *	@param column Column number on the board
	 *	@param letter Letter of that button on the board
	 */
	public synchronized void available(final int row, final int column, final char letter){
		onSwingThreadDo(new Runnable(){
			public void run(){
				letterButton[row][column].setLetter(letter);
				if(myTurn) letterButton[row][column].setEnabled(true);
				else letterButton[row][column].setEnabled(true);
				letterButton[row][column].setVisible(true);
			}
		});
	}
	/** Action of choosing a button on the board and updates the wordfield
	 *	@param row Row number on the board
	 *	@param column Column number on the board
	 */
	public synchronized void chosen(final int row, final int column){
		onSwingThreadDo(new Runnable(){
			public void run(){
				letterButton[row][column].setVisible(false);
				wordfield.append(letterButton[row][column].getLetters());
				wordField.setText(wordfield.toString());
			}
		});
	}
	/** Action of assigning turn for client
	 *	@param idn Identification number
	 */
	public synchronized void turn(final int idn){
		onSwingThreadDo(new Runnable(){
			public void run(){
				if(idn == id) myTurn = true;
				else myTurn = false;
				wordfield = new StringBuilder();
				wordField.setText("");
				for(int row = 0; row < letterButton.length; row++){
					for(int column = 0; column < letterButton[row].length; column++){
						letterButton[row][column].setEnabled(myTurn);				
					}
				}
				okButton.setEnabled(myTurn);
			}
		});
	}
	/** Action of quitting for client */
	public synchronized void quit(){
		viewListener.quit();
		System.exit(0);
	}
	private synchronized void onLetterButton(int row, int column){
		viewListener.letter(row, column);
	}
	private synchronized void ok(){
		viewListener.ok();
	}
	}
