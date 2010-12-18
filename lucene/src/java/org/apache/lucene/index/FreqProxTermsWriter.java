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
name|Collection
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
name|Comparator
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
name|codecs
operator|.
name|PostingsConsumer
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
name|codecs
operator|.
name|FieldsConsumer
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
name|codecs
operator|.
name|TermsConsumer
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
name|CollectionUtil
import|;
end_import
begin_class
DECL|class|FreqProxTermsWriter
specifier|final
class|class
name|FreqProxTermsWriter
extends|extends
name|TermsHashConsumer
block|{
annotation|@
name|Override
DECL|method|addThread
specifier|public
name|TermsHashConsumerPerThread
name|addThread
parameter_list|(
name|TermsHashPerThread
name|perThread
parameter_list|)
block|{
return|return
operator|new
name|FreqProxTermsWriterPerThread
argument_list|(
name|perThread
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{}
DECL|field|flushedDocCount
specifier|private
name|int
name|flushedDocCount
decl_stmt|;
comment|// TODO: would be nice to factor out more of this, eg the
comment|// FreqProxFieldMergeState, and code to visit all Fields
comment|// under the same FieldInfo together, up into TermsHash*.
comment|// Other writers would presumably share alot of this...
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|TermsHashConsumerPerThread
argument_list|,
name|Collection
argument_list|<
name|TermsHashConsumerPerField
argument_list|>
argument_list|>
name|threadsAndFields
parameter_list|,
specifier|final
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Gather all FieldData's that have postings, across all
comment|// ThreadStates
name|List
argument_list|<
name|FreqProxTermsWriterPerField
argument_list|>
name|allFields
init|=
operator|new
name|ArrayList
argument_list|<
name|FreqProxTermsWriterPerField
argument_list|>
argument_list|()
decl_stmt|;
name|flushedDocCount
operator|=
name|state
operator|.
name|numDocs
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|TermsHashConsumerPerThread
argument_list|,
name|Collection
argument_list|<
name|TermsHashConsumerPerField
argument_list|>
argument_list|>
name|entry
range|:
name|threadsAndFields
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Collection
argument_list|<
name|TermsHashConsumerPerField
argument_list|>
name|fields
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|TermsHashConsumerPerField
name|i
range|:
name|fields
control|)
block|{
specifier|final
name|FreqProxTermsWriterPerField
name|perField
init|=
operator|(
name|FreqProxTermsWriterPerField
operator|)
name|i
decl_stmt|;
if|if
condition|(
name|perField
operator|.
name|termsHashPerField
operator|.
name|bytesHash
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|allFields
operator|.
name|add
argument_list|(
name|perField
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|numAllFields
init|=
name|allFields
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// Sort by field name
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|allFields
argument_list|)
expr_stmt|;
specifier|final
name|FieldsConsumer
name|consumer
init|=
name|state
operator|.
name|segmentCodecs
operator|.
name|codec
argument_list|()
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
decl_stmt|;
comment|/*     Current writer chain:       FieldsConsumer         -> IMPL: FormatPostingsTermsDictWriter           -> TermsConsumer             -> IMPL: FormatPostingsTermsDictWriter.TermsWriter               -> DocsConsumer                 -> IMPL: FormatPostingsDocsWriter                   -> PositionsConsumer                     -> IMPL: FormatPostingsPositionsWriter     */
name|int
name|start
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|start
operator|<
name|numAllFields
condition|)
block|{
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|allFields
operator|.
name|get
argument_list|(
name|start
argument_list|)
operator|.
name|fieldInfo
decl_stmt|;
specifier|final
name|String
name|fieldName
init|=
name|fieldInfo
operator|.
name|name
decl_stmt|;
name|int
name|end
init|=
name|start
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|end
operator|<
name|numAllFields
operator|&&
name|allFields
operator|.
name|get
argument_list|(
name|end
argument_list|)
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
name|end
operator|++
expr_stmt|;
name|FreqProxTermsWriterPerField
index|[]
name|fields
init|=
operator|new
name|FreqProxTermsWriterPerField
index|[
name|end
operator|-
name|start
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|fields
index|[
name|i
operator|-
name|start
index|]
operator|=
name|allFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// Aggregate the storePayload as seen by the same
comment|// field across multiple threads
name|fieldInfo
operator|.
name|storePayloads
operator||=
name|fields
index|[
name|i
operator|-
name|start
index|]
operator|.
name|hasPayloads
expr_stmt|;
block|}
comment|// If this field has postings then add them to the
comment|// segment
name|appendPostings
argument_list|(
name|fields
argument_list|,
name|consumer
argument_list|)
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TermsHashPerField
name|perField
init|=
name|fields
index|[
name|i
index|]
operator|.
name|termsHashPerField
decl_stmt|;
name|int
name|numPostings
init|=
name|perField
operator|.
name|bytesHash
operator|.
name|size
argument_list|()
decl_stmt|;
name|perField
operator|.
name|reset
argument_list|()
expr_stmt|;
name|perField
operator|.
name|shrinkHash
argument_list|(
name|numPostings
argument_list|)
expr_stmt|;
name|fields
index|[
name|i
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|start
operator|=
name|end
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|TermsHashConsumerPerThread
argument_list|,
name|Collection
argument_list|<
name|TermsHashConsumerPerField
argument_list|>
argument_list|>
name|entry
range|:
name|threadsAndFields
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|FreqProxTermsWriterPerThread
name|perThread
init|=
operator|(
name|FreqProxTermsWriterPerThread
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|perThread
operator|.
name|termsHashPerThread
operator|.
name|reset
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|field|payload
name|BytesRef
name|payload
decl_stmt|;
comment|/* Walk through all unique text tokens (Posting    * instances) found in this field and serialize them    * into a single RAM segment. */
DECL|method|appendPostings
name|void
name|appendPostings
parameter_list|(
name|FreqProxTermsWriterPerField
index|[]
name|fields
parameter_list|,
name|FieldsConsumer
name|consumer
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|int
name|numFields
init|=
name|fields
operator|.
name|length
decl_stmt|;
specifier|final
name|BytesRef
name|text
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
specifier|final
name|FreqProxFieldMergeState
index|[]
name|mergeStates
init|=
operator|new
name|FreqProxFieldMergeState
index|[
name|numFields
index|]
decl_stmt|;
specifier|final
name|TermsConsumer
name|termsConsumer
init|=
name|consumer
operator|.
name|addField
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|fieldInfo
argument_list|)
decl_stmt|;
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
init|=
name|termsConsumer
operator|.
name|getComparator
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|FreqProxFieldMergeState
name|fms
init|=
name|mergeStates
index|[
name|i
index|]
operator|=
operator|new
name|FreqProxFieldMergeState
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|termComp
argument_list|)
decl_stmt|;
assert|assert
name|fms
operator|.
name|field
operator|.
name|fieldInfo
operator|==
name|fields
index|[
literal|0
index|]
operator|.
name|fieldInfo
assert|;
comment|// Should always be true
name|boolean
name|result
init|=
name|fms
operator|.
name|nextTerm
argument_list|()
decl_stmt|;
assert|assert
name|result
assert|;
block|}
name|FreqProxFieldMergeState
index|[]
name|termStates
init|=
operator|new
name|FreqProxFieldMergeState
index|[
name|numFields
index|]
decl_stmt|;
specifier|final
name|boolean
name|currentFieldOmitTermFreqAndPositions
init|=
name|fields
index|[
literal|0
index|]
operator|.
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
decl_stmt|;
comment|//System.out.println("flush terms field=" + fields[0].fieldInfo.name);
comment|// TODO: really TermsHashPerField should take over most
comment|// of this loop, including merge sort of terms from
comment|// multiple threads and interacting with the
comment|// TermsConsumer, only calling out to us (passing us the
comment|// DocsConsumer) to handle delivery of docs/positions
while|while
condition|(
name|numFields
operator|>
literal|0
condition|)
block|{
comment|// Get the next term to merge
name|termStates
index|[
literal|0
index|]
operator|=
name|mergeStates
index|[
literal|0
index|]
expr_stmt|;
name|int
name|numToMerge
init|=
literal|1
decl_stmt|;
comment|// TODO: pqueue
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numFields
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|cmp
init|=
name|termComp
operator|.
name|compare
argument_list|(
name|mergeStates
index|[
name|i
index|]
operator|.
name|text
argument_list|,
name|termStates
index|[
literal|0
index|]
operator|.
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|termStates
index|[
literal|0
index|]
operator|=
name|mergeStates
index|[
name|i
index|]
expr_stmt|;
name|numToMerge
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|termStates
index|[
name|numToMerge
operator|++
index|]
operator|=
name|mergeStates
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
comment|// Need shallow copy here because termStates[0].text
comment|// changes by the time we call finishTerm
name|text
operator|.
name|bytes
operator|=
name|termStates
index|[
literal|0
index|]
operator|.
name|text
operator|.
name|bytes
expr_stmt|;
name|text
operator|.
name|offset
operator|=
name|termStates
index|[
literal|0
index|]
operator|.
name|text
operator|.
name|offset
expr_stmt|;
name|text
operator|.
name|length
operator|=
name|termStates
index|[
literal|0
index|]
operator|.
name|text
operator|.
name|length
expr_stmt|;
comment|//System.out.println("  term=" + text.toUnicodeString());
comment|//System.out.println("  term=" + text.toString());
specifier|final
name|PostingsConsumer
name|postingsConsumer
init|=
name|termsConsumer
operator|.
name|startTerm
argument_list|(
name|text
argument_list|)
decl_stmt|;
comment|// Now termStates has numToMerge FieldMergeStates
comment|// which all share the same term.  Now we must
comment|// interleave the docID streams.
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|numToMerge
operator|>
literal|0
condition|)
block|{
name|FreqProxFieldMergeState
name|minState
init|=
name|termStates
index|[
literal|0
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
name|numToMerge
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|termStates
index|[
name|i
index|]
operator|.
name|docID
operator|<
name|minState
operator|.
name|docID
condition|)
block|{
name|minState
operator|=
name|termStates
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|termDocFreq
init|=
name|minState
operator|.
name|termFreq
decl_stmt|;
name|numDocs
operator|++
expr_stmt|;
assert|assert
name|minState
operator|.
name|docID
operator|<
name|flushedDocCount
operator|:
literal|"doc="
operator|+
name|minState
operator|.
name|docID
operator|+
literal|" maxDoc="
operator|+
name|flushedDocCount
assert|;
name|postingsConsumer
operator|.
name|startDoc
argument_list|(
name|minState
operator|.
name|docID
argument_list|,
name|termDocFreq
argument_list|)
expr_stmt|;
specifier|final
name|ByteSliceReader
name|prox
init|=
name|minState
operator|.
name|prox
decl_stmt|;
comment|// Carefully copy over the prox + payload info,
comment|// changing the format to match Lucene's segment
comment|// format.
if|if
condition|(
operator|!
name|currentFieldOmitTermFreqAndPositions
condition|)
block|{
comment|// omitTermFreqAndPositions == false so we do write positions&
comment|// payload
name|int
name|position
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|termDocFreq
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|code
init|=
name|prox
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|position
operator|+=
name|code
operator|>>
literal|1
expr_stmt|;
comment|//System.out.println("    pos=" + position);
specifier|final
name|int
name|payloadLength
decl_stmt|;
specifier|final
name|BytesRef
name|thisPayload
decl_stmt|;
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// This position has a payload
name|payloadLength
operator|=
name|prox
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|payload
operator|==
literal|null
condition|)
block|{
name|payload
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|payload
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|payloadLength
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|payload
operator|.
name|bytes
operator|.
name|length
operator|<
name|payloadLength
condition|)
block|{
name|payload
operator|.
name|grow
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
name|prox
operator|.
name|readBytes
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|payloadLength
argument_list|)
expr_stmt|;
name|payload
operator|.
name|length
operator|=
name|payloadLength
expr_stmt|;
name|thisPayload
operator|=
name|payload
expr_stmt|;
block|}
else|else
block|{
name|payloadLength
operator|=
literal|0
expr_stmt|;
name|thisPayload
operator|=
literal|null
expr_stmt|;
block|}
name|postingsConsumer
operator|.
name|addPosition
argument_list|(
name|position
argument_list|,
name|thisPayload
argument_list|)
expr_stmt|;
block|}
comment|//End for
name|postingsConsumer
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|minState
operator|.
name|nextDoc
argument_list|()
condition|)
block|{
comment|// Remove from termStates
name|int
name|upto
init|=
literal|0
decl_stmt|;
comment|// TODO: inefficient O(N) where N = number of
comment|// threads that had seen this term:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numToMerge
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|termStates
index|[
name|i
index|]
operator|!=
name|minState
condition|)
block|{
name|termStates
index|[
name|upto
operator|++
index|]
operator|=
name|termStates
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
name|numToMerge
operator|--
expr_stmt|;
assert|assert
name|upto
operator|==
name|numToMerge
assert|;
comment|// Advance this state to the next term
if|if
condition|(
operator|!
name|minState
operator|.
name|nextTerm
argument_list|()
condition|)
block|{
comment|// OK, no more terms, so remove from mergeStates
comment|// as well
name|upto
operator|=
literal|0
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
name|numFields
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|mergeStates
index|[
name|i
index|]
operator|!=
name|minState
condition|)
name|mergeStates
index|[
name|upto
operator|++
index|]
operator|=
name|mergeStates
index|[
name|i
index|]
expr_stmt|;
name|numFields
operator|--
expr_stmt|;
assert|assert
name|upto
operator|==
name|numFields
assert|;
block|}
block|}
block|}
assert|assert
name|numDocs
operator|>
literal|0
assert|;
name|termsConsumer
operator|.
name|finishTerm
argument_list|(
name|text
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
block|}
name|termsConsumer
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
