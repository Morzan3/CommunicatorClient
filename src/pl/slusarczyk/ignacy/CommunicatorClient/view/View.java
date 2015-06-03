package pl.slusarczyk.ignacy.CommunicatorClient.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import pl.slusarczyk.ignacy.CommunicatorClient.serverhandeledevent.ServerHandeledEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.ClientHandeledEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.ConnectionEstablishedServerEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.ConversationInformationServerEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.UserAlreadyExistsServerEvent;
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
	/**Główne okno chatu*/
	private MainChatWindow mainChatView;
	/**Kolejka blokująca do której wrzucamy zdarzenia obsługiwane przez kontroler*/
	private final BlockingQueue<ServerHandeledEvent> eventQueue;
	
	private final Map<Class<? extends ClientHandeledEvent>, ClientHandeledEventStrategy> strategyMap;
	
	/**
	 * Konstruktor tworzący widok na podstawie zadanego parametru
	 * 
	 * @param eventQueue kolejka blokująca
	 */
	public View(BlockingQueue<ServerHandeledEvent> eventQueue)
	{
		this.createJoinRoomView = new CreateJoinRoomWindow(eventQueue);
		this.eventQueue = eventQueue;
		
		this.strategyMap = new HashMap<Class<? extends ClientHandeledEvent>, ClientHandeledEventStrategy>();
		this.strategyMap.put(ConnectionEstablishedServerEvent.class, new ConnectionEstablishedStrategy());
		this.strategyMap.put(ConversationInformationServerEvent.class, new ConversationInformationServerEventStrategy());
		this.strategyMap.put(UserAlreadyExistsServerEvent.class, new UserAlreadyExistsServerEventStrategy());
	}
	
	public void setServerEvent(ClientHandeledEvent clientHandeledEventObject) 
	{
		ClientHandeledEventStrategy clientHandeledEventStrategy = strategyMap.get(clientHandeledEventObject.getClass());
		clientHandeledEventStrategy.execute((ClientHandeledEvent)clientHandeledEventObject);
	}
	
	abstract class ClientHandeledEventStrategy {
		
		/**
		 * Obsługuje makiety
		 * 
		 * @param 
		 */
		abstract void execute(final ClientHandeledEvent clientHandeledEventObject);
	}
	
	
	class ConnectionEstablishedStrategy extends ClientHandeledEventStrategy
	{
		
		@Override
		void execute(ClientHandeledEvent clientHandeledEventObject) 
		{
			ConnectionEstablishedServerEvent connectionEstablishedInformation = (ConnectionEstablishedServerEvent) clientHandeledEventObject;
			mainChatView = new MainChatWindow(eventQueue,connectionEstablishedInformation.getUserID(), connectionEstablishedInformation.getRoomName());
			createJoinRoomView.closeCreateRoomWindow();
			createJoinRoomView = null;
		}
	}
	
	class ConversationInformationServerEventStrategy extends ClientHandeledEventStrategy
	{
		
		@Override
		void execute(ClientHandeledEvent clientHandeledEventObject) 
		{
			ConversationInformationServerEvent conversationInformationObject = (ConversationInformationServerEvent) clientHandeledEventObject;
			updateUserConversationAndList(conversationInformationObject);
			
		}
	}
	
	class UserAlreadyExistsServerEventStrategy extends ClientHandeledEventStrategy
	{

		@Override
		void execute(ClientHandeledEvent clientHandeledEventObject) 
		{
			createJoinRoomView.userAlreadyExists();
		}
		
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
}
