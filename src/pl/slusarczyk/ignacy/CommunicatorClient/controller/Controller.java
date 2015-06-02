package pl.slusarczyk.ignacy.CommunicatorClient.controller;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import pl.slusarczyk.ignacy.CommunicatorClient.connection.Client;
import pl.slusarczyk.ignacy.CommunicatorClient.view.View;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.ClientHandeledEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.CloseWindowButtonViewEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.ConnectionEstablishedServerEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.ConversationInformationServerEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.CreateRoomButtonViewEventEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.JoinRoomButtonViewEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.SendMessageButtonViewEvent;


/**
 * Klasa kontrolera odpowiadajaca za odpowiednią komunikację pomiędzy klientem a serwerem, zawierająca w sobie klasy
 * odpowiadające strategiom obsługi zdarzeń pochodzących od serwera oraz widoku.
 * 
 * @author Ignacy Ślusarczyk
 */
public class Controller 
{	
	/**Kolejka blokująca do przesyłania zdarzeń pomiędzy widokiem a kontrolerem*/
	final private BlockingQueue<ClientHandeledEvent> eventQueue;
	/**Mapa strategii obsługi zdarzeń*/
	private Map<Class<? extends ClientHandeledEvent>, ClientHandeledEventStrategy> strategyMap;
	/**Referencja do klienta*/
	final private Client client;
	/**Referencja do widoku**/
	final private View view;
	
	/**
	 * Konstruktor tworzący controler na podstawie zadanych parametrów
	 * 
	 * @param eventQueue kolejka blokująca
	 * @param view widok
	 */
	public Controller(final BlockingQueue<ClientHandeledEvent> eventQueue,final View view)
	{
		this.eventQueue = eventQueue;
		this.view = view;
		this.client = new Client(eventQueue);
		
		//Tworzenie mapy strategii obsługi zdarzeń
		strategyMap = new HashMap<Class<? extends ClientHandeledEvent>, ClientHandeledEventStrategy>();
		//Zdarzenia pochodzące od serwera 
		strategyMap.put(ConversationInformationServerEvent.class, new ConversationInformationServerEventStrategy());
		strategyMap.put(ConnectionEstablishedServerEvent.class, new ConnectionEstablishedServerEventStrategy());
		//Zdarzenia generowane przez widok aplikacji
		strategyMap.put(JoinRoomButtonViewEvent.class, new JoinRoomButtonViewEventStrategy());
		strategyMap.put(CreateRoomButtonViewEventEvent.class, new CreateRoomButtonViewEventStrategy());
		strategyMap.put(SendMessageButtonViewEvent.class, new SendMessageButtonViewEventStrategy());
		strategyMap.put(CloseWindowButtonViewEvent.class, new CloseWindowButtonViewEventStrategy());
	}
	
	
	/**
	 * Główna metoda kontrolera, czeka on w niej na zdarzenia, a następnie odpowiednio je obsługuje.
	 */
	public void work()
	{
		while (true)
		{
			try
			{
				ClientHandeledEvent serverEvent = eventQueue.take();
				ClientHandeledEventStrategy clientHandeledEventStrategy = strategyMap.get(serverEvent.getClass());
				clientHandeledEventStrategy.execute(serverEvent);
			}
			catch(InterruptedException e)
			{
				//Nic nie robimy, ponieważ kontroler ma być zawieszony dopóki nie pojawi się zdarzenie
			}
		}
	}
	
	/**
	 * Abstrakcyjna klasa bazowa dla klas odpowiedzialnych za odowiednią obsługę zdarzeń pochodzących od serwera oraz od widoku
	 * 
	 * @author Ignacy Ślusarczyk
	 */
	abstract class ClientHandeledEventStrategy
	{
		/**
		 * Abstrakcyjna metoda opisująca obsługę danego zdarzenia.
		 * 
		 * @param applicationEvent zdarzenie aplikacji które musi zostać obsłużone
		 */
		abstract void execute(final ClientHandeledEvent eventObject);
	}
	
	
	/**
	 * Klasa wewnetrzna opisująca strategię obsługi przyjścia konwersacji do wyświetlenia
	 *
	 * @author Ignacy Ślusarczyk
	 */
	class ConversationInformationServerEventStrategy extends ClientHandeledEventStrategy
	{
		@Override
		void execute(final ClientHandeledEvent eventObject) 
		{
			ConversationInformationServerEvent conversationInformationServerEvent = (ConversationInformationServerEvent) eventObject;
			view.updateUserConversationAndList(conversationInformationServerEvent);
		}
	}
	
	/**
	 * Klasa wewnętrzna opisująca strategię obsługi zaakceptowania połączenia przez serwer
	 * 
	 * @author Ignacy Ślusarczyk
	 */
	class ConnectionEstablishedServerEventStrategy extends ClientHandeledEventStrategy
	{
		@Override
		void execute(final ClientHandeledEvent eventObject)
		{
			ConnectionEstablishedServerEvent connectionEstablishedServerEvent = (ConnectionEstablishedServerEvent) eventObject;
			view.connectionEstablishedServerEvent(connectionEstablishedServerEvent);
		}
	}
	
	/**
	 * Klasa wewnętrzna opisująca strategię obsługi przyciśniecia przez użytkownika wyboru 
	 * dołączenia do już istniejącego pokoju po podaniu odpowiednich danych.
	 * 
	 * @author Ignacy Ślusarczyk
	 */
	class JoinRoomButtonViewEventStrategy extends ClientHandeledEventStrategy
	{
		@Override
		void execute(final ClientHandeledEvent eventObject) 
		{
			JoinRoomButtonViewEvent joinRoomButton = (JoinRoomButtonViewEvent) eventObject;
			client.establishNewConnectionAndJoinRoom(joinRoomButton);
		}
	}
	
	/**
	 * Klasa wewnętrzna opisująca strategię obsługi przyciśnięcia przez użytkownika wyboru
	 * założenia nowego pokoju rozmów po podaniu odpowiednich danych.
	 * 
	 * @author Ignacy Ślusarczyk
	 */
	class CreateRoomButtonViewEventStrategy extends ClientHandeledEventStrategy
	{
		@Override
		void execute(final ClientHandeledEvent eventObject) 
		{
			CreateRoomButtonViewEventEvent createRoomButton = (CreateRoomButtonViewEventEvent) eventObject;
			client.establishNewConnectionAndCreateRoom(createRoomButton);
		}
	}
	
	/**
	 * Klasa wewnętrzna opisująca strategię obsługi przyciśnięcia przez użytkownika przycisku wysłania wiadomości w oknie rozmowy.
	 *
	 * @author Ignacy Ślusarczyk
	 */
	class SendMessageButtonViewEventStrategy extends ClientHandeledEventStrategy
	{
		@Override
		void execute(final ClientHandeledEvent eventObject) 
		{
			SendMessageButtonViewEvent sendMessageButtonViewEvent = (SendMessageButtonViewEvent) eventObject;
			client.sendMessage(sendMessageButtonViewEvent);
		}
	}
	
	/**
	 * Klasa wewnętrzna opisująca strategię obsługi przyciśnięcia przez użytkownika przycisku wyjścia z chatu.
	 * 
	 * @author Ignacy Ślusarczyk 
	 */
	class CloseWindowButtonViewEventStrategy extends ClientHandeledEventStrategy
	{
		@Override
		void execute(final ClientHandeledEvent eventObject)
		{
			client.closeConnection();
		}
	}
}