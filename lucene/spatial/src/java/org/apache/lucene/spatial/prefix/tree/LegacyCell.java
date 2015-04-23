begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.prefix.tree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
operator|.
name|tree
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|SpatialRelation
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
name|BytesRef
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
name|StringHelper
import|;
end_import
begin_comment
comment|/** The base for the original two SPT's: Geohash and Quad. Don't subclass this for new SPTs.  * @lucene.internal */
end_comment
begin_comment
comment|//public for RPT pruneLeafyBranches code
end_comment
begin_class
DECL|class|LegacyCell
specifier|public
specifier|abstract
class|class
name|LegacyCell
implements|implements
name|Cell
block|{
comment|// Important: A LegacyCell doesn't share state for getNextLevelCells(), and
comment|//  LegacySpatialPrefixTree assumes this in its simplify tree logic.
DECL|field|LEAF_BYTE
specifier|private
specifier|static
specifier|final
name|byte
name|LEAF_BYTE
init|=
literal|'+'
decl_stmt|;
comment|//NOTE: must sort before letters& numbers
comment|//Arguably we could simply use a BytesRef, using an extra Object.
DECL|field|bytes
specifier|protected
name|byte
index|[]
name|bytes
decl_stmt|;
comment|//generally bigger to potentially hold a leaf
DECL|field|b_off
specifier|protected
name|int
name|b_off
decl_stmt|;
DECL|field|b_len
specifier|protected
name|int
name|b_len
decl_stmt|;
comment|//doesn't reflect leaf; same as getLevel()
DECL|field|isLeaf
specifier|protected
name|boolean
name|isLeaf
decl_stmt|;
comment|/**    * When set via getSubCells(filter), it is the relationship between this cell    * and the given shape filter. Doesn't participate in shape equality.    */
DECL|field|shapeRel
specifier|protected
name|SpatialRelation
name|shapeRel
decl_stmt|;
DECL|field|shape
specifier|protected
name|Shape
name|shape
decl_stmt|;
comment|//cached
comment|/** Warning: Refers to the same bytes (no copy). If {@link #setLeaf()} is subsequently called then it    * may modify bytes. */
DECL|method|LegacyCell
specifier|protected
name|LegacyCell
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|b_off
operator|=
name|off
expr_stmt|;
name|this
operator|.
name|b_len
operator|=
name|len
expr_stmt|;
name|readLeafAdjust
argument_list|()
expr_stmt|;
block|}
DECL|method|readCell
specifier|protected
name|void
name|readCell
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
name|shapeRel
operator|=
literal|null
expr_stmt|;
name|shape
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
name|bytes
operator|.
name|bytes
expr_stmt|;
name|this
operator|.
name|b_off
operator|=
name|bytes
operator|.
name|offset
expr_stmt|;
name|this
operator|.
name|b_len
operator|=
operator|(
name|short
operator|)
name|bytes
operator|.
name|length
expr_stmt|;
name|readLeafAdjust
argument_list|()
expr_stmt|;
block|}
DECL|method|readLeafAdjust
specifier|protected
name|void
name|readLeafAdjust
parameter_list|()
block|{
name|isLeaf
operator|=
operator|(
name|b_len
operator|>
literal|0
operator|&&
name|bytes
index|[
name|b_off
operator|+
name|b_len
operator|-
literal|1
index|]
operator|==
name|LEAF_BYTE
operator|)
expr_stmt|;
if|if
condition|(
name|isLeaf
condition|)
name|b_len
operator|--
expr_stmt|;
if|if
condition|(
name|getLevel
argument_list|()
operator|==
name|getMaxLevels
argument_list|()
condition|)
name|isLeaf
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getGrid
specifier|protected
specifier|abstract
name|SpatialPrefixTree
name|getGrid
parameter_list|()
function_decl|;
DECL|method|getMaxLevels
specifier|protected
specifier|abstract
name|int
name|getMaxLevels
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|getShapeRel
specifier|public
name|SpatialRelation
name|getShapeRel
parameter_list|()
block|{
return|return
name|shapeRel
return|;
block|}
annotation|@
name|Override
DECL|method|setShapeRel
specifier|public
name|void
name|setShapeRel
parameter_list|(
name|SpatialRelation
name|rel
parameter_list|)
block|{
name|this
operator|.
name|shapeRel
operator|=
name|rel
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isLeaf
specifier|public
name|boolean
name|isLeaf
parameter_list|()
block|{
return|return
name|isLeaf
return|;
block|}
annotation|@
name|Override
DECL|method|setLeaf
specifier|public
name|void
name|setLeaf
parameter_list|()
block|{
name|isLeaf
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTokenBytesWithLeaf
specifier|public
name|BytesRef
name|getTokenBytesWithLeaf
parameter_list|(
name|BytesRef
name|result
parameter_list|)
block|{
name|result
operator|=
name|getTokenBytesNoLeaf
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isLeaf
operator|||
name|getLevel
argument_list|()
operator|==
name|getMaxLevels
argument_list|()
condition|)
return|return
name|result
return|;
if|if
condition|(
name|result
operator|.
name|bytes
operator|.
name|length
operator|<
name|result
operator|.
name|offset
operator|+
name|result
operator|.
name|length
operator|+
literal|1
condition|)
block|{
assert|assert
literal|false
operator|:
literal|"Not supposed to happen; performance bug"
assert|;
name|byte
index|[]
name|copy
init|=
operator|new
name|byte
index|[
name|result
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|result
operator|.
name|bytes
argument_list|,
name|result
operator|.
name|offset
argument_list|,
name|copy
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
name|result
operator|.
name|bytes
operator|=
name|copy
expr_stmt|;
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
block|}
name|result
operator|.
name|bytes
index|[
name|result
operator|.
name|offset
operator|+
name|result
operator|.
name|length
operator|++
index|]
operator|=
name|LEAF_BYTE
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getTokenBytesNoLeaf
specifier|public
name|BytesRef
name|getTokenBytesNoLeaf
parameter_list|(
name|BytesRef
name|result
parameter_list|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
return|return
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|,
name|b_off
argument_list|,
name|b_len
argument_list|)
return|;
name|result
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|result
operator|.
name|offset
operator|=
name|b_off
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|b_len
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getLevel
specifier|public
name|int
name|getLevel
parameter_list|()
block|{
return|return
name|b_len
return|;
block|}
annotation|@
name|Override
DECL|method|getNextLevelCells
specifier|public
name|CellIterator
name|getNextLevelCells
parameter_list|(
name|Shape
name|shapeFilter
parameter_list|)
block|{
assert|assert
name|getLevel
argument_list|()
operator|<
name|getGrid
argument_list|()
operator|.
name|getMaxLevels
argument_list|()
assert|;
if|if
condition|(
name|shapeFilter
operator|instanceof
name|Point
condition|)
block|{
name|LegacyCell
name|cell
init|=
name|getSubCell
argument_list|(
operator|(
name|Point
operator|)
name|shapeFilter
argument_list|)
decl_stmt|;
name|cell
operator|.
name|shapeRel
operator|=
name|SpatialRelation
operator|.
name|CONTAINS
expr_stmt|;
return|return
operator|new
name|SingletonCellIterator
argument_list|(
name|cell
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|FilterCellIterator
argument_list|(
name|getSubCells
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
name|shapeFilter
argument_list|)
return|;
block|}
block|}
comment|/**    * Performant implementations are expected to implement this efficiently by    * considering the current cell's boundary.    *<p>    * Precondition: Never called when getLevel() == maxLevel.    * Precondition: this.getShape().relate(p) != DISJOINT.    */
DECL|method|getSubCell
specifier|protected
specifier|abstract
name|LegacyCell
name|getSubCell
parameter_list|(
name|Point
name|p
parameter_list|)
function_decl|;
comment|/**    * Gets the cells at the next grid cell level that covers this cell.    * Precondition: Never called when getLevel() == maxLevel.    *    * @return A set of cells (no dups), sorted, modifiable, not empty, not null.    */
DECL|method|getSubCells
specifier|protected
specifier|abstract
name|Collection
argument_list|<
name|Cell
argument_list|>
name|getSubCells
parameter_list|()
function_decl|;
comment|/**    * {@link #getSubCells()}.size() -- usually a constant. Should be&gt;=2    */
DECL|method|getSubCellsSize
specifier|public
specifier|abstract
name|int
name|getSubCellsSize
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|isPrefixOf
specifier|public
name|boolean
name|isPrefixOf
parameter_list|(
name|Cell
name|c
parameter_list|)
block|{
comment|//Note: this only works when each level uses a whole number of bytes.
name|LegacyCell
name|cell
init|=
operator|(
name|LegacyCell
operator|)
name|c
decl_stmt|;
name|boolean
name|result
init|=
name|sliceEquals
argument_list|(
name|cell
operator|.
name|bytes
argument_list|,
name|cell
operator|.
name|b_off
argument_list|,
name|cell
operator|.
name|b_len
argument_list|,
name|bytes
argument_list|,
name|b_off
argument_list|,
name|b_len
argument_list|)
decl_stmt|;
assert|assert
name|result
operator|==
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|c
operator|.
name|getTokenBytesNoLeaf
argument_list|(
literal|null
argument_list|)
argument_list|,
name|getTokenBytesNoLeaf
argument_list|(
literal|null
argument_list|)
argument_list|)
assert|;
return|return
name|result
return|;
block|}
comment|/** Copied from {@link org.apache.lucene.util.StringHelper#startsWith(BytesRef, BytesRef)}    *  which calls this. This is to avoid creating a BytesRef.  */
DECL|method|sliceEquals
specifier|private
specifier|static
name|boolean
name|sliceEquals
parameter_list|(
name|byte
index|[]
name|sliceToTest_bytes
parameter_list|,
name|int
name|sliceToTest_offset
parameter_list|,
name|int
name|sliceToTest_length
parameter_list|,
name|byte
index|[]
name|other_bytes
parameter_list|,
name|int
name|other_offset
parameter_list|,
name|int
name|other_length
parameter_list|)
block|{
if|if
condition|(
name|sliceToTest_length
operator|<
name|other_length
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|i
init|=
name|sliceToTest_offset
decl_stmt|;
name|int
name|j
init|=
name|other_offset
decl_stmt|;
specifier|final
name|int
name|k
init|=
name|other_offset
operator|+
name|other_length
decl_stmt|;
while|while
condition|(
name|j
operator|<
name|k
condition|)
block|{
if|if
condition|(
name|sliceToTest_bytes
index|[
name|i
operator|++
index|]
operator|!=
name|other_bytes
index|[
name|j
operator|++
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|compareToNoLeaf
specifier|public
name|int
name|compareToNoLeaf
parameter_list|(
name|Cell
name|fromCell
parameter_list|)
block|{
name|LegacyCell
name|b
init|=
operator|(
name|LegacyCell
operator|)
name|fromCell
decl_stmt|;
return|return
name|compare
argument_list|(
name|bytes
argument_list|,
name|b_off
argument_list|,
name|b_len
argument_list|,
name|b
operator|.
name|bytes
argument_list|,
name|b
operator|.
name|b_off
argument_list|,
name|b
operator|.
name|b_len
argument_list|)
return|;
block|}
comment|/** Copied from {@link BytesRef#compareTo(BytesRef)}.    * This is to avoid creating a BytesRef. */
DECL|method|compare
specifier|protected
specifier|static
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|aBytes
parameter_list|,
name|int
name|aUpto
parameter_list|,
name|int
name|a_length
parameter_list|,
name|byte
index|[]
name|bBytes
parameter_list|,
name|int
name|bUpto
parameter_list|,
name|int
name|b_length
parameter_list|)
block|{
specifier|final
name|int
name|aStop
init|=
name|aUpto
operator|+
name|Math
operator|.
name|min
argument_list|(
name|a_length
argument_list|,
name|b_length
argument_list|)
decl_stmt|;
while|while
condition|(
name|aUpto
operator|<
name|aStop
condition|)
block|{
name|int
name|aByte
init|=
name|aBytes
index|[
name|aUpto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
name|int
name|bByte
init|=
name|bBytes
index|[
name|bUpto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
name|int
name|diff
init|=
name|aByte
operator|-
name|bByte
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
block|{
return|return
name|diff
return|;
block|}
block|}
comment|// One is a prefix of the other, or, they are equal:
return|return
name|a_length
operator|-
name|b_length
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
comment|//this method isn't "normally" called; just in asserts/tests
if|if
condition|(
name|obj
operator|instanceof
name|Cell
condition|)
block|{
name|Cell
name|cell
init|=
operator|(
name|Cell
operator|)
name|obj
decl_stmt|;
return|return
name|getTokenBytesWithLeaf
argument_list|(
literal|null
argument_list|)
operator|.
name|equals
argument_list|(
name|cell
operator|.
name|getTokenBytesWithLeaf
argument_list|(
literal|null
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getTokenBytesWithLeaf
argument_list|(
literal|null
argument_list|)
operator|.
name|hashCode
argument_list|()
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
comment|//this method isn't "normally" called; just in asserts/tests
return|return
name|getTokenBytesWithLeaf
argument_list|(
literal|null
argument_list|)
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
