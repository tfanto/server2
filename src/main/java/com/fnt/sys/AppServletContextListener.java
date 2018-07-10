package com.fnt.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.crypto.SecretKey;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppServletContextListener implements ServletContextListener {

	private static SecretKey encryptionKey;	
	public static SecretKey getEncryptionKey() {
		return encryptionKey;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {


		try {
			
			Properties props = new Properties();
			InputStream is = servletContextEvent.getServletContext().getResourceAsStream("WEB-INF/settings.properties");		
			props.load(is);

			String keystoreLocation = props.getProperty("keystorelocation");
			String keystorefilepwd = props.getProperty("keystorefilepwd");
			String encryptionkeypwd = props.getProperty("encryptionkeypwd");
			String encryptionalias = props.getProperty("encryptionalias");

			// HMac-SHA256 key size = 256

			KeyStore keyStore = KeyStore.getInstance("JCEKS");
			File keyStoreFile = new File(keystoreLocation);
			FileInputStream keyStoreStream = new FileInputStream(keyStoreFile);
			keyStore.load(keyStoreStream, keystorefilepwd.toCharArray());
			keyStoreStream.close();
			encryptionKey = (SecretKey) keyStore.getKey(encryptionalias, encryptionkeypwd.toCharArray());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		
		if(encryptionKey != null) {
			encryptionKey = null;
		}

	}

}
