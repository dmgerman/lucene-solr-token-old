begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
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
name|ContentStream
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
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
begin_comment
comment|/**  *   *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrRequest
specifier|public
specifier|abstract
class|class
name|SolrRequest
parameter_list|<
name|T
extends|extends
name|SolrResponse
parameter_list|>
implements|implements
name|Serializable
block|{
DECL|enum|METHOD
specifier|public
enum|enum
name|METHOD
block|{
DECL|enum constant|GET
name|GET
block|,
DECL|enum constant|POST
name|POST
block|,
DECL|enum constant|PUT
name|PUT
block|}
empty_stmt|;
DECL|field|method
specifier|private
name|METHOD
name|method
init|=
name|METHOD
operator|.
name|GET
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
init|=
literal|null
decl_stmt|;
DECL|field|responseParser
specifier|private
name|ResponseParser
name|responseParser
decl_stmt|;
DECL|field|callback
specifier|private
name|StreamingResponseCallback
name|callback
decl_stmt|;
DECL|field|queryParams
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|queryParams
decl_stmt|;
DECL|field|basicAuthUser
DECL|field|basicAuthPwd
specifier|private
name|String
name|basicAuthUser
decl_stmt|,
name|basicAuthPwd
decl_stmt|;
DECL|method|setBasicAuthCredentials
specifier|public
name|SolrRequest
name|setBasicAuthCredentials
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|basicAuthUser
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|basicAuthPwd
operator|=
name|password
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getBasicAuthUser
specifier|public
name|String
name|getBasicAuthUser
parameter_list|()
block|{
return|return
name|basicAuthUser
return|;
block|}
DECL|method|getBasicAuthPassword
specifier|public
name|String
name|getBasicAuthPassword
parameter_list|()
block|{
return|return
name|basicAuthPwd
return|;
block|}
comment|//---------------------------------------------------------
comment|//---------------------------------------------------------
DECL|method|SolrRequest
specifier|public
name|SolrRequest
parameter_list|(
name|METHOD
name|m
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|m
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
comment|//---------------------------------------------------------
comment|//---------------------------------------------------------
DECL|method|getMethod
specifier|public
name|METHOD
name|getMethod
parameter_list|()
block|{
return|return
name|method
return|;
block|}
DECL|method|setMethod
specifier|public
name|void
name|setMethod
parameter_list|(
name|METHOD
name|method
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
block|}
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|setPath
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
comment|/**    *    * @return The {@link org.apache.solr.client.solrj.ResponseParser}    */
DECL|method|getResponseParser
specifier|public
name|ResponseParser
name|getResponseParser
parameter_list|()
block|{
return|return
name|responseParser
return|;
block|}
comment|/**    * Optionally specify how the Response should be parsed.  Not all server implementations require a ResponseParser    * to be specified.    * @param responseParser The {@link org.apache.solr.client.solrj.ResponseParser}    */
DECL|method|setResponseParser
specifier|public
name|void
name|setResponseParser
parameter_list|(
name|ResponseParser
name|responseParser
parameter_list|)
block|{
name|this
operator|.
name|responseParser
operator|=
name|responseParser
expr_stmt|;
block|}
DECL|method|getStreamingResponseCallback
specifier|public
name|StreamingResponseCallback
name|getStreamingResponseCallback
parameter_list|()
block|{
return|return
name|callback
return|;
block|}
DECL|method|setStreamingResponseCallback
specifier|public
name|void
name|setStreamingResponseCallback
parameter_list|(
name|StreamingResponseCallback
name|callback
parameter_list|)
block|{
name|this
operator|.
name|callback
operator|=
name|callback
expr_stmt|;
block|}
comment|/**    * Parameter keys that are sent via the query string    */
DECL|method|getQueryParams
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getQueryParams
parameter_list|()
block|{
return|return
name|this
operator|.
name|queryParams
return|;
block|}
DECL|method|setQueryParams
specifier|public
name|void
name|setQueryParams
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|queryParams
parameter_list|)
block|{
name|this
operator|.
name|queryParams
operator|=
name|queryParams
expr_stmt|;
block|}
DECL|method|getParams
specifier|public
specifier|abstract
name|SolrParams
name|getParams
parameter_list|()
function_decl|;
DECL|method|getContentStreams
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a new SolrResponse to hold the response from the server    * @param client the {@link SolrClient} the request will be sent to    */
DECL|method|createResponse
specifier|protected
specifier|abstract
name|T
name|createResponse
parameter_list|(
name|SolrClient
name|client
parameter_list|)
function_decl|;
comment|/**    * Send this request to a {@link SolrClient} and return the response    *    * @param client the SolrClient to communicate with    * @param collection the collection to execute the request against    *    * @return the response    *    * @throws SolrServerException if there is an error on the Solr server    * @throws IOException if there is a communication error    */
DECL|method|process
specifier|public
specifier|final
name|T
name|process
parameter_list|(
name|SolrClient
name|client
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|long
name|startTime
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|T
name|res
init|=
name|createResponse
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|res
operator|.
name|setResponse
argument_list|(
name|client
operator|.
name|request
argument_list|(
name|this
argument_list|,
name|collection
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|endTime
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|res
operator|.
name|setElapsedTime
argument_list|(
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
comment|/**    * Send this request to a {@link SolrClient} and return the response    *    * @param client the SolrClient to communicate with    *    * @return the response    *    * @throws SolrServerException if there is an error on the Solr server    * @throws IOException if there is a communication error    */
DECL|method|process
specifier|public
specifier|final
name|T
name|process
parameter_list|(
name|SolrClient
name|client
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|process
argument_list|(
name|client
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class
end_unit
