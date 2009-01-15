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
name|request
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
name|Reader
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
specifier|private
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
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|stream
operator|.
name|getReader
argument_list|()
expr_stmt|;
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
name|String
name|body
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|XmlUpdateRequestHandler
operator|.
name|log
operator|.
name|trace
argument_list|(
literal|"body"
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|StringReader
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
name|XMLStreamReader
name|parser
init|=
name|inputFactory
operator|.
name|createXMLStreamReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|this
operator|.
name|processUpdate
argument_list|(
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
comment|//Hmmm, not quite right
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @since solr 1.2    */
DECL|method|processUpdate
name|void
name|processUpdate
parameter_list|(
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
argument_list|()
expr_stmt|;
name|boolean
name|overwrite
init|=
literal|true
decl_stmt|;
comment|// the default
name|Boolean
name|overwritePending
init|=
literal|null
decl_stmt|;
name|Boolean
name|overwriteCommitted
init|=
literal|null
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
name|OVERWRITE
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
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
name|ALLOW_DUPS
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|overwrite
operator|=
operator|!
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
elseif|else
if|if
condition|(
name|XmlUpdateRequestHandler
operator|.
name|OVERWRITE_PENDING
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|overwritePending
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
name|OVERWRITE_COMMITTED
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|overwriteCommitted
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
literal|"Unknown attribute id in add:"
operator|+
name|attrName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// check if these flags are set
if|if
condition|(
name|overwritePending
operator|!=
literal|null
operator|&&
name|overwriteCommitted
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|overwritePending
operator|!=
name|overwriteCommitted
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
name|BAD_REQUEST
argument_list|,
literal|"can't have different values for 'overwritePending' and 'overwriteCommitted'"
argument_list|)
throw|;
block|}
name|overwrite
operator|=
name|overwritePending
expr_stmt|;
block|}
name|addCmd
operator|.
name|overwriteCommitted
operator|=
name|overwrite
expr_stmt|;
name|addCmd
operator|.
name|overwritePending
operator|=
name|overwrite
expr_stmt|;
name|addCmd
operator|.
name|allowDups
operator|=
operator|!
name|overwrite
expr_stmt|;
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
name|boolean
name|sawWaitSearcher
init|=
literal|false
decl_stmt|,
name|sawWaitFlush
init|=
literal|false
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
name|WAIT_FLUSH
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|cmd
operator|.
name|waitFlush
operator|=
name|StrUtils
operator|.
name|parseBoolean
argument_list|(
name|attrVal
argument_list|)
expr_stmt|;
name|sawWaitFlush
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
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
name|sawWaitSearcher
operator|=
literal|true
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
comment|// If waitFlush is specified and waitSearcher wasn't, then
comment|// clear waitSearcher.
if|if
condition|(
name|sawWaitFlush
operator|&&
operator|!
name|sawWaitSearcher
condition|)
block|{
name|cmd
operator|.
name|waitSearcher
operator|=
literal|false
expr_stmt|;
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
argument_list|()
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
argument_list|()
decl_stmt|;
name|deleteCmd
operator|.
name|fromPending
operator|=
literal|true
expr_stmt|;
name|deleteCmd
operator|.
name|fromCommitted
operator|=
literal|true
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
name|deleteCmd
operator|.
name|fromPending
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
literal|"fromCommitted"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|deleteCmd
operator|.
name|fromCommitted
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
