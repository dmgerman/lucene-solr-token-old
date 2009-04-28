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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
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
name|FlagsAttribute
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
name|PayloadAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermAttribute
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
name|TypeAttribute
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
name|Attribute
import|;
end_import
begin_comment
comment|/** This is a DocFieldConsumer that inverts each field,  *  separately, from a Document, and accepts a  *  InvertedTermsConsumer to process those terms. */
end_comment
begin_class
DECL|class|DocInverterPerThread
specifier|final
class|class
name|DocInverterPerThread
extends|extends
name|DocFieldConsumerPerThread
block|{
DECL|field|docInverter
specifier|final
name|DocInverter
name|docInverter
decl_stmt|;
DECL|field|consumer
specifier|final
name|InvertedDocConsumerPerThread
name|consumer
decl_stmt|;
DECL|field|endConsumer
specifier|final
name|InvertedDocEndConsumerPerThread
name|endConsumer
decl_stmt|;
DECL|field|localToken
specifier|final
name|Token
name|localToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
comment|//TODO: change to SingleTokenTokenStream after Token was removed
DECL|field|singleTokenTokenStream
specifier|final
name|SingleTokenTokenStream
name|singleTokenTokenStream
init|=
operator|new
name|SingleTokenTokenStream
argument_list|()
decl_stmt|;
DECL|field|localTokenStream
specifier|final
name|BackwardsCompatibilityStream
name|localTokenStream
init|=
operator|new
name|BackwardsCompatibilityStream
argument_list|()
decl_stmt|;
DECL|class|SingleTokenTokenStream
specifier|static
class|class
name|SingleTokenTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|termAttribute
name|TermAttribute
name|termAttribute
decl_stmt|;
DECL|field|offsetAttribute
name|OffsetAttribute
name|offsetAttribute
decl_stmt|;
DECL|method|SingleTokenTokenStream
name|SingleTokenTokenStream
parameter_list|()
block|{
name|termAttribute
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|=
operator|(
name|OffsetAttribute
operator|)
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|reinit
specifier|public
name|void
name|reinit
parameter_list|(
name|String
name|stringValue
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
name|termAttribute
operator|.
name|setTermBuffer
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** This stream wrapper is only used to maintain backwards compatibility with the    *  old TokenStream API and can be removed in Lucene 3.0    * @deprecated     */
DECL|class|BackwardsCompatibilityStream
specifier|static
class|class
name|BackwardsCompatibilityStream
extends|extends
name|TokenStream
block|{
DECL|field|token
specifier|private
name|Token
name|token
decl_stmt|;
DECL|field|termAttribute
name|TermAttribute
name|termAttribute
init|=
operator|new
name|TermAttribute
argument_list|()
block|{
specifier|public
name|String
name|term
parameter_list|()
block|{
return|return
name|token
operator|.
name|term
argument_list|()
return|;
block|}
specifier|public
name|char
index|[]
name|termBuffer
parameter_list|()
block|{
return|return
name|token
operator|.
name|termBuffer
argument_list|()
return|;
block|}
specifier|public
name|int
name|termLength
parameter_list|()
block|{
return|return
name|token
operator|.
name|termLength
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|offsetAttribute
name|OffsetAttribute
name|offsetAttribute
init|=
operator|new
name|OffsetAttribute
argument_list|()
block|{
specifier|public
name|int
name|startOffset
parameter_list|()
block|{
return|return
name|token
operator|.
name|startOffset
argument_list|()
return|;
block|}
specifier|public
name|int
name|endOffset
parameter_list|()
block|{
return|return
name|token
operator|.
name|endOffset
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|positionIncrementAttribute
name|PositionIncrementAttribute
name|positionIncrementAttribute
init|=
operator|new
name|PositionIncrementAttribute
argument_list|()
block|{
specifier|public
name|int
name|getPositionIncrement
parameter_list|()
block|{
return|return
name|token
operator|.
name|getPositionIncrement
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|flagsAttribute
name|FlagsAttribute
name|flagsAttribute
init|=
operator|new
name|FlagsAttribute
argument_list|()
block|{
specifier|public
name|int
name|getFlags
parameter_list|()
block|{
return|return
name|token
operator|.
name|getFlags
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|payloadAttribute
name|PayloadAttribute
name|payloadAttribute
init|=
operator|new
name|PayloadAttribute
argument_list|()
block|{
specifier|public
name|Payload
name|getPayload
parameter_list|()
block|{
return|return
name|token
operator|.
name|getPayload
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|typeAttribute
name|TypeAttribute
name|typeAttribute
init|=
operator|new
name|TypeAttribute
argument_list|()
block|{
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|token
operator|.
name|type
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|method|BackwardsCompatibilityStream
name|BackwardsCompatibilityStream
parameter_list|()
block|{
name|attributes
operator|.
name|put
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|,
name|termAttribute
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|,
name|offsetAttribute
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|,
name|positionIncrementAttribute
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|,
name|flagsAttribute
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|,
name|payloadAttribute
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|,
name|typeAttribute
argument_list|)
expr_stmt|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
block|}
block|}
empty_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriter
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|fieldState
specifier|final
name|FieldInvertState
name|fieldState
init|=
operator|new
name|FieldInvertState
argument_list|()
decl_stmt|;
comment|// Used to read a string value for a field
DECL|field|stringReader
specifier|final
name|ReusableStringReader
name|stringReader
init|=
operator|new
name|ReusableStringReader
argument_list|()
decl_stmt|;
DECL|method|DocInverterPerThread
specifier|public
name|DocInverterPerThread
parameter_list|(
name|DocFieldProcessorPerThread
name|docFieldProcessorPerThread
parameter_list|,
name|DocInverter
name|docInverter
parameter_list|)
block|{
name|this
operator|.
name|docInverter
operator|=
name|docInverter
expr_stmt|;
name|docState
operator|=
name|docFieldProcessorPerThread
operator|.
name|docState
expr_stmt|;
name|consumer
operator|=
name|docInverter
operator|.
name|consumer
operator|.
name|addThread
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|endConsumer
operator|=
name|docInverter
operator|.
name|endConsumer
operator|.
name|addThread
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|endConsumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
DECL|method|finishDocument
specifier|public
name|DocumentsWriter
operator|.
name|DocWriter
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: allow endConsumer.finishDocument to also return
comment|// a DocWriter
name|endConsumer
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
return|return
name|consumer
operator|.
name|finishDocument
argument_list|()
return|;
block|}
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
DECL|method|addField
specifier|public
name|DocFieldConsumerPerField
name|addField
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{
return|return
operator|new
name|DocInverterPerField
argument_list|(
name|this
argument_list|,
name|fi
argument_list|)
return|;
block|}
block|}
end_class
end_unit
