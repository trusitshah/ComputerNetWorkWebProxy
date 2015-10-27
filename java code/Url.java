/**
 * Url class to parse the Url Strings.
 * 
 * @author trusit
 *
 */

public class Url 
{
	private String url;
	private String host;
	private int port;
	private String path;
	
	/**
	 * Construct the url and parse it.
	 * @param str 
	 */
	public Url(String str)
	{
		this.url = str;
		parseUrl();
	}
	
	/**
	 * parsing of url.
	 */
	private void parseUrl()
	{
		 int indexHost = url.indexOf("//")+2;        // finding host.
		 
		 int indexPath = url.indexOf("/", indexHost); //finding path index.
		 
		 int indexPort = url.indexOf(":",indexHost); //finding port index.
		 
		 // if both host and path index are valid find the strings for host, path and port.
		 if(indexHost>-1 && indexPath>-1)
		 {
			 path = url.substring(indexPath);
			 
			 if(indexPort == -1)
			 {
				 host = url.substring(indexHost, indexPath);
				 port = 80;
			 }
			 else
			 {
				 host = url.substring(indexHost, indexPort);
				 port = Integer.parseInt(url.substring(indexPort + 1,indexPath));
			 }
		 }
		 else
		 {
			 host = null;
			 path = null;
			 port = 80;
		 }
	}
	
	/**
	 * Returns host from url string.
	 * @return
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 * Returns path from url string.
	 * @return
	 */
	public String getPath()
	{
		return path;
	}
	
	/**
	 * Returns port from url string.
	 * @return
	 */
	public int getPort()
	{
		return port;
	}	
	
	/**
	 * Returns current working directory from url string.
	 * @return
	 */
	public String getCurrentDir()
	{
		String ret;
		
		ret = url.substring(0, url.lastIndexOf("/"));
		
		return ret;
	}
}