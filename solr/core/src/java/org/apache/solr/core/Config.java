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
name|lucene
operator|.
name|util
operator|.
name|Version
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
name|common
operator|.
name|util
operator|.
name|SystemIdResolver
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
name|XMLErrorLogger
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
name|Document
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
name|xml
operator|.
name|sax
operator|.
name|InputSource
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
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
name|Arrays
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
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|Config
specifier|public
class|class
name|Config
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Config
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|xmllog
specifier|private
specifier|static
specifier|final
name|XMLErrorLogger
name|xmllog
init|=
operator|new
name|XMLErrorLogger
argument_list|(
name|log
argument_list|)
decl_stmt|;
DECL|field|xpathFactory
specifier|static
specifier|final
name|XPathFactory
name|xpathFactory
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
DECL|field|doc
specifier|private
specifier|final
name|Document
name|doc
decl_stmt|;
DECL|field|prefix
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|loader
specifier|private
specifier|final
name|SolrResourceLoader
name|loader
decl_stmt|;
comment|/**    * Builds a config from a resource name with no xpath prefix.    * @param loader    * @param name    * @throws javax.xml.parsers.ParserConfigurationException    * @throws java.io.IOException    * @throws org.xml.sax.SAXException    */
DECL|method|Config
specifier|public
name|Config
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|this
argument_list|(
name|loader
argument_list|,
name|name
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|Config
specifier|public
name|Config
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|name
parameter_list|,
name|InputSource
name|is
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|this
argument_list|(
name|loader
argument_list|,
name|name
argument_list|,
name|is
argument_list|,
name|prefix
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds a config:    *<p>    * Note that the 'name' parameter is used to obtain a valid input stream if no valid one is provided through 'is'.    * If no valid stream is provided, a valid SolrResourceLoader instance should be provided through 'loader' so    * the resource can be opened (@see SolrResourceLoader#openResource); if no SolrResourceLoader instance is provided, a default one    * will be created.    *</p>    *<p>    * Consider passing a non-null 'name' parameter in all use-cases since it is used for logging& exception reporting.    *</p>    * @param loader the resource loader used to obtain an input stream if 'is' is null    * @param name the resource name used if the input stream 'is' is null    * @param is the resource as a SAX InputSource    * @param prefix an optional prefix that will be preprended to all non-absolute xpath expressions    * @throws javax.xml.parsers.ParserConfigurationException    * @throws java.io.IOException    * @throws org.xml.sax.SAXException    */
DECL|method|Config
specifier|public
name|Config
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|name
parameter_list|,
name|InputSource
name|is
parameter_list|,
name|String
name|prefix
parameter_list|,
name|boolean
name|subProps
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
name|loader
operator|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
operator|(
name|prefix
operator|!=
literal|null
operator|&&
operator|!
name|prefix
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|)
condition|?
name|prefix
operator|+
literal|'/'
else|:
name|prefix
expr_stmt|;
try|try
block|{
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|is
operator|=
operator|new
name|InputSource
argument_list|(
name|loader
operator|.
name|openConfig
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|.
name|setSystemId
argument_list|(
name|SystemIdResolver
operator|.
name|createSystemIdFromResourceName
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// only enable xinclude, if a SystemId is available
if|if
condition|(
name|is
operator|.
name|getSystemId
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|dbf
operator|.
name|setXIncludeAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dbf
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|name
operator|+
literal|" XML parser doesn't support XInclude option"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|DocumentBuilder
name|db
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|db
operator|.
name|setEntityResolver
argument_list|(
operator|new
name|SystemIdResolver
argument_list|(
name|loader
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|setErrorHandler
argument_list|(
name|xmllog
argument_list|)
expr_stmt|;
try|try
block|{
name|doc
operator|=
name|db
operator|.
name|parse
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// some XML parsers are broken and don't close the byte stream (but they should according to spec)
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
operator|.
name|getByteStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|subProps
condition|)
block|{
name|DOMUtil
operator|.
name|substituteProperties
argument_list|(
name|doc
argument_list|,
name|loader
operator|.
name|getCoreProperties
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Exception during parsing file: "
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Exception during parsing file: "
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error in "
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|Config
specifier|public
name|Config
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|name
parameter_list|,
name|Document
name|doc
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
comment|/**    * @since solr 1.3    */
DECL|method|getResourceLoader
specifier|public
name|SolrResourceLoader
name|getResourceLoader
parameter_list|()
block|{
return|return
name|loader
return|;
block|}
comment|/**    * @since solr 1.3    */
DECL|method|getResourceName
specifier|public
name|String
name|getResourceName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getDocument
specifier|public
name|Document
name|getDocument
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|getXPath
specifier|public
name|XPath
name|getXPath
parameter_list|()
block|{
return|return
name|xpathFactory
operator|.
name|newXPath
argument_list|()
return|;
block|}
DECL|method|normalize
specifier|private
name|String
name|normalize
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|(
name|prefix
operator|==
literal|null
operator|||
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
operator|)
condition|?
name|path
else|:
name|prefix
operator|+
name|path
return|;
block|}
DECL|method|substituteProperties
specifier|public
name|void
name|substituteProperties
parameter_list|()
block|{
name|DOMUtil
operator|.
name|substituteProperties
argument_list|(
name|doc
argument_list|,
name|loader
operator|.
name|getCoreProperties
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|evaluate
specifier|public
name|Object
name|evaluate
parameter_list|(
name|String
name|path
parameter_list|,
name|QName
name|type
parameter_list|)
block|{
name|XPath
name|xpath
init|=
name|xpathFactory
operator|.
name|newXPath
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|xstr
init|=
name|normalize
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// TODO: instead of prepending /prefix/, we could do the search rooted at /prefix...
name|Object
name|o
init|=
name|xpath
operator|.
name|evaluate
argument_list|(
name|xstr
argument_list|,
name|doc
argument_list|,
name|type
argument_list|)
decl_stmt|;
return|return
name|o
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
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error in xpath:"
operator|+
name|path
operator|+
literal|" for "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getNode
specifier|public
name|Node
name|getNode
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|errIfMissing
parameter_list|)
block|{
name|XPath
name|xpath
init|=
name|xpathFactory
operator|.
name|newXPath
argument_list|()
decl_stmt|;
name|Node
name|nd
init|=
literal|null
decl_stmt|;
name|String
name|xstr
init|=
name|normalize
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
name|nd
operator|=
operator|(
name|Node
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
name|xstr
argument_list|,
name|doc
argument_list|,
name|XPathConstants
operator|.
name|NODE
argument_list|)
expr_stmt|;
if|if
condition|(
name|nd
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|errIfMissing
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|name
operator|+
literal|" missing "
operator|+
name|path
argument_list|)
throw|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
name|name
operator|+
literal|" missing optional "
operator|+
name|path
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
name|log
operator|.
name|trace
argument_list|(
name|name
operator|+
literal|":"
operator|+
name|path
operator|+
literal|"="
operator|+
name|nd
argument_list|)
expr_stmt|;
return|return
name|nd
return|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error in xpath"
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
literal|"Error in xpath:"
operator|+
name|xstr
operator|+
literal|" for "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|e
operator|)
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error in xpath"
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
literal|"Error in xpath:"
operator|+
name|xstr
operator|+
literal|" for "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getVal
specifier|public
name|String
name|getVal
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|errIfMissing
parameter_list|)
block|{
name|Node
name|nd
init|=
name|getNode
argument_list|(
name|path
argument_list|,
name|errIfMissing
argument_list|)
decl_stmt|;
if|if
condition|(
name|nd
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|txt
init|=
name|DOMUtil
operator|.
name|getText
argument_list|(
name|nd
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|name
operator|+
literal|' '
operator|+
name|path
operator|+
literal|'='
operator|+
name|txt
argument_list|)
expr_stmt|;
return|return
name|txt
return|;
comment|/******     short typ = nd.getNodeType();     if (typ==Node.ATTRIBUTE_NODE || typ==Node.TEXT_NODE) {       return nd.getNodeValue();     }     return nd.getTextContent();     ******/
block|}
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|getVal
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|String
name|val
init|=
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
operator|==
literal|null
operator|||
name|val
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|def
return|;
block|}
return|return
name|val
return|;
block|}
DECL|method|getInt
specifier|public
name|int
name|getInt
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|getVal
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getInt
specifier|public
name|int
name|getInt
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|getVal
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|val
operator|!=
literal|null
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
else|:
name|def
return|;
block|}
DECL|method|getBool
specifier|public
name|boolean
name|getBool
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|getVal
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getBool
specifier|public
name|boolean
name|getBool
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|getVal
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|val
operator|!=
literal|null
condition|?
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|val
argument_list|)
else|:
name|def
return|;
block|}
DECL|method|getFloat
specifier|public
name|float
name|getFloat
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|getVal
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getFloat
specifier|public
name|float
name|getFloat
parameter_list|(
name|String
name|path
parameter_list|,
name|float
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|getVal
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|val
operator|!=
literal|null
condition|?
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
else|:
name|def
return|;
block|}
DECL|method|getDouble
specifier|public
name|double
name|getDouble
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|getVal
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getDouble
specifier|public
name|double
name|getDouble
parameter_list|(
name|String
name|path
parameter_list|,
name|double
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|getVal
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|val
operator|!=
literal|null
condition|?
name|Double
operator|.
name|parseDouble
argument_list|(
name|val
argument_list|)
else|:
name|def
return|;
block|}
DECL|method|getLuceneVersion
specifier|public
name|Version
name|getLuceneVersion
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|parseLuceneVersionString
argument_list|(
name|getVal
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getLuceneVersion
specifier|public
name|Version
name|getLuceneVersion
parameter_list|(
name|String
name|path
parameter_list|,
name|Version
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|getVal
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|val
operator|!=
literal|null
condition|?
name|parseLuceneVersionString
argument_list|(
name|val
argument_list|)
else|:
name|def
return|;
block|}
DECL|field|versionWarningAlreadyLogged
specifier|private
specifier|static
specifier|final
name|AtomicBoolean
name|versionWarningAlreadyLogged
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|parseLuceneVersionString
specifier|public
specifier|static
specifier|final
name|Version
name|parseLuceneVersionString
parameter_list|(
specifier|final
name|String
name|matchVersion
parameter_list|)
block|{
name|String
name|parsedMatchVersion
init|=
name|matchVersion
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
comment|// be lenient with the supplied version parameter
name|parsedMatchVersion
operator|=
name|parsedMatchVersion
operator|.
name|replaceFirst
argument_list|(
literal|"^(\\d)\\.(\\d)$"
argument_list|,
literal|"LUCENE_$1$2"
argument_list|)
expr_stmt|;
specifier|final
name|Version
name|version
decl_stmt|;
try|try
block|{
name|version
operator|=
name|Version
operator|.
name|valueOf
argument_list|(
name|parsedMatchVersion
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
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
literal|"Invalid luceneMatchVersion '"
operator|+
name|matchVersion
operator|+
literal|"', valid values are: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|Version
operator|.
name|values
argument_list|()
argument_list|)
operator|+
literal|" or a string in format 'V.V'"
argument_list|,
name|iae
argument_list|)
throw|;
block|}
if|if
condition|(
name|version
operator|==
name|Version
operator|.
name|LUCENE_CURRENT
operator|&&
operator|!
name|versionWarningAlreadyLogged
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"You should not use LUCENE_CURRENT as luceneMatchVersion property: "
operator|+
literal|"if you use this setting, and then Solr upgrades to a newer release of Lucene, "
operator|+
literal|"sizable changes may happen. If precise back compatibility is important "
operator|+
literal|"then you should instead explicitly specify an actual Lucene version."
argument_list|)
expr_stmt|;
block|}
return|return
name|version
return|;
block|}
block|}
end_class
end_unit
