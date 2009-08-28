begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|SEVERE
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
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|wrapAndThrow
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Source
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamSource
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|CharArrayReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|CharArrayWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ArrayBlockingQueue
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
name|BlockingQueue
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
name|TimeUnit
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
name|AtomicReference
import|;
end_import
begin_comment
comment|/**  *<p> An implementation of EntityProcessor which uses a streaming xpath parser to extract values out of XML documents.  * It is typically used in conjunction with HttpDataSource or FileDataSource.</p><p/><p> Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a> for more  * details.</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @version $Id$  * @see XPathRecordReader  * @since solr 1.3  */
end_comment
begin_class
DECL|class|XPathEntityProcessor
specifier|public
class|class
name|XPathEntityProcessor
extends|extends
name|EntityProcessorBase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|XPathEntityProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|placeHolderVariables
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|placeHolderVariables
decl_stmt|;
DECL|field|commonFields
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|commonFields
decl_stmt|;
DECL|field|pk
specifier|private
name|String
name|pk
decl_stmt|;
DECL|field|xpathReader
specifier|private
name|XPathRecordReader
name|xpathReader
decl_stmt|;
DECL|field|dataSource
specifier|protected
name|DataSource
argument_list|<
name|Reader
argument_list|>
name|dataSource
decl_stmt|;
DECL|field|xslTransformer
specifier|protected
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
name|xslTransformer
decl_stmt|;
DECL|field|useSolrAddXml
specifier|protected
name|boolean
name|useSolrAddXml
init|=
literal|false
decl_stmt|;
DECL|field|streamRows
specifier|protected
name|boolean
name|streamRows
init|=
literal|false
decl_stmt|;
DECL|field|batchSz
specifier|private
name|int
name|batchSz
init|=
literal|1000
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|xpathReader
operator|==
literal|null
condition|)
name|initXpathReader
argument_list|()
expr_stmt|;
name|pk
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"pk"
argument_list|)
expr_stmt|;
name|dataSource
operator|=
name|context
operator|.
name|getDataSource
argument_list|()
expr_stmt|;
name|rowIterator
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|initXpathReader
specifier|private
name|void
name|initXpathReader
parameter_list|()
block|{
name|useSolrAddXml
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|USE_SOLR_ADD_SCHEMA
argument_list|)
argument_list|)
expr_stmt|;
name|streamRows
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|STREAM
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"batchSize"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|batchSz
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"batchSize"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|xslt
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|XSL
argument_list|)
decl_stmt|;
if|if
condition|(
name|xslt
operator|!=
literal|null
condition|)
block|{
name|xslt
operator|=
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|xslt
argument_list|)
expr_stmt|;
try|try
block|{
name|Source
name|xsltSource
init|=
operator|new
name|StreamSource
argument_list|(
name|xslt
argument_list|)
decl_stmt|;
comment|// create an instance of TransformerFactory
name|TransformerFactory
name|transFact
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|xslTransformer
operator|=
name|transFact
operator|.
name|newTransformer
argument_list|(
name|xsltSource
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using xslTransformer: "
operator|+
name|xslTransformer
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
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
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Error initializing XSL "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|useSolrAddXml
condition|)
block|{
comment|// Support solr add documents
name|xpathReader
operator|=
operator|new
name|XPathRecordReader
argument_list|(
literal|"/add/doc"
argument_list|)
expr_stmt|;
name|xpathReader
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|"/add/doc/field/@name"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|xpathReader
operator|.
name|addField
argument_list|(
literal|"value"
argument_list|,
literal|"/add/doc/field"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|forEachXpath
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|FOR_EACH
argument_list|)
decl_stmt|;
if|if
condition|(
name|forEachXpath
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Entity : "
operator|+
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"name"
argument_list|)
operator|+
literal|" must have a 'forEach' attribute"
argument_list|)
throw|;
try|try
block|{
name|xpathReader
operator|=
operator|new
name|XPathRecordReader
argument_list|(
name|forEachXpath
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|field
range|:
name|context
operator|.
name|getAllEntityFields
argument_list|()
control|)
block|{
if|if
condition|(
name|field
operator|.
name|get
argument_list|(
name|XPATH
argument_list|)
operator|==
literal|null
condition|)
continue|continue;
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|field
operator|.
name|get
argument_list|(
literal|"flatten"
argument_list|)
argument_list|)
condition|)
block|{
name|flags
operator|=
name|XPathRecordReader
operator|.
name|FLATTEN
expr_stmt|;
block|}
name|String
name|xpath
init|=
name|field
operator|.
name|get
argument_list|(
name|XPATH
argument_list|)
decl_stmt|;
name|xpath
operator|=
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|xpath
argument_list|)
expr_stmt|;
name|xpathReader
operator|.
name|addField
argument_list|(
name|field
operator|.
name|get
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|)
argument_list|,
name|xpath
argument_list|,
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|field
operator|.
name|get
argument_list|(
name|DataImporter
operator|.
name|MULTI_VALUED
argument_list|)
argument_list|)
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Exception while reading xpaths for fields"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|String
name|url
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|URL
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
name|url
operator|==
literal|null
condition|?
name|Collections
operator|.
name|EMPTY_LIST
else|:
name|TemplateString
operator|.
name|getVariables
argument_list|(
name|url
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|l
control|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
name|entityName
operator|+
literal|"."
argument_list|)
condition|)
block|{
if|if
condition|(
name|placeHolderVariables
operator|==
literal|null
condition|)
name|placeHolderVariables
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|placeHolderVariables
operator|.
name|add
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|entityName
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fld
range|:
name|context
operator|.
name|getAllEntityFields
argument_list|()
control|)
block|{
if|if
condition|(
name|fld
operator|.
name|get
argument_list|(
name|COMMON_FIELD
argument_list|)
operator|!=
literal|null
operator|&&
literal|"true"
operator|.
name|equals
argument_list|(
name|fld
operator|.
name|get
argument_list|(
name|COMMON_FIELD
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|commonFields
operator|==
literal|null
condition|)
name|commonFields
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|commonFields
operator|.
name|add
argument_list|(
name|fld
operator|.
name|get
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
decl_stmt|;
if|if
condition|(
operator|!
name|context
operator|.
name|isRootEntity
argument_list|()
condition|)
return|return
name|fetchNextRow
argument_list|()
return|;
while|while
condition|(
literal|true
condition|)
block|{
name|result
operator|=
name|fetchNextRow
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|pk
operator|==
literal|null
operator|||
name|result
operator|.
name|get
argument_list|(
name|pk
argument_list|)
operator|!=
literal|null
condition|)
return|return
name|result
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|postTransform
specifier|public
name|void
name|postTransform
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
parameter_list|)
block|{
name|readUsefulVars
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|fetchNextRow
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fetchNextRow
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|rowIterator
operator|==
literal|null
condition|)
name|initQuery
argument_list|(
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|URL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|getNext
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|Object
name|hasMore
init|=
name|context
operator|.
name|getSessionAttribute
argument_list|(
name|HAS_MORE
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|hasMore
argument_list|)
operator|||
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|hasMore
argument_list|)
condition|)
block|{
name|String
name|url
init|=
operator|(
name|String
operator|)
name|context
operator|.
name|getSessionAttribute
argument_list|(
name|NEXT_URL
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
name|url
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|URL
argument_list|)
expr_stmt|;
name|addNamespace
argument_list|()
expr_stmt|;
name|initQuery
argument_list|(
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|getNext
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
finally|finally
block|{
name|context
operator|.
name|setSessionAttribute
argument_list|(
name|HAS_MORE
argument_list|,
literal|null
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
expr_stmt|;
name|context
operator|.
name|setSessionAttribute
argument_list|(
name|NEXT_URL
argument_list|,
literal|null
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
expr_stmt|;
block|}
block|}
name|addCommonFields
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
block|}
DECL|method|addNamespace
specifier|private
name|void
name|addNamespace
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|namespace
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|allNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|commonFields
operator|!=
literal|null
condition|)
name|allNames
operator|.
name|addAll
argument_list|(
name|commonFields
argument_list|)
expr_stmt|;
if|if
condition|(
name|placeHolderVariables
operator|!=
literal|null
condition|)
name|allNames
operator|.
name|addAll
argument_list|(
name|placeHolderVariables
argument_list|)
expr_stmt|;
if|if
condition|(
name|allNames
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
for|for
control|(
name|String
name|name
range|:
name|allNames
control|)
block|{
name|Object
name|val
init|=
name|context
operator|.
name|getSessionAttribute
argument_list|(
name|name
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
name|namespace
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
name|resolver
operator|.
name|addNamespace
argument_list|(
name|entityName
argument_list|,
name|namespace
argument_list|)
expr_stmt|;
block|}
DECL|method|addCommonFields
specifier|private
name|void
name|addCommonFields
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
parameter_list|)
block|{
if|if
condition|(
name|commonFields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|commonField
range|:
name|commonFields
control|)
block|{
if|if
condition|(
name|r
operator|.
name|get
argument_list|(
name|commonField
argument_list|)
operator|==
literal|null
condition|)
block|{
name|Object
name|val
init|=
name|context
operator|.
name|getSessionAttribute
argument_list|(
name|commonField
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
name|r
operator|.
name|put
argument_list|(
name|commonField
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|initQuery
specifier|private
name|void
name|initQuery
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|Reader
name|data
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|data
operator|=
name|dataSource
operator|.
name|getData
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|ABORT
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SKIP
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Skipping url : "
operator|+
name|s
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|wrapAndThrow
argument_list|(
name|DataImportHandlerException
operator|.
name|SKIP
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed for url : "
operator|+
name|s
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rowIterator
operator|=
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|xslTransformer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|SimpleCharArrayReader
name|caw
init|=
operator|new
name|SimpleCharArrayReader
argument_list|()
decl_stmt|;
name|xslTransformer
operator|.
name|transform
argument_list|(
operator|new
name|StreamSource
argument_list|(
name|data
argument_list|)
argument_list|,
operator|new
name|StreamResult
argument_list|(
name|caw
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|=
name|caw
operator|.
name|getReader
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
if|if
condition|(
name|ABORT
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Exception in applying XSL Transformeation"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SKIP
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|DataImportHandlerException
operator|.
name|SKIP
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed for url : "
operator|+
name|s
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rowIterator
operator|=
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
block|}
if|if
condition|(
name|streamRows
condition|)
block|{
name|rowIterator
operator|=
name|getRowIterator
argument_list|(
name|data
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|xpathReader
operator|.
name|streamRecords
argument_list|(
name|data
argument_list|,
operator|new
name|XPathRecordReader
operator|.
name|Handler
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|handle
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
parameter_list|,
name|String
name|xpath
parameter_list|)
block|{
name|rows
operator|.
name|add
argument_list|(
name|readRow
argument_list|(
name|record
argument_list|,
name|xpath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Parsing failed for xml, url:"
operator|+
name|s
operator|+
literal|" rows processed:"
operator|+
name|rows
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|rows
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|msg
operator|+=
literal|" last row: "
operator|+
name|rows
operator|.
name|get
argument_list|(
name|rows
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|ABORT
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SKIP
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SKIP_DOC
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|CONTINUE
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|rowIterator
operator|=
name|rows
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|streamRows
condition|)
block|{
name|closeIt
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|closeIt
specifier|private
name|void
name|closeIt
parameter_list|(
name|Reader
name|data
parameter_list|)
block|{
try|try
block|{
name|data
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
block|{
comment|/* Ignore */
block|}
block|}
DECL|method|readRow
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
parameter_list|,
name|String
name|xpath
parameter_list|)
block|{
if|if
condition|(
name|useSolrAddXml
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|record
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|record
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
name|names
operator|.
name|size
argument_list|()
operator|&&
name|i
operator|<
name|values
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|row
operator|.
name|containsKey
argument_list|(
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|Object
name|existing
init|=
name|row
operator|.
name|get
argument_list|(
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|instanceof
name|List
condition|)
block|{
name|List
name|list
init|=
operator|(
name|List
operator|)
name|existing
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|existing
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|row
operator|.
name|put
argument_list|(
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|row
operator|.
name|put
argument_list|(
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|row
return|;
block|}
else|else
block|{
name|record
operator|.
name|put
argument_list|(
name|XPATH_FIELD_NAME
argument_list|,
name|xpath
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
block|}
DECL|class|SimpleCharArrayReader
specifier|private
specifier|static
class|class
name|SimpleCharArrayReader
extends|extends
name|CharArrayWriter
block|{
DECL|method|getReader
specifier|public
name|Reader
name|getReader
parameter_list|()
block|{
return|return
operator|new
name|CharArrayReader
argument_list|(
name|super
operator|.
name|buf
argument_list|,
literal|0
argument_list|,
name|super
operator|.
name|count
argument_list|)
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|readUsefulVars
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readUsefulVars
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
parameter_list|)
block|{
name|Object
name|val
init|=
name|r
operator|.
name|get
argument_list|(
name|HAS_MORE
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
name|context
operator|.
name|setSessionAttribute
argument_list|(
name|HAS_MORE
argument_list|,
name|val
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
expr_stmt|;
name|val
operator|=
name|r
operator|.
name|get
argument_list|(
name|NEXT_URL
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
name|context
operator|.
name|setSessionAttribute
argument_list|(
name|NEXT_URL
argument_list|,
name|val
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
expr_stmt|;
if|if
condition|(
name|placeHolderVariables
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|placeHolderVariables
control|)
block|{
name|val
operator|=
name|r
operator|.
name|get
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|context
operator|.
name|setSessionAttribute
argument_list|(
name|s
argument_list|,
name|val
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|commonFields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|commonFields
control|)
block|{
name|Object
name|commonVal
init|=
name|r
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|commonVal
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|setSessionAttribute
argument_list|(
name|s
argument_list|,
name|commonVal
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|r
return|;
block|}
DECL|method|getRowIterator
specifier|private
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getRowIterator
parameter_list|(
specifier|final
name|Reader
name|data
parameter_list|,
specifier|final
name|String
name|s
parameter_list|)
block|{
comment|//nothing atomic about it. I just needed a StongReference
specifier|final
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|exp
init|=
operator|new
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|BlockingQueue
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|blockingQueue
init|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|(
name|batchSz
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|isEnd
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|xpathReader
operator|.
name|streamRecords
argument_list|(
name|data
argument_list|,
operator|new
name|XPathRecordReader
operator|.
name|Handler
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|handle
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
parameter_list|,
name|String
name|xpath
parameter_list|)
block|{
if|if
condition|(
name|isEnd
operator|.
name|get
argument_list|()
condition|)
return|return;
try|try
block|{
name|blockingQueue
operator|.
name|offer
argument_list|(
name|readRow
argument_list|(
name|record
argument_list|,
name|xpath
argument_list|)
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|isEnd
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exp
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeIt
argument_list|(
name|data
argument_list|)
expr_stmt|;
try|try
block|{
name|blockingQueue
operator|.
name|offer
argument_list|(
name|Collections
operator|.
name|EMPTY_MAP
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{           }
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|lastRow
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|!
name|isEnd
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|next
parameter_list|()
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
name|blockingQueue
operator|.
name|poll
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|row
operator|==
literal|null
operator|||
name|row
operator|==
name|Collections
operator|.
name|EMPTY_MAP
condition|)
block|{
name|isEnd
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|exp
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Parsing failed for xml, url:"
operator|+
name|s
operator|+
literal|" rows processed in this xml:"
operator|+
name|count
decl_stmt|;
if|if
condition|(
name|lastRow
operator|!=
literal|null
condition|)
name|msg
operator|+=
literal|" last row in this xml:"
operator|+
name|lastRow
expr_stmt|;
if|if
condition|(
name|ABORT
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|exp
operator|.
name|get
argument_list|()
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SKIP
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|DataImportHandlerException
operator|.
name|SKIP
argument_list|,
name|exp
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|,
name|exp
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
name|count
operator|++
expr_stmt|;
return|return
name|lastRow
operator|=
name|row
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|isEnd
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
comment|/*no op*/
block|}
block|}
return|;
block|}
DECL|field|URL
specifier|public
specifier|static
specifier|final
name|String
name|URL
init|=
literal|"url"
decl_stmt|;
DECL|field|HAS_MORE
specifier|public
specifier|static
specifier|final
name|String
name|HAS_MORE
init|=
literal|"$hasMore"
decl_stmt|;
DECL|field|NEXT_URL
specifier|public
specifier|static
specifier|final
name|String
name|NEXT_URL
init|=
literal|"$nextUrl"
decl_stmt|;
DECL|field|XPATH_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|XPATH_FIELD_NAME
init|=
literal|"$forEach"
decl_stmt|;
DECL|field|FOR_EACH
specifier|public
specifier|static
specifier|final
name|String
name|FOR_EACH
init|=
literal|"forEach"
decl_stmt|;
DECL|field|XPATH
specifier|public
specifier|static
specifier|final
name|String
name|XPATH
init|=
literal|"xpath"
decl_stmt|;
DECL|field|COMMON_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|COMMON_FIELD
init|=
literal|"commonField"
decl_stmt|;
DECL|field|USE_SOLR_ADD_SCHEMA
specifier|public
specifier|static
specifier|final
name|String
name|USE_SOLR_ADD_SCHEMA
init|=
literal|"useSolrAddSchema"
decl_stmt|;
DECL|field|XSL
specifier|public
specifier|static
specifier|final
name|String
name|XSL
init|=
literal|"xsl"
decl_stmt|;
DECL|field|STREAM
specifier|public
specifier|static
specifier|final
name|String
name|STREAM
init|=
literal|"stream"
decl_stmt|;
block|}
end_class
end_unit
