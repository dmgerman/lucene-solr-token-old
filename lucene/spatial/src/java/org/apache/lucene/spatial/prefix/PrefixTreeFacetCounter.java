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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|index
operator|.
name|IndexReaderContext
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
name|LeafReaderContext
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
name|PostingsEnum
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
name|search
operator|.
name|DocIdSet
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
name|search
operator|.
name|DocIdSetIterator
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
name|util
operator|.
name|Bits
import|;
end_import
begin_comment
comment|/**  * Computes facets on cells for {@link org.apache.lucene.spatial.prefix.PrefixTreeStrategy}.  *<p>  *<em>NOTE:</em> If for a given document and a given field using  * {@link org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy}  * multiple values are indexed (i.e. multi-valued) and at least one of them is a non-point, then there is a possibility  * of double-counting the document in the facet results.  Since each shape is independently turned into grid cells at  * a resolution chosen by the shape's size, it's possible they will be indexed at different resolutions.  This means  * the document could be present in BOTH the postings for a cell in both its prefix and leaf variants.  To avoid this,  * use a single valued field with a {@link com.spatial4j.core.shape.ShapeCollection} (or WKT equivalent).  Or  * calculate a suitable level/distErr to index both and call  * {@link org.apache.lucene.spatial.prefix.PrefixTreeStrategy#createIndexableFields(com.spatial4j.core.shape.Shape, int)}  * with the same value for all shapes for a given document/field.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PrefixTreeFacetCounter
specifier|public
class|class
name|PrefixTreeFacetCounter
block|{
comment|/** A callback/visitor of facet counts. */
DECL|class|FacetVisitor
specifier|public
specifier|static
specifier|abstract
class|class
name|FacetVisitor
block|{
comment|/** Called at the start of the segment, if there is indexed data. */
DECL|method|startOfSegment
specifier|public
name|void
name|startOfSegment
parameter_list|()
block|{}
comment|/** Called for cells with a leaf, or cells at the target facet level.  {@code count} is greater than zero.      * When an ancestor cell is given with non-zero count, the count can be considered to be added to all cells      * below. You won't necessarily get a cell at level {@code facetLevel} if the indexed data is courser (bigger).      */
DECL|method|visit
specifier|public
specifier|abstract
name|void
name|visit
parameter_list|(
name|Cell
name|cell
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
block|}
DECL|method|PrefixTreeFacetCounter
specifier|private
name|PrefixTreeFacetCounter
parameter_list|()
block|{   }
comment|/**    * Computes facets using a callback/visitor style design, allowing flexibility for the caller to determine what to do    * with each underlying count.    * @param strategy the prefix tree strategy (contains the field reference, grid, max levels)    * @param context the IndexReader's context    * @param topAcceptDocs a Bits to limit counted docs. If null, live docs are counted.    * @param queryShape the shape to limit the range of facet counts to    * @param facetLevel the maximum depth (detail) of faceted cells    * @param facetVisitor the visitor/callback to receive the counts    */
DECL|method|compute
specifier|public
specifier|static
name|void
name|compute
parameter_list|(
name|PrefixTreeStrategy
name|strategy
parameter_list|,
name|IndexReaderContext
name|context
parameter_list|,
name|Bits
name|topAcceptDocs
parameter_list|,
name|Shape
name|queryShape
parameter_list|,
name|int
name|facetLevel
parameter_list|,
name|FacetVisitor
name|facetVisitor
parameter_list|)
throws|throws
name|IOException
block|{
comment|//We collect per-leaf
for|for
control|(
specifier|final
name|LeafReaderContext
name|leafCtx
range|:
name|context
operator|.
name|leaves
argument_list|()
control|)
block|{
comment|//determine leaf acceptDocs Bits
name|Bits
name|leafAcceptDocs
decl_stmt|;
if|if
condition|(
name|topAcceptDocs
operator|==
literal|null
condition|)
block|{
name|leafAcceptDocs
operator|=
name|leafCtx
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
comment|//filter deleted
block|}
else|else
block|{
name|leafAcceptDocs
operator|=
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|topAcceptDocs
operator|.
name|get
argument_list|(
name|leafCtx
operator|.
name|docBase
operator|+
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|leafCtx
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
return|;
block|}
block|}
expr_stmt|;
block|}
name|compute
argument_list|(
name|strategy
argument_list|,
name|leafCtx
argument_list|,
name|leafAcceptDocs
argument_list|,
name|queryShape
argument_list|,
name|facetLevel
argument_list|,
name|facetVisitor
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Lower-level per-leaf segment method. */
DECL|method|compute
specifier|public
specifier|static
name|void
name|compute
parameter_list|(
specifier|final
name|PrefixTreeStrategy
name|strategy
parameter_list|,
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|,
specifier|final
name|Shape
name|queryShape
parameter_list|,
specifier|final
name|int
name|facetLevel
parameter_list|,
specifier|final
name|FacetVisitor
name|facetVisitor
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|acceptDocs
operator|!=
literal|null
operator|&&
name|acceptDocs
operator|.
name|length
argument_list|()
operator|!=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"acceptDocs bits length "
operator|+
name|acceptDocs
operator|.
name|length
argument_list|()
operator|+
literal|" != leaf maxdoc "
operator|+
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|SpatialPrefixTree
name|tree
init|=
name|strategy
operator|.
name|getGrid
argument_list|()
decl_stmt|;
comment|//scanLevel is an optimization knob of AbstractVisitingPrefixTreeFilter. It's unlikely
comment|// another scanLevel would be much faster and it tends to be a risky knob (can help a little, can hurt a ton).
comment|// TODO use RPT's configured scan level?  Do we know better here?  Hard to say.
specifier|final
name|int
name|scanLevel
init|=
name|tree
operator|.
name|getMaxLevels
argument_list|()
decl_stmt|;
comment|//AbstractVisitingPrefixTreeFilter is a Lucene Filter.  We don't need a filter; we use it for its great prefix-tree
comment|// traversal code.  TODO consider refactoring if/when it makes sense (more use cases than this)
operator|new
name|AbstractVisitingPrefixTreeQuery
argument_list|(
name|queryShape
argument_list|,
name|strategy
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|tree
argument_list|,
name|facetLevel
argument_list|,
name|scanLevel
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"anonPrefixTreeQuery"
return|;
comment|//un-used
block|}
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|contexts
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|facetLevel
operator|==
name|super
operator|.
name|detailLevel
assert|;
comment|//same thing, FYI. (constant)
return|return
operator|new
name|VisitorTemplate
argument_list|(
name|context
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|facetVisitor
operator|.
name|startOfSegment
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|DocIdSet
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
comment|//unused;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|visitPrefix
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
comment|// At facetLevel...
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|==
name|facetLevel
condition|)
block|{
comment|// Count docs
name|visitLeaf
argument_list|(
name|cell
argument_list|)
expr_stmt|;
comment|//we're not a leaf but we treat it as such at facet level
return|return
literal|false
return|;
comment|//don't descend further; this is enough detail
block|}
comment|// We optimize for discriminating filters (reflected in acceptDocs) and short-circuit if no
comment|// matching docs. We could do this at all levels or never but the closer we get to the facet level, the
comment|// higher the probability this is worthwhile. We do when docFreq == 1 because it's a cheap check, especially
comment|// due to "pulsing" in the codec.
comment|//TODO this opt should move to VisitorTemplate (which contains an optimization TODO to this effect)
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|==
name|facetLevel
operator|-
literal|1
operator|||
name|termsEnum
operator|.
name|docFreq
argument_list|()
operator|==
literal|1
condition|)
block|{
if|if
condition|(
operator|!
name|hasDocsAtThisTerm
argument_list|()
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
specifier|protected
name|void
name|visitLeaf
parameter_list|(
name|Cell
name|cell
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|count
init|=
name|countDocsAtThisTerm
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|facetVisitor
operator|.
name|visit
argument_list|(
name|cell
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|countDocsAtThisTerm
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|acceptDocs
operator|==
literal|null
condition|)
block|{
return|return
name|termsEnum
operator|.
name|docFreq
argument_list|()
return|;
block|}
name|int
name|count
init|=
literal|0
decl_stmt|;
name|postingsEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|postingsEnum
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
while|while
condition|(
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|acceptDocs
operator|.
name|get
argument_list|(
name|postingsEnum
operator|.
name|docID
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
name|count
operator|++
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
specifier|private
name|boolean
name|hasDocsAtThisTerm
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|acceptDocs
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|postingsEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|postingsEnum
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|int
name|nextDoc
init|=
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
while|while
condition|(
name|nextDoc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
operator|&&
name|acceptDocs
operator|.
name|get
argument_list|(
name|nextDoc
argument_list|)
operator|==
literal|false
condition|)
block|{
name|nextDoc
operator|=
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
return|return
name|nextDoc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
block|}
operator|.
name|getDocIdSet
argument_list|()
return|;
block|}
block|}
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
