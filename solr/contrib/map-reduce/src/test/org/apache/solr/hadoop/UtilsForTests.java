begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
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
name|EmbeddedSolrServer
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import
begin_class
DECL|class|UtilsForTests
specifier|public
class|class
name|UtilsForTests
block|{
DECL|method|validateSolrServerDocumentCount
specifier|public
specifier|static
name|void
name|validateSolrServerDocumentCount
parameter_list|(
name|File
name|solrHomeDir
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Path
name|outDir
parameter_list|,
name|int
name|expectedDocs
parameter_list|,
name|int
name|expectedShards
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|long
name|actualDocs
init|=
literal|0
decl_stmt|;
name|int
name|actualShards
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FileStatus
name|dir
range|:
name|fs
operator|.
name|listStatus
argument_list|(
name|outDir
argument_list|)
control|)
block|{
comment|// for each shard
if|if
condition|(
name|dir
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"part"
argument_list|)
operator|&&
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|actualShards
operator|++
expr_stmt|;
try|try
init|(
name|EmbeddedSolrServer
name|solr
init|=
name|SolrRecordWriter
operator|.
name|createEmbeddedSolrServer
argument_list|(
operator|new
name|Path
argument_list|(
name|solrHomeDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|fs
argument_list|,
name|dir
operator|.
name|getPath
argument_list|()
argument_list|)
init|)
block|{
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|QueryResponse
name|resp
init|=
name|solr
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|long
name|numDocs
init|=
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|actualDocs
operator|+=
name|numDocs
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
name|expectedShards
argument_list|,
name|actualShards
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedDocs
argument_list|,
name|actualDocs
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
