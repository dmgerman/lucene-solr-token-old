begin_unit
begin_package
DECL|package|org.apache.solr.security
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequestWrapper
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
name|security
operator|.
name|Principal
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PublicKey
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|http
operator|.
name|Header
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
name|auth
operator|.
name|BasicUserPrincipal
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
name|message
operator|.
name|BasicHttpRequest
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
name|SolrTestCaseJ4
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
name|core
operator|.
name|CoreContainer
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
name|LocalSolrQueryRequest
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
name|CryptoKeys
import|;
end_import
begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|EasyMock
import|;
end_import
begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|IAnswer
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|getCurrentArguments
import|;
end_import
begin_class
DECL|class|TestPKIAuthenticationPlugin
specifier|public
class|class
name|TestPKIAuthenticationPlugin
extends|extends
name|SolrTestCaseJ4
block|{
DECL|class|MockPKIAuthenticationPlugin
specifier|static
class|class
name|MockPKIAuthenticationPlugin
extends|extends
name|PKIAuthenticationPlugin
block|{
DECL|field|solrRequestInfo
name|SolrRequestInfo
name|solrRequestInfo
decl_stmt|;
DECL|field|remoteKeys
name|Map
argument_list|<
name|String
argument_list|,
name|PublicKey
argument_list|>
name|remoteKeys
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|MockPKIAuthenticationPlugin
specifier|public
name|MockPKIAuthenticationPlugin
parameter_list|(
name|CoreContainer
name|cores
parameter_list|,
name|String
name|node
parameter_list|)
block|{
name|super
argument_list|(
name|cores
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|disabled
name|boolean
name|disabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getRequestInfo
name|SolrRequestInfo
name|getRequestInfo
parameter_list|()
block|{
return|return
name|solrRequestInfo
return|;
block|}
annotation|@
name|Override
DECL|method|getRemotePublicKey
name|PublicKey
name|getRemotePublicKey
parameter_list|(
name|String
name|nodename
parameter_list|)
block|{
return|return
name|remoteKeys
operator|.
name|get
argument_list|(
name|nodename
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isSolrThread
name|boolean
name|isSolrThread
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|AtomicReference
argument_list|<
name|Principal
argument_list|>
name|principal
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|nodeName
init|=
literal|"node_x_233"
decl_stmt|;
specifier|final
name|MockPKIAuthenticationPlugin
name|mock
init|=
operator|new
name|MockPKIAuthenticationPlugin
argument_list|(
literal|null
argument_list|,
name|nodeName
argument_list|)
decl_stmt|;
name|LocalSolrQueryRequest
name|localSolrQueryRequest
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
literal|null
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
name|principal
operator|.
name|get
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|PublicKey
name|correctKey
init|=
name|CryptoKeys
operator|.
name|deserializeX509PublicKey
argument_list|(
name|mock
operator|.
name|getPublicKey
argument_list|()
argument_list|)
decl_stmt|;
name|mock
operator|.
name|remoteKeys
operator|.
name|put
argument_list|(
name|nodeName
argument_list|,
name|correctKey
argument_list|)
expr_stmt|;
name|principal
operator|.
name|set
argument_list|(
operator|new
name|BasicUserPrincipal
argument_list|(
literal|"solr"
argument_list|)
argument_list|)
expr_stmt|;
name|mock
operator|.
name|solrRequestInfo
operator|=
operator|new
name|SolrRequestInfo
argument_list|(
name|localSolrQueryRequest
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
name|BasicHttpRequest
name|request
init|=
operator|new
name|BasicHttpRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"http://localhost:56565"
argument_list|)
decl_stmt|;
name|mock
operator|.
name|setHeader
argument_list|(
name|request
argument_list|)
expr_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Header
argument_list|>
name|header
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
name|header
operator|.
name|set
argument_list|(
name|request
operator|.
name|getFirstHeader
argument_list|(
name|PKIAuthenticationPlugin
operator|.
name|HEADER
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|header
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|header
operator|.
name|get
argument_list|()
operator|.
name|getValue
argument_list|()
operator|.
name|startsWith
argument_list|(
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|ServletRequest
argument_list|>
name|wrappedRequestByFilter
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
name|HttpServletRequest
name|mockReq
init|=
name|createMockRequest
argument_list|(
name|header
argument_list|)
decl_stmt|;
name|FilterChain
name|filterChain
init|=
operator|new
name|FilterChain
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|servletRequest
parameter_list|,
name|ServletResponse
name|servletResponse
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|wrappedRequestByFilter
operator|.
name|set
argument_list|(
name|servletRequest
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|mock
operator|.
name|doAuthenticate
argument_list|(
name|mockReq
argument_list|,
literal|null
argument_list|,
name|filterChain
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|wrappedRequestByFilter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"solr"
argument_list|,
operator|(
operator|(
name|HttpServletRequest
operator|)
name|wrappedRequestByFilter
operator|.
name|get
argument_list|()
operator|)
operator|.
name|getUserPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|//test 2
name|principal
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// no user
name|header
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|wrappedRequestByFilter
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//
name|request
operator|=
operator|new
name|BasicHttpRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"http://localhost:56565"
argument_list|)
expr_stmt|;
name|mock
operator|.
name|setHeader
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|request
operator|.
name|getFirstHeader
argument_list|(
name|PKIAuthenticationPlugin
operator|.
name|HEADER
argument_list|)
argument_list|)
expr_stmt|;
name|mock
operator|.
name|doAuthenticate
argument_list|(
name|mockReq
argument_list|,
literal|null
argument_list|,
name|filterChain
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|wrappedRequestByFilter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|HttpServletRequest
operator|)
name|wrappedRequestByFilter
operator|.
name|get
argument_list|()
operator|)
operator|.
name|getUserPrincipal
argument_list|()
argument_list|)
expr_stmt|;
comment|//test 3 . No user request . Request originated from Solr
name|mock
operator|.
name|solrRequestInfo
operator|=
literal|null
expr_stmt|;
name|header
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|wrappedRequestByFilter
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|BasicHttpRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"http://localhost:56565"
argument_list|)
expr_stmt|;
name|mock
operator|.
name|setHeader
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|header
operator|.
name|set
argument_list|(
name|request
operator|.
name|getFirstHeader
argument_list|(
name|PKIAuthenticationPlugin
operator|.
name|HEADER
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|header
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|header
operator|.
name|get
argument_list|()
operator|.
name|getValue
argument_list|()
operator|.
name|startsWith
argument_list|(
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
name|mock
operator|.
name|doAuthenticate
argument_list|(
name|mockReq
argument_list|,
literal|null
argument_list|,
name|filterChain
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|wrappedRequestByFilter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"$"
argument_list|,
operator|(
operator|(
name|HttpServletRequest
operator|)
name|wrappedRequestByFilter
operator|.
name|get
argument_list|()
operator|)
operator|.
name|getUserPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|MockPKIAuthenticationPlugin
name|mock1
init|=
operator|new
name|MockPKIAuthenticationPlugin
argument_list|(
literal|null
argument_list|,
name|nodeName
argument_list|)
block|{
name|int
name|called
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
name|PublicKey
name|getRemotePublicKey
parameter_list|(
name|String
name|nodename
parameter_list|)
block|{
try|try
block|{
return|return
name|called
operator|==
literal|0
condition|?
operator|new
name|CryptoKeys
operator|.
name|RSAKeyPair
argument_list|()
operator|.
name|getPublicKey
argument_list|()
else|:
name|correctKey
return|;
block|}
finally|finally
block|{
name|called
operator|++
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|mock1
operator|.
name|doAuthenticate
argument_list|(
name|mockReq
argument_list|,
literal|null
argument_list|,
name|filterChain
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|wrappedRequestByFilter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"$"
argument_list|,
operator|(
operator|(
name|HttpServletRequest
operator|)
name|wrappedRequestByFilter
operator|.
name|get
argument_list|()
operator|)
operator|.
name|getUserPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createMockRequest
specifier|private
name|HttpServletRequest
name|createMockRequest
parameter_list|(
specifier|final
name|AtomicReference
argument_list|<
name|Header
argument_list|>
name|header
parameter_list|)
block|{
name|HttpServletRequest
name|mockReq
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|EasyMock
operator|.
name|reset
argument_list|(
name|mockReq
argument_list|)
expr_stmt|;
name|mockReq
operator|.
name|getHeader
argument_list|(
name|EasyMock
operator|.
name|anyObject
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|EasyMock
operator|.
name|expectLastCall
argument_list|()
operator|.
name|andAnswer
argument_list|(
operator|new
name|IAnswer
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|answer
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
name|PKIAuthenticationPlugin
operator|.
name|HEADER
operator|.
name|equals
argument_list|(
name|getCurrentArguments
argument_list|()
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
if|if
condition|(
name|header
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|header
operator|.
name|get
argument_list|()
operator|.
name|getValue
argument_list|()
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|mockReq
operator|.
name|getUserPrincipal
argument_list|()
expr_stmt|;
name|EasyMock
operator|.
name|expectLastCall
argument_list|()
operator|.
name|andAnswer
argument_list|(
operator|new
name|IAnswer
argument_list|<
name|Principal
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Principal
name|answer
parameter_list|()
throws|throws
name|Throwable
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|mockReq
operator|.
name|getRequestURI
argument_list|()
expr_stmt|;
name|EasyMock
operator|.
name|expectLastCall
argument_list|()
operator|.
name|andAnswer
argument_list|(
operator|new
name|IAnswer
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|answer
parameter_list|()
throws|throws
name|Throwable
block|{
return|return
literal|"/collection1/select"
return|;
block|}
block|}
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|EasyMock
operator|.
name|replay
argument_list|(
name|mockReq
argument_list|)
expr_stmt|;
return|return
name|mockReq
return|;
block|}
block|}
end_class
end_unit
