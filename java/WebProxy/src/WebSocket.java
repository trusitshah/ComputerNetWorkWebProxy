import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class WebSocket 
{
	private Socket socket;
	
	public WebSocket(Socket socket)
	{
		this.socket = socket;
	}
	
	public void sendRequest(String path)
	{ 
		try 
		{
			OutputStream out = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(out);
			
			//pw.print(payload);
			pw.print("GET " + path /*+ " HTTP/1.1\r\n"*/);
	       // pw.print("Host: "+ "www.google.com" + ":" + socket.getPort() +"\r\n");
	        pw.print("\r\n");
	        pw.flush();
	        //out.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getResponse()
	{
		StringBuilder response = new StringBuilder();
		
		InputStream in;
		try
		{
			in = socket.getInputStream();
		
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String str;
			while(!(str = br.readLine()).equals(""))
			{
				response.append(str+"\r\n");
			}
			response.append("\r\n\r\n");
			while((str = br.readLine())!=null)
			{
				response.append(str+"\r\n");
			}
			
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.toString();
	}
	
	public static void main(String[] str) throws UnknownHostException, IOException
	{
		ServerSocket proxyServer = new ServerSocket(1111);
		while(true)
		{
		
		Socket client = proxyServer.accept();
		InputStream clientInputStream= client.getInputStream();
		
		BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientInputStream));
		String clientRequest = new String();
		StringBuilder clientRequestBuilder = new StringBuilder();
		
		while(!(clientRequest = clientReader.readLine()).equals(""))
		{
			clientRequestBuilder.append(clientRequest+"\r\n");
		}
		System.out.println(clientRequestBuilder.toString());
		
		System.out.println("Host:"+getHost(clientRequestBuilder.toString().split("\r\n")[0]));
		
		WebSocket ws = new WebSocket(new Socket("www.youtube.com", 80));
		ws.sendRequest("");
		String proxyResponse = ws.getResponse();
		System.out.println("Proxy Response: " + proxyResponse);
		
		PrintWriter clientResponse = new PrintWriter(client.getOutputStream(),false);
		//clientResponse.println("HTTP/1.1 200 OK");
		clientResponse.print(proxyResponse);
		clientResponse.print("\r\n\r\n");
		clientResponse.flush();
		clientResponse.close();
		client.close();
		
		}
	}
	
	private static String getHost(String request)
	{
		return request.substring(request.indexOf("/") + 1, request.indexOf("HTTP") - 1);	
	}
}
