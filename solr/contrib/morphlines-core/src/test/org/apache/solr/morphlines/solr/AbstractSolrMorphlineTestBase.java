begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.morphlines.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|solr
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|SolrServer
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|XMLResponseParser
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
name|response
operator|.
name|QueryResponse
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
name|SolrDocument
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
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Collector
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Command
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Record
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Compiler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|FaultTolerance
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Fields
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Notifications
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|stdlib
operator|.
name|PipeBuilder
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
begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Files
import|;
end_import
begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|Config
import|;
end_import
begin_class
DECL|class|AbstractSolrMorphlineTestBase
specifier|public
class|class
name|AbstractSolrMorphlineTestBase
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|collector
specifier|protected
name|Collector
name|collector
decl_stmt|;
DECL|field|morphline
specifier|protected
name|Command
name|morphline
decl_stmt|;
DECL|field|solrServer
specifier|protected
name|SolrServer
name|solrServer
decl_stmt|;
DECL|field|testServer
specifier|protected
name|DocumentLoader
name|testServer
decl_stmt|;
DECL|field|TEST_WITH_EMBEDDED_SOLR_SERVER
specifier|protected
specifier|static
specifier|final
name|boolean
name|TEST_WITH_EMBEDDED_SOLR_SERVER
init|=
literal|true
decl_stmt|;
DECL|field|EXTERNAL_SOLR_SERVER_URL
specifier|protected
specifier|static
specifier|final
name|String
name|EXTERNAL_SOLR_SERVER_URL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"externalSolrServer"
argument_list|)
decl_stmt|;
comment|//  protected static final String EXTERNAL_SOLR_SERVER_URL = "http://127.0.0.1:8983/solr";
DECL|field|RESOURCES_DIR
specifier|protected
specifier|static
specifier|final
name|String
name|RESOURCES_DIR
init|=
name|getFile
argument_list|(
literal|"morphlines-core.marker"
argument_list|)
operator|.
name|getParent
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_BASE_DIR
specifier|protected
specifier|static
specifier|final
name|String
name|DEFAULT_BASE_DIR
init|=
literal|"solr"
decl_stmt|;
DECL|field|SEQ_NUM
specifier|protected
specifier|static
specifier|final
name|AtomicInteger
name|SEQ_NUM
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|SEQ_NUM2
specifier|protected
specifier|static
specifier|final
name|AtomicInteger
name|SEQ_NUM2
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|NON_EMPTY_FIELD
specifier|protected
specifier|static
specifier|final
name|Object
name|NON_EMPTY_FIELD
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|LOGGER
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractSolrMorphlineTestBase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tempDir
specifier|protected
name|String
name|tempDir
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
name|myInitCore
argument_list|(
name|DEFAULT_BASE_DIR
argument_list|)
expr_stmt|;
block|}
DECL|method|myInitCore
specifier|protected
specifier|static
name|void
name|myInitCore
parameter_list|(
name|String
name|baseDirName
parameter_list|)
throws|throws
name|Exception
block|{
name|Joiner
name|joiner
init|=
name|Joiner
operator|.
name|on
argument_list|(
name|File
operator|.
name|separator
argument_list|)
decl_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
name|joiner
operator|.
name|join
argument_list|(
name|RESOURCES_DIR
argument_list|,
name|baseDirName
argument_list|)
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
name|collector
operator|=
operator|new
name|Collector
argument_list|()
expr_stmt|;
if|if
condition|(
name|EXTERNAL_SOLR_SERVER_URL
operator|!=
literal|null
condition|)
block|{
comment|//solrServer = new ConcurrentUpdateSolrServer(EXTERNAL_SOLR_SERVER_URL, 2, 2);
comment|//solrServer = new SafeConcurrentUpdateSolrServer(EXTERNAL_SOLR_SERVER_URL, 2, 2);
name|solrServer
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|EXTERNAL_SOLR_SERVER_URL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|HttpSolrServer
operator|)
name|solrServer
operator|)
operator|.
name|setParser
argument_list|(
operator|new
name|XMLResponseParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|TEST_WITH_EMBEDDED_SOLR_SERVER
condition|)
block|{
name|solrServer
operator|=
operator|new
name|EmbeddedTestSolrServer
argument_list|(
name|h
operator|.
name|getCoreContainer
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not yet implemented"
argument_list|)
throw|;
comment|//solrServer = new TestSolrServer(getSolrServer());
block|}
block|}
name|int
name|batchSize
init|=
name|SEQ_NUM2
operator|.
name|incrementAndGet
argument_list|()
operator|%
literal|2
operator|==
literal|0
condition|?
literal|100
else|:
literal|1
decl_stmt|;
comment|//SolrInspector.DEFAULT_SOLR_SERVER_BATCH_SIZE : 1;
name|testServer
operator|=
operator|new
name|SolrServerDocumentLoader
argument_list|(
name|solrServer
argument_list|,
name|batchSize
argument_list|)
expr_stmt|;
name|deleteAllDocuments
argument_list|()
expr_stmt|;
name|tempDir
operator|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
operator|+
literal|"/test-morphlines-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|tempDir
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
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
name|collector
operator|=
literal|null
expr_stmt|;
name|solrServer
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testDocumentTypesInternal
specifier|protected
name|void
name|testDocumentTypesInternal
parameter_list|(
name|String
index|[]
name|files
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|expectedRecords
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|expectedRecordContents
parameter_list|)
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"This test has issues with this locale: https://issues.apache.org/jira/browse/SOLR-5778"
argument_list|,
literal|"GregorianCalendar"
operator|.
name|equals
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TimeZone
operator|.
name|getDefault
argument_list|()
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|deleteAllDocuments
argument_list|()
expr_stmt|;
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|byte
index|[]
name|body
init|=
name|Files
operator|.
name|toByteArray
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Record
name|event
init|=
operator|new
name|Record
argument_list|()
decl_stmt|;
comment|//event.put(Fields.ID, docId++);
name|event
operator|.
name|getFields
argument_list|()
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|ATTACHMENT_BODY
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|body
argument_list|)
argument_list|)
expr_stmt|;
name|event
operator|.
name|getFields
argument_list|()
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|ATTACHMENT_NAME
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|event
operator|.
name|getFields
argument_list|()
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|BASE_ID
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|collector
operator|.
name|reset
argument_list|()
expr_stmt|;
name|load
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|Integer
name|count
init|=
name|expectedRecords
operator|.
name|get
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|numDocs
operator|+=
name|count
expr_stmt|;
block|}
else|else
block|{
name|numDocs
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"unexpected results in "
operator|+
name|file
argument_list|,
name|numDocs
argument_list|,
name|queryResultSetSize
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedContents
init|=
name|expectedRecordContents
operator|.
name|get
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedContents
operator|!=
literal|null
condition|)
block|{
name|Record
name|actual
init|=
name|collector
operator|.
name|getFirstRecord
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|expectedContents
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|==
name|NON_EMPTY_FIELD
condition|)
block|{
name|assertNotNull
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|actual
operator|.
name|getFirstValue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"key:"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|0
argument_list|,
name|actual
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"key:"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|actual
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|queryResultSetSize
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|load
specifier|private
name|boolean
name|load
parameter_list|(
name|Record
name|record
parameter_list|)
block|{
name|Notifications
operator|.
name|notifyStartSession
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
return|return
name|morphline
operator|.
name|process
argument_list|(
name|record
argument_list|)
return|;
block|}
DECL|method|queryResultSetSize
specifier|protected
name|int
name|queryResultSetSize
parameter_list|(
name|String
name|query
parameter_list|)
block|{
comment|//    return collector.getRecords().size();
try|try
block|{
name|testServer
operator|.
name|commitTransaction
argument_list|()
expr_stmt|;
name|solrServer
operator|.
name|commit
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|solrServer
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
name|query
argument_list|)
operator|.
name|setRows
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
decl_stmt|;
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"rsp: {}"
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|rsp
operator|.
name|getResults
argument_list|()
control|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"rspDoc #{}: {}"
argument_list|,
name|i
operator|++
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|int
name|size
init|=
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
name|size
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|deleteAllDocuments
specifier|private
name|void
name|deleteAllDocuments
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|collector
operator|.
name|reset
argument_list|()
expr_stmt|;
name|SolrServer
name|s
init|=
name|solrServer
decl_stmt|;
name|s
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|s
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|createMorphline
specifier|protected
name|Command
name|createMorphline
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|PipeBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|parse
argument_list|(
name|file
argument_list|)
argument_list|,
literal|null
argument_list|,
name|collector
argument_list|,
name|createMorphlineContext
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createMorphlineContext
specifier|private
name|MorphlineContext
name|createMorphlineContext
parameter_list|()
block|{
return|return
operator|new
name|SolrMorphlineContext
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentLoader
argument_list|(
name|testServer
argument_list|)
comment|//      .setDocumentLoader(new CollectingDocumentLoader(100))
operator|.
name|setExceptionHandler
argument_list|(
operator|new
name|FaultTolerance
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
name|SolrServerException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMetricRegistry
argument_list|(
operator|new
name|MetricRegistry
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|parse
specifier|private
name|Config
name|parse
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrLocator
name|locator
init|=
operator|new
name|SolrLocator
argument_list|(
name|createMorphlineContext
argument_list|()
argument_list|)
decl_stmt|;
name|locator
operator|.
name|setSolrHomeDir
argument_list|(
name|testSolrHome
operator|+
literal|"/collection1"
argument_list|)
expr_stmt|;
name|File
name|morphlineFile
decl_stmt|;
if|if
condition|(
operator|new
name|File
argument_list|(
name|file
argument_list|)
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|morphlineFile
operator|=
operator|new
name|File
argument_list|(
name|file
operator|+
literal|".conf"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|morphlineFile
operator|=
operator|new
name|File
argument_list|(
name|RESOURCES_DIR
operator|+
literal|"/"
operator|+
name|file
operator|+
literal|".conf"
argument_list|)
expr_stmt|;
block|}
name|Config
name|config
init|=
operator|new
name|Compiler
argument_list|()
operator|.
name|parse
argument_list|(
name|morphlineFile
argument_list|,
name|locator
operator|.
name|toConfig
argument_list|(
literal|"SOLR_LOCATOR"
argument_list|)
argument_list|)
decl_stmt|;
name|config
operator|=
name|config
operator|.
name|getConfigList
argument_list|(
literal|"morphlines"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
DECL|method|startSession
specifier|protected
name|void
name|startSession
parameter_list|()
block|{
name|Notifications
operator|.
name|notifyStartSession
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocumentContent
specifier|protected
name|void
name|testDocumentContent
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|ExpectedResult
argument_list|>
name|expectedResultMap
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryResponse
name|rsp
init|=
name|solrServer
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setRows
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
decl_stmt|;
comment|// Check that every expected field/values shows up in the actual query
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|ExpectedResult
argument_list|>
name|current
range|:
name|expectedResultMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|field
init|=
name|current
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|expectedFieldValue
range|:
name|current
operator|.
name|getValue
argument_list|()
operator|.
name|getFieldValues
argument_list|()
control|)
block|{
name|ExpectedResult
operator|.
name|CompareType
name|compareType
init|=
name|current
operator|.
name|getValue
argument_list|()
operator|.
name|getCompareType
argument_list|()
decl_stmt|;
name|boolean
name|foundField
init|=
literal|false
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|rsp
operator|.
name|getResults
argument_list|()
control|)
block|{
name|Collection
argument_list|<
name|Object
argument_list|>
name|actualFieldValues
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|compareType
operator|==
name|ExpectedResult
operator|.
name|CompareType
operator|.
name|equals
condition|)
block|{
if|if
condition|(
name|actualFieldValues
operator|!=
literal|null
operator|&&
name|actualFieldValues
operator|.
name|contains
argument_list|(
name|expectedFieldValue
argument_list|)
condition|)
block|{
name|foundField
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Object
argument_list|>
name|it
init|=
name|actualFieldValues
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|actualValue
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// test only supports string comparison
if|if
condition|(
name|actualFieldValues
operator|!=
literal|null
operator|&&
name|actualValue
operator|.
name|contains
argument_list|(
name|expectedFieldValue
argument_list|)
condition|)
block|{
name|foundField
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
assert|assert
operator|(
name|foundField
operator|)
assert|;
comment|// didn't find expected field/value in query
block|}
block|}
block|}
comment|/**    * Representation of the expected output of a SolrQuery.    */
DECL|class|ExpectedResult
specifier|protected
specifier|static
class|class
name|ExpectedResult
block|{
DECL|field|fieldValues
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|fieldValues
decl_stmt|;
DECL|enum|CompareType
specifier|public
enum|enum
name|CompareType
block|{
DECL|enum constant|equals
name|equals
block|,
comment|// Compare with equals, i.e. actual.equals(expected)
DECL|enum constant|contains
name|contains
block|;
comment|// Compare with contains, i.e. actual.contains(expected)
block|}
DECL|field|compareType
specifier|private
name|CompareType
name|compareType
decl_stmt|;
DECL|method|ExpectedResult
specifier|public
name|ExpectedResult
parameter_list|(
name|HashSet
argument_list|<
name|String
argument_list|>
name|fieldValues
parameter_list|,
name|CompareType
name|compareType
parameter_list|)
block|{
name|this
operator|.
name|fieldValues
operator|=
name|fieldValues
expr_stmt|;
name|this
operator|.
name|compareType
operator|=
name|compareType
expr_stmt|;
block|}
DECL|method|getFieldValues
specifier|public
name|HashSet
argument_list|<
name|String
argument_list|>
name|getFieldValues
parameter_list|()
block|{
return|return
name|fieldValues
return|;
block|}
DECL|method|getCompareType
specifier|public
name|CompareType
name|getCompareType
parameter_list|()
block|{
return|return
name|compareType
return|;
block|}
block|}
DECL|method|setupMorphline
specifier|public
specifier|static
name|void
name|setupMorphline
parameter_list|(
name|String
name|tempDir
parameter_list|,
name|String
name|file
parameter_list|,
name|boolean
name|replaceSolrLocator
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|morphlineText
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
operator|new
name|File
argument_list|(
name|RESOURCES_DIR
operator|+
literal|"/"
operator|+
name|file
operator|+
literal|".conf"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|morphlineText
operator|=
name|morphlineText
operator|.
name|replace
argument_list|(
literal|"RESOURCES_DIR"
argument_list|,
operator|new
name|File
argument_list|(
name|tempDir
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|replaceSolrLocator
condition|)
block|{
name|morphlineText
operator|=
name|morphlineText
operator|.
name|replace
argument_list|(
literal|"${SOLR_LOCATOR}"
argument_list|,
literal|"{ collection : collection1 }"
argument_list|)
expr_stmt|;
block|}
operator|new
name|File
argument_list|(
name|tempDir
operator|+
literal|"/"
operator|+
name|file
operator|+
literal|".conf"
argument_list|)
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
operator|new
name|File
argument_list|(
name|tempDir
operator|+
literal|"/"
operator|+
name|file
operator|+
literal|".conf"
argument_list|)
argument_list|,
name|morphlineText
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
