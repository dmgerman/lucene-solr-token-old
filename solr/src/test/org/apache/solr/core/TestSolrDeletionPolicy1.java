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
name|lucene
operator|.
name|index
operator|.
name|IndexCommit
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
name|junit
operator|.
name|Before
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
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
begin_comment
comment|/**  * @version $Id$  */
end_comment
begin_class
DECL|class|TestSolrDeletionPolicy1
specifier|public
class|class
name|TestSolrDeletionPolicy1
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
literal|"solrconfig-delpolicy1.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp
specifier|public
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
name|clearIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|addDocs
specifier|private
name|void
name|addDocs
parameter_list|()
block|{
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
literal|2
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
literal|2
argument_list|)
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
literal|"id:[0 TO 2]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
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
literal|3
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
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"return all docs"
argument_list|,
name|req
argument_list|(
literal|"id:[0 TO 3]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
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
literal|4
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
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"return all docs"
argument_list|,
name|req
argument_list|(
literal|"id:[0 TO 4]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=4]"
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
literal|5
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
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"return all docs"
argument_list|,
name|req
argument_list|(
literal|"id:[0 TO 5]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=5]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKeepOptimizedOnlyCommits
specifier|public
name|void
name|testKeepOptimizedOnlyCommits
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
name|addDocs
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|IndexCommit
argument_list|>
name|commits
init|=
name|delPolicy
operator|.
name|getCommits
argument_list|()
decl_stmt|;
name|IndexCommit
name|latest
init|=
name|delPolicy
operator|.
name|getLatestCommit
argument_list|()
decl_stmt|;
for|for
control|(
name|Long
name|version
range|:
name|commits
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|commits
operator|.
name|get
argument_list|(
name|version
argument_list|)
operator|==
name|latest
condition|)
continue|continue;
name|assertTrue
argument_list|(
name|commits
operator|.
name|get
argument_list|(
name|version
argument_list|)
operator|.
name|isOptimized
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNumCommitsConfigured
specifier|public
name|void
name|testNumCommitsConfigured
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
name|addDocs
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|IndexCommit
argument_list|>
name|commits
init|=
name|delPolicy
operator|.
name|getCommits
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|commits
operator|.
name|size
argument_list|()
operator|<=
operator|(
call|(
name|SolrDeletionPolicy
call|)
argument_list|(
name|delPolicy
operator|.
name|getWrappedDeletionPolicy
argument_list|()
argument_list|)
operator|)
operator|.
name|getMaxOptimizedCommitsToKeep
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitAge
specifier|public
name|void
name|testCommitAge
parameter_list|()
throws|throws
name|InterruptedException
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
name|addDocs
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|IndexCommit
argument_list|>
name|commits
init|=
name|delPolicy
operator|.
name|getCommits
argument_list|()
decl_stmt|;
name|IndexCommit
name|ic
init|=
name|delPolicy
operator|.
name|getLatestCommit
argument_list|()
decl_stmt|;
name|String
name|agestr
init|=
operator|(
call|(
name|SolrDeletionPolicy
call|)
argument_list|(
name|delPolicy
operator|.
name|getWrappedDeletionPolicy
argument_list|()
argument_list|)
operator|)
operator|.
name|getMaxCommitAge
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[a-zA-Z]"
argument_list|,
literal|""
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"-"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|long
name|age
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|agestr
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|age
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
literal|6
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
literal|6
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"return all docs"
argument_list|,
name|req
argument_list|(
literal|"id:[0 TO 6]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=6]"
argument_list|)
expr_stmt|;
name|commits
operator|=
name|delPolicy
operator|.
name|getCommits
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|commits
operator|.
name|containsKey
argument_list|(
name|ic
operator|.
name|getVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
