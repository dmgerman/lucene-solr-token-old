begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *   */
end_comment
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
name|SolrException
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
comment|/**  * Test that checks that long running queries are exited by Solr using the  * SolrQueryTimeoutImpl implementation.  */
end_comment
begin_class
DECL|class|ExitableDirectoryReaderTest
specifier|public
class|class
name|ExitableDirectoryReaderTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|NUM_DOCS_PER_TYPE
specifier|static
name|int
name|NUM_DOCS_PER_TYPE
init|=
literal|400
decl_stmt|;
DECL|field|assertionString
specifier|static
specifier|final
name|String
name|assertionString
init|=
literal|"//result[@numFound='"
operator|+
operator|(
name|NUM_DOCS_PER_TYPE
operator|-
literal|1
operator|)
operator|+
literal|"']"
decl_stmt|;
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
name|initCore
argument_list|(
literal|"solrconfig-nocache.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|createIndex
specifier|public
specifier|static
name|void
name|createIndex
parameter_list|()
block|{
name|int
name|counter
init|=
literal|1
decl_stmt|;
for|for
control|(
init|;
operator|(
name|counter
operator|%
name|NUM_DOCS_PER_TYPE
operator|)
operator|!=
literal|0
condition|;
name|counter
operator|++
control|)
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|,
literal|"name"
argument_list|,
literal|"a"
operator|+
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
for|for
control|(
init|;
operator|(
name|counter
operator|%
name|NUM_DOCS_PER_TYPE
operator|)
operator|!=
literal|0
condition|;
name|counter
operator|++
control|)
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|,
literal|"name"
argument_list|,
literal|"b"
operator|+
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
for|for
control|(
init|;
name|counter
operator|%
name|NUM_DOCS_PER_TYPE
operator|!=
literal|0
condition|;
name|counter
operator|++
control|)
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|,
literal|"name"
argument_list|,
literal|"dummy term doc"
operator|+
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrefixQuery
specifier|public
name|void
name|testPrefixQuery
parameter_list|()
block|{
name|assertQEx
argument_list|(
literal|""
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:a*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"timeAllowed"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:a*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"timeAllowed"
argument_list|,
literal|"1000"
argument_list|)
argument_list|,
name|assertionString
argument_list|)
expr_stmt|;
name|assertQEx
argument_list|(
literal|""
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:a*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"timeAllowed"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:b*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"timeAllowed"
argument_list|,
literal|"1000"
argument_list|)
argument_list|,
name|assertionString
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:b*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"timeAllowed"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|,
name|assertionString
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:b*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"timeAllowed"
argument_list|,
literal|"-7"
argument_list|)
argument_list|)
expr_stmt|;
comment|// negative timeAllowed should disable timeouts
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:b*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueriesOnDocsWithMultipleTerms
specifier|public
name|void
name|testQueriesOnDocsWithMultipleTerms
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:dummy"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"timeAllowed"
argument_list|,
literal|"1000"
argument_list|)
argument_list|,
name|assertionString
argument_list|)
expr_stmt|;
comment|// This should pass even though this may take more than the 'timeAllowed' time, it doesn't take long
comment|// to iterate over 1 term (dummy).
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:dummy"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"timeAllowed"
argument_list|,
literal|"1000"
argument_list|)
argument_list|,
name|assertionString
argument_list|)
expr_stmt|;
name|assertQEx
argument_list|(
literal|""
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:doc*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"timeAllowed"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
