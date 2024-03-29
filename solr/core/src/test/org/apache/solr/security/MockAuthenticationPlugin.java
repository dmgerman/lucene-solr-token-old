begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|function
operator|.
name|Predicate
import|;
end_import
begin_class
DECL|class|MockAuthenticationPlugin
specifier|public
class|class
name|MockAuthenticationPlugin
extends|extends
name|AuthenticationPlugin
block|{
DECL|field|predicate
specifier|static
name|Predicate
argument_list|<
name|ServletRequest
argument_list|>
name|predicate
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|pluginConfig
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|doAuthenticate
specifier|public
name|void
name|doAuthenticate
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|filterChain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|String
name|user
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|predicate
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|predicate
operator|.
name|test
argument_list|(
name|request
argument_list|)
condition|)
block|{
name|user
operator|=
operator|(
name|String
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
name|Principal
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|removeAttribute
argument_list|(
name|Principal
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|forward
argument_list|(
name|user
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|filterChain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{    }
block|}
end_class
end_unit
