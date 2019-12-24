
/**
 *date: 08.11.2019   -  time: 11:03:26
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package dataHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public abstract class DataHandlerToHTTP extends DataHandlerDecorator {

	public DataHandlerToHTTP(DataHandlerG7 handler) {
		super(handler);
	}

	protected void postFileToURL(String content, String url) {

		PostMethod post = new PostMethod(url);
		try {
			post.setRequestEntity(new StringRequestEntity(content, "text/xml", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		}
		post.setRequestHeader("Content-type", "text/xml; charset=UTF-8");

		// Get HTTP client
		HttpClient httpclient = new HttpClient();

		// Execute request
		try {
			try {
				httpclient.executeMethod(post);
				// Display status code
				// System.out.println("Response status code: " + result);
				
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

		} finally {
			post.releaseConnection();
		}
	}
}
