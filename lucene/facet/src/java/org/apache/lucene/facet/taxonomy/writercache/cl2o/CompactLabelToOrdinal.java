begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.writercache.cl2o
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|writercache
operator|.
name|cl2o
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import
begin_comment
comment|/**  * This is a very efficient LabelToOrdinal implementation that uses a  * CharBlockArray to store all labels and a configurable number of HashArrays to  * reference the labels.  *<p>  * Since the HashArrays don't handle collisions, a {@link CollisionMap} is used  * to store the colliding labels.  *<p>  * This data structure grows by adding a new HashArray whenever the number of  * collisions in the {@link CollisionMap} exceeds {@code loadFactor} *   * {@link #getMaxOrdinal()}. Growing also includes reinserting all colliding  * labels into the HashArrays to possibly reduce the number of collisions.  *   * For setting the {@code loadFactor} see   * {@link #CompactLabelToOrdinal(int, float, int)}.   *   *<p>  * This data structure has a much lower memory footprint (~30%) compared to a  * Java HashMap&lt;String, Integer&gt;. It also only uses a small fraction of objects  * a HashMap would use, thus limiting the GC overhead. Ingestion speed was also  * ~50% faster compared to a HashMap for 3M unique labels.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|CompactLabelToOrdinal
specifier|public
class|class
name|CompactLabelToOrdinal
extends|extends
name|LabelToOrdinal
block|{
DECL|field|DefaultLoadFactor
specifier|public
specifier|static
specifier|final
name|float
name|DefaultLoadFactor
init|=
literal|0.15f
decl_stmt|;
DECL|field|TERMINATOR_CHAR
specifier|static
specifier|final
name|char
name|TERMINATOR_CHAR
init|=
literal|0xffff
decl_stmt|;
DECL|field|COLLISION
specifier|private
specifier|static
specifier|final
name|int
name|COLLISION
init|=
operator|-
literal|5
decl_stmt|;
DECL|field|hashArrays
specifier|private
name|HashArray
index|[]
name|hashArrays
decl_stmt|;
DECL|field|collisionMap
specifier|private
name|CollisionMap
name|collisionMap
decl_stmt|;
DECL|field|labelRepository
specifier|private
name|CharBlockArray
name|labelRepository
decl_stmt|;
DECL|field|capacity
specifier|private
name|int
name|capacity
decl_stmt|;
DECL|field|threshold
specifier|private
name|int
name|threshold
decl_stmt|;
DECL|field|loadFactor
specifier|private
name|float
name|loadFactor
decl_stmt|;
DECL|method|sizeOfMap
specifier|public
name|int
name|sizeOfMap
parameter_list|()
block|{
return|return
name|this
operator|.
name|collisionMap
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|CompactLabelToOrdinal
specifier|private
name|CompactLabelToOrdinal
parameter_list|()
block|{   }
DECL|method|CompactLabelToOrdinal
specifier|public
name|CompactLabelToOrdinal
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|float
name|loadFactor
parameter_list|,
name|int
name|numHashArrays
parameter_list|)
block|{
name|this
operator|.
name|hashArrays
operator|=
operator|new
name|HashArray
index|[
name|numHashArrays
index|]
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|determineCapacity
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|numHashArrays
argument_list|)
argument_list|,
name|initialCapacity
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
name|this
operator|.
name|collisionMap
operator|=
operator|new
name|CollisionMap
argument_list|(
name|this
operator|.
name|labelRepository
argument_list|)
expr_stmt|;
name|this
operator|.
name|counter
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|loadFactor
operator|=
name|loadFactor
expr_stmt|;
name|this
operator|.
name|threshold
operator|=
call|(
name|int
call|)
argument_list|(
name|this
operator|.
name|loadFactor
operator|*
name|this
operator|.
name|capacity
argument_list|)
expr_stmt|;
block|}
DECL|method|determineCapacity
specifier|static
name|int
name|determineCapacity
parameter_list|(
name|int
name|minCapacity
parameter_list|,
name|int
name|initialCapacity
parameter_list|)
block|{
name|int
name|capacity
init|=
name|minCapacity
decl_stmt|;
while|while
condition|(
name|capacity
operator|<
name|initialCapacity
condition|)
block|{
name|capacity
operator|<<=
literal|1
expr_stmt|;
block|}
return|return
name|capacity
return|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|()
block|{
name|labelRepository
operator|=
operator|new
name|CharBlockArray
argument_list|()
expr_stmt|;
name|CategoryPathUtils
operator|.
name|serialize
argument_list|(
name|CategoryPath
operator|.
name|EMPTY
argument_list|,
name|labelRepository
argument_list|)
expr_stmt|;
name|int
name|c
init|=
name|this
operator|.
name|capacity
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
name|this
operator|.
name|hashArrays
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|hashArrays
index|[
name|i
index|]
operator|=
operator|new
name|HashArray
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|c
operator|/=
literal|2
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addLabel
specifier|public
name|void
name|addLabel
parameter_list|(
name|CategoryPath
name|label
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
if|if
condition|(
name|collisionMap
operator|.
name|size
argument_list|()
operator|>
name|threshold
condition|)
block|{
name|grow
argument_list|()
expr_stmt|;
block|}
name|int
name|hash
init|=
name|CompactLabelToOrdinal
operator|.
name|stringHashCode
argument_list|(
name|label
argument_list|)
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
name|this
operator|.
name|hashArrays
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|addLabel
argument_list|(
name|this
operator|.
name|hashArrays
index|[
name|i
index|]
argument_list|,
name|label
argument_list|,
name|hash
argument_list|,
name|ordinal
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|int
name|prevVal
init|=
name|collisionMap
operator|.
name|addLabel
argument_list|(
name|label
argument_list|,
name|hash
argument_list|,
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|prevVal
operator|!=
name|ordinal
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Label already exists: "
operator|+
name|label
operator|.
name|toString
argument_list|(
literal|'/'
argument_list|)
operator|+
literal|" prev ordinal "
operator|+
name|prevVal
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getOrdinal
specifier|public
name|int
name|getOrdinal
parameter_list|(
name|CategoryPath
name|label
parameter_list|)
block|{
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
return|return
name|LabelToOrdinal
operator|.
name|INVALID_ORDINAL
return|;
block|}
name|int
name|hash
init|=
name|CompactLabelToOrdinal
operator|.
name|stringHashCode
argument_list|(
name|label
argument_list|)
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
name|this
operator|.
name|hashArrays
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ord
init|=
name|getOrdinal
argument_list|(
name|this
operator|.
name|hashArrays
index|[
name|i
index|]
argument_list|,
name|label
argument_list|,
name|hash
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|!=
name|COLLISION
condition|)
block|{
return|return
name|ord
return|;
block|}
block|}
return|return
name|this
operator|.
name|collisionMap
operator|.
name|get
argument_list|(
name|label
argument_list|,
name|hash
argument_list|)
return|;
block|}
DECL|method|grow
specifier|private
name|void
name|grow
parameter_list|()
block|{
name|HashArray
name|temp
init|=
name|this
operator|.
name|hashArrays
index|[
name|this
operator|.
name|hashArrays
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|this
operator|.
name|hashArrays
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|this
operator|.
name|hashArrays
index|[
name|i
index|]
operator|=
name|this
operator|.
name|hashArrays
index|[
name|i
operator|-
literal|1
index|]
expr_stmt|;
block|}
name|this
operator|.
name|capacity
operator|*=
literal|2
expr_stmt|;
name|this
operator|.
name|hashArrays
index|[
literal|0
index|]
operator|=
operator|new
name|HashArray
argument_list|(
name|this
operator|.
name|capacity
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|this
operator|.
name|hashArrays
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
index|[]
name|sourceOffsetArray
init|=
name|this
operator|.
name|hashArrays
index|[
name|i
index|]
operator|.
name|offsets
decl_stmt|;
name|int
index|[]
name|sourceCidsArray
init|=
name|this
operator|.
name|hashArrays
index|[
name|i
index|]
operator|.
name|cids
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|sourceOffsetArray
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|i
operator|&&
name|sourceOffsetArray
index|[
name|k
index|]
operator|!=
literal|0
condition|;
name|j
operator|++
control|)
block|{
name|int
index|[]
name|targetOffsetArray
init|=
name|this
operator|.
name|hashArrays
index|[
name|j
index|]
operator|.
name|offsets
decl_stmt|;
name|int
index|[]
name|targetCidsArray
init|=
name|this
operator|.
name|hashArrays
index|[
name|j
index|]
operator|.
name|cids
decl_stmt|;
name|int
name|newIndex
init|=
name|indexFor
argument_list|(
name|stringHashCode
argument_list|(
name|this
operator|.
name|labelRepository
argument_list|,
name|sourceOffsetArray
index|[
name|k
index|]
argument_list|)
argument_list|,
name|targetOffsetArray
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetOffsetArray
index|[
name|newIndex
index|]
operator|==
literal|0
condition|)
block|{
name|targetOffsetArray
index|[
name|newIndex
index|]
operator|=
name|sourceOffsetArray
index|[
name|k
index|]
expr_stmt|;
name|targetCidsArray
index|[
name|newIndex
index|]
operator|=
name|sourceCidsArray
index|[
name|k
index|]
expr_stmt|;
name|sourceOffsetArray
index|[
name|k
index|]
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
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
name|temp
operator|.
name|offsets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|offset
init|=
name|temp
operator|.
name|offsets
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|offset
operator|>
literal|0
condition|)
block|{
name|int
name|hash
init|=
name|stringHashCode
argument_list|(
name|this
operator|.
name|labelRepository
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|addLabelOffset
argument_list|(
name|hash
argument_list|,
name|temp
operator|.
name|cids
index|[
name|i
index|]
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
name|CollisionMap
name|oldCollisionMap
init|=
name|this
operator|.
name|collisionMap
decl_stmt|;
name|this
operator|.
name|collisionMap
operator|=
operator|new
name|CollisionMap
argument_list|(
name|oldCollisionMap
operator|.
name|capacity
argument_list|()
argument_list|,
name|this
operator|.
name|labelRepository
argument_list|)
expr_stmt|;
name|this
operator|.
name|threshold
operator|=
call|(
name|int
call|)
argument_list|(
name|this
operator|.
name|capacity
operator|*
name|this
operator|.
name|loadFactor
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|CollisionMap
operator|.
name|Entry
argument_list|>
name|it
init|=
name|oldCollisionMap
operator|.
name|entryIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|CollisionMap
operator|.
name|Entry
name|e
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|addLabelOffset
argument_list|(
name|stringHashCode
argument_list|(
name|this
operator|.
name|labelRepository
argument_list|,
name|e
operator|.
name|offset
argument_list|)
argument_list|,
name|e
operator|.
name|cid
argument_list|,
name|e
operator|.
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addLabel
specifier|private
name|boolean
name|addLabel
parameter_list|(
name|HashArray
name|a
parameter_list|,
name|CategoryPath
name|label
parameter_list|,
name|int
name|hash
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
name|int
name|index
init|=
name|CompactLabelToOrdinal
operator|.
name|indexFor
argument_list|(
name|hash
argument_list|,
name|a
operator|.
name|offsets
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|a
operator|.
name|offsets
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
name|offset
operator|==
literal|0
condition|)
block|{
name|a
operator|.
name|offsets
index|[
name|index
index|]
operator|=
name|this
operator|.
name|labelRepository
operator|.
name|length
argument_list|()
expr_stmt|;
name|CategoryPathUtils
operator|.
name|serialize
argument_list|(
name|label
argument_list|,
name|labelRepository
argument_list|)
expr_stmt|;
name|a
operator|.
name|cids
index|[
name|index
index|]
operator|=
name|ordinal
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|addLabelOffset
specifier|private
name|void
name|addLabelOffset
parameter_list|(
name|int
name|hash
parameter_list|,
name|int
name|cid
parameter_list|,
name|int
name|knownOffset
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|this
operator|.
name|hashArrays
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|addLabelOffsetToHashArray
argument_list|(
name|this
operator|.
name|hashArrays
index|[
name|i
index|]
argument_list|,
name|hash
argument_list|,
name|cid
argument_list|,
name|knownOffset
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|this
operator|.
name|collisionMap
operator|.
name|addLabelOffset
argument_list|(
name|hash
argument_list|,
name|knownOffset
argument_list|,
name|cid
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|collisionMap
operator|.
name|size
argument_list|()
operator|>
name|this
operator|.
name|threshold
condition|)
block|{
name|grow
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addLabelOffsetToHashArray
specifier|private
name|boolean
name|addLabelOffsetToHashArray
parameter_list|(
name|HashArray
name|a
parameter_list|,
name|int
name|hash
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|int
name|knownOffset
parameter_list|)
block|{
name|int
name|index
init|=
name|CompactLabelToOrdinal
operator|.
name|indexFor
argument_list|(
name|hash
argument_list|,
name|a
operator|.
name|offsets
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|a
operator|.
name|offsets
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
name|offset
operator|==
literal|0
condition|)
block|{
name|a
operator|.
name|offsets
index|[
name|index
index|]
operator|=
name|knownOffset
expr_stmt|;
name|a
operator|.
name|cids
index|[
name|index
index|]
operator|=
name|ordinal
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|getOrdinal
specifier|private
name|int
name|getOrdinal
parameter_list|(
name|HashArray
name|a
parameter_list|,
name|CategoryPath
name|label
parameter_list|,
name|int
name|hash
parameter_list|)
block|{
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
return|return
name|LabelToOrdinal
operator|.
name|INVALID_ORDINAL
return|;
block|}
name|int
name|index
init|=
name|indexFor
argument_list|(
name|hash
argument_list|,
name|a
operator|.
name|offsets
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|a
operator|.
name|offsets
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
name|offset
operator|==
literal|0
condition|)
block|{
return|return
name|LabelToOrdinal
operator|.
name|INVALID_ORDINAL
return|;
block|}
if|if
condition|(
name|CategoryPathUtils
operator|.
name|equalsToSerialized
argument_list|(
name|label
argument_list|,
name|labelRepository
argument_list|,
name|offset
argument_list|)
condition|)
block|{
return|return
name|a
operator|.
name|cids
index|[
name|index
index|]
return|;
block|}
return|return
name|COLLISION
return|;
block|}
comment|/** Returns index for hash code h. */
DECL|method|indexFor
specifier|static
name|int
name|indexFor
parameter_list|(
name|int
name|h
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
name|h
operator|&
operator|(
name|length
operator|-
literal|1
operator|)
return|;
block|}
comment|// static int stringHashCode(String label) {
comment|// int len = label.length();
comment|// int hash = 0;
comment|// int i;
comment|// for (i = 0; i< len; ++i)
comment|// hash = 33 * hash + label.charAt(i);
comment|//
comment|// hash = hash ^ ((hash>>> 20) ^ (hash>>> 12));
comment|// hash = hash ^ (hash>>> 7) ^ (hash>>> 4);
comment|//
comment|// return hash;
comment|//
comment|// }
DECL|method|stringHashCode
specifier|static
name|int
name|stringHashCode
parameter_list|(
name|CategoryPath
name|label
parameter_list|)
block|{
name|int
name|hash
init|=
name|label
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|hash
operator|=
name|hash
operator|^
operator|(
operator|(
name|hash
operator|>>>
literal|20
operator|)
operator|^
operator|(
name|hash
operator|>>>
literal|12
operator|)
operator|)
expr_stmt|;
name|hash
operator|=
name|hash
operator|^
operator|(
name|hash
operator|>>>
literal|7
operator|)
operator|^
operator|(
name|hash
operator|>>>
literal|4
operator|)
expr_stmt|;
return|return
name|hash
return|;
block|}
DECL|method|stringHashCode
specifier|static
name|int
name|stringHashCode
parameter_list|(
name|CharBlockArray
name|labelRepository
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|int
name|hash
init|=
name|CategoryPathUtils
operator|.
name|hashCodeOfSerialized
argument_list|(
name|labelRepository
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|hash
operator|=
name|hash
operator|^
operator|(
operator|(
name|hash
operator|>>>
literal|20
operator|)
operator|^
operator|(
name|hash
operator|>>>
literal|12
operator|)
operator|)
expr_stmt|;
name|hash
operator|=
name|hash
operator|^
operator|(
name|hash
operator|>>>
literal|7
operator|)
operator|^
operator|(
name|hash
operator|>>>
literal|4
operator|)
expr_stmt|;
return|return
name|hash
return|;
block|}
comment|// public static boolean equals(CharSequence label, CharBlockArray array,
comment|// int offset) {
comment|// // CONTINUE HERE
comment|// int len = label.length();
comment|// int bi = array.blockIndex(offset);
comment|// CharBlockArray.Block b = array.blocks.get(bi);
comment|// int index = array.indexInBlock(offset);
comment|//
comment|// for (int i = 0; i< len; i++) {
comment|// if (label.charAt(i) != b.chars[index]) {
comment|// return false;
comment|// }
comment|// index++;
comment|// if (index == b.length) {
comment|// b = array.blocks.get(++bi);
comment|// index = 0;
comment|// }
comment|// }
comment|//
comment|// return b.chars[index] == TerminatorChar;
comment|// }
comment|/**    * Returns an estimate of the amount of memory used by this table. Called only in    * this package. Memory is consumed mainly by three structures: the hash arrays,    * label repository and collision map.    */
DECL|method|getMemoryUsage
name|int
name|getMemoryUsage
parameter_list|()
block|{
name|int
name|memoryUsage
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|hashArrays
operator|!=
literal|null
condition|)
block|{
comment|// HashArray capacity is instance-specific.
for|for
control|(
name|HashArray
name|ha
range|:
name|this
operator|.
name|hashArrays
control|)
block|{
comment|// Each has 2 capacity-length arrays of ints.
name|memoryUsage
operator|+=
operator|(
name|ha
operator|.
name|capacity
operator|*
literal|2
operator|*
literal|4
operator|)
operator|+
literal|4
expr_stmt|;
block|}
block|}
if|if
condition|(
name|this
operator|.
name|labelRepository
operator|!=
literal|null
condition|)
block|{
comment|// All blocks are the same size.
name|int
name|blockSize
init|=
name|this
operator|.
name|labelRepository
operator|.
name|blockSize
decl_stmt|;
comment|// Each block has room for blockSize UTF-16 chars.
name|int
name|actualBlockSize
init|=
operator|(
name|blockSize
operator|*
literal|2
operator|)
operator|+
literal|4
decl_stmt|;
name|memoryUsage
operator|+=
name|this
operator|.
name|labelRepository
operator|.
name|blocks
operator|.
name|size
argument_list|()
operator|*
name|actualBlockSize
expr_stmt|;
name|memoryUsage
operator|+=
literal|8
expr_stmt|;
comment|// Two int values for array as a whole.
block|}
if|if
condition|(
name|this
operator|.
name|collisionMap
operator|!=
literal|null
condition|)
block|{
name|memoryUsage
operator|+=
name|this
operator|.
name|collisionMap
operator|.
name|getMemoryUsage
argument_list|()
expr_stmt|;
block|}
return|return
name|memoryUsage
return|;
block|}
comment|/**    * Opens the file and reloads the CompactLabelToOrdinal. The file it expects    * is generated from the {@link #flush(File)} command.    */
DECL|method|open
specifier|static
name|CompactLabelToOrdinal
name|open
parameter_list|(
name|File
name|file
parameter_list|,
name|float
name|loadFactor
parameter_list|,
name|int
name|numHashArrays
parameter_list|)
throws|throws
name|IOException
block|{
comment|/**      * Part of the file is the labelRepository, which needs to be rehashed      * and label offsets re-added to the object. I am unsure as to why we      * can't just store these off in the file as well, but in keeping with      * the spirit of the original code, I did it this way. (ssuppe)      */
name|CompactLabelToOrdinal
name|l2o
init|=
operator|new
name|CompactLabelToOrdinal
argument_list|()
decl_stmt|;
name|l2o
operator|.
name|loadFactor
operator|=
name|loadFactor
expr_stmt|;
name|l2o
operator|.
name|hashArrays
operator|=
operator|new
name|HashArray
index|[
name|numHashArrays
index|]
expr_stmt|;
name|DataInputStream
name|dis
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dis
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// TaxiReader needs to load the "counter" or occupancy (L2O) to know
comment|// the next unique facet. we used to load the delimiter too, but
comment|// never used it.
name|l2o
operator|.
name|counter
operator|=
name|dis
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|l2o
operator|.
name|capacity
operator|=
name|determineCapacity
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|l2o
operator|.
name|hashArrays
operator|.
name|length
argument_list|)
argument_list|,
name|l2o
operator|.
name|counter
argument_list|)
expr_stmt|;
name|l2o
operator|.
name|init
argument_list|()
expr_stmt|;
comment|// now read the chars
name|l2o
operator|.
name|labelRepository
operator|=
name|CharBlockArray
operator|.
name|open
argument_list|(
name|dis
argument_list|)
expr_stmt|;
name|l2o
operator|.
name|collisionMap
operator|=
operator|new
name|CollisionMap
argument_list|(
name|l2o
operator|.
name|labelRepository
argument_list|)
expr_stmt|;
comment|// Calculate hash on the fly based on how CategoryPath hashes
comment|// itself. Maybe in the future we can call some static based methods
comment|// in CategoryPath so that this doesn't break again? I don't like
comment|// having code in two different places...
name|int
name|cid
init|=
literal|0
decl_stmt|;
comment|// Skip the initial offset, it's the CategoryPath(0,0), which isn't
comment|// a hashed value.
name|int
name|offset
init|=
literal|1
decl_stmt|;
name|int
name|lastStartOffset
init|=
name|offset
decl_stmt|;
comment|// This loop really relies on a well-formed input (assumes pretty blindly
comment|// that array offsets will work).  Since the initial file is machine
comment|// generated, I think this should be OK.
while|while
condition|(
name|offset
operator|<
name|l2o
operator|.
name|labelRepository
operator|.
name|length
argument_list|()
condition|)
block|{
comment|// identical code to CategoryPath.hashFromSerialized. since we need to
comment|// advance offset, we cannot call the method directly. perhaps if we
comment|// could pass a mutable Integer or something...
name|int
name|length
init|=
operator|(
name|short
operator|)
name|l2o
operator|.
name|labelRepository
operator|.
name|charAt
argument_list|(
name|offset
operator|++
argument_list|)
decl_stmt|;
name|int
name|hash
init|=
name|length
decl_stmt|;
if|if
condition|(
name|length
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|len
init|=
operator|(
name|short
operator|)
name|l2o
operator|.
name|labelRepository
operator|.
name|charAt
argument_list|(
name|offset
operator|++
argument_list|)
decl_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
name|l2o
operator|.
name|labelRepository
operator|.
name|subSequence
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|len
argument_list|)
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|offset
operator|+=
name|len
expr_stmt|;
block|}
block|}
comment|// Now that we've hashed the components of the label, do the
comment|// final part of the hash algorithm.
name|hash
operator|=
name|hash
operator|^
operator|(
operator|(
name|hash
operator|>>>
literal|20
operator|)
operator|^
operator|(
name|hash
operator|>>>
literal|12
operator|)
operator|)
expr_stmt|;
name|hash
operator|=
name|hash
operator|^
operator|(
name|hash
operator|>>>
literal|7
operator|)
operator|^
operator|(
name|hash
operator|>>>
literal|4
operator|)
expr_stmt|;
comment|// Add the label, and let's keep going
name|l2o
operator|.
name|addLabelOffset
argument_list|(
name|hash
argument_list|,
name|cid
argument_list|,
name|lastStartOffset
argument_list|)
expr_stmt|;
name|cid
operator|++
expr_stmt|;
name|lastStartOffset
operator|=
name|offset
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid file format. Cannot deserialize."
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|dis
operator|!=
literal|null
condition|)
block|{
name|dis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|l2o
operator|.
name|threshold
operator|=
call|(
name|int
call|)
argument_list|(
name|l2o
operator|.
name|loadFactor
operator|*
name|l2o
operator|.
name|capacity
argument_list|)
expr_stmt|;
return|return
name|l2o
return|;
block|}
DECL|method|flush
name|void
name|flush
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|BufferedOutputStream
name|os
init|=
operator|new
name|BufferedOutputStream
argument_list|(
name|fos
argument_list|)
decl_stmt|;
name|DataOutputStream
name|dos
init|=
operator|new
name|DataOutputStream
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|dos
operator|.
name|writeInt
argument_list|(
name|this
operator|.
name|counter
argument_list|)
expr_stmt|;
comment|// write the labelRepository
name|this
operator|.
name|labelRepository
operator|.
name|flush
argument_list|(
name|dos
argument_list|)
expr_stmt|;
comment|// Closes the data output stream
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|HashArray
specifier|private
specifier|static
specifier|final
class|class
name|HashArray
block|{
DECL|field|offsets
name|int
index|[]
name|offsets
decl_stmt|;
DECL|field|cids
name|int
index|[]
name|cids
decl_stmt|;
DECL|field|capacity
name|int
name|capacity
decl_stmt|;
DECL|method|HashArray
name|HashArray
parameter_list|(
name|int
name|c
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
name|c
expr_stmt|;
name|this
operator|.
name|offsets
operator|=
operator|new
name|int
index|[
name|this
operator|.
name|capacity
index|]
expr_stmt|;
name|this
operator|.
name|cids
operator|=
operator|new
name|int
index|[
name|this
operator|.
name|capacity
index|]
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
