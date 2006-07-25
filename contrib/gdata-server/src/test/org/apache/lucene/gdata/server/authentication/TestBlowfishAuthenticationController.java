begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.server.authentication
package|package
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
name|UnsupportedEncodingException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|BadPaddingException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|IllegalBlockSizeException
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
name|GDataAccount
operator|.
name|AccountRole
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
begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment
begin_class
DECL|class|TestBlowfishAuthenticationController
specifier|public
class|class
name|TestBlowfishAuthenticationController
extends|extends
name|TestCase
block|{
DECL|field|controller
specifier|private
name|BlowfishAuthenticationController
name|controller
decl_stmt|;
DECL|field|key
specifier|private
name|String
name|key
init|=
literal|"myKey"
decl_stmt|;
DECL|field|accountName
specifier|private
name|String
name|accountName
init|=
literal|"simon"
decl_stmt|;
DECL|field|clientIp
specifier|private
name|String
name|clientIp
init|=
literal|"192.168.0.127"
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|controller
operator|=
operator|new
name|BlowfishAuthenticationController
argument_list|()
expr_stmt|;
name|this
operator|.
name|controller
operator|.
name|setKey
argument_list|(
name|this
operator|.
name|key
argument_list|)
expr_stmt|;
name|this
operator|.
name|controller
operator|.
name|initialize
argument_list|()
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
comment|/*      * Test method for 'org.apache.lucene.gdata.server.authentication.AuthenticationController.authenticatAccount(HttpServletRequest)'      */
DECL|method|testAuthenticatAccount
specifier|public
name|void
name|testAuthenticatAccount
parameter_list|()
throws|throws
name|IllegalBlockSizeException
throws|,
name|BadPaddingException
throws|,
name|AuthenticationException
throws|,
name|IOException
block|{
name|GDataAccount
name|account
init|=
operator|new
name|GDataAccount
argument_list|()
decl_stmt|;
name|account
operator|.
name|setName
argument_list|(
name|accountName
argument_list|)
expr_stmt|;
name|account
operator|.
name|setPassword
argument_list|(
literal|"testme"
argument_list|)
expr_stmt|;
name|account
operator|.
name|setRole
argument_list|(
name|AccountRole
operator|.
name|ENTRYAMINISTRATOR
argument_list|)
expr_stmt|;
name|String
name|token
init|=
name|this
operator|.
name|controller
operator|.
name|authenticatAccount
argument_list|(
name|account
argument_list|,
name|this
operator|.
name|clientIp
argument_list|)
decl_stmt|;
name|String
name|notSame
init|=
name|this
operator|.
name|controller
operator|.
name|calculateAuthToken
argument_list|(
literal|"192.168.0"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|account
operator|.
name|getRolesAsInt
argument_list|()
argument_list|)
argument_list|,
name|this
operator|.
name|accountName
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|notSame
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|String
name|authString
init|=
literal|"192.168.0#"
operator|+
name|this
operator|.
name|accountName
operator|+
literal|"#"
operator|+
name|account
operator|.
name|getRolesAsInt
argument_list|()
operator|+
literal|"#"
decl_stmt|;
name|assertTrue
argument_list|(
name|this
operator|.
name|controller
operator|.
name|deCryptAuthToken
argument_list|(
name|token
argument_list|)
operator|.
name|startsWith
argument_list|(
name|authString
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|this
operator|.
name|controller
operator|.
name|deCryptAuthToken
argument_list|(
name|notSame
argument_list|)
operator|.
name|startsWith
argument_list|(
name|authString
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.lucene.gdata.server.authentication.AuthenticationController.authenticateToken(String)'      */
DECL|method|testAuthenticateToken
specifier|public
name|void
name|testAuthenticateToken
parameter_list|()
throws|throws
name|IllegalBlockSizeException
throws|,
name|BadPaddingException
throws|,
name|UnsupportedEncodingException
throws|,
name|AuthenticationException
block|{
name|GDataAccount
name|account
init|=
operator|new
name|GDataAccount
argument_list|()
decl_stmt|;
name|account
operator|.
name|setName
argument_list|(
literal|"simon"
argument_list|)
expr_stmt|;
name|account
operator|.
name|setPassword
argument_list|(
literal|"testme"
argument_list|)
expr_stmt|;
name|account
operator|.
name|setRole
argument_list|(
name|AccountRole
operator|.
name|ENTRYAMINISTRATOR
argument_list|)
expr_stmt|;
name|String
name|token
init|=
name|this
operator|.
name|controller
operator|.
name|calculateAuthToken
argument_list|(
literal|"192.168.0"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|account
operator|.
name|getRolesAsInt
argument_list|()
argument_list|)
argument_list|,
name|this
operator|.
name|accountName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|this
operator|.
name|controller
operator|.
name|authenticateToken
argument_list|(
name|token
argument_list|,
name|this
operator|.
name|clientIp
argument_list|,
name|AccountRole
operator|.
name|ENTRYAMINISTRATOR
argument_list|,
name|this
operator|.
name|accountName
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|this
operator|.
name|controller
operator|.
name|authenticateToken
argument_list|(
name|token
argument_list|,
name|this
operator|.
name|clientIp
argument_list|,
name|AccountRole
operator|.
name|USER
argument_list|,
name|this
operator|.
name|accountName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|this
operator|.
name|controller
operator|.
name|authenticateToken
argument_list|(
name|token
argument_list|,
name|this
operator|.
name|clientIp
argument_list|,
name|AccountRole
operator|.
name|USERADMINISTRATOR
argument_list|,
literal|"someOtherAccount"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|controller
operator|.
name|authenticateToken
argument_list|(
name|token
operator|+
literal|"test"
argument_list|,
name|this
operator|.
name|clientIp
argument_list|,
name|AccountRole
operator|.
name|ENTRYAMINISTRATOR
argument_list|,
name|this
operator|.
name|accountName
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO: handle exception
block|}
name|this
operator|.
name|controller
operator|.
name|setLoginTimeout
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|this
operator|.
name|controller
operator|.
name|authenticateToken
argument_list|(
name|token
argument_list|,
name|this
operator|.
name|clientIp
argument_list|,
name|AccountRole
operator|.
name|ENTRYAMINISTRATOR
argument_list|,
name|this
operator|.
name|accountName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
