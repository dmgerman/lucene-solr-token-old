begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.servlet.handler
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|servlet
operator|.
name|handler
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|HttpServletResponse
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|gdata
operator|.
name|data
operator|.
name|GDataAccount
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseFeed
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
name|gdata
operator|.
name|server
operator|.
name|GDataResponse
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
name|gdata
operator|.
name|server
operator|.
name|ServiceException
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
name|gdata
operator|.
name|server
operator|.
name|ServiceFactory
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
name|gdata
operator|.
name|server
operator|.
name|administration
operator|.
name|AdminService
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ComponentType
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|GDataServerRegistry
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|RegistryException
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
name|gdata
operator|.
name|servlet
operator|.
name|handler
operator|.
name|AbstractFeedHandler
operator|.
name|FeedHandlerException
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
name|gdata
operator|.
name|utils
operator|.
name|ProvidedServiceStub
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
name|gdata
operator|.
name|utils
operator|.
name|ServiceFactoryStub
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
name|gdata
operator|.
name|utils
operator|.
name|StorageStub
import|;
end_import
begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|MockControl
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|util
operator|.
name|ParseException
import|;
end_import
begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment
begin_class
DECL|class|TestAbstractFeedHandler
specifier|public
class|class
name|TestAbstractFeedHandler
extends|extends
name|TestCase
block|{
DECL|field|requestMockControl
specifier|private
name|MockControl
name|requestMockControl
decl_stmt|;
DECL|field|mockRequest
specifier|private
name|HttpServletRequest
name|mockRequest
init|=
literal|null
decl_stmt|;
DECL|field|accountName
specifier|private
name|String
name|accountName
init|=
literal|"acc"
decl_stmt|;
DECL|field|adminServiceMockControl
specifier|private
name|MockControl
name|adminServiceMockControl
decl_stmt|;
DECL|field|adminService
specifier|private
name|AdminService
name|adminService
init|=
literal|null
decl_stmt|;
DECL|field|stub
specifier|private
name|ServiceFactoryStub
name|stub
decl_stmt|;
DECL|field|serviceName
specifier|private
name|String
name|serviceName
init|=
name|StorageStub
operator|.
name|SERVICE_TYPE_RETURN
decl_stmt|;
DECL|field|fileDir
specifier|private
specifier|static
name|String
name|fileDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"lucene.common.dir"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
DECL|field|incomingFeed
specifier|private
specifier|static
name|File
name|incomingFeed
init|=
operator|new
name|File
argument_list|(
name|fileDir
argument_list|,
literal|"contrib/gdata-server/src/core/src/test/org/apache/lucene/gdata/server/registry/TestEntityBuilderIncomingFeed.xml"
argument_list|)
decl_stmt|;
DECL|field|reader
name|BufferedReader
name|reader
decl_stmt|;
static|static
block|{
try|try
block|{
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|registerComponent
argument_list|(
name|StorageStub
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|registerComponent
argument_list|(
name|ServiceFactoryStub
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RegistryException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|registerService
argument_list|(
operator|new
name|ProvidedServiceStub
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|=
name|MockControl
operator|.
name|createControl
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|adminServiceMockControl
operator|=
name|MockControl
operator|.
name|createControl
argument_list|(
name|AdminService
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|adminService
operator|=
operator|(
name|AdminService
operator|)
name|this
operator|.
name|adminServiceMockControl
operator|.
name|getMock
argument_list|()
expr_stmt|;
name|this
operator|.
name|mockRequest
operator|=
operator|(
name|HttpServletRequest
operator|)
name|this
operator|.
name|requestMockControl
operator|.
name|getMock
argument_list|()
expr_stmt|;
name|this
operator|.
name|stub
operator|=
operator|(
name|ServiceFactoryStub
operator|)
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|lookup
argument_list|(
name|ServiceFactory
operator|.
name|class
argument_list|,
name|ComponentType
operator|.
name|SERVICEFACTORY
argument_list|)
expr_stmt|;
name|this
operator|.
name|stub
operator|.
name|setAdminService
argument_list|(
name|this
operator|.
name|adminService
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|incomingFeed
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.lucene.gdata.servlet.handler.AbstractFeedHandler.createFeedFromRequest(HttpServletRequest)'      */
DECL|method|testCreateFeedFromRequest
specifier|public
name|void
name|testCreateFeedFromRequest
parameter_list|()
throws|throws
name|ParseException
throws|,
name|IOException
throws|,
name|FeedHandlerException
block|{
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getParameter
argument_list|(
literal|"service"
argument_list|)
argument_list|,
name|this
operator|.
name|serviceName
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getReader
argument_list|()
argument_list|,
name|this
operator|.
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
name|AbstractFeedHandler
name|handler
init|=
operator|new
name|InsertFeedHandler
argument_list|()
decl_stmt|;
try|try
block|{
name|ServerBaseFeed
name|feed
init|=
name|handler
operator|.
name|createFeedFromRequest
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|feed
operator|.
name|getId
argument_list|()
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
name|fail
argument_list|(
literal|"unexpected exception -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|/*          * Test for not registered service          */
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getParameter
argument_list|(
literal|"service"
argument_list|)
argument_list|,
literal|"some other service"
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|InsertFeedHandler
argument_list|()
expr_stmt|;
try|try
block|{
name|ServerBaseFeed
name|feed
init|=
name|handler
operator|.
name|createFeedFromRequest
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|" exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FeedHandlerException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
name|handler
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|/*          * Test for IOException          */
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getParameter
argument_list|(
literal|"service"
argument_list|)
argument_list|,
name|this
operator|.
name|serviceName
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getReader
argument_list|()
argument_list|,
name|this
operator|.
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|InsertFeedHandler
argument_list|()
expr_stmt|;
try|try
block|{
name|ServerBaseFeed
name|feed
init|=
name|handler
operator|.
name|createFeedFromRequest
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|" exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|handler
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.lucene.gdata.servlet.handler.AbstractFeedHandler.createRequestedAccount(HttpServletRequest)'      */
DECL|method|testCreateRequestedAccount
specifier|public
name|void
name|testCreateRequestedAccount
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
throws|,
name|ServiceException
block|{
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getParameter
argument_list|(
name|AbstractFeedHandler
operator|.
name|PARAMETER_ACCOUNT
argument_list|)
argument_list|,
name|this
operator|.
name|accountName
argument_list|)
expr_stmt|;
name|GDataAccount
name|a
init|=
operator|new
name|GDataAccount
argument_list|()
decl_stmt|;
name|a
operator|.
name|setName
argument_list|(
literal|"helloworld"
argument_list|)
expr_stmt|;
name|this
operator|.
name|adminServiceMockControl
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|adminService
operator|.
name|getAccount
argument_list|(
name|this
operator|.
name|accountName
argument_list|)
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|adminServiceMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
name|AbstractFeedHandler
name|handler
init|=
operator|new
name|InsertFeedHandler
argument_list|()
decl_stmt|;
try|try
block|{
name|GDataAccount
name|account
init|=
name|handler
operator|.
name|createRequestedAccount
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|a
argument_list|,
name|account
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
name|fail
argument_list|(
literal|"unexpected exception -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|adminServiceMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|adminServiceMockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|/*          *Test for service exception           */
name|this
operator|.
name|requestMockControl
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|mockRequest
operator|.
name|getParameter
argument_list|(
name|AbstractFeedHandler
operator|.
name|PARAMETER_ACCOUNT
argument_list|)
argument_list|,
name|this
operator|.
name|accountName
argument_list|)
expr_stmt|;
name|a
operator|.
name|setName
argument_list|(
literal|"helloworld"
argument_list|)
expr_stmt|;
name|this
operator|.
name|adminServiceMockControl
operator|.
name|expectAndDefaultThrow
argument_list|(
name|this
operator|.
name|adminService
operator|.
name|getAccount
argument_list|(
name|this
operator|.
name|accountName
argument_list|)
argument_list|,
operator|new
name|ServiceException
argument_list|(
name|GDataResponse
operator|.
name|BAD_REQUEST
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|adminServiceMockControl
operator|.
name|replay
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|InsertFeedHandler
argument_list|()
expr_stmt|;
try|try
block|{
name|GDataAccount
name|account
init|=
name|handler
operator|.
name|createRequestedAccount
argument_list|(
name|this
operator|.
name|mockRequest
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|" exception expected "
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
name|assertEquals
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|handler
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|requestMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestMockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|adminServiceMockControl
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|adminServiceMockControl
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
