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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
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
name|Rectangle
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/** The base for the original two SPT's: Geohash& Quad. Don't subclass this for new SPTs.  * @lucene.internal */
end_comment
begin_class
DECL|class|LegacyPrefixTree
specifier|abstract
class|class
name|LegacyPrefixTree
extends|extends
name|SpatialPrefixTree
block|{
DECL|method|LegacyPrefixTree
specifier|public
name|LegacyPrefixTree
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|int
name|maxLevels
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|,
name|maxLevels
argument_list|)
expr_stmt|;
block|}
DECL|method|getDistanceForLevel
specifier|public
name|double
name|getDistanceForLevel
parameter_list|(
name|int
name|level
parameter_list|)
block|{
if|if
condition|(
name|level
argument_list|<
literal|1
operator|||
name|level
argument_list|>
name|getMaxLevels
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Level must be in 1 to maxLevels range"
argument_list|)
throw|;
comment|//TODO cache for each level
name|Cell
name|cell
init|=
name|getCell
argument_list|(
name|ctx
operator|.
name|getWorldBounds
argument_list|()
operator|.
name|getCenter
argument_list|()
argument_list|,
name|level
argument_list|)
decl_stmt|;
name|Rectangle
name|bbox
init|=
name|cell
operator|.
name|getShape
argument_list|()
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
name|double
name|width
init|=
name|bbox
operator|.
name|getWidth
argument_list|()
decl_stmt|;
name|double
name|height
init|=
name|bbox
operator|.
name|getHeight
argument_list|()
decl_stmt|;
comment|//Use standard cartesian hypotenuse. For geospatial, this answer is larger
comment|// than the correct one but it's okay to over-estimate.
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|width
operator|*
name|width
operator|+
name|height
operator|*
name|height
argument_list|)
return|;
block|}
comment|/**    * Returns the cell containing point {@code p} at the specified {@code level}.    */
DECL|method|getCell
specifier|protected
specifier|abstract
name|Cell
name|getCell
parameter_list|(
name|Point
name|p
parameter_list|,
name|int
name|level
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|getTreeCellIterator
specifier|public
name|CellIterator
name|getTreeCellIterator
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|int
name|detailLevel
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|shape
operator|instanceof
name|Point
operator|)
condition|)
return|return
name|super
operator|.
name|getTreeCellIterator
argument_list|(
name|shape
argument_list|,
name|detailLevel
argument_list|)
return|;
comment|//This specialization is here because the legacy implementations don't have a fast implementation of
comment|// cell.getSubCells(point). It's fastest here to encode the full bytes for detailLevel, and create
comment|// subcells from the bytesRef in a loop. This avoids an O(N^2) encode, and we have O(N) instead.
name|Cell
name|cell
init|=
name|getCell
argument_list|(
operator|(
name|Point
operator|)
name|shape
argument_list|,
name|detailLevel
argument_list|)
decl_stmt|;
assert|assert
operator|!
name|cell
operator|.
name|isLeaf
argument_list|()
operator|&&
name|cell
operator|instanceof
name|LegacyCell
assert|;
name|BytesRef
name|fullBytes
init|=
name|cell
operator|.
name|getTokenBytesNoLeaf
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|//fill in reverse order to be sorted
name|Cell
index|[]
name|cells
init|=
operator|new
name|Cell
index|[
name|detailLevel
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
name|detailLevel
condition|;
name|i
operator|++
control|)
block|{
name|Cell
name|parentCell
init|=
name|getWorldCell
argument_list|()
decl_stmt|;
name|fullBytes
operator|.
name|length
operator|=
name|i
expr_stmt|;
name|parentCell
operator|.
name|readCell
argument_list|(
name|fullBytes
argument_list|)
expr_stmt|;
name|cells
index|[
name|i
operator|-
literal|1
index|]
operator|=
name|parentCell
expr_stmt|;
block|}
name|cells
index|[
name|detailLevel
operator|-
literal|1
index|]
operator|=
name|cell
expr_stmt|;
return|return
operator|new
name|FilterCellIterator
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|cells
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
comment|//null filter
block|}
block|}
end_class
end_unit
