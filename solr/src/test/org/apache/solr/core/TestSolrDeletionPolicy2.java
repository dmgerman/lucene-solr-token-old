begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|junit
operator|.
name|BeforeClass
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
comment|/**  * @version $Id$  */
end_comment
begin_class
DECL|class|TestSolrDeletionPolicy2
specifier|public
class|class
name|TestSolrDeletionPolicy2
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-delpolicy2.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFakeDeletionPolicyClass
specifier|public
name|void
name|testFakeDeletionPolicyClass
parameter_list|()
block|{
name|IndexDeletionPolicyWrapper
name|delPolicy
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDeletionPolicy
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|delPolicy
operator|.
name|getWrappedDeletionPolicy
argument_list|()
operator|instanceof
name|FakeDeletionPolicy
argument_list|)
expr_stmt|;
name|FakeDeletionPolicy
name|f
init|=
operator|(
name|FakeDeletionPolicy
operator|)
name|delPolicy
operator|.
name|getWrappedDeletionPolicy
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"value1"
operator|.
name|equals
argument_list|(
name|f
operator|.
name|getVar1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"value2"
operator|.
name|equals
argument_list|(
name|f
operator|.
name|getVar2
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"name"
argument_list|,
literal|"name"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"onInit"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"test.org.apache.solr.core.FakeDeletionPolicy.onInit"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"return all docs"
argument_list|,
name|req
argument_list|(
literal|"id:[0 TO 1]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"onCommit"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"test.org.apache.solr.core.FakeDeletionPolicy.onCommit"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"onInit"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"onCommit"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
