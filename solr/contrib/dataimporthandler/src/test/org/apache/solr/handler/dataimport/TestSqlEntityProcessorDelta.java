begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
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
name|FileOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|/**  *<p>  * Test for SqlEntityProcessor which checks variations in primary key names and deleted ids  *</p>  *   *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestSqlEntityProcessorDelta
specifier|public
class|class
name|TestSqlEntityProcessorDelta
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|FULLIMPORT_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|FULLIMPORT_QUERY
init|=
literal|"select * from x"
decl_stmt|;
DECL|field|DELTA_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|DELTA_QUERY
init|=
literal|"select id from x where last_modified> NOW"
decl_stmt|;
DECL|field|DELETED_PK_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|DELETED_PK_QUERY
init|=
literal|"select id from x where last_modified> NOW AND deleted='true'"
decl_stmt|;
DECL|field|dataConfig_delta
specifier|private
specifier|static
specifier|final
name|String
name|dataConfig_delta
init|=
literal|"<dataConfig>"
operator|+
literal|"<dataSource  type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>\n"
operator|+
literal|"<entity name=\"x\" transformer=\"TemplateTransformer\""
operator|+
literal|"            query=\""
operator|+
name|FULLIMPORT_QUERY
operator|+
literal|"\""
operator|+
literal|"            deletedPkQuery=\""
operator|+
name|DELETED_PK_QUERY
operator|+
literal|"\""
operator|+
literal|"            deltaImportQuery=\"select * from x where id='${dih.delta.id}'\""
operator|+
literal|"            deltaQuery=\""
operator|+
name|DELTA_QUERY
operator|+
literal|"\">\n"
operator|+
literal|"<field column=\"id\" name=\"id\"/>\n"
operator|+
literal|"<entity name=\"y\" query=\"select * from y where y.A='${x.id}'\">\n"
operator|+
literal|"<field column=\"desc\" />\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>\n"
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
name|initCore
argument_list|(
literal|"dataimport-solrconfig.xml"
argument_list|,
literal|"dataimport-schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
annotation|@
name|Override
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
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|add1document
specifier|private
name|void
name|add1document
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|parentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|FULLIMPORT_QUERY
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A='1'"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR add1document"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:hello"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testCompositePk_FullImport
specifier|public
name|void
name|testCompositePk_FullImport
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testNonWritablePersistFile
specifier|public
name|void
name|testNonWritablePersistFile
parameter_list|()
throws|throws
name|Exception
block|{
comment|// See SOLR-2551
name|String
name|configDir
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getConfigDir
argument_list|()
decl_stmt|;
name|String
name|filePath
init|=
name|configDir
decl_stmt|;
if|if
condition|(
name|configDir
operator|!=
literal|null
operator|&&
operator|!
name|configDir
operator|.
name|endsWith
argument_list|(
name|File
operator|.
name|separator
argument_list|)
condition|)
name|filePath
operator|+=
name|File
operator|.
name|separator
expr_stmt|;
name|filePath
operator|+=
literal|"dataimport.properties"
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
comment|// execute the test only if we are able to set file to read only mode
if|if
condition|(
operator|(
name|f
operator|.
name|exists
argument_list|()
operator|||
name|f
operator|.
name|createNewFile
argument_list|()
operator|)
operator|&&
name|f
operator|.
name|setReadOnly
argument_list|()
condition|)
block|{
try|try
block|{
name|List
name|parentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|FULLIMPORT_QUERY
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A='1'"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// WORKS
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testCompositePk_DeltaImport_delete
specifier|public
name|void
name|testCompositePk_DeltaImport_delete
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
name|List
name|deletedRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deletedRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELETED_PK_QUERY
argument_list|,
name|deletedRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELTA_QUERY
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A='1'"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR testCompositePk_DeltaImport_delete"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testCompositePk_DeltaImport_empty
specifier|public
name|void
name|testCompositePk_DeltaImport_empty
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|deltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELTA_QUERY
argument_list|,
name|deltaRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELETED_PK_QUERY
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|parentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x where id='1'"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A='1'"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR testCompositePk_DeltaImport_empty"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:hello"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
comment|// WORKS
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testCompositePk_DeltaImport_replace_delete
specifier|public
name|void
name|testCompositePk_DeltaImport_replace_delete
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|List
name|deltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELTA_QUERY
argument_list|,
name|deltaRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|deletedRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deletedRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELETED_PK_QUERY
argument_list|,
name|deletedRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|parentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x where id='1'"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"goodbye"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A='1'"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR testCompositePk_DeltaImport_replace_delete"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testCompositePk_DeltaImport_replace_nodelete
specifier|public
name|void
name|testCompositePk_DeltaImport_replace_nodelete
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|List
name|deltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELTA_QUERY
argument_list|,
name|deltaRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELETED_PK_QUERY
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|parentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x where id='1'"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"goodbye"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A='1'"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR XtestCompositePk_DeltaImport_replace_nodelete"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:hello OR XtestCompositePk_DeltaImport_replace_nodelete"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:goodbye"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testCompositePk_DeltaImport_add
specifier|public
name|void
name|testCompositePk_DeltaImport_add
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|List
name|deltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELTA_QUERY
argument_list|,
name|deltaRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|parentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x where id='2'"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"goodbye"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A='2'"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR testCompositePk_DeltaImport_add"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:hello"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:goodbye"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testCompositePk_DeltaImport_nodelta
specifier|public
name|void
name|testCompositePk_DeltaImport_nodelta
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELTA_QUERY
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR testCompositePk_DeltaImport_nodelta"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1 OR testCompositePk_DeltaImport_nodelta"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:hello OR testCompositePk_DeltaImport_nodelta"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testCompositePk_DeltaImport_add_delete
specifier|public
name|void
name|testCompositePk_DeltaImport_add_delete
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|List
name|deltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELTA_QUERY
argument_list|,
name|deltaRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|deletedRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deletedRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|DELETED_PK_QUERY
argument_list|,
name|deletedRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|parentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x where id='2'"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"goodbye"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A='2'"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR XtestCompositePk_DeltaImport_add_delete"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:hello"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:goodbye"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
