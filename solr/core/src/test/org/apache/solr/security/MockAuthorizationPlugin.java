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
DECL|class|MockAuthorizationPlugin
specifier|public
class|class
name|MockAuthorizationPlugin
implements|implements
name|AuthorizationPlugin
block|{
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
DECL|field|denyUsers
specifier|static
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|denyUsers
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|predicate
specifier|static
name|Predicate
argument_list|<
name|AuthorizationContext
argument_list|>
name|predicate
decl_stmt|;
annotation|@
name|Override
DECL|method|authorize
specifier|public
name|AuthorizationResponse
name|authorize
parameter_list|(
name|AuthorizationContext
name|context
parameter_list|)
block|{
name|String
name|uname
init|=
name|context
operator|.
name|getUserPrincipal
argument_list|()
operator|==
literal|null
condition|?
literal|null
else|:
name|context
operator|.
name|getUserPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|predicate
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|predicate
operator|.
name|test
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
operator|new
name|AuthorizationResponse
argument_list|(
literal|200
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
return|return
operator|new
name|AuthorizationResponse
argument_list|(
name|e
operator|.
name|code
argument_list|()
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|uname
operator|==
literal|null
condition|)
name|uname
operator|=
name|context
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"uname"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"User request: "
operator|+
name|uname
argument_list|)
expr_stmt|;
if|if
condition|(
name|denyUsers
operator|.
name|contains
argument_list|(
name|uname
argument_list|)
condition|)
return|return
operator|new
name|AuthorizationResponse
argument_list|(
literal|403
argument_list|)
return|;
else|else
return|return
operator|new
name|AuthorizationResponse
argument_list|(
literal|200
argument_list|)
return|;
block|}
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
name|initInfo
parameter_list|)
block|{   }
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
