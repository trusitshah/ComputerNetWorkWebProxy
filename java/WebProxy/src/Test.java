import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Test 
{
	public static void main(String[] str) throws UnknownHostException, IOException
	{
		ServerSocket proxyServer = new ServerSocket(8091);
		String hostName = "";

		while(true)
		{
			Socket client = proxyServer.accept();
			
			InputStream clientInputStream= client.getInputStream();
			
			BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientInputStream));
			String clientRequest = new String();
			StringBuilder clientRequestBuilder = new StringBuilder();
			
			do
			{
				clientRequest = clientReader.readLine();
				clientRequestBuilder.append(clientRequest+"\r\n");
			}while(!clientRequest.equals(""));

			System.out.println(clientRequestBuilder.toString().split("\r\n")[0]);
			
			String method = clientRequestBuilder.toString().split(" ")[0];
			
			int length = 0;
			if(method.equals("POST"))	
			{
				int index = clientRequestBuilder.toString().indexOf("Content-Length:");
				int endindex = clientRequestBuilder.toString().indexOf("\r", index);
				length = Integer.parseInt(clientRequestBuilder.toString().substring(index + "Content-Length: ".length(), endindex));
				
			//	System.out.println(length);
				
				int count = 0;
				do
				{
					int ch = clientReader.read();
					clientRequestBuilder.append(new Character((char)ch).toString());
					count++;
				}while(count < length);
			}
			
			String fileName = clientRequestBuilder.toString().split(" ")[1].replace("http://","");
			fileName = fileName.replace("/", "_");
			fileName = fileName.replace("?", "_");
			
			File file = new File(fileName);
			byte[] response;
			
			if(file.exists())
			{
				System.out.println("Get it from the cache");
				DataInputStream in = new DataInputStream(new FileInputStream(file));
			
				int i = 0;
				response = new byte[(int) file.length()];
				while(i<file.length())
				{
					response[i++] = in.readByte();
				}
				in.close();
			}
			else
			{
				WebSocket ws = new WebSocket();
			
				response = ws.sendRequest(clientRequestBuilder.toString());
				
				DataOutputStream cache = new DataOutputStream(new FileOutputStream(new File(fileName)));
				cache.write(response);
				cache.flush();
				cache.close();
			}

			DataOutputStream clientResponse = new DataOutputStream(client.getOutputStream());
			clientResponse.write(response);
			clientResponse.flush();
			clientResponse.close();
			
			client.close();
		}
	}
}
