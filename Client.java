import java.io.*;
import java.net.*;

public class Client implements Constants {
	String server = "";
	int port;
	Socket sock = null;
	BufferedReader in = null;
	PrintWriter out = null;
	boolean quit;
	Refresher r;
	Sender s;
	
	public Client(String a, int p) {
		server = a;
		port = p;
		quit = false;
	}
	
	public void log(String s) {
		System.out.println(s);
	}
	
	public void start() {
		try {
			sock = new Socket(server, port);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);
		} catch (IOException e) {
			log("Exception creating socket / streams: " + e);
		}
		r = new Refresher();
		r.start();
		s = new Sender();
		s.start();

	}
	
	public void exit() {
		try {	
			r.interrupt();
			s.interrupt();
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			System.out.println("Error closing socket and streams");
		}
		System.out.println("Quitting Spider...");
		System.exit(0);
	}
	
	protected class Refresher extends Thread {
		public void run() {
			try {
				String line;
				while (true) {
					line = in.readLine();
					if (!line.equals(null)) {
						System.out.println(line);
					}
				}
			} catch (Exception e) {
				if (quit == true) {
					return;
				}
				log("Error in refresher: " + e);
				System.exit(0);
			}
		}
	}
	
	protected class Sender extends Thread {
		
		public void help() {
			log("Sphinx's Spider Client");
			log("To broadcast a message to the whole chatroom, just type your message and press enter.");
			log("To send a private message to a particular user, use this syntax:");
			log("mesg <user>:<message>");
			log("");
			log("Sphinx's Spider Client, made by Dave Paola July 1st, 2003.");
			log("Copyright 2003 Dave Paola No Rights Reserved.");
		}
		
		public void run() {
			try {
				String line;
				while (true) {
					BufferedReader sys = new BufferedReader(new InputStreamReader(System.in));
					line = sys.readLine();
					if (line.equals("quit")) {
						out.println(QUIT);
						exit();
					}
					if (line.startsWith("login")) {
						line = line.substring(6);
						out.println(LOGIN + line);
					} else
					if (line.startsWith("mesg")) {
						line = line.substring(6);
						int where = line.indexOf(SEP);
						out.println(MESG + line);
					} else
					if (line.equals("help")) {
						help();
					} else {
						broadcast(line);
					}
				}
			} catch (Exception e) {
				log("Error in reading input: " + e);
				System.exit(0);
			}
		}
		
		public void broadcast(String line) {
			out.println(BCAST + line);
			out.flush();
		}		
	}
}