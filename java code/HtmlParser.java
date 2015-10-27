import java.util.ArrayList;

/**
 * This class parses the html data.
 * @author trusit
 *
 */
public class HtmlParser 
{
	private ArrayList<String> ImageList; // list containing image paths. 
	private ArrayList<String> TagList;   // list for the tag data to be printed.
	
	/**
	 * It will initialize the lists and parse the html.
	 * @param html
	 */
	public HtmlParser(String html)
	{
		ImageList = new ArrayList<String>();
		TagList = new ArrayList<String>();
		parse(html);
	}
	
	private void parse(String str)
	{
		int pos = 0;
		int indexOpen = pos;         // index for "<"
		int indexClose = pos;        // index for ">"
		boolean dontRead = false;
		
		while(indexOpen!=-1 && indexClose!=-1) // loop untill both the index are valid.
		{
			indexOpen = str.indexOf("<",pos);          
			pos = indexOpen;
			
			if(indexClose<indexOpen)                
			{
				//getting data between >< brackets. means printing data between the tags.
				
				if(!str.substring(indexClose+1, indexOpen).trim().equals(""))
				{
					// avoiding data which are not be printed. like data inside style tag.
					if(dontRead == false)
					{
						TagList.add(str.substring(indexClose+1, indexOpen));
					}
				}
			}
			indexClose = str.indexOf(">", pos);
			
			if(indexClose>indexOpen)
			{
				// getting data between <> tags. means the data of tag.
				// If it is img tag add it into the image list.
				if(str.substring(indexOpen+1, indexClose).indexOf("img")!= -1)
				{
					String imgsrc = getImageSrc(str.substring(indexOpen+1, indexClose));
					TagList.add("Image:"+ imgsrc.substring(imgsrc.lastIndexOf("/") + 1));  //finding the file name of the image.
					ImageList.add(imgsrc);
					//System.out.println(imgSrc);
				}
				// setting the flag to indicate not to take data from the style tag.
				if(str.substring(indexOpen+1, indexClose).indexOf("/style")!= -1)
				{
					dontRead = false;
				}				
				else if(str.substring(indexOpen+1, indexClose).indexOf("style")!= -1)
				{
					dontRead = true;
				}				
			}
			pos = indexClose;
		}
	}
	
	/**
	 * This will parse the image source from the image tag.
	 * @param str image tag string.
	 * @return image source path.s
	 */
	private String getImageSrc(String str)
	{
		int indexStart = str.indexOf("src=\"");
		int indexEnd = str.indexOf("\"",indexStart + ("src=\"").length());
		
		String ret = str.substring(indexStart + ("src=\"").length(), indexEnd); 
		
		if(ret.indexOf("http") != -1)
			return ret;
		
		if(ret.lastIndexOf("/") == -1)
			return ret;
		
		return ret;
	}
	
	public ArrayList<String> getImageList()
	{
		return ImageList;
	}
	
	public ArrayList<String> getTagList()
	{
		return TagList;
	}
}