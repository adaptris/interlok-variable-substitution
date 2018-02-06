package com.adaptris.core.varsub;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.util.URLHelper;
import com.adaptris.util.URLString;

/**
 * <p>
 * Will load a properties file from either the String or URL path.
 * </p>
 * @author amcgrath
 *
 */
class PropertyFileLoader {
  
  private transient Logger log = LoggerFactory.getLogger("VariableSubstitution");

  public Properties load(String url) throws IOException {
    return load(url, true);
  }

  public Properties load(String url, boolean notFoundAsError) throws IOException {
    return load(new URLString(url), notFoundAsError);
  }
  
  public Properties load(URL url) throws IOException {
    return load(url, true);
  }

  public Properties load(URL url, boolean notFoundAsError) throws IOException {
    return load(new URLString(url), notFoundAsError);
  }

  public Properties formatAndLoad(String url, boolean notFoundAsError, Object... formatArgs) throws IOException {
    String actualURL = String.format(url, formatArgs);
    log.trace("Generated [{}] from [{}]", actualURL, url);
    return load(new URLString(actualURL), notFoundAsError);
  }

  private Properties load(URLString loc, boolean notFoundAsError) throws IOException {
    Properties result = new Properties();
    log.trace("Loading [{}]", loc.toString());
    try (InputStream inputStream = URLHelper.connect(loc)) {
      if (inputStream == null) {
        throw new FileNotFoundException(loc.toString());
      }
      result.load(inputStream);
    }
    catch (FileNotFoundException e) {
      if (notFoundAsError) {
        throw e;
      }
      else {
        log.warn("Failed to read [{}], ignoring", loc.toString());
      }
    }
    return result;
  }

  public static Properties getEnvironment() {
    Properties result = new Properties();
    Map<String, String> env = System.getenv();
    for (String envName : env.keySet()) {
      result.put(envName, env.get(envName));
    }
    return result;
  }
}
