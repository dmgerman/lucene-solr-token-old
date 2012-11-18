begin_unit
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|AtomicReader
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
name|SortedDocValues
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
name|FixedBitSet
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
begin_class
DECL|class|SortedDocValuesConsumer
specifier|public
specifier|abstract
class|class
name|SortedDocValuesConsumer
block|{
comment|/** This is called, in value sort order, once per unique    *  value. */
DECL|method|addValue
specifier|public
specifier|abstract
name|void
name|addValue
parameter_list|(
name|BytesRef
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** This is called once per document after all values are    *  added. */
DECL|method|addDoc
specifier|public
specifier|abstract
name|void
name|addDoc
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|class|Merger
specifier|public
specifier|static
class|class
name|Merger
block|{
DECL|field|fixedLength
specifier|public
name|int
name|fixedLength
init|=
operator|-
literal|2
decl_stmt|;
DECL|field|maxLength
specifier|public
name|int
name|maxLength
decl_stmt|;
DECL|field|numMergedTerms
specifier|public
name|int
name|numMergedTerms
decl_stmt|;
DECL|field|mergedTerms
specifier|private
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|mergedTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|segStates
specifier|private
specifier|final
name|List
argument_list|<
name|SegmentState
argument_list|>
name|segStates
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentState
argument_list|>
argument_list|()
decl_stmt|;
DECL|class|SegmentState
specifier|private
specifier|static
class|class
name|SegmentState
block|{
DECL|field|reader
name|AtomicReader
name|reader
decl_stmt|;
DECL|field|liveTerms
name|FixedBitSet
name|liveTerms
decl_stmt|;
DECL|field|ord
name|int
name|ord
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|values
name|SortedDocValues
name|values
decl_stmt|;
DECL|field|scratch
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|// nocommit can we factor out the compressed fields
comment|// compression?  ie we have a good idea "roughly" what
comment|// the ord should be (linear projection) so we only
comment|// need to encode the delta from that ...:
DECL|field|segOrdToMergedOrd
name|int
index|[]
name|segOrdToMergedOrd
decl_stmt|;
DECL|method|nextTerm
specifier|public
name|BytesRef
name|nextTerm
parameter_list|()
block|{
while|while
condition|(
name|ord
operator|<
name|values
operator|.
name|getValueCount
argument_list|()
operator|-
literal|1
condition|)
block|{
name|ord
operator|++
expr_stmt|;
if|if
condition|(
name|liveTerms
operator|==
literal|null
operator|||
name|liveTerms
operator|.
name|get
argument_list|(
name|ord
argument_list|)
condition|)
block|{
name|values
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
return|return
name|scratch
return|;
block|}
else|else
block|{
comment|// Skip "deleted" terms (ie, terms that were not
comment|// referenced by any live docs):
name|values
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|class|TermMergeQueue
specifier|private
specifier|static
class|class
name|TermMergeQueue
extends|extends
name|PriorityQueue
argument_list|<
name|SegmentState
argument_list|>
block|{
DECL|method|TermMergeQueue
specifier|public
name|TermMergeQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|SegmentState
name|a
parameter_list|,
name|SegmentState
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|scratch
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|scratch
argument_list|)
operator|<=
literal|0
return|;
block|}
block|}
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// First pass: mark "live" terms
for|for
control|(
name|AtomicReader
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
comment|// nocommit what if this is null...?  need default source?
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|SegmentState
name|state
init|=
operator|new
name|SegmentState
argument_list|()
decl_stmt|;
name|state
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|state
operator|.
name|values
operator|=
name|reader
operator|.
name|getSortedDocValues
argument_list|(
name|mergeState
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|values
operator|==
literal|null
condition|)
block|{
name|state
operator|.
name|values
operator|=
name|SortedDocValues
operator|.
name|DEFAULT
expr_stmt|;
block|}
name|segStates
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
assert|assert
name|state
operator|.
name|values
operator|.
name|getValueCount
argument_list|()
operator|<
name|Integer
operator|.
name|MAX_VALUE
assert|;
if|if
condition|(
name|reader
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
name|state
operator|.
name|liveTerms
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|state
operator|.
name|values
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
name|state
operator|.
name|liveTerms
operator|.
name|set
argument_list|(
name|state
operator|.
name|values
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// nocommit we can unload the bits to disk to reduce
comment|// transient ram spike...
block|}
comment|// Second pass: merge only the live terms
name|TermMergeQueue
name|q
init|=
operator|new
name|TermMergeQueue
argument_list|(
name|segStates
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SegmentState
name|segState
range|:
name|segStates
control|)
block|{
if|if
condition|(
name|segState
operator|.
name|nextTerm
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// nocommit we could defer this to 3rd pass (and
comment|// reduce transient RAM spike) but then
comment|// we'd spend more effort computing the mapping...:
name|segState
operator|.
name|segOrdToMergedOrd
operator|=
operator|new
name|int
index|[
name|segState
operator|.
name|values
operator|.
name|getValueCount
argument_list|()
index|]
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|segState
argument_list|)
expr_stmt|;
block|}
block|}
name|BytesRef
name|lastTerm
init|=
literal|null
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
name|int
name|ord
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|q
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|SegmentState
name|top
init|=
name|q
operator|.
name|top
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastTerm
operator|==
literal|null
operator|||
operator|!
name|lastTerm
operator|.
name|equals
argument_list|(
name|top
operator|.
name|scratch
argument_list|)
condition|)
block|{
name|lastTerm
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|top
operator|.
name|scratch
argument_list|)
expr_stmt|;
comment|// nocommit we could spill this to disk instead of
comment|// RAM, and replay on finish...
name|mergedTerms
operator|.
name|add
argument_list|(
name|lastTerm
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastTerm
operator|==
literal|null
condition|)
block|{
name|fixedLength
operator|=
name|lastTerm
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|ord
operator|++
expr_stmt|;
if|if
condition|(
name|lastTerm
operator|.
name|length
operator|!=
name|fixedLength
condition|)
block|{
name|fixedLength
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
name|maxLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxLength
argument_list|,
name|lastTerm
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|top
operator|.
name|segOrdToMergedOrd
index|[
name|top
operator|.
name|ord
index|]
operator|=
name|ord
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|top
operator|.
name|nextTerm
argument_list|()
operator|==
literal|null
condition|)
block|{
name|q
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|q
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
block|}
name|numMergedTerms
operator|=
name|ord
expr_stmt|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|SortedDocValuesConsumer
name|consumer
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Third pass: write merged result
for|for
control|(
name|BytesRef
name|term
range|:
name|mergedTerms
control|)
block|{
name|consumer
operator|.
name|addValue
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|SegmentState
name|segState
range|:
name|segStates
control|)
block|{
name|Bits
name|liveDocs
init|=
name|segState
operator|.
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|int
name|maxDoc
init|=
name|segState
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
name|int
name|segOrd
init|=
name|segState
operator|.
name|values
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|int
name|mergedOrd
init|=
name|segState
operator|.
name|segOrdToMergedOrd
index|[
name|segOrd
index|]
decl_stmt|;
name|consumer
operator|.
name|addDoc
argument_list|(
name|mergedOrd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|// nocommit why return int...?
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|Merger
name|merger
parameter_list|)
throws|throws
name|IOException
block|{
name|merger
operator|.
name|finish
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
