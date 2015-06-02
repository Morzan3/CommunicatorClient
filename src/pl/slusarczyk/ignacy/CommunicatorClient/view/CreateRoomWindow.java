package pl.slusarczyk.ignacy.CommunicatorClient.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;


import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**Klasa odpowiedzialna za okno tworzenia nowego pokoju**/
class CreateRoomWindow
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

	public CreateRoomWindow()
	{
		/**Tworzymy główne okno tworzenia nowego pokoju*/
		frame = new JFrame("Create new room");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(400, 200));
	
		/**Ustawiamy odpowiedni Layout*/
		JPanel container = new JPanel();	
		BoxLayout layout = new BoxLayout(container, BoxLayout.Y_AXIS);
		container.setLayout(layout);
			
		/**Inicjalizujemy przycisk oraz obszary wpisywania informacji*/
		submitInformationButton = new JButton("Create");
		hostNameField = new JTextField();
		portNumberField = new JTextField();
		userNameField = new JTextField();
		roomNameField = new JTextField();
			
			
		/**Ustawiamy położenie wszystkich elementów*/
		hostNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
		portNumberField.setAlignmentX(Component.CENTER_ALIGNMENT);
		userNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
		roomNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
		submitInformationButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		/**Ustawiamy tekst domyślnie wpisany w poszczególne pola, pełniący funkcję infomracyjną*/
		hostNameField.setText("localhost");
		portNumberField.setText("5000");
		userNameField.setText("Test");
		roomNameField.setText("Projekt");
			
		/**Dodajemy wszystkie elemenety do kontenera*/
		 container.add(hostNameField);
		 container.add(portNumberField);
		 container.add(userNameField);
		 container.add(roomNameField);
		 container.add(submitInformationButton);
			 
		 /**Dodajemy kontener do głównej ramki oraz wyświeltamy główna ramkę*/
		 frame.add(container);
		 frame.setVisible(true);
			  

	}
	
	/**
	 * Metoda odpowiedzialna za zamknięcie okna
	 */
	public void closeCreateRoomWindow()
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
	 * @return user name
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
	
	/**Listener przycisku tworzącego nowy pokój**/
	public void addSubmitCreateRoomButton(ActionListener listenForSubmitCreateRoomButton) 
	{
		submitInformationButton.addActionListener(listenForSubmitCreateRoomButton);
	}
}
