begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
package|;
end_package
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
name|GDataAccount
operator|.
name|AccountRole
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
name|authentication
operator|.
name|AuthenticationController
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
name|Component
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
begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment
begin_class
annotation|@
name|Component
argument_list|(
name|componentType
operator|=
name|ComponentType
operator|.
name|AUTHENTICATIONCONTROLLER
argument_list|)
DECL|class|AuthenticationContorllerStub
specifier|public
class|class
name|AuthenticationContorllerStub
implements|implements
name|AuthenticationController
block|{
DECL|field|controller
specifier|public
specifier|static
name|AuthenticationController
name|controller
decl_stmt|;
comment|/**      *       */
DECL|method|AuthenticationContorllerStub
specifier|public
name|AuthenticationContorllerStub
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/**      * @see org.apache.lucene.gdata.server.authentication.AuthenticationController#authenticatAccount(org.apache.lucene.gdata.data.GDataAccount, java.lang.String, java.lang.String)      */
DECL|method|authenticatAccount
specifier|public
name|String
name|authenticatAccount
parameter_list|(
name|GDataAccount
name|account
parameter_list|,
name|String
name|requestIp
parameter_list|)
block|{
return|return
name|controller
operator|.
name|authenticatAccount
argument_list|(
name|account
argument_list|,
name|requestIp
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.server.authentication.AuthenticationController#authenticateToken(java.lang.String, java.lang.String, org.apache.lucene.gdata.data.GDataAccount.AccountRole, java.lang.String)      */
DECL|method|authenticateToken
specifier|public
name|boolean
name|authenticateToken
parameter_list|(
name|String
name|token
parameter_list|,
name|String
name|requestIp
parameter_list|,
name|AccountRole
name|role
parameter_list|,
name|String
name|serviceName
parameter_list|)
block|{
return|return
name|controller
operator|.
name|authenticateToken
argument_list|(
name|token
argument_list|,
name|requestIp
argument_list|,
name|role
argument_list|,
name|serviceName
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.server.registry.ServerComponent#initialize()      */
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|()
block|{     }
comment|/**      * @see org.apache.lucene.gdata.server.registry.ServerComponent#destroy()      */
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{     }
block|}
end_class
end_unit
