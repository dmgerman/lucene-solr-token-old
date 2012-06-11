begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
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
name|IOUtils
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
name|AbstractUpdateRequest
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
name|request
operator|.
name|*
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
name|FileUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
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
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
import|;
end_import
begin_comment
comment|/**  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestSolrProperties
specifier|public
class|class
name|TestSolrProperties
extends|extends
name|AbstractEmbeddedSolrServerTestCase
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
DECL|field|SOLR_XML
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_XML
init|=
literal|"solr.xml"
decl_stmt|;
DECL|field|SOLR_PERSIST_XML
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_PERSIST_XML
init|=
literal|"solr-persist.xml"
decl_stmt|;
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
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
annotation|@
name|Override
DECL|method|getSolrXml
specifier|protected
name|File
name|getSolrXml
parameter_list|()
throws|throws
name|Exception
block|{
comment|//This test writes on the directory where the solr.xml is located. Better to copy the solr.xml to
comment|//the temporary directory where we store the index
name|File
name|origSolrXml
init|=
operator|new
name|File
argument_list|(
name|SOLR_HOME
argument_list|,
name|SOLR_XML
argument_list|)
decl_stmt|;
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
name|SOLR_XML
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|origSolrXml
argument_list|,
name|solrXml
argument_list|)
expr_stmt|;
return|return
name|solrXml
return|;
block|}
annotation|@
name|Override
DECL|method|deleteAdditionalFiles
specifier|protected
name|void
name|deleteAdditionalFiles
parameter_list|()
block|{
name|super
operator|.
name|deleteAdditionalFiles
argument_list|()
expr_stmt|;
comment|//Cleans the solr.xml persisted while testing and the solr.xml copied to the temporary directory
name|File
name|persistedFile
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
name|SOLR_PERSIST_XML
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to delete "
operator|+
name|persistedFile
argument_list|,
name|persistedFile
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
name|SOLR_XML
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to delete "
operator|+
name|solrXml
argument_list|,
name|solrXml
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|getRenamedSolrAdmin
specifier|protected
name|SolrServer
name|getRenamedSolrAdmin
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"renamed_core"
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
name|SOLR_PERSIST_XML
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
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
name|tempDir
argument_list|,
name|SOLR_PERSIST_XML
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
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
name|SOLR_PERSIST_XML
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|solrPersistXml
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|fis
argument_list|)
decl_stmt|;
comment|//System.out.println("xml:" + solrPersistXml);
name|assertTrue
argument_list|(
literal|"\"/solr/cores[@defaultCoreName='core0']\" doesn't match in:\n"
operator|+
name|solrPersistXml
argument_list|,
name|exists
argument_list|(
literal|"/solr/cores[@defaultCoreName='core0']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"\"/solr/cores[@host='127.0.0.1']\" doesn't match in:\n"
operator|+
name|solrPersistXml
argument_list|,
name|exists
argument_list|(
literal|"/solr/cores[@host='127.0.0.1']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"\"/solr/cores[@hostPort='${hostPort:8983}']\" doesn't match in:\n"
operator|+
name|solrPersistXml
argument_list|,
name|exists
argument_list|(
literal|"/solr/cores[@hostPort='${hostPort:8983}']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"\"/solr/cores[@zkClientTimeout='8000']\" doesn't match in:\n"
operator|+
name|solrPersistXml
argument_list|,
name|exists
argument_list|(
literal|"/solr/cores[@zkClientTimeout='8000']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"\"/solr/cores[@hostContext='solr']\" doesn't match in:\n"
operator|+
name|solrPersistXml
argument_list|,
name|exists
argument_list|(
literal|"/solr/cores[@hostContext='solr']"
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
name|CoreAdminRequest
operator|.
name|renameCore
argument_list|(
name|name
argument_list|,
literal|"renamed_core"
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
name|mcr
operator|=
name|CoreAdminRequest
operator|.
name|persist
argument_list|(
name|SOLR_PERSIST_XML
argument_list|,
name|getRenamedSolrAdmin
argument_list|()
argument_list|)
expr_stmt|;
comment|//    fis = new FileInputStream(new File(solrXml.getParent(), SOLR_PERSIST_XML));
comment|//    String solrPersistXml = IOUtils.toString(fis);
comment|//    System.out.println("xml:" + solrPersistXml);
comment|//    fis.close();
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
name|SOLR_PERSIST_XML
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"/solr/cores/core[@name='renamed_core']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|(
literal|"/solr/cores/core[@instanceDir='${theInstanceDir:./}']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|(
literal|"/solr/cores/core[@collection='${collection:acollection}']"
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
name|coreadmin
operator|=
name|getRenamedSolrAdmin
argument_list|()
expr_stmt|;
name|CoreAdminRequest
operator|.
name|createCore
argument_list|(
literal|"newCore"
argument_list|,
name|SOLR_HOME
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
comment|//    fis = new FileInputStream(new File(solrXml.getParent(), SOLR_PERSIST_XML));
comment|//    solrPersistXml = IOUtils.toString(fis);
comment|//    System.out.println("xml:" + solrPersistXml);
comment|//    fis.close();
name|mcr
operator|=
name|CoreAdminRequest
operator|.
name|persist
argument_list|(
name|SOLR_PERSIST_XML
argument_list|,
name|getRenamedSolrAdmin
argument_list|()
argument_list|)
expr_stmt|;
comment|//    fis = new FileInputStream(new File(solrXml.getParent(), SOLR_PERSIST_XML));
comment|//    solrPersistXml = IOUtils.toString(fis);
comment|//    System.out.println("xml:" + solrPersistXml);
comment|//    fis.close();
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
name|SOLR_PERSIST_XML
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"/solr/cores/core[@name='collection1' and (@instanceDir='./' or @instanceDir='.\\')]"
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
comment|// test reload and parse
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cores
operator|=
operator|new
name|CoreContainer
argument_list|(
name|SOLR_HOME
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
name|SOLR_PERSIST_XML
argument_list|)
argument_list|)
expr_stmt|;
name|mcr
operator|=
name|CoreAdminRequest
operator|.
name|persist
argument_list|(
name|SOLR_PERSIST_XML
argument_list|,
name|getRenamedSolrAdmin
argument_list|()
argument_list|)
expr_stmt|;
comment|//     fis = new FileInputStream(new File(solrXml.getParent(),
comment|//     SOLR_PERSIST_XML));
comment|//     solrPersistXml = IOUtils.toString(fis);
comment|//     System.out.println("xml:" + solrPersistXml);
comment|//     fis.close();
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
