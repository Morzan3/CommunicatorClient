package pl.slusarczyk.ignacy.CommunicatorClient.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.ClientHandeledEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.CloseWindowButtonViewEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.ConnectionEstablishedServerEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.ConversationInformationServerEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.CreateRoomButtonViewEventEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.JoinRoomButtonViewEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.SendMessageButtonViewEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.model.data.MessageData;
import pl.slusarczyk.ignacy.CommunicatorServer.model.data.UserData;


/**
 * Główna klasa widoku odpowiedzialna za odpowiednie wyświetlanie okien i otrzymanych konwersacji
 * @author Ignacy Ślusarczyk
 *
 */
public class View 
{
	/**Okno wyboru dołączenia lub stworzenia nowego pokoju*/
	private CreateJoinRoomWindow createJoinRoomView;
	/**Okno wpisywania informacji do stworzenia pokoju*/
	private CreateRoomWindow createRoomView;
	/**Okno wpisywania ifnormacji do dołączenia do pokoju*/
	private JoinRoomWindow joinRoomView;
	/**Główne okno chatu*/
	private MainChatWindow mainChatView;
	/**Kolejka blokująca do której wrzucamy zdarzenia obsługiwane przez kontroler*/
	private final BlockingQueue<ClientHandeledEvent> eventQueue;
	
	/**
	 * Konstruktor tworzący widok na podstawie zadanego parametru
	 * 
	 * @param eventQueue kolejka blokująca
	 */
	public View(BlockingQueue<ClientHandeledEvent> eventQueue)
	{
		this.createJoinRoomView = new CreateJoinRoomWindow();
		this.createJoinRoomView.addCreateNewRoomButtonListener(new CreateNewRoomButtonListener ());
		this.createJoinRoomView.addJoinExistingRoomButtonListener(new JoinExistingRoomButtonListener());
	
		this.createRoomView = null;
		this.joinRoomView = null;
		this.mainChatView = null;
	
		this.eventQueue = eventQueue;
	}
	
	/**
	 * Metoda uaktualniająca wyświetlaną rozmowę oraz aktywnych użytkowników
	 * 
	 * @param conversationInformationServerEvent opakowane informacje do wyświetlenia
	 */
	public void updateUserConversationAndList(ConversationInformationServerEvent conversationInformationServerEvent)
	{
		updateConversation(conversationInformationServerEvent);
		updateUserList(conversationInformationServerEvent);
		mainChatView.resetUserTextField();
	}
	
	/**
	 * Metoda odpowiedzialna bezpośrednio na uaktualnienie wyświetlanej rozmowy
	 * @param conversationInformationServerEvent
	 */
	public void updateConversation(ConversationInformationServerEvent conversationInformationServerEvent)
	{
		addUsersNicksToMessage(conversationInformationServerEvent);
		HashSet<MessageData> allMessages = gatherAllMessages(conversationInformationServerEvent);
		ArrayList<MessageData> sortedMessages = sortAllMessages(allMessages);
		String conversationToDisplay = sortedMessagesToString(sortedMessages);
		
		mainChatView.updateUsersConversation(conversationToDisplay);
	}
	
	/**
	 * Metoda odpowiedzialna za dodanie nazwy użytkownika do każdej wiadomości w celu możliwości ich identyfikacji po wyświetleniu
	 * 
	 * @param conversationInfo opakowane informacje
	 */
	void addUsersNicksToMessage(ConversationInformationServerEvent conversationInfo)
	{
		HashSet<UserData> userDataSet = conversationInfo.getRoom().getUserSet();
		
		for(UserData userData : userDataSet)
		{
			for(MessageData messageData : userData.getUsersMessages())
			{
				messageData.setUserMessage(userData.getUserId().getUserName() +":"+ messageData.getMessage());
			}
		}
	}
	
	/**
	 * Metoda zbierająca wszystkie informacje użytkowników z danego pokoju
	 * 
	 * @param conversationInfo opakowane informacje
	 * @return zbiór wszystkich wiadomości
	 */
	HashSet<MessageData> gatherAllMessages (ConversationInformationServerEvent conversationInfo)
	{
		HashSet<MessageData> conversation = new HashSet<MessageData>();
		
		for(UserData userData: conversationInfo.getRoom().getUserSet())
		{
			conversation.addAll(userData.getUsersMessages());
		}
		return conversation;
	}
	
	/**
	 * Metoda sortująca wszystkie wiadomości użytkowników wg daty ich powstania
	 * 
	 * @param usersMessages zbior wiadomości użytkowników
	 * @return
	 */
	ArrayList<MessageData> sortAllMessages(HashSet<MessageData> usersMessages)
	{
		ArrayList<MessageData> sortedMessages = new ArrayList<MessageData>();
		sortedMessages.addAll(usersMessages);
		Collections.sort(sortedMessages);
		return sortedMessages;
	}
	
	/**
	 * Metoda zamieniająca posortowany zbiór wiadomości w string gotowy do wyświetlenia w oknie chatu
	 * 
	 * @param sortedMessages lista wiadomości
	 * @return
	 */
	String sortedMessagesToString(ArrayList<MessageData> sortedMessages)
	{
		String conversationToDisplay = new String("");
		for(MessageData messageData : sortedMessages)
		{
			conversationToDisplay = conversationToDisplay + messageData.getMessage() + "\n";
		}
		return conversationToDisplay;
	}
	
	/**
	 * Metoda uaktualniająca listę użytkowników chatu
	 * 
	 * @param conversationInformationServerEvent opakowane dane
	 */
	void updateUserList(ConversationInformationServerEvent conversationInformationServerEvent)
	{
		String usersListToDisplay = new String("");
		
		List<String> userListToSort = new ArrayList<String>();
		
		for(UserData userData: conversationInformationServerEvent.getRoom().getUserSet())
		{
			if(userData.isUserActive() == true)
			{
				userListToSort.add(userData.getUserId().getUserName());
			}
		}
		
		Collections.sort(userListToSort);
		for (String imie:userListToSort)
		{
			usersListToDisplay = usersListToDisplay + imie + "\n";
		}
		mainChatView.updateUsersList(usersListToDisplay);
	}
	
	/**
	 * Metoda wywoływana po otrzymaniu potwierdzenia od serwera, iż połączenie zostało nawiązane. Zamyka dotychczasowe 
	 * okna i wyświetla okno rozmowy,
	 * 
	 * @param connectionEstablishedServerEvent czy połączenie nawiązane
	 */
	public void connectionEstablishedServerEvent(ConnectionEstablishedServerEvent connectionEstablishedServerEvent)
	{
		if(connectionEstablishedServerEvent.getConnectionInfrmation() == true)
		{
			if(joinRoomView != null)
			{
				joinRoomView.closeJoinRoomWindow();
				joinRoomView = null;
			}
			else
			{
				createRoomView.closeCreateRoomWindow();
				createRoomView = null;
			}
			
			mainChatView = new MainChatWindow();
			mainChatView.addSendMessageButtonListener(new SendMessageButtonListener());
			mainChatView.addWindowCloseButtonListener(new CloseWindowButtonListener());
		}
	}

	//Listenery
	
	/**
	 * Strategi obsługi kliknięcia przycisku wyboru utworzenia nowego pokoju przez użytkownika
	 * 
	 * @author Ignacy Ślusarczyk
	 */
	class CreateNewRoomButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			createJoinRoomView.closeCreateJoinRoom();
			createJoinRoomView = null;
			
			createRoomView = new CreateRoomWindow();
			createRoomView.addSubmitCreateRoomButton(new SubmitCreateNewRoomButtonListener());	
		}
	}
	
	/**
	 * Obsługa kliknięcia przycisku wyboru dołączenia do istniejącego pokoju przez użytkownika
	 * 
	 * @author Ignacy Ślusarczyk
	 */
	class JoinExistingRoomButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			createJoinRoomView.closeCreateJoinRoom();
			createJoinRoomView = null;
			
			joinRoomView = new JoinRoomWindow();
			joinRoomView.addSubmitJoinRoomButton(new SubmitJoinNewRoomButtonListener());		
		}
	}
	
	/**
	 * Obsługa kliknięcia przycisku podania informacji o tworzonym nowym pokoju
	 * 
	 * @author Ignacy Ślusarczyk
	 */
	class SubmitCreateNewRoomButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				eventQueue.put(new CreateRoomButtonViewEventEvent(createRoomView.getHostName(),Integer.parseInt(createRoomView.getPortNumber()), createRoomView.getUserName(),createRoomView.getHostName()));
			}
			catch (InterruptedException ex)
			{
				System.err.println("Client, controller,create new room button" + ex);
			}
		}
	}
	
	/**
	 * Obsługa kliknięcia przez użytkownika przycisku podania informacji o pokoju do którego chce dołączyć  
	 * 
	 * @author Ignacy Ślusarczyk
	 */
	class SubmitJoinNewRoomButtonListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				eventQueue.put(new JoinRoomButtonViewEvent(joinRoomView.getHostName(),Integer.parseInt(joinRoomView.getPortNumber()), joinRoomView.getUserName(),joinRoomView.getHostName()));
			}
			catch(InterruptedException ex)
			{
				System.err.println(ex);
			}
		}
		
	}
	
	/**
	 * Obsługa kliknięcia przez użytkownika przycisku wysłania wiadomości
	 * 
	 * @author Ignacy Ślusarczyk
	 */
	class SendMessageButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				eventQueue.put(new SendMessageButtonViewEvent(mainChatView.getUserTextField()));
			}
			catch (InterruptedException ex)
			{
				System.err.println("Send message button listener problem" + ex);
			}
		}	
	}
	
	/**
	 * Strategia obsługi kliknięcia przez użytkownika przycisku wyjścia z chatu
	 * 
	 * @author Ignacy Ślusarczyk
	 */
	class CloseWindowButtonListener implements WindowListener
	{
		
		@Override
		public void windowClosing(WindowEvent eve) 
		{
			try
			{
				eventQueue.put(new CloseWindowButtonViewEvent());
				mainChatView.closeMainChatView();
			}
			catch(InterruptedException ex)
			{
				System.err.println(ex);
			}
		}
		
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
		}

		
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
		}

		
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
		}
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
		}
	}
}
