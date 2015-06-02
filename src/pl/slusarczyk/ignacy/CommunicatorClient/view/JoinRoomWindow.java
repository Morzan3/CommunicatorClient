package pl.slusarczyk.ignacy.CommunicatorClient.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**Klasa odpowiedzialna za okno dołączania do istniejącego już pokoju**/
class JoinRoomWindow
{
	/**Ramka aplikacji*/
	final JFrame frame;
	/** Pole wpisywania nazwy hosta */
	final JTextField hostNameField ;
	/**Pole wpisywania numeru portu*/
	final JTextField portNumberField;
	/**Pole wpisywania nazwy użytkownika*/
	final JTextField userNameField;
	/**Pole wpisywania nazwy pokoju*/
	final JTextField roomNameField;
	/**Przycisk potwierdzający wpisane informacje i tworzący nowy pokój*/
	JButton submitInformationButton;
	/**Kontener na wszystkie elementy*/
	JPanel container;
	
	public JoinRoomWindow()
	{
	
		/**Inicjalizujemy główna ramkę aplikacji*/
		frame = new JFrame("Join existing room");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(400, 200));
		
		/**Ustawiam layout contenera*/
		container = new JPanel();
		BoxLayout layout = new BoxLayout(container, BoxLayout.Y_AXIS);
		container.setLayout(layout);
		
		/**Inicjalizuje przycisk oraz pola wpisywani informacji*/
		submitInformationButton = new JButton("Join");			
		hostNameField = new JTextField();
		portNumberField = new JTextField();
		userNameField = new JTextField();
		roomNameField = new JTextField();
					
		/**Ustawiam ich położenie*/
		hostNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
		portNumberField.setAlignmentX(Component.CENTER_ALIGNMENT);
		userNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
		roomNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
		submitInformationButton.setAlignmentX(Component.CENTER_ALIGNMENT);
				
		/**Usawiam domyślny tekst w odpowiednich polach pełniący funkcję informacyjną*/			
		hostNameField.setText("localhost");
		portNumberField.setText("5000");
		userNameField.setText("Test1");
		roomNameField.setText("Projekt");
		
		/**Dodaję wszystkie elementy do kontenera*/
		container.add(hostNameField);
		container.add(portNumberField);
		container.add(userNameField);
		container.add(roomNameField);
		container.add(submitInformationButton);
					 
		/**Dodaję kontener do ramki i wyświetlam ją*/	
		frame.add(container);
		frame.setVisible(true);
	}
	
	/**
	 * Metoda odpowiedzialna za zamknięcie okna
	 */
	public void closeJoinRoomWindow()
	{
		frame.setVisible(false);
		frame.dispose();
	}
	
	/**
	 * Metoda zwracająca tekst wpisany w polu host name
	 * 
	 * @return host name
	 */
	public String getHostName()
	{
		return hostNameField.getText();
	}
	
	/**
	 * Metoda zwracająca tekst wpisany w polu port number
	 * 
	 * @return port number
	 */
	public String getPortNumber()
	{
		return portNumberField.getText();
	}
	
	/**
	 * Metoda zwracająca tekst wpisany w polu user name
	 * 
	 * @return userName
	 */
	public String getUserName()
	{
		return userNameField.getText();
	}
	
	/**
	 * Metoda zwracająca tekst wpisany w polu room name
	 * 
	 * @return room name
	 */
	public String getRoomName()
	{
		return roomNameField.getText();
	}

	//LISTENERY 
	
	/**Listener przycisku dołączania do pokoju**/
	public void addSubmitJoinRoomButton(ActionListener listenForSubmitJoinRoomButton) 
	{
		submitInformationButton.addActionListener(listenForSubmitJoinRoomButton);
	}

}