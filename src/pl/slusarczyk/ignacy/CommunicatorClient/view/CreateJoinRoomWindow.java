package pl.slusarczyk.ignacy.CommunicatorClient.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;

/** Klasa odpowiedzialna za wyświetlanie okna wyboru stworzenia lub dołączenia do pokoju**/
class CreateJoinRoomWindow 
{
	/**Ramka aplikacji*/
	final private JFrame frame;
	/**Przycisk dołączania do istniejącego pokoju*/
	private JButton joinExistingRoom;
	/**Przycisk tworzenia nowego pokoju*/
	private JButton createNewRoomButton;
	
	public CreateJoinRoomWindow()
	{
		/**Inicjalizowanie głównej ramki*/
		frame = new JFrame("Chat");
		frame.setSize(400, 200);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		Container container = frame.getContentPane();

		/**Inicjalizujemy przycisk dołączania i tworzenia pokoju, ustawiamy ich rozmiar i dodajemy je do kontenera*/
		joinExistingRoom = new JButton(("Join existing room"));
		createNewRoomButton = new JButton("Create new room");
		container.add(createNewRoomButton,BorderLayout.NORTH);
		container.add(joinExistingRoom, BorderLayout.SOUTH);
		createNewRoomButton.setPreferredSize(new Dimension(400, 100));
		joinExistingRoom.setPreferredSize(new Dimension(400, 100));
	
		frame.setVisible(true);
	}
	
	
	public void closeCreateJoinRoom()
	{
		frame.setVisible(false);
		frame.dispose();
	}
	
	// LISTENERY

	public void addCreateNewRoomButtonListener(ActionListener listenForCreateNewRoomButton) 
	{
		createNewRoomButton.addActionListener(listenForCreateNewRoomButton);
	}
	
	
	public void addJoinExistingRoomButtonListener(ActionListener listenForJoinExistingRoom)
	{
		joinExistingRoom.addActionListener(listenForJoinExistingRoom);
	}	
}
