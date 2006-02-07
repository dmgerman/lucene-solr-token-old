begin_unit
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|SolrException
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
name|*
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
name|XPathFactory
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
name|namespace
operator|.
name|QName
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import
begin_comment
comment|/**  * @author yonik  * @version $Id: Config.java,v 1.10 2005/12/20 16:05:46 yonik Exp $  */
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
name|Logger
operator|.
name|getLogger
argument_list|(
name|SolrCore
operator|.
name|class
operator|.
name|getName
argument_list|()
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
name|Document
name|doc
decl_stmt|;
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|method|Config
specifier|public
name|Config
parameter_list|(
name|String
name|name
parameter_list|,
name|InputStream
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
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
if|if
condition|(
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
condition|)
name|prefix
operator|+=
literal|'/'
expr_stmt|;
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
name|builder
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|doc
operator|=
name|builder
operator|.
name|parse
argument_list|(
name|is
argument_list|)
expr_stmt|;
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
literal|500
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
argument_list|,
literal|false
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
name|fine
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
name|finest
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
literal|500
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
argument_list|,
literal|false
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
literal|500
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
argument_list|,
literal|false
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
comment|// should do the right thing for both attributes and elements.
comment|// Oops, when running in Resin, I get an unsupported operation
comment|// exception... need to use Sun default (apache)
name|String
name|txt
init|=
name|nd
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
name|log
operator|.
name|fine
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
return|return
name|val
operator|!=
literal|null
condition|?
name|val
else|:
name|def
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
literal|false
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
literal|false
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
literal|false
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
comment|//
comment|// classloader related functions
comment|//
DECL|field|project
specifier|private
specifier|static
specifier|final
name|String
name|project
init|=
literal|"solr"
decl_stmt|;
DECL|field|base
specifier|private
specifier|static
specifier|final
name|String
name|base
init|=
literal|"org.apache"
operator|+
literal|"."
operator|+
name|project
decl_stmt|;
DECL|field|packages
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|packages
init|=
block|{
literal|""
block|,
literal|"analysis."
block|,
literal|"schema."
block|,
literal|"search."
block|,
literal|"update."
block|,
literal|"core."
block|,
literal|"request."
block|,
literal|"util."
block|}
decl_stmt|;
DECL|method|findClass
specifier|public
specifier|static
name|Class
name|findClass
parameter_list|(
name|String
name|cname
parameter_list|,
name|String
modifier|...
name|subpackages
parameter_list|)
block|{
name|ClassLoader
name|loader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|subpackages
operator|.
name|length
operator|==
literal|0
condition|)
name|subpackages
operator|=
name|packages
expr_stmt|;
comment|// first try cname == full name
try|try
block|{
return|return
name|Class
operator|.
name|forName
argument_list|(
name|cname
argument_list|,
literal|true
argument_list|,
name|loader
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|String
name|newName
init|=
name|cname
decl_stmt|;
if|if
condition|(
name|newName
operator|.
name|startsWith
argument_list|(
literal|"solar."
argument_list|)
condition|)
block|{
comment|// handle legacy package names
name|newName
operator|=
name|cname
operator|.
name|substring
argument_list|(
literal|"solar."
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cname
operator|.
name|startsWith
argument_list|(
name|project
operator|+
literal|"."
argument_list|)
condition|)
block|{
name|newName
operator|=
name|cname
operator|.
name|substring
argument_list|(
name|project
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|subpackage
range|:
name|subpackages
control|)
block|{
try|try
block|{
name|String
name|name
init|=
name|base
operator|+
literal|'.'
operator|+
name|subpackage
operator|+
name|newName
decl_stmt|;
name|log
operator|.
name|finest
argument_list|(
literal|"Trying class name "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|loader
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e1
parameter_list|)
block|{
comment|// ignore... assume first exception is best.
block|}
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
literal|500
argument_list|,
literal|"Error loading class '"
operator|+
name|cname
operator|+
literal|"'"
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
throw|;
block|}
block|}
DECL|method|newInstance
specifier|public
specifier|static
name|Object
name|newInstance
parameter_list|(
name|String
name|cname
parameter_list|,
name|String
modifier|...
name|subpackages
parameter_list|)
block|{
name|Class
name|clazz
init|=
name|findClass
argument_list|(
name|cname
argument_list|,
name|subpackages
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|clazz
operator|.
name|newInstance
argument_list|()
return|;
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
literal|500
argument_list|,
literal|"Error instantiating class "
operator|+
name|clazz
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
throw|;
block|}
block|}
DECL|method|openResource
specifier|public
specifier|static
name|InputStream
name|openResource
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
comment|// try $CWD/conf/
name|f
operator|=
operator|new
name|File
argument_list|(
literal|"conf/"
operator|+
name|resource
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|isFile
argument_list|()
operator|&&
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
return|return
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
return|;
block|}
else|else
block|{
comment|// try $CWD
name|f
operator|=
operator|new
name|File
argument_list|(
name|resource
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isFile
argument_list|()
operator|&&
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
return|return
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
return|;
block|}
block|}
name|ClassLoader
name|loader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|is
operator|=
name|loader
operator|.
name|getResourceAsStream
argument_list|(
name|resource
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
literal|"Error opening "
operator|+
name|resource
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can't find resource "
operator|+
name|resource
argument_list|)
throw|;
block|}
return|return
name|is
return|;
block|}
comment|/**    * Returns a list of non-blank non-comment lines with whitespace trimmed from front and back.    * @param resource    * @return    * @throws IOException    */
DECL|method|getLines
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getLines
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|input
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// todo - allow configurable charset?
name|input
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|openResource
argument_list|(
name|resource
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
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
name|ArrayList
argument_list|<
name|String
argument_list|>
name|lines
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|word
init|=
literal|null
init|;
operator|(
name|word
operator|=
name|input
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|;
control|)
block|{
comment|// skip comments
if|if
condition|(
name|word
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
continue|continue;
name|word
operator|=
name|word
operator|.
name|trim
argument_list|()
expr_stmt|;
comment|// skip blank lines
if|if
condition|(
name|word
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|lines
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
block|}
return|return
name|lines
return|;
block|}
block|}
end_class
end_unit
