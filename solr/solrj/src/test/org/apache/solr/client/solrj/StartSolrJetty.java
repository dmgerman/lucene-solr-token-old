begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
package|;
end_package
begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Connector
import|;
end_import
begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Server
import|;
end_import
begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|bio
operator|.
name|SocketConnector
import|;
end_import
begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|webapp
operator|.
name|WebAppContext
import|;
end_import
begin_comment
comment|/**  * @since solr 1.3  */
end_comment
begin_class
DECL|class|StartSolrJetty
specifier|public
class|class
name|StartSolrJetty
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
comment|//System.setProperty("solr.solr.home", "../../../example/solr");
name|Server
name|server
init|=
operator|new
name|Server
argument_list|()
decl_stmt|;
name|SocketConnector
name|connector
init|=
operator|new
name|SocketConnector
argument_list|()
decl_stmt|;
comment|// Set some timeout options to make debugging easier.
name|connector
operator|.
name|setMaxIdleTime
argument_list|(
literal|1000
operator|*
literal|60
operator|*
literal|60
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setSoLingerTime
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setPort
argument_list|(
literal|8080
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectors
argument_list|(
operator|new
name|Connector
index|[]
block|{
name|connector
block|}
argument_list|)
expr_stmt|;
name|WebAppContext
name|bb
init|=
operator|new
name|WebAppContext
argument_list|()
decl_stmt|;
name|bb
operator|.
name|setServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|bb
operator|.
name|setContextPath
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|bb
operator|.
name|setWar
argument_list|(
literal|"webapp/web"
argument_list|)
expr_stmt|;
comment|//    // START JMX SERVER
comment|//    if( true ) {
comment|//      MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
comment|//      MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
comment|//      server.getContainer().addEventListener(mBeanContainer);
comment|//      mBeanContainer.start();
comment|//    }
name|server
operator|.
name|addHandler
argument_list|(
name|bb
argument_list|)
expr_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP"
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
name|System
operator|.
name|in
operator|.
name|available
argument_list|()
operator|==
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|server
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
