public class Spider {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage:");
			System.out.println("java Spider { -c <server> <port> } | {-s <port> }");
			System.exit(0);
		}
		if (args[0].equals("-c")) {
			Client c = new Client(args[1], Integer.parseInt(args[2]));
			c.start();  //should never return
		}
		if (args[0].equals("-s")) {
			Server s = new Server(Integer.parseInt(args[1]));
			s.start();  //should never return
		}
	}
}