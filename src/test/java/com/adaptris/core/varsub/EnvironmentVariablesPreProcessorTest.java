package com.adaptris.core.varsub;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adaptris.core.Adapter;
import com.adaptris.core.DefaultMarshaller;
import com.adaptris.core.stubs.JunitBootstrapProperties;
import com.adaptris.interlok.junit.scaffolding.BaseCase;
import com.adaptris.util.KeyValuePairSet;

public class EnvironmentVariablesPreProcessorTest extends BaseCase {
  private static final String KEY_ENV_ADAPTER_XML = "varsub.environment.adapter.xml";

  private EnvironmentVariablesPreProcessor preProcessor;

  private Properties bootstrapProperties;
  private File adapterXmlFile;

  @BeforeEach
  public void setUp() throws Exception {
    adapterXmlFile = new File(PROPERTIES.getProperty(KEY_ENV_ADAPTER_XML));
    bootstrapProperties = new Properties();
    bootstrapProperties.put(Constants.SYSPROP_PREFIX_KEY, Constants.DEFAULT_VARIABLE_PREFIX);
    bootstrapProperties.put(Constants.SYSPROP_POSTFIX_KEY, Constants.DEFAULT_VARIABLE_POSTFIX);
    bootstrapProperties.put(Constants.SYSPROP_IMPL_KEY, VariableSubstitutionType.SIMPLE.name());

    preProcessor = new EnvironmentVariablesPreProcessor(new JunitBootstrapProperties(bootstrapProperties));
  }

  @Test
  public void testEnvironmentVariableReplacement_URL() throws Exception {
    EnvironmentVariablesPreProcessor myProcessor = new EnvironmentVariablesPreProcessor(new KeyValuePairSet(bootstrapProperties));

    String xml = myProcessor.process(adapterXmlFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);

    doStandardAssertions(adapter);
  }

  @Test
  public void testEnvironmentVariableReplacement_String() throws Exception {
    String xml = preProcessor.process(IOUtils.toString(adapterXmlFile.toURI().toURL(), Charset.defaultCharset()));
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);

    doStandardAssertions(adapter);
  }

  @Test
  public void testEnvironmentVariableReplacement_NoPrefix() throws Exception {
    // Remove the property - should use the default, which happens to be the same anyway....
    bootstrapProperties.remove(Constants.SYSPROP_PREFIX_KEY);
    preProcessor.setProperties(new JunitBootstrapProperties(bootstrapProperties));

    String xml = preProcessor.process(adapterXmlFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);

    doStandardAssertions(adapter);
  }

  @Test
  public void testEnvironmentVariableReplacement_NoPostfix() throws Exception {
    // Remove the property - should use the default, which happens to be the same anyway....
    bootstrapProperties.remove(Constants.SYSPROP_POSTFIX_KEY);
    preProcessor.setProperties(new JunitBootstrapProperties(bootstrapProperties));

    String xml = preProcessor.process(adapterXmlFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);

    doStandardAssertions(adapter);
  }

  private void doStandardAssertions(Adapter adapter) {
    assertEquals("adapterUniqueId", adapter.getUniqueId());
    assertEquals("channel1", adapter.getChannelList().get(0).getUniqueId());
    assertEquals("channel2", adapter.getChannelList().get(1).getUniqueId());
    String workflowId = System.getenv("PATH");
    assertEquals(workflowId, adapter.getChannelList().get(0).getWorkflowList().get(0).getUniqueId());
    assertEquals("workflow2", adapter.getChannelList().get(0).getWorkflowList().get(1).getUniqueId());
    assertEquals(workflowId, adapter.getChannelList().get(1).getWorkflowList().get(0).getUniqueId());
    assertEquals("workflow2", adapter.getChannelList().get(1).getWorkflowList().get(1).getUniqueId());
  }

}
