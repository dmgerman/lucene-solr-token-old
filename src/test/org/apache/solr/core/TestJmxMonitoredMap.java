begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrConfig
operator|.
name|JmxConfiguration
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerConnection
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectInstance
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Query
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnector
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnectorFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXServiceURL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RemoteException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|registry
operator|.
name|LocateRegistry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Test for JmxMonitoredMap  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestJmxMonitoredMap
specifier|public
class|class
name|TestJmxMonitoredMap
block|{
DECL|field|port
specifier|private
name|int
name|port
init|=
literal|0
decl_stmt|;
DECL|field|connector
specifier|private
name|JMXConnector
name|connector
decl_stmt|;
DECL|field|mbeanServer
specifier|private
name|MBeanServerConnection
name|mbeanServer
decl_stmt|;
DECL|field|monitoredMap
specifier|private
name|JmxMonitoredMap
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|monitoredMap
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|ServerSocket
name|server
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|port
operator|=
name|server
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using port: "
operator|+
name|port
argument_list|)
expr_stmt|;
try|try
block|{
name|LocateRegistry
operator|.
name|createRegistry
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{         }
name|String
name|url
init|=
literal|"service:jmx:rmi:///jndi/rmi://:"
operator|+
name|port
operator|+
literal|"/solrjmx"
decl_stmt|;
name|JmxConfiguration
name|config
init|=
operator|new
name|JmxConfiguration
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|,
name|url
argument_list|)
decl_stmt|;
name|monitoredMap
operator|=
operator|new
name|JmxMonitoredMap
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
argument_list|(
literal|null
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|JMXServiceURL
name|u
init|=
operator|new
name|JMXServiceURL
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|connector
operator|=
name|JMXConnectorFactory
operator|.
name|connect
argument_list|(
name|u
argument_list|)
expr_stmt|;
name|mbeanServer
operator|=
name|connector
operator|.
name|getMBeanServerConnection
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{        }
block|}
block|}
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|connector
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
block|}
annotation|@
name|Test
DECL|method|testPutRemoveClear
specifier|public
name|void
name|testPutRemoveClear
parameter_list|()
throws|throws
name|Exception
block|{
name|MockInfoMBean
name|mock
init|=
operator|new
name|MockInfoMBean
argument_list|()
decl_stmt|;
name|monitoredMap
operator|.
name|put
argument_list|(
literal|"mock"
argument_list|,
name|mock
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ObjectInstance
argument_list|>
name|objects
init|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
name|Query
operator|.
name|match
argument_list|(
name|Query
operator|.
name|attr
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|Query
operator|.
name|value
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"No MBean for mock object found in MBeanServer"
argument_list|,
name|objects
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|monitoredMap
operator|.
name|remove
argument_list|(
literal|"mock"
argument_list|)
expr_stmt|;
name|objects
operator|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
name|Query
operator|.
name|match
argument_list|(
name|Query
operator|.
name|attr
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|Query
operator|.
name|value
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"MBean for mock object found in MBeanServer even after removal"
argument_list|,
name|objects
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|monitoredMap
operator|.
name|put
argument_list|(
literal|"mock"
argument_list|,
name|mock
argument_list|)
expr_stmt|;
name|monitoredMap
operator|.
name|put
argument_list|(
literal|"mock2"
argument_list|,
name|mock
argument_list|)
expr_stmt|;
name|objects
operator|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
name|Query
operator|.
name|match
argument_list|(
name|Query
operator|.
name|attr
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|Query
operator|.
name|value
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"No MBean for mock object found in MBeanServer"
argument_list|,
name|objects
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|monitoredMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|objects
operator|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
name|Query
operator|.
name|match
argument_list|(
name|Query
operator|.
name|attr
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|Query
operator|.
name|value
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"MBean for mock object found in MBeanServer even after clear has been called"
argument_list|,
name|objects
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|MockInfoMBean
specifier|private
class|class
name|MockInfoMBean
implements|implements
name|SolrInfoMBean
block|{
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"mock"
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|OTHER
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"mock"
return|;
block|}
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"mock"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"mock"
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"mock"
return|;
block|}
block|}
block|}
end_class
end_unit
