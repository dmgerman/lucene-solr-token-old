begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|BitsSlice
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
name|MultiBits
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
name|ReaderUtil
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
name|Comparator
import|;
end_import
begin_comment
comment|/**  * Exposes flex API, merged from flex API of sub-segments.  * This does a merge sort, by term text, of the sub-readers.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|MultiTermsEnum
specifier|public
specifier|final
class|class
name|MultiTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|queue
specifier|private
specifier|final
name|TermMergeQueue
name|queue
decl_stmt|;
DECL|field|subs
specifier|private
specifier|final
name|TermsEnumWithSlice
index|[]
name|subs
decl_stmt|;
comment|// all of our subs (one per sub-reader)
DECL|field|currentSubs
specifier|private
specifier|final
name|TermsEnumWithSlice
index|[]
name|currentSubs
decl_stmt|;
comment|// current subs that have at least one term for this field
DECL|field|top
specifier|private
specifier|final
name|TermsEnumWithSlice
index|[]
name|top
decl_stmt|;
DECL|field|subDocs
specifier|private
specifier|final
name|MultiDocsEnum
operator|.
name|EnumWithSlice
index|[]
name|subDocs
decl_stmt|;
DECL|field|subDocsAndPositions
specifier|private
specifier|final
name|MultiDocsAndPositionsEnum
operator|.
name|EnumWithSlice
index|[]
name|subDocsAndPositions
decl_stmt|;
DECL|field|lastSeek
specifier|private
name|BytesRef
name|lastSeek
decl_stmt|;
DECL|field|lastSeekScratch
specifier|private
specifier|final
name|BytesRef
name|lastSeekScratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|numTop
specifier|private
name|int
name|numTop
decl_stmt|;
DECL|field|numSubs
specifier|private
name|int
name|numSubs
decl_stmt|;
DECL|field|current
specifier|private
name|BytesRef
name|current
decl_stmt|;
DECL|field|termComp
specifier|private
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
decl_stmt|;
DECL|class|TermsEnumIndex
specifier|public
specifier|static
class|class
name|TermsEnumIndex
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|final
specifier|static
name|TermsEnumIndex
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|TermsEnumIndex
index|[
literal|0
index|]
decl_stmt|;
DECL|field|subIndex
specifier|final
name|int
name|subIndex
decl_stmt|;
DECL|field|termsEnum
specifier|final
name|TermsEnum
name|termsEnum
decl_stmt|;
DECL|method|TermsEnumIndex
specifier|public
name|TermsEnumIndex
parameter_list|(
name|TermsEnum
name|termsEnum
parameter_list|,
name|int
name|subIndex
parameter_list|)
block|{
name|this
operator|.
name|termsEnum
operator|=
name|termsEnum
expr_stmt|;
name|this
operator|.
name|subIndex
operator|=
name|subIndex
expr_stmt|;
block|}
block|}
DECL|method|getMatchCount
specifier|public
name|int
name|getMatchCount
parameter_list|()
block|{
return|return
name|numTop
return|;
block|}
DECL|method|getMatchArray
specifier|public
name|TermsEnumWithSlice
index|[]
name|getMatchArray
parameter_list|()
block|{
return|return
name|top
return|;
block|}
DECL|method|MultiTermsEnum
specifier|public
name|MultiTermsEnum
parameter_list|(
name|ReaderUtil
operator|.
name|Slice
index|[]
name|slices
parameter_list|)
block|{
name|queue
operator|=
operator|new
name|TermMergeQueue
argument_list|(
name|slices
operator|.
name|length
argument_list|)
expr_stmt|;
name|top
operator|=
operator|new
name|TermsEnumWithSlice
index|[
name|slices
operator|.
name|length
index|]
expr_stmt|;
name|subs
operator|=
operator|new
name|TermsEnumWithSlice
index|[
name|slices
operator|.
name|length
index|]
expr_stmt|;
name|subDocs
operator|=
operator|new
name|MultiDocsEnum
operator|.
name|EnumWithSlice
index|[
name|slices
operator|.
name|length
index|]
expr_stmt|;
name|subDocsAndPositions
operator|=
operator|new
name|MultiDocsAndPositionsEnum
operator|.
name|EnumWithSlice
index|[
name|slices
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|slices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subs
index|[
name|i
index|]
operator|=
operator|new
name|TermsEnumWithSlice
argument_list|(
name|slices
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|subDocs
index|[
name|i
index|]
operator|=
operator|new
name|MultiDocsEnum
operator|.
name|EnumWithSlice
argument_list|()
expr_stmt|;
name|subDocs
index|[
name|i
index|]
operator|.
name|slice
operator|=
name|slices
index|[
name|i
index|]
expr_stmt|;
name|subDocsAndPositions
index|[
name|i
index|]
operator|=
operator|new
name|MultiDocsAndPositionsEnum
operator|.
name|EnumWithSlice
argument_list|()
expr_stmt|;
name|subDocsAndPositions
index|[
name|i
index|]
operator|.
name|slice
operator|=
name|slices
index|[
name|i
index|]
expr_stmt|;
block|}
name|currentSubs
operator|=
operator|new
name|TermsEnumWithSlice
index|[
name|slices
operator|.
name|length
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
block|{
return|return
name|current
return|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|termComp
return|;
block|}
comment|/** The terms array must be newly created TermsEnum, ie    *  {@link TermsEnum#next} has not yet been called. */
DECL|method|reset
specifier|public
name|TermsEnum
name|reset
parameter_list|(
name|TermsEnumIndex
index|[]
name|termsEnumsIndex
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|termsEnumsIndex
operator|.
name|length
operator|<=
name|top
operator|.
name|length
assert|;
name|numSubs
operator|=
literal|0
expr_stmt|;
name|numTop
operator|=
literal|0
expr_stmt|;
name|termComp
operator|=
literal|null
expr_stmt|;
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|termsEnumsIndex
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|TermsEnumIndex
name|termsEnumIndex
init|=
name|termsEnumsIndex
index|[
name|i
index|]
decl_stmt|;
assert|assert
name|termsEnumIndex
operator|!=
literal|null
assert|;
comment|// init our term comp
if|if
condition|(
name|termComp
operator|==
literal|null
condition|)
block|{
name|queue
operator|.
name|termComp
operator|=
name|termComp
operator|=
name|termsEnumIndex
operator|.
name|termsEnum
operator|.
name|getComparator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// We cannot merge sub-readers that have
comment|// different TermComps
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|subTermComp
init|=
name|termsEnumIndex
operator|.
name|termsEnum
operator|.
name|getComparator
argument_list|()
decl_stmt|;
if|if
condition|(
name|subTermComp
operator|!=
literal|null
operator|&&
operator|!
name|subTermComp
operator|.
name|equals
argument_list|(
name|termComp
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"sub-readers have different BytesRef.Comparators: "
operator|+
name|subTermComp
operator|+
literal|" vs "
operator|+
name|termComp
operator|+
literal|"; cannot merge"
argument_list|)
throw|;
block|}
block|}
specifier|final
name|BytesRef
name|term
init|=
name|termsEnumIndex
operator|.
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnumWithSlice
name|entry
init|=
name|subs
index|[
name|termsEnumIndex
operator|.
name|subIndex
index|]
decl_stmt|;
name|entry
operator|.
name|reset
argument_list|(
name|termsEnumIndex
operator|.
name|termsEnum
argument_list|,
name|term
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|currentSubs
index|[
name|numSubs
operator|++
index|]
operator|=
name|entry
expr_stmt|;
block|}
else|else
block|{
comment|// field has no terms
block|}
block|}
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|TermsEnum
operator|.
name|EMPTY
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|boolean
name|seekExact
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|numTop
operator|=
literal|0
expr_stmt|;
name|boolean
name|seekOpt
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|lastSeek
operator|!=
literal|null
operator|&&
name|termComp
operator|.
name|compare
argument_list|(
name|lastSeek
argument_list|,
name|term
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|seekOpt
operator|=
literal|true
expr_stmt|;
block|}
name|lastSeek
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSubs
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|boolean
name|status
decl_stmt|;
comment|// LUCENE-2130: if we had just seek'd already, prior
comment|// to this seek, and the new seek term is after the
comment|// previous one, don't try to re-seek this sub if its
comment|// current term is already beyond this new seek term.
comment|// Doing so is a waste because this sub will simply
comment|// seek to the same spot.
if|if
condition|(
name|seekOpt
condition|)
block|{
specifier|final
name|BytesRef
name|curTerm
init|=
name|currentSubs
index|[
name|i
index|]
operator|.
name|current
decl_stmt|;
if|if
condition|(
name|curTerm
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|cmp
init|=
name|termComp
operator|.
name|compare
argument_list|(
name|term
argument_list|,
name|curTerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|status
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|status
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|status
operator|=
name|currentSubs
index|[
name|i
index|]
operator|.
name|terms
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
name|useCache
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|status
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
name|status
operator|=
name|currentSubs
index|[
name|i
index|]
operator|.
name|terms
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
name|useCache
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|status
condition|)
block|{
name|top
index|[
name|numTop
operator|++
index|]
operator|=
name|currentSubs
index|[
name|i
index|]
expr_stmt|;
name|current
operator|=
name|currentSubs
index|[
name|i
index|]
operator|.
name|current
operator|=
name|currentSubs
index|[
name|i
index|]
operator|.
name|terms
operator|.
name|term
argument_list|()
expr_stmt|;
block|}
block|}
comment|// if at least one sub had exact match to the requested
comment|// term then we found match
return|return
name|numTop
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|numTop
operator|=
literal|0
expr_stmt|;
name|boolean
name|seekOpt
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|lastSeek
operator|!=
literal|null
operator|&&
name|termComp
operator|.
name|compare
argument_list|(
name|lastSeek
argument_list|,
name|term
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|seekOpt
operator|=
literal|true
expr_stmt|;
block|}
name|lastSeekScratch
operator|.
name|copy
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|lastSeek
operator|=
name|lastSeekScratch
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSubs
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SeekStatus
name|status
decl_stmt|;
comment|// LUCENE-2130: if we had just seek'd already, prior
comment|// to this seek, and the new seek term is after the
comment|// previous one, don't try to re-seek this sub if its
comment|// current term is already beyond this new seek term.
comment|// Doing so is a waste because this sub will simply
comment|// seek to the same spot.
if|if
condition|(
name|seekOpt
condition|)
block|{
specifier|final
name|BytesRef
name|curTerm
init|=
name|currentSubs
index|[
name|i
index|]
operator|.
name|current
decl_stmt|;
if|if
condition|(
name|curTerm
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|cmp
init|=
name|termComp
operator|.
name|compare
argument_list|(
name|term
argument_list|,
name|curTerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|status
operator|=
name|SeekStatus
operator|.
name|FOUND
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|status
operator|=
name|SeekStatus
operator|.
name|NOT_FOUND
expr_stmt|;
block|}
else|else
block|{
name|status
operator|=
name|currentSubs
index|[
name|i
index|]
operator|.
name|terms
operator|.
name|seekCeil
argument_list|(
name|term
argument_list|,
name|useCache
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|status
operator|=
name|SeekStatus
operator|.
name|END
expr_stmt|;
block|}
block|}
else|else
block|{
name|status
operator|=
name|currentSubs
index|[
name|i
index|]
operator|.
name|terms
operator|.
name|seekCeil
argument_list|(
name|term
argument_list|,
name|useCache
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|status
operator|==
name|SeekStatus
operator|.
name|FOUND
condition|)
block|{
name|top
index|[
name|numTop
operator|++
index|]
operator|=
name|currentSubs
index|[
name|i
index|]
expr_stmt|;
name|current
operator|=
name|currentSubs
index|[
name|i
index|]
operator|.
name|current
operator|=
name|currentSubs
index|[
name|i
index|]
operator|.
name|terms
operator|.
name|term
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|status
operator|==
name|SeekStatus
operator|.
name|NOT_FOUND
condition|)
block|{
name|currentSubs
index|[
name|i
index|]
operator|.
name|current
operator|=
name|currentSubs
index|[
name|i
index|]
operator|.
name|terms
operator|.
name|term
argument_list|()
expr_stmt|;
assert|assert
name|currentSubs
index|[
name|i
index|]
operator|.
name|current
operator|!=
literal|null
assert|;
name|queue
operator|.
name|add
argument_list|(
name|currentSubs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// enum exhausted
name|currentSubs
index|[
name|i
index|]
operator|.
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|numTop
operator|>
literal|0
condition|)
block|{
comment|// at least one sub had exact match to the requested term
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
block|}
elseif|else
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// no sub had exact match, but at least one sub found
comment|// a term after the requested term -- advance to that
comment|// next term:
name|pullTop
argument_list|()
expr_stmt|;
return|return
name|SeekStatus
operator|.
name|NOT_FOUND
return|;
block|}
else|else
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|pullTop
specifier|private
name|void
name|pullTop
parameter_list|()
block|{
comment|// extract all subs from the queue that have the same
comment|// top term
assert|assert
name|numTop
operator|==
literal|0
assert|;
while|while
condition|(
literal|true
condition|)
block|{
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
name|terms
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
else|else
block|{
comment|// no more fields in this reader
block|}
block|}
name|numTop
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|lastSeek
operator|=
literal|null
expr_stmt|;
comment|// restore queue
name|pushTop
argument_list|()
expr_stmt|;
comment|// gather equal top fields
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|pullTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|sum
init|=
literal|0
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
name|numTop
condition|;
name|i
operator|++
control|)
block|{
name|sum
operator|+=
name|top
index|[
name|i
index|]
operator|.
name|terms
operator|.
name|docFreq
argument_list|()
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|sum
init|=
literal|0
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
name|numTop
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|v
init|=
name|top
index|[
name|i
index|]
operator|.
name|terms
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|v
return|;
block|}
name|sum
operator|+=
name|v
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|MultiDocsEnum
name|docsEnum
decl_stmt|;
if|if
condition|(
name|reuse
operator|!=
literal|null
condition|)
block|{
name|docsEnum
operator|=
operator|(
name|MultiDocsEnum
operator|)
name|reuse
expr_stmt|;
block|}
else|else
block|{
name|docsEnum
operator|=
operator|new
name|MultiDocsEnum
argument_list|()
expr_stmt|;
block|}
specifier|final
name|MultiBits
name|multiSkipDocs
decl_stmt|;
if|if
condition|(
name|skipDocs
operator|instanceof
name|MultiBits
condition|)
block|{
name|multiSkipDocs
operator|=
operator|(
name|MultiBits
operator|)
name|skipDocs
expr_stmt|;
block|}
else|else
block|{
name|multiSkipDocs
operator|=
literal|null
expr_stmt|;
block|}
name|int
name|upto
init|=
literal|0
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
name|numTop
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|TermsEnumWithSlice
name|entry
init|=
name|top
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|Bits
name|b
decl_stmt|;
if|if
condition|(
name|multiSkipDocs
operator|!=
literal|null
condition|)
block|{
comment|// optimize for common case: requested skip docs is a
comment|// congruent sub-slice of MultiBits: in this case, we
comment|// just pull the skipDocs from the sub reader, rather
comment|// than making the inefficient
comment|// Slice(Multi(sub-readers)):
specifier|final
name|MultiBits
operator|.
name|SubResult
name|sub
init|=
name|multiSkipDocs
operator|.
name|getMatchingSub
argument_list|(
name|entry
operator|.
name|subSlice
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|.
name|matches
condition|)
block|{
name|b
operator|=
name|sub
operator|.
name|result
expr_stmt|;
block|}
else|else
block|{
comment|// custom case: requested skip docs is foreign:
comment|// must slice it on every access
name|b
operator|=
operator|new
name|BitsSlice
argument_list|(
name|skipDocs
argument_list|,
name|entry
operator|.
name|subSlice
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|skipDocs
operator|!=
literal|null
condition|)
block|{
name|b
operator|=
operator|new
name|BitsSlice
argument_list|(
name|skipDocs
argument_list|,
name|entry
operator|.
name|subSlice
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// no deletions
name|b
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|DocsEnum
name|subDocsEnum
init|=
name|entry
operator|.
name|terms
operator|.
name|docs
argument_list|(
name|b
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|subDocsEnum
operator|!=
literal|null
condition|)
block|{
name|subDocs
index|[
name|upto
index|]
operator|.
name|docsEnum
operator|=
name|subDocsEnum
expr_stmt|;
name|subDocs
index|[
name|upto
index|]
operator|.
name|slice
operator|=
name|entry
operator|.
name|subSlice
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|upto
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|docsEnum
operator|.
name|reset
argument_list|(
name|subDocs
argument_list|,
name|upto
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|MultiDocsAndPositionsEnum
name|docsAndPositionsEnum
decl_stmt|;
if|if
condition|(
name|reuse
operator|!=
literal|null
condition|)
block|{
name|docsAndPositionsEnum
operator|=
operator|(
name|MultiDocsAndPositionsEnum
operator|)
name|reuse
expr_stmt|;
block|}
else|else
block|{
name|docsAndPositionsEnum
operator|=
operator|new
name|MultiDocsAndPositionsEnum
argument_list|()
expr_stmt|;
block|}
specifier|final
name|MultiBits
name|multiSkipDocs
decl_stmt|;
if|if
condition|(
name|skipDocs
operator|instanceof
name|MultiBits
condition|)
block|{
name|multiSkipDocs
operator|=
operator|(
name|MultiBits
operator|)
name|skipDocs
expr_stmt|;
block|}
else|else
block|{
name|multiSkipDocs
operator|=
literal|null
expr_stmt|;
block|}
name|int
name|upto
init|=
literal|0
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
name|numTop
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|TermsEnumWithSlice
name|entry
init|=
name|top
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|Bits
name|b
decl_stmt|;
if|if
condition|(
name|multiSkipDocs
operator|!=
literal|null
condition|)
block|{
comment|// Optimize for common case: requested skip docs is a
comment|// congruent sub-slice of MultiBits: in this case, we
comment|// just pull the skipDocs from the sub reader, rather
comment|// than making the inefficient
comment|// Slice(Multi(sub-readers)):
specifier|final
name|MultiBits
operator|.
name|SubResult
name|sub
init|=
name|multiSkipDocs
operator|.
name|getMatchingSub
argument_list|(
name|top
index|[
name|i
index|]
operator|.
name|subSlice
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|.
name|matches
condition|)
block|{
name|b
operator|=
name|sub
operator|.
name|result
expr_stmt|;
block|}
else|else
block|{
comment|// custom case: requested skip docs is foreign:
comment|// must slice it on every access (very
comment|// inefficient)
name|b
operator|=
operator|new
name|BitsSlice
argument_list|(
name|skipDocs
argument_list|,
name|top
index|[
name|i
index|]
operator|.
name|subSlice
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|skipDocs
operator|!=
literal|null
condition|)
block|{
name|b
operator|=
operator|new
name|BitsSlice
argument_list|(
name|skipDocs
argument_list|,
name|top
index|[
name|i
index|]
operator|.
name|subSlice
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// no deletions
name|b
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|DocsAndPositionsEnum
name|subPostings
init|=
name|entry
operator|.
name|terms
operator|.
name|docsAndPositions
argument_list|(
name|b
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|subPostings
operator|!=
literal|null
condition|)
block|{
name|subDocsAndPositions
index|[
name|upto
index|]
operator|.
name|docsAndPositionsEnum
operator|=
name|subPostings
expr_stmt|;
name|subDocsAndPositions
index|[
name|upto
index|]
operator|.
name|slice
operator|=
name|entry
operator|.
name|subSlice
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|entry
operator|.
name|terms
operator|.
name|docs
argument_list|(
name|b
argument_list|,
literal|null
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// At least one of our subs does not store
comment|// positions -- we can't correctly produce a
comment|// MultiDocsAndPositions enum
return|return
literal|null
return|;
block|}
block|}
block|}
if|if
condition|(
name|upto
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|docsAndPositionsEnum
operator|.
name|reset
argument_list|(
name|subDocsAndPositions
argument_list|,
name|upto
argument_list|)
return|;
block|}
block|}
DECL|class|TermsEnumWithSlice
specifier|private
specifier|final
specifier|static
class|class
name|TermsEnumWithSlice
block|{
DECL|field|subSlice
specifier|private
specifier|final
name|ReaderUtil
operator|.
name|Slice
name|subSlice
decl_stmt|;
DECL|field|terms
specifier|private
name|TermsEnum
name|terms
decl_stmt|;
DECL|field|current
specifier|public
name|BytesRef
name|current
decl_stmt|;
DECL|method|TermsEnumWithSlice
specifier|public
name|TermsEnumWithSlice
parameter_list|(
name|ReaderUtil
operator|.
name|Slice
name|subSlice
parameter_list|)
block|{
name|this
operator|.
name|subSlice
operator|=
name|subSlice
expr_stmt|;
assert|assert
name|subSlice
operator|.
name|length
operator|>=
literal|0
operator|:
literal|"length="
operator|+
name|subSlice
operator|.
name|length
assert|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|TermsEnum
name|terms
parameter_list|,
name|BytesRef
name|term
parameter_list|)
block|{
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|current
operator|=
name|term
expr_stmt|;
block|}
block|}
DECL|class|TermMergeQueue
specifier|private
specifier|final
specifier|static
class|class
name|TermMergeQueue
extends|extends
name|PriorityQueue
argument_list|<
name|TermsEnumWithSlice
argument_list|>
block|{
DECL|field|termComp
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
decl_stmt|;
DECL|method|TermMergeQueue
name|TermMergeQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
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
name|TermsEnumWithSlice
name|termsA
parameter_list|,
name|TermsEnumWithSlice
name|termsB
parameter_list|)
block|{
specifier|final
name|int
name|cmp
init|=
name|termComp
operator|.
name|compare
argument_list|(
name|termsA
operator|.
name|current
argument_list|,
name|termsB
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
return|return
name|termsA
operator|.
name|subSlice
operator|.
name|start
operator|<
name|termsB
operator|.
name|subSlice
operator|.
name|start
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
