/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.web.itest.karaf;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Hashtable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author achim
 */
@RunWith(PaxExam.class)
public class FeaturesKarafTest extends KarafBaseTest {

	@Configuration
	public Option[] config() {
		return jettyConfig();
	}

	@Test
	public void test() throws Exception {

		//this is needed since the test is a bit to fast :)
		Thread.sleep(2000);

		assertTrue(featuresService.isInstalled(featuresService
				.getFeature("pax-war")));
		assertTrue(featuresService.isInstalled(featuresService
				.getFeature("pax-http-whiteboard")));
	}

	@Test
	@Ignore("Fails due to changes in Karaf an JMX")
	public void testJmx() throws Exception {
		Thread.sleep(2000);

		JMXConnector connector = null;
		try {
			connector = this.getJMXConnector();
			MBeanServerConnection connection = connector.getMBeanServerConnection();
			ObjectName name = new ObjectName("org.ops4j.pax.web.service.jetty.internal:type=jettyserverhandlercollection,id=0");
			Object handlers = connection.getAttribute(name, "handlers");
			assertNotNull(handlers);
		} finally {
			if (connector != null) {
				connector.close();
			}
		}
	}

	public JMXConnector getJMXConnector() throws Exception {
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + RMI_REG_PORT + "/karaf-root");
		Hashtable<String, Object> env = new Hashtable<>();
		String[] credentials = new String[]{"karaf", "karaf"};
		env.put("jmx.remote.credentials", credentials);
		return JMXConnectorFactory.connect(url, env);
	}

}
