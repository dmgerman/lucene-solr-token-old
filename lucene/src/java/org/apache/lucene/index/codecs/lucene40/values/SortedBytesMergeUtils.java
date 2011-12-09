begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.lucene40.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|lucene40
operator|.
name|values
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collections
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
name|List
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
name|DocValues
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
name|MergeState
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
name|DocValues
operator|.
name|SortedSource
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
name|DocValues
operator|.
name|Source
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
name|DocValues
operator|.
name|Type
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
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
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
name|store
operator|.
name|IndexOutput
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
name|PriorityQueue
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
name|packed
operator|.
name|PackedInts
import|;
end_import
begin_comment
comment|/**  * @lucene.internal  */
end_comment
begin_class
DECL|class|SortedBytesMergeUtils
specifier|final
class|class
name|SortedBytesMergeUtils
block|{
DECL|method|SortedBytesMergeUtils
specifier|private
name|SortedBytesMergeUtils
parameter_list|()
block|{
comment|// no instance
block|}
DECL|method|init
specifier|static
name|MergeContext
name|init
parameter_list|(
name|Type
name|type
parameter_list|,
name|DocValues
index|[]
name|docValues
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|MergeState
name|mergeState
parameter_list|)
block|{
name|int
name|size
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|BYTES_FIXED_SORTED
condition|)
block|{
for|for
control|(
name|DocValues
name|indexDocValues
range|:
name|docValues
control|)
block|{
if|if
condition|(
name|indexDocValues
operator|!=
literal|null
condition|)
block|{
name|size
operator|=
name|indexDocValues
operator|.
name|getValueSize
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
assert|assert
name|size
operator|>=
literal|0
assert|;
block|}
return|return
operator|new
name|MergeContext
argument_list|(
name|comp
argument_list|,
name|mergeState
argument_list|,
name|size
argument_list|,
name|type
argument_list|)
return|;
block|}
DECL|class|MergeContext
specifier|public
specifier|static
specifier|final
class|class
name|MergeContext
block|{
DECL|field|comp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
decl_stmt|;
DECL|field|missingValue
specifier|private
specifier|final
name|BytesRef
name|missingValue
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|sizePerValues
specifier|final
name|int
name|sizePerValues
decl_stmt|;
comment|// -1 if var length
DECL|field|type
specifier|final
name|Type
name|type
decl_stmt|;
DECL|field|docToEntry
specifier|final
name|int
index|[]
name|docToEntry
decl_stmt|;
DECL|field|offsets
name|long
index|[]
name|offsets
decl_stmt|;
comment|// if non-null #mergeRecords collects byte offsets here
DECL|method|MergeContext
specifier|public
name|MergeContext
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|MergeState
name|mergeState
parameter_list|,
name|int
name|size
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
assert|assert
name|type
operator|==
name|Type
operator|.
name|BYTES_FIXED_SORTED
operator|||
name|type
operator|==
name|Type
operator|.
name|BYTES_VAR_SORTED
assert|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
name|this
operator|.
name|sizePerValues
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|missingValue
operator|.
name|grow
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|missingValue
operator|.
name|length
operator|=
name|size
expr_stmt|;
block|}
name|docToEntry
operator|=
operator|new
name|int
index|[
name|mergeState
operator|.
name|mergedDocCount
index|]
expr_stmt|;
block|}
block|}
DECL|method|buildSlices
specifier|static
name|List
argument_list|<
name|SortedSourceSlice
argument_list|>
name|buildSlices
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|DocValues
index|[]
name|docValues
parameter_list|,
name|MergeContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|SortedSourceSlice
argument_list|>
name|slices
init|=
operator|new
name|ArrayList
argument_list|<
name|SortedSourceSlice
argument_list|>
argument_list|()
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
name|docValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SortedSourceSlice
name|nextSlice
decl_stmt|;
specifier|final
name|Source
name|directSource
decl_stmt|;
if|if
condition|(
name|docValues
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
operator|(
name|directSource
operator|=
name|docValues
index|[
name|i
index|]
operator|.
name|getDirectSource
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|SortedSourceSlice
name|slice
init|=
operator|new
name|SortedSourceSlice
argument_list|(
name|i
argument_list|,
name|directSource
operator|.
name|asSortedSource
argument_list|()
argument_list|,
name|mergeState
argument_list|,
name|ctx
operator|.
name|docToEntry
argument_list|)
decl_stmt|;
name|nextSlice
operator|=
name|slice
expr_stmt|;
block|}
else|else
block|{
name|nextSlice
operator|=
operator|new
name|SortedSourceSlice
argument_list|(
name|i
argument_list|,
operator|new
name|MissingValueSource
argument_list|(
name|ctx
argument_list|)
argument_list|,
name|mergeState
argument_list|,
name|ctx
operator|.
name|docToEntry
argument_list|)
expr_stmt|;
block|}
name|createOrdMapping
argument_list|(
name|mergeState
argument_list|,
name|nextSlice
argument_list|)
expr_stmt|;
name|slices
operator|.
name|add
argument_list|(
name|nextSlice
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|slices
argument_list|)
return|;
block|}
comment|/*    * In order to merge we need to map the ords used in each segment to the new    * global ords in the new segment. Additionally we need to drop values that    * are not referenced anymore due to deleted documents. This method walks all    * live documents and fetches their current ordinal. We store this ordinal per    * slice and (SortedSourceSlice#ordMapping) and remember the doc to ord    * mapping in docIDToRelativeOrd. After the merge SortedSourceSlice#ordMapping    * contains the new global ordinals for the relative index.    */
DECL|method|createOrdMapping
specifier|private
specifier|static
name|void
name|createOrdMapping
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|SortedSourceSlice
name|currentSlice
parameter_list|)
block|{
specifier|final
name|int
name|readerIdx
init|=
name|currentSlice
operator|.
name|readerIdx
decl_stmt|;
specifier|final
name|int
index|[]
name|currentDocMap
init|=
name|mergeState
operator|.
name|docMaps
index|[
name|readerIdx
index|]
decl_stmt|;
specifier|final
name|int
name|docBase
init|=
name|currentSlice
operator|.
name|docToOrdStart
decl_stmt|;
assert|assert
name|docBase
operator|==
name|mergeState
operator|.
name|docBase
index|[
name|readerIdx
index|]
assert|;
if|if
condition|(
name|currentDocMap
operator|!=
literal|null
condition|)
block|{
comment|// we have deletes
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|currentDocMap
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|doc
init|=
name|currentDocMap
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// not deleted
specifier|final
name|int
name|ord
init|=
name|currentSlice
operator|.
name|source
operator|.
name|ord
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// collect ords strictly
comment|// increasing
name|currentSlice
operator|.
name|docIDToRelativeOrd
index|[
name|docBase
operator|+
name|doc
index|]
operator|=
name|ord
expr_stmt|;
comment|// use ord + 1 to identify unreferenced values (ie. == 0)
name|currentSlice
operator|.
name|ordMapping
index|[
name|ord
index|]
operator|=
name|ord
operator|+
literal|1
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// no deletes
specifier|final
name|IndexReaderAndLiveDocs
name|indexReaderAndLiveDocs
init|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|readerIdx
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|indexReaderAndLiveDocs
operator|.
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
assert|assert
name|indexReaderAndLiveDocs
operator|.
name|liveDocs
operator|==
literal|null
assert|;
assert|assert
name|currentSlice
operator|.
name|docToOrdEnd
operator|-
name|currentSlice
operator|.
name|docToOrdStart
operator|==
name|numDocs
assert|;
for|for
control|(
name|int
name|doc
init|=
literal|0
init|;
name|doc
operator|<
name|numDocs
condition|;
name|doc
operator|++
control|)
block|{
specifier|final
name|int
name|ord
init|=
name|currentSlice
operator|.
name|source
operator|.
name|ord
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|currentSlice
operator|.
name|docIDToRelativeOrd
index|[
name|docBase
operator|+
name|doc
index|]
operator|=
name|ord
expr_stmt|;
comment|// use ord + 1 to identify unreferenced values (ie. == 0)
name|currentSlice
operator|.
name|ordMapping
index|[
name|ord
index|]
operator|=
name|ord
operator|+
literal|1
expr_stmt|;
block|}
block|}
block|}
DECL|method|mergeRecords
specifier|static
name|int
name|mergeRecords
parameter_list|(
name|MergeContext
name|ctx
parameter_list|,
name|IndexOutput
name|datOut
parameter_list|,
name|List
argument_list|<
name|SortedSourceSlice
argument_list|>
name|slices
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|RecordMerger
name|merger
init|=
operator|new
name|RecordMerger
argument_list|(
operator|new
name|MergeQueue
argument_list|(
name|slices
operator|.
name|size
argument_list|()
argument_list|,
name|ctx
operator|.
name|comp
argument_list|)
argument_list|,
name|slices
operator|.
name|toArray
argument_list|(
operator|new
name|SortedSourceSlice
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|long
index|[]
name|offsets
init|=
name|ctx
operator|.
name|offsets
decl_stmt|;
specifier|final
name|boolean
name|recordOffsets
init|=
name|offsets
operator|!=
literal|null
decl_stmt|;
name|long
name|offset
init|=
literal|0
decl_stmt|;
name|BytesRef
name|currentMergedBytes
decl_stmt|;
name|merger
operator|.
name|pushTop
argument_list|()
expr_stmt|;
while|while
condition|(
name|merger
operator|.
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|merger
operator|.
name|pullTop
argument_list|()
expr_stmt|;
name|currentMergedBytes
operator|=
name|merger
operator|.
name|current
expr_stmt|;
assert|assert
name|ctx
operator|.
name|sizePerValues
operator|==
operator|-
literal|1
operator|||
name|ctx
operator|.
name|sizePerValues
operator|==
name|currentMergedBytes
operator|.
name|length
operator|:
literal|"size: "
operator|+
name|ctx
operator|.
name|sizePerValues
operator|+
literal|" spare: "
operator|+
name|currentMergedBytes
operator|.
name|length
assert|;
if|if
condition|(
name|recordOffsets
condition|)
block|{
name|offset
operator|+=
name|currentMergedBytes
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|merger
operator|.
name|currentOrd
operator|>=
name|offsets
operator|.
name|length
condition|)
block|{
name|offsets
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|offsets
argument_list|,
name|merger
operator|.
name|currentOrd
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|offsets
index|[
name|merger
operator|.
name|currentOrd
index|]
operator|=
name|offset
expr_stmt|;
block|}
name|datOut
operator|.
name|writeBytes
argument_list|(
name|currentMergedBytes
operator|.
name|bytes
argument_list|,
name|currentMergedBytes
operator|.
name|offset
argument_list|,
name|currentMergedBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|merger
operator|.
name|pushTop
argument_list|()
expr_stmt|;
block|}
name|ctx
operator|.
name|offsets
operator|=
name|offsets
expr_stmt|;
assert|assert
name|offsets
operator|==
literal|null
operator|||
name|offsets
index|[
name|merger
operator|.
name|currentOrd
operator|-
literal|1
index|]
operator|==
name|offset
assert|;
return|return
name|merger
operator|.
name|currentOrd
return|;
block|}
DECL|class|RecordMerger
specifier|private
specifier|static
specifier|final
class|class
name|RecordMerger
block|{
DECL|field|queue
specifier|private
specifier|final
name|MergeQueue
name|queue
decl_stmt|;
DECL|field|top
specifier|private
specifier|final
name|SortedSourceSlice
index|[]
name|top
decl_stmt|;
DECL|field|numTop
specifier|private
name|int
name|numTop
decl_stmt|;
DECL|field|current
name|BytesRef
name|current
decl_stmt|;
DECL|field|currentOrd
name|int
name|currentOrd
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|RecordMerger
name|RecordMerger
parameter_list|(
name|MergeQueue
name|queue
parameter_list|,
name|SortedSourceSlice
index|[]
name|top
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|top
operator|=
name|top
expr_stmt|;
name|this
operator|.
name|numTop
operator|=
name|top
operator|.
name|length
expr_stmt|;
block|}
DECL|method|pullTop
specifier|private
name|void
name|pullTop
parameter_list|()
block|{
comment|// extract all subs from the queue that have the same
comment|// top record
assert|assert
name|numTop
operator|==
literal|0
assert|;
assert|assert
name|currentOrd
operator|>=
literal|0
assert|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|SortedSourceSlice
name|popped
init|=
name|top
index|[
name|numTop
operator|++
index|]
operator|=
name|queue
operator|.
name|pop
argument_list|()
decl_stmt|;
comment|// use ord + 1 to identify unreferenced values (ie. == 0)
name|popped
operator|.
name|ordMapping
index|[
name|popped
operator|.
name|relativeOrd
index|]
operator|=
name|currentOrd
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
operator|!
operator|(
name|queue
operator|.
name|top
argument_list|()
operator|)
operator|.
name|current
operator|.
name|bytesEquals
argument_list|(
name|top
index|[
literal|0
index|]
operator|.
name|current
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
name|current
operator|=
name|top
index|[
literal|0
index|]
operator|.
name|current
expr_stmt|;
block|}
DECL|method|pushTop
specifier|private
name|void
name|pushTop
parameter_list|()
throws|throws
name|IOException
block|{
comment|// call next() on each top, and put back into queue
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numTop
condition|;
name|i
operator|++
control|)
block|{
name|top
index|[
name|i
index|]
operator|.
name|current
operator|=
name|top
index|[
name|i
index|]
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|top
index|[
name|i
index|]
operator|.
name|current
operator|!=
literal|null
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|top
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|currentOrd
operator|++
expr_stmt|;
name|numTop
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|class|SortedSourceSlice
specifier|static
class|class
name|SortedSourceSlice
block|{
DECL|field|source
specifier|final
name|SortedSource
name|source
decl_stmt|;
DECL|field|readerIdx
specifier|final
name|int
name|readerIdx
decl_stmt|;
comment|/* global array indexed by docID containg the relative ord for the doc */
DECL|field|docIDToRelativeOrd
specifier|final
name|int
index|[]
name|docIDToRelativeOrd
decl_stmt|;
comment|/*      * maps relative ords to merged global ords - index is relative ord value      * new global ord this map gets updates as we merge ords. later we use the      * docIDtoRelativeOrd to get the previous relative ord to get the new ord      * from the relative ord map.      */
DECL|field|ordMapping
specifier|final
name|int
index|[]
name|ordMapping
decl_stmt|;
comment|/* start index into docIDToRelativeOrd */
DECL|field|docToOrdStart
specifier|final
name|int
name|docToOrdStart
decl_stmt|;
comment|/* end index into docIDToRelativeOrd */
DECL|field|docToOrdEnd
specifier|final
name|int
name|docToOrdEnd
decl_stmt|;
DECL|field|current
name|BytesRef
name|current
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|/* the currently merged relative ordinal */
DECL|field|relativeOrd
name|int
name|relativeOrd
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|SortedSourceSlice
name|SortedSourceSlice
parameter_list|(
name|int
name|readerIdx
parameter_list|,
name|SortedSource
name|source
parameter_list|,
name|MergeState
name|state
parameter_list|,
name|int
index|[]
name|docToOrd
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|readerIdx
operator|=
name|readerIdx
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|docIDToRelativeOrd
operator|=
name|docToOrd
expr_stmt|;
name|this
operator|.
name|ordMapping
operator|=
operator|new
name|int
index|[
name|source
operator|.
name|getValueCount
argument_list|()
index|]
expr_stmt|;
name|this
operator|.
name|docToOrdStart
operator|=
name|state
operator|.
name|docBase
index|[
name|readerIdx
index|]
expr_stmt|;
name|this
operator|.
name|docToOrdEnd
operator|=
name|this
operator|.
name|docToOrdStart
operator|+
name|numDocs
argument_list|(
name|state
argument_list|,
name|readerIdx
argument_list|)
expr_stmt|;
block|}
DECL|method|numDocs
specifier|private
specifier|static
name|int
name|numDocs
parameter_list|(
name|MergeState
name|state
parameter_list|,
name|int
name|readerIndex
parameter_list|)
block|{
if|if
condition|(
name|readerIndex
operator|==
name|state
operator|.
name|docBase
operator|.
name|length
operator|-
literal|1
condition|)
block|{
return|return
name|state
operator|.
name|mergedDocCount
operator|-
name|state
operator|.
name|docBase
index|[
name|readerIndex
index|]
return|;
block|}
return|return
name|state
operator|.
name|docBase
index|[
name|readerIndex
operator|+
literal|1
index|]
operator|-
name|state
operator|.
name|docBase
index|[
name|readerIndex
index|]
return|;
block|}
DECL|method|next
name|BytesRef
name|next
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
name|relativeOrd
operator|+
literal|1
init|;
name|i
operator|<
name|ordMapping
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ordMapping
index|[
name|i
index|]
operator|!=
literal|0
condition|)
block|{
comment|// skip ords that are not referenced anymore
name|source
operator|.
name|getByOrd
argument_list|(
name|i
argument_list|,
name|current
argument_list|)
expr_stmt|;
name|relativeOrd
operator|=
name|i
expr_stmt|;
return|return
name|current
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|writeOrds
name|void
name|writeOrds
parameter_list|(
name|PackedInts
operator|.
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
name|docToOrdStart
init|;
name|i
operator|<
name|docToOrdEnd
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|mappedOrd
init|=
name|docIDToRelativeOrd
index|[
name|i
index|]
decl_stmt|;
assert|assert
name|mappedOrd
operator|<
name|ordMapping
operator|.
name|length
assert|;
assert|assert
name|ordMapping
index|[
name|mappedOrd
index|]
operator|>
literal|0
operator|:
literal|"illegal mapping ord maps to an unreferenced value"
assert|;
name|writer
operator|.
name|add
argument_list|(
name|ordMapping
index|[
name|mappedOrd
index|]
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*    * if a segment has no values at all we use this source to fill in the missing    * value in the right place (depending on the comparator used)    */
DECL|class|MissingValueSource
specifier|private
specifier|static
specifier|final
class|class
name|MissingValueSource
extends|extends
name|SortedSource
block|{
DECL|field|missingValue
specifier|private
name|BytesRef
name|missingValue
decl_stmt|;
DECL|method|MissingValueSource
specifier|public
name|MissingValueSource
parameter_list|(
name|MergeContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
operator|.
name|type
argument_list|,
name|ctx
operator|.
name|comp
argument_list|)
expr_stmt|;
name|this
operator|.
name|missingValue
operator|=
name|ctx
operator|.
name|missingValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|int
name|ord
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getByOrd
specifier|public
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
name|bytesRef
operator|.
name|copyBytes
argument_list|(
name|missingValue
argument_list|)
expr_stmt|;
return|return
name|bytesRef
return|;
block|}
annotation|@
name|Override
DECL|method|getDocToOrd
specifier|public
name|PackedInts
operator|.
name|Reader
name|getDocToOrd
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
comment|/*    * merge queue    */
DECL|class|MergeQueue
specifier|private
specifier|static
specifier|final
class|class
name|MergeQueue
extends|extends
name|PriorityQueue
argument_list|<
name|SortedSourceSlice
argument_list|>
block|{
DECL|field|comp
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
decl_stmt|;
DECL|method|MergeQueue
specifier|public
name|MergeQueue
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|SortedSourceSlice
name|a
parameter_list|,
name|SortedSourceSlice
name|b
parameter_list|)
block|{
name|int
name|cmp
init|=
name|comp
operator|.
name|compare
argument_list|(
name|a
operator|.
name|current
argument_list|,
name|b
operator|.
name|current
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
operator|<
literal|0
return|;
block|}
else|else
block|{
comment|// just a tie-breaker
return|return
name|a
operator|.
name|docToOrdStart
operator|<
name|b
operator|.
name|docToOrdStart
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
