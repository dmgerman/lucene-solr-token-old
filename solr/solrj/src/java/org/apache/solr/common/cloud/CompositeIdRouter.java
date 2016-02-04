begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Hash
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
name|List
import|;
end_import
begin_comment
comment|//
end_comment
begin_comment
comment|// user!uniqueid
end_comment
begin_comment
comment|// app!user!uniqueid
end_comment
begin_comment
comment|// user/4!uniqueid
end_comment
begin_comment
comment|// app/2!user/4!uniqueid
end_comment
begin_comment
comment|//
end_comment
begin_class
DECL|class|CompositeIdRouter
specifier|public
class|class
name|CompositeIdRouter
extends|extends
name|HashBasedRouter
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"compositeId"
decl_stmt|;
DECL|field|SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|SEPARATOR
init|=
literal|"!"
decl_stmt|;
comment|// separator used to optionally specify number of bits to allocate toward first part.
DECL|field|bitsSeparator
specifier|public
specifier|static
specifier|final
name|int
name|bitsSeparator
init|=
literal|'/'
decl_stmt|;
DECL|field|bits
specifier|private
name|int
name|bits
init|=
literal|16
decl_stmt|;
annotation|@
name|Override
DECL|method|sliceHash
specifier|public
name|int
name|sliceHash
parameter_list|(
name|String
name|id
parameter_list|,
name|SolrInputDocument
name|doc
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
block|{
name|String
name|shardFieldName
init|=
name|getRouteField
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardFieldName
operator|!=
literal|null
operator|&&
name|doc
operator|!=
literal|null
condition|)
block|{
name|Object
name|o
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
name|shardFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"No value for :"
operator|+
name|shardFieldName
operator|+
literal|". Unable to identify shard"
argument_list|)
throw|;
name|id
operator|=
name|o
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|id
operator|.
name|indexOf
argument_list|(
name|SEPARATOR
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|id
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
return|;
block|}
return|return
operator|new
name|KeyParser
argument_list|(
name|id
argument_list|)
operator|.
name|getHash
argument_list|()
return|;
block|}
comment|/**    * Get Range for a given CompositeId based route key    *    * @param routeKey to return Range for    * @return Range for given routeKey    */
DECL|method|keyHashRange
specifier|public
name|Range
name|keyHashRange
parameter_list|(
name|String
name|routeKey
parameter_list|)
block|{
if|if
condition|(
name|routeKey
operator|.
name|indexOf
argument_list|(
name|SEPARATOR
argument_list|)
operator|<
literal|0
condition|)
block|{
name|int
name|hash
init|=
name|sliceHash
argument_list|(
name|routeKey
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|Range
argument_list|(
name|hash
argument_list|,
name|hash
argument_list|)
return|;
block|}
return|return
operator|new
name|KeyParser
argument_list|(
name|routeKey
argument_list|)
operator|.
name|getRange
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSearchSlicesSingle
specifier|public
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
block|{
if|if
condition|(
name|shardKey
operator|==
literal|null
condition|)
block|{
comment|// search across whole collection
comment|// TODO: this may need modification in the future when shard splitting could cause an overlap
return|return
name|collection
operator|.
name|getActiveSlices
argument_list|()
return|;
block|}
name|String
name|id
init|=
name|shardKey
decl_stmt|;
if|if
condition|(
name|shardKey
operator|.
name|indexOf
argument_list|(
name|SEPARATOR
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// shardKey is a simple id, so don't do a range
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|hashToSlice
argument_list|(
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|id
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
name|collection
argument_list|)
argument_list|)
return|;
block|}
name|Range
name|completeRange
init|=
operator|new
name|KeyParser
argument_list|(
name|id
argument_list|)
operator|.
name|getRange
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Slice
argument_list|>
name|targetSlices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|collection
operator|.
name|getActiveSlices
argument_list|()
control|)
block|{
name|Range
name|range
init|=
name|slice
operator|.
name|getRange
argument_list|()
decl_stmt|;
if|if
condition|(
name|range
operator|!=
literal|null
operator|&&
name|range
operator|.
name|overlaps
argument_list|(
name|completeRange
argument_list|)
condition|)
block|{
name|targetSlices
operator|.
name|add
argument_list|(
name|slice
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|targetSlices
return|;
block|}
DECL|method|partitionRangeByKey
specifier|public
name|List
argument_list|<
name|Range
argument_list|>
name|partitionRangeByKey
parameter_list|(
name|String
name|key
parameter_list|,
name|Range
name|range
parameter_list|)
block|{
name|List
argument_list|<
name|Range
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|Range
name|keyRange
init|=
name|keyHashRange
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|keyRange
operator|.
name|overlaps
argument_list|(
name|range
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Key range does not overlap given range"
argument_list|)
throw|;
block|}
if|if
condition|(
name|keyRange
operator|.
name|equals
argument_list|(
name|range
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|keyRange
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|keyRange
operator|.
name|isSubsetOf
argument_list|(
name|range
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|range
operator|.
name|min
argument_list|,
name|keyRange
operator|.
name|min
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|keyRange
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|(
operator|new
name|Range
argument_list|(
name|keyRange
operator|.
name|max
operator|+
literal|1
argument_list|,
name|range
operator|.
name|max
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|range
operator|.
name|includes
argument_list|(
name|keyRange
operator|.
name|max
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|range
operator|.
name|min
argument_list|,
name|keyRange
operator|.
name|max
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|keyRange
operator|.
name|max
operator|+
literal|1
argument_list|,
name|range
operator|.
name|max
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|range
operator|.
name|min
argument_list|,
name|keyRange
operator|.
name|min
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|keyRange
operator|.
name|min
argument_list|,
name|range
operator|.
name|max
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
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
argument_list|<>
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
comment|// keep track of the idealized target to avoid accumulating rounding errors
name|long
name|targetStart
init|=
name|min
decl_stmt|;
name|long
name|targetEnd
init|=
name|targetStart
decl_stmt|;
comment|// Round to avoid splitting hash domains across ranges if such rounding is not significant.
comment|// With default bits==16, one would need to create more than 4000 shards before this
comment|// becomes false by default.
name|int
name|mask
init|=
literal|0x0000ffff
decl_stmt|;
name|boolean
name|round
init|=
name|rangeStep
operator|>=
operator|(
literal|1
operator|<<
name|bits
operator|)
operator|*
literal|16
decl_stmt|;
while|while
condition|(
name|end
operator|<
name|max
condition|)
block|{
name|targetEnd
operator|=
name|targetStart
operator|+
name|rangeStep
expr_stmt|;
name|end
operator|=
name|targetEnd
expr_stmt|;
if|if
condition|(
name|round
operator|&&
operator|(
operator|(
name|end
operator|&
name|mask
operator|)
operator|!=
name|mask
operator|)
condition|)
block|{
comment|// round up or down?
name|int
name|increment
init|=
literal|1
operator|<<
name|bits
decl_stmt|;
comment|// 0x00010000
name|long
name|roundDown
init|=
operator|(
name|end
operator||
name|mask
operator|)
operator|-
name|increment
decl_stmt|;
name|long
name|roundUp
init|=
operator|(
name|end
operator||
name|mask
operator|)
operator|+
name|increment
decl_stmt|;
if|if
condition|(
name|end
operator|-
name|roundDown
argument_list|<
name|roundUp
operator|-
name|end
operator|&&
name|roundDown
argument_list|>
name|start
condition|)
block|{
name|end
operator|=
name|roundDown
expr_stmt|;
block|}
else|else
block|{
name|end
operator|=
name|roundUp
expr_stmt|;
block|}
block|}
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
name|targetStart
operator|=
name|targetEnd
operator|+
literal|1L
expr_stmt|;
block|}
return|return
name|ranges
return|;
block|}
comment|/**    * Helper class to calculate parts, masks etc for an id.    */
DECL|class|KeyParser
specifier|static
class|class
name|KeyParser
block|{
DECL|field|key
name|String
name|key
decl_stmt|;
DECL|field|numBits
name|int
index|[]
name|numBits
decl_stmt|;
DECL|field|hashes
name|int
index|[]
name|hashes
decl_stmt|;
DECL|field|masks
name|int
index|[]
name|masks
decl_stmt|;
DECL|field|triLevel
name|boolean
name|triLevel
decl_stmt|;
DECL|field|pieces
name|int
name|pieces
decl_stmt|;
DECL|method|KeyParser
specifier|public
name|KeyParser
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|partsList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|int
name|firstSeparatorPos
init|=
name|key
operator|.
name|indexOf
argument_list|(
name|SEPARATOR
argument_list|)
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|==
name|firstSeparatorPos
condition|)
block|{
name|partsList
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|partsList
operator|.
name|add
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|firstSeparatorPos
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|lastPos
init|=
name|key
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
comment|// Don't make any more parts if the first separator is the last char
if|if
condition|(
name|firstSeparatorPos
operator|<
name|lastPos
condition|)
block|{
name|int
name|secondSeparatorPos
init|=
name|key
operator|.
name|indexOf
argument_list|(
name|SEPARATOR
argument_list|,
name|firstSeparatorPos
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|==
name|secondSeparatorPos
condition|)
block|{
name|partsList
operator|.
name|add
argument_list|(
name|key
operator|.
name|substring
argument_list|(
name|firstSeparatorPos
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|secondSeparatorPos
operator|==
name|lastPos
condition|)
block|{
comment|// Don't make any more parts if the key has exactly two separators and
comment|// they're the last two chars - back-compatibility with the behavior of
comment|// String.split() - see SOLR-6257.
if|if
condition|(
name|firstSeparatorPos
operator|<
name|secondSeparatorPos
operator|-
literal|1
condition|)
block|{
name|partsList
operator|.
name|add
argument_list|(
name|key
operator|.
name|substring
argument_list|(
name|firstSeparatorPos
operator|+
literal|1
argument_list|,
name|secondSeparatorPos
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// The second separator is not the last char
name|partsList
operator|.
name|add
argument_list|(
name|key
operator|.
name|substring
argument_list|(
name|firstSeparatorPos
operator|+
literal|1
argument_list|,
name|secondSeparatorPos
argument_list|)
argument_list|)
expr_stmt|;
name|partsList
operator|.
name|add
argument_list|(
name|key
operator|.
name|substring
argument_list|(
name|secondSeparatorPos
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Ignore any further separators beyond the first two
block|}
block|}
name|pieces
operator|=
name|partsList
operator|.
name|size
argument_list|()
expr_stmt|;
name|String
index|[]
name|parts
init|=
name|partsList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|pieces
index|]
argument_list|)
decl_stmt|;
name|numBits
operator|=
operator|new
name|int
index|[
literal|2
index|]
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|endsWith
argument_list|(
literal|"!"
argument_list|)
operator|&&
name|pieces
operator|<
literal|3
condition|)
name|pieces
operator|++
expr_stmt|;
name|hashes
operator|=
operator|new
name|int
index|[
name|pieces
index|]
expr_stmt|;
if|if
condition|(
name|pieces
operator|==
literal|3
condition|)
block|{
name|numBits
index|[
literal|0
index|]
operator|=
literal|8
expr_stmt|;
name|numBits
index|[
literal|1
index|]
operator|=
literal|8
expr_stmt|;
name|triLevel
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|numBits
index|[
literal|0
index|]
operator|=
literal|16
expr_stmt|;
name|triLevel
operator|=
literal|false
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pieces
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|<
name|pieces
operator|-
literal|1
condition|)
block|{
name|int
name|commaIdx
init|=
name|parts
index|[
name|i
index|]
operator|.
name|indexOf
argument_list|(
name|bitsSeparator
argument_list|)
decl_stmt|;
if|if
condition|(
name|commaIdx
operator|>
literal|0
condition|)
block|{
name|numBits
index|[
name|i
index|]
operator|=
name|getNumBits
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|,
name|commaIdx
argument_list|)
expr_stmt|;
name|parts
index|[
name|i
index|]
operator|=
name|parts
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|commaIdx
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Last component of an ID that ends with a '!'
if|if
condition|(
name|i
operator|>=
name|parts
operator|.
name|length
condition|)
name|hashes
index|[
name|i
index|]
operator|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
literal|""
argument_list|,
literal|0
argument_list|,
literal|""
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
else|else
name|hashes
index|[
name|i
index|]
operator|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|parts
index|[
name|i
index|]
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|masks
operator|=
name|getMasks
argument_list|()
expr_stmt|;
block|}
DECL|method|getRange
name|Range
name|getRange
parameter_list|()
block|{
name|int
name|lowerBound
decl_stmt|;
name|int
name|upperBound
decl_stmt|;
if|if
condition|(
name|triLevel
condition|)
block|{
name|lowerBound
operator|=
name|hashes
index|[
literal|0
index|]
operator|&
name|masks
index|[
literal|0
index|]
operator||
name|hashes
index|[
literal|1
index|]
operator|&
name|masks
index|[
literal|1
index|]
expr_stmt|;
name|upperBound
operator|=
name|lowerBound
operator||
name|masks
index|[
literal|2
index|]
expr_stmt|;
block|}
else|else
block|{
name|lowerBound
operator|=
name|hashes
index|[
literal|0
index|]
operator|&
name|masks
index|[
literal|0
index|]
expr_stmt|;
name|upperBound
operator|=
name|lowerBound
operator||
name|masks
index|[
literal|1
index|]
expr_stmt|;
block|}
comment|//  If the upper bits are 0xF0000000, the range we want to cover is
comment|//  0xF0000000 0xFfffffff
if|if
condition|(
operator|(
name|masks
index|[
literal|0
index|]
operator|==
literal|0
operator|&&
operator|!
name|triLevel
operator|)
operator|||
operator|(
name|masks
index|[
literal|0
index|]
operator|==
literal|0
operator|&&
name|masks
index|[
literal|1
index|]
operator|==
literal|0
operator|&&
name|triLevel
operator|)
condition|)
block|{
comment|// no bits used from first part of key.. the code above will produce 0x000000000->0xffffffff
comment|// which only works on unsigned space, but we're using signed space.
name|lowerBound
operator|=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
name|upperBound
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
name|Range
name|r
init|=
operator|new
name|Range
argument_list|(
name|lowerBound
argument_list|,
name|upperBound
argument_list|)
decl_stmt|;
return|return
name|r
return|;
block|}
comment|/**      * Get bit masks for routing based on routing level      */
DECL|method|getMasks
specifier|private
name|int
index|[]
name|getMasks
parameter_list|()
block|{
name|int
index|[]
name|masks
decl_stmt|;
if|if
condition|(
name|triLevel
condition|)
name|masks
operator|=
name|getBitMasks
argument_list|(
name|numBits
index|[
literal|0
index|]
argument_list|,
name|numBits
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
else|else
name|masks
operator|=
name|getBitMasks
argument_list|(
name|numBits
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
name|masks
return|;
block|}
DECL|method|getBitMasks
specifier|private
name|int
index|[]
name|getBitMasks
parameter_list|(
name|int
name|firstBits
parameter_list|,
name|int
name|secondBits
parameter_list|)
block|{
comment|// java can't shift 32 bits
name|int
index|[]
name|masks
init|=
operator|new
name|int
index|[
literal|3
index|]
decl_stmt|;
name|masks
index|[
literal|0
index|]
operator|=
name|firstBits
operator|==
literal|0
condition|?
literal|0
else|:
operator|(
operator|-
literal|1
operator|<<
operator|(
literal|32
operator|-
name|firstBits
operator|)
operator|)
expr_stmt|;
name|masks
index|[
literal|1
index|]
operator|=
operator|(
name|firstBits
operator|+
name|secondBits
operator|)
operator|==
literal|0
condition|?
literal|0
else|:
operator|(
operator|-
literal|1
operator|<<
operator|(
literal|32
operator|-
name|firstBits
operator|-
name|secondBits
operator|)
operator|)
expr_stmt|;
name|masks
index|[
literal|1
index|]
operator|=
name|masks
index|[
literal|0
index|]
operator|^
name|masks
index|[
literal|1
index|]
expr_stmt|;
name|masks
index|[
literal|2
index|]
operator|=
operator|(
name|firstBits
operator|+
name|secondBits
operator|)
operator|==
literal|32
condition|?
literal|0
else|:
operator|~
operator|(
name|masks
index|[
literal|0
index|]
operator||
name|masks
index|[
literal|1
index|]
operator|)
expr_stmt|;
return|return
name|masks
return|;
block|}
DECL|method|getNumBits
specifier|private
name|int
name|getNumBits
parameter_list|(
name|String
name|firstPart
parameter_list|,
name|int
name|commaIdx
parameter_list|)
block|{
name|int
name|v
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|commaIdx
operator|+
literal|1
init|;
name|idx
operator|<
name|firstPart
operator|.
name|length
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
name|char
name|ch
init|=
name|firstPart
operator|.
name|charAt
argument_list|(
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
argument_list|<
literal|'0'
operator|||
name|ch
argument_list|>
literal|'9'
condition|)
return|return
operator|-
literal|1
return|;
name|v
operator|=
name|v
operator|*
literal|10
operator|+
operator|(
name|ch
operator|-
literal|'0'
operator|)
expr_stmt|;
block|}
return|return
name|v
operator|>
literal|32
condition|?
operator|-
literal|1
else|:
name|v
return|;
block|}
DECL|method|getBitMasks
specifier|private
name|int
index|[]
name|getBitMasks
parameter_list|(
name|int
name|firstBits
parameter_list|)
block|{
comment|// java can't shift 32 bits
name|int
index|[]
name|masks
decl_stmt|;
name|masks
operator|=
operator|new
name|int
index|[
literal|2
index|]
expr_stmt|;
name|masks
index|[
literal|0
index|]
operator|=
name|firstBits
operator|==
literal|0
condition|?
literal|0
else|:
operator|(
operator|-
literal|1
operator|<<
operator|(
literal|32
operator|-
name|firstBits
operator|)
operator|)
expr_stmt|;
name|masks
index|[
literal|1
index|]
operator|=
name|firstBits
operator|==
literal|32
condition|?
literal|0
else|:
operator|(
operator|-
literal|1
operator|>>>
name|firstBits
operator|)
expr_stmt|;
return|return
name|masks
return|;
block|}
DECL|method|getHash
name|int
name|getHash
parameter_list|()
block|{
name|int
name|result
init|=
name|hashes
index|[
literal|0
index|]
operator|&
name|masks
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|pieces
condition|;
name|i
operator|++
control|)
name|result
operator|=
name|result
operator||
operator|(
name|hashes
index|[
name|i
index|]
operator|&
name|masks
index|[
name|i
index|]
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class
end_unit
