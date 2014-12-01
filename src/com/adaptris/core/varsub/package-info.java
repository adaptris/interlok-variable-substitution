/**
 * Custom {@link com.adaptris.core.runtime.AdapterRegistry} implementation that
 * supports variable substitution before configuration is unmarshalled.
 * <p>
 * This AdapterRegistry can be activated by the setting the system property
 * {@value com.adaptris.core.management.AdapterConfigManager#ADAPTER_REGISTRY_IMPL} to be
 * {@code com.adaptris.core.varsub.AdapterRegistry} and making sure the required jars are available on the classpath.
 * </p>
 * <p>
 * The following properties can be specified in the bootstrap.propertiues to control the behaviour of the variable substitution;
 * </p>
 * <p>
 * <table border="1">
 * <tr><th>Property</th><th>Default</th><th>Mandatory</th><th>Description</th></tr>
 * <tr>
 *   <td>variable-substitution.varprefix</td>
 *   <td><strong>${</strong></td>
 *   <td>No</td>
 *   <td>The value here will be prepended to the variable name to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 *   <td>variable-substitution.varpostfix</td>
 *   <td><strong>}</strong></td>
 *   <td>No</td>
 *   <td>The value here will be appended to the variable name to search for in the configuration to be switched out.</td>
 * </tr>
 * <tr>
 *   <td>variable-substitution.properties.url</td>
 *   <td></td>
 *   <td>Yes</td>
 *   <td>The URL to the property file containing the list of substitutions; in the form of variableName=Value. One substitution per line.</td>
 * </tr>
 * <tr>
 *   <td>variable-substitution.impl</td>
 *   <td><strong>simple</strong></td>
 *   <td>No</td>
 *   <td>The substitution engine that will perform the variable substitution.  At this time there is only one implementation - "simple".</td>
 * </tr>
 * </table>
 * </p>
 * For instance if you have in your bootstrap.properties
 * <pre>
 * <code>
 * sysprop.com.adaptris.adapter.registry.impl=com.adaptris.core.varsub.AdapterRegistry
 * variable-substitution.properties.url=file://localhost//path/to/my/variables
 * </code>
 * </pre>
 * And {@code /path/to/my/variables} contains
 * <pre>
 * <code>
 * broker.url=tcp://localhost:2506
 * broker.backup.url=tcp://my.host:2507
 * </code>
 * </pre>
 * Then all instances of {@code ${broker.url}} and {@code ${broker.backup.url}} will be replaced within the adapter.xml as it is
 * read in, but before the Adapter itself is unmarshalled.
 * </p>
 */
package com.adaptris.core.varsub;