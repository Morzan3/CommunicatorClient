package pl.slusarczyk.ignacy.CommunicatorClient.amainclassapplication;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import pl.slusarczyk.ignacy.CommunicatorClient.controller.Controller;
import pl.slusarczyk.ignacy.CommunicatorClient.view.View;
import pl.slusarczyk.ignacy.CommunicatorServer.clienthandeledevent.ClientHandeledEvent;

/**
 * Główna klasa applikacji odpowiada za odpowiednie zainicjalizowanie wszystkich komponentów
 * 
 * @author Ignacy Ślusarczyk
 */
public class CommunicatorClient 
{
	/**
	 * Głowna metoda aplikacjitworzy widok, kolejkę zdarzeń oraz kontroler.
	 * 
	 * @param args argumenty wywołania programu
	 */
	public static void main(String args[])
	{
		BlockingQueue<ClientHandeledEvent> eventQueue = new LinkedBlockingQueue<ClientHandeledEvent>();
		View view = new View(eventQueue);
		Controller controller = new Controller(eventQueue, view);
		controller.work();
	}
}
