begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|index
operator|.
name|MultiPostingsEnum
operator|.
name|EnumWithSlice
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
begin_comment
comment|/**  * Exposes flex API, merged from flex API of sub-segments,  * remapping docIDs (this is used for segment merging).  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|MappingMultiPostingsEnum
specifier|final
class|class
name|MappingMultiPostingsEnum
extends|extends
name|PostingsEnum
block|{
DECL|field|subs
specifier|private
name|MultiPostingsEnum
operator|.
name|EnumWithSlice
index|[]
name|subs
decl_stmt|;
DECL|field|numSubs
name|int
name|numSubs
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|currentMap
name|MergeState
operator|.
name|DocMap
name|currentMap
decl_stmt|;
DECL|field|current
name|PostingsEnum
name|current
decl_stmt|;
DECL|field|currentBase
name|int
name|currentBase
decl_stmt|;
DECL|field|doc
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|mergeState
specifier|private
name|MergeState
name|mergeState
decl_stmt|;
DECL|field|multiDocsAndPositionsEnum
name|MultiPostingsEnum
name|multiDocsAndPositionsEnum
decl_stmt|;
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|MappingMultiPostingsEnum
specifier|public
name|MappingMultiPostingsEnum
parameter_list|(
name|String
name|field
parameter_list|,
name|MergeState
name|mergeState
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|mergeState
operator|=
name|mergeState
expr_stmt|;
block|}
DECL|method|reset
name|MappingMultiPostingsEnum
name|reset
parameter_list|(
name|MultiPostingsEnum
name|postingsEnum
parameter_list|)
block|{
name|this
operator|.
name|numSubs
operator|=
name|postingsEnum
operator|.
name|getNumSubs
argument_list|()
expr_stmt|;
name|this
operator|.
name|subs
operator|=
name|postingsEnum
operator|.
name|getSubs
argument_list|()
expr_stmt|;
name|upto
operator|=
operator|-
literal|1
expr_stmt|;
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
name|current
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|multiDocsAndPositionsEnum
operator|=
name|postingsEnum
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** How many sub-readers we are merging.    *  @see #getSubs */
DECL|method|getNumSubs
specifier|public
name|int
name|getNumSubs
parameter_list|()
block|{
return|return
name|numSubs
return|;
block|}
comment|/** Returns sub-readers we are merging. */
DECL|method|getSubs
specifier|public
name|EnumWithSlice
index|[]
name|getSubs
parameter_list|()
block|{
return|return
name|subs
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|upto
operator|==
name|numSubs
operator|-
literal|1
condition|)
block|{
return|return
name|this
operator|.
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|upto
operator|++
expr_stmt|;
specifier|final
name|int
name|reader
init|=
name|subs
index|[
name|upto
index|]
operator|.
name|slice
operator|.
name|readerIndex
decl_stmt|;
name|current
operator|=
name|subs
index|[
name|upto
index|]
operator|.
name|postingsEnum
expr_stmt|;
name|currentBase
operator|=
name|mergeState
operator|.
name|docBase
index|[
name|reader
index|]
expr_stmt|;
name|currentMap
operator|=
name|mergeState
operator|.
name|docMaps
index|[
name|reader
index|]
expr_stmt|;
block|}
block|}
name|int
name|doc
init|=
name|current
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
comment|// compact deletions
name|doc
operator|=
name|currentMap
operator|.
name|get
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
operator|-
literal|1
condition|)
block|{
continue|continue;
block|}
return|return
name|this
operator|.
name|doc
operator|=
name|currentBase
operator|+
name|doc
return|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|pos
init|=
name|current
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"position="
operator|+
name|pos
operator|+
literal|" is negative, field=\""
operator|+
name|field
operator|+
literal|" doc="
operator|+
name|doc
argument_list|,
name|mergeState
operator|.
name|fieldsProducers
index|[
name|upto
index|]
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|>
name|IndexWriter
operator|.
name|MAX_POSITION
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"position="
operator|+
name|pos
operator|+
literal|" is too large (> IndexWriter.MAX_POSITION="
operator|+
name|IndexWriter
operator|.
name|MAX_POSITION
operator|+
literal|"), field=\""
operator|+
name|field
operator|+
literal|"\" doc="
operator|+
name|doc
argument_list|,
name|mergeState
operator|.
name|fieldsProducers
index|[
name|upto
index|]
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|startOffset
specifier|public
name|int
name|startOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|startOffset
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|endOffset
specifier|public
name|int
name|endOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|endOffset
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|getPayload
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
name|long
name|cost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|EnumWithSlice
name|enumWithSlice
range|:
name|subs
control|)
block|{
name|cost
operator|+=
name|enumWithSlice
operator|.
name|postingsEnum
operator|.
name|cost
argument_list|()
expr_stmt|;
block|}
return|return
name|cost
return|;
block|}
block|}
end_class
end_unit
