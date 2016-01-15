begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.impl
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
operator|.
name|impl
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
name|InterruptedIOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ConnectException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashSet
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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpRequestRetryHandler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpUriRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|protocol
operator|.
name|HttpClientContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|RequestWrapper
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|protocol
operator|.
name|HttpContext
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
begin_class
DECL|class|SolrHttpRequestRetryHandler
specifier|public
class|class
name|SolrHttpRequestRetryHandler
implements|implements
name|HttpRequestRetryHandler
block|{
DECL|field|GET
specifier|private
specifier|static
specifier|final
name|String
name|GET
init|=
literal|"GET"
decl_stmt|;
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|SolrHttpRequestRetryHandler
name|INSTANCE
init|=
operator|new
name|SolrHttpRequestRetryHandler
argument_list|()
decl_stmt|;
comment|/** the number of times a method will be retried */
DECL|field|retryCount
specifier|private
specifier|final
name|int
name|retryCount
decl_stmt|;
DECL|field|nonRetriableClasses
specifier|private
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|IOException
argument_list|>
argument_list|>
name|nonRetriableClasses
decl_stmt|;
comment|/**    * Create the request retry handler using the specified IOException classes    *    * @param retryCount    *          how many times to retry; 0 means no retries    *          true if it's OK to retry requests that have been sent    * @param clazzes    *          the IOException types that should not be retried    */
DECL|method|SolrHttpRequestRetryHandler
specifier|protected
name|SolrHttpRequestRetryHandler
parameter_list|(
specifier|final
name|int
name|retryCount
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|IOException
argument_list|>
argument_list|>
name|clazzes
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|retryCount
operator|=
name|retryCount
expr_stmt|;
name|this
operator|.
name|nonRetriableClasses
operator|=
operator|new
name|HashSet
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|IOException
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|IOException
argument_list|>
name|clazz
range|:
name|clazzes
control|)
block|{
name|this
operator|.
name|nonRetriableClasses
operator|.
name|add
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create the request retry handler using the following list of non-retriable IOException classes:<br>    *<ul>    *<li>InterruptedIOException</li>    *<li>UnknownHostException</li>    *<li>ConnectException</li>    *<li>SSLException</li>    *</ul>    *     * @param retryCount    *          how many times to retry; 0 means no retries    *          true if it's OK to retry non-idempotent requests that have been sent    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|SolrHttpRequestRetryHandler
specifier|public
name|SolrHttpRequestRetryHandler
parameter_list|(
specifier|final
name|int
name|retryCount
parameter_list|)
block|{
name|this
argument_list|(
name|retryCount
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InterruptedIOException
operator|.
name|class
argument_list|,
name|UnknownHostException
operator|.
name|class
argument_list|,
name|ConnectException
operator|.
name|class
argument_list|,
name|SSLException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create the request retry handler with a retry count of 3, requestSentRetryEnabled false and using the following    * list of non-retriable IOException classes:<br>    *<ul>    *<li>InterruptedIOException</li>    *<li>UnknownHostException</li>    *<li>ConnectException</li>    *<li>SSLException</li>    *</ul>    */
DECL|method|SolrHttpRequestRetryHandler
specifier|public
name|SolrHttpRequestRetryHandler
parameter_list|()
block|{
name|this
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|retryRequest
specifier|public
name|boolean
name|retryRequest
parameter_list|(
specifier|final
name|IOException
name|exception
parameter_list|,
specifier|final
name|int
name|executionCount
parameter_list|,
specifier|final
name|HttpContext
name|context
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Retry http request {} out of {}"
argument_list|,
name|executionCount
argument_list|,
name|this
operator|.
name|retryCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|executionCount
operator|>
name|this
operator|.
name|retryCount
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Do not retry, over max retry count"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|nonRetriableClasses
operator|.
name|contains
argument_list|(
name|exception
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Do not retry, non retriable class {}"
argument_list|,
name|exception
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
for|for
control|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|IOException
argument_list|>
name|rejectException
range|:
name|this
operator|.
name|nonRetriableClasses
control|)
block|{
if|if
condition|(
name|rejectException
operator|.
name|isInstance
argument_list|(
name|exception
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Do not retry, non retriable class {}"
argument_list|,
name|exception
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
specifier|final
name|HttpClientContext
name|clientContext
init|=
name|HttpClientContext
operator|.
name|adapt
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|HttpRequest
name|request
init|=
name|clientContext
operator|.
name|getRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|requestIsAborted
argument_list|(
name|request
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Do not retry, request was aborted"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|handleAsIdempotent
argument_list|(
name|clientContext
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Retry, request should be idempotent"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Do not retry, no allow rules matched"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|getRetryCount
specifier|public
name|int
name|getRetryCount
parameter_list|()
block|{
return|return
name|retryCount
return|;
block|}
DECL|method|handleAsIdempotent
specifier|protected
name|boolean
name|handleAsIdempotent
parameter_list|(
specifier|final
name|HttpClientContext
name|context
parameter_list|)
block|{
name|String
name|method
init|=
name|context
operator|.
name|getRequest
argument_list|()
operator|.
name|getRequestLine
argument_list|()
operator|.
name|getMethod
argument_list|()
decl_stmt|;
comment|// do not retry admin requests, even if they are GET as they are not idempotent
if|if
condition|(
name|context
operator|.
name|getRequest
argument_list|()
operator|.
name|getRequestLine
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"/admin/"
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Do not retry, this is an admin request"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
name|method
operator|.
name|equals
argument_list|(
name|GET
argument_list|)
return|;
block|}
DECL|method|requestIsAborted
specifier|protected
name|boolean
name|requestIsAborted
parameter_list|(
specifier|final
name|HttpRequest
name|request
parameter_list|)
block|{
name|HttpRequest
name|req
init|=
name|request
decl_stmt|;
if|if
condition|(
name|request
operator|instanceof
name|RequestWrapper
condition|)
block|{
comment|// does not forward request to original
name|req
operator|=
operator|(
operator|(
name|RequestWrapper
operator|)
name|request
operator|)
operator|.
name|getOriginal
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|req
operator|instanceof
name|HttpUriRequest
operator|&&
operator|(
operator|(
name|HttpUriRequest
operator|)
name|req
operator|)
operator|.
name|isAborted
argument_list|()
operator|)
return|;
block|}
block|}
end_class
end_unit
