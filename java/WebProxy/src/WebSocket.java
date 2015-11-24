import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class WebSocket 
{
	private Socket socket;
	private final static String USER_AGENT = "Mozilla/5.0";
	
	public WebSocket(Socket socket)
	{
		this.socket = socket;
	}
	
	public WebSocket()
	{
		
	}
	
	public byte[] sendRequest(String body) throws IOException
	{
		String host = getHost(body);
		int port = getPort(body);
		Socket client = new Socket(host, port);
		
		if(getMethod(body).contains("CONNECT"))
		{
			return "200 OK\r\n\r\n".getBytes();
		}
		//System.out.println(host);
		body = body.replace("http://"+host,"");
		
		PrintWriter pw = new PrintWriter(client.getOutputStream(),false);
		//System.out.println(body);
	    pw.print(body);
		pw.print("\r\n");
		pw.flush();
		
		String str = new String();
				
		DataInputStream in = new DataInputStream(client.getInputStream());
		
        // Reads the http header.
        
        StringBuilder header = new StringBuilder("");
        
        byte[] byteData = null;
    
        int ch;
        do
        {
        	str = "";
	        while((ch = in.read())!=13)
	        {
	        	str+=(char)ch;
	        }
	        header.append(str+"\n");
	        in.read();
        }while(!str.equals(""));           //End the http header.
        
        //System.out.println(header.toString());
        // if header has not found error return header.
        if(header.indexOf("HTTP/1.1 404 Not Found") != -1)
        	return ("HTTP/1.1 200 OK" + "\r\n\r\n<html>Page not found</html>").getBytes();
 
        // gets the content length from the header.
       	int contentLengthStartIndex = header.indexOf("Content-Length:") + "Content-Length:".length() + 1;
    	int contentLengthEndIndex = header.indexOf("\n",contentLengthStartIndex);
    	int contentLength = Integer.parseInt(header.toString().substring(contentLengthStartIndex, contentLengthEndIndex));
   	
    	//read the data.
    	int readData;
    	byteData = new byte[contentLength];
    	int count = 0;
    	
    	while(count<contentLength)
    	{
    		readData = in.read();
    		byteData[count++] = (byte)readData;    		
    	}
		
    	byte[] returnData = new byte[header.length() + byteData.length + 5];
    	
    	for(int i = 0;i < header.length() ; i++)
    	{
    		returnData[i] = (byte) header.charAt(i);
    	}
    	
    	for(int i = 0;i < byteData.length ; i++)
    	{
    		returnData[i + header.length()] = byteData[i];
    	}    	
    
		return returnData;
	}
	
	private static String getHost(String request)
	{
		String[] lines = request.split("\r\n");
		
		for(String line : lines)
		{
			if(line.contains("Host"))
			{
				String hostName = line.split(" ")[1];
		
				if(hostName.contains(":"))
				{
					return hostName.substring(0, hostName.indexOf(":"));
				}
				else
				{
					return hostName;
				}
			}
		}
		return null;
	}
	
	private static int getPort(String request)
	{
		String[] lines = request.split("\r\n");
		
		for(String line : lines)
		{
			if(line.contains("Host"))
			{
				String hostName = line.split(" ")[1];
		
				if(hostName.contains(":"))
				{
					return Integer.parseInt(hostName.substring(hostName.indexOf(":")+1));
				}
				else
				{
					return 80;
				}
			}
		}
		return 80;
	}
	
	private static String getMethod(String request)
	{
		return request.split(" ")[0];
	}
}
