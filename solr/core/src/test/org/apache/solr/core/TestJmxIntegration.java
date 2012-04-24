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
name|core
operator|.
name|JmxMonitoredMap
operator|.
name|SolrDynamicMBean
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
name|util
operator|.
name|AbstractSolrTestCase
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
name|Ignore
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
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Test for JMX Integration  *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestJmxIntegration
specifier|public
class|class
name|TestJmxIntegration
extends|extends
name|AbstractSolrTestCase
block|{
annotation|@
name|Override
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
annotation|@
name|Override
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
comment|// Make sure that at least one MBeanServer is available
name|MBeanServer
name|mbeanServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJmxRegistration
specifier|public
name|void
name|testJmxRegistration
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|MBeanServer
argument_list|>
name|servers
init|=
name|MBeanServerFactory
operator|.
name|findMBeanServer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Servers in testJmxRegistration: "
operator|+
name|servers
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"MBeanServers were null"
argument_list|,
name|servers
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"No MBeanServer was found"
argument_list|,
name|servers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|MBeanServer
name|mbeanServer
init|=
name|servers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No MBeans found in server"
argument_list|,
name|mbeanServer
operator|.
name|getMBeanCount
argument_list|()
operator|>
literal|0
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
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"No SolrInfoMBean objects found in mbean server"
argument_list|,
name|objects
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ObjectInstance
name|o
range|:
name|objects
control|)
block|{
name|MBeanInfo
name|mbeanInfo
init|=
name|mbeanServer
operator|.
name|getMBeanInfo
argument_list|(
name|o
operator|.
name|getObjectName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mbeanInfo
operator|.
name|getClassName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|SolrDynamicMBean
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
literal|"No Attributes found for mbean: "
operator|+
name|mbeanInfo
argument_list|,
name|mbeanInfo
operator|.
name|getAttributes
argument_list|()
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testJmxUpdate
specifier|public
name|void
name|testJmxUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|MBeanServer
argument_list|>
name|servers
init|=
name|MBeanServerFactory
operator|.
name|findMBeanServer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Servers in testJmxUpdate: "
operator|+
name|servers
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SolrInfoMBean
name|bean
init|=
literal|null
decl_stmt|;
comment|// wait until searcher is registered
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|bean
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
expr_stmt|;
if|if
condition|(
name|bean
operator|!=
literal|null
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bean
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"searcher was never registered"
argument_list|)
throw|;
name|ObjectName
name|searcher
init|=
name|getObjectName
argument_list|(
literal|"searcher"
argument_list|,
name|bean
argument_list|)
decl_stmt|;
name|MBeanServer
name|mbeanServer
init|=
name|servers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Mbeans in server: "
operator|+
name|mbeanServer
operator|.
name|queryNames
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"No mbean found for SolrIndexSearcher"
argument_list|,
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
name|searcher
argument_list|,
literal|null
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|oldNumDocs
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|searcher
argument_list|,
literal|"numDocs"
argument_list|)
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"commit"
argument_list|,
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|searcher
argument_list|,
literal|"numDocs"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"New numDocs is same as old numDocs as reported by JMX"
argument_list|,
name|numDocs
operator|>
name|oldNumDocs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"timing problem? https://issues.apache.org/jira/browse/SOLR-2715"
argument_list|)
DECL|method|testJmxOnCoreReload
specifier|public
name|void
name|testJmxOnCoreReload
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|MBeanServer
argument_list|>
name|servers
init|=
name|MBeanServerFactory
operator|.
name|findMBeanServer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|MBeanServer
name|mbeanServer
init|=
name|servers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|coreName
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|ObjectInstance
argument_list|>
name|oldBeans
init|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|oldNumberOfObjects
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ObjectInstance
name|bean
range|:
name|oldBeans
control|)
block|{
try|try
block|{
if|if
condition|(
name|String
operator|.
name|valueOf
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|bean
operator|.
name|getObjectName
argument_list|()
argument_list|,
literal|"coreHashCode"
argument_list|)
argument_list|)
condition|)
block|{
name|oldNumberOfObjects
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AttributeNotFoundException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Before Reload: Size of infoRegistry: "
operator|+
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" MBeans: "
operator|+
name|oldNumberOfObjects
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of registered MBeans is not the same as info registry size"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|oldNumberOfObjects
argument_list|)
expr_stmt|;
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|coreName
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ObjectInstance
argument_list|>
name|newBeans
init|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|newNumberOfObjects
init|=
literal|0
decl_stmt|;
name|int
name|registrySize
init|=
literal|0
decl_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
decl_stmt|;
try|try
block|{
name|registrySize
operator|=
name|core
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
for|for
control|(
name|ObjectInstance
name|bean
range|:
name|newBeans
control|)
block|{
try|try
block|{
if|if
condition|(
name|String
operator|.
name|valueOf
argument_list|(
name|core
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|bean
operator|.
name|getObjectName
argument_list|()
argument_list|,
literal|"coreHashCode"
argument_list|)
argument_list|)
condition|)
block|{
name|newNumberOfObjects
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AttributeNotFoundException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
finally|finally
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"After Reload: Size of infoRegistry: "
operator|+
name|registrySize
operator|+
literal|" MBeans: "
operator|+
name|newNumberOfObjects
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of registered MBeans is not the same as info registry size"
argument_list|,
name|registrySize
argument_list|,
name|newNumberOfObjects
argument_list|)
expr_stmt|;
block|}
DECL|method|getObjectName
specifier|private
name|ObjectName
name|getObjectName
parameter_list|(
name|String
name|key
parameter_list|,
name|SolrInfoMBean
name|infoBean
parameter_list|)
throws|throws
name|MalformedObjectNameException
block|{
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|infoBean
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|coreName
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|ObjectName
operator|.
name|getInstance
argument_list|(
operator|(
literal|"solr"
operator|+
operator|(
literal|null
operator|!=
name|coreName
condition|?
literal|"/"
operator|+
name|coreName
else|:
literal|""
operator|)
operator|)
argument_list|,
name|map
argument_list|)
return|;
block|}
block|}
end_class
end_unit
