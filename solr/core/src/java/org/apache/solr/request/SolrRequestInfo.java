begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|TimeZone
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
name|util
operator|.
name|ExecutorUtil
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|util
operator|.
name|TimeZoneUtils
import|;
end_import
begin_class
DECL|class|SolrRequestInfo
specifier|public
class|class
name|SolrRequestInfo
block|{
DECL|field|threadLocal
specifier|protected
specifier|final
specifier|static
name|ThreadLocal
argument_list|<
name|SolrRequestInfo
argument_list|>
name|threadLocal
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|req
specifier|protected
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|rsp
specifier|protected
name|SolrQueryResponse
name|rsp
decl_stmt|;
DECL|field|now
specifier|protected
name|Date
name|now
decl_stmt|;
DECL|field|tz
specifier|protected
name|TimeZone
name|tz
decl_stmt|;
DECL|field|rb
specifier|protected
name|ResponseBuilder
name|rb
decl_stmt|;
DECL|field|closeHooks
specifier|protected
name|List
argument_list|<
name|Closeable
argument_list|>
name|closeHooks
decl_stmt|;
DECL|method|getRequestInfo
specifier|public
specifier|static
name|SolrRequestInfo
name|getRequestInfo
parameter_list|()
block|{
return|return
name|threadLocal
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|setRequestInfo
specifier|public
specifier|static
name|void
name|setRequestInfo
parameter_list|(
name|SolrRequestInfo
name|info
parameter_list|)
block|{
comment|// TODO: temporary sanity check... this can be changed to just an assert in the future
name|SolrRequestInfo
name|prev
init|=
name|threadLocal
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|prev
operator|!=
literal|null
condition|)
block|{
name|SolrCore
operator|.
name|log
operator|.
name|error
argument_list|(
literal|"Previous SolrRequestInfo was not closed!  req="
operator|+
name|prev
operator|.
name|req
operator|.
name|getOriginalParams
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SolrCore
operator|.
name|log
operator|.
name|error
argument_list|(
literal|"prev == info : {}"
argument_list|,
name|prev
operator|.
name|req
operator|==
name|info
operator|.
name|req
argument_list|)
expr_stmt|;
block|}
assert|assert
name|prev
operator|==
literal|null
assert|;
name|threadLocal
operator|.
name|set
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
DECL|method|clearRequestInfo
specifier|public
specifier|static
name|void
name|clearRequestInfo
parameter_list|()
block|{
try|try
block|{
name|SolrRequestInfo
name|info
init|=
name|threadLocal
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
operator|&&
name|info
operator|.
name|closeHooks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Closeable
name|hook
range|:
name|info
operator|.
name|closeHooks
control|)
block|{
try|try
block|{
name|hook
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
name|SolrException
operator|.
name|log
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
literal|"Exception during close hook"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|threadLocal
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|SolrRequestInfo
specifier|public
name|SolrRequestInfo
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|rsp
operator|=
name|rsp
expr_stmt|;
block|}
DECL|method|getNOW
specifier|public
name|Date
name|getNOW
parameter_list|()
block|{
if|if
condition|(
name|now
operator|!=
literal|null
condition|)
return|return
name|now
return|;
name|long
name|ms
init|=
literal|0
decl_stmt|;
name|String
name|nowStr
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|NOW
argument_list|)
decl_stmt|;
if|if
condition|(
name|nowStr
operator|!=
literal|null
condition|)
block|{
name|ms
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|nowStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ms
operator|=
name|req
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
block|}
name|now
operator|=
operator|new
name|Date
argument_list|(
name|ms
argument_list|)
expr_stmt|;
return|return
name|now
return|;
block|}
comment|/** The TimeZone specified by the request, or null if none was specified */
DECL|method|getClientTimeZone
specifier|public
name|TimeZone
name|getClientTimeZone
parameter_list|()
block|{
if|if
condition|(
name|tz
operator|==
literal|null
condition|)
block|{
name|String
name|tzStr
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|TZ
argument_list|)
decl_stmt|;
if|if
condition|(
name|tzStr
operator|!=
literal|null
condition|)
block|{
name|tz
operator|=
name|TimeZoneUtils
operator|.
name|getTimeZone
argument_list|(
name|tzStr
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|tz
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
literal|"Solr JVM does not support TZ: "
operator|+
name|tzStr
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|tz
return|;
block|}
DECL|method|getReq
specifier|public
name|SolrQueryRequest
name|getReq
parameter_list|()
block|{
return|return
name|req
return|;
block|}
DECL|method|getRsp
specifier|public
name|SolrQueryResponse
name|getRsp
parameter_list|()
block|{
return|return
name|rsp
return|;
block|}
comment|/** May return null if the request handler is not based on SearchHandler */
DECL|method|getResponseBuilder
specifier|public
name|ResponseBuilder
name|getResponseBuilder
parameter_list|()
block|{
return|return
name|rb
return|;
block|}
DECL|method|setResponseBuilder
specifier|public
name|void
name|setResponseBuilder
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|this
operator|.
name|rb
operator|=
name|rb
expr_stmt|;
block|}
DECL|method|addCloseHook
specifier|public
name|void
name|addCloseHook
parameter_list|(
name|Closeable
name|hook
parameter_list|)
block|{
comment|// is this better here, or on SolrQueryRequest?
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|closeHooks
operator|==
literal|null
condition|)
block|{
name|closeHooks
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|closeHooks
operator|.
name|add
argument_list|(
name|hook
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getInheritableThreadLocalProvider
specifier|public
specifier|static
name|ExecutorUtil
operator|.
name|InheritableThreadLocalProvider
name|getInheritableThreadLocalProvider
parameter_list|()
block|{
return|return
operator|new
name|ExecutorUtil
operator|.
name|InheritableThreadLocalProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|store
parameter_list|(
name|AtomicReference
name|ctx
parameter_list|)
block|{
name|SolrRequestInfo
name|me
init|=
name|threadLocal
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|me
operator|!=
literal|null
condition|)
name|ctx
operator|.
name|set
argument_list|(
name|me
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|set
parameter_list|(
name|AtomicReference
name|ctx
parameter_list|)
block|{
name|SolrRequestInfo
name|me
init|=
operator|(
name|SolrRequestInfo
operator|)
name|ctx
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|me
operator|!=
literal|null
condition|)
block|{
name|ctx
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|threadLocal
operator|.
name|set
argument_list|(
name|me
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|clean
parameter_list|(
name|AtomicReference
name|ctx
parameter_list|)
block|{
name|threadLocal
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
