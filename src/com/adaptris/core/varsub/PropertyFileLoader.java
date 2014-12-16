package com.adaptris.core.varsub;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import com.adaptris.util.URLString;

/**
 * <p>
 * Will load a properties file from either the String or URL path.
 * </p>
 * @author amcgrath
 *
 */
public class PropertyFileLoader {
  
  public Properties load(String url) throws IOException {
    return load(new URLString(url));
  }
  
  public Properties load(URL url) throws IOException {
    return load(new URLString(url));
  }

  private Properties load(URLString loc) throws IOException {
    Properties result = new Properties();
    try (InputStream inputStream = connectToUrl(loc)) {
      result.load(inputStream);
    }
    return result;
  }

  // Copied out of AbstractMarshaller...
  private InputStream connectToUrl(URLString loc) throws IOException {
    if (loc.getProtocol() == null || "file".equals(loc.getProtocol())) {
      return connectToFile(loc.getFile());
    }
    URL url = new URL(loc.toString());
    URLConnection conn = url.openConnection();
    // ProxyUtil.applyBasicProxyAuthorisation(conn);
    return conn.getInputStream();
  }

  private InputStream connectToFile(String localFile) throws IOException {
    InputStream in = null;
    File f = new File(localFile);
    if (f.exists()) {
      in = new FileInputStream(f);
    }
    else {
      ClassLoader c = this.getClass().getClassLoader();
      URL u = c.getResource(localFile);
      if (u != null) {
        in = u.openStream();
      }
    }
    return in;
  }

}
