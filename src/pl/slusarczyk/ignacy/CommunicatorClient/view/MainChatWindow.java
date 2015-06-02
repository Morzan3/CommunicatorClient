package pl.slusarczyk.ignacy.CommunicatorClient.view;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

/**Klasa odpowiedzialna za wyświetlanie głównego okna chatu**/

class MainChatWindow 
{
	/**Główna ramka*/
	private JFrame frame;
	/**Obszar gdzie wyśwyeitlana jest rozmowa pomiedzy użytkownikami */
	private JTextArea usersConversation;
	/**Obszar gdzie użytkownik wpisuje swoją wiadomość */
	private JTextArea userTextfield;
	/**Obszar gdzie wyświetlani są obecni użytkownicy chatu*/
	private JTextArea onlineUsers;
	/**Przycisk sygnalizujący wysłanie wiadomości*/
	private JButton sendButton;
	/**Etykieta wskazująca miejsce wyświetlania listy użytkowników*/
	private JLabel lblUsersInRoom;
	/**Scrollery poszczególnych obszarów tekstowych*/
	private JScrollPane userConversationScroll;
	private JScrollPane userTextMessageScroll;
	private JScrollPane onlineUsersScroll;


	/**Konstruktro inicjulizujący i wyświetlający ramkę*/
	public MainChatWindow()
	{	
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		/**Inicjalizowanie głównej ramki*/
		frame = new JFrame("ChatRoom");
		frame.setBounds(100, 100, 450, 320);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		/**Inicjalizowanie obszaru rozmowy użytkowników wraz ze scrollerem*/
		usersConversation = new JTextArea();
		usersConversation.setBounds(12, 12, 260, 189);
		usersConversation.setEditable(false);
		userConversationScroll = new JScrollPane(usersConversation);
		userConversationScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		userConversationScroll.setBounds(12, 12, 260, 189);
		frame.getContentPane().add(userConversationScroll);	
		
		/**Inicjalizowanie obszaru, w którym użytkownik wpisuje swoją wiadomość wraz ze scrollerem*/
		userTextfield = new JTextArea();
		userTextfield.setBounds(12, 213, 260, 56);
		userTextMessageScroll = new JScrollPane(userTextfield);
		userTextMessageScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		userTextMessageScroll.setBounds(12, 213, 260, 56);
		frame.getContentPane().add(userTextMessageScroll);	
		
		/**Inicjalizowanie obszaru, w którym wyświetlana jest lista uzytkowników wraz ze scrollerem*/
		onlineUsers = new JTextArea();
		onlineUsers.setBounds(284, 34, 154, 184);
		onlineUsers.setEditable(false);
		onlineUsersScroll = new JScrollPane(onlineUsers);
		onlineUsersScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		onlineUsersScroll.setBounds(284, 34, 154, 184);
		frame.getContentPane().add(onlineUsersScroll);
		
		/**Inicjalizowanie wycisku wyślij*/
		sendButton = new JButton("Send");
		sendButton.setBounds(288, 224, 117, 25);
		frame.getContentPane().add(sendButton);
		
		/**Inicjalizowanie etykiety wkazującej listę obecnych użytkowników*/
		lblUsersInRoom = new JLabel("Users in room");
		lblUsersInRoom.setBounds(300, 4, 126, 30);
		frame.getContentPane().add(lblUsersInRoom);
		
	}
	
	/**
	 * Listener przycisku wysłania wiadomości
	 * 
	 * @param listenForSendMessageButton
	 */
	public void addSendMessageButtonListener(ActionListener listenForSendMessageButton) 
	{
		sendButton.addActionListener(listenForSendMessageButton);
	}
	
	/**
	 * Listerner przycisku zamknięcia okna chatu
	 * 
	 * @param listenForCloseWindowButton
	 */
	public void addWindowCloseButtonListener(WindowListener listenForCloseWindowButton)
	{
		frame.addWindowListener(listenForCloseWindowButton);
	}
	
	/**
	 * Metoda zwracająca zawartość pola, w którym użytkownik wpisuje wiadomość o wysłania
	 * 
	 * @return treść wiadomosci
	 */
	public String getUserTextField()
	{
		return this.userTextfield.getText();
	}
	
	/**
	 * Metoda usuwająca tekst wpisany przez użytkownika w polu wiadomości
	 */
	public void resetUserTextField()
	{
		userTextfield.setText("");
	}
	
	/**
	 * Metoda uaktualniająca okno, w którym wyświetlana jest rozmowa użytkowników
	 * 
	 * @param usersConversationText
	 */
	public void updateUsersConversation(final String usersConversationText)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				usersConversation.setText("");
				usersConversation.append(usersConversationText);
			}
		});
	}
	
	/**
	 * Metoda uaktualniająca wyświetlaną listę aktywnych użytkowników
	 * 
	 * @param userList
	 */
	public void updateUsersList(final String userList)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				onlineUsers.setText("");
				onlineUsers.append(userList);
			}
		});
	}
	
	/**
	 * Metoda zamykająca okno chatu
	 */
	public void closeMainChatView()
	{
		frame.dispose();
		frame.setVisible(false);
	}
}