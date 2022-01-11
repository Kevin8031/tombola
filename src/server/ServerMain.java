package server;

import java.util.Scanner;

import net.Connection;
import net.Message;
import net.MessageType;
import net.OwnedMessage;
import net.Server;

public class ServerMain extends Server<MessageType> {

	@Override
	public boolean OnClientConnect(Connection<MessageType> client) {
		return true;
	}

	@Override
	public void OnMessage(Connection<MessageType> client, Message<MessageType> msg) {
		switch (msg.getHeadId()) {
			case SetName:
				String s = new String();
				s = msg.Get(s);
				OwnedMessage<MessageType> msg1 = new OwnedMessage<MessageType>(MessageType.SetName);
				if(!s.equals("null")) {
					client.setName(s);
					System.out.println("Name set for: " + client.getId() + " \"" + client.getName() + "\"");
					msg.Add("true");
					client.Send(msg1);
				} else {
					System.out.println("No name set for: " + client.getId() + " \"" + client.getName() + "\". Setting a default name");
					msg1.Add(client.getName());
					client.Send(msg1);
				}
				break;

			case Disconnect:
				client.Disconnect();
				removeClient(client.getId());
				break;

			default:
				System.out.println("Invalid message");
				break;
		}
	}

	private static ServerMain server;
	private static boolean update;
	private static void menu() {
		System.out.println("Tombola sever!\n"
						 + "-Available commands: \n"
						 + "start: starts the server\n"
						 + "stop: stops the server\n"
						 + "opentolan: makes the server visible on lan\n"
						 + "closetolan: makes the server invisible on lan\n"
						 + "help: shows this menu\n"
						 + "q - exit: stops the server and closes the program");
	}

	public static void main(String[] args) throws InterruptedException {
		server = new ServerMain();

		Thread updateThread = new Thread(() -> update());
		updateThread.setName("Update");

		Thread.currentThread().setName("MainThread");

		boolean quit = false;
		Scanner scanner = new Scanner(System.in);

		menu();
		while(!quit) {
			if(scanner.hasNext()) {
				String input = scanner.nextLine().toLowerCase();
				switch (input) {
					case "start":
						server.Start();
						updateThread.start();
						update = true;
						break;
	
					case "stop":
						update = false;
						server.Stop();
						break;

					case "opentolan":
						server.StartOpenToLan();
						break;

					case "closetolan":
						server.StopOpenToLan();
						break;
				
					case "help":
						menu();
						break;

					case "q":
					case "exit":
						update = false;
						synchronized(updateThread) {
							updateThread.notify();
						}
						server.Stop();
						quit = true;
						break;

					default:
						System.out.println("Invalid command '" + input + "'. Type 'help' for available commands");
						break;
				}
			}

			Thread.sleep(150);
		}
		System.out.println("bye");
	}

	private static void update() {
		while(update) {
			server.Update(true);
		}
	}
}
