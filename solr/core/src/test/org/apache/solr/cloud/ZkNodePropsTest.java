begin_unit
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|cloud
operator|.
name|ZkNodeProps
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
name|cloud
operator|.
name|ZkStateReader
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
begin_class
DECL|class|ZkNodePropsTest
specifier|public
class|class
name|ZkNodePropsTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"prop1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"prop2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"prop3"
argument_list|,
literal|"value3"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"prop4"
argument_list|,
literal|"value4"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"prop5"
argument_list|,
literal|"value5"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"prop6"
argument_list|,
literal|"value6"
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|zkProps
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|zkProps
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|props2
init|=
name|ZkNodeProps
operator|.
name|load
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|props2
operator|.
name|getStr
argument_list|(
literal|"prop1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
name|props2
operator|.
name|getStr
argument_list|(
literal|"prop2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value3"
argument_list|,
name|props2
operator|.
name|getStr
argument_list|(
literal|"prop3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value4"
argument_list|,
name|props2
operator|.
name|getStr
argument_list|(
literal|"prop4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value5"
argument_list|,
name|props2
operator|.
name|getStr
argument_list|(
literal|"prop5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value6"
argument_list|,
name|props2
operator|.
name|getStr
argument_list|(
literal|"prop6"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
