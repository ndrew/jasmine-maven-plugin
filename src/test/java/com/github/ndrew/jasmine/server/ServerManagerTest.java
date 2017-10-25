/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.ndrew.jasmine.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ServerManagerTest {

  @Mock
  private ResourceHandlerConfigurator configurator;

  @Mock
  private Server server;

  @Mock
  private Connector connector;

  private ServerManager serverManager;

  @Captor
  private ArgumentCaptor<Connector> connectorCaptor;

  @Before
  public void before() {
    this.serverManager = new ServerManager(server, connector, configurator);
  }

  @Test
  public void testStartAnyPort() throws Exception {
    this.serverManager.start();

    verify(this.server).addConnector(this.connector);
    verify(this.connector).setPort(0);
  }

  @Test
  public void testStartOnSpecificPort() throws Exception {
    this.serverManager.start(1234);

    verify(this.server).addConnector(connectorCaptor.capture());
    verify(this.connector).setPort(1234);
  }

  @Test
  public void testStop() throws Exception {
    this.serverManager.stop();
    verify(this.server).stop();
  }

  @Test
  public void testJoin() throws Exception {
    this.serverManager.join();
    verify(this.server).join();
  }

}
