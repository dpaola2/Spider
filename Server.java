import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server implements Constants {

	protected final static String SERVERID = "SPHINX SAYS";
	protected ServerSocket serv;
	protected ArrayList clients;
	protected int port;
	private boolean DEBUG = false;
	
	
	public Server(int p) {
		port = p;
		clients = new ArrayList();
		try {
			serv = new ServerSocket(port);
			log("Server up and running on port " + port + ".");
		} catch (IOException e) {
			log("IO Exception creating ServerSocket in Server.<init>");
			System.exit(0);
		}
	}
	
	public void start() {
		try {
			while (true) {
				Socket us = serv.accept();
				String hostName = us.getInetAddress().getHostName();
				log("Accepted from " + hostName);
				Handler cl = new Handler(us, hostName);
				synchronized (clients) {
					clients.add(cl);
					cl.start();
					if (clients.size() == 1) 
						cl.send(SERVERID, "You are all alone");
					else {
						cl.send(SERVERID, "Welcome to the Spider Server, you are the latest of " + clients.size() + " users.");
					}
				}
			}
		} catch (IOException e) {
			log("IO Exception in start() of serv: " + e);
			System.exit(0);
		}
	}
	
	protected void log(String s) {
		System.out.println(s);
	}
	
	protected class Handler extends Thread {
		protected Socket sock;
		protected BufferedReader is;
		protected PrintWriter pw;
		protected String clientIP;
		protected String login;
		
		public Handler(Socket s, String clnt) throws IOException {
			sock = s;
			clientIP = clnt;
			is = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			pw = new PrintWriter(sock.getOutputStream());
		}
		
		public void run() {
			String line;
			try {
				while ((line = is.readLine()) != null) {
					char c = line.charAt(0);
					line = line.substring(1);
					switch (c) {
						case LOGIN:
							if (!isValidLoginName(line)) {
								send(SERVERID, "LOGIN " + line + " is invalid, and this IS logged...");
								log("INVALID LOGIN: " + line);
								continue;
							}
							
							login = line;
							broadcast(SERVERID, login + " is now here, for a total of " + clients.size() + " users connected.");
							break;
						case MESG:
							if (login == null) {
								send(SERVERID, "login first, you idiot");
								continue;
							}
							int where = line.indexOf(SEP);
							String recip = line.substring(0, where);
							String mesg = line.substring(where + 1);
							log("MESG: " + login + "-->" + recip + ": " + mesg);
							Handler cl = lookup(recip);
							if (cl == null) 
								psend(SERVERID, recip + " not logged in");
							else
								cl.psend(login, mesg);
							break;
						case QUIT:
							broadcast(SERVERID, login + " has left.");
							close();
							return;
						case GETFILE:
							psend(SERVERID, "not working yet");
							return;						
						case BCAST:
							if (login != null)
								broadcast(login, line);
							else
								log("B<L FROM " + clientIP);
							break;
						default:
							log("Unknown cmd " + c + " from " + login + "@" + clientIP);
						}
					}
				} catch (IOException e) {
					log("IOException in switch of commands: " + e);
				} finally {
					//sock ended, done
					log(login + SEP + " left");
					synchronized(clients) {
						clients.remove(this);
						if (clients.size() == 0) {
							log(SERVERID + SEP + "nobody here");
						} else if (clients.size() == 1) {
							Handler last = (Handler)clients.get(0);
							last.send(SERVERID, "You are all alone");
						} else {
							broadcast(SERVERID, "There are now " + clients.size() + " users logged in.");
						}	
					}
				}
			}
			
			protected void close() {
				if (sock == null) {
					log("close when not open");
					return;
				}
				try {
					sock.close();
					sock = null;
				} catch (IOException e) {
					log("Failure during close of " + clientIP);
				}
			}
			
			public void send(String sender, String mesg) {
				pw.println(sender + SEP + mesg);
				pw.flush();
			}
			
			public void psend(String sender, String mesg) {
				send("<*" + sender + "*>", mesg);
			}
			
			public void broadcast(String sender, String mesg) {
				log("Broadcasting " + sender + SEP + mesg);
				for (int i = 0; i < clients.size(); i++) {
					Handler sib = (Handler)clients.get(i);
					if (DEBUG) 
						log("Sending to " + sib);
					sib.send(sender, mesg);
				}
				if (DEBUG) log("Finished Broadcasting.");
			}
			
			protected Handler lookup(String nick) {
				synchronized(clients) {
					for (int i = 0; i < clients.size(); i++) {
						Handler cl = (Handler)clients.get(i);
						if (cl.login.equals(nick))	
							return cl;
					}
					return null;
				}
			}
			
			public boolean isValidLoginName(String name) {
				return true;
			}
			
			public String toString() {
				return "Handler[" + login + "]";
			}
		}
	}
