import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Connection {

	private Socket socket;
	public static final int PORT = 28411;
	public static final String ENCODING = "UTF-8";
	public static final char EOL = '\n';
	private PrintStream outStream;
	private Scanner DataStream;
	private String nickname;
	private InputStream kek;
	DataInputStream in;	

	public Connection(Socket s, String nickname) throws IOException, SocketException {
		this.socket = s;
		this.socket.setSoTimeout(100000);
		outStream = new PrintStream(s.getOutputStream(),true, ENCODING);
		kek = s.getInputStream();
		DataStream = new Scanner(kek);
		this.nickname = nickname;

	}

	public boolean isOpen() {
		return !socket.isClosed();
	}

	public void sendNickHello(String nick) throws UnsupportedEncodingException, IOException {
		outStream.println("ChatApp 2015 user " + nick);
	}

	public void sendNickBusy(String nick) throws UnsupportedEncodingException, IOException {
		outStream.println("ChatApp 2015 user " + nick + " busy");
	}
	
	public void accept() throws IOException {
		outStream.println("Accepted");
	}

	public void reject() throws IOException {
		outStream.println("Rejected");
		outStream.close();
	}

	public void sendMessage(final String message) throws UnsupportedEncodingException, IOException {
		outStream.println("Message");
		outStream.println(message.trim());
	}

	public void disconnect() throws IOException {
		outStream.println("Disconnect");
		outStream.close();
		socket.close();
	}


	public Command receive() throws IOException {
		if (DataStream.hasNextLine()) {String data=DataStream.nextLine();
		System.out.println(data);
		if (data.toUpperCase().startsWith("CHATAPP 2015 USER")) {
			Scanner in = new Scanner(data);
			in.next();
			return new NickCommand(in.next(), in.skip(" [a-z,A-Z]{4} ").nextLine().replaceAll(" BUSY",""), data.toUpperCase().endsWith(" BUSY"));
		} else if (data.toUpperCase().equals("MESSAGE")) {
				data=DataStream.nextLine();
			return new MessageCommand(data);
		} else {
			data = data.toUpperCase().replaceAll("[\r\n]","");
			for (Command.CommandType cc : Command.CommandType.values())
				if (cc.toString().equals(data))
					return new Command(Command.CommandType.valueOf(data.replaceAll("ED", "")));
		}
		}
		
		return new Command(Command.CommandType.NULL);
		}
	}
