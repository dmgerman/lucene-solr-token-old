begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|Test
import|;
end_import
begin_class
DECL|class|DataDrivenBlockJoinTest
specifier|public
class|class
name|DataDrivenBlockJoinTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|tmpSolrHome
specifier|private
name|File
name|tmpSolrHome
decl_stmt|;
DECL|field|tmpConfDir
specifier|private
name|File
name|tmpConfDir
decl_stmt|;
DECL|field|collection
specifier|private
specifier|static
specifier|final
name|String
name|collection
init|=
literal|"collection1"
decl_stmt|;
DECL|field|confDir
specifier|private
specifier|static
specifier|final
name|String
name|confDir
init|=
name|collection
operator|+
literal|"/conf"
decl_stmt|;
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|tmpSolrHome
operator|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|tmpConfDir
operator|=
operator|new
name|File
argument_list|(
name|tmpSolrHome
argument_list|,
name|confDir
argument_list|)
expr_stmt|;
name|File
name|testHomeConfDir
init|=
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|,
name|confDir
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
literal|"solrconfig-schemaless.xml"
argument_list|)
argument_list|,
name|tmpConfDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
literal|"schema-add-schema-fields-update-processor.xml"
argument_list|)
argument_list|,
name|tmpConfDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|,
name|tmpConfDir
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-schemaless.xml"
argument_list|,
literal|"schema-add-schema-fields-update-processor.xml"
argument_list|,
name|tmpSolrHome
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddNestedDocuments
specifier|public
name|void
name|testAddNestedDocuments
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
literal|"<add>"
operator|+
literal|"<doc>"
operator|+
literal|"<field name='id'>1</field>"
operator|+
literal|"<field name='parent'>X</field>"
operator|+
literal|"<field name='hierarchical_numbering'>8</field>"
operator|+
literal|"<doc>"
operator|+
literal|"<field name='id'>2</field>"
operator|+
literal|"<field name='child'>y</field>"
operator|+
literal|"<field name='hierarchical_numbering'>8.138</field>"
operator|+
literal|"<doc>"
operator|+
literal|"<field name='id'>3</field>"
operator|+
literal|"<field name='grandchild'>z</field>"
operator|+
literal|"<field name='hierarchical_numbering'>8.138.4498</field>"
operator|+
literal|"</doc>"
operator|+
literal|"</doc>"
operator|+
literal|"</doc>"
operator|+
literal|"</add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
