begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
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
name|BytesRef
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
name|common
operator|.
name|util
operator|.
name|Hash
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
name|SolrQueryRequest
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
DECL|class|VersionInfoTest
specifier|public
class|class
name|VersionInfoTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testMaxIndexedVersionFromIndex
specifier|public
name|void
name|testMaxIndexedVersionFromIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema-version-indexed.xml"
argument_list|)
expr_stmt|;
try|try
block|{
name|testMaxVersionLogic
argument_list|(
name|req
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|deleteCore
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMaxDocValuesVersionFromIndex
specifier|public
name|void
name|testMaxDocValuesVersionFromIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema-version-dv.xml"
argument_list|)
expr_stmt|;
try|try
block|{
name|testMaxVersionLogic
argument_list|(
name|req
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|deleteCore
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testMaxVersionLogic
specifier|protected
name|void
name|testMaxVersionLogic
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateHandler
name|uhandler
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|UpdateLog
name|ulog
init|=
name|uhandler
operator|.
name|getUpdateLog
argument_list|()
decl_stmt|;
name|ulog
operator|.
name|init
argument_list|(
name|uhandler
argument_list|,
name|req
operator|.
name|getCore
argument_list|()
argument_list|)
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
comment|// index the first doc
name|String
name|docId
init|=
name|Integer
operator|.
name|toString
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|docId
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// max from index should not be 0 or null
name|Long
name|maxVersionFromIndex
init|=
name|ulog
operator|.
name|getMaxVersionFromIndex
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|maxVersionFromIndex
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|maxVersionFromIndex
operator|!=
literal|0L
argument_list|)
expr_stmt|;
comment|// version from index should be less than or equal the version of the first doc indexed
name|VersionInfo
name|vInfo
init|=
name|ulog
operator|.
name|getVersionInfo
argument_list|()
decl_stmt|;
name|Long
name|version
init|=
name|vInfo
operator|.
name|getVersionFromIndex
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|docId
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"version info should not be null for test doc: "
operator|+
name|docId
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"max version from index should be less than or equal to the version of first doc added, diff: "
operator|+
operator|(
name|version
operator|-
name|maxVersionFromIndex
operator|)
argument_list|,
name|maxVersionFromIndex
operator|<=
name|version
argument_list|)
expr_stmt|;
name|BytesRef
name|idBytes
init|=
operator|new
name|BytesRef
argument_list|(
name|docId
argument_list|)
decl_stmt|;
name|int
name|bucketHash
init|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|idBytes
operator|.
name|bytes
argument_list|,
name|idBytes
operator|.
name|offset
argument_list|,
name|idBytes
operator|.
name|length
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|VersionBucket
name|bucket
init|=
name|vInfo
operator|.
name|bucket
argument_list|(
name|bucketHash
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bucket
operator|.
name|highest
operator|==
name|version
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// send 2nd doc ...
name|docId
operator|=
name|Integer
operator|.
name|toString
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|docId
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|maxVersionFromIndex
operator|=
name|ulog
operator|.
name|getMaxVersionFromIndex
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|maxVersionFromIndex
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|maxVersionFromIndex
operator|!=
literal|0L
argument_list|)
expr_stmt|;
name|vInfo
operator|=
name|ulog
operator|.
name|getVersionInfo
argument_list|()
expr_stmt|;
name|version
operator|=
name|vInfo
operator|.
name|getVersionFromIndex
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|docId
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"version info should not be null for test doc: "
operator|+
name|docId
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"max version from index should be less than version of last doc added, diff: "
operator|+
operator|(
name|version
operator|-
name|maxVersionFromIndex
operator|)
argument_list|,
name|maxVersionFromIndex
operator|<
name|version
argument_list|)
expr_stmt|;
name|idBytes
operator|=
operator|new
name|BytesRef
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|bucketHash
operator|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|idBytes
operator|.
name|bytes
argument_list|,
name|idBytes
operator|.
name|offset
argument_list|,
name|idBytes
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|bucket
operator|=
name|vInfo
operator|.
name|bucket
argument_list|(
name|bucketHash
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bucket
operator|.
name|highest
operator|==
name|version
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Long
name|versionFromTLog
init|=
name|ulog
operator|.
name|lookupVersion
argument_list|(
name|idBytes
argument_list|)
decl_stmt|;
name|Long
name|versionFromIndex
init|=
name|vInfo
operator|.
name|getVersionFromIndex
argument_list|(
name|idBytes
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"version from tlog and version from index should be the same"
argument_list|,
name|versionFromTLog
argument_list|,
name|versionFromIndex
argument_list|)
expr_stmt|;
comment|// reload the core, which should reset the max
name|CoreContainer
name|coreContainer
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|coreContainer
operator|.
name|reload
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|maxVersionFromIndex
operator|=
name|ulog
operator|.
name|getMaxVersionFromIndex
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max version from index should be equal to version of last doc added after reload"
argument_list|,
name|maxVersionFromIndex
argument_list|,
name|version
argument_list|)
expr_stmt|;
comment|// one more doc after reload
name|docId
operator|=
name|Integer
operator|.
name|toString
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|docId
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|maxVersionFromIndex
operator|=
name|ulog
operator|.
name|getMaxVersionFromIndex
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|maxVersionFromIndex
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|maxVersionFromIndex
operator|!=
literal|0L
argument_list|)
expr_stmt|;
name|vInfo
operator|=
name|ulog
operator|.
name|getVersionInfo
argument_list|()
expr_stmt|;
name|version
operator|=
name|vInfo
operator|.
name|getVersionFromIndex
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|docId
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"version info should not be null for test doc: "
operator|+
name|docId
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"max version from index should be less than version of last doc added, diff: "
operator|+
operator|(
name|version
operator|-
name|maxVersionFromIndex
operator|)
argument_list|,
name|maxVersionFromIndex
operator|<
name|version
argument_list|)
expr_stmt|;
name|idBytes
operator|=
operator|new
name|BytesRef
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|bucketHash
operator|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|idBytes
operator|.
name|bytes
argument_list|,
name|idBytes
operator|.
name|offset
argument_list|,
name|idBytes
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|bucket
operator|=
name|vInfo
operator|.
name|bucket
argument_list|(
name|bucketHash
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bucket
operator|.
name|highest
operator|==
name|version
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
