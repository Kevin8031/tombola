package server;

import java.util.Scanner;

import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.ByteArrayOutputStream;
import java.net.MulticastSocket;

import net.Common;
import net.Connection;
import net.Message;
import net.MessageType;
import net.OwnedMessage;
import net.Server;

public class ServerMain extends Server<MessageType> {
	private MulticastSocket multicastSocket;
	private Thread lanThread;
	private boolean openToLan;

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

	public void StartOpenToLan() {
		if(lanThread == null) {
			lanThread = new Thread(() -> OpenToLan());
			lanThread.setName("lanThread");
			lanThread.start();
			openToLan = true;
			if(!isRunning()) {
				System.out.println("[LAN SEARCH] Server was not started. Starting...");
				Start();
			}
		} else {
			System.out.println("[LAN SEARCH] Already started.");
		}
	}

	private void OpenToLan() {
		try {
			multicastSocket = new MulticastSocket();
			InetAddress inet = InetAddress.getByName(Common.MULTICAST_INET);
			multicastSocket.joinGroup(inet);
			System.out.println("[SERVER] Server opened to lan.");

			Message<MessageType> msg = new Message<MessageType>();
			msg.setHeadId(MessageType.LAN_SERVER_DISCOVEY);
			msg.Add(serverName, serverSocket.getLocalPort(), clientNumber());

			while (openToLan) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(msg);
				byte[] data = baos.toByteArray();
				DatagramPacket send = new DatagramPacket(data, data.length, inet, Common.MULTICAST_PORT);
				multicastSocket.send(send);
				
				System.out.println("[LAN SEARCH] Sent: " + msg);
				Thread.sleep(5000);
			}
			System.out.println("[SERVER] Not visible on lan.");
		} catch (Exception e) {
			System.err.println(e);
			System.err.println("[SERVER] Cannot open server to lan.");
			StopOpenToLan();
		}
	}

	public void StopOpenToLan() {
		if(multicastSocket != null) {
			openToLan = false;
			try {
				multicastSocket.close();
				multicastSocket = null;
				System.out.println("[LAN SEARCH] Waiting for thread to finish execution...");
				lanThread.join();
				lanThread = null;
			} catch (Exception e) {}
		} else
			System.out.println("[SERVER] Lan visibility already stopped");
	}

	public static void main(String[] args) throws InterruptedException {
		server = new ServerMain();

		Thread updateThread = null;
		Thread.currentThread().setName("MainThread");

		boolean quit = false;
		Scanner scanner = new Scanner(System.in);

		menu();

		while(!quit) {
			if(scanner.hasNext()) {
				String input = scanner.nextLine().toLowerCase();
				switch (input) {
					case "start":
						if(!server.isRunning()) {
							updateThread = new Thread(() -> update());
							updateThread.setName("Update");
							server.Start();
							update = true;
							System.out.println(updateThread.getState());
							updateThread.start();
						}
						break;

					case "stop":
						if(server.isRunning()) {
							update = false;
							synchronized(updateThread) {
								updateThread.notify();
							}
							server.Stop();
						}
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
		scanner.close();
		System.out.println("bye");
	}

	private static void update() {
		while(update) {
			server.Update(true);
		}
	}
}
