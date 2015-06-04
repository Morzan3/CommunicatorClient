package pl.slusarczyk.ignacy.CommunicatorServer.model.data;

import java.io.Serializable;


public class UserIdData implements Serializable
{
	private static final long serialVersionUID = 1L;
	/**Nazwa użytkownika*/
	private final String userNameToDisplay;
	
	/**
	 * Konstruktor tworzący obiekt na podstawei zadanego parametru
	 * 
	 * @param userName Nazwa użytownika
	 */
	public UserIdData(final String userNameToDisplay)
	{
		this.userNameToDisplay = userNameToDisplay;
	}

	/**
	 * Metoda zwracająca nazwę użytkownika z ID konieczna przy wyświetlaniu wiadomości w widoku ponieważ
	 * bez niej nie da się ich zidentyfikować
	 * 
	 * @return user name
	 */
	public String getUserName()
	{
		return userNameToDisplay;
	}
}

