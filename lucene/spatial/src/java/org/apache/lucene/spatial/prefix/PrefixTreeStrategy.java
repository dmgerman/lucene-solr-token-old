begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.prefix
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
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
name|index
operator|.
name|FieldInfo
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|spatial
operator|.
name|SpatialStrategy
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|Cell
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTree
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
name|spatial
operator|.
name|query
operator|.
name|SpatialArgs
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
name|spatial
operator|.
name|util
operator|.
name|ShapeFieldCacheDistanceValueSource
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import
begin_comment
comment|/**  * An abstract SpatialStrategy based on {@link SpatialPrefixTree}. The two  * subclasses are {@link RecursivePrefixTreeStrategy} and {@link  * TermQueryPrefixTreeStrategy}.  This strategy is most effective as a fast  * approximate spatial search filter.  *  *<h4>Characteristics:</h4>  *<ul>  *<li>Can index any shape; however only {@link RecursivePrefixTreeStrategy}  * can effectively search non-point shapes.</li>  *<li>Can index a variable number of shapes per field value. This strategy  * can do it via multiple calls to {@link #createIndexableFields(com.spatial4j.core.shape.Shape)}  * for a document or by giving it some sort of Shape aggregate (e.g. JTS  * WKT MultiPoint).  The shape's boundary is approximated to a grid precision.  *</li>  *<li>Can query with any shape.  The shape's boundary is approximated to a grid  * precision.</li>  *<li>Only {@link org.apache.lucene.spatial.query.SpatialOperation#Intersects}  * is supported.  If only points are indexed then this is effectively equivalent  * to IsWithin.</li>  *<li>The strategy supports {@link #makeDistanceValueSource(com.spatial4j.core.shape.Point,double)}  * even for multi-valued data, so long as the indexed data is all points; the  * behavior is undefined otherwise.  However,<em>it will likely be removed in  * the future</em> in lieu of using another strategy with a more scalable  * implementation.  Use of this call is the only  * circumstance in which a cache is used.  The cache is simple but as such  * it doesn't scale to large numbers of points nor is it real-time-search  * friendly.</li>  *</ul>  *  *<h4>Implementation:</h4>  * The {@link SpatialPrefixTree} does most of the work, for example returning  * a list of terms representing grids of various sizes for a supplied shape.  * An important  * configuration item is {@link #setDistErrPct(double)} which balances  * shape precision against scalability.  See those javadocs.  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|PrefixTreeStrategy
specifier|public
specifier|abstract
class|class
name|PrefixTreeStrategy
extends|extends
name|SpatialStrategy
block|{
DECL|field|grid
specifier|protected
specifier|final
name|SpatialPrefixTree
name|grid
decl_stmt|;
DECL|field|provider
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PointPrefixTreeFieldCacheProvider
argument_list|>
name|provider
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|simplifyIndexedCells
specifier|protected
specifier|final
name|boolean
name|simplifyIndexedCells
decl_stmt|;
DECL|field|defaultFieldValuesArrayLen
specifier|protected
name|int
name|defaultFieldValuesArrayLen
init|=
literal|2
decl_stmt|;
DECL|field|distErrPct
specifier|protected
name|double
name|distErrPct
init|=
name|SpatialArgs
operator|.
name|DEFAULT_DISTERRPCT
decl_stmt|;
comment|// [ 0 TO 0.5 ]
DECL|method|PrefixTreeStrategy
specifier|public
name|PrefixTreeStrategy
parameter_list|(
name|SpatialPrefixTree
name|grid
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|boolean
name|simplifyIndexedCells
parameter_list|)
block|{
name|super
argument_list|(
name|grid
operator|.
name|getSpatialContext
argument_list|()
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|this
operator|.
name|grid
operator|=
name|grid
expr_stmt|;
name|this
operator|.
name|simplifyIndexedCells
operator|=
name|simplifyIndexedCells
expr_stmt|;
block|}
comment|/**    * A memory hint used by {@link #makeDistanceValueSource(com.spatial4j.core.shape.Point)}    * for how big the initial size of each Document's array should be. The    * default is 2.  Set this to slightly more than the default expected number    * of points per document.    */
DECL|method|setDefaultFieldValuesArrayLen
specifier|public
name|void
name|setDefaultFieldValuesArrayLen
parameter_list|(
name|int
name|defaultFieldValuesArrayLen
parameter_list|)
block|{
name|this
operator|.
name|defaultFieldValuesArrayLen
operator|=
name|defaultFieldValuesArrayLen
expr_stmt|;
block|}
DECL|method|getDistErrPct
specifier|public
name|double
name|getDistErrPct
parameter_list|()
block|{
return|return
name|distErrPct
return|;
block|}
comment|/**    * The default measure of shape precision affecting shapes at index and query    * times. Points don't use this as they are always indexed at the configured    * maximum precision ({@link org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree#getMaxLevels()});    * this applies to all other shapes. Specific shapes at index and query time    * can use something different than this default value.  If you don't set a    * default then the default is {@link SpatialArgs#DEFAULT_DISTERRPCT} --    * 2.5%.    *    * @see org.apache.lucene.spatial.query.SpatialArgs#getDistErrPct()    */
DECL|method|setDistErrPct
specifier|public
name|void
name|setDistErrPct
parameter_list|(
name|double
name|distErrPct
parameter_list|)
block|{
name|this
operator|.
name|distErrPct
operator|=
name|distErrPct
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createIndexableFields
specifier|public
name|Field
index|[]
name|createIndexableFields
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
name|double
name|distErr
init|=
name|SpatialArgs
operator|.
name|calcDistanceFromErrPct
argument_list|(
name|shape
argument_list|,
name|distErrPct
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
return|return
name|createIndexableFields
argument_list|(
name|shape
argument_list|,
name|distErr
argument_list|)
return|;
block|}
DECL|method|createIndexableFields
specifier|public
name|Field
index|[]
name|createIndexableFields
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|double
name|distErr
parameter_list|)
block|{
name|int
name|detailLevel
init|=
name|grid
operator|.
name|getLevelForDistance
argument_list|(
name|distErr
argument_list|)
decl_stmt|;
comment|// note: maybe CellTokenStream should do this line, but it doesn't matter and it would create extra
comment|// coupling
name|List
argument_list|<
name|Cell
argument_list|>
name|cells
init|=
name|grid
operator|.
name|getCells
argument_list|(
name|shape
argument_list|,
name|detailLevel
argument_list|,
literal|true
argument_list|,
name|simplifyIndexedCells
argument_list|)
decl_stmt|;
comment|//intermediates cells
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
name|getFieldName
argument_list|()
argument_list|,
operator|new
name|CellTokenStream
argument_list|()
operator|.
name|setCells
argument_list|(
name|cells
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|,
name|FIELD_TYPE
argument_list|)
decl_stmt|;
return|return
operator|new
name|Field
index|[]
block|{
name|field
block|}
return|;
block|}
comment|/* Indexed, tokenized, not stored. */
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|FIELD_TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setIndexOptions
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|makeDistanceValueSource
specifier|public
name|ValueSource
name|makeDistanceValueSource
parameter_list|(
name|Point
name|queryPoint
parameter_list|,
name|double
name|multiplier
parameter_list|)
block|{
name|PointPrefixTreeFieldCacheProvider
name|p
init|=
name|provider
operator|.
name|get
argument_list|(
name|getFieldName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|//double checked locking idiom is okay since provider is threadsafe
name|p
operator|=
name|provider
operator|.
name|get
argument_list|(
name|getFieldName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|p
operator|=
operator|new
name|PointPrefixTreeFieldCacheProvider
argument_list|(
name|grid
argument_list|,
name|getFieldName
argument_list|()
argument_list|,
name|defaultFieldValuesArrayLen
argument_list|)
expr_stmt|;
name|provider
operator|.
name|put
argument_list|(
name|getFieldName
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|ShapeFieldCacheDistanceValueSource
argument_list|(
name|ctx
argument_list|,
name|p
argument_list|,
name|queryPoint
argument_list|,
name|multiplier
argument_list|)
return|;
block|}
DECL|method|getGrid
specifier|public
name|SpatialPrefixTree
name|getGrid
parameter_list|()
block|{
return|return
name|grid
return|;
block|}
block|}
end_class
end_unit
