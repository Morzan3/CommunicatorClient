package pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent;

public class CreateRoomButtonViewEventEvent extends ClientHandeledEvent
{
	final private  String userName;
	/**Nazwa hosta z którym jest połączony**/
	final private String hostName;
	/**Port na którym się łączy**/
	final private int port;
	/**Nazwa pokoju na którym prowadzi rozmowę**/
	final private String roomName;
	
	public CreateRoomButtonViewEventEvent(final String hostName,final int port,final String userName,final String roomName)
	{
		this.userName = userName;
		this.hostName = hostName;
		this.port = port;
		this.roomName = roomName;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() 
	{
		return userName;
	}

	/**
	 * @return the hostName
	 */
	public String getHostName() 
	{
		return hostName;
	}

	/**
	 * @return the port
	 */
	public int getPort() 
	{
		return port;
	}

	/**
	 * @return the roomName
	 */
	public String getRoomName() 
	{
		return roomName;
	}
	
}
