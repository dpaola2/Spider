import java.io.*;
import java.net.*;

public class FileSender {

	protected String filename;
	protected long length;
	protected File file;
	protected String target;
	protected int targetport = 8888;
	protected Socket sock;
	protected FileInputStream is;
	protected DataOutputStream os;
	
	public FileSender(String f) {
		filename = f;
		try {
			file = new File(filename);
			length = file.length();
		} catch (NullPointerException e) {
			System.out.println(e);
		}
	}
	
	public boolean canSend() {
		if (file.exists()) {
			return true;
		}
		return false;
	}
	
	public void setTarget(String t) {
		target = t;
	}
	
	public void send() {
		try {
			sock = new Socket(target, targetport); //create socket
		} catch (IOException e) {
			System.out.println("Creating socket: " + e);
		}
		try {
			is = new FileInputStream(file); //create fileinputstream to file
		} catch (Exception e) {
			System.out.println("FileInputStream: " + e);
		}
		try {
			os = new DataOutputStream(sock.getOutputStream()); //create outputstream to socket/client
		} catch (IOException e) {
			System.out.println("Creating DataOutputStream: " + e);
		}
		try {
			int c;
			int num = 0;
			while(num <= length) {
				c = is.read();
				System.out.print(c);
				os.writeInt(c);
				os.flush();
				num++;
			}
			System.out.println("");
		} catch (Exception e) {
			System.out.println("Writing file: " + e);
		}
		System.out.println("File Written successfully!");
		try {
			is.close();
			os.close();
			sock.close();
		} catch (IOException e) {
			System.out.println("Error closing streams/socket: " + e);
		}
	}
	
}