begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.embedded
package|package
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
name|FileInputStream
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathFactory
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
name|request
operator|.
name|CoreAdminRequest
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
name|request
operator|.
name|QueryRequest
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
name|request
operator|.
name|UpdateRequest
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
name|request
operator|.
name|UpdateRequest
operator|.
name|ACTION
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
name|CoreAdminResponse
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
name|util
operator|.
name|AbstractSolrTestCase
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_comment
comment|/**  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestSolrProperties
specifier|public
class|class
name|TestSolrProperties
extends|extends
name|LuceneTestCase
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestSolrProperties
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cores
specifier|protected
name|CoreContainer
name|cores
init|=
literal|null
decl_stmt|;
DECL|field|solrXml
specifier|private
name|File
name|solrXml
decl_stmt|;
DECL|field|xpathFactory
specifier|private
specifier|static
specifier|final
name|XPathFactory
name|xpathFactory
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
literal|"solr/shared"
return|;
block|}
DECL|method|getSolrXml
specifier|public
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"solr.xml"
return|;
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|getSolrHome
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"pwd: "
operator|+
operator|(
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|home
init|=
operator|new
name|File
argument_list|(
name|getSolrHome
argument_list|()
argument_list|)
decl_stmt|;
name|solrXml
operator|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"solr.xml"
argument_list|)
expr_stmt|;
name|cores
operator|=
operator|new
name|CoreContainer
argument_list|(
name|getSolrHome
argument_list|()
argument_list|,
name|solrXml
argument_list|)
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
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|getSolrHome
argument_list|()
operator|+
literal|"/data"
argument_list|)
decl_stmt|;
name|String
name|skip
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.test.leavedatadir"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|skip
operator|&&
literal|0
operator|!=
name|skip
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"NOTE: per solr.test.leavedatadir, dataDir will not be removed: "
operator|+
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|AbstractSolrTestCase
operator|.
name|recurseDelete
argument_list|(
name|dataDir
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"!!!! WARNING: best effort to remove "
operator|+
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" FAILED !!!!!"
argument_list|)
expr_stmt|;
block|}
block|}
name|File
name|persistedFile
init|=
operator|new
name|File
argument_list|(
name|getSolrHome
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr-persist.xml"
argument_list|)
decl_stmt|;
name|persistedFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|getSolrCore0
specifier|protected
name|SolrServer
name|getSolrCore0
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"core0"
argument_list|)
return|;
block|}
DECL|method|getSolrCore1
specifier|protected
name|SolrServer
name|getSolrCore1
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"core1"
argument_list|)
return|;
block|}
DECL|method|getSolrAdmin
specifier|protected
name|SolrServer
name|getSolrAdmin
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"core0"
argument_list|)
return|;
block|}
DECL|method|getSolrCore
specifier|protected
name|SolrServer
name|getSolrCore
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testProperties
specifier|public
name|void
name|testProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|UpdateRequest
name|up
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|up
operator|.
name|setAction
argument_list|(
name|ACTION
operator|.
name|COMMIT
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|up
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|up
operator|.
name|process
argument_list|(
name|getSolrCore0
argument_list|()
argument_list|)
expr_stmt|;
name|up
operator|.
name|process
argument_list|(
name|getSolrCore1
argument_list|()
argument_list|)
expr_stmt|;
name|up
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Add something to each core
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"AAA"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"core0"
argument_list|,
literal|"yup stopfra stopfrb stopena stopenb"
argument_list|)
expr_stmt|;
comment|// Add to core0
name|up
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|up
operator|.
name|process
argument_list|(
name|getSolrCore0
argument_list|()
argument_list|)
expr_stmt|;
name|SolrTestCaseJ4
operator|.
name|ignoreException
argument_list|(
literal|"unknown field"
argument_list|)
expr_stmt|;
comment|// You can't add it to core1
try|try
block|{
name|up
operator|.
name|process
argument_list|(
name|getSolrCore1
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Can't add core0 field to core1!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{     }
comment|// Add to core1
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"BBB"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"core1"
argument_list|,
literal|"yup stopfra stopfrb stopena stopenb"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
literal|"core0"
argument_list|)
expr_stmt|;
name|up
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|up
operator|.
name|process
argument_list|(
name|getSolrCore1
argument_list|()
argument_list|)
expr_stmt|;
comment|// You can't add it to core1
try|try
block|{
name|SolrTestCaseJ4
operator|.
name|ignoreException
argument_list|(
literal|"core0"
argument_list|)
expr_stmt|;
name|up
operator|.
name|process
argument_list|(
name|getSolrCore0
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Can't add core1 field to core0!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{     }
name|SolrTestCaseJ4
operator|.
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
comment|// now Make sure AAA is in 0 and BBB in 1
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|QueryRequest
name|r
init|=
operator|new
name|QueryRequest
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|q
operator|.
name|setQuery
argument_list|(
literal|"id:AAA"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|process
argument_list|(
name|getSolrCore0
argument_list|()
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|r
operator|.
name|process
argument_list|(
name|getSolrCore1
argument_list|()
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now test Changing the default core
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getSolrCore0
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:AAA"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getSolrCore0
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:BBB"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getSolrCore1
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:AAA"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getSolrCore1
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:BBB"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now test reloading it should have a newer open time
name|String
name|name
init|=
literal|"core0"
decl_stmt|;
name|SolrServer
name|coreadmin
init|=
name|getSolrAdmin
argument_list|()
decl_stmt|;
name|CoreAdminResponse
name|mcr
init|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
name|name
argument_list|,
name|coreadmin
argument_list|)
decl_stmt|;
name|long
name|before
init|=
name|mcr
operator|.
name|getStartTime
argument_list|(
name|name
argument_list|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|CoreAdminRequest
operator|.
name|reloadCore
argument_list|(
name|name
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
name|mcr
operator|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
name|name
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
name|long
name|after
init|=
name|mcr
operator|.
name|getStartTime
argument_list|(
name|name
argument_list|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"should have more recent time: "
operator|+
name|after
operator|+
literal|","
operator|+
name|before
argument_list|,
name|after
operator|>
name|before
argument_list|)
expr_stmt|;
name|mcr
operator|=
name|CoreAdminRequest
operator|.
name|persist
argument_list|(
literal|"solr-persist.xml"
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
comment|// System.out.println(IOUtils.toString(new FileInputStream(new File(solrXml.getParent(), "solr-persist.xml"))));
name|DocumentBuilder
name|builder
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|solrXml
operator|.
name|getParent
argument_list|()
argument_list|,
literal|"solr-persist.xml"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Document
name|document
init|=
name|builder
operator|.
name|parse
argument_list|(
name|fis
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|(
literal|"/solr/cores[@defaultCoreName='core0']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|exists
specifier|public
specifier|static
name|boolean
name|exists
parameter_list|(
name|String
name|xpathStr
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|XPathExpressionException
block|{
name|XPath
name|xpath
init|=
name|xpathFactory
operator|.
name|newXPath
argument_list|()
decl_stmt|;
return|return
operator|(
name|Boolean
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
name|xpathStr
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|BOOLEAN
argument_list|)
return|;
block|}
block|}
end_class
end_unit
