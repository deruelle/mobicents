package org.mobicents.slee.service.httpclientra.test;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * This is a very simple test to understand the api of ROME. https://rome.dev.java.net/
 * @author amit.bhayani
 *
 */
public class FeedTest {
	public static void main(String[] args) {
		boolean ok = false;

		try {
			URL feedUrl = new URL("http://rss.cnn.com/rss/cnn_world.rss");

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));

			System.out.println(feed);
			System.out.println(feed.hashCode());

			List list = feed.getEntries();

			//System.out.println("Lists = "+list);

			Iterator itr = list.iterator();
			while(itr.hasNext()){
				SyndEntryImpl syndFeed = (SyndEntryImpl)itr.next();
				System.out.println("Links  = "+syndFeed.getLink());
			}


			ok = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ERROR: " + ex.getMessage());
		}

		if (!ok) {
			System.out.println();
			System.out
					.println("FeedReader reads and prints any RSS/Atom feed type.");
			System.out
					.println("The first parameter must be the URL of the feed to read.");
			System.out.println();
		}
	}
}
