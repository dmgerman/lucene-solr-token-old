begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|params
operator|.
name|CommonParams
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
name|common
operator|.
name|util
operator|.
name|XML
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
name|SolrCore
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
name|CoreContainer
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
name|CoreDescriptor
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
name|SolrResourceLoader
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
name|handler
operator|.
name|XmlUpdateRequestHandler
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|QueryResponseWriter
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
name|response
operator|.
name|SolrQueryResponse
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
name|schema
operator|.
name|IndexSchema
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
name|Document
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
operator|.
name|NamedListEntry
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
name|DocumentBuilder
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
name|DocumentBuilderFactory
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
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathFactory
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|Arrays
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
begin_comment
comment|/**  * This class provides a simple harness that may be useful when  * writing testcases.  *  *<p>  * This class lives in the main source tree (and not in the test source  * tree), so that it will be included with even the most minimal solr  * distribution, in order to encourage plugin writers to create unit   * tests for their plugins.  *  * @version $Id:$  */
end_comment
begin_class
DECL|class|TestHarness
specifier|public
class|class
name|TestHarness
block|{
DECL|field|container
specifier|protected
name|CoreContainer
name|container
decl_stmt|;
DECL|field|core
specifier|private
name|SolrCore
name|core
decl_stmt|;
DECL|field|xpath
specifier|private
name|XPath
name|xpath
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newXPath
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|DocumentBuilder
name|builder
decl_stmt|;
DECL|field|updater
specifier|public
name|XmlUpdateRequestHandler
name|updater
decl_stmt|;
DECL|method|createConfig
specifier|public
specifier|static
name|SolrConfig
name|createConfig
parameter_list|(
name|String
name|confFile
parameter_list|)
block|{
comment|// set some system properties for use by tests
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|new
name|SolrConfig
argument_list|(
name|confFile
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|xany
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|xany
argument_list|)
throw|;
block|}
block|}
comment|/**    * Assumes "solrconfig.xml" is the config file to use, and    * "schema.xml" is the schema path to use.    *    * @param dataDirectory path for index data, will not be cleaned up    */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|String
name|dataDirectory
parameter_list|)
block|{
name|this
argument_list|(
name|dataDirectory
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assumes "solrconfig.xml" is the config file to use.    *    * @param dataDirectory path for index data, will not be cleaned up    * @param schemaFile path of schema file    */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|String
name|dataDirectory
parameter_list|,
name|String
name|schemaFile
parameter_list|)
block|{
name|this
argument_list|(
name|dataDirectory
argument_list|,
literal|"solrconfig.xml"
argument_list|,
name|schemaFile
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param dataDirectory path for index data, will not be cleaned up    * @param configFile solrconfig filename    * @param schemaFile schema filename    */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|String
name|dataDirectory
parameter_list|,
name|String
name|configFile
parameter_list|,
name|String
name|schemaFile
parameter_list|)
block|{
name|this
argument_list|(
name|dataDirectory
argument_list|,
name|createConfig
argument_list|(
name|configFile
argument_list|)
argument_list|,
name|schemaFile
argument_list|)
expr_stmt|;
block|}
comment|/**     * @param dataDirectory path for index data, will not be cleaned up     * @param solrConfig solronfig instance     * @param schemaFile schema filename     */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|String
name|dataDirectory
parameter_list|,
name|SolrConfig
name|solrConfig
parameter_list|,
name|String
name|schemaFile
parameter_list|)
block|{
name|this
argument_list|(
name|dataDirectory
argument_list|,
name|solrConfig
argument_list|,
operator|new
name|IndexSchema
argument_list|(
name|solrConfig
argument_list|,
name|schemaFile
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * @param dataDirectory path for index data, will not be cleaned up     * @param solrConfig solrconfig instance     * @param indexSchema schema instance     */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|String
name|dataDirectory
parameter_list|,
name|SolrConfig
name|solrConfig
parameter_list|,
name|IndexSchema
name|indexSchema
parameter_list|)
block|{
name|this
argument_list|(
literal|""
argument_list|,
operator|new
name|Initializer
argument_list|(
literal|""
argument_list|,
name|dataDirectory
argument_list|,
name|solrConfig
argument_list|,
name|indexSchema
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|String
name|coreName
parameter_list|,
name|CoreContainer
operator|.
name|Initializer
name|init
parameter_list|)
block|{
try|try
block|{
name|container
operator|=
name|init
operator|.
name|initialize
argument_list|()
expr_stmt|;
if|if
condition|(
name|coreName
operator|==
literal|null
condition|)
name|coreName
operator|=
literal|""
expr_stmt|;
comment|// get the core& decrease its refcount:
comment|// the container holds the core for the harness lifetime
name|core
operator|=
name|container
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
expr_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
name|builder
operator|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
expr_stmt|;
name|updater
operator|=
operator|new
name|XmlUpdateRequestHandler
argument_list|()
expr_stmt|;
name|updater
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|// Creates a container based on infos needed to create one core
DECL|class|Initializer
specifier|static
class|class
name|Initializer
extends|extends
name|CoreContainer
operator|.
name|Initializer
block|{
DECL|field|coreName
name|String
name|coreName
decl_stmt|;
DECL|field|dataDirectory
name|String
name|dataDirectory
decl_stmt|;
DECL|field|solrConfig
name|SolrConfig
name|solrConfig
decl_stmt|;
DECL|field|indexSchema
name|IndexSchema
name|indexSchema
decl_stmt|;
DECL|method|Initializer
specifier|public
name|Initializer
parameter_list|(
name|String
name|coreName
parameter_list|,
name|String
name|dataDirectory
parameter_list|,
name|SolrConfig
name|solrConfig
parameter_list|,
name|IndexSchema
name|indexSchema
parameter_list|)
block|{
if|if
condition|(
name|coreName
operator|==
literal|null
condition|)
name|coreName
operator|=
literal|""
expr_stmt|;
name|this
operator|.
name|coreName
operator|=
name|coreName
expr_stmt|;
name|this
operator|.
name|dataDirectory
operator|=
name|dataDirectory
expr_stmt|;
name|this
operator|.
name|solrConfig
operator|=
name|solrConfig
expr_stmt|;
name|this
operator|.
name|indexSchema
operator|=
name|indexSchema
expr_stmt|;
block|}
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
name|coreName
return|;
block|}
annotation|@
name|Override
DECL|method|initialize
specifier|public
name|CoreContainer
name|initialize
parameter_list|()
block|{
name|CoreContainer
name|container
init|=
operator|new
name|CoreContainer
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
name|SolrResourceLoader
operator|.
name|locateSolrHome
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|CoreDescriptor
name|dcore
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|container
argument_list|,
name|coreName
argument_list|,
name|solrConfig
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
decl_stmt|;
name|dcore
operator|.
name|setConfigName
argument_list|(
name|solrConfig
operator|.
name|getResourceName
argument_list|()
argument_list|)
expr_stmt|;
name|dcore
operator|.
name|setSchemaName
argument_list|(
name|indexSchema
operator|.
name|getResourceName
argument_list|()
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
operator|new
name|SolrCore
argument_list|(
literal|null
argument_list|,
name|dataDirectory
argument_list|,
name|solrConfig
argument_list|,
name|indexSchema
argument_list|,
name|dcore
argument_list|)
decl_stmt|;
name|container
operator|.
name|register
argument_list|(
name|coreName
argument_list|,
name|core
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
block|}
DECL|method|getCoreContainer
specifier|public
name|CoreContainer
name|getCoreContainer
parameter_list|()
block|{
return|return
name|container
return|;
block|}
DECL|method|getCore
specifier|public
name|SolrCore
name|getCore
parameter_list|()
block|{
return|return
name|core
return|;
block|}
comment|/**    * Processes an "update" (add, commit or optimize) and    * returns the response as a String.    *     * @deprecated The better approach is to instantiate an Updatehandler directly    *    * @param xml The XML of the update    * @return The XML response to the update    */
annotation|@
name|Deprecated
DECL|method|update
specifier|public
name|String
name|update
parameter_list|(
name|String
name|xml
parameter_list|)
block|{
name|StringReader
name|req
init|=
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|(
literal|32000
argument_list|)
decl_stmt|;
name|updater
operator|.
name|doLegacyUpdate
argument_list|(
name|req
argument_list|,
name|writer
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Validates that an "update" (add, commit or optimize) results in success.    *    * :TODO: currently only deals with one add/doc at a time, this will need changed if/when SOLR-2 is resolved    *     * @param xml The XML of the update    * @return null if successful, otherwise the XML response to the update    */
DECL|method|validateUpdate
specifier|public
name|String
name|validateUpdate
parameter_list|(
name|String
name|xml
parameter_list|)
throws|throws
name|SAXException
block|{
return|return
name|checkUpdateStatus
argument_list|(
name|xml
argument_list|,
literal|"0"
argument_list|)
return|;
block|}
comment|/**    * Validates that an "update" (add, commit or optimize) results in success.    *    * :TODO: currently only deals with one add/doc at a time, this will need changed if/when SOLR-2 is resolved    *     * @param xml The XML of the update    * @return null if successful, otherwise the XML response to the update    */
DECL|method|validateErrorUpdate
specifier|public
name|String
name|validateErrorUpdate
parameter_list|(
name|String
name|xml
parameter_list|)
throws|throws
name|SAXException
block|{
return|return
name|checkUpdateStatus
argument_list|(
name|xml
argument_list|,
literal|"1"
argument_list|)
return|;
block|}
comment|/**    * Validates that an "update" (add, commit or optimize) results in success.    *    * :TODO: currently only deals with one add/doc at a time, this will need changed if/when SOLR-2 is resolved    *     * @param xml The XML of the update    * @return null if successful, otherwise the XML response to the update    */
DECL|method|checkUpdateStatus
specifier|public
name|String
name|checkUpdateStatus
parameter_list|(
name|String
name|xml
parameter_list|,
name|String
name|code
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
name|String
name|res
init|=
name|update
argument_list|(
name|xml
argument_list|)
decl_stmt|;
name|String
name|valid
init|=
name|validateXPath
argument_list|(
name|res
argument_list|,
literal|"//result[@status="
operator|+
name|code
operator|+
literal|"]"
argument_list|)
decl_stmt|;
return|return
operator|(
literal|null
operator|==
name|valid
operator|)
condition|?
literal|null
else|:
name|res
return|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"?!? static xpath has bug?"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Validates that an add of a single document results in success.    *    * @param fieldsAndValues Odds are field names, Evens are values    * @return null if successful, otherwise the XML response to the update    * @see #appendSimpleDoc    */
DECL|method|validateAddDoc
specifier|public
name|String
name|validateAddDoc
parameter_list|(
name|String
modifier|...
name|fieldsAndValues
parameter_list|)
throws|throws
name|XPathExpressionException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<add>"
argument_list|)
expr_stmt|;
name|appendSimpleDoc
argument_list|(
name|buf
argument_list|,
name|fieldsAndValues
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"</add>"
argument_list|)
expr_stmt|;
name|String
name|res
init|=
name|update
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|valid
init|=
name|validateXPath
argument_list|(
name|res
argument_list|,
literal|"//result[@status=0]"
argument_list|)
decl_stmt|;
return|return
operator|(
literal|null
operator|==
name|valid
operator|)
condition|?
literal|null
else|:
name|res
return|;
block|}
comment|/**    * Validates a "query" response against an array of XPath test strings    *    * @param req the Query to process    * @return null if all good, otherwise the first test that fails.    * @exception Exception any exception in the response.    * @exception IOException if there is a problem writing the XML    * @see LocalSolrQueryRequest    */
DECL|method|validateQuery
specifier|public
name|String
name|validateQuery
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|IOException
throws|,
name|Exception
block|{
name|String
name|res
init|=
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|validateXPath
argument_list|(
name|res
argument_list|,
name|tests
argument_list|)
return|;
block|}
comment|/**    * Processes a "query" using a user constructed SolrQueryRequest    *    * @param req the Query to process, will be closed.    * @return The XML response to the query    * @exception Exception any exception in the response.    * @exception IOException if there is a problem writing the XML    * @see LocalSolrQueryRequest    */
DECL|method|query
specifier|public
name|String
name|query
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|IOException
throws|,
name|Exception
block|{
return|return
name|query
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
argument_list|,
name|req
argument_list|)
return|;
block|}
comment|/**    * Processes a "query" using a user constructed SolrQueryRequest    *    * @param handler the name of the request handler to process the request    * @param req the Query to process, will be closed.    * @return The XML response to the query    * @exception Exception any exception in the response.    * @exception IOException if there is a problem writing the XML    * @see LocalSolrQueryRequest    */
DECL|method|query
specifier|public
name|String
name|query
parameter_list|(
name|String
name|handler
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|IOException
throws|,
name|Exception
block|{
name|SolrQueryResponse
name|rsp
init|=
name|queryAndResponse
argument_list|(
name|handler
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|(
literal|32000
argument_list|)
decl_stmt|;
name|QueryResponseWriter
name|responseWriter
init|=
name|core
operator|.
name|getQueryResponseWriter
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|responseWriter
operator|.
name|write
argument_list|(
name|sw
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|queryAndResponse
specifier|public
name|SolrQueryResponse
name|queryAndResponse
parameter_list|(
name|String
name|handler
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|core
operator|.
name|getRequestHandler
argument_list|(
name|handler
argument_list|)
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
name|rsp
operator|.
name|getException
argument_list|()
throw|;
block|}
return|return
name|rsp
return|;
block|}
comment|/**    * A helper method which valides a String against an array of XPath test    * strings.    *    * @param xml The xml String to validate    * @param tests Array of XPath strings to test (in boolean mode) on the xml    * @return null if all good, otherwise the first test that fails.    */
DECL|method|validateXPath
specifier|public
name|String
name|validateXPath
parameter_list|(
name|String
name|xml
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|XPathExpressionException
throws|,
name|SAXException
block|{
if|if
condition|(
name|tests
operator|==
literal|null
operator|||
name|tests
operator|.
name|length
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|Document
name|document
init|=
literal|null
decl_stmt|;
try|try
block|{
name|document
operator|=
name|builder
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|xml
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Totally weird UTF-8 exception"
argument_list|,
name|e1
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e2
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Totally weird io exception"
argument_list|,
name|e2
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|xp
range|:
name|tests
control|)
block|{
name|xp
operator|=
name|xp
operator|.
name|trim
argument_list|()
expr_stmt|;
name|Boolean
name|bool
init|=
operator|(
name|Boolean
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
name|xp
argument_list|,
name|document
argument_list|,
name|XPathConstants
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|bool
condition|)
block|{
return|return
name|xp
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Shuts down and frees any resources    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SolrCore
name|c
range|:
name|container
operator|.
name|getCores
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getOpenCount
argument_list|()
operator|>
literal|1
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SolrCore.getOpenCount()=="
operator|+
name|core
operator|.
name|getOpenCount
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|container
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|container
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * A helper that adds an xml&lt;doc&gt; containing all of the    * fields and values specified (odds are fields, evens are values)    * to a StringBuilder    */
DECL|method|appendSimpleDoc
specifier|public
name|void
name|appendSimpleDoc
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
modifier|...
name|fieldsAndValues
parameter_list|)
throws|throws
name|IOException
block|{
name|buf
operator|.
name|append
argument_list|(
name|makeSimpleDoc
argument_list|(
name|fieldsAndValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * A helper that adds an xml&lt;doc&gt; containing all of the    * fields and values specified (odds are fields, evens are values)    * to a StringBuffer.    * @deprecated see {@link #appendSimpleDoc(StringBuilder, String...)}    */
DECL|method|appendSimpleDoc
specifier|public
name|void
name|appendSimpleDoc
parameter_list|(
name|StringBuffer
name|buf
parameter_list|,
name|String
modifier|...
name|fieldsAndValues
parameter_list|)
throws|throws
name|IOException
block|{
name|buf
operator|.
name|append
argument_list|(
name|makeSimpleDoc
argument_list|(
name|fieldsAndValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * A helper that creates an xml&lt;doc&gt; containing all of the    * fields and values specified    *    * @param fieldsAndValues 0 and Even numbered args are fields names odds are field values.    */
DECL|method|makeSimpleDoc
specifier|public
specifier|static
name|StringBuffer
name|makeSimpleDoc
parameter_list|(
name|String
modifier|...
name|fieldsAndValues
parameter_list|)
block|{
try|try
block|{
name|StringWriter
name|w
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|w
operator|.
name|append
argument_list|(
literal|"<doc>"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldsAndValues
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|XML
operator|.
name|writeXML
argument_list|(
name|w
argument_list|,
literal|"field"
argument_list|,
name|fieldsAndValues
index|[
name|i
operator|+
literal|1
index|]
argument_list|,
literal|"name"
argument_list|,
name|fieldsAndValues
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|append
argument_list|(
literal|"</doc>"
argument_list|)
expr_stmt|;
return|return
name|w
operator|.
name|getBuffer
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"this should never happen with a StringWriter"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Generates a delete by query xml string    * @param q Query that has not already been xml escaped    */
DECL|method|deleteByQuery
specifier|public
specifier|static
name|String
name|deleteByQuery
parameter_list|(
name|String
name|q
parameter_list|)
block|{
return|return
name|delete
argument_list|(
literal|"query"
argument_list|,
name|q
argument_list|)
return|;
block|}
comment|/**    * Generates a delete by id xml string    * @param id ID that has not already been xml escaped    */
DECL|method|deleteById
specifier|public
specifier|static
name|String
name|deleteById
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|delete
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
return|;
block|}
comment|/**    * Generates a delete xml string    * @param val text that has not already been xml escaped    */
DECL|method|delete
specifier|private
specifier|static
name|String
name|delete
parameter_list|(
name|String
name|deltype
parameter_list|,
name|String
name|val
parameter_list|)
block|{
try|try
block|{
name|StringWriter
name|r
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|r
operator|.
name|write
argument_list|(
literal|"<delete>"
argument_list|)
expr_stmt|;
name|XML
operator|.
name|writeXML
argument_list|(
name|r
argument_list|,
name|deltype
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|r
operator|.
name|write
argument_list|(
literal|"</delete>"
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"this should never happen with a StringWriter"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Helper that returns an&lt;optimize&gt; String with    * optional key/val pairs.    *    * @param args 0 and Even numbered args are params, Odd numbered args are values.    */
DECL|method|optimize
specifier|public
specifier|static
name|String
name|optimize
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|simpleTag
argument_list|(
literal|"optimize"
argument_list|,
name|args
argument_list|)
return|;
block|}
DECL|method|simpleTag
specifier|private
specifier|static
name|String
name|simpleTag
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
try|try
block|{
name|StringWriter
name|r
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
comment|// this is annoying
if|if
condition|(
literal|null
operator|==
name|args
operator|||
literal|0
operator|==
name|args
operator|.
name|length
condition|)
block|{
name|XML
operator|.
name|writeXML
argument_list|(
name|r
argument_list|,
name|tag
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XML
operator|.
name|writeXML
argument_list|(
name|r
argument_list|,
name|tag
argument_list|,
literal|null
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|args
argument_list|)
expr_stmt|;
block|}
return|return
name|r
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"this should never happen with a StringWriter"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Helper that returns an&lt;commit&gt; String with    * optional key/val pairs.    *    * @param args 0 and Even numbered args are params, Odd numbered args are values.    */
DECL|method|commit
specifier|public
specifier|static
name|String
name|commit
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|simpleTag
argument_list|(
literal|"commit"
argument_list|,
name|args
argument_list|)
return|;
block|}
DECL|method|getRequestFactory
specifier|public
name|LocalRequestFactory
name|getRequestFactory
parameter_list|(
name|String
name|qtype
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|LocalRequestFactory
name|f
init|=
operator|new
name|LocalRequestFactory
argument_list|()
decl_stmt|;
name|f
operator|.
name|qtype
operator|=
name|qtype
expr_stmt|;
name|f
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|f
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/**    * 0 and Even numbered args are keys, Odd numbered args are values.    */
DECL|method|getRequestFactory
specifier|public
name|LocalRequestFactory
name|getRequestFactory
parameter_list|(
name|String
name|qtype
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
name|LocalRequestFactory
name|f
init|=
name|getRequestFactory
argument_list|(
name|qtype
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
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
name|args
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|f
operator|.
name|args
operator|.
name|put
argument_list|(
name|args
index|[
name|i
index|]
argument_list|,
name|args
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|f
return|;
block|}
DECL|method|getRequestFactory
specifier|public
name|LocalRequestFactory
name|getRequestFactory
parameter_list|(
name|String
name|qtype
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|LocalRequestFactory
name|f
init|=
name|getRequestFactory
argument_list|(
name|qtype
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|f
operator|.
name|args
operator|.
name|putAll
argument_list|(
name|args
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/**    * A Factory that generates LocalSolrQueryRequest objects using a    * specified set of default options.    */
DECL|class|LocalRequestFactory
specifier|public
class|class
name|LocalRequestFactory
block|{
DECL|field|qtype
specifier|public
name|String
name|qtype
init|=
literal|"standard"
decl_stmt|;
DECL|field|start
specifier|public
name|int
name|start
init|=
literal|0
decl_stmt|;
DECL|field|limit
specifier|public
name|int
name|limit
init|=
literal|1000
decl_stmt|;
DECL|field|args
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
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
DECL|method|LocalRequestFactory
specifier|public
name|LocalRequestFactory
parameter_list|()
block|{     }
comment|/**      * Creates a LocalSolrQueryRequest based on variable args; for      * historical reasons, this method has some peculiar behavior:      *<ul>      *<li>If there is a single arg, then it is treated as the "q"      *       param, and the LocalSolrQueryRequest consists of that query      *       string along with "qt", "start", and "rows" params (based      *       on the qtype, start, and limit properties of this factory)      *       along with any other default "args" set on this factory.      *</li>      *<li>If there are multiple args, then there must be an even number      *       of them, and each pair of args is used as a key=value param in      *       the LocalSolrQueryRequest.<b>NOTE: In this usage, the "qtype",      *       "start", "limit", and "args" properties of this factory are      *       ignored.</b>      *</li>      *</ul>      */
DECL|method|makeRequest
specifier|public
name|LocalSolrQueryRequest
name|makeRequest
parameter_list|(
name|String
modifier|...
name|q
parameter_list|)
block|{
if|if
condition|(
name|q
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|TestHarness
operator|.
name|this
operator|.
name|getCore
argument_list|()
argument_list|,
name|q
index|[
literal|0
index|]
argument_list|,
name|qtype
argument_list|,
name|start
argument_list|,
name|limit
argument_list|,
name|args
argument_list|)
return|;
block|}
if|if
condition|(
name|q
operator|.
name|length
operator|%
literal|2
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The length of the string array (query arguments) needs to be even"
argument_list|)
throw|;
block|}
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
index|[]
name|entries
init|=
operator|new
name|NamedListEntry
index|[
name|q
operator|.
name|length
operator|/
literal|2
index|]
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
name|q
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|entries
index|[
name|i
operator|/
literal|2
index|]
operator|=
operator|new
name|NamedListEntry
argument_list|<
name|String
argument_list|>
argument_list|(
name|q
index|[
name|i
index|]
argument_list|,
name|q
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|TestHarness
operator|.
name|this
operator|.
name|getCore
argument_list|()
argument_list|,
operator|new
name|NamedList
argument_list|(
name|entries
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
