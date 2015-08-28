begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
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
name|internal
operator|.
name|csv
operator|.
name|CSVPrinter
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
name|internal
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
name|lucene
operator|.
name|index
operator|.
name|IndexableField
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
name|SolrDocument
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
name|SolrDocumentList
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
name|DateUtil
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
name|FastWriter
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|StrField
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
name|search
operator|.
name|DocList
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
name|search
operator|.
name|ReturnFields
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
comment|/**  *  */
end_comment
begin_class
DECL|class|CSVResponseWriter
specifier|public
class|class
name|CSVResponseWriter
implements|implements
name|QueryResponseWriter
block|{
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|n
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|CSVWriter
name|w
init|=
operator|new
name|CSVWriter
argument_list|(
name|writer
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
try|try
block|{
name|w
operator|.
name|writeResponse
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
comment|// using the text/plain allows this to be viewed in the browser easily
return|return
name|CONTENT_TYPE_TEXT_UTF8
return|;
block|}
block|}
end_class
begin_class
DECL|class|CSVWriter
class|class
name|CSVWriter
extends|extends
name|TextResponseWriter
block|{
DECL|field|SEPARATOR
specifier|static
name|String
name|SEPARATOR
init|=
literal|"separator"
decl_stmt|;
DECL|field|ENCAPSULATOR
specifier|static
name|String
name|ENCAPSULATOR
init|=
literal|"encapsulator"
decl_stmt|;
DECL|field|ESCAPE
specifier|static
name|String
name|ESCAPE
init|=
literal|"escape"
decl_stmt|;
DECL|field|CSV
specifier|static
name|String
name|CSV
init|=
literal|"csv."
decl_stmt|;
DECL|field|CSV_SEPARATOR
specifier|static
name|String
name|CSV_SEPARATOR
init|=
name|CSV
operator|+
name|SEPARATOR
decl_stmt|;
DECL|field|CSV_ENCAPSULATOR
specifier|static
name|String
name|CSV_ENCAPSULATOR
init|=
name|CSV
operator|+
name|ENCAPSULATOR
decl_stmt|;
DECL|field|CSV_ESCAPE
specifier|static
name|String
name|CSV_ESCAPE
init|=
name|CSV
operator|+
name|ESCAPE
decl_stmt|;
DECL|field|MV
specifier|static
name|String
name|MV
init|=
name|CSV
operator|+
literal|"mv."
decl_stmt|;
DECL|field|MV_SEPARATOR
specifier|static
name|String
name|MV_SEPARATOR
init|=
name|MV
operator|+
name|SEPARATOR
decl_stmt|;
DECL|field|MV_ENCAPSULATOR
specifier|static
name|String
name|MV_ENCAPSULATOR
init|=
name|MV
operator|+
name|ENCAPSULATOR
decl_stmt|;
DECL|field|MV_ESCAPE
specifier|static
name|String
name|MV_ESCAPE
init|=
name|MV
operator|+
name|ESCAPE
decl_stmt|;
DECL|field|CSV_NULL
specifier|static
name|String
name|CSV_NULL
init|=
name|CSV
operator|+
literal|"null"
decl_stmt|;
DECL|field|CSV_HEADER
specifier|static
name|String
name|CSV_HEADER
init|=
name|CSV
operator|+
literal|"header"
decl_stmt|;
DECL|field|CSV_NEWLINE
specifier|static
name|String
name|CSV_NEWLINE
init|=
name|CSV
operator|+
literal|"newline"
decl_stmt|;
DECL|field|sharedCSVBuf
name|char
index|[]
name|sharedCSVBuf
init|=
operator|new
name|char
index|[
literal|8192
index|]
decl_stmt|;
comment|// prevent each instance from creating its own buffer
DECL|class|CSVSharedBufPrinter
class|class
name|CSVSharedBufPrinter
extends|extends
name|CSVPrinter
block|{
DECL|method|CSVSharedBufPrinter
specifier|public
name|CSVSharedBufPrinter
parameter_list|(
name|Writer
name|out
parameter_list|,
name|CSVStrategy
name|strategy
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|,
name|strategy
argument_list|)
expr_stmt|;
name|super
operator|.
name|buf
operator|=
name|sharedCSVBuf
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|super
operator|.
name|newLine
operator|=
literal|true
expr_stmt|;
comment|// update our shared buf in case a new bigger one was allocated
name|sharedCSVBuf
operator|=
name|super
operator|.
name|buf
expr_stmt|;
block|}
block|}
comment|// allows access to internal buf w/o copying it
DECL|class|OpenCharArrayWriter
specifier|static
class|class
name|OpenCharArrayWriter
extends|extends
name|CharArrayWriter
block|{
DECL|method|getInternalBuf
specifier|public
name|char
index|[]
name|getInternalBuf
parameter_list|()
block|{
return|return
name|buf
return|;
block|}
block|}
comment|// Writes all data to a char array,
comment|// allows access to internal buffer, and allows fast resetting.
DECL|class|ResettableFastWriter
specifier|static
class|class
name|ResettableFastWriter
extends|extends
name|FastWriter
block|{
DECL|field|cw
name|OpenCharArrayWriter
name|cw
init|=
operator|new
name|OpenCharArrayWriter
argument_list|()
decl_stmt|;
DECL|field|result
name|char
index|[]
name|result
decl_stmt|;
DECL|field|resultLen
name|int
name|resultLen
decl_stmt|;
DECL|method|ResettableFastWriter
specifier|public
name|ResettableFastWriter
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|OpenCharArrayWriter
argument_list|()
argument_list|)
expr_stmt|;
name|cw
operator|=
operator|(
name|OpenCharArrayWriter
operator|)
name|sink
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|cw
operator|.
name|reset
argument_list|()
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|freeze
specifier|public
name|void
name|freeze
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cw
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
name|result
operator|=
name|cw
operator|.
name|getInternalBuf
argument_list|()
expr_stmt|;
name|resultLen
operator|=
name|cw
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|buf
expr_stmt|;
name|resultLen
operator|=
name|pos
expr_stmt|;
block|}
block|}
DECL|method|getFrozenSize
specifier|public
name|int
name|getFrozenSize
parameter_list|()
block|{
return|return
name|resultLen
return|;
block|}
DECL|method|getFrozenBuf
specifier|public
name|char
index|[]
name|getFrozenBuf
parameter_list|()
block|{
return|return
name|result
return|;
block|}
block|}
DECL|class|CSVField
specifier|static
class|class
name|CSVField
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|sf
name|SchemaField
name|sf
decl_stmt|;
DECL|field|mvPrinter
name|CSVSharedBufPrinter
name|mvPrinter
decl_stmt|;
comment|// printer used to encode multiple values in a single CSV value
comment|// used to collect values
DECL|field|values
name|List
argument_list|<
name|IndexableField
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// low starting amount in case there are many fields
DECL|field|tmp
name|int
name|tmp
decl_stmt|;
block|}
DECL|field|pass
name|int
name|pass
decl_stmt|;
DECL|field|csvFields
name|Map
argument_list|<
name|String
argument_list|,
name|CSVField
argument_list|>
name|csvFields
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|cal
name|Calendar
name|cal
decl_stmt|;
comment|// for formatting date objects
DECL|field|strategy
name|CSVStrategy
name|strategy
decl_stmt|;
comment|// strategy for encoding the fields of documents
DECL|field|printer
name|CSVPrinter
name|printer
decl_stmt|;
DECL|field|mvWriter
name|ResettableFastWriter
name|mvWriter
init|=
operator|new
name|ResettableFastWriter
argument_list|()
decl_stmt|;
comment|// writer used for multi-valued fields
DECL|field|NullValue
name|String
name|NullValue
decl_stmt|;
DECL|method|CSVWriter
specifier|public
name|CSVWriter
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|super
argument_list|(
name|writer
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|writeResponse
specifier|public
name|void
name|writeResponse
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
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
name|CSVStrategy
name|strat
init|=
name|strategy
decl_stmt|;
name|String
name|sep
init|=
name|params
operator|.
name|get
argument_list|(
name|CSV_SEPARATOR
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
name|strat
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
name|nl
init|=
name|params
operator|.
name|get
argument_list|(
name|CSV_NEWLINE
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nl
operator|.
name|length
argument_list|()
operator|==
literal|0
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
literal|"Invalid newline:'"
operator|+
name|nl
operator|+
literal|"'"
argument_list|)
throw|;
name|strat
operator|.
name|setPrinterNewline
argument_list|(
name|nl
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
name|CSV_ENCAPSULATOR
argument_list|)
decl_stmt|;
name|String
name|escape
init|=
name|params
operator|.
name|get
argument_list|(
name|CSV_ESCAPE
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
name|strat
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
name|strat
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
if|if
condition|(
name|encapsulator
operator|==
literal|null
condition|)
block|{
name|strat
operator|.
name|setEncapsulator
argument_list|(
name|CSVStrategy
operator|.
name|ENCAPSULATOR_DISABLED
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|strat
operator|.
name|getEscape
argument_list|()
operator|==
literal|'\\'
condition|)
block|{
comment|// If the escape is the standard backslash, then also enable
comment|// unicode escapes (it's harmless since 'u' would not otherwise
comment|// be escaped.
name|strat
operator|.
name|setUnicodeEscapeInterpretation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|printer
operator|=
operator|new
name|CSVPrinter
argument_list|(
name|writer
argument_list|,
name|strategy
argument_list|)
expr_stmt|;
name|CSVStrategy
name|mvStrategy
init|=
operator|new
name|CSVStrategy
argument_list|(
name|strategy
operator|.
name|getDelimiter
argument_list|()
argument_list|,
name|CSVStrategy
operator|.
name|ENCAPSULATOR_DISABLED
argument_list|,
name|CSVStrategy
operator|.
name|COMMENTS_DISABLED
argument_list|,
literal|'\\'
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
name|strat
operator|=
name|mvStrategy
expr_stmt|;
name|sep
operator|=
name|params
operator|.
name|get
argument_list|(
name|MV_SEPARATOR
argument_list|)
expr_stmt|;
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
literal|"Invalid mv separator:'"
operator|+
name|sep
operator|+
literal|"'"
argument_list|)
throw|;
name|strat
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
name|encapsulator
operator|=
name|params
operator|.
name|get
argument_list|(
name|MV_ENCAPSULATOR
argument_list|)
expr_stmt|;
name|escape
operator|=
name|params
operator|.
name|get
argument_list|(
name|MV_ESCAPE
argument_list|)
expr_stmt|;
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
literal|"Invalid mv encapsulator:'"
operator|+
name|encapsulator
operator|+
literal|"'"
argument_list|)
throw|;
name|strat
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
if|if
condition|(
name|escape
operator|==
literal|null
condition|)
block|{
name|strat
operator|.
name|setEscape
argument_list|(
name|CSVStrategy
operator|.
name|ESCAPE_DISABLED
argument_list|)
expr_stmt|;
block|}
block|}
name|escape
operator|=
name|params
operator|.
name|get
argument_list|(
name|MV_ESCAPE
argument_list|)
expr_stmt|;
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
literal|"Invalid mv escape:'"
operator|+
name|escape
operator|+
literal|"'"
argument_list|)
throw|;
name|strat
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
comment|// encapsulator will already be disabled if it wasn't specified
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|returnFields
operator|.
name|getRequestedFieldNames
argument_list|()
decl_stmt|;
name|Object
name|responseObj
init|=
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
decl_stmt|;
name|boolean
name|returnOnlyStored
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|returnFields
operator|.
name|hasPatternMatching
argument_list|()
condition|)
block|{
if|if
condition|(
name|responseObj
operator|instanceof
name|SolrDocumentList
condition|)
block|{
comment|// get the list of fields from the SolrDocumentList
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|SolrDocument
name|sdoc
range|:
operator|(
name|SolrDocumentList
operator|)
name|responseObj
control|)
block|{
name|fields
operator|.
name|addAll
argument_list|(
name|sdoc
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// get the list of fields from the index
name|Collection
argument_list|<
name|String
argument_list|>
name|all
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|all
expr_stmt|;
block|}
else|else
block|{
name|fields
operator|.
name|addAll
argument_list|(
name|all
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|returnFields
operator|.
name|wantsScore
argument_list|()
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
literal|"score"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fields
operator|.
name|remove
argument_list|(
literal|"score"
argument_list|)
expr_stmt|;
block|}
name|returnOnlyStored
operator|=
literal|true
expr_stmt|;
block|}
name|CSVSharedBufPrinter
name|csvPrinterMV
init|=
operator|new
name|CSVSharedBufPrinter
argument_list|(
name|mvWriter
argument_list|,
name|mvStrategy
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
operator|!
name|returnFields
operator|.
name|wantsField
argument_list|(
name|field
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"score"
argument_list|)
condition|)
block|{
name|CSVField
name|csvField
init|=
operator|new
name|CSVField
argument_list|()
decl_stmt|;
name|csvField
operator|.
name|name
operator|=
literal|"score"
expr_stmt|;
name|csvFields
operator|.
name|put
argument_list|(
literal|"score"
argument_list|,
name|csvField
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|==
literal|null
condition|)
block|{
name|FieldType
name|ft
init|=
operator|new
name|StrField
argument_list|()
decl_stmt|;
name|sf
operator|=
operator|new
name|SchemaField
argument_list|(
name|field
argument_list|,
name|ft
argument_list|)
expr_stmt|;
block|}
comment|// Return only stored fields, unless an explicit field list is specified
if|if
condition|(
name|returnOnlyStored
operator|&&
name|sf
operator|!=
literal|null
operator|&&
operator|!
name|sf
operator|.
name|stored
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// check for per-field overrides
name|sep
operator|=
name|params
operator|.
name|get
argument_list|(
literal|"f."
operator|+
name|field
operator|+
literal|'.'
operator|+
name|CSV_SEPARATOR
argument_list|)
expr_stmt|;
name|encapsulator
operator|=
name|params
operator|.
name|get
argument_list|(
literal|"f."
operator|+
name|field
operator|+
literal|'.'
operator|+
name|CSV_ENCAPSULATOR
argument_list|)
expr_stmt|;
name|escape
operator|=
name|params
operator|.
name|get
argument_list|(
literal|"f."
operator|+
name|field
operator|+
literal|'.'
operator|+
name|CSV_ESCAPE
argument_list|)
expr_stmt|;
comment|// if polyfield and no escape is provided, add "\\" escape by default
if|if
condition|(
name|sf
operator|.
name|isPolyField
argument_list|()
condition|)
block|{
name|escape
operator|=
operator|(
name|escape
operator|==
literal|null
operator|)
condition|?
literal|"\\"
else|:
name|escape
expr_stmt|;
block|}
name|CSVSharedBufPrinter
name|csvPrinter
init|=
name|csvPrinterMV
decl_stmt|;
if|if
condition|(
name|sep
operator|!=
literal|null
operator|||
name|encapsulator
operator|!=
literal|null
operator|||
name|escape
operator|!=
literal|null
condition|)
block|{
comment|// create a new strategy + printer if there were any per-field overrides
name|strat
operator|=
operator|(
name|CSVStrategy
operator|)
name|mvStrategy
operator|.
name|clone
argument_list|()
expr_stmt|;
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
literal|"Invalid mv separator:'"
operator|+
name|sep
operator|+
literal|"'"
argument_list|)
throw|;
name|strat
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
literal|"Invalid mv encapsulator:'"
operator|+
name|encapsulator
operator|+
literal|"'"
argument_list|)
throw|;
name|strat
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
if|if
condition|(
name|escape
operator|==
literal|null
condition|)
block|{
name|strat
operator|.
name|setEscape
argument_list|(
name|CSVStrategy
operator|.
name|ESCAPE_DISABLED
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Invalid mv escape:'"
operator|+
name|escape
operator|+
literal|"'"
argument_list|)
throw|;
name|strat
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
if|if
condition|(
name|encapsulator
operator|==
literal|null
condition|)
block|{
name|strat
operator|.
name|setEncapsulator
argument_list|(
name|CSVStrategy
operator|.
name|ENCAPSULATOR_DISABLED
argument_list|)
expr_stmt|;
block|}
block|}
name|csvPrinter
operator|=
operator|new
name|CSVSharedBufPrinter
argument_list|(
name|mvWriter
argument_list|,
name|strat
argument_list|)
expr_stmt|;
block|}
name|CSVField
name|csvField
init|=
operator|new
name|CSVField
argument_list|()
decl_stmt|;
name|csvField
operator|.
name|name
operator|=
name|field
expr_stmt|;
name|csvField
operator|.
name|sf
operator|=
name|sf
expr_stmt|;
name|csvField
operator|.
name|mvPrinter
operator|=
name|csvPrinter
expr_stmt|;
name|csvFields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|csvField
argument_list|)
expr_stmt|;
block|}
name|NullValue
operator|=
name|params
operator|.
name|get
argument_list|(
name|CSV_NULL
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|CSV_HEADER
argument_list|,
literal|true
argument_list|)
condition|)
block|{
for|for
control|(
name|CSVField
name|csvField
range|:
name|csvFields
operator|.
name|values
argument_list|()
control|)
block|{
name|printer
operator|.
name|print
argument_list|(
name|csvField
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
name|printer
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|responseObj
operator|instanceof
name|ResultContext
condition|)
block|{
name|writeDocuments
argument_list|(
literal|null
argument_list|,
operator|(
name|ResultContext
operator|)
name|responseObj
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|responseObj
operator|instanceof
name|DocList
condition|)
block|{
name|ResultContext
name|ctx
init|=
operator|new
name|BasicResultContext
argument_list|(
operator|(
name|DocList
operator|)
name|responseObj
argument_list|,
name|returnFields
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|writeDocuments
argument_list|(
literal|null
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|responseObj
operator|instanceof
name|SolrDocumentList
condition|)
block|{
name|writeSolrDocumentList
argument_list|(
literal|null
argument_list|,
operator|(
name|SolrDocumentList
operator|)
name|responseObj
argument_list|,
name|returnFields
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|printer
operator|!=
literal|null
condition|)
name|printer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNamedList
specifier|public
name|void
name|writeNamedList
parameter_list|(
name|String
name|name
parameter_list|,
name|NamedList
name|val
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|writeStartDocumentList
specifier|public
name|void
name|writeStartDocumentList
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|start
parameter_list|,
name|int
name|size
parameter_list|,
name|long
name|numFound
parameter_list|,
name|Float
name|maxScore
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nothing
block|}
annotation|@
name|Override
DECL|method|writeEndDocumentList
specifier|public
name|void
name|writeEndDocumentList
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing
block|}
comment|//NOTE: a document cannot currently contain another document
DECL|field|tmpList
name|List
name|tmpList
decl_stmt|;
annotation|@
name|Override
DECL|method|writeSolrDocument
specifier|public
name|void
name|writeSolrDocument
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrDocument
name|doc
parameter_list|,
name|ReturnFields
name|returnFields
parameter_list|,
name|int
name|idx
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|tmpList
operator|==
literal|null
condition|)
block|{
name|tmpList
operator|=
operator|new
name|ArrayList
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|tmpList
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|CSVField
name|csvField
range|:
name|csvFields
operator|.
name|values
argument_list|()
control|)
block|{
name|Object
name|val
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
name|csvField
operator|.
name|name
argument_list|)
decl_stmt|;
name|int
name|nVals
init|=
name|val
operator|instanceof
name|Collection
condition|?
operator|(
operator|(
name|Collection
operator|)
name|val
operator|)
operator|.
name|size
argument_list|()
else|:
operator|(
name|val
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|nVals
operator|==
literal|0
condition|)
block|{
name|writeNull
argument_list|(
name|csvField
operator|.
name|name
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|(
name|csvField
operator|.
name|sf
operator|!=
literal|null
operator|&&
name|csvField
operator|.
name|sf
operator|.
name|multiValued
argument_list|()
operator|)
operator|||
name|nVals
operator|>
literal|1
condition|)
block|{
name|Collection
name|values
decl_stmt|;
comment|// normalize to a collection
if|if
condition|(
name|val
operator|instanceof
name|Collection
condition|)
block|{
name|values
operator|=
operator|(
name|Collection
operator|)
name|val
expr_stmt|;
block|}
else|else
block|{
name|tmpList
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|values
operator|=
name|tmpList
expr_stmt|;
block|}
name|mvWriter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|csvField
operator|.
name|mvPrinter
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// switch the printer to use the multi-valued one
name|CSVPrinter
name|tmp
init|=
name|printer
decl_stmt|;
name|printer
operator|=
name|csvField
operator|.
name|mvPrinter
expr_stmt|;
for|for
control|(
name|Object
name|fval
range|:
name|values
control|)
block|{
name|writeVal
argument_list|(
name|csvField
operator|.
name|name
argument_list|,
name|fval
argument_list|)
expr_stmt|;
block|}
name|printer
operator|=
name|tmp
expr_stmt|;
comment|// restore the original printer
name|mvWriter
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|printer
operator|.
name|print
argument_list|(
name|mvWriter
operator|.
name|getFrozenBuf
argument_list|()
argument_list|,
literal|0
argument_list|,
name|mvWriter
operator|.
name|getFrozenSize
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// normalize to first value
if|if
condition|(
name|val
operator|instanceof
name|Collection
condition|)
block|{
name|Collection
name|values
init|=
operator|(
name|Collection
operator|)
name|val
decl_stmt|;
name|val
operator|=
name|values
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
comment|// if field is polyfield, use the multi-valued printer to apply appropriate escaping
if|if
condition|(
name|csvField
operator|.
name|sf
operator|!=
literal|null
operator|&&
name|csvField
operator|.
name|sf
operator|.
name|isPolyField
argument_list|()
condition|)
block|{
name|mvWriter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|csvField
operator|.
name|mvPrinter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|CSVPrinter
name|tmp
init|=
name|printer
decl_stmt|;
name|printer
operator|=
name|csvField
operator|.
name|mvPrinter
expr_stmt|;
name|writeVal
argument_list|(
name|csvField
operator|.
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|printer
operator|=
name|tmp
expr_stmt|;
name|mvWriter
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|printer
operator|.
name|print
argument_list|(
name|mvWriter
operator|.
name|getFrozenBuf
argument_list|()
argument_list|,
literal|0
argument_list|,
name|mvWriter
operator|.
name|getFrozenSize
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeVal
argument_list|(
name|csvField
operator|.
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|printer
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeStr
specifier|public
name|void
name|writeStr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
block|{
name|printer
operator|.
name|print
argument_list|(
name|val
argument_list|,
name|needsEscaping
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeMap
specifier|public
name|void
name|writeMap
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
name|val
parameter_list|,
name|boolean
name|excludeOuter
parameter_list|,
name|boolean
name|isFirstVal
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|writeArray
specifier|public
name|void
name|writeArray
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterator
name|val
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|writeNull
specifier|public
name|void
name|writeNull
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|printer
operator|.
name|print
argument_list|(
name|NullValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|printer
operator|.
name|print
argument_list|(
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeLong
specifier|public
name|void
name|writeLong
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|printer
operator|.
name|print
argument_list|(
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBool
specifier|public
name|void
name|writeBool
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|printer
operator|.
name|print
argument_list|(
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeFloat
specifier|public
name|void
name|writeFloat
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|printer
operator|.
name|print
argument_list|(
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDouble
specifier|public
name|void
name|writeDouble
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|printer
operator|.
name|print
argument_list|(
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDate
specifier|public
name|void
name|writeDate
parameter_list|(
name|String
name|name
parameter_list|,
name|Date
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|25
argument_list|)
decl_stmt|;
name|cal
operator|=
name|DateUtil
operator|.
name|formatDate
argument_list|(
name|val
argument_list|,
name|cal
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|writeDate
argument_list|(
name|name
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDate
specifier|public
name|void
name|writeDate
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|printer
operator|.
name|print
argument_list|(
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
