package pl.slusarczyk.ignacy.CommunicatorClient.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import pl.slusarczyk.ignacy.CommunicatorClient.serverhandeledevent.CreateNewRoom;
import pl.slusarczyk.ignacy.CommunicatorClient.serverhandeledevent.JoinExistingRoom;
import pl.slusarczyk.ignacy.CommunicatorClient.serverhandeledevent.ServerHandeledEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.InformationMessageServerEvent;
import pl.slusarczyk.ignacy.CommunicatorServer.model.data.UserIdData;

/**Klasa odpowiedzialna za okno tworzenia nowego pokoju**/
class CreateJoinRoomWindow
{
	/**Ramka aplikacji*/
	final JFrame frame;

	/**Pole wpisywania nazwy użytkownika*/
	final JTextField userNameField;
	/**Pole wpisywania nazwy pokoju*/
	final JTextField roomNameField;
	/**Przycisk potwierdzający wpisane informacje i tworzący nowy pokój*/
	JButton submitInformationButtonAndJoinRoom;
	/**Przycisk potwierdzający wpisane informacje i dołączanie do nowego pokoju*/
	JButton submitInformationButtonAndCreateRoom;
	/**Kolejka blokujaca do ktorej sa dodawane nowe eventy*/
	private final BlockingQueue<ServerHandeledEvent> eventQueue;

	public CreateJoinRoomWindow(final BlockingQueue<ServerHandeledEvent> eventQueueObject)
	{
		this.eventQueue = eventQueueObject;
		/**Tworzymy główne okno tworzenia nowego pokoju*/
		frame = new JFrame("Create or join room");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(400, 150));
	
		/**Ustawiamy odpowiedni Layout*/
		JPanel container = new JPanel();	
		BoxLayout layout = new BoxLayout(container, BoxLayout.Y_AXIS);
		container.setLayout(layout);
			
		/**Inicjalizujemy przycisk oraz obszary wpisywania informacji*/
		submitInformationButtonAndJoinRoom = new JButton("Create");
		submitInformationButtonAndJoinRoom.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e)
			{	
				eventQueue.offer(new CreateNewRoom(roomNameField.getText(), new UserIdData(userNameField.getText())));
			}
		});
		
		submitInformationButtonAndCreateRoom = new JButton("Join");
		submitInformationButtonAndCreateRoom.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				eventQueue.offer(new JoinExistingRoom(roomNameField.getText(), new UserIdData(userNameField.getText())));
			}
		});
		
		userNameField = new JTextField();
		roomNameField = new JTextField();
			
			
		/**Ustawiamy położenie wszystkich elementów*/
		userNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
		roomNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
		submitInformationButtonAndJoinRoom.setAlignmentX(Component.CENTER_ALIGNMENT);
		submitInformationButtonAndCreateRoom.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		/**Ustawiamy tekst domyślnie wpisany w poszczególne pola, pełniący funkcję infomracyjną*/
		userNameField.setText("Test");
		roomNameField.setText("Projekt");
			
		/**Dodajemy wszystkie elemenety do kontenera*/
		 container.add(userNameField);
		 container.add(roomNameField);
		 container.add(submitInformationButtonAndJoinRoom);
		 container.add(submitInformationButtonAndCreateRoom);
			 
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
	 * Metoda odpowiedzialna za wyświetlanie informacji przychodzacych z serwera
	 * 
	 * @param informationMessageObject Obiekt InformationMessage zawierającyc informację do wyswietlenia 
	 */
	public void displayInfoMessage(final InformationMessageServerEvent informationMessageObject)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				JOptionPane.showMessageDialog(frame, informationMessageObject.getMessage());
			}
		});
	}
}
