begin_unit
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|SolrException
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
name|StrUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONWriter
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
name|LinkedHashMap
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
import|import static
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
operator|.
name|DOC_ROUTER
import|;
end_import
begin_comment
comment|/**  * Class to partition int range into n ranges.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DocRouter
specifier|public
specifier|abstract
class|class
name|DocRouter
block|{
DECL|field|DEFAULT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NAME
init|=
name|CompositeIdRouter
operator|.
name|NAME
decl_stmt|;
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|DocRouter
name|DEFAULT
init|=
operator|new
name|CompositeIdRouter
argument_list|()
decl_stmt|;
DECL|method|getDocRouter
specifier|public
specifier|static
name|DocRouter
name|getDocRouter
parameter_list|(
name|Object
name|routerName
parameter_list|)
block|{
name|DocRouter
name|router
init|=
name|routerMap
operator|.
name|get
argument_list|(
name|routerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|router
operator|!=
literal|null
condition|)
return|return
name|router
return|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unknown document router '"
operator|+
name|routerName
operator|+
literal|"'"
argument_list|)
throw|;
block|}
DECL|method|getRouteField
specifier|protected
name|String
name|getRouteField
parameter_list|(
name|DocCollection
name|coll
parameter_list|)
block|{
if|if
condition|(
name|coll
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|coll
operator|.
name|get
argument_list|(
name|DOC_ROUTER
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|String
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
return|;
block|}
DECL|method|getRouterSpec
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRouterSpec
parameter_list|(
name|ZkNodeProps
name|props
parameter_list|)
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
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|props
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"router."
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|7
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Object
name|o
init|=
name|props
operator|.
name|get
argument_list|(
literal|"router"
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|map
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|DEFAULT_NAME
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|// currently just an implementation detail...
DECL|field|routerMap
specifier|private
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|DocRouter
argument_list|>
name|routerMap
decl_stmt|;
static|static
block|{
name|routerMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocRouter
argument_list|>
argument_list|()
expr_stmt|;
name|PlainIdRouter
name|plain
init|=
operator|new
name|PlainIdRouter
argument_list|()
decl_stmt|;
comment|// instead of doing back compat this way, we could always convert the clusterstate on first read to "plain" if it doesn't have any properties.
name|routerMap
operator|.
name|put
argument_list|(
literal|null
argument_list|,
name|plain
argument_list|)
expr_stmt|;
comment|// back compat with 4.0
name|routerMap
operator|.
name|put
argument_list|(
name|PlainIdRouter
operator|.
name|NAME
argument_list|,
name|plain
argument_list|)
expr_stmt|;
name|routerMap
operator|.
name|put
argument_list|(
name|CompositeIdRouter
operator|.
name|NAME
argument_list|,
name|DEFAULT_NAME
operator|.
name|equals
argument_list|(
name|CompositeIdRouter
operator|.
name|NAME
argument_list|)
condition|?
name|DEFAULT
else|:
operator|new
name|CompositeIdRouter
argument_list|()
argument_list|)
expr_stmt|;
name|routerMap
operator|.
name|put
argument_list|(
name|ImplicitDocRouter
operator|.
name|NAME
argument_list|,
operator|new
name|ImplicitDocRouter
argument_list|()
argument_list|)
expr_stmt|;
comment|// NOTE: careful that the map keys (the static .NAME members) are filled in by making them final
block|}
comment|// Hash ranges can't currently "wrap" - i.e. max must be greater or equal to min.
comment|// TODO: ranges may not be all contiguous in the future (either that or we will
comment|// need an extra class to model a collection of ranges)
DECL|class|Range
specifier|public
specifier|static
class|class
name|Range
implements|implements
name|JSONWriter
operator|.
name|Writable
block|{
DECL|field|min
specifier|public
name|int
name|min
decl_stmt|;
comment|// inclusive
DECL|field|max
specifier|public
name|int
name|max
decl_stmt|;
comment|// inclusive
DECL|method|Range
specifier|public
name|Range
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
assert|assert
name|min
operator|<=
name|max
assert|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
DECL|method|includes
specifier|public
name|boolean
name|includes
parameter_list|(
name|int
name|hash
parameter_list|)
block|{
return|return
name|hash
operator|>=
name|min
operator|&&
name|hash
operator|<=
name|max
return|;
block|}
DECL|method|isSubsetOf
specifier|public
name|boolean
name|isSubsetOf
parameter_list|(
name|Range
name|superset
parameter_list|)
block|{
return|return
name|superset
operator|.
name|min
operator|<=
name|min
operator|&&
name|superset
operator|.
name|max
operator|>=
name|max
return|;
block|}
DECL|method|overlaps
specifier|public
name|boolean
name|overlaps
parameter_list|(
name|Range
name|other
parameter_list|)
block|{
return|return
name|includes
argument_list|(
name|other
operator|.
name|min
argument_list|)
operator|||
name|includes
argument_list|(
name|other
operator|.
name|max
argument_list|)
operator|||
name|isSubsetOf
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|toHexString
argument_list|(
name|min
argument_list|)
operator|+
literal|'-'
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// difficult numbers to hash... only the highest bits will tend to differ.
comment|// ranges will only overlap during a split, so we can just hash the lower range.
return|return
operator|(
name|min
operator|>>
literal|28
operator|)
operator|+
operator|(
name|min
operator|>>
literal|25
operator|)
operator|+
operator|(
name|min
operator|>>
literal|21
operator|)
operator|+
name|min
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Range
name|other
init|=
operator|(
name|Range
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|min
operator|==
name|other
operator|.
name|min
operator|&&
name|this
operator|.
name|max
operator|==
name|other
operator|.
name|max
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|JSONWriter
name|writer
parameter_list|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fromString
specifier|public
name|Range
name|fromString
parameter_list|(
name|String
name|range
parameter_list|)
block|{
name|int
name|middle
init|=
name|range
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
decl_stmt|;
name|String
name|minS
init|=
name|range
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|middle
argument_list|)
decl_stmt|;
name|String
name|maxS
init|=
name|range
operator|.
name|substring
argument_list|(
name|middle
operator|+
literal|1
argument_list|)
decl_stmt|;
name|long
name|min
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|minS
argument_list|,
literal|16
argument_list|)
decl_stmt|;
comment|// use long to prevent the parsing routines from potentially worrying about overflow
name|long
name|max
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|maxS
argument_list|,
literal|16
argument_list|)
decl_stmt|;
return|return
operator|new
name|Range
argument_list|(
operator|(
name|int
operator|)
name|min
argument_list|,
operator|(
name|int
operator|)
name|max
argument_list|)
return|;
block|}
DECL|method|fullRange
specifier|public
name|Range
name|fullRange
parameter_list|()
block|{
return|return
operator|new
name|Range
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
comment|/**    * Returns the range for each partition    */
DECL|method|partitionRange
specifier|public
name|List
argument_list|<
name|Range
argument_list|>
name|partitionRange
parameter_list|(
name|int
name|partitions
parameter_list|,
name|Range
name|range
parameter_list|)
block|{
name|int
name|min
init|=
name|range
operator|.
name|min
decl_stmt|;
name|int
name|max
init|=
name|range
operator|.
name|max
decl_stmt|;
assert|assert
name|max
operator|>=
name|min
assert|;
if|if
condition|(
name|partitions
operator|==
literal|0
condition|)
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
name|long
name|rangeSize
init|=
operator|(
name|long
operator|)
name|max
operator|-
operator|(
name|long
operator|)
name|min
decl_stmt|;
name|long
name|rangeStep
init|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|rangeSize
operator|/
name|partitions
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Range
argument_list|>
name|ranges
init|=
operator|new
name|ArrayList
argument_list|<
name|Range
argument_list|>
argument_list|(
name|partitions
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|min
decl_stmt|;
name|long
name|end
init|=
name|start
decl_stmt|;
while|while
condition|(
name|end
operator|<
name|max
condition|)
block|{
name|end
operator|=
name|start
operator|+
name|rangeStep
expr_stmt|;
comment|// make last range always end exactly on MAX_VALUE
if|if
condition|(
name|ranges
operator|.
name|size
argument_list|()
operator|==
name|partitions
operator|-
literal|1
condition|)
block|{
name|end
operator|=
name|max
expr_stmt|;
block|}
name|ranges
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
operator|(
name|int
operator|)
name|start
argument_list|,
operator|(
name|int
operator|)
name|end
argument_list|)
argument_list|)
expr_stmt|;
name|start
operator|=
name|end
operator|+
literal|1L
expr_stmt|;
block|}
return|return
name|ranges
return|;
block|}
comment|/** Returns the Slice that the document should reside on, or null if there is not enough information */
DECL|method|getTargetSlice
specifier|public
specifier|abstract
name|Slice
name|getTargetSlice
parameter_list|(
name|String
name|id
parameter_list|,
name|SolrInputDocument
name|sdoc
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
function_decl|;
comment|/** This method is consulted to determine what slices should be queried for a request when    *  an explicit shards parameter was not used.    *  This method only accepts a single shard key (or null).  If you have a comma separated list of shard keys,    *  call getSearchSlices    **/
DECL|method|getSearchSlicesSingle
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|Slice
argument_list|>
name|getSearchSlicesSingle
parameter_list|(
name|String
name|shardKey
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
function_decl|;
DECL|method|isTargetSlice
specifier|public
specifier|abstract
name|boolean
name|isTargetSlice
parameter_list|(
name|String
name|id
parameter_list|,
name|SolrInputDocument
name|sdoc
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|String
name|shardId
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
function_decl|;
comment|/** This method is consulted to determine what slices should be queried for a request when    *  an explicit shards parameter was not used.    *  This method accepts a multi-valued shardKeys parameter (normally comma separated from the shard.keys request parameter)    *  and aggregates the slices returned by getSearchSlicesSingle for each shardKey.    **/
DECL|method|getSearchSlices
specifier|public
name|Collection
argument_list|<
name|Slice
argument_list|>
name|getSearchSlices
parameter_list|(
name|String
name|shardKeys
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
block|{
if|if
condition|(
name|shardKeys
operator|==
literal|null
operator|||
name|shardKeys
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|getSearchSlicesSingle
argument_list|(
name|shardKeys
argument_list|,
name|params
argument_list|,
name|collection
argument_list|)
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|shardKeyList
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|shardKeys
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|Slice
argument_list|>
name|allSlices
init|=
operator|new
name|HashSet
argument_list|<
name|Slice
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|shardKey
range|:
name|shardKeyList
control|)
block|{
name|allSlices
operator|.
name|addAll
argument_list|(
name|getSearchSlicesSingle
argument_list|(
name|shardKey
argument_list|,
name|params
argument_list|,
name|collection
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|allSlices
return|;
block|}
block|}
end_class
end_unit
