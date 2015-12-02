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
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configurable
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
name|conf
operator|.
name|Configuration
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
name|io
operator|.
name|Text
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
name|mapreduce
operator|.
name|Partitioner
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
name|common
operator|.
name|cloud
operator|.
name|DocCollection
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
name|DocRouter
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
name|params
operator|.
name|MapSolrParams
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
name|SolrParams
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
comment|/**  * MapReduce partitioner that partitions the Mapper output such that each  * SolrInputDocument gets sent to the SolrCloud shard that it would have been  * sent to if the document were ingested via the standard SolrCloud Near Real  * Time (NRT) API.  *   * In other words, this class implements the same partitioning semantics as the  * standard SolrCloud NRT API. This enables to mix batch updates from MapReduce  * ingestion with updates from standard NRT ingestion on the same SolrCloud  * cluster, using identical unique document keys.  */
end_comment
begin_class
DECL|class|SolrCloudPartitioner
specifier|public
class|class
name|SolrCloudPartitioner
extends|extends
name|Partitioner
argument_list|<
name|Text
argument_list|,
name|SolrInputDocumentWritable
argument_list|>
implements|implements
name|Configurable
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|docCollection
specifier|private
name|DocCollection
name|docCollection
decl_stmt|;
DECL|field|shardNumbers
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|shardNumbers
decl_stmt|;
DECL|field|shards
specifier|private
name|int
name|shards
init|=
literal|0
decl_stmt|;
DECL|field|emptySolrParams
specifier|private
specifier|final
name|SolrParams
name|emptySolrParams
init|=
operator|new
name|MapSolrParams
argument_list|(
name|Collections
operator|.
name|EMPTY_MAP
argument_list|)
decl_stmt|;
DECL|field|SHARDS
specifier|public
specifier|static
specifier|final
name|String
name|SHARDS
init|=
name|SolrCloudPartitioner
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".shards"
decl_stmt|;
DECL|field|ZKHOST
specifier|public
specifier|static
specifier|final
name|String
name|ZKHOST
init|=
name|SolrCloudPartitioner
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".zkHost"
decl_stmt|;
DECL|field|COLLECTION
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTION
init|=
name|SolrCloudPartitioner
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".collection"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|SolrCloudPartitioner
specifier|public
name|SolrCloudPartitioner
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|setConf
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|shards
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|SHARDS
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|shards
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal shards: "
operator|+
name|shards
argument_list|)
throw|;
block|}
name|String
name|zkHost
init|=
name|conf
operator|.
name|get
argument_list|(
name|ZKHOST
argument_list|)
decl_stmt|;
if|if
condition|(
name|zkHost
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"zkHost must not be null"
argument_list|)
throw|;
block|}
name|String
name|collection
init|=
name|conf
operator|.
name|get
argument_list|(
name|COLLECTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"collection must not be null"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Using SolrCloud zkHost: {}, collection: {}"
argument_list|,
name|zkHost
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|docCollection
operator|=
operator|new
name|ZooKeeperInspector
argument_list|()
operator|.
name|extractDocCollection
argument_list|(
name|zkHost
argument_list|,
name|collection
argument_list|)
expr_stmt|;
if|if
condition|(
name|docCollection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docCollection must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|docCollection
operator|.
name|getSlicesMap
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
name|shards
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Incompatible shards: + "
operator|+
name|shards
operator|+
literal|" for docCollection: "
operator|+
name|docCollection
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|ZooKeeperInspector
argument_list|()
operator|.
name|getSortedSlices
argument_list|(
name|docCollection
operator|.
name|getSlices
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|.
name|size
argument_list|()
operator|!=
name|shards
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Incompatible sorted shards: + "
operator|+
name|shards
operator|+
literal|" for docCollection: "
operator|+
name|docCollection
argument_list|)
throw|;
block|}
name|shardNumbers
operator|=
operator|new
name|HashMap
argument_list|(
literal|10
operator|*
name|slices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// sparse for performance
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|slices
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|shardNumbers
operator|.
name|put
argument_list|(
name|slices
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using SolrCloud docCollection: {}"
argument_list|,
name|docCollection
argument_list|)
expr_stmt|;
name|DocRouter
name|docRouter
init|=
name|docCollection
operator|.
name|getRouter
argument_list|()
decl_stmt|;
if|if
condition|(
name|docRouter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docRouter must not be null"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Using SolrCloud docRouterClass: {}"
argument_list|,
name|docRouter
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|getPartition
specifier|public
name|int
name|getPartition
parameter_list|(
name|Text
name|key
parameter_list|,
name|SolrInputDocumentWritable
name|value
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
name|DocRouter
name|docRouter
init|=
name|docCollection
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|value
operator|.
name|getSolrInputDocument
argument_list|()
decl_stmt|;
name|String
name|keyStr
init|=
name|key
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// TODO: scalability: replace linear search in HashBasedRouter.hashToSlice() with binary search on sorted hash ranges
name|Slice
name|slice
init|=
name|docRouter
operator|.
name|getTargetSlice
argument_list|(
name|keyStr
argument_list|,
name|doc
argument_list|,
literal|null
argument_list|,
name|emptySolrParams
argument_list|,
name|docCollection
argument_list|)
decl_stmt|;
comment|//    LOG.info("slice: {}", slice);
if|if
condition|(
name|slice
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No matching slice found! The slice seems unavailable. docRouterClass: "
operator|+
name|docRouter
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|int
name|rootShard
init|=
name|shardNumbers
operator|.
name|get
argument_list|(
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootShard
operator|<
literal|0
operator|||
name|rootShard
operator|>=
name|shards
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal shard number "
operator|+
name|rootShard
operator|+
literal|" for slice: "
operator|+
name|slice
operator|+
literal|", docCollection: "
operator|+
name|docCollection
argument_list|)
throw|;
block|}
comment|// map doc to micro shard aka leaf shard, akin to HashBasedRouter.sliceHash()
comment|// taking into account mtree merge algorithm
assert|assert
name|numPartitions
operator|%
name|shards
operator|==
literal|0
assert|;
comment|// Also note that numPartitions is equal to the number of reducers
name|int
name|hashCode
init|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|keyStr
argument_list|,
literal|0
argument_list|,
name|keyStr
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
operator|(
name|hashCode
operator|&
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|%
operator|(
name|numPartitions
operator|/
name|shards
operator|)
decl_stmt|;
name|int
name|microShard
init|=
operator|(
name|rootShard
operator|*
operator|(
name|numPartitions
operator|/
name|shards
operator|)
operator|)
operator|+
name|offset
decl_stmt|;
comment|//    LOG.info("Subpartitions rootShard: {}, offset: {}", rootShard, offset);
comment|//    LOG.info("Partitioned to p: {} for numPartitions: {}, shards: {}, key: {}, value: {}", microShard, numPartitions, shards, key, value);
assert|assert
name|microShard
operator|>=
literal|0
operator|&&
name|microShard
operator|<
name|numPartitions
assert|;
return|return
name|microShard
return|;
block|}
block|}
end_class
end_unit
