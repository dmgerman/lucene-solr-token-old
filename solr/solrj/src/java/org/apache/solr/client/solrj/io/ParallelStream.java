begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io
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
name|io
package|;
end_package
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
name|io
operator|.
name|ObjectOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
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
name|Comparator
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|ClusterState
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
name|cloud
operator|.
name|Replica
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
name|cloud
operator|.
name|Slice
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
name|cloud
operator|.
name|ZkCoreNodeProps
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
name|cloud
operator|.
name|ZkStateReader
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
name|Base64
import|;
end_import
begin_comment
comment|/**  * The ParallelStream decorates a TupleStream implementation and pushes it to N workers for parallel execution.  * Workers are chosen from a SolrCloud collection.  * Tuples that are streamed back from the workers are ordered by a Comparator.  **/
end_comment
begin_class
DECL|class|ParallelStream
specifier|public
class|class
name|ParallelStream
extends|extends
name|CloudSolrStream
block|{
DECL|field|tupleStream
specifier|private
name|TupleStream
name|tupleStream
decl_stmt|;
DECL|field|workers
specifier|private
name|int
name|workers
decl_stmt|;
DECL|field|encoded
specifier|private
name|String
name|encoded
decl_stmt|;
DECL|method|ParallelStream
specifier|public
name|ParallelStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|TupleStream
name|tupleStream
parameter_list|,
name|int
name|workers
parameter_list|,
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|workers
operator|=
name|workers
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
name|this
operator|.
name|tupleStream
operator|=
name|tupleStream
expr_stmt|;
name|ByteArrayOutputStream
name|bout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ObjectOutputStream
name|out
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|bout
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeObject
argument_list|(
name|tupleStream
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|bout
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|this
operator|.
name|encoded
operator|=
name|Base64
operator|.
name|byteArrayToBase64
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|encoded
operator|=
name|URLEncoder
operator|.
name|encode
argument_list|(
name|this
operator|.
name|encoded
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|this
operator|.
name|tuples
operator|=
operator|new
name|TreeSet
argument_list|()
expr_stmt|;
block|}
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
name|List
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|tupleStream
argument_list|)
expr_stmt|;
return|return
name|l
return|;
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|Tuple
name|tuple
init|=
name|_read
argument_list|()
decl_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Tuple
name|t
init|=
operator|new
name|Tuple
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|t
operator|.
name|setMetrics
argument_list|(
name|this
operator|.
name|eofTuples
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
return|return
name|tuple
return|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|streamContext
parameter_list|)
block|{
comment|//Note the parallel stream does not set the StreamContext on it's substream.
comment|//This is because the substream is not actually opened by the ParallelStream.
name|this
operator|.
name|streamContext
operator|=
name|streamContext
expr_stmt|;
block|}
DECL|method|constructStreams
specifier|protected
name|void
name|constructStreams
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|ZkStateReader
name|zkStateReader
init|=
name|cloudSolrClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getActiveSlices
argument_list|(
name|this
operator|.
name|collection
argument_list|)
decl_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|shuffler
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
name|shuffler
operator|.
name|add
argument_list|(
name|replica
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|workers
operator|>
name|shuffler
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Number of workers exceeds nodes in the worker collection"
argument_list|)
throw|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|shuffler
argument_list|,
operator|new
name|Random
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|w
init|=
literal|0
init|;
name|w
operator|<
name|workers
condition|;
name|w
operator|++
control|)
block|{
name|HashMap
name|params
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// We are the aggregator.
name|params
operator|.
name|put
argument_list|(
literal|"numWorkers"
argument_list|,
name|workers
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"workerID"
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"stream"
argument_list|,
name|this
operator|.
name|encoded
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"qt"
argument_list|,
literal|"/stream"
argument_list|)
expr_stmt|;
name|Replica
name|rep
init|=
name|shuffler
operator|.
name|get
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|ZkCoreNodeProps
name|zkProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|rep
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|zkProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|SolrStream
name|solrStream
init|=
operator|new
name|SolrStream
argument_list|(
name|url
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|solrStreams
operator|.
name|add
argument_list|(
name|solrStream
argument_list|)
expr_stmt|;
block|}
assert|assert
operator|(
name|solrStreams
operator|.
name|size
argument_list|()
operator|==
name|workers
operator|)
assert|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
