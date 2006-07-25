begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IOException
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|InsertFeedHandler
specifier|public
class|class
name|InsertFeedHandler
extends|extends
name|AbstractFeedHandler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|InsertFeedHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * @see org.apache.lucene.gdata.servlet.handler.GDataRequestHandler#processRequest(javax.servlet.http.HttpServletRequest,      *      javax.servlet.http.HttpServletResponse)      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|processRequest
specifier|public
name|void
name|processRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|super
operator|.
name|processRequest
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|authenticated
condition|)
block|{
name|AdminService
name|service
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ServerBaseFeed
name|feed
init|=
name|createFeedFromRequest
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|GDataAccount
name|account
init|=
name|createRequestedAccount
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|GDataServerRegistry
name|registry
init|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
decl_stmt|;
name|ServiceFactory
name|serviceFactory
init|=
name|registry
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
decl_stmt|;
if|if
condition|(
name|serviceFactory
operator|==
literal|null
condition|)
block|{
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
literal|"required component is not available"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FeedHandlerException
argument_list|(
literal|"Can't save feed - ServiceFactory is null"
argument_list|)
throw|;
block|}
name|service
operator|=
name|serviceFactory
operator|.
name|getAdminService
argument_list|()
expr_stmt|;
name|service
operator|.
name|createFeed
argument_list|(
name|feed
argument_list|,
name|account
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
literal|"can not create feed"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Can not create feed -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
name|error
argument_list|(
literal|"Can not create feed -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
name|service
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|sendResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
