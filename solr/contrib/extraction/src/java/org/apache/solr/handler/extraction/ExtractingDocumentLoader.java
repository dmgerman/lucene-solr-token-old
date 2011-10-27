begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.extraction
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|extraction
package|;
end_package
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
name|io
operator|.
name|StringWriter
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
name|handler
operator|.
name|ContentStreamLoader
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
name|tika
operator|.
name|config
operator|.
name|TikaConfig
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|exception
operator|.
name|TikaException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
operator|.
name|MediaType
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|AutoDetectParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|ParseContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|Parser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|XHTMLContentHandler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|xpath
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|xpath
operator|.
name|MatchingContentHandler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|xpath
operator|.
name|XPathParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|BaseMarkupSerializer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|OutputFormat
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|TextSerializer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|XMLSerializer
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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
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
begin_comment
comment|/**  * The class responsible for loading extracted content into Solr.  *  **/
end_comment
begin_class
DECL|class|ExtractingDocumentLoader
specifier|public
class|class
name|ExtractingDocumentLoader
extends|extends
name|ContentStreamLoader
block|{
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
name|ExtractingDocumentLoader
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Extract Only supported format    */
DECL|field|TEXT_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FORMAT
init|=
literal|"text"
decl_stmt|;
comment|/**    * Extract Only supported format.  Default    */
DECL|field|XML_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|XML_FORMAT
init|=
literal|"xml"
decl_stmt|;
comment|/**    * XHTML XPath parser.    */
DECL|field|PARSER
specifier|private
specifier|static
specifier|final
name|XPathParser
name|PARSER
init|=
operator|new
name|XPathParser
argument_list|(
literal|"xhtml"
argument_list|,
name|XHTMLContentHandler
operator|.
name|XHTML
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
DECL|field|processor
specifier|final
name|UpdateRequestProcessor
name|processor
decl_stmt|;
DECL|field|ignoreTikaException
specifier|final
name|boolean
name|ignoreTikaException
decl_stmt|;
DECL|field|autoDetectParser
specifier|protected
name|AutoDetectParser
name|autoDetectParser
decl_stmt|;
DECL|field|templateAdd
specifier|private
specifier|final
name|AddUpdateCommand
name|templateAdd
decl_stmt|;
DECL|field|config
specifier|protected
name|TikaConfig
name|config
decl_stmt|;
DECL|field|factory
specifier|protected
name|SolrContentHandlerFactory
name|factory
decl_stmt|;
comment|//protected Collection<String> dateFormats = DateUtil.DEFAULT_DATE_FORMATS;
DECL|method|ExtractingDocumentLoader
specifier|public
name|ExtractingDocumentLoader
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|TikaConfig
name|config
parameter_list|,
name|SolrContentHandlerFactory
name|factory
parameter_list|)
block|{
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
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|processor
operator|=
name|processor
expr_stmt|;
name|templateAdd
operator|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|templateAdd
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
name|templateAdd
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
comment|//this is lightweight
name|autoDetectParser
operator|=
operator|new
name|AutoDetectParser
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|ignoreTikaException
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|ExtractingParams
operator|.
name|IGNORE_TIKA_EXCEPTION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * this must be MT safe... may be called concurrently from multiple threads.    *    * @param    * @param    */
DECL|method|doAdd
name|void
name|doAdd
parameter_list|(
name|SolrContentHandler
name|handler
parameter_list|,
name|AddUpdateCommand
name|template
parameter_list|)
throws|throws
name|IOException
block|{
name|template
operator|.
name|solrDoc
operator|=
name|handler
operator|.
name|newDocument
argument_list|()
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|template
argument_list|)
expr_stmt|;
block|}
DECL|method|addDoc
name|void
name|addDoc
parameter_list|(
name|SolrContentHandler
name|handler
parameter_list|)
throws|throws
name|IOException
block|{
name|templateAdd
operator|.
name|clear
argument_list|()
expr_stmt|;
name|doAdd
argument_list|(
name|handler
argument_list|,
name|templateAdd
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param req    * @param stream    * @throws java.io.IOException    */
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
name|IOException
block|{
name|errHeader
operator|=
literal|"ExtractingDocumentLoader: "
operator|+
name|stream
operator|.
name|getSourceInfo
argument_list|()
expr_stmt|;
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|String
name|streamType
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|ExtractingParams
operator|.
name|STREAM_TYPE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|streamType
operator|!=
literal|null
condition|)
block|{
comment|//Cache?  Parsers are lightweight to construct and thread-safe, so I'm told
name|MediaType
name|mt
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|streamType
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|=
name|config
operator|.
name|getParser
argument_list|(
name|mt
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parser
operator|=
name|autoDetectParser
expr_stmt|;
block|}
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
comment|// If you specify the resource name (the filename, roughly) with this parameter,
comment|// then Tika can make use of it in guessing the appropriate MIME type:
name|String
name|resourceName
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|ExtractingParams
operator|.
name|RESOURCE_NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|resourceName
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|resourceName
argument_list|)
expr_stmt|;
block|}
name|InputStream
name|inputStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|inputStream
operator|=
name|stream
operator|.
name|getStream
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|ExtractingMetadataConstants
operator|.
name|STREAM_NAME
argument_list|,
name|stream
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|ExtractingMetadataConstants
operator|.
name|STREAM_SOURCE_INFO
argument_list|,
name|stream
operator|.
name|getSourceInfo
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|ExtractingMetadataConstants
operator|.
name|STREAM_SIZE
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|stream
operator|.
name|getSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|ExtractingMetadataConstants
operator|.
name|STREAM_CONTENT_TYPE
argument_list|,
name|stream
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|xpathExpr
init|=
name|params
operator|.
name|get
argument_list|(
name|ExtractingParams
operator|.
name|XPATH_EXPRESSION
argument_list|)
decl_stmt|;
name|boolean
name|extractOnly
init|=
name|params
operator|.
name|getBool
argument_list|(
name|ExtractingParams
operator|.
name|EXTRACT_ONLY
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SolrContentHandler
name|handler
init|=
name|factory
operator|.
name|createSolrContentHandler
argument_list|(
name|metadata
argument_list|,
name|params
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|ContentHandler
name|parsingHandler
init|=
name|handler
decl_stmt|;
name|StringWriter
name|writer
init|=
literal|null
decl_stmt|;
name|BaseMarkupSerializer
name|serializer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|extractOnly
operator|==
literal|true
condition|)
block|{
name|String
name|extractFormat
init|=
name|params
operator|.
name|get
argument_list|(
name|ExtractingParams
operator|.
name|EXTRACT_FORMAT
argument_list|,
literal|"xml"
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
if|if
condition|(
name|extractFormat
operator|.
name|equals
argument_list|(
name|TEXT_FORMAT
argument_list|)
condition|)
block|{
name|serializer
operator|=
operator|new
name|TextSerializer
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|setOutputCharStream
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setOutputFormat
argument_list|(
operator|new
name|OutputFormat
argument_list|(
literal|"Text"
argument_list|,
literal|"UTF-8"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|serializer
operator|=
operator|new
name|XMLSerializer
argument_list|(
name|writer
argument_list|,
operator|new
name|OutputFormat
argument_list|(
literal|"XML"
argument_list|,
literal|"UTF-8"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|xpathExpr
operator|!=
literal|null
condition|)
block|{
name|Matcher
name|matcher
init|=
name|PARSER
operator|.
name|parse
argument_list|(
name|xpathExpr
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
comment|//The MatchingContentHandler does not invoke startDocument.  See http://tika.markmail.org/message/kknu3hw7argwiqin
name|parsingHandler
operator|=
operator|new
name|MatchingContentHandler
argument_list|(
name|serializer
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parsingHandler
operator|=
name|serializer
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|xpathExpr
operator|!=
literal|null
condition|)
block|{
name|Matcher
name|matcher
init|=
name|PARSER
operator|.
name|parse
argument_list|(
name|xpathExpr
argument_list|)
decl_stmt|;
name|parsingHandler
operator|=
operator|new
name|MatchingContentHandler
argument_list|(
name|handler
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
comment|//else leave it as is
try|try
block|{
comment|//potentially use a wrapper handler for parsing, but we still need the SolrContentHandler for getting the document.
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
comment|//TODO: should we design a way to pass in parse context?
name|parser
operator|.
name|parse
argument_list|(
name|inputStream
argument_list|,
name|parsingHandler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
if|if
condition|(
name|ignoreTikaException
condition|)
name|log
operator|.
name|warn
argument_list|(
operator|new
name|StringBuilder
argument_list|(
literal|"skip extracting text due to "
argument_list|)
operator|.
name|append
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|". metadata="
argument_list|)
operator|.
name|append
argument_list|(
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
else|else
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
if|if
condition|(
name|extractOnly
operator|==
literal|false
condition|)
block|{
name|addDoc
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//serializer is not null, so we need to call endDoc on it if using xpath
if|if
condition|(
name|xpathExpr
operator|!=
literal|null
condition|)
block|{
name|serializer
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
name|stream
operator|.
name|getName
argument_list|()
argument_list|,
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
index|[]
name|names
init|=
name|metadata
operator|.
name|names
argument_list|()
decl_stmt|;
name|NamedList
name|metadataNL
init|=
operator|new
name|NamedList
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|vals
init|=
name|metadata
operator|.
name|getValues
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|metadataNL
operator|.
name|add
argument_list|(
name|names
index|[
name|i
index|]
argument_list|,
name|vals
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
name|stream
operator|.
name|getName
argument_list|()
operator|+
literal|"_metadata"
argument_list|,
name|metadataNL
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
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
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
block|}
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
literal|"Stream type of "
operator|+
name|streamType
operator|+
literal|" didn't match any known parsers.  Please supply the "
operator|+
name|ExtractingParams
operator|.
name|STREAM_TYPE
operator|+
literal|" parameter."
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
