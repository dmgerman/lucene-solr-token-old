begin_unit
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|processor
operator|.
name|UpdateRequestProcessor
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
name|AddUpdateCommand
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
name|CommitUpdateCommand
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
name|RollbackUpdateCommand
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
name|DeleteUpdateCommand
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
name|common
operator|.
name|util
operator|.
name|ContentStream
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
name|ContentStreamBase
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
name|StrUtils
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
name|SolrInputDocument
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
name|params
operator|.
name|SolrParams
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
name|params
operator|.
name|UpdateParams
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
name|stream
operator|.
name|XMLStreamReader
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|FactoryConfigurationError
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamConstants
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
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
name|TransformerConfigurationException
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
name|InputStream
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
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|XMLLoader
class|class
name|XMLLoader
extends|extends
name|ContentStreamLoader
block|{
DECL|field|processor
specifier|protected
name|UpdateRequestProcessor
name|processor
decl_stmt|;
DECL|field|inputFactory
specifier|protected
name|XMLInputFactory
name|inputFactory
decl_stmt|;
DECL|method|XMLLoader
specifier|public
name|XMLLoader
parameter_list|(
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|XMLInputFactory
name|inputFactory
parameter_list|)
block|{
name|this
operator|.
name|processor
operator|=
name|processor
expr_stmt|;
name|this
operator|.
name|inputFactory
operator|=
name|inputFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|void
name|load
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|ContentStream
name|stream
parameter_list|)
throws|throws
name|Exception
block|{
name|errHeader
operator|=
literal|"XMLLoader: "
operator|+
name|stream
operator|.
name|getSourceInfo
argument_list|()
expr_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|XMLStreamReader
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|stream
operator|.
name|getStream
argument_list|()
expr_stmt|;
specifier|final
name|String
name|charset
init|=
name|ContentStreamBase
operator|.
name|getCharsetFromContentType
argument_list|(
name|stream
operator|.
name|getContentType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
specifier|final
name|byte
index|[]
name|body
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|is
argument_list|)
decl_stmt|;
comment|// TODO: The charset may be wrong, as the real charset is later
comment|// determined by the XML parser, the content-type is only used as a hint!
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|trace
argument_list|(
literal|"body"
argument_list|,
operator|new
name|String
argument_list|(
name|body
argument_list|,
operator|(
name|charset
operator|==
literal|null
operator|)
condition|?
name|ContentStreamBase
operator|.
name|DEFAULT_CHARSET
else|:
name|charset
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
name|parser
operator|=
operator|(
name|charset
operator|==
literal|null
operator|)
condition|?
name|inputFactory
operator|.
name|createXMLStreamReader
argument_list|(
name|is
argument_list|)
else|:
name|inputFactory
operator|.
name|createXMLStreamReader
argument_list|(
name|is
argument_list|,
name|charset
argument_list|)
expr_stmt|;
name|this
operator|.
name|processUpdate
argument_list|(
name|req
argument_list|,
name|processor
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
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
name|BAD_REQUEST
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @since solr 1.2    */
DECL|method|processUpdate
name|void
name|processUpdate
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|IOException
throws|,
name|FactoryConfigurationError
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
throws|,
name|TransformerConfigurationException
block|{
name|AddUpdateCommand
name|addCmd
init|=
literal|null
decl_stmt|;
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|END_DOCUMENT
case|:
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
return|return;
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|String
name|currTag
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|currTag
operator|.
name|equals
argument_list|(
name|XmlUpdateRequestHandler
operator|.
name|ADD
argument_list|)
condition|)
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|trace
argument_list|(
literal|"SolrCore.update(add)"
argument_list|)
expr_stmt|;
name|addCmd
operator|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
expr_stmt|;
comment|// First look for commitWithin parameter on the request, will be overwritten for individual<add>'s
name|addCmd
operator|.
name|commitWithin
operator|=
name|params
operator|.
name|getInt
argument_list|(
name|UpdateParams
operator|.
name|COMMIT_WITHIN
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|addCmd
operator|.
name|overwrite
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|OVERWRITE
argument_list|,
literal|true
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
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrVal
init|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|OVERWRITE
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|addCmd
operator|.
name|overwrite
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|COMMIT_WITHIN
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|addCmd
operator|.
name|commitWithin
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown attribute id in add:"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"doc"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
if|if
condition|(
name|addCmd
operator|!=
literal|null
condition|)
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|trace
argument_list|(
literal|"adding doc..."
argument_list|)
expr_stmt|;
name|addCmd
operator|.
name|clear
argument_list|()
expr_stmt|;
name|addCmd
operator|.
name|solrDoc
operator|=
name|readDoc
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|addCmd
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unexpected<doc> tag without an<add> tag surrounding it."
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|COMMIT
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
operator|||
name|XmlUpdateRequestHandler
operator|.
name|OPTIMIZE
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|trace
argument_list|(
literal|"parsing "
operator|+
name|currTag
argument_list|)
expr_stmt|;
name|CommitUpdateCommand
name|cmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
name|XmlUpdateRequestHandler
operator|.
name|OPTIMIZE
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
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
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrVal
init|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|WAIT_SEARCHER
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|waitSearcher
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|SOFT_COMMIT
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|softCommit
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|UpdateParams
operator|.
name|MAX_OPTIMIZE_SEGMENTS
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|maxOptimizeSegments
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|UpdateParams
operator|.
name|EXPUNGE_DELETES
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|expungeDeletes
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"unexpected attribute commit/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
name|processor
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
comment|// end commit
elseif|else
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|ROLLBACK
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|trace
argument_list|(
literal|"parsing "
operator|+
name|currTag
argument_list|)
expr_stmt|;
name|RollbackUpdateCommand
name|cmd
init|=
operator|new
name|RollbackUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|processor
operator|.
name|processRollback
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
comment|// end rollback
elseif|else
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|DELETE
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|trace
argument_list|(
literal|"parsing delete"
argument_list|)
expr_stmt|;
name|processDelete
argument_list|(
name|req
argument_list|,
name|processor
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
comment|// end delete
break|break;
block|}
block|}
block|}
comment|/**    * @since solr 1.3    */
DECL|method|processDelete
name|void
name|processDelete
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|IOException
block|{
comment|// Parse the command
name|DeleteUpdateCommand
name|deleteCmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
comment|// First look for commitWithin parameter on the request, will be overwritten for individual<delete>'s
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|deleteCmd
operator|.
name|commitWithin
operator|=
name|params
operator|.
name|getInt
argument_list|(
name|UpdateParams
operator|.
name|COMMIT_WITHIN
argument_list|,
operator|-
literal|1
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
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrVal
init|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"fromPending"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
comment|// deprecated
block|}
elseif|else
if|if
condition|(
literal|"fromCommitted"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
comment|// deprecated
block|}
elseif|else
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|COMMIT_WITHIN
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|deleteCmd
operator|.
name|commitWithin
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"unexpected attribute delete/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|String
name|mode
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
literal|"id"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
operator|||
literal|"query"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
operator|)
condition|)
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"unexpected XML tag /delete/"
operator|+
name|mode
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
name|BAD_REQUEST
argument_list|,
literal|"unexpected XML tag /delete/"
operator|+
name|mode
argument_list|)
throw|;
block|}
name|text
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
name|String
name|currTag
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"id"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|deleteCmd
operator|.
name|id
operator|=
name|text
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"query"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|deleteCmd
operator|.
name|query
operator|=
name|text
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"delete"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
return|return;
block|}
else|else
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"unexpected XML tag /delete/"
operator|+
name|currTag
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
name|BAD_REQUEST
argument_list|,
literal|"unexpected XML tag /delete/"
operator|+
name|currTag
argument_list|)
throw|;
block|}
name|processor
operator|.
name|processDelete
argument_list|(
name|deleteCmd
argument_list|)
expr_stmt|;
name|deleteCmd
operator|.
name|clear
argument_list|()
expr_stmt|;
break|break;
comment|// Add everything to the text
case|case
name|XMLStreamConstants
operator|.
name|SPACE
case|:
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|text
operator|.
name|append
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**    * Given the input stream, read a document    *    * @since solr 1.3    */
DECL|method|readDoc
name|SolrInputDocument
name|readDoc
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|String
name|attrName
init|=
literal|""
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
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|attrName
operator|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"boost"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|doc
operator|.
name|setDocumentBoost
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown attribute doc/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|boolean
name|isNull
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
comment|// Add everything to the text
case|case
name|XMLStreamConstants
operator|.
name|SPACE
case|:
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|text
operator|.
name|append
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
literal|"doc"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|doc
return|;
block|}
elseif|else
if|if
condition|(
literal|"field"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|isNull
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|text
operator|.
name|toString
argument_list|()
argument_list|,
name|boost
argument_list|)
expr_stmt|;
name|boost
operator|=
literal|1.0f
expr_stmt|;
block|}
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|text
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
name|localName
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"field"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"unexpected XML tag doc/"
operator|+
name|localName
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
name|BAD_REQUEST
argument_list|,
literal|"unexpected XML tag doc/"
operator|+
name|localName
argument_list|)
throw|;
block|}
name|boost
operator|=
literal|1.0f
expr_stmt|;
name|String
name|attrVal
init|=
literal|""
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
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|attrName
operator|=
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|attrVal
operator|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|name
operator|=
name|attrVal
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boost"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|boost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"null"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|isNull
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown attribute doc/field/@"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
block|}
block|}
block|}
end_class
end_unit
