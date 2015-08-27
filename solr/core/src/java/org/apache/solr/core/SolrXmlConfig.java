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
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|logging
operator|.
name|LogWatcherConfig
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
name|update
operator|.
name|UpdateShardHandlerConfig
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
name|DOMUtil
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
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|InputSource
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
operator|.
name|NAME
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|SolrXmlConfig
specifier|public
class|class
name|SolrXmlConfig
block|{
DECL|field|SOLR_XML_FILE
specifier|public
specifier|final
specifier|static
name|String
name|SOLR_XML_FILE
init|=
literal|"solr.xml"
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrXmlConfig
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|fromConfig
specifier|public
specifier|static
name|NodeConfig
name|fromConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|checkForIllegalConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|config
operator|.
name|substituteProperties
argument_list|()
expr_stmt|;
name|CloudConfig
name|cloudConfig
init|=
literal|null
decl_stmt|;
name|UpdateShardHandlerConfig
name|deprecatedUpdateConfig
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|getNodeList
argument_list|(
literal|"solr/solrcloud"
argument_list|,
literal|false
argument_list|)
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|cloudSection
init|=
name|readNodeListAsNamedList
argument_list|(
name|config
argument_list|,
literal|"solr/solrcloud/*[@name]"
argument_list|,
literal|"<solrcloud>"
argument_list|)
decl_stmt|;
name|deprecatedUpdateConfig
operator|=
name|loadUpdateConfig
argument_list|(
name|cloudSection
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cloudConfig
operator|=
name|fillSolrCloudSection
argument_list|(
name|cloudSection
argument_list|)
expr_stmt|;
block|}
name|NamedList
argument_list|<
name|Object
argument_list|>
name|entries
init|=
name|readNodeListAsNamedList
argument_list|(
name|config
argument_list|,
literal|"solr/*[@name]"
argument_list|,
literal|"<solr>"
argument_list|)
decl_stmt|;
name|String
name|nodeName
init|=
operator|(
name|String
operator|)
name|entries
operator|.
name|remove
argument_list|(
literal|"nodeName"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|nodeName
argument_list|)
operator|&&
name|cloudConfig
operator|!=
literal|null
condition|)
name|nodeName
operator|=
name|cloudConfig
operator|.
name|getHost
argument_list|()
expr_stmt|;
name|UpdateShardHandlerConfig
name|updateConfig
decl_stmt|;
if|if
condition|(
name|deprecatedUpdateConfig
operator|==
literal|null
condition|)
block|{
name|updateConfig
operator|=
name|loadUpdateConfig
argument_list|(
name|readNodeListAsNamedList
argument_list|(
name|config
argument_list|,
literal|"solr/updateshardhandler/*[@name]"
argument_list|,
literal|"<updateshardhandler>"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|updateConfig
operator|=
name|loadUpdateConfig
argument_list|(
name|readNodeListAsNamedList
argument_list|(
name|config
argument_list|,
literal|"solr/updateshardhandler/*[@name]"
argument_list|,
literal|"<updateshardhandler>"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateConfig
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
literal|"UpdateShardHandler configuration defined twice in solr.xml"
argument_list|)
throw|;
block|}
name|updateConfig
operator|=
name|deprecatedUpdateConfig
expr_stmt|;
block|}
name|NodeConfig
operator|.
name|NodeConfigBuilder
name|configBuilder
init|=
operator|new
name|NodeConfig
operator|.
name|NodeConfigBuilder
argument_list|(
name|nodeName
argument_list|,
name|config
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
decl_stmt|;
name|configBuilder
operator|.
name|setUpdateShardHandlerConfig
argument_list|(
name|updateConfig
argument_list|)
expr_stmt|;
name|configBuilder
operator|.
name|setShardHandlerFactoryConfig
argument_list|(
name|getShardHandlerFactoryPluginInfo
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
name|configBuilder
operator|.
name|setLogWatcherConfig
argument_list|(
name|loadLogWatcherConfig
argument_list|(
name|config
argument_list|,
literal|"solr/logging/*[@name]"
argument_list|,
literal|"solr/logging/watcher/*[@name]"
argument_list|)
argument_list|)
expr_stmt|;
name|configBuilder
operator|.
name|setSolrProperties
argument_list|(
name|loadProperties
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|cloudConfig
operator|!=
literal|null
condition|)
name|configBuilder
operator|.
name|setCloudConfig
argument_list|(
name|cloudConfig
argument_list|)
expr_stmt|;
return|return
name|fillSolrSection
argument_list|(
name|configBuilder
argument_list|,
name|entries
argument_list|)
return|;
block|}
DECL|method|fromFile
specifier|public
specifier|static
name|NodeConfig
name|fromFile
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|File
name|configFile
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Loading container configuration from {}"
argument_list|,
name|configFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|configFile
operator|.
name|exists
argument_list|()
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
literal|"solr.xml does not exist in "
operator|+
name|configFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" cannot start Solr"
argument_list|)
throw|;
block|}
try|try
init|(
name|InputStream
name|inputStream
init|=
operator|new
name|FileInputStream
argument_list|(
name|configFile
argument_list|)
init|)
block|{
return|return
name|fromInputStream
argument_list|(
name|loader
argument_list|,
name|inputStream
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|exc
parameter_list|)
block|{
throw|throw
name|exc
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
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
literal|"Could not load SOLR configuration"
argument_list|,
name|exc
argument_list|)
throw|;
block|}
block|}
DECL|method|fromString
specifier|public
specifier|static
name|NodeConfig
name|fromString
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|xml
parameter_list|)
block|{
return|return
name|fromInputStream
argument_list|(
name|loader
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|xml
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fromInputStream
specifier|public
specifier|static
name|NodeConfig
name|fromInputStream
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|InputStream
name|is
parameter_list|)
block|{
try|try
block|{
name|byte
index|[]
name|buf
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|is
argument_list|)
decl_stmt|;
try|try
init|(
name|ByteArrayInputStream
name|dup
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|buf
argument_list|)
init|)
block|{
name|Config
name|config
init|=
operator|new
name|Config
argument_list|(
name|loader
argument_list|,
literal|null
argument_list|,
operator|new
name|InputSource
argument_list|(
name|dup
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|fromConfig
argument_list|(
name|config
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SolrException
name|exc
parameter_list|)
block|{
throw|throw
name|exc
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
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
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|fromSolrHome
specifier|public
specifier|static
name|NodeConfig
name|fromSolrHome
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|solrHome
parameter_list|)
block|{
return|return
name|fromFile
argument_list|(
name|loader
argument_list|,
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
name|SOLR_XML_FILE
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fromSolrHome
specifier|public
specifier|static
name|NodeConfig
name|fromSolrHome
parameter_list|(
name|Path
name|solrHome
parameter_list|)
block|{
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|solrHome
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|fromSolrHome
argument_list|(
name|loader
argument_list|,
name|solrHome
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|checkForIllegalConfig
specifier|private
specifier|static
name|void
name|checkForIllegalConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|failIfFound
argument_list|(
name|config
argument_list|,
literal|"solr/@coreLoadThreads"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
name|config
argument_list|,
literal|"solr/@persistent"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
name|config
argument_list|,
literal|"solr/@sharedLib"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
name|config
argument_list|,
literal|"solr/@zkHost"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
name|config
argument_list|,
literal|"solr/cores"
argument_list|)
expr_stmt|;
name|assertSingleInstance
argument_list|(
literal|"solrcloud"
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|assertSingleInstance
argument_list|(
literal|"logging"
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|assertSingleInstance
argument_list|(
literal|"logging/watcher"
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSingleInstance
specifier|private
specifier|static
name|void
name|assertSingleInstance
parameter_list|(
name|String
name|section
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
if|if
condition|(
name|config
operator|.
name|getNodeList
argument_list|(
literal|"/solr/"
operator|+
name|section
argument_list|,
literal|false
argument_list|)
operator|.
name|getLength
argument_list|()
operator|>
literal|1
condition|)
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
literal|"Multiple instances of "
operator|+
name|section
operator|+
literal|" section found in solr.xml"
argument_list|)
throw|;
block|}
DECL|method|failIfFound
specifier|private
specifier|static
name|void
name|failIfFound
parameter_list|(
name|Config
name|config
parameter_list|,
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
literal|"\n. Please upgrade your solr.xml: https://cwiki.apache.org/confluence/display/solr/Format+of+solr.xml"
argument_list|)
throw|;
block|}
block|}
DECL|method|loadProperties
specifier|private
specifier|static
name|Properties
name|loadProperties
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
try|try
block|{
name|Node
name|node
init|=
operator|(
operator|(
name|NodeList
operator|)
name|config
operator|.
name|evaluate
argument_list|(
literal|"solr"
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
operator|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|XPath
name|xpath
init|=
name|config
operator|.
name|getXPath
argument_list|()
decl_stmt|;
name|NodeList
name|props
init|=
operator|(
name|NodeList
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
literal|"property"
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|props
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|prop
init|=
name|props
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|prop
argument_list|,
name|NAME
argument_list|)
argument_list|,
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|prop
argument_list|,
literal|"value"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error parsing solr.xml: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|readNodeListAsNamedList
specifier|private
specifier|static
name|NamedList
argument_list|<
name|Object
argument_list|>
name|readNodeListAsNamedList
parameter_list|(
name|Config
name|config
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|section
parameter_list|)
block|{
name|NodeList
name|nodes
init|=
name|config
operator|.
name|getNodeList
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|checkForDuplicates
argument_list|(
name|section
argument_list|,
name|DOMUtil
operator|.
name|nodesToNamedList
argument_list|(
name|nodes
argument_list|)
argument_list|)
return|;
block|}
DECL|method|checkForDuplicates
specifier|private
specifier|static
name|NamedList
argument_list|<
name|Object
argument_list|>
name|checkForDuplicates
parameter_list|(
name|String
name|section
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|keys
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|nl
control|)
block|{
if|if
condition|(
operator|!
name|keys
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
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
name|section
operator|+
literal|" section of solr.xml contains duplicated '"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|nl
return|;
block|}
DECL|method|parseInt
specifier|private
specifier|static
name|int
name|parseInt
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
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
literal|"Error parsing '"
operator|+
name|field
operator|+
literal|"', value '"
operator|+
name|value
operator|+
literal|"' cannot be parsed as int"
argument_list|)
throw|;
block|}
block|}
DECL|method|fillSolrSection
specifier|private
specifier|static
name|NodeConfig
name|fillSolrSection
parameter_list|(
name|NodeConfig
operator|.
name|NodeConfigBuilder
name|builder
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|nl
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
continue|continue;
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|name
condition|)
block|{
case|case
literal|"adminHandler"
case|:
name|builder
operator|.
name|setCoreAdminHandlerClass
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"collectionsHandler"
case|:
name|builder
operator|.
name|setCollectionsAdminHandlerClass
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"infoHandler"
case|:
name|builder
operator|.
name|setInfoHandlerClass
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"configSetsHandler"
case|:
name|builder
operator|.
name|setConfigSetsHandlerClass
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"coreRootDirectory"
case|:
name|builder
operator|.
name|setCoreRootDirectory
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"managementPath"
case|:
name|builder
operator|.
name|setManagementPath
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"sharedLib"
case|:
name|builder
operator|.
name|setSharedLibDirectory
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"configSetBaseDir"
case|:
name|builder
operator|.
name|setConfigSetBaseDirectory
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"shareSchema"
case|:
name|builder
operator|.
name|setUseSchemaCache
argument_list|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"coreLoadThreads"
case|:
name|builder
operator|.
name|setCoreLoadThreads
argument_list|(
name|parseInt
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"transientCacheSize"
case|:
name|builder
operator|.
name|setTransientCacheSize
argument_list|(
name|parseInt
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
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
literal|"Unknown configuration value in solr.xml: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|loadUpdateConfig
specifier|private
specifier|static
name|UpdateShardHandlerConfig
name|loadUpdateConfig
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
parameter_list|,
name|boolean
name|alwaysDefine
parameter_list|)
block|{
if|if
condition|(
name|nl
operator|==
literal|null
operator|&&
operator|!
name|alwaysDefine
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|nl
operator|==
literal|null
condition|)
return|return
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT
return|;
name|boolean
name|defined
init|=
literal|false
decl_stmt|;
name|int
name|maxUpdateConnections
init|=
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT_MAXUPDATECONNECTIONS
decl_stmt|;
name|int
name|maxUpdateConnectionsPerHost
init|=
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT_MAXUPDATECONNECTIONSPERHOST
decl_stmt|;
name|int
name|distributedSocketTimeout
init|=
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT_DISTRIBUPDATESOTIMEOUT
decl_stmt|;
name|int
name|distributedConnectionTimeout
init|=
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT_DISTRIBUPDATECONNTIMEOUT
decl_stmt|;
name|Object
name|muc
init|=
name|nl
operator|.
name|remove
argument_list|(
literal|"maxUpdateConnections"
argument_list|)
decl_stmt|;
if|if
condition|(
name|muc
operator|!=
literal|null
condition|)
block|{
name|maxUpdateConnections
operator|=
name|parseInt
argument_list|(
literal|"maxUpdateConnections"
argument_list|,
name|muc
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|defined
operator|=
literal|true
expr_stmt|;
block|}
name|Object
name|mucph
init|=
name|nl
operator|.
name|remove
argument_list|(
literal|"maxUpdateConnectionsPerHost"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mucph
operator|!=
literal|null
condition|)
block|{
name|maxUpdateConnectionsPerHost
operator|=
name|parseInt
argument_list|(
literal|"maxUpdateConnectionsPerHost"
argument_list|,
name|mucph
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|defined
operator|=
literal|true
expr_stmt|;
block|}
name|Object
name|dst
init|=
name|nl
operator|.
name|remove
argument_list|(
literal|"distribUpdateSoTimeout"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dst
operator|!=
literal|null
condition|)
block|{
name|distributedSocketTimeout
operator|=
name|parseInt
argument_list|(
literal|"distribUpdateSoTimeout"
argument_list|,
name|dst
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|defined
operator|=
literal|true
expr_stmt|;
block|}
name|Object
name|dct
init|=
name|nl
operator|.
name|remove
argument_list|(
literal|"distribUpdateConnTimeout"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dct
operator|!=
literal|null
condition|)
block|{
name|distributedConnectionTimeout
operator|=
name|parseInt
argument_list|(
literal|"distribUpdateConnTimeout"
argument_list|,
name|dct
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|defined
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|defined
operator|&&
operator|!
name|alwaysDefine
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|UpdateShardHandlerConfig
argument_list|(
name|maxUpdateConnections
argument_list|,
name|maxUpdateConnectionsPerHost
argument_list|,
name|distributedSocketTimeout
argument_list|,
name|distributedConnectionTimeout
argument_list|)
return|;
block|}
DECL|method|removeValue
specifier|private
specifier|static
name|String
name|removeValue
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|Object
name|value
init|=
name|nl
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|required
specifier|private
specifier|static
name|String
name|required
parameter_list|(
name|String
name|section
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
return|return
name|value
return|;
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
name|section
operator|+
literal|" section missing required entry '"
operator|+
name|key
operator|+
literal|"'"
argument_list|)
throw|;
block|}
DECL|method|fillSolrCloudSection
specifier|private
specifier|static
name|CloudConfig
name|fillSolrCloudSection
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
parameter_list|)
block|{
name|String
name|hostName
init|=
name|required
argument_list|(
literal|"solrcloud"
argument_list|,
literal|"host"
argument_list|,
name|removeValue
argument_list|(
name|nl
argument_list|,
literal|"host"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|hostPort
init|=
name|parseInt
argument_list|(
literal|"hostPort"
argument_list|,
name|required
argument_list|(
literal|"solrcloud"
argument_list|,
literal|"hostPort"
argument_list|,
name|removeValue
argument_list|(
name|nl
argument_list|,
literal|"hostPort"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|hostContext
init|=
name|required
argument_list|(
literal|"solrcloud"
argument_list|,
literal|"hostContext"
argument_list|,
name|removeValue
argument_list|(
name|nl
argument_list|,
literal|"hostContext"
argument_list|)
argument_list|)
decl_stmt|;
name|CloudConfig
operator|.
name|CloudConfigBuilder
name|builder
init|=
operator|new
name|CloudConfig
operator|.
name|CloudConfigBuilder
argument_list|(
name|hostName
argument_list|,
name|hostPort
argument_list|,
name|hostContext
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|nl
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
continue|continue;
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|name
condition|)
block|{
case|case
literal|"leaderVoteWait"
case|:
name|builder
operator|.
name|setLeaderVoteWait
argument_list|(
name|parseInt
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"leaderConflictResolveWait"
case|:
name|builder
operator|.
name|setLeaderConflictResolveWait
argument_list|(
name|parseInt
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"zkClientTimeout"
case|:
name|builder
operator|.
name|setZkClientTimeout
argument_list|(
name|parseInt
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"autoReplicaFailoverBadNodeExpiration"
case|:
name|builder
operator|.
name|setAutoReplicaFailoverBadNodeExpiration
argument_list|(
name|parseInt
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"autoReplicaFailoverWaitAfterExpiration"
case|:
name|builder
operator|.
name|setAutoReplicaFailoverWaitAfterExpiration
argument_list|(
name|parseInt
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"autoReplicaFailoverWorkLoopDelay"
case|:
name|builder
operator|.
name|setAutoReplicaFailoverWorkLoopDelay
argument_list|(
name|parseInt
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"zkHost"
case|:
name|builder
operator|.
name|setZkHost
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"genericCoreNodeNames"
case|:
name|builder
operator|.
name|setUseGenericCoreNames
argument_list|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"zkACLProvider"
case|:
name|builder
operator|.
name|setZkACLProviderClass
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"zkCredentialsProvider"
case|:
name|builder
operator|.
name|setZkCredentialsProviderClass
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
default|default:
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
literal|"Unknown configuration parameter in<solrcloud> section of solr.xml: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|loadLogWatcherConfig
specifier|private
specifier|static
name|LogWatcherConfig
name|loadLogWatcherConfig
parameter_list|(
name|Config
name|config
parameter_list|,
name|String
name|loggingPath
parameter_list|,
name|String
name|watcherPath
parameter_list|)
block|{
name|String
name|loggingClass
init|=
literal|null
decl_stmt|;
name|boolean
name|enabled
init|=
literal|true
decl_stmt|;
name|int
name|watcherQueueSize
init|=
literal|50
decl_stmt|;
name|String
name|watcherThreshold
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|readNodeListAsNamedList
argument_list|(
name|config
argument_list|,
name|loggingPath
argument_list|,
literal|"<logging>"
argument_list|)
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|name
condition|)
block|{
case|case
literal|"class"
case|:
name|loggingClass
operator|=
name|value
expr_stmt|;
break|break;
case|case
literal|"enabled"
case|:
name|enabled
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
default|default:
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
literal|"Unknown value in logwatcher config: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|readNodeListAsNamedList
argument_list|(
name|config
argument_list|,
name|watcherPath
argument_list|,
literal|"<watcher>"
argument_list|)
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|name
condition|)
block|{
case|case
literal|"size"
case|:
name|watcherQueueSize
operator|=
name|parseInt
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"threshold"
case|:
name|watcherThreshold
operator|=
name|value
expr_stmt|;
break|break;
default|default:
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
literal|"Unknown value in logwatcher config: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|LogWatcherConfig
argument_list|(
name|enabled
argument_list|,
name|loggingClass
argument_list|,
name|watcherThreshold
argument_list|,
name|watcherQueueSize
argument_list|)
return|;
block|}
DECL|method|getShardHandlerFactoryPluginInfo
specifier|private
specifier|static
name|PluginInfo
name|getShardHandlerFactoryPluginInfo
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|Node
name|node
init|=
name|config
operator|.
name|getNode
argument_list|(
literal|"solr/shardHandlerFactory"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|(
name|node
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|PluginInfo
argument_list|(
name|node
argument_list|,
literal|"shardHandlerFactory"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class
end_unit
