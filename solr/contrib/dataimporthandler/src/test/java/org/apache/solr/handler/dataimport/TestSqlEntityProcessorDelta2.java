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
name|Test
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
name|Collection
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
comment|/**  *<p>  * Test for SqlEntityProcessor which checks variations in primary key names and deleted ids  *</p>  *   *  * @version $Id: TestSqlEntityProcessor2.java 723824 2008-12-05 19:14:11Z shalin $  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestSqlEntityProcessorDelta2
specifier|public
class|class
name|TestSqlEntityProcessorDelta2
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
annotation|@
name|Override
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"dataimport-solr_id-schema.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"dataimport-solrconfig.xml"
return|;
block|}
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
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
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
name|super
operator|.
name|runFullImport
argument_list|(
name|dataConfig_delta2
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
literal|"solr_id:prefix-1"
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
name|super
operator|.
name|runDeltaImport
argument_list|(
name|dataConfig_delta2
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
name|super
operator|.
name|runDeltaImport
argument_list|(
name|dataConfig_delta2
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
literal|"solr_id:prefix-1"
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
DECL|method|XtestCompositePk_DeltaImport_replace_delete
specifier|public
name|void
name|XtestCompositePk_DeltaImport_replace_delete
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
name|super
operator|.
name|runDeltaImport
argument_list|(
name|dataConfig_delta2
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
name|super
operator|.
name|runDeltaImport
argument_list|(
name|dataConfig_delta2
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
literal|"solr_id:prefix-1"
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
name|super
operator|.
name|runDeltaImport
argument_list|(
name|dataConfig_delta2
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
literal|"solr_id:prefix-1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"solr_id:prefix-2"
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
name|super
operator|.
name|runDeltaImport
argument_list|(
name|dataConfig_delta2
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
literal|"solr_id:prefix-1 OR testCompositePk_DeltaImport_nodelta"
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
name|super
operator|.
name|runDeltaImport
argument_list|(
name|dataConfig_delta2
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
literal|"solr_id:prefix-2"
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
DECL|field|dataConfig_delta2
specifier|private
specifier|static
name|String
name|dataConfig_delta2
init|=
literal|"<dataConfig><dataSource  type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>\n"
operator|+
literal|"<entity name=\"x\" transformer=\"TemplateTransformer\""
operator|+
literal|"				query=\""
operator|+
name|FULLIMPORT_QUERY
operator|+
literal|"\""
operator|+
literal|"				deletedPkQuery=\""
operator|+
name|DELETED_PK_QUERY
operator|+
literal|"\""
operator|+
literal|" 				deltaImportQuery=\"select * from x where id='${dataimporter.delta.id}'\""
operator|+
literal|"				deltaQuery=\""
operator|+
name|DELTA_QUERY
operator|+
literal|"\">\n"
operator|+
literal|"<field column=\"tmpid\" template=\"prefix-${x.id}\" name=\"solr_id\"/>\n"
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
block|}
end_class
end_unit
