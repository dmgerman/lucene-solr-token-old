begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|common
operator|.
name|SolrException
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
name|PropertiesUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|ConfigSolrXml
specifier|public
class|class
name|ConfigSolrXml
extends|extends
name|ConfigSolr
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConfigSolrXml
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|solrCoreDiscoverer
specifier|private
name|SolrCoreDiscoverer
name|solrCoreDiscoverer
init|=
operator|new
name|SolrCoreDiscoverer
argument_list|()
decl_stmt|;
DECL|field|coreDescriptorMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CoreDescriptor
argument_list|>
name|coreDescriptorMap
decl_stmt|;
DECL|method|ConfigSolrXml
specifier|public
name|ConfigSolrXml
parameter_list|(
name|Config
name|config
parameter_list|,
name|CoreContainer
name|container
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|checkForIllegalConfig
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|fillPropMap
argument_list|()
expr_stmt|;
name|String
name|coreRoot
init|=
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_COREROOTDIRECTORY
argument_list|,
operator|(
name|container
operator|==
literal|null
condition|?
literal|null
else|:
name|container
operator|.
name|getSolrHome
argument_list|()
operator|)
argument_list|)
decl_stmt|;
name|coreDescriptorMap
operator|=
name|solrCoreDiscoverer
operator|.
name|discover
argument_list|(
name|container
argument_list|,
operator|new
name|File
argument_list|(
name|coreRoot
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkForIllegalConfig
specifier|private
name|void
name|checkForIllegalConfig
parameter_list|(
name|CoreContainer
name|container
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Do sanity checks - we don't want to find old style config
name|failIfFound
argument_list|(
literal|"solr/@coreLoadThreads"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/@persist"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/@sharedLib"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/@zkHost"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/logging/@class"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/logging/@enabled"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/logging/watcher/@size"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/logging/watcher/@threshold"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@adminHandler"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@distribUpdateConnTimeout"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@distribUpdateSoTimeout"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@host"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@hostContext"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@hostPort"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@leaderVoteWait"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@managementPath"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@shareSchema"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@transientCacheSize"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@zkClientTimeout"
argument_list|)
expr_stmt|;
comment|// These have no counterpart in 5.0, asking for any of these in Solr 5.0
comment|// will result in an error being
comment|// thrown.
name|failIfFound
argument_list|(
literal|"solr/cores/@defaultCoreName"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/@persistent"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/cores/@adminPath"
argument_list|)
expr_stmt|;
block|}
DECL|method|failIfFound
specifier|private
name|void
name|failIfFound
parameter_list|(
name|String
name|xPath
parameter_list|)
block|{
if|if
condition|(
name|config
operator|.
name|getVal
argument_list|(
name|xPath
argument_list|,
literal|false
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Should not have found "
operator|+
name|xPath
operator|+
literal|" solr.xml may be a mix of old and new style formats."
argument_list|)
throw|;
block|}
block|}
comment|// We can do this in 5.0 when we read the solr.xml since we don't need to keep the original around for persistence.
DECL|method|doSub
specifier|private
name|String
name|doSub
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|val
init|=
name|config
operator|.
name|getVal
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|val
operator|=
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|val
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|val
return|;
block|}
DECL|method|fillPropMap
specifier|private
name|void
name|fillPropMap
parameter_list|()
block|{
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_ADMINHANDLER
argument_list|,
name|doSub
argument_list|(
literal|"solr/str[@name='adminHandler']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_CORELOADTHREADS
argument_list|,
name|doSub
argument_list|(
literal|"solr/int[@name='coreLoadThreads']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_COREROOTDIRECTORY
argument_list|,
name|doSub
argument_list|(
literal|"solr/str[@name='coreRootDirectory']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_DISTRIBUPDATECONNTIMEOUT
argument_list|,
name|doSub
argument_list|(
literal|"solr/solrcloud/int[@name='distribUpdateConnTimeout']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_DISTRIBUPDATESOTIMEOUT
argument_list|,
name|doSub
argument_list|(
literal|"solr/solrcloud/int[@name='distribUpdateSoTimeout']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_HOST
argument_list|,
name|doSub
argument_list|(
literal|"solr/solrcloud/str[@name='host']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_HOSTCONTEXT
argument_list|,
name|doSub
argument_list|(
literal|"solr/solrcloud/str[@name='hostContext']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_HOSTPORT
argument_list|,
name|doSub
argument_list|(
literal|"solr/solrcloud/int[@name='hostPort']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_LEADERVOTEWAIT
argument_list|,
name|doSub
argument_list|(
literal|"solr/solrcloud/int[@name='leaderVoteWait']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_MANAGEMENTPATH
argument_list|,
name|doSub
argument_list|(
literal|"solr/str[@name='managementPath']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHAREDLIB
argument_list|,
name|doSub
argument_list|(
literal|"solr/str[@name='sharedLib']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHARESCHEMA
argument_list|,
name|doSub
argument_list|(
literal|"solr/str[@name='shareSchema']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_TRANSIENTCACHESIZE
argument_list|,
name|doSub
argument_list|(
literal|"solr/int[@name='transientCacheSize']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_ZKCLIENTTIMEOUT
argument_list|,
name|doSub
argument_list|(
literal|"solr/solrcloud/int[@name='zkClientTimeout']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_ZKHOST
argument_list|,
name|doSub
argument_list|(
literal|"solr/solrcloud/str[@name='zkHost']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_CLASS
argument_list|,
name|doSub
argument_list|(
literal|"solr/logging/str[@name='class']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_ENABLED
argument_list|,
name|doSub
argument_list|(
literal|"solr/logging/str[@name='enabled']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_WATCHER_SIZE
argument_list|,
name|doSub
argument_list|(
literal|"solr/logging/watcher/int[@name='size']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_WATCHER_THRESHOLD
argument_list|,
name|doSub
argument_list|(
literal|"solr/logging/watcher/int[@name='threshold']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHARDHANDLERFACTORY_CLASS
argument_list|,
name|doSub
argument_list|(
literal|"solr/shardHandlerFactory/@class"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHARDHANDLERFACTORY_NAME
argument_list|,
name|doSub
argument_list|(
literal|"solr/shardHandlerFactory/@name"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHARDHANDLERFACTORY_CONNTIMEOUT
argument_list|,
name|doSub
argument_list|(
literal|"solr/shardHandlerFactory/int[@name='connTimeout']"
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHARDHANDLERFACTORY_SOCKETTIMEOUT
argument_list|,
name|doSub
argument_list|(
literal|"solr/shardHandlerFactory/int[@name='socketTimeout']"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readCoreAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|readCoreAttributes
parameter_list|(
name|String
name|coreName
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
return|return
name|attrs
return|;
comment|// this is a no-op.... intentionally
block|}
annotation|@
name|Override
DECL|method|getAllCoreNames
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAllCoreNames
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|coreDescriptorMap
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|getProperty
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|coreName
parameter_list|,
name|String
name|property
parameter_list|,
name|String
name|defaultVal
parameter_list|)
block|{
name|CoreDescriptor
name|cd
init|=
name|coreDescriptorMap
operator|.
name|get
argument_list|(
name|coreName
argument_list|)
decl_stmt|;
if|if
condition|(
name|cd
operator|==
literal|null
condition|)
return|return
name|defaultVal
return|;
return|return
name|cd
operator|.
name|getProperty
argument_list|(
name|property
argument_list|,
name|defaultVal
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readCoreProperties
specifier|public
name|Properties
name|readCoreProperties
parameter_list|(
name|String
name|coreName
parameter_list|)
block|{
name|CoreDescriptor
name|cd
init|=
name|coreDescriptorMap
operator|.
name|get
argument_list|(
name|coreName
argument_list|)
decl_stmt|;
if|if
condition|(
name|cd
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|Properties
argument_list|(
name|cd
operator|.
name|getCoreProperties
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getCoreProperties
specifier|static
name|Properties
name|getCoreProperties
parameter_list|(
name|String
name|instanceDir
parameter_list|,
name|CoreDescriptor
name|dcore
parameter_list|)
block|{
name|String
name|file
init|=
name|dcore
operator|.
name|getPropertiesName
argument_list|()
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
name|file
operator|=
literal|"conf"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrcore.properties"
expr_stmt|;
name|File
name|corePropsFile
init|=
operator|new
name|File
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|corePropsFile
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|corePropsFile
operator|=
operator|new
name|File
argument_list|(
name|instanceDir
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
name|Properties
name|p
init|=
name|dcore
operator|.
name|getCoreProperties
argument_list|()
decl_stmt|;
if|if
condition|(
name|corePropsFile
operator|.
name|exists
argument_list|()
operator|&&
name|corePropsFile
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|p
operator|=
operator|new
name|Properties
argument_list|(
name|dcore
operator|.
name|getCoreProperties
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|corePropsFile
argument_list|)
expr_stmt|;
name|p
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error loading properties "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|p
return|;
block|}
annotation|@
name|Override
DECL|method|substituteProperties
specifier|public
name|void
name|substituteProperties
parameter_list|()
block|{
name|config
operator|.
name|substituteProperties
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
