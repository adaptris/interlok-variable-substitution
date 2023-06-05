package com.adaptris.core.varsub;

import static com.adaptris.core.varsub.PropertyFileLoaderTest.SAMPLE_MISSING_SUBSTITUTION_PROPERTIES;
import static com.adaptris.core.varsub.PropertyFileLoaderTest.SAMPLE_SUBSTITUTION_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adaptris.core.Adapter;
import com.adaptris.core.CoreException;
import com.adaptris.core.DefaultMarshaller;
import com.adaptris.core.stubs.JunitBootstrapProperties;
import com.adaptris.interlok.junit.scaffolding.BaseCase;
import com.adaptris.util.KeyValuePairSet;

public class VariableSubstitutionPreProcessorTest extends BaseCase {

  private static final String PROPS_VARIABLES_ADAPTER = "varsub.variables.adapter.xml";

  private File variablesAdapterFile;

  @Mock
  private PropertyFileLoader propertyFileLoader;

  private VariableSubstitutionPreProcessor preProcessor;

  private Properties sampleBootstrapProperties;

  private AutoCloseable mocks;

  @BeforeEach
  public void setUp() throws Exception {
    mocks = MockitoAnnotations.openMocks(this);

    variablesAdapterFile = new File(PROPERTIES.getProperty(PROPS_VARIABLES_ADAPTER));

    sampleBootstrapProperties = new Properties();
    sampleBootstrapProperties.put("variable-substitution.varprefix", "${");
    sampleBootstrapProperties.put("variable-substitution.varpostfix", "}");
    sampleBootstrapProperties.put("variable-substitution.impl", VariableSubstitutionType.SIMPLE.name());
    sampleBootstrapProperties.put("variable-substitution.properties.url", "dummy-url-using-mocks-instead");

    preProcessor = new VariableSubstitutionPreProcessor(new JunitBootstrapProperties(sampleBootstrapProperties));
    preProcessor.setPropertyFileLoader(propertyFileLoader);
  }

  @AfterEach
  public void tearDown() throws Exception {
    mocks.close();
  }

  @Test
  public void testSimpleVarSubAdapterRegistry() throws Exception {
    // We don't actually want to go to the file system for the variable substitutions
    Properties variableSubstitutions = createProperties();
    when(propertyFileLoader.load(anyString(), anyBoolean())).thenReturn(variableSubstitutions);

    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);

    doStandardAssertions(adapter);
  }

  @Test
  public void testSimpleVarSubAdapterRegistry_String() throws Exception {
    // We don't actually want to go to the file system for the variable substitutions
    Properties variableSubstitutions = createProperties();
    when(propertyFileLoader.load(anyString(), anyBoolean())).thenReturn(variableSubstitutions);

    String xml = preProcessor.process(IOUtils.toString(variablesAdapterFile.toURI().toURL(), Charset.defaultCharset()));
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);

    doStandardAssertions(adapter);
  }

  @Test
  public void testSimpleVarSubAdapterRegistryWithProperPropertiesFile() throws Exception {
    Properties myBootstrapProperties = new Properties();
    myBootstrapProperties.put(Constants.VARSUB_PROPERTIES_USE_HOSTNAME, "true");

    myBootstrapProperties.put("variable-substitution.properties.url.1", PROPERTIES.getProperty(SAMPLE_SUBSTITUTION_PROPERTIES));
    myBootstrapProperties.put("variable-substitution.properties.url.2", PROPERTIES.getProperty(SAMPLE_MISSING_SUBSTITUTION_PROPERTIES));
    VariableSubstitutionPreProcessor myPreProcessor = new VariableSubstitutionPreProcessor(new KeyValuePairSet(myBootstrapProperties));

    String xml = myPreProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);

    doStandardAssertions(adapter);
  }

  @Test
  public void testSimpleVarSubAdapterRegistry_NestedVariables() throws Exception {
    Properties myVarSubs = new Properties();
    myVarSubs.put("my.adapter", "MyAdapter");
    myVarSubs.put("Channel", "Channel");
    myVarSubs.put("adapter.id", "${my.adapter}ID");
    myVarSubs.put("channel.id", "My${Channel}ID");
    myVarSubs.put("channel.alternate.id", "Another${Channel}Id");
    myVarSubs.put("workflow.id1", "${my.workflow}ID1");
    myVarSubs.put("workflow.id2", "${my.workflow}ID2");
    myVarSubs.put("my.workflow", "MyWorkflow");

    when(propertyFileLoader.load(anyString(), anyBoolean())).thenReturn(myVarSubs);

    sampleBootstrapProperties.put("variable-substitution.properties.url", PROPERTIES.getProperty(SAMPLE_SUBSTITUTION_PROPERTIES));

    preProcessor.setProperties(new JunitBootstrapProperties(sampleBootstrapProperties));

    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);

    doStandardAssertions(adapter);
  }

  @Test
  public void testSimpleVarSubAdapterRegistry_UnresolvableNestedVariables() throws Exception {
    Properties myVarSubs = new Properties();
    myVarSubs.put("Channel", "Channel");
    myVarSubs.put("adapter.id", "${my.adapter}ID");
    myVarSubs.put("channel.id", "My${Channel}ID");
    myVarSubs.put("channel.alternate.id", "Another${Channel}Id");
    myVarSubs.put("workflow.id1", "${my.workflow}ID1");
    myVarSubs.put("workflow.id2", "${my.workflow}ID2");
    myVarSubs.put("my.workflow", "MyWorkflow");

    when(propertyFileLoader.load(anyString(), anyBoolean())).thenReturn(myVarSubs);

    sampleBootstrapProperties.put("variable-substitution.properties.url", PROPERTIES.getProperty(SAMPLE_SUBSTITUTION_PROPERTIES));

    preProcessor.setProperties(new JunitBootstrapProperties(sampleBootstrapProperties));

    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
    assertEquals("${my.adapter}ID", adapter.getUniqueId());
  }

  @Test
  public void testSimpleVarSubAdapterRegistry_SystemPropertiesVariables() throws Exception {
    Properties myVarSubs = new Properties();
    myVarSubs.put("Channel", "Channel");
    myVarSubs.put("adapter.id", "${java.home}ID");
    myVarSubs.put("channel.id", "My${Channel}ID");
    myVarSubs.put("channel.alternate.id", "Another${Channel}Id");
    myVarSubs.put("workflow.id1", "${my.workflow}ID1");
    myVarSubs.put("workflow.id2", "${my.workflow}ID2");
    myVarSubs.put("my.workflow", "MyWorkflow");

    when(propertyFileLoader.load(anyString(), anyBoolean())).thenReturn(myVarSubs);

    sampleBootstrapProperties.put("variable-substitution.properties.url", PROPERTIES.getProperty(SAMPLE_SUBSTITUTION_PROPERTIES));

    preProcessor.setProperties(new JunitBootstrapProperties(sampleBootstrapProperties));

    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
    String javaHome = System.getProperty("java.home");
    assertEquals(javaHome + "ID", adapter.getUniqueId());
  }

  @Test
  public void testSimpleVarSubAdapterRegistry_EnvironmentVariables() throws Exception {
    Properties myVarSubs = new Properties();
    myVarSubs.put("Channel", "Channel");
    myVarSubs.put("adapter.id", "${PATH}");
    myVarSubs.put("channel.id", "My${Channel}ID");
    myVarSubs.put("channel.alternate.id", "Another${Channel}Id");
    myVarSubs.put("workflow.id1", "${my.workflow}ID1");
    myVarSubs.put("workflow.id2", "${my.workflow}ID2");
    myVarSubs.put("my.workflow", "MyWorkflow");

    when(propertyFileLoader.load(anyString(), anyBoolean())).thenReturn(myVarSubs);

    sampleBootstrapProperties.put("variable-substitution.properties.url", PROPERTIES.getProperty(SAMPLE_SUBSTITUTION_PROPERTIES));

    preProcessor.setProperties(new JunitBootstrapProperties(sampleBootstrapProperties));

    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
    String path = System.getenv("PATH");
    assertEquals(path, adapter.getUniqueId());
  }

  @Test
  public void testSimpleVarSubAdapterRegistryNoPropertyURL() throws Exception {
    // We don't actually want to go to the file system for the variable substitutions
    Properties variableSubstitutions = createProperties();
    when(propertyFileLoader.load(anyString(), anyBoolean())).thenReturn(variableSubstitutions);

    // Remove the property - no substitution should take place.
    sampleBootstrapProperties.remove("variable-substitution.properties.url");
    preProcessor.setProperties(new JunitBootstrapProperties(sampleBootstrapProperties));

    String adapterXml = preProcessor.process(variablesAdapterFile.toURI().toURL());

    assertEquals(FileUtils.readFileToString(variablesAdapterFile, Charset.defaultCharset()), adapterXml);
  }

  @Test
  public void testSimpleVarSubAdapterRegistryNoPrefix() throws Exception {
    // We don't actually want to go to the file system for the variable substitutions
    Properties variableSubstitutions = createProperties();
    when(propertyFileLoader.load(anyString(), anyBoolean())).thenReturn(variableSubstitutions);

    // Remove the property - should use the default, which happens to be the same anyway....
    sampleBootstrapProperties.remove("variable-substitution.varprefix");
    preProcessor.setProperties(new JunitBootstrapProperties(sampleBootstrapProperties));

    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);

    doStandardAssertions(adapter);
  }

  @Test
  public void testSimpleVarSubAdapterRegistryNoPostfix() throws Exception {
    // We don't actually want to go to the file system for the variable substitutions
    Properties variableSubstitutions = createProperties();
    when(propertyFileLoader.load(anyString(), anyBoolean())).thenReturn(variableSubstitutions);

    // Remove the property - should use the default, which happens to be the same anyway....
    sampleBootstrapProperties.remove("variable-substitution.varpostfix");
    preProcessor.setProperties(new JunitBootstrapProperties(sampleBootstrapProperties));

    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);

    doStandardAssertions(adapter);
  }

  @Test
  public void testSimpleVarSubAdapterRegistryOnly1Match() throws Exception {
    Properties variableSubstitutions = createProperties();
    variableSubstitutions.remove("adapter.id");
    variableSubstitutions.remove("channel.id");
    variableSubstitutions.remove("workflow.id1");

    // We don't actually want to go to the file system for the variable substitutions
    when(propertyFileLoader.load(anyString(), anyBoolean())).thenReturn(variableSubstitutions);

    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);

    assertEquals("${adapter.id}", adapter.getUniqueId());
    assertEquals("${channel.id}", adapter.getChannelList().get(0).getUniqueId());
    assertEquals("${workflow.id1}", adapter.getChannelList().get(0).getWorkflowList().get(0).getUniqueId());
    assertEquals("MyWorkflowID2", adapter.getChannelList().get(0).getWorkflowList().get(1).getUniqueId());
  }

  @Test
  public void testSelfReferentialVariables() throws Exception {
    // We don't actually want to go to the file system for the variable substitutions
    Properties variableSubstitutions = createProperties();
    variableSubstitutions.setProperty("SELF_REFERENTIAL", "${SELF_REFERENTIAL}");
    when(propertyFileLoader.load(anyString(), anyBoolean())).thenReturn(variableSubstitutions);

    assertThrows(CoreException.class, () -> preProcessor.process(variablesAdapterFile.toURI().toURL()));
  }

  private void doStandardAssertions(Adapter adapter) {
    assertEquals("MyAdapterID", adapter.getUniqueId());
    assertEquals("MyChannelID", adapter.getChannelList().get(0).getUniqueId());
    assertEquals("AnotherChannelId", adapter.getChannelList().get(1).getUniqueId());
    assertEquals("MyWorkflowID1", adapter.getChannelList().get(0).getWorkflowList().get(0).getUniqueId());
    assertEquals("MyWorkflowID2", adapter.getChannelList().get(0).getWorkflowList().get(1).getUniqueId());
    assertEquals("MyWorkflowID1", adapter.getChannelList().get(1).getWorkflowList().get(0).getUniqueId());
    assertEquals("MyWorkflowID2", adapter.getChannelList().get(1).getWorkflowList().get(1).getUniqueId());
  }

  private Properties createProperties() {
    Properties subs = new Properties();
    subs.put("adapter.id", "MyAdapterID");
    subs.put("channel.id", "MyChannelID");
    subs.put("channel.alternate.id", "AnotherChannelId");
    subs.put("workflow.id1", "MyWorkflowID1");
    subs.put("workflow.id2", "MyWorkflowID2");
    return subs;
  }

}
