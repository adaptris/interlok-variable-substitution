package com.adaptris.core.varsub;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.adaptris.core.CoreException;
import com.adaptris.core.config.ConfigPreProcessorImpl;
import com.adaptris.core.management.BootstrapProperties;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.util.KeyValuePairSet;

public abstract class VariablePreProcessorImpl extends ConfigPreProcessorImpl {

  public VariablePreProcessorImpl(BootstrapProperties bootstrapProperties) {
    super(bootstrapProperties);
  }

  public VariablePreProcessorImpl(KeyValuePairSet kvps) {
    super(kvps);
  }


  @Override
  public String process(String xml) throws CoreException {
    String result = xml;
    try {
      result = expand(xml);
    }
    catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return result;
  }

  @Override
  public String process(URL urlToXml) throws CoreException {
    String result = null;
    try (InputStream inputStream = urlToXml.openConnection().getInputStream()) {
      result = process(IOUtils.toString(inputStream, Charset.defaultCharset()));
    }
    catch (IOException ex) {
      throw ExceptionHelper.wrapCoreException(ex);
    }
    return result;
  }


  protected abstract String expand(String xml) throws Exception;
}
