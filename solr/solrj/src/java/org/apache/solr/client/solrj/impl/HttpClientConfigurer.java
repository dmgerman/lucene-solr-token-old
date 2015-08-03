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
name|HttpRequestInterceptor
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
name|conn
operator|.
name|ssl
operator|.
name|SSLSocketFactory
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
name|DefaultHttpClient
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
name|SolrException
operator|.
name|ErrorCode
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
begin_comment
comment|/**  * The default http client configurer. If the behaviour needs to be customized a  * new HttpCilentConfigurer can be set by calling  * {@link HttpClientUtil#setConfigurer(HttpClientConfigurer)}  */
end_comment
begin_class
DECL|class|HttpClientConfigurer
specifier|public
class|class
name|HttpClientConfigurer
block|{
DECL|method|configure
specifier|public
name|void
name|configure
parameter_list|(
name|DefaultHttpClient
name|httpClient
parameter_list|,
name|SolrParams
name|config
parameter_list|)
block|{
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|HttpClientUtil
operator|.
name|setMaxConnections
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getInt
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS_PER_HOST
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|HttpClientUtil
operator|.
name|setMaxConnectionsPerHost
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getInt
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS_PER_HOST
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_CONNECTION_TIMEOUT
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|HttpClientUtil
operator|.
name|setConnectionTimeout
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getInt
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_CONNECTION_TIMEOUT
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_SO_TIMEOUT
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|HttpClientUtil
operator|.
name|setSoTimeout
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getInt
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_SO_TIMEOUT
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_FOLLOW_REDIRECTS
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|HttpClientUtil
operator|.
name|setFollowRedirects
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getBool
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_FOLLOW_REDIRECTS
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// always call setUseRetry, whether it is in config or not
name|HttpClientUtil
operator|.
name|setUseRetry
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getBool
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_USE_RETRY
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|basicAuthUser
init|=
name|config
operator|.
name|get
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_BASIC_AUTH_USER
argument_list|)
decl_stmt|;
specifier|final
name|String
name|basicAuthPass
init|=
name|config
operator|.
name|get
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_BASIC_AUTH_PASS
argument_list|)
decl_stmt|;
name|HttpClientUtil
operator|.
name|setBasicAuth
argument_list|(
name|httpClient
argument_list|,
name|basicAuthUser
argument_list|,
name|basicAuthPass
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_ALLOW_COMPRESSION
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|HttpClientUtil
operator|.
name|setAllowCompression
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getBool
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_ALLOW_COMPRESSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|sslCheckPeerName
init|=
name|toBooleanDefaultIfNull
argument_list|(
name|toBooleanObject
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|HttpClientUtil
operator|.
name|SYS_PROP_CHECK_PEER_NAME
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|sslCheckPeerName
operator|==
literal|false
condition|)
block|{
name|HttpClientUtil
operator|.
name|setHostNameVerifier
argument_list|(
name|httpClient
argument_list|,
name|SSLSocketFactory
operator|.
name|ALLOW_ALL_HOSTNAME_VERIFIER
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toBooleanDefaultIfNull
specifier|public
specifier|static
name|boolean
name|toBooleanDefaultIfNull
parameter_list|(
name|Boolean
name|bool
parameter_list|,
name|boolean
name|valueIfNull
parameter_list|)
block|{
if|if
condition|(
name|bool
operator|==
literal|null
condition|)
block|{
return|return
name|valueIfNull
return|;
block|}
return|return
name|bool
operator|.
name|booleanValue
argument_list|()
condition|?
literal|true
else|:
literal|false
return|;
block|}
DECL|method|toBooleanObject
specifier|public
specifier|static
name|Boolean
name|toBooleanObject
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|str
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
elseif|else
if|if
condition|(
literal|"false"
operator|.
name|equalsIgnoreCase
argument_list|(
name|str
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
comment|// no match
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
