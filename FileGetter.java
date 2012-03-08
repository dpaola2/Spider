import java.io.*;
import java.net.*;

public class FileGetter {
	
	protected String host;
	protected int port = 8888;
	protected DataInputStream is;
	protected FileOutputStream os;
	protected File file;
	protected long length;
	protected ServerSocket serv;
	protected Socket sock;
	
	public FileGetter(String name) {
		try {
			file = new File(name);
			os = new FileOutputStream(file);
			sock = serv.accept();
			is = new DataInputStream(sock.getInputStream());
		} catch (Exception e) {
			System.out.println("Error in constructor: " + e);
		}
		try {
			boolean go = true;
			while(go) {
				int c = is.readInt();
				if (c != -1) {
					os.write(c);
					os.flush();
				} else go = false;
			}
		} catch (Exception e) {
			System.out.println("Error getting file: " + e);
		}			
	}
}
	
	