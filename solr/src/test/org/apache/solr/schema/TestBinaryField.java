begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|beans
operator|.
name|Field
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
name|CommonsHttpSolrServer
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrDocumentList
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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|AbstractSolrTestCase
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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|util
operator|.
name|List
import|;
end_import
begin_class
DECL|class|TestBinaryField
specifier|public
class|class
name|TestBinaryField
extends|extends
name|LuceneTestCase
block|{
DECL|field|server
name|CommonsHttpSolrServer
name|server
decl_stmt|;
DECL|field|jetty
name|JettySolrRunner
name|jetty
decl_stmt|;
DECL|field|port
name|int
name|port
init|=
literal|0
decl_stmt|;
DECL|field|context
specifier|static
specifier|final
name|String
name|context
init|=
literal|"/example"
decl_stmt|;
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
name|File
name|home
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"solrtest-TestBinaryField-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|homeDir
init|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"example"
argument_list|)
decl_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|File
name|confDir
init|=
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
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
name|String
name|fname
init|=
literal|"."
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
operator|+
literal|"solrconfig-slave1.xml"
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|fname
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
name|fname
operator|=
literal|"."
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
operator|+
literal|"schema-binaryfield.xml"
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|fname
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|homeDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
name|jetty
operator|=
operator|new
name|JettySolrRunner
argument_list|(
name|context
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
name|port
operator|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|String
name|url
init|=
literal|"http://localhost:"
operator|+
name|jetty
operator|.
name|getLocalPort
argument_list|()
operator|+
name|context
decl_stmt|;
name|server
operator|=
operator|new
name|CommonsHttpSolrServer
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|10
index|]
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|buf
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
name|SolrInputDocument
name|doc
init|=
literal|null
decl_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"data"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buf
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"data"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buf
argument_list|,
literal|4
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"data"
argument_list|,
name|buf
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|QueryResponse
name|resp
init|=
name|server
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|res
init|=
name|resp
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Bean
argument_list|>
name|beans
init|=
name|resp
operator|.
name|getBeans
argument_list|(
name|Bean
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|res
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|beans
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrDocument
name|d
range|:
name|res
control|)
block|{
name|Integer
name|id
init|=
operator|(
name|Integer
operator|)
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|(
name|byte
index|[]
operator|)
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"data"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|data
operator|.
name|length
argument_list|)
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|+
literal|2
argument_list|)
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|2
condition|)
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|data
operator|.
name|length
argument_list|)
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|+
literal|4
argument_list|)
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|3
condition|)
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|data
operator|.
name|length
argument_list|)
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Bean
name|d
range|:
name|beans
control|)
block|{
name|Integer
name|id
init|=
name|d
operator|.
name|id
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|d
operator|.
name|data
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|data
operator|.
name|length
argument_list|)
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|+
literal|2
argument_list|)
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|2
condition|)
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|data
operator|.
name|length
argument_list|)
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|+
literal|4
argument_list|)
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|3
condition|)
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|data
operator|.
name|length
argument_list|)
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|Bean
specifier|public
specifier|static
class|class
name|Bean
block|{
annotation|@
name|Field
DECL|field|id
name|int
name|id
decl_stmt|;
annotation|@
name|Field
DECL|field|data
name|byte
index|[]
name|data
decl_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
