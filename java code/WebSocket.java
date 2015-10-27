import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * 
 * This class fetches data from the web server using the socket.
 * 
 * @author trusit
 *
 */
public class WebSocket 
{
	private Socket browser; // socket object to fetch html data from the web server.
	
	/**
	 * This function reads the html data from web server. If type is html it returns it. 
	 * For non text type it downloads the file and returns the header.
	 *  
	 * @param url Url of the website
	 * @return for html page - html data
	 *             image - header for the image
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public String getHtml(String url) throws UnknownHostException, IOException
	{
		Url URL = new Url(url);          //url object to parse url
		String host = URL.getHost();     
		String path = URL.getPath();
		int port = URL.getPort();
		
		if(host == null || path == null)            
		{
			System.out.println("Invalid URL");
			return null;
		}
		
		browser = new Socket(host,port);             //browser object
		
		// output and input streams of the browser.
		OutputStream out = browser.getOutputStream();    
		InputStream in = browser.getInputStream();
		
		// sending get request to fetch the data.
		PrintWriter pw = new PrintWriter(out, false);
       
        pw.print("GET " + path + " HTTP/1.1\r\n");
        pw.print("Host: "+ host + ":" + port +"\r\n");
        pw.print("\r\n");
        pw.flush();
		
        // Reads the http header.
        String str="";
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
        
        // if header has not found error return header.
        if(header.indexOf("HTTP/1.1 404 Not Found") != -1)
        	return header.toString();
 
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

    	// If content type is image save the content to the file.
    	// else return the content(html data)
        if(header.indexOf("Content-Type: image/") != -1)
        {
        	fileWrite(url.substring(url.lastIndexOf("/") + 1),byteData);        
        	
        	out.close();
            in.close();
             
        	return header.toString();
        }
        else
        {   
            return new String(byteData);
        }
	}
	
	/**
	 * closes the browser.
	 */
	@Override
	protected void finalize() throws Throwable 
	{
		browser.close();
		super.finalize();
	}
	
	/**
	 * Writes the data to the file.
	 * @param filename FileName of the file
	 * @param data     Data to be written to the file.
	 * @throws IOException
	 */
	public void fileWrite(String filename,byte[] data) throws IOException
	{
		try 
		{
			File f = fileCreate(filename);
			FileOutputStream out = new FileOutputStream(f);
			out.write(data);
			out.close();
		} 
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates new file
	 * @param filename File name
	 * @return File object of new created file.
	 */
	public File fileCreate(String filename) 
	{
		 File file = new File(filename);
		 
	     try 
	     {
			file.createNewFile();
	     } 
	     catch (IOException e) 
	     {
			// TODO Auto-generated catch block
			e.printStackTrace();
	     }
	     return file;
	}

	
	public static void main(String[] str) throws UnknownHostException, IOException
	{
		WebSocket web = new WebSocket();
		
		String html = web.getHtml(str[0]);    //getting html from the server.
		
		Url url = new Url(str[0]);
		
		String currentDir = url.getCurrentDir();   // getting current directory to find the absolute path for the image.
		
		if(html != null)
		{			
			if(html.indexOf("HTTP/1.1 404 Not Found") == -1)
			{
				HtmlParser parser = new HtmlParser(html);
				
				ArrayList<String> TagList = parser.getTagList();
				ArrayList<String> ImageList = parser.getImageList();

				for(String image:ImageList)
				{
					if(image.indexOf("http")!=-1)
						web.getHtml(image);
					else
						web.getHtml(currentDir + "/" + image);
				}
				
				for(String tag:TagList)
				{
					System.out.println(tag);
				}
			}
			else
			{
				System.out.println("Web Page not found");
			}
		}	
	}
}