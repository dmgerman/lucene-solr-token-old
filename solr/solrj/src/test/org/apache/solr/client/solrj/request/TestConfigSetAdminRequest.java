begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.request
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
name|request
package|;
end_package
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
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|ConfigSetAdminResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/**  * Basic error checking of ConfigSetAdminRequests.  */
end_comment
begin_class
DECL|class|TestConfigSetAdminRequest
specifier|public
class|class
name|TestConfigSetAdminRequest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testNoAction
specifier|public
name|void
name|testNoAction
parameter_list|()
block|{
name|ConfigSetAdminRequest
name|request
init|=
operator|new
name|MyConfigSetAdminRequest
argument_list|()
decl_stmt|;
name|verifyException
argument_list|(
name|request
argument_list|,
literal|"action"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreate
specifier|public
name|void
name|testCreate
parameter_list|()
block|{
name|ConfigSetAdminRequest
operator|.
name|Create
name|create
init|=
operator|new
name|ConfigSetAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|verifyException
argument_list|(
name|create
argument_list|,
literal|"ConfigSet"
argument_list|)
expr_stmt|;
name|create
operator|.
name|setConfigSetName
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|verifyException
argument_list|(
name|create
argument_list|,
literal|"Base ConfigSet"
argument_list|)
expr_stmt|;
name|create
operator|.
name|setBaseConfigSetName
argument_list|(
literal|"baseConfigSet"
argument_list|)
expr_stmt|;
name|create
operator|.
name|getParams
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelete
specifier|public
name|void
name|testDelete
parameter_list|()
block|{
name|ConfigSetAdminRequest
operator|.
name|Delete
name|delete
init|=
operator|new
name|ConfigSetAdminRequest
operator|.
name|Delete
argument_list|()
decl_stmt|;
name|verifyException
argument_list|(
name|delete
argument_list|,
literal|"ConfigSet"
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyException
specifier|private
name|void
name|verifyException
parameter_list|(
name|ConfigSetAdminRequest
name|request
parameter_list|,
name|String
name|errorContains
parameter_list|)
block|{
try|try
block|{
name|request
operator|.
name|getParams
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Expected exception message to contain: "
operator|+
name|errorContains
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|errorContains
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MyConfigSetAdminRequest
specifier|private
specifier|static
class|class
name|MyConfigSetAdminRequest
extends|extends
name|ConfigSetAdminRequest
argument_list|<
name|MyConfigSetAdminRequest
argument_list|,
name|ConfigSetAdminResponse
argument_list|>
block|{
DECL|method|MyConfigSetAdminRequest
specifier|public
name|MyConfigSetAdminRequest
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|getThis
specifier|public
name|MyConfigSetAdminRequest
name|getThis
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|createResponse
specifier|public
name|ConfigSetAdminResponse
name|createResponse
parameter_list|(
name|SolrClient
name|client
parameter_list|)
block|{
return|return
operator|new
name|ConfigSetAdminResponse
argument_list|()
return|;
block|}
block|}
empty_stmt|;
block|}
end_class
end_unit
