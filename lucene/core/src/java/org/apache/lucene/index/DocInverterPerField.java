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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
operator|.
name|IndexOptions
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
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * Holds state for inverting all occurrences of a single  * field in the document.  This class doesn't do anything  * itself; instead, it forwards the tokens produced by  * analysis to its own consumer  * (InvertedDocConsumerPerField).  It also interacts with an  * endConsumer (InvertedDocEndConsumerPerField).  */
end_comment
begin_class
DECL|class|DocInverterPerField
specifier|final
class|class
name|DocInverterPerField
extends|extends
name|DocFieldConsumerPerField
block|{
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|consumer
specifier|final
name|InvertedDocConsumerPerField
name|consumer
decl_stmt|;
DECL|field|endConsumer
specifier|final
name|InvertedDocEndConsumerPerField
name|endConsumer
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|fieldState
specifier|final
name|FieldInvertState
name|fieldState
decl_stmt|;
DECL|method|DocInverterPerField
specifier|public
name|DocInverterPerField
parameter_list|(
name|DocInverter
name|parent
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|docState
operator|=
name|parent
operator|.
name|docState
expr_stmt|;
name|fieldState
operator|=
operator|new
name|FieldInvertState
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|parent
operator|.
name|consumer
operator|.
name|addField
argument_list|(
name|this
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|endConsumer
operator|=
name|parent
operator|.
name|endConsumer
operator|.
name|addField
argument_list|(
name|this
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|endConsumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|processFields
specifier|public
name|void
name|processFields
parameter_list|(
specifier|final
name|IndexableField
index|[]
name|fields
parameter_list|,
specifier|final
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldState
operator|.
name|reset
argument_list|()
expr_stmt|;
specifier|final
name|boolean
name|doInvert
init|=
name|consumer
operator|.
name|start
argument_list|(
name|fields
argument_list|,
name|count
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|IndexableField
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|IndexableFieldType
name|fieldType
init|=
name|field
operator|.
name|fieldType
argument_list|()
decl_stmt|;
comment|// TODO FI: this should be "genericized" to querying
comment|// consumer if it wants to see this particular field
comment|// tokenized.
if|if
condition|(
name|doInvert
condition|)
block|{
specifier|final
name|boolean
name|analyzed
init|=
name|fieldType
operator|.
name|tokenized
argument_list|()
operator|&&
name|docState
operator|.
name|analyzer
operator|!=
literal|null
decl_stmt|;
comment|// if the field omits norms, the boost cannot be indexed.
if|if
condition|(
name|fieldType
operator|.
name|omitNorms
argument_list|()
operator|&&
name|field
operator|.
name|boost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"You cannot set an index-time boost: norms are omitted for field '"
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|// only bother checking offsets if something will consume them.
comment|// TODO: after we fix analyzers, also check if termVectorOffsets will be indexed.
specifier|final
name|boolean
name|checkOffsets
init|=
name|fieldType
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
decl_stmt|;
name|int
name|lastStartOffset
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|fieldState
operator|.
name|position
operator|+=
name|analyzed
condition|?
name|docState
operator|.
name|analyzer
operator|.
name|getPositionIncrementGap
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
else|:
literal|0
expr_stmt|;
block|}
specifier|final
name|TokenStream
name|stream
init|=
name|field
operator|.
name|tokenStream
argument_list|(
name|docState
operator|.
name|analyzer
argument_list|)
decl_stmt|;
comment|// reset the TokenStream to the first token
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|boolean
name|success2
init|=
literal|false
decl_stmt|;
try|try
block|{
name|boolean
name|hasMoreTokens
init|=
name|stream
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
name|fieldState
operator|.
name|attributeSource
operator|=
name|stream
expr_stmt|;
name|OffsetAttribute
name|offsetAttribute
init|=
name|fieldState
operator|.
name|attributeSource
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAttribute
init|=
name|fieldState
operator|.
name|attributeSource
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasMoreTokens
condition|)
block|{
name|consumer
operator|.
name|start
argument_list|(
name|field
argument_list|)
expr_stmt|;
do|do
block|{
comment|// If we hit an exception in stream.next below
comment|// (which is fairly common, eg if analyzer
comment|// chokes on a given document), then it's
comment|// non-aborting and (above) this one document
comment|// will be marked as deleted, but still
comment|// consume a docID
specifier|final
name|int
name|posIncr
init|=
name|posIncrAttribute
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|posIncr
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"position increment must be>=0 (got "
operator|+
name|posIncr
operator|+
literal|") for field '"
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|fieldState
operator|.
name|position
operator|==
literal|0
operator|&&
name|posIncr
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"first position increment must be> 0 (got 0) for field '"
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|int
name|position
init|=
name|fieldState
operator|.
name|position
operator|+
name|posIncr
decl_stmt|;
if|if
condition|(
name|position
operator|>
literal|0
condition|)
block|{
comment|// NOTE: confusing: this "mirrors" the
comment|// position++ we do below
name|position
operator|--
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|position
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"position overflow for field '"
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|// position is legal, we can safely place it in fieldState now.
comment|// not sure if anything will use fieldState after non-aborting exc...
name|fieldState
operator|.
name|position
operator|=
name|position
expr_stmt|;
if|if
condition|(
name|posIncr
operator|==
literal|0
condition|)
name|fieldState
operator|.
name|numOverlap
operator|++
expr_stmt|;
if|if
condition|(
name|checkOffsets
condition|)
block|{
name|int
name|startOffset
init|=
name|fieldState
operator|.
name|offset
operator|+
name|offsetAttribute
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|endOffset
init|=
name|fieldState
operator|.
name|offset
operator|+
name|offsetAttribute
operator|.
name|endOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|startOffset
operator|<
literal|0
operator|||
name|endOffset
operator|<
name|startOffset
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"startOffset must be non-negative, and endOffset must be>= startOffset, "
operator|+
literal|"startOffset="
operator|+
name|startOffset
operator|+
literal|",endOffset="
operator|+
name|endOffset
operator|+
literal|" for field '"
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|startOffset
operator|<
name|lastStartOffset
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"offsets must not go backwards startOffset="
operator|+
name|startOffset
operator|+
literal|" is< lastStartOffset="
operator|+
name|lastStartOffset
operator|+
literal|" for field '"
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|lastStartOffset
operator|=
name|startOffset
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// If we hit an exception in here, we abort
comment|// all buffered documents since the last
comment|// flush, on the likelihood that the
comment|// internal state of the consumer is now
comment|// corrupt and should not be flushed to a
comment|// new segment:
name|consumer
operator|.
name|add
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|docState
operator|.
name|docWriter
operator|.
name|setAborting
argument_list|()
expr_stmt|;
block|}
block|}
name|fieldState
operator|.
name|length
operator|++
expr_stmt|;
name|fieldState
operator|.
name|position
operator|++
expr_stmt|;
block|}
do|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
do|;
block|}
comment|// trigger streams to perform end-of-stream operations
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
comment|// TODO: maybe add some safety? then again, its already checked
comment|// when we come back around to the field...
name|fieldState
operator|.
name|position
operator|+=
name|posIncrAttribute
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
name|fieldState
operator|.
name|offset
operator|+=
name|offsetAttribute
operator|.
name|endOffset
argument_list|()
expr_stmt|;
name|success2
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success2
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|fieldState
operator|.
name|offset
operator|+=
name|analyzed
condition|?
name|docState
operator|.
name|analyzer
operator|.
name|getOffsetGap
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
else|:
literal|0
expr_stmt|;
name|fieldState
operator|.
name|boost
operator|*=
name|field
operator|.
name|boost
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-2387: don't hang onto the field, so GC can
comment|// reclaim
name|fields
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
name|consumer
operator|.
name|finish
argument_list|()
expr_stmt|;
name|endConsumer
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfo
name|FieldInfo
name|getFieldInfo
parameter_list|()
block|{
return|return
name|fieldInfo
return|;
block|}
block|}
end_class
end_unit
