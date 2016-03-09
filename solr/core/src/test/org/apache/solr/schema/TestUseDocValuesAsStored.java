begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|core
operator|.
name|AbstractBadConfigTestBase
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
begin_comment
comment|/**  * Tests the useDocValuesAsStored functionality.  */
end_comment
begin_class
DECL|class|TestUseDocValuesAsStored
specifier|public
class|class
name|TestUseDocValuesAsStored
extends|extends
name|AbstractBadConfigTestBase
block|{
DECL|field|id
specifier|private
name|int
name|id
init|=
literal|1
decl_stmt|;
DECL|field|tmpSolrHome
specifier|private
specifier|static
name|File
name|tmpSolrHome
decl_stmt|;
DECL|field|tmpConfDir
specifier|private
specifier|static
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
DECL|method|initManagedSchemaCore
specifier|private
name|void
name|initManagedSchemaCore
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
literal|"solrconfig-managed-schema.xml"
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
literal|"solrconfig-basic.xml"
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
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
literal|"schema-one-field-no-dynamic-field.xml"
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
literal|"schema-one-field-no-dynamic-field-unique-key.xml"
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
literal|"enumsConfig.xml"
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
literal|"schema-non-stored-docvalues.xml"
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
literal|"schema-minimal.xml"
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
literal|"schema_codec.xml"
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
literal|"schema-bm25.xml"
argument_list|)
argument_list|,
name|tmpConfDir
argument_list|)
expr_stmt|;
comment|// initCore will trigger an upgrade to managed schema, since the solrconfig has
comment|//<schemaFactory class="ManagedIndexSchemaFactory" ... />
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
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
name|initCore
argument_list|(
literal|"solrconfig-managed-schema.xml"
argument_list|,
literal|"schema-non-stored-docvalues.xml"
argument_list|,
name|tmpSolrHome
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|afterClass
specifier|private
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"enable.update.log"
argument_list|)
expr_stmt|;
block|}
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
literal|"basic"
return|;
block|}
annotation|@
name|Test
DECL|method|testOnEmptyIndex
specifier|public
name|void
name|testOnEmptyIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"test_nonstored_dv_str"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,test_nonstored_dv_str"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"xyz"
argument_list|,
literal|"test_nonstored_dv_str"
argument_list|,
literal|"xyz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"test_nonstored_dv_str"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,test_nonstored_dv_str"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|,
literal|"/response/docs==["
operator|+
literal|"{'id':'xyz','test_nonstored_dv_str':'xyz'}"
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|,
literal|"/response/docs==["
operator|+
literal|"{'id':'xyz','test_nonstored_dv_str':'xyz'}"
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"test_nonstored_dv_str"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|,
literal|"/response/docs==["
operator|+
literal|"{'test_nonstored_dv_str':'xyz'}"
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,test_nonstored_dv_str"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|,
literal|"/response/docs==["
operator|+
literal|"{'id':'xyz','test_nonstored_dv_str':'xyz'}"
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"xyz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|,
literal|"/response/docs==["
operator|+
literal|"{'id':'xyz'}"
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSinglyValued
specifier|public
name|void
name|testSinglyValued
parameter_list|()
throws|throws
name|IOException
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|doTest
argument_list|(
literal|"check string value is correct"
argument_list|,
literal|"test_s_dvo"
argument_list|,
literal|"str"
argument_list|,
literal|"keyword"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check int value is correct"
argument_list|,
literal|"test_i_dvo"
argument_list|,
literal|"int"
argument_list|,
literal|"1234"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check double value is correct"
argument_list|,
literal|"test_d_dvo"
argument_list|,
literal|"double"
argument_list|,
literal|"1.234"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check long value is correct"
argument_list|,
literal|"test_l_dvo"
argument_list|,
literal|"long"
argument_list|,
literal|"12345"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check float value is correct"
argument_list|,
literal|"test_f_dvo"
argument_list|,
literal|"float"
argument_list|,
literal|"1.234"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check dt value is correct"
argument_list|,
literal|"test_dt_dvo"
argument_list|,
literal|"date"
argument_list|,
literal|"1976-07-04T12:08:56.235Z"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check stored and docValues value is correct"
argument_list|,
literal|"test_s_dv"
argument_list|,
literal|"str"
argument_list|,
literal|"storedAndDocValues"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check non-stored and non-indexed is accessible"
argument_list|,
literal|"test_s_dvo2"
argument_list|,
literal|"str"
argument_list|,
literal|"gotIt"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"enumField"
argument_list|,
literal|"enum_dvo"
argument_list|,
literal|"str"
argument_list|,
literal|"Critical"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiValued
specifier|public
name|void
name|testMultiValued
parameter_list|()
throws|throws
name|IOException
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|doTest
argument_list|(
literal|"check string value is correct"
argument_list|,
literal|"test_ss_dvo"
argument_list|,
literal|"str"
argument_list|,
literal|"keyword"
argument_list|,
literal|"keyword2"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check int value is correct"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"int"
argument_list|,
literal|"1234"
argument_list|,
literal|"12345"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check double value is correct"
argument_list|,
literal|"test_ds_dvo"
argument_list|,
literal|"double"
argument_list|,
literal|"1.234"
argument_list|,
literal|"12.34"
argument_list|,
literal|"123.4"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check long value is correct"
argument_list|,
literal|"test_ls_dvo"
argument_list|,
literal|"long"
argument_list|,
literal|"12345"
argument_list|,
literal|"123456"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check float value is correct"
argument_list|,
literal|"test_fs_dvo"
argument_list|,
literal|"float"
argument_list|,
literal|"1.234"
argument_list|,
literal|"12.34"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check dt value is correct"
argument_list|,
literal|"test_dts_dvo"
argument_list|,
literal|"date"
argument_list|,
literal|"1976-07-04T12:08:56.235Z"
argument_list|,
literal|"1978-07-04T12:08:56.235Z"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check stored and docValues value is correct"
argument_list|,
literal|"test_ss_dv"
argument_list|,
literal|"str"
argument_list|,
literal|"storedAndDocValues"
argument_list|,
literal|"storedAndDocValues2"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"check non-stored and non-indexed is accessible"
argument_list|,
literal|"test_ss_dvo2"
argument_list|,
literal|"str"
argument_list|,
literal|"gotIt"
argument_list|,
literal|"gotIt2"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"enumField"
argument_list|,
literal|"enums_dvo"
argument_list|,
literal|"str"
argument_list|,
literal|"High"
argument_list|,
literal|"Critical"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleSearchResults
specifier|public
name|void
name|testMultipleSearchResults
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Three documents with different numbers of values for a field
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"myid1"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"101"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"102"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"103"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"myid2"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"201"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"202"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"myid3"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"301"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"302"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"303"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"304"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Multivalued and singly valued fields in the same document
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"myid4"
argument_list|,
literal|"test_s_dvo"
argument_list|,
literal|"hello"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"401"
argument_list|,
literal|"test_is_dvo"
argument_list|,
literal|"402"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test a field which has useDocValuesAsStored=false
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"myid5"
argument_list|,
literal|"nonstored_dv_str"
argument_list|,
literal|"dont see me"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"myid6"
argument_list|,
literal|"nonstored_dv_str"
argument_list|,
literal|"dont see me"
argument_list|,
literal|"test_s_dvo"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:myid*"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|)
argument_list|,
literal|"/response/docs==["
operator|+
literal|"{'id':'myid1','test_is_dvo':[101,102,103]},"
operator|+
literal|"{'id':'myid2','test_is_dvo':[201,202]},"
operator|+
literal|"{'id':'myid3','test_is_dvo':[301,302,303,304]},"
operator|+
literal|"{'id':'myid4','test_s_dvo':'hello','test_is_dvo':[401,402]},"
operator|+
literal|"{'id':'myid5'},"
operator|+
literal|"{'id':'myid6','test_s_dvo':'hello'}"
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testManagedSchema
specifier|public
name|void
name|testManagedSchema
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexSchema
name|oldSchema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|StrField
name|type
init|=
operator|new
name|StrField
argument_list|()
decl_stmt|;
name|type
operator|.
name|setTypeName
argument_list|(
literal|"str"
argument_list|)
expr_stmt|;
name|SchemaField
name|falseDVASField
init|=
operator|new
name|SchemaField
argument_list|(
literal|"false_dvas"
argument_list|,
name|type
argument_list|,
name|SchemaField
operator|.
name|INDEXED
operator||
name|SchemaField
operator|.
name|DOC_VALUES
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SchemaField
name|trueDVASField
init|=
operator|new
name|SchemaField
argument_list|(
literal|"true_dvas"
argument_list|,
name|type
argument_list|,
name|SchemaField
operator|.
name|INDEXED
operator||
name|SchemaField
operator|.
name|DOC_VALUES
operator||
name|SchemaField
operator|.
name|USE_DOCVALUES_AS_STORED
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|IndexSchema
name|newSchema
init|=
name|oldSchema
operator|.
name|addField
argument_list|(
name|falseDVASField
argument_list|)
operator|.
name|addField
argument_list|(
name|trueDVASField
argument_list|)
decl_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"myid1"
argument_list|,
literal|"false_dvas"
argument_list|,
literal|"101"
argument_list|,
literal|"true_dvas"
argument_list|,
literal|"102"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:myid*"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|)
argument_list|,
literal|"/response/docs==["
operator|+
literal|"{'id':'myid1', 'true_dvas':'102'}]"
argument_list|)
expr_stmt|;
block|}
DECL|method|doTest
specifier|private
name|void
name|doTest
parameter_list|(
name|String
name|desc
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|type
parameter_list|,
name|String
modifier|...
name|value
parameter_list|)
block|{
name|String
name|id
init|=
literal|""
operator|+
name|this
operator|.
name|id
operator|++
decl_stmt|;
name|String
index|[]
name|xpaths
init|=
operator|new
name|String
index|[
name|value
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|String
index|[]
name|fieldAndValues
init|=
operator|new
name|String
index|[
name|value
operator|.
name|length
operator|*
literal|2
operator|+
literal|2
index|]
decl_stmt|;
name|fieldAndValues
index|[
literal|0
index|]
operator|=
literal|"id"
expr_stmt|;
name|fieldAndValues
index|[
literal|1
index|]
operator|=
name|id
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|value
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|fieldAndValues
index|[
name|i
operator|*
literal|2
operator|+
literal|2
index|]
operator|=
name|field
expr_stmt|;
name|fieldAndValues
index|[
name|i
operator|*
literal|2
operator|+
literal|3
index|]
operator|=
name|value
index|[
name|i
index|]
expr_stmt|;
name|xpaths
index|[
name|i
index|]
operator|=
literal|"//arr[@name='"
operator|+
name|field
operator|+
literal|"']/"
operator|+
name|type
operator|+
literal|"["
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|"][.='"
operator|+
name|value
index|[
name|i
index|]
operator|+
literal|"']"
expr_stmt|;
block|}
name|xpaths
index|[
name|value
operator|.
name|length
index|]
operator|=
literal|"*[count(//arr[@name='"
operator|+
name|field
operator|+
literal|"']/"
operator|+
name|type
operator|+
literal|") = "
operator|+
name|value
operator|.
name|length
operator|+
literal|"]"
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|fieldAndValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|field
argument_list|,
name|value
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|xpaths
index|[
literal|0
index|]
operator|=
literal|"//"
operator|+
name|type
operator|+
literal|"[@name='"
operator|+
name|field
operator|+
literal|"'][.='"
operator|+
name|value
index|[
literal|0
index|]
operator|+
literal|"']"
expr_stmt|;
name|xpaths
index|[
literal|1
index|]
operator|=
literal|"*[count(//"
operator|+
name|type
operator|+
literal|"[@name='"
operator|+
name|field
operator|+
literal|"']) = 1]"
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fl
init|=
name|field
decl_stmt|;
name|assertQ
argument_list|(
name|desc
operator|+
literal|": "
operator|+
name|fl
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|id
argument_list|,
literal|"fl"
argument_list|,
name|fl
argument_list|)
argument_list|,
name|xpaths
argument_list|)
expr_stmt|;
name|fl
operator|=
name|field
operator|+
literal|",*"
expr_stmt|;
name|assertQ
argument_list|(
name|desc
operator|+
literal|": "
operator|+
name|fl
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|id
argument_list|,
literal|"fl"
argument_list|,
name|fl
argument_list|)
argument_list|,
name|xpaths
argument_list|)
expr_stmt|;
name|fl
operator|=
literal|"*"
operator|+
name|field
operator|.
name|substring
argument_list|(
name|field
operator|.
name|length
argument_list|()
operator|-
literal|3
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|desc
operator|+
literal|": "
operator|+
name|fl
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|id
argument_list|,
literal|"fl"
argument_list|,
name|fl
argument_list|)
argument_list|,
name|xpaths
argument_list|)
expr_stmt|;
name|fl
operator|=
literal|"*"
expr_stmt|;
name|assertQ
argument_list|(
name|desc
operator|+
literal|": "
operator|+
name|fl
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|id
argument_list|,
literal|"fl"
argument_list|,
name|fl
argument_list|)
argument_list|,
name|xpaths
argument_list|)
expr_stmt|;
name|fl
operator|=
name|field
operator|+
literal|",fakeFieldName"
expr_stmt|;
name|assertQ
argument_list|(
name|desc
operator|+
literal|": "
operator|+
name|fl
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|id
argument_list|,
literal|"fl"
argument_list|,
name|fl
argument_list|)
argument_list|,
name|xpaths
argument_list|)
expr_stmt|;
name|fl
operator|=
literal|"*"
expr_stmt|;
name|assertQ
argument_list|(
name|desc
operator|+
literal|": "
operator|+
name|fl
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
name|fl
argument_list|)
argument_list|,
name|xpaths
argument_list|)
expr_stmt|;
block|}
comment|// See SOLR-8740 for a discussion. This test is here to make sure we consciously change behavior of multiValued
comment|// fields given that we can now return docValues fields. The behavior we've guaranteed in the past is that if
comment|// multiValued fields are stored, they're returned in the document in the order they were added.
comment|// There are four new fieldTypes added:
comment|//<field name="test_mvt_dvt_st_str" type="string" indexed="true" multiValued="true" docValues="true"  stored="true"/>
comment|//<field name="test_mvt_dvt_sf_str" type="string" indexed="true" multiValued="true" docValues="true"  stored="false"/>
comment|//<field name="test_mvt_dvf_st_str" type="string" indexed="true" multiValued="true" docValues="false" stored="true"/>
comment|//<field name="test_mvt_dvu_st_str" type="string" indexed="true" multiValued="true"                   stored="true"/>
comment|//
comment|// If any of these tests break as a result of returning DocValues rather than stored values, make sure we reach some
comment|// consensus that any breaks on back-compat are A Good Thing and that that behavior is carefully documented!
annotation|@
name|Test
DECL|method|testMultivaluedOrdering
specifier|public
name|void
name|testMultivaluedOrdering
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
comment|// multiValued=true, docValues=true, stored=true. Should return in original order
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"test_mvt_dvt_st_str"
argument_list|,
literal|"cccc"
argument_list|,
literal|"test_mvt_dvt_st_str"
argument_list|,
literal|"aaaa"
argument_list|,
literal|"test_mvt_dvt_st_str"
argument_list|,
literal|"bbbb"
argument_list|)
argument_list|)
expr_stmt|;
comment|// multiValued=true, docValues=true, stored=false. Should return in sorted order
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"test_mvt_dvt_sf_str"
argument_list|,
literal|"cccc"
argument_list|,
literal|"test_mvt_dvt_sf_str"
argument_list|,
literal|"aaaa"
argument_list|,
literal|"test_mvt_dvt_sf_str"
argument_list|,
literal|"bbbb"
argument_list|)
argument_list|)
expr_stmt|;
comment|// multiValued=true, docValues=false, stored=true. Should return in original order
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"test_mvt_dvf_st_str"
argument_list|,
literal|"cccc"
argument_list|,
literal|"test_mvt_dvf_st_str"
argument_list|,
literal|"aaaa"
argument_list|,
literal|"test_mvt_dvf_st_str"
argument_list|,
literal|"bbbb"
argument_list|)
argument_list|)
expr_stmt|;
comment|// multiValued=true, docValues=not specified, stored=true. Should return in original order
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"test_mvt_dvu_st_str"
argument_list|,
literal|"cccc"
argument_list|,
literal|"test_mvt_dvu_st_str"
argument_list|,
literal|"aaaa"
argument_list|,
literal|"test_mvt_dvu_st_str"
argument_list|,
literal|"bbbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"fl"
argument_list|,
literal|"test_mvt_dvt_st_str"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvt_st_str/[0]==cccc"
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvt_st_str/[1]==aaaa"
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvt_st_str/[2]==bbbb"
argument_list|)
expr_stmt|;
comment|// Currently, this test fails since stored=false. When SOLR-8740 is committed, it should not throw an exception
comment|// and should succeed, returning the field in sorted order.
try|try
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|,
literal|"fl"
argument_list|,
literal|"test_mvt_dvt_sf_str"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvt_sf_str/[0]==aaaa"
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvt_sf_str/[1]==bbbb"
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvt_sf_str/[2]==cccc"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do nothing until SOLR-8740 is committed. At that point this should not throw an exception.
comment|// NOTE: I think the test is correct after 8740 so just remove the try/catch
block|}
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:3"
argument_list|,
literal|"fl"
argument_list|,
literal|"test_mvt_dvf_st_str"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvf_st_str/[0]==cccc"
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvf_st_str/[1]==aaaa"
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvf_st_str/[2]==bbbb"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:4"
argument_list|,
literal|"fl"
argument_list|,
literal|"test_mvt_dvu_st_str"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvu_st_str/[0]==cccc"
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvu_st_str/[1]==aaaa"
argument_list|,
literal|"/response/docs/[0]/test_mvt_dvu_st_str/[2]==bbbb"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
