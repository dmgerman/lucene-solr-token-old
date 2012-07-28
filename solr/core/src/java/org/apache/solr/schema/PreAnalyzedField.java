begin_unit
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|IOException
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|Tokenizer
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
name|document
operator|.
name|Field
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
name|GeneralField
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
name|lucene
operator|.
name|search
operator|.
name|SortField
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
name|util
operator|.
name|AttributeSource
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
name|util
operator|.
name|AttributeSource
operator|.
name|State
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
name|analysis
operator|.
name|SolrAnalyzer
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
name|TextResponseWriter
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
begin_comment
comment|/**  * Pre-analyzed field type provides a way to index a serialized token stream,  * optionally with an independent stored value of a field.  */
end_comment
begin_class
DECL|class|PreAnalyzedField
specifier|public
class|class
name|PreAnalyzedField
extends|extends
name|FieldType
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
name|PreAnalyzedField
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Init argument name. Value is a fully-qualified class name of the parser    * that implements {@link PreAnalyzedParser}.    */
DECL|field|PARSER_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|PARSER_IMPL
init|=
literal|"parserImpl"
decl_stmt|;
DECL|field|DEFAULT_IMPL
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_IMPL
init|=
name|JsonPreAnalyzedParser
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|parser
specifier|private
name|PreAnalyzedParser
name|parser
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
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
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|String
name|implName
init|=
name|args
operator|.
name|get
argument_list|(
name|PARSER_IMPL
argument_list|)
decl_stmt|;
if|if
condition|(
name|implName
operator|==
literal|null
condition|)
block|{
name|parser
operator|=
operator|new
name|JsonPreAnalyzedParser
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|implClazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|implName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|PreAnalyzedParser
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|implClazz
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"must implement "
operator|+
name|PreAnalyzedParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|Constructor
argument_list|<
name|?
argument_list|>
name|c
init|=
name|implClazz
operator|.
name|getConstructor
argument_list|(
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|parser
operator|=
operator|(
name|PreAnalyzedParser
operator|)
name|c
operator|.
name|newInstance
argument_list|(
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't use the configured PreAnalyzedParser class '"
operator|+
name|implName
operator|+
literal|"' ("
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"), using default "
operator|+
name|DEFAULT_IMPL
argument_list|)
expr_stmt|;
name|parser
operator|=
operator|new
name|JsonPreAnalyzedParser
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
operator|new
name|SolrAnalyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|PreAnalyzedTokenizer
argument_list|(
name|reader
argument_list|,
name|parser
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getQueryAnalyzer
specifier|public
name|Analyzer
name|getQueryAnalyzer
parameter_list|()
block|{
return|return
name|getAnalyzer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createField
specifier|public
name|IndexableField
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|value
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|IndexableField
name|f
init|=
literal|null
decl_stmt|;
try|try
block|{
name|f
operator|=
name|fromString
argument_list|(
name|field
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|f
return|;
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|top
parameter_list|)
block|{
return|return
name|getStringSort
argument_list|(
name|field
argument_list|,
name|top
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|GeneralField
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Utility method to convert a field to a string that is parse-able by this    * class.    * @param f field to convert    * @return string that is compatible with the serialization format    * @throws IOException    */
DECL|method|toFormattedString
specifier|public
name|String
name|toFormattedString
parameter_list|(
name|Field
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|toFormattedString
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/**    * This is a simple holder of a stored part and the collected states (tokens with attributes).    */
DECL|class|ParseResult
specifier|public
specifier|static
class|class
name|ParseResult
block|{
DECL|field|str
specifier|public
name|String
name|str
decl_stmt|;
DECL|field|bin
specifier|public
name|byte
index|[]
name|bin
decl_stmt|;
DECL|field|states
specifier|public
name|List
argument_list|<
name|State
argument_list|>
name|states
init|=
operator|new
name|LinkedList
argument_list|<
name|State
argument_list|>
argument_list|()
decl_stmt|;
block|}
comment|/**    * Parse the input and return the stored part and the tokens with attributes.    */
DECL|interface|PreAnalyzedParser
specifier|public
specifier|static
interface|interface
name|PreAnalyzedParser
block|{
comment|/**      * Parse input.      * @param reader input to read from      * @param parent parent who will own the resulting states (tokens with attributes)      * @return parse result, with possibly null stored and/or states fields.      * @throws IOException if a parsing error or IO error occurs      */
DECL|method|parse
specifier|public
name|ParseResult
name|parse
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|AttributeSource
name|parent
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Format a field so that the resulting String is valid for parsing with {@link #parse(Reader, AttributeSource)}.      * @param f field instance      * @return formatted string      * @throws IOException      */
DECL|method|toFormattedString
specifier|public
name|String
name|toFormattedString
parameter_list|(
name|Field
name|f
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|method|fromString
specifier|public
name|IndexableField
name|fromString
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|val
operator|==
literal|null
operator|||
name|val
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PreAnalyzedTokenizer
name|parse
init|=
operator|new
name|PreAnalyzedTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|val
argument_list|)
argument_list|,
name|parser
argument_list|)
decl_stmt|;
name|Field
name|f
init|=
operator|(
name|Field
operator|)
name|super
operator|.
name|createField
argument_list|(
name|field
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
decl_stmt|;
if|if
condition|(
name|parse
operator|.
name|getStringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|f
operator|.
name|setStringValue
argument_list|(
name|parse
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parse
operator|.
name|getBinaryValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|f
operator|.
name|setBytesValue
argument_list|(
name|parse
operator|.
name|getBinaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|setStored
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parse
operator|.
name|hasTokenStream
argument_list|()
condition|)
block|{
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|setTokenStream
argument_list|(
name|parse
argument_list|)
expr_stmt|;
block|}
return|return
name|f
return|;
block|}
comment|/**    * Token stream that works from a list of saved states.    */
DECL|class|PreAnalyzedTokenizer
specifier|private
specifier|static
class|class
name|PreAnalyzedTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|cachedStates
specifier|private
specifier|final
name|List
argument_list|<
name|AttributeSource
operator|.
name|State
argument_list|>
name|cachedStates
init|=
operator|new
name|LinkedList
argument_list|<
name|AttributeSource
operator|.
name|State
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|it
specifier|private
name|Iterator
argument_list|<
name|AttributeSource
operator|.
name|State
argument_list|>
name|it
init|=
literal|null
decl_stmt|;
DECL|field|stringValue
specifier|private
name|String
name|stringValue
init|=
literal|null
decl_stmt|;
DECL|field|binaryValue
specifier|private
name|byte
index|[]
name|binaryValue
init|=
literal|null
decl_stmt|;
DECL|field|parser
specifier|private
name|PreAnalyzedParser
name|parser
decl_stmt|;
DECL|method|PreAnalyzedTokenizer
specifier|public
name|PreAnalyzedTokenizer
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|PreAnalyzedParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|hasTokenStream
specifier|public
name|boolean
name|hasTokenStream
parameter_list|()
block|{
return|return
operator|!
name|cachedStates
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|getStringValue
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
return|return
name|stringValue
return|;
block|}
DECL|method|getBinaryValue
specifier|public
name|byte
index|[]
name|getBinaryValue
parameter_list|()
block|{
return|return
name|binaryValue
return|;
block|}
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
block|{
comment|// lazy init the iterator
if|if
condition|(
name|it
operator|==
literal|null
condition|)
block|{
name|it
operator|=
name|cachedStates
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AttributeSource
operator|.
name|State
name|state
init|=
operator|(
name|State
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|restoreState
argument_list|(
name|state
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|reset
specifier|public
specifier|final
name|void
name|reset
parameter_list|()
block|{
name|it
operator|=
name|cachedStates
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setReader
specifier|public
name|void
name|setReader
parameter_list|(
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setReader
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|cachedStates
operator|.
name|clear
argument_list|()
expr_stmt|;
name|stringValue
operator|=
literal|null
expr_stmt|;
name|binaryValue
operator|=
literal|null
expr_stmt|;
name|ParseResult
name|res
init|=
name|parser
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
name|stringValue
operator|=
name|res
operator|.
name|str
expr_stmt|;
name|binaryValue
operator|=
name|res
operator|.
name|bin
expr_stmt|;
if|if
condition|(
name|res
operator|.
name|states
operator|!=
literal|null
condition|)
block|{
name|cachedStates
operator|.
name|addAll
argument_list|(
name|res
operator|.
name|states
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
