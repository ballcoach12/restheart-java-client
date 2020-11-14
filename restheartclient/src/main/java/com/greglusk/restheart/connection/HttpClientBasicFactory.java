package com.greglusk.restheart.connection;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Created by Alon Eirew on 7/16/2017.
 */
public class HttpClientBasicFactory implements IHttpClientFactory, Closeable {

	private static final Logger LOGGER = Logger.getLogger(HttpClientBasicFactory.class.getName());

	protected CloseableHttpClient httpClient;

	
	
	/**
	 * CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope("httpbin.org", 80),
                new UsernamePasswordCredentials("user", "passwd"));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
	 */

	public HttpClientBasicFactory() {
		this.httpClient = HttpClients.createDefault();
	}

	public HttpClientBasicFactory(String userName, String password) {
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);
		provider.setCredentials(AuthScope.ANY, credentials);
		
		this.httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build();
	}

	@Override
	public CloseableHttpClient getHttpClient() {
		return this.httpClient;
	}

	@Override
	public void close() throws IOException {
		try {
			LOGGER.log(Level.INFO, "Closing HttpClient instance");
			this.httpClient.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Exception when trying to free http client resource", e);
		}
	}
}
