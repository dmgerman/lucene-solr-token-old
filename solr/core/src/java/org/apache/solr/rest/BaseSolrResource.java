begin_unit
begin_package
DECL|package|org.apache.solr.rest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|params
operator|.
name|ModifiableSolrParams
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
name|SimpleOrderedMap
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
name|SolrRequestInfo
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
name|BinaryQueryResponseWriter
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
name|apache
operator|.
name|solr
operator|.
name|servlet
operator|.
name|ResponseUtils
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
name|restlet
operator|.
name|data
operator|.
name|MediaType
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|data
operator|.
name|Method
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|data
operator|.
name|Status
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|representation
operator|.
name|OutputRepresentation
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|resource
operator|.
name|ResourceException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|resource
operator|.
name|ServerResource
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
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|io
operator|.
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLDecoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_comment
comment|/**  * Base class of all Solr Restlet server resource classes.  */
end_comment
begin_class
DECL|class|BaseSolrResource
specifier|public
specifier|abstract
class|class
name|BaseSolrResource
extends|extends
name|ServerResource
block|{
DECL|field|UTF8
specifier|protected
specifier|static
specifier|final
name|Charset
name|UTF8
init|=
name|StandardCharsets
operator|.
name|UTF_8
decl_stmt|;
DECL|field|SHOW_DEFAULTS
specifier|protected
specifier|static
specifier|final
name|String
name|SHOW_DEFAULTS
init|=
literal|"showDefaults"
decl_stmt|;
DECL|field|solrCore
specifier|private
name|SolrCore
name|solrCore
decl_stmt|;
DECL|field|schema
specifier|private
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|solrRequest
specifier|private
name|SolrQueryRequest
name|solrRequest
decl_stmt|;
DECL|field|solrResponse
specifier|private
name|SolrQueryResponse
name|solrResponse
decl_stmt|;
DECL|field|responseWriter
specifier|private
name|QueryResponseWriter
name|responseWriter
decl_stmt|;
DECL|field|contentType
specifier|private
name|String
name|contentType
decl_stmt|;
DECL|method|getSolrCore
specifier|public
name|SolrCore
name|getSolrCore
parameter_list|()
block|{
return|return
name|solrCore
return|;
block|}
DECL|method|getSchema
specifier|public
name|IndexSchema
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
DECL|method|getSolrRequest
specifier|public
name|SolrQueryRequest
name|getSolrRequest
parameter_list|()
block|{
return|return
name|solrRequest
return|;
block|}
DECL|method|getSolrResponse
specifier|public
name|SolrQueryResponse
name|getSolrResponse
parameter_list|()
block|{
return|return
name|solrResponse
return|;
block|}
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|contentType
return|;
block|}
DECL|method|BaseSolrResource
specifier|protected
name|BaseSolrResource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Pulls the SolrQueryRequest constructed in SolrDispatchFilter    * from the SolrRequestInfo thread local, then gets the SolrCore    * and IndexSchema and sets up the response.    * writer.    *<p/>    * If an error occurs during initialization, setExisting(false) is    * called and an error status code and message is set; in this case,    * Restlet will not continue servicing the request (by calling the    * method annotated to associate it with GET, etc., but rather will    * send an error response.    */
annotation|@
name|Override
DECL|method|doInit
specifier|public
name|void
name|doInit
parameter_list|()
throws|throws
name|ResourceException
block|{
name|super
operator|.
name|doInit
argument_list|()
expr_stmt|;
name|setNegotiated
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Turn off content negotiation for now
if|if
condition|(
name|isExisting
argument_list|()
condition|)
block|{
try|try
block|{
name|SolrRequestInfo
name|solrRequestInfo
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|solrRequestInfo
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"No handler or core found in "
operator|+
name|getRequest
argument_list|()
operator|.
name|getOriginalRef
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|doError
argument_list|(
name|Status
operator|.
name|CLIENT_ERROR_BAD_REQUEST
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|setExisting
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|solrRequest
operator|=
name|solrRequestInfo
operator|.
name|getReq
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|solrRequest
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"No handler or core found in "
operator|+
name|getRequest
argument_list|()
operator|.
name|getOriginalRef
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|doError
argument_list|(
name|Status
operator|.
name|CLIENT_ERROR_BAD_REQUEST
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|setExisting
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|solrResponse
operator|=
name|solrRequestInfo
operator|.
name|getRsp
argument_list|()
expr_stmt|;
name|solrCore
operator|=
name|solrRequest
operator|.
name|getCore
argument_list|()
expr_stmt|;
name|schema
operator|=
name|solrRequest
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|String
name|responseWriterName
init|=
name|solrRequest
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|responseWriterName
condition|)
block|{
name|responseWriterName
operator|=
literal|"json"
expr_stmt|;
comment|// Default to json writer
block|}
name|String
name|indent
init|=
name|solrRequest
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"indent"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|indent
operator|||
operator|!
operator|(
literal|"off"
operator|.
name|equals
argument_list|(
name|indent
argument_list|)
operator|||
literal|"false"
operator|.
name|equals
argument_list|(
name|indent
argument_list|)
operator|)
condition|)
block|{
comment|// indent by default
name|ModifiableSolrParams
name|newParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|solrRequest
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|newParams
operator|.
name|remove
argument_list|(
name|indent
argument_list|)
expr_stmt|;
name|newParams
operator|.
name|add
argument_list|(
literal|"indent"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|solrRequest
operator|.
name|setParams
argument_list|(
name|newParams
argument_list|)
expr_stmt|;
block|}
name|responseWriter
operator|=
name|solrCore
operator|.
name|getQueryResponseWriter
argument_list|(
name|responseWriterName
argument_list|)
expr_stmt|;
name|contentType
operator|=
name|responseWriter
operator|.
name|getContentType
argument_list|(
name|solrRequest
argument_list|,
name|solrResponse
argument_list|)
expr_stmt|;
specifier|final
name|String
name|path
init|=
name|getRequest
argument_list|()
operator|.
name|getRootRef
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|RestManager
operator|.
name|SCHEMA_BASE_PATH
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|&&
operator|!
name|RestManager
operator|.
name|CONFIG_BASE_PATH
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|// don't set webapp property on the request when context and core/collection are excluded
specifier|final
name|int
name|cutoffPoint
init|=
name|path
operator|.
name|indexOf
argument_list|(
literal|"/"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|firstPathElement
init|=
operator|-
literal|1
operator|==
name|cutoffPoint
condition|?
name|path
else|:
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|cutoffPoint
argument_list|)
decl_stmt|;
name|solrRequest
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
literal|"webapp"
argument_list|,
name|firstPathElement
argument_list|)
expr_stmt|;
comment|// Context path
block|}
name|SolrCore
operator|.
name|preDecorateResponse
argument_list|(
name|solrRequest
argument_list|,
name|solrResponse
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|OutOfMemoryError
condition|)
block|{
throw|throw
operator|(
name|OutOfMemoryError
operator|)
name|t
throw|;
block|}
name|setExisting
argument_list|(
literal|false
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * This class serves as an adapter between Restlet and Solr's response writers.     */
DECL|class|SolrOutputRepresentation
specifier|public
class|class
name|SolrOutputRepresentation
extends|extends
name|OutputRepresentation
block|{
DECL|method|SolrOutputRepresentation
specifier|public
name|SolrOutputRepresentation
parameter_list|()
block|{
comment|// No normalization, in case of a custom media type
name|super
argument_list|(
name|MediaType
operator|.
name|valueOf
argument_list|(
name|contentType
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: For now, don't send the Vary: header, but revisit if/when content negotiation is added
name|getDimensions
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/** Called by Restlet to get the response body */
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|outputStream
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|getRequest
argument_list|()
operator|.
name|getMethod
argument_list|()
operator|!=
name|Method
operator|.
name|HEAD
condition|)
block|{
if|if
condition|(
name|responseWriter
operator|instanceof
name|BinaryQueryResponseWriter
condition|)
block|{
name|BinaryQueryResponseWriter
name|binWriter
init|=
operator|(
name|BinaryQueryResponseWriter
operator|)
name|responseWriter
decl_stmt|;
name|binWriter
operator|.
name|write
argument_list|(
name|outputStream
argument_list|,
name|solrRequest
argument_list|,
name|solrResponse
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|charset
init|=
name|ContentStreamBase
operator|.
name|getCharsetFromContentType
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
name|Writer
name|out
init|=
operator|(
name|charset
operator|==
literal|null
operator|)
condition|?
operator|new
name|OutputStreamWriter
argument_list|(
name|outputStream
argument_list|,
name|UTF8
argument_list|)
else|:
operator|new
name|OutputStreamWriter
argument_list|(
name|outputStream
argument_list|,
name|charset
argument_list|)
decl_stmt|;
name|out
operator|=
operator|new
name|FastWriter
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|responseWriter
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|solrRequest
argument_list|,
name|solrResponse
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Deal with an exception on the SolrResponse, fill in response header info,    * and log the accumulated messages on the SolrResponse.    */
DECL|method|handlePostExecution
specifier|protected
name|void
name|handlePostExecution
parameter_list|(
name|Logger
name|log
parameter_list|)
block|{
name|handleException
argument_list|(
name|log
argument_list|)
expr_stmt|;
comment|// TODO: should status=0 (success?) be left as-is in the response header?
name|SolrCore
operator|.
name|postDecorateResponse
argument_list|(
literal|null
argument_list|,
name|solrRequest
argument_list|,
name|solrResponse
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
operator|&&
name|solrResponse
operator|.
name|getToLog
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|solrResponse
operator|.
name|getToLogAsString
argument_list|(
name|solrCore
operator|.
name|getLogId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * If there is an exception on the SolrResponse:    *<ul>    *<li>error info is added to the SolrResponse;</li>    *<li>the response status code is set to the error code from the exception; and</li>    *<li>the exception message is added to the list of things to be logged.</li>    *</ul>    */
DECL|method|handleException
specifier|protected
name|void
name|handleException
parameter_list|(
name|Logger
name|log
parameter_list|)
block|{
name|Exception
name|exception
init|=
name|getSolrResponse
argument_list|()
operator|.
name|getException
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|exception
condition|)
block|{
name|NamedList
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|int
name|code
init|=
name|ResponseUtils
operator|.
name|getErrorInfo
argument_list|(
name|exception
argument_list|,
name|info
argument_list|,
name|log
argument_list|)
decl_stmt|;
name|setStatus
argument_list|(
name|Status
operator|.
name|valueOf
argument_list|(
name|code
argument_list|)
argument_list|)
expr_stmt|;
name|getSolrResponse
argument_list|()
operator|.
name|add
argument_list|(
literal|"error"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|String
name|message
init|=
operator|(
name|String
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|message
operator|&&
operator|!
name|message
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|getSolrResponse
argument_list|()
operator|.
name|getToLog
argument_list|()
operator|.
name|add
argument_list|(
literal|"msg"
argument_list|,
literal|"{"
operator|+
name|message
operator|.
name|trim
argument_list|()
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Decode URL-encoded strings as UTF-8, and avoid converting "+" to space */
DECL|method|urlDecode
specifier|protected
specifier|static
name|String
name|urlDecode
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
return|return
name|URLDecoder
operator|.
name|decode
argument_list|(
name|str
operator|.
name|replace
argument_list|(
literal|"+"
argument_list|,
literal|"%2B"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
block|}
end_class
end_unit
