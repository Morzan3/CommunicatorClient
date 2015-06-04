package pl.slusarczyk.ignacy.CommunicatorServer.model;

import java.io.Serializable;

/**
 * Klasa opakowująca nazwę użytkownika impelmentująca metody equals oraz hashCode, stanowiąca sposób identyfikacji przynależności obiektów
 * 
 * @author Ignacy Ślusarczyk
 */
public class UserId implements Serializable
{
	private static final long serialVersionUID = 1L;
	/**Nazwa użytkownika*/
	private final String userName;
	
	/**
	 * Konstruktor tworzący obiekt na podstawei zadanego parametru
	 * 
	 * @param userName Nazwa użytownika
	 */
	public UserId(final String userName)
	{
		this.userName = userName;
	}
}
