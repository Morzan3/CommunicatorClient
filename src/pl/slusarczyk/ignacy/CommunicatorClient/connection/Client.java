package pl.slusarczyk.ignacy.CommunicatorClient.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import pl.slusarczyk.ignacy.CommunicatorClient.serverhandeledevent.ClientLeftRoom;
import pl.slusarczyk.ignacy.CommunicatorClient.serverhandeledevent.CreateNewRoom;
import pl.slusarczyk.ignacy.CommunicatorClient.serverhandeledevent.JoinExistingRoom;
import pl.slusarczyk.ignacy.CommunicatorClient.serverhandeledevent.NewMessage;
import pl.slusarczyk.ignacy.CommunicatorClient.serverhandeledevent.UserName;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.ClientHandeledEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.CreateRoomButtonViewEventEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.JoinRoomButtonViewEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.SendMessageButtonViewEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.model.UserId;

/**
 * Główna klasa klienta, w której następuje połączenie z serwerem oraz przechowywanie informacji o aktualnym połączeniu
 * 
 * @author Ignacy Ślusarczyk
 */
public class Client 
{
	/**Atrybuty Klienta*/
	
	/**Nazwa użytkownika*/
	private String userName;
	/**Id użytkownika*/
	private UserId userID;
	/**Nazwa hosta z którym jest połączony*/
	private String hostName;
	/**Port na którym się łączy*/
	private int port;
	/**Nazwa pokoju na którym prowadzi rozmowę*/
	private String roomName;
	/**Socket klienta*/
	private Socket socket;
	/**Strumień wyjściowy*/
	private ObjectOutputStream outputStream;
	/**Strumień wejściowy*/
	private ObjectInputStream inputStream;
	/**Kolejka blokująca do której client dodaje zdarzenia z serwera w celu obsłużenia ich w kontrolerze*/
	private final BlockingQueue<ClientHandeledEvent> eventQueue;
	/**Flaga oznaczająca czy wątek nasłuchujący obiektów od serwera działa*/
	private boolean running;
	
	/**
	 * Konstruktor tworzący klienta na podstawie zadanych argumentów
	 * 
	 * @param eventQueue kolejka blokująca
	 */
	public Client(final BlockingQueue<ClientHandeledEvent> eventQueue)
	{
		this.hostName = null;
		this.port = 0;
		this.userName = null;
		this.roomName = null;
		this.userID = null;
		this.eventQueue = eventQueue;
		this.running = false;
	}

	/**
	 * Metoda tworząca socket i rozpoczynająca nasłuchiwanie obietków od serwera
	 */
	public void createSocketAndListen()
	{
		try 
		{
			this.socket = new Socket(this.hostName, this.port);
			this.inputStream = new ObjectInputStream(socket.getInputStream());
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
			ListenFromServer listenFromServer = new ListenFromServer();
			listenFromServer.start();
		}	
		catch(IOException e)
		{
			System.err.println("Nie udało się nawiązać połączenia z podanym serwerm, prawdopodobnie podano błędne dane" + e);
		}		
	}
	
	/**
	 * Metoda nawiązująca połączenie z serwerem i sygnalizująca chęć dołączenia do pokoju
	 * 
	 * @param joinRoomButton opakowane informacje dotyczące połączenia
	 */
	public void establishNewConnectionAndJoinRoom(final JoinRoomButtonViewEvent joinRoomButton)
	{
		hostName = joinRoomButton.getHostName();
		port = joinRoomButton.getPort();
		userName = joinRoomButton.getUserName();
		userID = new UserId(userName);
		roomName = joinRoomButton.getRoomName();
		running=true;
		createSocketAndListen();
		joinRoom();
	}
	
	/**
	 * Metoda sygnalizująca serwerowi chęć dołączenia do istniejącego pokoju
	 */
	void joinRoom()
	{
		try 
		{
			outputStream.writeObject(new UserName(userID,roomName));
			outputStream.writeObject(new JoinExistingRoom(roomName, userID));
		} 
		catch (IOException e) 
		{
			System.err.println(e);
		}
	}
	
	/**
	 * Metoda nawiązująca połączenie z serwerem i sygnalizująca chęć stworzenia nowego pokoju
	 * @param createRoomButton opakowane informacje dotyczące połączenia
	 */
	public void establishNewConnectionAndCreateRoom(final CreateRoomButtonViewEventEvent createRoomButton)
	{
		hostName = createRoomButton.getHostName();
		port = createRoomButton.getPort();
		userName = createRoomButton.getUserName();
		userID = new UserId(userName);
		roomName = createRoomButton.getRoomName();
		running=true;
		createSocketAndListen();
		
		createRoom();
	}
	
	/**
	 * Metoda sygnalizująca serwerowi chęć stworzenia nowego pokoju
	 */
	void createRoom()
	{
		try
		{
			outputStream.writeObject(new UserName(userID,roomName));
			outputStream.writeObject(new CreateNewRoom(roomName, userID));
		}
		catch (IOException ex)
		{
			System.err.println(ex);
		}
	}
	
	/**
	 * Metoda wysyłająca zadaną wiadomość do serwera
	 * 
	 * @param sendMessageButtonViewEvent
	 */
	public void sendMessage(final SendMessageButtonViewEvent sendMessageButtonViewEvent)
	{
		try
		{
			outputStream.writeObject(new NewMessage(roomName, userID, sendMessageButtonViewEvent.getMessage()));
		}
		catch(IOException ex)
		{
			System.err.println(ex);
		}
	}
	
	/**
	 * Metoda bezpiecznie zamykająca połączenie
	 */
	public void closeConnection()
	{
		try
		{
			running = false;
			outputStream.writeObject(new ClientLeftRoom(userID,roomName));
			socket.close();
		}
		catch(IOException ex)
		{
			System.err.println(ex);
		}
	}
	
	/**
	 * Klasa wewnętrzna służąca do nasłuchiwania zdarzeń od serwera. W przypadku zdarzeń od serwera, dodawane są one do mapy
	 * 
	 * @author Ignacy Ślusarczyk 
	 */
	public class ListenFromServer extends Thread 
	{
		public void run() 
		{
			while (running) 
			{
				try 
				{
					ClientHandeledEvent serverEvent = (ClientHandeledEvent)inputStream.readObject();
					eventQueue.add(serverEvent);
				}	
				catch(IOException e) 
				{
					System.exit(0);
				}
				catch(ClassNotFoundException e) 
				{
					System.err.println(e);
				}
			}				
		}
	}
}
