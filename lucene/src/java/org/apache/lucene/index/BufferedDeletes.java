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
name|io
operator|.
name|PrintStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|atomic
operator|.
name|AtomicInteger
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
name|atomic
operator|.
name|AtomicLong
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
name|IndexSearcher
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
name|Query
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
name|Scorer
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
name|Weight
import|;
end_import
begin_comment
comment|/** Holds a {@link SegmentDeletes} for each segment in the  *  index. */
end_comment
begin_class
DECL|class|BufferedDeletes
class|class
name|BufferedDeletes
block|{
comment|// Deletes for all flushed/merged segments:
DECL|field|deletesMap
specifier|private
specifier|final
name|Map
argument_list|<
name|SegmentInfo
argument_list|,
name|SegmentDeletes
argument_list|>
name|deletesMap
init|=
operator|new
name|HashMap
argument_list|<
name|SegmentInfo
argument_list|,
name|SegmentDeletes
argument_list|>
argument_list|()
decl_stmt|;
comment|// used only by assert
DECL|field|lastDeleteTerm
specifier|private
name|Term
name|lastDeleteTerm
decl_stmt|;
DECL|field|infoStream
specifier|private
name|PrintStream
name|infoStream
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|AtomicLong
name|bytesUsed
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numTerms
specifier|private
specifier|final
name|AtomicInteger
name|numTerms
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|messageID
specifier|private
specifier|final
name|int
name|messageID
decl_stmt|;
DECL|method|BufferedDeletes
specifier|public
name|BufferedDeletes
parameter_list|(
name|int
name|messageID
parameter_list|)
block|{
name|this
operator|.
name|messageID
operator|=
name|messageID
expr_stmt|;
block|}
DECL|method|message
specifier|private
specifier|synchronized
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|infoStream
operator|.
name|println
argument_list|(
literal|"BD "
operator|+
name|messageID
operator|+
literal|" ["
operator|+
operator|new
name|Date
argument_list|()
operator|+
literal|"; "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]: BD "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setInfoStream
specifier|public
specifier|synchronized
name|void
name|setInfoStream
parameter_list|(
name|PrintStream
name|infoStream
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
block|}
DECL|method|pushDeletes
specifier|public
specifier|synchronized
name|void
name|pushDeletes
parameter_list|(
name|SegmentDeletes
name|newDeletes
parameter_list|,
name|SegmentInfo
name|info
parameter_list|)
block|{
name|pushDeletes
argument_list|(
name|newDeletes
argument_list|,
name|info
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Moves all pending deletes onto the provided segment,
comment|// then clears the pending deletes
DECL|method|pushDeletes
specifier|public
specifier|synchronized
name|void
name|pushDeletes
parameter_list|(
name|SegmentDeletes
name|newDeletes
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|boolean
name|noLimit
parameter_list|)
block|{
assert|assert
name|newDeletes
operator|.
name|any
argument_list|()
assert|;
name|numTerms
operator|.
name|addAndGet
argument_list|(
name|newDeletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|noLimit
condition|)
block|{
assert|assert
operator|!
name|deletesMap
operator|.
name|containsKey
argument_list|(
name|info
argument_list|)
assert|;
assert|assert
name|info
operator|!=
literal|null
assert|;
name|deletesMap
operator|.
name|put
argument_list|(
name|info
argument_list|,
name|newDeletes
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|newDeletes
operator|.
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|SegmentDeletes
name|deletes
init|=
name|getDeletes
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|deletes
operator|.
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|deletes
operator|.
name|update
argument_list|(
name|newDeletes
argument_list|,
name|noLimit
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|deletes
operator|.
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"push deletes seg="
operator|+
name|info
operator|+
literal|" dels="
operator|+
name|getDeletes
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
block|}
DECL|method|clear
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|deletesMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|numTerms
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|any
specifier|synchronized
name|boolean
name|any
parameter_list|()
block|{
return|return
name|bytesUsed
operator|.
name|get
argument_list|()
operator|!=
literal|0
return|;
block|}
DECL|method|numTerms
specifier|public
name|int
name|numTerms
parameter_list|()
block|{
return|return
name|numTerms
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|bytesUsed
specifier|public
name|long
name|bytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
operator|.
name|get
argument_list|()
return|;
block|}
comment|// IW calls this on finishing a merge.  While the merge
comment|// was running, it's possible new deletes were pushed onto
comment|// our last (and only our last) segment.  In this case we
comment|// must carry forward those deletes onto the merged
comment|// segment.
DECL|method|commitMerge
specifier|synchronized
name|void
name|commitMerge
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
block|{
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"commitMerge merge.info="
operator|+
name|merge
operator|.
name|info
operator|+
literal|" merge.segments="
operator|+
name|merge
operator|.
name|segments
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SegmentInfo
name|lastInfo
init|=
name|merge
operator|.
name|segments
operator|.
name|lastElement
argument_list|()
decl_stmt|;
specifier|final
name|SegmentDeletes
name|lastDeletes
init|=
name|deletesMap
operator|.
name|get
argument_list|(
name|lastInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastDeletes
operator|!=
literal|null
condition|)
block|{
name|deletesMap
operator|.
name|remove
argument_list|(
name|lastInfo
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|deletesMap
operator|.
name|containsKey
argument_list|(
name|merge
operator|.
name|info
argument_list|)
assert|;
name|deletesMap
operator|.
name|put
argument_list|(
name|merge
operator|.
name|info
argument_list|,
name|lastDeletes
argument_list|)
expr_stmt|;
comment|// don't need to update numTerms/bytesUsed since we
comment|// are just moving the deletes from one info to
comment|// another
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"commitMerge done: new deletions="
operator|+
name|lastDeletes
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"commitMerge done: no new deletions"
argument_list|)
expr_stmt|;
block|}
assert|assert
operator|!
name|anyDeletes
argument_list|(
name|merge
operator|.
name|segments
operator|.
name|range
argument_list|(
literal|0
argument_list|,
name|merge
operator|.
name|segments
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
assert|;
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
block|}
DECL|method|clear
specifier|synchronized
name|void
name|clear
parameter_list|(
name|SegmentDeletes
name|deletes
parameter_list|)
block|{
name|deletes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|applyDeletes
specifier|public
specifier|synchronized
name|boolean
name|applyDeletes
parameter_list|(
name|IndexWriter
operator|.
name|ReaderPool
name|readerPool
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|SegmentInfos
name|applyInfos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|any
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|long
name|t0
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"applyDeletes: applyInfos="
operator|+
name|applyInfos
operator|+
literal|"; index="
operator|+
name|segmentInfos
argument_list|)
expr_stmt|;
block|}
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
assert|assert
name|applyInfos
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
name|boolean
name|any
init|=
literal|false
decl_stmt|;
specifier|final
name|SegmentInfo
name|lastApplyInfo
init|=
name|applyInfos
operator|.
name|lastElement
argument_list|()
decl_stmt|;
specifier|final
name|int
name|lastIdx
init|=
name|segmentInfos
operator|.
name|indexOf
argument_list|(
name|lastApplyInfo
argument_list|)
decl_stmt|;
specifier|final
name|SegmentInfo
name|firstInfo
init|=
name|applyInfos
operator|.
name|firstElement
argument_list|()
decl_stmt|;
specifier|final
name|int
name|firstIdx
init|=
name|segmentInfos
operator|.
name|indexOf
argument_list|(
name|firstInfo
argument_list|)
decl_stmt|;
comment|// applyInfos must be a slice of segmentInfos
assert|assert
name|lastIdx
operator|-
name|firstIdx
operator|+
literal|1
operator|==
name|applyInfos
operator|.
name|size
argument_list|()
assert|;
comment|// iterate over all segment infos backwards
comment|// coalesceing deletes along the way
comment|// when we're at or below the last of the
comment|// segments to apply to, start applying the deletes
comment|// we traverse up to the first apply infos
name|SegmentDeletes
name|coalescedDeletes
init|=
literal|null
decl_stmt|;
name|boolean
name|hasDeletes
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|segIdx
init|=
name|segmentInfos
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|segIdx
operator|>=
name|firstIdx
condition|;
name|segIdx
operator|--
control|)
block|{
specifier|final
name|SegmentInfo
name|info
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|segIdx
argument_list|)
decl_stmt|;
specifier|final
name|SegmentDeletes
name|deletes
init|=
name|deletesMap
operator|.
name|get
argument_list|(
name|info
argument_list|)
decl_stmt|;
assert|assert
name|deletes
operator|==
literal|null
operator|||
name|deletes
operator|.
name|any
argument_list|()
assert|;
if|if
condition|(
name|deletes
operator|==
literal|null
operator|&&
name|coalescedDeletes
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"applyDeletes: seg="
operator|+
name|info
operator|+
literal|" segment's deletes=["
operator|+
operator|(
name|deletes
operator|==
literal|null
condition|?
literal|"null"
else|:
name|deletes
operator|)
operator|+
literal|"]; coalesced deletes=["
operator|+
operator|(
name|coalescedDeletes
operator|==
literal|null
condition|?
literal|"null"
else|:
name|coalescedDeletes
operator|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|hasDeletes
operator||=
name|deletes
operator|!=
literal|null
expr_stmt|;
if|if
condition|(
name|segIdx
operator|<=
name|lastIdx
operator|&&
name|hasDeletes
condition|)
block|{
name|any
operator||=
name|applyDeletes
argument_list|(
name|readerPool
argument_list|,
name|info
argument_list|,
name|coalescedDeletes
argument_list|,
name|deletes
argument_list|)
expr_stmt|;
if|if
condition|(
name|deletes
operator|!=
literal|null
condition|)
block|{
comment|// we've applied doc ids, and they're only applied
comment|// on the current segment
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|deletes
operator|.
name|docIDs
operator|.
name|size
argument_list|()
operator|*
name|SegmentDeletes
operator|.
name|BYTES_PER_DEL_DOCID
argument_list|)
expr_stmt|;
name|deletes
operator|.
name|clearDocIDs
argument_list|()
expr_stmt|;
block|}
block|}
comment|// now coalesce at the max limit
if|if
condition|(
name|deletes
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|coalescedDeletes
operator|==
literal|null
condition|)
block|{
name|coalescedDeletes
operator|=
operator|new
name|SegmentDeletes
argument_list|()
expr_stmt|;
block|}
comment|// TODO: we could make this single pass (coalesce as
comment|// we apply the deletes
name|coalescedDeletes
operator|.
name|update
argument_list|(
name|deletes
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|// move all deletes to segment just before our merge.
if|if
condition|(
name|firstIdx
operator|>
literal|0
condition|)
block|{
name|SegmentDeletes
name|mergedDeletes
init|=
literal|null
decl_stmt|;
comment|// TODO: we could also make this single pass
for|for
control|(
name|SegmentInfo
name|info
range|:
name|applyInfos
control|)
block|{
specifier|final
name|SegmentDeletes
name|deletes
init|=
name|deletesMap
operator|.
name|get
argument_list|(
name|info
argument_list|)
decl_stmt|;
if|if
condition|(
name|deletes
operator|!=
literal|null
condition|)
block|{
assert|assert
name|deletes
operator|.
name|any
argument_list|()
assert|;
if|if
condition|(
name|mergedDeletes
operator|==
literal|null
condition|)
block|{
name|mergedDeletes
operator|=
name|getDeletes
argument_list|(
name|segmentInfos
operator|.
name|info
argument_list|(
name|firstIdx
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|numTerms
operator|.
name|addAndGet
argument_list|(
operator|-
name|mergedDeletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|mergedDeletes
operator|.
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mergedDeletes
operator|.
name|update
argument_list|(
name|deletes
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|mergedDeletes
operator|!=
literal|null
condition|)
block|{
name|numTerms
operator|.
name|addAndGet
argument_list|(
name|mergedDeletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|mergedDeletes
operator|.
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|mergedDeletes
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"applyDeletes: merge all deletes into seg="
operator|+
name|segmentInfos
operator|.
name|info
argument_list|(
name|firstIdx
operator|-
literal|1
argument_list|)
operator|+
literal|": "
operator|+
name|mergedDeletes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|message
argument_list|(
literal|"applyDeletes: no deletes to merge"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// We drop the deletes in this case, because we've
comment|// applied them to segment infos starting w/ the first
comment|// segment.  There are no prior segments so there's no
comment|// reason to keep them around.  When the applyInfos ==
comment|// segmentInfos this means all deletes have been
comment|// removed:
block|}
name|remove
argument_list|(
name|applyInfos
argument_list|)
expr_stmt|;
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
assert|assert
name|applyInfos
operator|!=
name|segmentInfos
operator|||
operator|!
name|any
argument_list|()
assert|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"applyDeletes took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|)
operator|+
literal|" msec"
argument_list|)
expr_stmt|;
block|}
return|return
name|any
return|;
block|}
DECL|method|applyDeletes
specifier|private
specifier|synchronized
name|boolean
name|applyDeletes
parameter_list|(
name|IndexWriter
operator|.
name|ReaderPool
name|readerPool
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|SegmentDeletes
name|coalescedDeletes
parameter_list|,
name|SegmentDeletes
name|segmentDeletes
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|readerPool
operator|.
name|infoIsLive
argument_list|(
name|info
argument_list|)
assert|;
assert|assert
name|coalescedDeletes
operator|==
literal|null
operator|||
name|coalescedDeletes
operator|.
name|docIDs
operator|.
name|size
argument_list|()
operator|==
literal|0
assert|;
name|boolean
name|any
init|=
literal|false
decl_stmt|;
comment|// Lock order: IW -> BD -> RP
name|SegmentReader
name|reader
init|=
name|readerPool
operator|.
name|get
argument_list|(
name|info
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|coalescedDeletes
operator|!=
literal|null
condition|)
block|{
name|any
operator||=
name|applyDeletes
argument_list|(
name|coalescedDeletes
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|segmentDeletes
operator|!=
literal|null
condition|)
block|{
name|any
operator||=
name|applyDeletes
argument_list|(
name|segmentDeletes
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|readerPool
operator|.
name|release
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|any
return|;
block|}
DECL|method|applyDeletes
specifier|private
specifier|synchronized
name|boolean
name|applyDeletes
parameter_list|(
name|SegmentDeletes
name|deletes
parameter_list|,
name|SegmentReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|any
init|=
literal|false
decl_stmt|;
assert|assert
name|checkDeleteTerm
argument_list|(
literal|null
argument_list|)
assert|;
if|if
condition|(
name|deletes
operator|.
name|terms
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
comment|// This reader has no postings
return|return
literal|false
return|;
block|}
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
name|String
name|currentField
init|=
literal|null
decl_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Term
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|deletes
operator|.
name|terms
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Term
name|term
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|// Since we visit terms sorted, we gain performance
comment|// by re-using the same TermsEnum and seeking only
comment|// forwards
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|!=
name|currentField
condition|)
block|{
assert|assert
name|currentField
operator|==
literal|null
operator|||
name|currentField
operator|.
name|compareTo
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
operator|<
literal|0
assert|;
name|currentField
operator|=
name|term
operator|.
name|field
argument_list|()
expr_stmt|;
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|currentField
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|termsEnum
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|termsEnum
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
assert|assert
name|checkDeleteTerm
argument_list|(
name|term
argument_list|)
assert|;
if|if
condition|(
name|termsEnum
operator|.
name|seek
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
condition|)
block|{
name|DocsEnum
name|docsEnum
init|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|reader
operator|.
name|getDeletedDocs
argument_list|()
argument_list|,
name|docs
argument_list|)
decl_stmt|;
if|if
condition|(
name|docsEnum
operator|!=
literal|null
condition|)
block|{
name|docs
operator|=
name|docsEnum
expr_stmt|;
specifier|final
name|int
name|limit
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|docID
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
operator|||
name|docID
operator|>=
name|limit
condition|)
block|{
break|break;
block|}
name|reader
operator|.
name|deleteDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|any
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|// Delete by docID
for|for
control|(
name|Integer
name|docIdInt
range|:
name|deletes
operator|.
name|docIDs
control|)
block|{
name|int
name|docID
init|=
name|docIdInt
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|any
operator|=
literal|true
expr_stmt|;
block|}
comment|// Delete by query
if|if
condition|(
name|deletes
operator|.
name|queries
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|Entry
argument_list|<
name|Query
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|deletes
operator|.
name|queries
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Query
name|query
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|limit
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|Weight
name|weight
init|=
name|query
operator|.
name|weight
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|doc
init|=
name|scorer
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|>=
name|limit
condition|)
break|break;
name|reader
operator|.
name|deleteDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|any
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|any
return|;
block|}
DECL|method|getDeletes
specifier|public
specifier|synchronized
name|SegmentDeletes
name|getDeletes
parameter_list|(
name|SegmentInfo
name|info
parameter_list|)
block|{
name|SegmentDeletes
name|deletes
init|=
name|deletesMap
operator|.
name|get
argument_list|(
name|info
argument_list|)
decl_stmt|;
if|if
condition|(
name|deletes
operator|==
literal|null
condition|)
block|{
name|deletes
operator|=
operator|new
name|SegmentDeletes
argument_list|()
expr_stmt|;
name|deletesMap
operator|.
name|put
argument_list|(
name|info
argument_list|,
name|deletes
argument_list|)
expr_stmt|;
block|}
return|return
name|deletes
return|;
block|}
DECL|method|remove
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|)
block|{
assert|assert
name|infos
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
for|for
control|(
name|SegmentInfo
name|info
range|:
name|infos
control|)
block|{
name|SegmentDeletes
name|deletes
init|=
name|deletesMap
operator|.
name|get
argument_list|(
name|info
argument_list|)
decl_stmt|;
if|if
condition|(
name|deletes
operator|!=
literal|null
condition|)
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|deletes
operator|.
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|bytesUsed
operator|.
name|get
argument_list|()
operator|>=
literal|0
operator|:
literal|"bytesUsed="
operator|+
name|bytesUsed
assert|;
name|numTerms
operator|.
name|addAndGet
argument_list|(
operator|-
name|deletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|numTerms
operator|.
name|get
argument_list|()
operator|>=
literal|0
operator|:
literal|"numTerms="
operator|+
name|numTerms
assert|;
name|deletesMap
operator|.
name|remove
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// used only by assert
DECL|method|anyDeletes
specifier|private
name|boolean
name|anyDeletes
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|)
block|{
for|for
control|(
name|SegmentInfo
name|info
range|:
name|infos
control|)
block|{
if|if
condition|(
name|deletesMap
operator|.
name|containsKey
argument_list|(
name|info
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|// used only by assert
DECL|method|checkDeleteTerm
specifier|private
name|boolean
name|checkDeleteTerm
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
if|if
condition|(
name|term
operator|!=
literal|null
condition|)
block|{
assert|assert
name|lastDeleteTerm
operator|==
literal|null
operator|||
name|term
operator|.
name|compareTo
argument_list|(
name|lastDeleteTerm
argument_list|)
operator|>
literal|0
operator|:
literal|"lastTerm="
operator|+
name|lastDeleteTerm
operator|+
literal|" vs term="
operator|+
name|term
assert|;
block|}
name|lastDeleteTerm
operator|=
name|term
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// only for assert
DECL|method|checkDeleteStats
specifier|private
name|boolean
name|checkDeleteStats
parameter_list|()
block|{
name|int
name|numTerms2
init|=
literal|0
decl_stmt|;
name|long
name|bytesUsed2
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SegmentDeletes
name|deletes
range|:
name|deletesMap
operator|.
name|values
argument_list|()
control|)
block|{
name|numTerms2
operator|+=
name|deletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
expr_stmt|;
name|bytesUsed2
operator|+=
name|deletes
operator|.
name|bytesUsed
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
assert|assert
name|numTerms2
operator|==
name|numTerms
operator|.
name|get
argument_list|()
operator|:
literal|"numTerms2="
operator|+
name|numTerms2
operator|+
literal|" vs "
operator|+
name|numTerms
operator|.
name|get
argument_list|()
assert|;
assert|assert
name|bytesUsed2
operator|==
name|bytesUsed
operator|.
name|get
argument_list|()
operator|:
literal|"bytesUsed2="
operator|+
name|bytesUsed2
operator|+
literal|" vs "
operator|+
name|bytesUsed
assert|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
