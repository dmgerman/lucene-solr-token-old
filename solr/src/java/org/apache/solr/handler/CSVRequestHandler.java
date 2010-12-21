begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|schema
operator|.
name|IndexSchema
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
name|SchemaField
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
name|*
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
name|commons
operator|.
name|csv
operator|.
name|CSVStrategy
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
name|csv
operator|.
name|CSVParser
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|io
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * @version $Id$  */
end_comment
begin_class
DECL|class|CSVRequestHandler
specifier|public
class|class
name|CSVRequestHandler
extends|extends
name|ContentStreamHandlerBase
block|{
DECL|method|newLoader
specifier|protected
name|ContentStreamLoader
name|newLoader
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|)
block|{
return|return
operator|new
name|SingleThreadedCSVLoader
argument_list|(
name|req
argument_list|,
name|processor
argument_list|)
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Add/Update multiple documents with CSV formatted rows"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class
begin_class
DECL|class|CSVLoader
specifier|abstract
class|class
name|CSVLoader
extends|extends
name|ContentStreamLoader
block|{
DECL|field|SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|SEPARATOR
init|=
literal|"separator"
decl_stmt|;
DECL|field|FIELDNAMES
specifier|public
specifier|static
specifier|final
name|String
name|FIELDNAMES
init|=
literal|"fieldnames"
decl_stmt|;
DECL|field|HEADER
specifier|public
specifier|static
specifier|final
name|String
name|HEADER
init|=
literal|"header"
decl_stmt|;
DECL|field|SKIP
specifier|public
specifier|static
specifier|final
name|String
name|SKIP
init|=
literal|"skip"
decl_stmt|;
DECL|field|SKIPLINES
specifier|public
specifier|static
specifier|final
name|String
name|SKIPLINES
init|=
literal|"skipLines"
decl_stmt|;
DECL|field|MAP
specifier|public
specifier|static
specifier|final
name|String
name|MAP
init|=
literal|"map"
decl_stmt|;
DECL|field|TRIM
specifier|public
specifier|static
specifier|final
name|String
name|TRIM
init|=
literal|"trim"
decl_stmt|;
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|String
name|EMPTY
init|=
literal|"keepEmpty"
decl_stmt|;
DECL|field|SPLIT
specifier|public
specifier|static
specifier|final
name|String
name|SPLIT
init|=
literal|"split"
decl_stmt|;
DECL|field|ENCAPSULATOR
specifier|public
specifier|static
specifier|final
name|String
name|ENCAPSULATOR
init|=
literal|"encapsulator"
decl_stmt|;
DECL|field|ESCAPE
specifier|public
specifier|static
specifier|final
name|String
name|ESCAPE
init|=
literal|"escape"
decl_stmt|;
DECL|field|OVERWRITE
specifier|public
specifier|static
specifier|final
name|String
name|OVERWRITE
init|=
literal|"overwrite"
decl_stmt|;
DECL|field|colonSplit
specifier|private
specifier|static
name|Pattern
name|colonSplit
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
DECL|field|commaSplit
specifier|private
specifier|static
name|Pattern
name|commaSplit
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|","
argument_list|)
decl_stmt|;
DECL|field|schema
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|params
specifier|final
name|SolrParams
name|params
decl_stmt|;
DECL|field|strategy
specifier|final
name|CSVStrategy
name|strategy
decl_stmt|;
DECL|field|processor
specifier|final
name|UpdateRequestProcessor
name|processor
decl_stmt|;
DECL|field|fieldnames
name|String
index|[]
name|fieldnames
decl_stmt|;
DECL|field|fields
name|SchemaField
index|[]
name|fields
decl_stmt|;
DECL|field|adders
name|CSVLoader
operator|.
name|FieldAdder
index|[]
name|adders
decl_stmt|;
DECL|field|skipLines
name|int
name|skipLines
decl_stmt|;
comment|// number of lines to skip at start of file
DECL|field|templateAdd
specifier|final
name|AddUpdateCommand
name|templateAdd
decl_stmt|;
comment|/** Add a field to a document unless it's zero length.    * The FieldAdder hierarchy handles all the complexity of    * further transforming or splitting field values to keep the    * main logic loop clean.  All implementations of add() must be    * MT-safe!    */
DECL|class|FieldAdder
specifier|private
class|class
name|FieldAdder
block|{
DECL|method|add
name|void
name|add
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|fields
index|[
name|column
index|]
operator|.
name|getName
argument_list|()
argument_list|,
name|val
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** add zero length fields */
DECL|class|FieldAdderEmpty
specifier|private
class|class
name|FieldAdderEmpty
extends|extends
name|CSVLoader
operator|.
name|FieldAdder
block|{
DECL|method|add
name|void
name|add
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|fields
index|[
name|column
index|]
operator|.
name|getName
argument_list|()
argument_list|,
name|val
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** trim fields */
DECL|class|FieldTrimmer
specifier|private
class|class
name|FieldTrimmer
extends|extends
name|CSVLoader
operator|.
name|FieldAdder
block|{
DECL|field|base
specifier|private
specifier|final
name|CSVLoader
operator|.
name|FieldAdder
name|base
decl_stmt|;
DECL|method|FieldTrimmer
name|FieldTrimmer
parameter_list|(
name|CSVLoader
operator|.
name|FieldAdder
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|base
operator|.
name|add
argument_list|(
name|doc
argument_list|,
name|line
argument_list|,
name|column
argument_list|,
name|val
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** map a single value.    * for just a couple of mappings, this is probably faster than    * using a HashMap.    */
DECL|class|FieldMapperSingle
specifier|private
class|class
name|FieldMapperSingle
extends|extends
name|CSVLoader
operator|.
name|FieldAdder
block|{
DECL|field|from
specifier|private
specifier|final
name|String
name|from
decl_stmt|;
DECL|field|to
specifier|private
specifier|final
name|String
name|to
decl_stmt|;
DECL|field|base
specifier|private
specifier|final
name|CSVLoader
operator|.
name|FieldAdder
name|base
decl_stmt|;
DECL|method|FieldMapperSingle
name|FieldMapperSingle
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|,
name|CSVLoader
operator|.
name|FieldAdder
name|base
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|from
operator|.
name|equals
argument_list|(
name|val
argument_list|)
condition|)
name|val
operator|=
name|to
expr_stmt|;
name|base
operator|.
name|add
argument_list|(
name|doc
argument_list|,
name|line
argument_list|,
name|column
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Split a single value into multiple values based on    * a CSVStrategy.    */
DECL|class|FieldSplitter
specifier|private
class|class
name|FieldSplitter
extends|extends
name|CSVLoader
operator|.
name|FieldAdder
block|{
DECL|field|strategy
specifier|private
specifier|final
name|CSVStrategy
name|strategy
decl_stmt|;
DECL|field|base
specifier|private
specifier|final
name|CSVLoader
operator|.
name|FieldAdder
name|base
decl_stmt|;
DECL|method|FieldSplitter
name|FieldSplitter
parameter_list|(
name|CSVStrategy
name|strategy
parameter_list|,
name|CSVLoader
operator|.
name|FieldAdder
name|base
parameter_list|)
block|{
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|CSVParser
name|parser
init|=
operator|new
name|CSVParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|val
argument_list|)
argument_list|,
name|strategy
argument_list|)
decl_stmt|;
try|try
block|{
name|String
index|[]
name|vals
init|=
name|parser
operator|.
name|getLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|vals
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|v
range|:
name|vals
control|)
name|base
operator|.
name|add
argument_list|(
name|doc
argument_list|,
name|line
argument_list|,
name|column
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|base
operator|.
name|add
argument_list|(
name|doc
argument_list|,
name|line
argument_list|,
name|column
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
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
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|errHeader
name|String
name|errHeader
init|=
literal|"CSVLoader:"
decl_stmt|;
DECL|method|CSVLoader
name|CSVLoader
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
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
name|params
operator|=
name|req
operator|.
name|getParams
argument_list|()
expr_stmt|;
name|schema
operator|=
name|req
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|templateAdd
operator|=
operator|new
name|AddUpdateCommand
argument_list|()
expr_stmt|;
name|templateAdd
operator|.
name|allowDups
operator|=
literal|false
expr_stmt|;
name|templateAdd
operator|.
name|overwriteCommitted
operator|=
literal|true
expr_stmt|;
name|templateAdd
operator|.
name|overwritePending
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|OVERWRITE
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|templateAdd
operator|.
name|allowDups
operator|=
literal|false
expr_stmt|;
name|templateAdd
operator|.
name|overwriteCommitted
operator|=
literal|true
expr_stmt|;
name|templateAdd
operator|.
name|overwritePending
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|templateAdd
operator|.
name|allowDups
operator|=
literal|true
expr_stmt|;
name|templateAdd
operator|.
name|overwriteCommitted
operator|=
literal|false
expr_stmt|;
name|templateAdd
operator|.
name|overwritePending
operator|=
literal|false
expr_stmt|;
block|}
name|strategy
operator|=
operator|new
name|CSVStrategy
argument_list|(
literal|','
argument_list|,
literal|'"'
argument_list|,
name|CSVStrategy
operator|.
name|COMMENTS_DISABLED
argument_list|,
name|CSVStrategy
operator|.
name|ESCAPE_DISABLED
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|sep
init|=
name|params
operator|.
name|get
argument_list|(
name|SEPARATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|sep
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sep
operator|.
name|length
argument_list|()
operator|!=
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
name|BAD_REQUEST
argument_list|,
literal|"Invalid separator:'"
operator|+
name|sep
operator|+
literal|"'"
argument_list|)
throw|;
name|strategy
operator|.
name|setDelimiter
argument_list|(
name|sep
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|encapsulator
init|=
name|params
operator|.
name|get
argument_list|(
name|ENCAPSULATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|encapsulator
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|encapsulator
operator|.
name|length
argument_list|()
operator|!=
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
name|BAD_REQUEST
argument_list|,
literal|"Invalid encapsulator:'"
operator|+
name|encapsulator
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|String
name|escape
init|=
name|params
operator|.
name|get
argument_list|(
name|ESCAPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|escape
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|escape
operator|.
name|length
argument_list|()
operator|!=
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
name|BAD_REQUEST
argument_list|,
literal|"Invalid escape:'"
operator|+
name|escape
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|// if only encapsulator or escape is set, disable the other escaping mechanism
if|if
condition|(
name|encapsulator
operator|==
literal|null
operator|&&
name|escape
operator|!=
literal|null
condition|)
block|{
name|strategy
operator|.
name|setEncapsulator
argument_list|(
name|CSVStrategy
operator|.
name|ENCAPSULATOR_DISABLED
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setEscape
argument_list|(
name|escape
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|encapsulator
operator|!=
literal|null
condition|)
block|{
name|strategy
operator|.
name|setEncapsulator
argument_list|(
name|encapsulator
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|escape
operator|!=
literal|null
condition|)
block|{
name|char
name|ch
init|=
name|escape
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|strategy
operator|.
name|setEscape
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'\\'
condition|)
block|{
comment|// If the escape is the standard backslash, then also enable
comment|// unicode escapes (it's harmless since 'u' would not otherwise
comment|// be escaped.
name|strategy
operator|.
name|setUnicodeEscapeInterpretation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|String
name|fn
init|=
name|params
operator|.
name|get
argument_list|(
name|FIELDNAMES
argument_list|)
decl_stmt|;
name|fieldnames
operator|=
name|fn
operator|!=
literal|null
condition|?
name|commaSplit
operator|.
name|split
argument_list|(
name|fn
argument_list|,
operator|-
literal|1
argument_list|)
else|:
literal|null
expr_stmt|;
name|Boolean
name|hasHeader
init|=
name|params
operator|.
name|getBool
argument_list|(
name|HEADER
argument_list|)
decl_stmt|;
name|skipLines
operator|=
name|params
operator|.
name|getInt
argument_list|(
name|SKIPLINES
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldnames
operator|==
literal|null
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|hasHeader
condition|)
block|{
comment|// assume the file has the headers if they aren't supplied in the args
name|hasHeader
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|hasHeader
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
literal|"CSVLoader: must specify fieldnames=<fields>* or header=true"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// if the fieldnames were supplied and the file has a header, we need to
comment|// skip over that header.
if|if
condition|(
name|hasHeader
operator|!=
literal|null
operator|&&
name|hasHeader
condition|)
name|skipLines
operator|++
expr_stmt|;
name|prepareFields
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** create the FieldAdders that control how each field  is indexed */
DECL|method|prepareFields
name|void
name|prepareFields
parameter_list|()
block|{
comment|// Possible future optimization: for really rapid incremental indexing
comment|// from a POST, one could cache all of this setup info based on the params.
comment|// The link from FieldAdder to this would need to be severed for that to happen.
name|fields
operator|=
operator|new
name|SchemaField
index|[
name|fieldnames
operator|.
name|length
index|]
expr_stmt|;
name|adders
operator|=
operator|new
name|CSVLoader
operator|.
name|FieldAdder
index|[
name|fieldnames
operator|.
name|length
index|]
expr_stmt|;
name|String
name|skipStr
init|=
name|params
operator|.
name|get
argument_list|(
name|SKIP
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|skipFields
init|=
name|skipStr
operator|==
literal|null
condition|?
literal|null
else|:
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|skipStr
argument_list|,
literal|','
argument_list|)
decl_stmt|;
name|CSVLoader
operator|.
name|FieldAdder
name|adder
init|=
operator|new
name|CSVLoader
operator|.
name|FieldAdder
argument_list|()
decl_stmt|;
name|CSVLoader
operator|.
name|FieldAdder
name|adderKeepEmpty
init|=
operator|new
name|CSVLoader
operator|.
name|FieldAdderEmpty
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fname
init|=
name|fieldnames
index|[
name|i
index|]
decl_stmt|;
comment|// to skip a field, leave the entries in fields and addrs null
if|if
condition|(
name|fname
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
operator|(
name|skipFields
operator|!=
literal|null
operator|&&
name|skipFields
operator|.
name|contains
argument_list|(
name|fname
argument_list|)
operator|)
condition|)
continue|continue;
name|fields
index|[
name|i
index|]
operator|=
name|schema
operator|.
name|getField
argument_list|(
name|fname
argument_list|)
expr_stmt|;
name|boolean
name|keepEmpty
init|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|fname
argument_list|,
name|EMPTY
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|adders
index|[
name|i
index|]
operator|=
name|keepEmpty
condition|?
name|adderKeepEmpty
else|:
name|adder
expr_stmt|;
comment|// Order that operations are applied: split -> trim -> map -> add
comment|// so create in reverse order.
comment|// Creation of FieldAdders could be optimized and shared among fields
name|String
index|[]
name|fmap
init|=
name|params
operator|.
name|getFieldParams
argument_list|(
name|fname
argument_list|,
name|MAP
argument_list|)
decl_stmt|;
if|if
condition|(
name|fmap
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|mapRule
range|:
name|fmap
control|)
block|{
name|String
index|[]
name|mapArgs
init|=
name|colonSplit
operator|.
name|split
argument_list|(
name|mapRule
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapArgs
operator|.
name|length
operator|!=
literal|2
condition|)
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
literal|"Map rules must be of the form 'from:to' ,got '"
operator|+
name|mapRule
operator|+
literal|"'"
argument_list|)
throw|;
name|adders
index|[
name|i
index|]
operator|=
operator|new
name|CSVLoader
operator|.
name|FieldMapperSingle
argument_list|(
name|mapArgs
index|[
literal|0
index|]
argument_list|,
name|mapArgs
index|[
literal|1
index|]
argument_list|,
name|adders
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|params
operator|.
name|getFieldBool
argument_list|(
name|fname
argument_list|,
name|TRIM
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|adders
index|[
name|i
index|]
operator|=
operator|new
name|CSVLoader
operator|.
name|FieldTrimmer
argument_list|(
name|adders
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|params
operator|.
name|getFieldBool
argument_list|(
name|fname
argument_list|,
name|SPLIT
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|String
name|sepStr
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fname
argument_list|,
name|SEPARATOR
argument_list|)
decl_stmt|;
name|char
name|fsep
init|=
name|sepStr
operator|==
literal|null
operator|||
name|sepStr
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|','
else|:
name|sepStr
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|encStr
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fname
argument_list|,
name|ENCAPSULATOR
argument_list|)
decl_stmt|;
name|char
name|fenc
init|=
name|encStr
operator|==
literal|null
operator|||
name|encStr
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
operator|(
name|char
operator|)
operator|-
literal|2
else|:
name|encStr
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|escStr
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fname
argument_list|,
name|ESCAPE
argument_list|)
decl_stmt|;
name|char
name|fesc
init|=
name|escStr
operator|==
literal|null
operator|||
name|escStr
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
name|CSVStrategy
operator|.
name|ESCAPE_DISABLED
else|:
name|escStr
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|CSVStrategy
name|fstrat
init|=
operator|new
name|CSVStrategy
argument_list|(
name|fsep
argument_list|,
name|fenc
argument_list|,
name|CSVStrategy
operator|.
name|COMMENTS_DISABLED
argument_list|,
name|fesc
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|adders
index|[
name|i
index|]
operator|=
operator|new
name|CSVLoader
operator|.
name|FieldSplitter
argument_list|(
name|fstrat
argument_list|,
name|adders
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|input_err
specifier|private
name|void
name|input_err
parameter_list|(
name|String
name|msg
parameter_list|,
name|String
index|[]
name|line
parameter_list|,
name|int
name|lineno
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|errHeader
operator|+
literal|", line="
operator|+
name|lineno
operator|+
literal|","
operator|+
name|msg
operator|+
literal|"\n\tvalues={"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|val
range|:
name|line
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"'"
operator|+
name|val
operator|+
literal|"',"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
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
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
comment|/** load the CSV input */
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
name|IOException
block|{
name|errHeader
operator|=
literal|"CSVLoader: input="
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
name|skipLines
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|reader
operator|instanceof
name|BufferedReader
operator|)
condition|)
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|BufferedReader
name|r
init|=
operator|(
name|BufferedReader
operator|)
name|reader
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
name|skipLines
condition|;
name|i
operator|++
control|)
block|{
name|r
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
name|CSVParser
name|parser
init|=
operator|new
name|CSVParser
argument_list|(
name|reader
argument_list|,
name|strategy
argument_list|)
decl_stmt|;
comment|// parse the fieldnames from the header of the file
if|if
condition|(
name|fieldnames
operator|==
literal|null
condition|)
block|{
name|fieldnames
operator|=
name|parser
operator|.
name|getLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldnames
operator|==
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
name|BAD_REQUEST
argument_list|,
literal|"Expected fieldnames in CSV input"
argument_list|)
throw|;
block|}
name|prepareFields
argument_list|()
expr_stmt|;
block|}
comment|// read the rest of the CSV file
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|line
init|=
name|parser
operator|.
name|getLineNumber
argument_list|()
decl_stmt|;
comment|// for error reporting in MT mode
name|String
index|[]
name|vals
init|=
name|parser
operator|.
name|getLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|vals
operator|==
literal|null
condition|)
break|break;
if|if
condition|(
name|vals
operator|.
name|length
operator|!=
name|fields
operator|.
name|length
condition|)
block|{
name|input_err
argument_list|(
literal|"expected "
operator|+
name|fields
operator|.
name|length
operator|+
literal|" values but got "
operator|+
name|vals
operator|.
name|length
argument_list|,
name|vals
argument_list|,
name|line
argument_list|)
expr_stmt|;
block|}
name|addDoc
argument_list|(
name|line
argument_list|,
name|vals
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
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
block|}
comment|/** called for each line of values (document) */
DECL|method|addDoc
specifier|abstract
name|void
name|addDoc
parameter_list|(
name|int
name|line
parameter_list|,
name|String
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** this must be MT safe... may be called concurrently from multiple threads. */
DECL|method|doAdd
name|void
name|doAdd
parameter_list|(
name|int
name|line
parameter_list|,
name|String
index|[]
name|vals
parameter_list|,
name|SolrInputDocument
name|doc
parameter_list|,
name|AddUpdateCommand
name|template
parameter_list|)
throws|throws
name|IOException
block|{
comment|// the line number is passed simply for error reporting in MT mode.
comment|// first, create the lucene document
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|==
literal|null
condition|)
continue|continue;
comment|// ignore this field
name|String
name|val
init|=
name|vals
index|[
name|i
index|]
decl_stmt|;
name|adders
index|[
name|i
index|]
operator|.
name|add
argument_list|(
name|doc
argument_list|,
name|line
argument_list|,
name|i
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
name|template
operator|.
name|solrDoc
operator|=
name|doc
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|template
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_class
DECL|class|SingleThreadedCSVLoader
class|class
name|SingleThreadedCSVLoader
extends|extends
name|CSVLoader
block|{
DECL|method|SingleThreadedCSVLoader
name|SingleThreadedCSVLoader
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|)
block|{
name|super
argument_list|(
name|req
argument_list|,
name|processor
argument_list|)
expr_stmt|;
block|}
DECL|method|addDoc
name|void
name|addDoc
parameter_list|(
name|int
name|line
parameter_list|,
name|String
index|[]
name|vals
parameter_list|)
throws|throws
name|IOException
block|{
name|templateAdd
operator|.
name|indexedId
operator|=
literal|null
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doAdd
argument_list|(
name|line
argument_list|,
name|vals
argument_list|,
name|doc
argument_list|,
name|templateAdd
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
