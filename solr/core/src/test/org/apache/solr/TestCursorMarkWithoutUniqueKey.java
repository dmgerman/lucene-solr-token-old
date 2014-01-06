begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|schema
operator|.
name|SchemaField
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CursorMarkParams
operator|.
name|CURSOR_MARK_START
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
name|After
import|;
end_import
begin_comment
comment|/**  * Tests that cursor requests fail unless the IndexSchema defines a uniqueKey.  */
end_comment
begin_class
DECL|class|TestCursorMarkWithoutUniqueKey
specifier|public
class|class
name|TestCursorMarkWithoutUniqueKey
extends|extends
name|SolrTestCaseJ4
block|{
comment|/** solrconfig.xml file name, shared with other cursor related tests */
DECL|field|TEST_SOLRCONFIG_NAME
specifier|public
specifier|final
specifier|static
name|String
name|TEST_SOLRCONFIG_NAME
init|=
name|CursorPagingTest
operator|.
name|TEST_SOLRCONFIG_NAME
decl_stmt|;
DECL|field|TEST_SCHEMAXML_NAME
specifier|public
specifier|final
specifier|static
name|String
name|TEST_SCHEMAXML_NAME
init|=
literal|"schema-minimal.xml"
decl_stmt|;
annotation|@
name|Before
DECL|method|beforeSetupCore
specifier|public
name|void
name|beforeSetupCore
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.useFilterForSortedQuery"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
name|TEST_SOLRCONFIG_NAME
argument_list|,
name|TEST_SCHEMAXML_NAME
argument_list|)
expr_stmt|;
name|SchemaField
name|uniqueKeyField
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"This test requires that the schema not have a uniquekey field -- someone violated that in "
operator|+
name|TEST_SCHEMAXML_NAME
argument_list|,
name|uniqueKeyField
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|afterDestroyCore
specifier|public
name|void
name|afterDestroyCore
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"fld"
argument_list|,
literal|"val"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|ignoreException
argument_list|(
literal|"Cursor functionality is not available unless the IndexSchema defines a uniqueKey field"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"fld desc"
argument_list|,
literal|"cursorMark"
argument_list|,
name|CURSOR_MARK_START
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"No exception when querying with a cursorMark with no uniqueKey defined."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|unIgnoreException
argument_list|(
literal|"Cursor functionality is not available unless the IndexSchema defines a uniqueKey field"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
