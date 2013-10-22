begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.directory
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
name|directory
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
name|ParallelTaxonomyArrays
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
name|TaxonomyReader
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
name|CorruptIndexException
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
name|DocsAndPositionsEnum
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
name|IndexReader
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
name|MultiFields
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
name|util
operator|.
name|ArrayUtil
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link ParallelTaxonomyArrays} that are initialized from the taxonomy  * index.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|TaxonomyIndexArrays
class|class
name|TaxonomyIndexArrays
extends|extends
name|ParallelTaxonomyArrays
block|{
DECL|field|parents
specifier|private
specifier|final
name|int
index|[]
name|parents
decl_stmt|;
comment|// the following two arrays are lazily intialized. note that we only keep a
comment|// single boolean member as volatile, instead of declaring the arrays
comment|// volatile. the code guarantees that only after the boolean is set to true,
comment|// the arrays are returned.
DECL|field|initializedChildren
specifier|private
specifier|volatile
name|boolean
name|initializedChildren
init|=
literal|false
decl_stmt|;
DECL|field|children
DECL|field|siblings
specifier|private
name|int
index|[]
name|children
decl_stmt|,
name|siblings
decl_stmt|;
comment|/** Used by {@link #add(int, int)} after the array grew. */
DECL|method|TaxonomyIndexArrays
specifier|private
name|TaxonomyIndexArrays
parameter_list|(
name|int
index|[]
name|parents
parameter_list|)
block|{
name|this
operator|.
name|parents
operator|=
name|parents
expr_stmt|;
block|}
DECL|method|TaxonomyIndexArrays
specifier|public
name|TaxonomyIndexArrays
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|parents
operator|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
expr_stmt|;
if|if
condition|(
name|parents
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|initParents
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Starting Lucene 2.9, following the change LUCENE-1542, we can
comment|// no longer reliably read the parent "-1" (see comment in
comment|// LuceneTaxonomyWriter.SinglePositionTokenStream). We have no way
comment|// to fix this in indexing without breaking backward-compatibility
comment|// with existing indexes, so what we'll do instead is just
comment|// hard-code the parent of ordinal 0 to be -1, and assume (as is
comment|// indeed the case) that no other parent can be -1.
name|parents
index|[
literal|0
index|]
operator|=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
expr_stmt|;
block|}
block|}
DECL|method|TaxonomyIndexArrays
specifier|public
name|TaxonomyIndexArrays
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|TaxonomyIndexArrays
name|copyFrom
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|copyFrom
operator|!=
literal|null
assert|;
comment|// note that copyParents.length may be equal to reader.maxDoc(). this is not a bug
comment|// it may be caused if e.g. the taxonomy segments were merged, and so an updated
comment|// NRT reader was obtained, even though nothing was changed. this is not very likely
comment|// to happen.
name|int
index|[]
name|copyParents
init|=
name|copyFrom
operator|.
name|parents
argument_list|()
decl_stmt|;
name|this
operator|.
name|parents
operator|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|copyParents
argument_list|,
literal|0
argument_list|,
name|parents
argument_list|,
literal|0
argument_list|,
name|copyParents
operator|.
name|length
argument_list|)
expr_stmt|;
name|initParents
argument_list|(
name|reader
argument_list|,
name|copyParents
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|copyFrom
operator|.
name|initializedChildren
condition|)
block|{
name|initChildrenSiblings
argument_list|(
name|copyFrom
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initChildrenSiblings
specifier|private
specifier|final
specifier|synchronized
name|void
name|initChildrenSiblings
parameter_list|(
name|TaxonomyIndexArrays
name|copyFrom
parameter_list|)
block|{
if|if
condition|(
operator|!
name|initializedChildren
condition|)
block|{
comment|// must do this check !
name|children
operator|=
operator|new
name|int
index|[
name|parents
operator|.
name|length
index|]
expr_stmt|;
name|siblings
operator|=
operator|new
name|int
index|[
name|parents
operator|.
name|length
index|]
expr_stmt|;
if|if
condition|(
name|copyFrom
operator|!=
literal|null
condition|)
block|{
comment|// called from the ctor, after we know copyFrom has initialized children/siblings
name|System
operator|.
name|arraycopy
argument_list|(
name|copyFrom
operator|.
name|children
argument_list|()
argument_list|,
literal|0
argument_list|,
name|children
argument_list|,
literal|0
argument_list|,
name|copyFrom
operator|.
name|children
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|copyFrom
operator|.
name|siblings
argument_list|()
argument_list|,
literal|0
argument_list|,
name|siblings
argument_list|,
literal|0
argument_list|,
name|copyFrom
operator|.
name|siblings
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|computeChildrenSiblings
argument_list|(
name|copyFrom
operator|.
name|parents
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|computeChildrenSiblings
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|initializedChildren
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|computeChildrenSiblings
specifier|private
name|void
name|computeChildrenSiblings
parameter_list|(
name|int
name|first
parameter_list|)
block|{
comment|// reset the youngest child of all ordinals. while this should be done only
comment|// for the leaves, we don't know up front which are the leaves, so we reset
comment|// all of them.
for|for
control|(
name|int
name|i
init|=
name|first
init|;
name|i
operator|<
name|parents
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|children
index|[
name|i
index|]
operator|=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
expr_stmt|;
block|}
comment|// the root category has no parent, and therefore no siblings
if|if
condition|(
name|first
operator|==
literal|0
condition|)
block|{
name|first
operator|=
literal|1
expr_stmt|;
name|siblings
index|[
literal|0
index|]
operator|=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|first
init|;
name|i
operator|<
name|parents
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// note that parents[i] is always< i, so the right-hand-side of
comment|// the following line is already set when we get here
name|siblings
index|[
name|i
index|]
operator|=
name|children
index|[
name|parents
index|[
name|i
index|]
index|]
expr_stmt|;
name|children
index|[
name|parents
index|[
name|i
index|]
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
comment|// Read the parents of the new categories
DECL|method|initParents
specifier|private
name|void
name|initParents
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|first
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|==
name|first
condition|)
block|{
return|return;
block|}
comment|// it's ok to use MultiFields because we only iterate on one posting list.
comment|// breaking it to loop over the leaves() only complicates code for no
comment|// apparent gain.
name|DocsAndPositionsEnum
name|positions
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
literal|null
argument_list|,
name|Consts
operator|.
name|FIELD_PAYLOADS
argument_list|,
name|Consts
operator|.
name|PAYLOAD_PARENT_BYTES_REF
argument_list|,
name|DocsAndPositionsEnum
operator|.
name|FLAG_PAYLOADS
argument_list|)
decl_stmt|;
comment|// shouldn't really happen, if it does, something's wrong
if|if
condition|(
name|positions
operator|==
literal|null
operator|||
name|positions
operator|.
name|advance
argument_list|(
name|first
argument_list|)
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
name|first
argument_list|)
throw|;
block|}
name|int
name|num
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|first
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|positions
operator|.
name|docID
argument_list|()
operator|==
name|i
condition|)
block|{
if|if
condition|(
name|positions
operator|.
name|freq
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// shouldn't happen
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
name|i
argument_list|)
throw|;
block|}
name|parents
index|[
name|i
index|]
operator|=
name|positions
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|positions
operator|.
name|nextDoc
argument_list|()
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|num
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
throw|;
block|}
break|break;
block|}
block|}
else|else
block|{
comment|// this shouldn't happen
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
name|i
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Adds the given ordinal/parent info and returns either a new instance if the    * underlying array had to grow, or this instance otherwise.    *<p>    *<b>NOTE:</b> you should call this method from a thread-safe code.    */
DECL|method|add
name|TaxonomyIndexArrays
name|add
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|int
name|parentOrdinal
parameter_list|)
block|{
if|if
condition|(
name|ordinal
operator|>=
name|parents
operator|.
name|length
condition|)
block|{
name|int
index|[]
name|newarray
init|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|parents
argument_list|,
name|ordinal
operator|+
literal|1
argument_list|)
decl_stmt|;
name|newarray
index|[
name|ordinal
index|]
operator|=
name|parentOrdinal
expr_stmt|;
return|return
operator|new
name|TaxonomyIndexArrays
argument_list|(
name|newarray
argument_list|)
return|;
block|}
name|parents
index|[
name|ordinal
index|]
operator|=
name|parentOrdinal
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the parents array, where {@code parents[i]} denotes the parent of    * category ordinal {@code i}.    */
annotation|@
name|Override
DECL|method|parents
specifier|public
name|int
index|[]
name|parents
parameter_list|()
block|{
return|return
name|parents
return|;
block|}
comment|/**    * Returns the children array, where {@code children[i]} denotes the youngest    * child of category ordinal {@code i}. The youngest child is defined as the    * category that was added last to the taxonomy as an immediate child of    * {@code i}.    */
annotation|@
name|Override
DECL|method|children
specifier|public
name|int
index|[]
name|children
parameter_list|()
block|{
if|if
condition|(
operator|!
name|initializedChildren
condition|)
block|{
name|initChildrenSiblings
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// the array is guaranteed to be populated
return|return
name|children
return|;
block|}
comment|/**    * Returns the siblings array, where {@code siblings[i]} denotes the sibling    * of category ordinal {@code i}. The sibling is defined as the previous    * youngest child of {@code parents[i]}.    */
annotation|@
name|Override
DECL|method|siblings
specifier|public
name|int
index|[]
name|siblings
parameter_list|()
block|{
if|if
condition|(
operator|!
name|initializedChildren
condition|)
block|{
name|initChildrenSiblings
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// the array is guaranteed to be populated
return|return
name|siblings
return|;
block|}
block|}
end_class
end_unit
