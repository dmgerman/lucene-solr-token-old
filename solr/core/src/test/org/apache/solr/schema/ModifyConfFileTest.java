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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|Charsets
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
name|ContentStream
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
name|ContentStreamBase
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
name|NamedList
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
name|SimpleOrderedMap
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
name|core
operator|.
name|SolrCore
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
name|SolrRequestHandler
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
name|response
operator|.
name|SolrQueryResponse
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
name|ArrayList
import|;
end_import
begin_class
DECL|class|ModifyConfFileTest
specifier|public
class|class
name|ModifyConfFileTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|solrHomeDirectory
specifier|private
name|File
name|solrHomeDirectory
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
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
DECL|method|init
specifier|private
name|CoreContainer
name|init
parameter_list|()
throws|throws
name|Exception
block|{
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
if|if
condition|(
name|solrHomeDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Failed to mkdirs workDir"
argument_list|,
name|solrHomeDirectory
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|copySolrHomeToTemp
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"core1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"core1"
argument_list|)
argument_list|,
literal|"core.properties"
argument_list|)
argument_list|,
literal|""
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
operator|new
name|CoreContainer
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|cores
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|cores
return|;
block|}
annotation|@
name|Test
DECL|method|testConfigWrite
specifier|public
name|void
name|testConfigWrite
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CoreContainer
name|cc
init|=
name|init
argument_list|()
decl_stmt|;
try|try
block|{
comment|//final CoreAdminHandler admin = new CoreAdminHandler(cc);
name|SolrCore
name|core
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core1"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/admin/fileedit"
argument_list|)
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
name|params
argument_list|(
literal|"file"
argument_list|,
literal|"schema.xml"
argument_list|,
literal|"op"
argument_list|,
literal|"write"
argument_list|)
decl_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rsp
operator|.
name|getException
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Input stream list was null for admin file write operation."
argument_list|)
expr_stmt|;
name|params
operator|=
name|params
argument_list|(
literal|"op"
argument_list|,
literal|"write"
argument_list|)
expr_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rsp
operator|.
name|getException
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"No file name specified for write operation."
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
literal|"Testing rewrite of schema.xml file."
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|=
name|params
argument_list|(
literal|"op"
argument_list|,
literal|"write"
argument_list|,
literal|"file"
argument_list|,
literal|"bogus.txt"
argument_list|)
expr_stmt|;
name|LocalSolrQueryRequest
name|locReq
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|locReq
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
name|locReq
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rsp
operator|.
name|getException
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Can not access: bogus.txt"
argument_list|)
expr_stmt|;
name|String
name|top
init|=
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
literal|"/collection1/conf"
decl_stmt|;
name|String
name|badConf
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
operator|new
name|File
argument_list|(
name|top
argument_list|,
literal|"solrconfig-minimal.xml"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"</dataDir>"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|params
operator|=
name|params
argument_list|(
literal|"op"
argument_list|,
literal|"write"
argument_list|,
literal|"file"
argument_list|,
literal|"solrconfig.xml"
argument_list|)
expr_stmt|;
name|locReq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|streams
operator|.
name|clear
argument_list|()
expr_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|badConf
argument_list|)
argument_list|)
expr_stmt|;
name|locReq
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
name|locReq
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should have detected an error early!"
argument_list|,
name|rsp
operator|.
name|getException
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"\"dataDir\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should have detected an error early!"
argument_list|,
name|rsp
operator|.
name|getException
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"\"</dataDir>\""
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|=
name|params
argument_list|(
literal|"op"
argument_list|,
literal|"test"
argument_list|,
literal|"file"
argument_list|,
literal|"schema.xml"
argument_list|,
literal|"stream.body"
argument_list|,
literal|"Testing rewrite of schema.xml file."
argument_list|)
expr_stmt|;
name|locReq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|locReq
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
name|locReq
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Schema should have caused core reload to fail!"
argument_list|,
name|rsp
operator|.
name|getException
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"SAXParseException"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|String
name|contents
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
operator|new
name|File
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
argument_list|,
literal|"conf/schema.xml"
argument_list|)
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Schema contents should NOT have changed!"
argument_list|,
name|contents
operator|.
name|contains
argument_list|(
literal|"Testing rewrite of schema.xml file."
argument_list|)
argument_list|)
expr_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
literal|"This should barf"
argument_list|)
argument_list|)
expr_stmt|;
name|locReq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|locReq
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
name|locReq
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rsp
operator|.
name|getException
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"More than one input stream was found for admin file write operation."
argument_list|)
expr_stmt|;
name|streams
operator|.
name|clear
argument_list|()
expr_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
literal|"Some bogus stuff for a test."
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|=
name|params
argument_list|(
literal|"op"
argument_list|,
literal|"write"
argument_list|,
literal|"file"
argument_list|,
literal|"velocity/test.vm"
argument_list|)
expr_stmt|;
name|locReq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|locReq
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
name|locReq
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|contents
operator|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
operator|new
name|File
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
argument_list|,
literal|"conf/velocity/test.vm"
argument_list|)
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Schema contents should have changed!"
argument_list|,
literal|"Some bogus stuff for a test."
argument_list|,
name|contents
argument_list|)
expr_stmt|;
name|streams
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|=
name|params
argument_list|()
expr_stmt|;
name|locReq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/admin/file"
argument_list|)
argument_list|,
name|locReq
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|NamedList
name|files
init|=
operator|(
name|NamedList
operator|)
name|res
operator|.
name|get
argument_list|(
literal|"files"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have gotten files back"
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
name|schema
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|files
operator|.
name|get
argument_list|(
literal|"schema.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have a schema returned"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Schema.xml should not be a directory"
argument_list|,
name|schema
operator|.
name|get
argument_list|(
literal|"directory"
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
name|velocity
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|files
operator|.
name|get
argument_list|(
literal|"velocity"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have velocity dir returned"
argument_list|,
name|velocity
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Velocity should be a directory"
argument_list|,
operator|(
name|boolean
operator|)
name|velocity
operator|.
name|get
argument_list|(
literal|"directory"
argument_list|)
argument_list|)
expr_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|solrHomeDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
