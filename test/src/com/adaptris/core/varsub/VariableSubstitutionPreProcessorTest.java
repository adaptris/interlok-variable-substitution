package com.adaptris.core.varsub;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Properties;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adaptris.core.Adapter;
import com.adaptris.core.CoreException;
import com.adaptris.core.DefaultMarshaller;
import com.adaptris.core.runtime.ComponentManagerCase;
import com.adaptris.core.stubs.JunitBootstrapProperties;

public class VariableSubstitutionPreProcessorTest extends ComponentManagerCase {

  private static final String PROPS_VARIABLES_ADAPTER = "varsub.variables.adapter.xml";
  private static final String SAMPLE_SUBSTITUTION_PROPERTIES = "varsub.variables.properties";
  
  private File variablesAdapterFile;
  
  @Mock
  private PropertyFileLoader propertyFileLoader;
  
  private VarariableSubstitutionPreProcessor preProcessor;
  
  private Properties variableSubstitutions;
  
  private Properties sampleBootstrapProperties;
  
  public VariableSubstitutionPreProcessorTest(String name) {
    super(name);
  }

  public void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void setUp() throws Exception {
    super.setUp();
    
    MockitoAnnotations.initMocks(this);
    
    variablesAdapterFile = new File(PROPERTIES.getProperty(PROPS_VARIABLES_ADAPTER));
    
    variableSubstitutions = new Properties();
    variableSubstitutions.put("adapter.id", "MyAdapterID");
    variableSubstitutions.put("channel.id", "MyChannelID");
    variableSubstitutions.put("workflow.id1", "MyWorkflowID1");
    variableSubstitutions.put("workflow.id2", "MyWorkflowID2");
    
    sampleBootstrapProperties = new Properties();
    sampleBootstrapProperties.put("variable-substitution.varprefix", "${");
    sampleBootstrapProperties.put("variable-substitution.varpostfix", "}");
    sampleBootstrapProperties.put("variable-substitution.impl", "simple");
    sampleBootstrapProperties.put("variable-substitution.properties.url", "dummy-url-using-mocks-instead");
    
    preProcessor = new VarariableSubstitutionPreProcessor(new JunitBootstrapProperties(sampleBootstrapProperties));
    preProcessor.setPropertyFileLoader(propertyFileLoader);
  }
  
  public void testSimpleVarSubAdapterRegistry() throws Exception {
    // We don't actually want to go to the file system for the variable substitutions
    when(propertyFileLoader.load(anyString())).thenReturn(variableSubstitutions);
    
    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
    
    assertEquals("MyAdapterID", adapter.getUniqueId());
    assertEquals("MyChannelID", adapter.getChannelList().get(0).getUniqueId());
    assertEquals("MyWorkflowID1", adapter.getChannelList().get(0).getWorkflowList().get(0).getUniqueId());
    assertEquals("MyWorkflowID2", adapter.getChannelList().get(0).getWorkflowList().get(1).getUniqueId());
  }
  
  public void testSimpleVarSubAdapterRegistryWithProperPropertiesFile() throws Exception {
    // We don't actually want to go to the file system for the variable substitutions
    when(propertyFileLoader.load(anyString())).thenReturn(variableSubstitutions);
    
    sampleBootstrapProperties.put("variable-substitution.properties.url", PROPERTIES.getProperty(SAMPLE_SUBSTITUTION_PROPERTIES));
    
    preProcessor.setBootstrapProperties(new JunitBootstrapProperties(sampleBootstrapProperties));
    
    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
    
    assertEquals("MyAdapterID", adapter.getUniqueId());
    assertEquals("MyChannelID", adapter.getChannelList().get(0).getUniqueId());
    assertEquals("MyWorkflowID1", adapter.getChannelList().get(0).getWorkflowList().get(0).getUniqueId());
    assertEquals("MyWorkflowID2", adapter.getChannelList().get(0).getWorkflowList().get(1).getUniqueId());
  }
  
  public void testSimpleVarSubAdapterRegistryNoPropertyURL() throws Exception {
    // We don't actually want to go to the file system for the variable substitutions
    when(propertyFileLoader.load(anyString())).thenReturn(variableSubstitutions);
    
    // Remove the property - no substitution should take place.
    sampleBootstrapProperties.remove("variable-substitution.properties.url");
    preProcessor.setBootstrapProperties(new JunitBootstrapProperties(sampleBootstrapProperties));
    
    try {
      preProcessor.process(variablesAdapterFile.toURI().toURL());
      fail("Should fail, missing mandatory property url.");
    } catch (CoreException ex) {
      // expected
    }
  }
  
  public void testSimpleVarSubAdapterRegistryNoPrefix() throws Exception {
    // We don't actually want to go to the file system for the variable substitutions
    when(propertyFileLoader.load(anyString())).thenReturn(variableSubstitutions);
    
    // Remove the property - should use the default, which happens to be the same anyway....
    sampleBootstrapProperties.remove("variable-substitution.varprefix");
    preProcessor.setBootstrapProperties(new JunitBootstrapProperties(sampleBootstrapProperties));
    
    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
    
    assertEquals("MyAdapterID", adapter.getUniqueId());
    assertEquals("MyChannelID", adapter.getChannelList().get(0).getUniqueId());
    assertEquals("MyWorkflowID1", adapter.getChannelList().get(0).getWorkflowList().get(0).getUniqueId());
    assertEquals("MyWorkflowID2", adapter.getChannelList().get(0).getWorkflowList().get(1).getUniqueId());
  }
  
  public void testSimpleVarSubAdapterRegistryNoPostfix() throws Exception {
    // We don't actually want to go to the file system for the variable substitutions
    when(propertyFileLoader.load(anyString())).thenReturn(variableSubstitutions);
    
    // Remove the property - should use the default, which happens to be the same anyway....
    sampleBootstrapProperties.remove("variable-substitution.varpostfix");
    preProcessor.setBootstrapProperties(new JunitBootstrapProperties(sampleBootstrapProperties));
    
    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
    
    assertEquals("MyAdapterID", adapter.getUniqueId());
    assertEquals("MyChannelID", adapter.getChannelList().get(0).getUniqueId());
    assertEquals("MyWorkflowID1", adapter.getChannelList().get(0).getWorkflowList().get(0).getUniqueId());
    assertEquals("MyWorkflowID2", adapter.getChannelList().get(0).getWorkflowList().get(1).getUniqueId());
  }
  
  public void testSimpleVarSubAdapterRegistryOnly1Match() throws Exception {
    variableSubstitutions.remove("adapter.id");
    variableSubstitutions.remove("channel.id");
    variableSubstitutions.remove("workflow.id1");
    
 // We don't actually want to go to the file system for the variable substitutions
    when(propertyFileLoader.load(anyString())).thenReturn(variableSubstitutions);
    
    String xml = preProcessor.process(variablesAdapterFile.toURI().toURL());
    Adapter adapter = (Adapter) DefaultMarshaller.getDefaultMarshaller().unmarshal(xml);
    
    assertEquals("${adapter.id}", adapter.getUniqueId());
    assertEquals("${channel.id}", adapter.getChannelList().get(0).getUniqueId());
    assertEquals("${workflow.id1}", adapter.getChannelList().get(0).getWorkflowList().get(0).getUniqueId());
    assertEquals("MyWorkflowID2", adapter.getChannelList().get(0).getWorkflowList().get(1).getUniqueId());
  }
}
