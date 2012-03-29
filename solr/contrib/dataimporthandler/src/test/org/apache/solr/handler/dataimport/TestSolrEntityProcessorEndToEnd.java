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
name|java
operator|.
name|net
operator|.
name|URL
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|DefaultHttpClient
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|conn
operator|.
name|tsccm
operator|.
name|ThreadSafeClientConnManager
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|embedded
operator|.
name|JettySolrRunner
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|HttpSolrServer
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
name|SolrInputDocument
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
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_comment
comment|/**  * End-to-end test of SolrEntityProcessor. "Real" test using embedded Solr  */
end_comment
begin_class
DECL|class|TestSolrEntityProcessorEndToEnd
specifier|public
class|class
name|TestSolrEntityProcessorEndToEnd
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestSolrEntityProcessorEndToEnd
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//rivate static final String SOLR_SOURCE_URL = "http://localhost:8983/solr";
DECL|field|SOLR_CONFIG
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_CONFIG
init|=
literal|"dataimport-solrconfig.xml"
decl_stmt|;
DECL|field|SOLR_SCHEMA
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_SCHEMA
init|=
literal|"dataimport-schema.xml"
decl_stmt|;
DECL|field|SOLR_HOME
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HOME
init|=
literal|"dih/solr"
decl_stmt|;
DECL|field|CONF_DIR
specifier|private
specifier|static
specifier|final
name|String
name|CONF_DIR
init|=
literal|"dih"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
DECL|field|DB_DOCS
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|DB_DOCS
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|SOLR_DOCS
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|SOLR_DOCS
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
comment|// dynamic fields in the destination schema
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|dbDoc
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
name|dbDoc
operator|.
name|put
argument_list|(
literal|"dbid_s"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|dbDoc
operator|.
name|put
argument_list|(
literal|"dbdesc_s"
argument_list|,
literal|"DbDescription"
argument_list|)
expr_stmt|;
name|DB_DOCS
operator|.
name|add
argument_list|(
name|dbDoc
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|solrDoc
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
name|solrDoc
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|solrDoc
operator|.
name|put
argument_list|(
literal|"desc"
argument_list|,
literal|"SolrDescription"
argument_list|)
expr_stmt|;
name|SOLR_DOCS
operator|.
name|add
argument_list|(
name|solrDoc
argument_list|)
expr_stmt|;
block|}
DECL|field|instance
specifier|private
name|SolrInstance
name|instance
init|=
literal|null
decl_stmt|;
DECL|field|jetty
specifier|private
name|JettySolrRunner
name|jetty
decl_stmt|;
DECL|method|getDihConfigTagsInnerEntity
specifier|private
specifier|static
name|String
name|getDihConfigTagsInnerEntity
parameter_list|(
name|int
name|port
parameter_list|)
block|{
return|return
literal|"<dataConfig>\r\n"
operator|+
literal|"<dataSource type='MockDataSource' />\r\n"
operator|+
literal|"<document>\r\n"
operator|+
literal|"<entity name='db' query='select * from x'>\r\n"
operator|+
literal|"<field column='dbid_s' />\r\n"
operator|+
literal|"<field column='dbdesc_s' />\r\n"
operator|+
literal|"<entity name='se' processor='SolrEntityProcessor' query='id:${db.dbid_s}'\n"
operator|+
literal|"     url='"
operator|+
name|getSourceUrl
argument_list|(
name|port
argument_list|)
operator|+
literal|"' fields='id,desc'>\r\n"
operator|+
literal|"<field column='id' />\r\n"
operator|+
literal|"<field column='desc' />\r\n"
operator|+
literal|"</entity>\r\n"
operator|+
literal|"</entity>\r\n"
operator|+
literal|"</document>\r\n"
operator|+
literal|"</dataConfig>\r\n"
return|;
block|}
DECL|method|generateDIHConfig
specifier|private
specifier|static
name|String
name|generateDIHConfig
parameter_list|(
name|String
name|options
parameter_list|,
name|int
name|port
parameter_list|)
block|{
return|return
literal|"<dataConfig>\r\n"
operator|+
literal|"<document>\r\n"
operator|+
literal|"<entity name='se' processor='SolrEntityProcessor'"
operator|+
literal|"   url='"
operator|+
name|getSourceUrl
argument_list|(
name|port
argument_list|)
operator|+
literal|"' "
operator|+
name|options
operator|+
literal|" />\r\n"
operator|+
literal|"</document>\r\n"
operator|+
literal|"</dataConfig>\r\n"
return|;
block|}
DECL|method|getSourceUrl
specifier|private
specifier|static
name|String
name|getSourceUrl
parameter_list|(
name|int
name|port
parameter_list|)
block|{
return|return
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/solr"
return|;
block|}
comment|//TODO: fix this test to close its directories
DECL|field|savedFactory
specifier|static
name|String
name|savedFactory
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
name|savedFactory
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.DirectoryFactory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"solr.StandardDirectoryFactory"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|savedFactory
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
name|savedFactory
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
comment|// destination solr core
name|initCore
argument_list|(
name|SOLR_CONFIG
argument_list|,
name|SOLR_SCHEMA
argument_list|,
name|SOLR_HOME
argument_list|)
expr_stmt|;
comment|// data source solr instance
name|instance
operator|=
operator|new
name|SolrInstance
argument_list|()
expr_stmt|;
name|instance
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|jetty
operator|=
name|createJetty
argument_list|(
name|instance
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|deleteCore
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error deleting core"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|instance
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testFullImport
specifier|public
name|void
name|testFullImport
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
try|try
block|{
name|addDocumentsToSolr
argument_list|(
name|SOLR_DOCS
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|generateDIHConfig
argument_list|(
literal|"query='*:*' rows='2' fl='id,desc' onError='skip'"
argument_list|,
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//result/doc/str[@name='id'][.='1']"
argument_list|,
literal|"//result/doc/arr[@name='desc'][.='SolrDescription']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFullImportFqParam
specifier|public
name|void
name|testFullImportFqParam
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
try|try
block|{
name|addDocumentsToSolr
argument_list|(
name|generateSolrDocuments
argument_list|(
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"rows"
argument_list|,
literal|"50"
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|generateDIHConfig
argument_list|(
literal|"query='*:*' fq='desc:Description1*,desc:Description*2' rows='2'"
argument_list|,
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:12"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//result/doc/arr[@name='desc'][.='Description12']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFullImportFieldsParam
specifier|public
name|void
name|testFullImportFieldsParam
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
try|try
block|{
name|addDocumentsToSolr
argument_list|(
name|generateSolrDocuments
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|generateDIHConfig
argument_list|(
literal|"query='*:*' fl='id' rows='2'"
argument_list|,
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='7']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//result/doc/arr[@name='desc']"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The document has a field with name desc"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{            }
block|}
comment|/**    * Receive a row from SQL (Mock) and fetch a row from Solr    */
DECL|method|testFullImportInnerEntity
specifier|public
name|void
name|testFullImportInnerEntity
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
try|try
block|{
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|DB_DOCS
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|addDocumentsToSolr
argument_list|(
name|SOLR_DOCS
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|getDihConfigTagsInnerEntity
argument_list|(
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
block|}
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//result/doc/str[@name='id'][.='1']"
argument_list|,
literal|"//result/doc/str[@name='dbdesc_s'][.='DbDescription']"
argument_list|,
literal|"//result/doc/str[@name='dbid_s'][.='1']"
argument_list|,
literal|"//result/doc/arr[@name='desc'][.='SolrDescription']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFullImportWrongSolrUrl
specifier|public
name|void
name|testFullImportWrongSolrUrl
parameter_list|()
block|{
try|try
block|{
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error stopping jetty"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
try|try
block|{
name|runFullImport
argument_list|(
name|generateDIHConfig
argument_list|(
literal|"query='*:*' rows='2' fl='id,desc' onError='skip'"
argument_list|,
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFullImportBadConfig
specifier|public
name|void
name|testFullImportBadConfig
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
try|try
block|{
name|runFullImport
argument_list|(
name|generateDIHConfig
argument_list|(
literal|"query='bogus:3' rows='2' fl='id,desc' onError='abort'"
argument_list|,
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
DECL|method|generateSolrDocuments
specifier|private
specifier|static
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|generateSolrDocuments
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|docList
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|num
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
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
name|map
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"desc"
argument_list|,
literal|"Description"
operator|+
name|i
argument_list|)
expr_stmt|;
name|docList
operator|.
name|add
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
return|return
name|docList
return|;
block|}
DECL|method|addDocumentsToSolr
specifier|private
name|void
name|addDocumentsToSolr
parameter_list|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|docs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|sidl
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
range|:
name|docs
control|)
block|{
name|SolrInputDocument
name|sd
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|doc
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sd
operator|.
name|addField
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sidl
operator|.
name|add
argument_list|(
name|sd
argument_list|)
expr_stmt|;
block|}
name|DefaultHttpClient
name|client
init|=
operator|new
name|DefaultHttpClient
argument_list|(
operator|new
name|ThreadSafeClientConnManager
argument_list|()
argument_list|)
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|getSourceUrl
argument_list|(
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|solrServer
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
operator|.
name|toExternalForm
argument_list|()
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|solrServer
operator|.
name|add
argument_list|(
name|sidl
argument_list|)
expr_stmt|;
name|solrServer
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|SolrInstance
specifier|private
specifier|static
class|class
name|SolrInstance
block|{
DECL|field|homeDir
name|File
name|homeDir
decl_stmt|;
DECL|field|confDir
name|File
name|confDir
decl_stmt|;
DECL|method|getHomeDir
specifier|public
name|String
name|getHomeDir
parameter_list|()
block|{
return|return
name|homeDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
name|CONF_DIR
operator|+
literal|"dataimport-schema.xml"
return|;
block|}
DECL|method|getDataDir
specifier|public
name|String
name|getDataDir
parameter_list|()
block|{
return|return
name|dataDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
name|CONF_DIR
operator|+
literal|"dataimport-solrconfig.xml"
return|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|home
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|homeDir
operator|=
operator|new
name|File
argument_list|(
name|home
operator|+
literal|"inst"
argument_list|)
expr_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"data"
argument_list|)
expr_stmt|;
name|confDir
operator|=
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
name|homeDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|confDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.xml"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
name|getSolrConfigFile
argument_list|()
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
name|getSchemaFile
argument_list|()
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"data-config.xml"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
name|CONF_DIR
operator|+
literal|"dataconfig-contentstream.xml"
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|recurseDelete
argument_list|(
name|homeDir
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createJetty
specifier|private
name|JettySolrRunner
name|createJetty
parameter_list|(
name|SolrInstance
name|instance
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|instance
operator|.
name|getDataDir
argument_list|()
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|jetty
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|instance
operator|.
name|getHomeDir
argument_list|()
argument_list|,
literal|"/solr"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|jetty
return|;
block|}
block|}
end_class
end_unit
