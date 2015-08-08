begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Date
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|cloud
operator|.
name|AbstractZkTestCase
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
name|cloud
operator|.
name|ZkTestServer
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
name|params
operator|.
name|ModifiableSolrParams
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
name|util
operator|.
name|SuppressForbidden
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
name|CoreContainer
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequest
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
name|AfterClass
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
begin_class
DECL|class|TestZKPropertiesWriter
specifier|public
class|class
name|TestZKPropertiesWriter
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|zkServer
specifier|protected
specifier|static
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|zkDir
specifier|protected
specifier|static
name|String
name|zkDir
decl_stmt|;
DECL|field|cc
specifier|private
specifier|static
name|CoreContainer
name|cc
decl_stmt|;
DECL|field|dateFormat
specifier|private
name|String
name|dateFormat
init|=
literal|"yyyy-MM-dd HH:mm:ss.SSSSSS"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|dihZk_beforeClass
specifier|public
specifier|static
name|void
name|dihZk_beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|zkDir
operator|=
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"jetty.port"
argument_list|,
literal|"0000"
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|buildZooKeeper
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|getFile
argument_list|(
literal|"dih/solr"
argument_list|)
argument_list|,
literal|"dataimport-solrconfig.xml"
argument_list|,
literal|"dataimport-schema.xml"
argument_list|)
expr_stmt|;
comment|//initCore("solrconfig.xml", "schema.xml", getFile("dih/solr").getAbsolutePath());
name|cc
operator|=
name|createDefaultCoreContainer
argument_list|(
name|getFile
argument_list|(
literal|"dih/solr"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|beforeDihZKTest
specifier|public
name|void
name|beforeDihZKTest
parameter_list|()
throws|throws
name|Exception
block|{    }
annotation|@
name|After
DECL|method|afterDihZkTest
specifier|public
name|void
name|afterDihZkTest
parameter_list|()
throws|throws
name|Exception
block|{
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|dihZk_afterClass
specifier|public
specifier|static
name|void
name|dihZk_afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|zkServer
operator|=
literal|null
expr_stmt|;
name|zkDir
operator|=
literal|null
expr_stmt|;
name|cc
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Needs currentTimeMillis to construct date stamps"
argument_list|)
annotation|@
name|Test
DECL|method|testZKPropertiesWriter
specifier|public
name|void
name|testZKPropertiesWriter
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test using ZooKeeper
name|assertTrue
argument_list|(
literal|"Not using ZooKeeper"
argument_list|,
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|isZooKeeperAware
argument_list|()
argument_list|)
expr_stmt|;
comment|// for the really slow/busy computer, we wait to make sure we have a leader before starting
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderUrl
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test query on empty index"
argument_list|,
name|request
argument_list|(
literal|"qlkciyopsbgzyvkylsjhchghjrdf"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|SimpleDateFormat
name|errMsgFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|dateFormat
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|SimpleDateFormat
name|df
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|dateFormat
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|Date
name|oneSecondAgo
init|=
operator|new
name|Date
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|1000
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|init
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|init
operator|.
name|put
argument_list|(
literal|"dateFormat"
argument_list|,
name|dateFormat
argument_list|)
expr_stmt|;
name|ZKPropertiesWriter
name|spw
init|=
operator|new
name|ZKPropertiesWriter
argument_list|()
decl_stmt|;
name|spw
operator|.
name|init
argument_list|(
operator|new
name|DataImporter
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"dataimport"
argument_list|)
argument_list|,
name|init
argument_list|)
expr_stmt|;
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
argument_list|<>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"SomeDates.last_index_time"
argument_list|,
name|oneSecondAgo
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"last_index_time"
argument_list|,
name|oneSecondAgo
argument_list|)
expr_stmt|;
name|spw
operator|.
name|persist
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"year_s"
argument_list|,
literal|"2013"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select "
operator|+
name|df
operator|.
name|format
argument_list|(
name|oneSecondAgo
argument_list|)
operator|+
literal|" from dummy"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|,
literal|"dataConfig"
argument_list|,
name|generateConfig
argument_list|()
argument_list|,
literal|"clean"
argument_list|,
literal|"true"
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"synchronous"
argument_list|,
literal|"true"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|=
name|spw
operator|.
name|readIndexerProperties
argument_list|()
expr_stmt|;
name|Date
name|entityDate
init|=
name|df
operator|.
name|parse
argument_list|(
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"SomeDates.last_index_time"
argument_list|)
argument_list|)
decl_stmt|;
name|Date
name|docDate
init|=
name|df
operator|.
name|parse
argument_list|(
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"last_index_time"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"This date: "
operator|+
name|errMsgFormat
operator|.
name|format
argument_list|(
name|oneSecondAgo
argument_list|)
operator|+
literal|" should be prior to the document date: "
operator|+
name|errMsgFormat
operator|.
name|format
argument_list|(
name|docDate
argument_list|)
argument_list|,
name|docDate
operator|.
name|getTime
argument_list|()
operator|-
name|oneSecondAgo
operator|.
name|getTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"This date: "
operator|+
name|errMsgFormat
operator|.
name|format
argument_list|(
name|oneSecondAgo
argument_list|)
operator|+
literal|" should be prior to the entity date: "
operator|+
name|errMsgFormat
operator|.
name|format
argument_list|(
name|entityDate
argument_list|)
argument_list|,
name|entityDate
operator|.
name|getTime
argument_list|()
operator|-
name|oneSecondAgo
operator|.
name|getTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|request
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//doc/str[@name=\"year_s\"]=\"2013\""
argument_list|)
expr_stmt|;
block|}
DECL|method|request
specifier|public
name|SolrQueryRequest
name|request
parameter_list|(
name|String
modifier|...
name|q
parameter_list|)
block|{
name|LocalSolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
return|return
name|req
return|;
block|}
DECL|method|generateConfig
specifier|protected
name|String
name|generateConfig
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<dataConfig> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<propertyWriter dateFormat=\""
operator|+
name|dateFormat
operator|+
literal|"\" type=\"ZKPropertiesWriter\" />\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<dataSource name=\"mock\" type=\"MockDataSource\"/>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<document name=\"TestSimplePropertiesWriter\"> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<entity name=\"SomeDates\" processor=\"SqlEntityProcessor\" dataSource=\"mock\" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"query=\"select ${dih.last_index_time} from dummy\">\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"AYEAR_S\" name=\"year_s\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</entity>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</document> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</dataConfig> \n"
argument_list|)
expr_stmt|;
name|String
name|config
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
block|}
end_class
end_unit
