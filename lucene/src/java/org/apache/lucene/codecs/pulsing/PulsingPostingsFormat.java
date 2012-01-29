begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.pulsing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|pulsing
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
name|Set
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
name|codecs
operator|.
name|BlockTreeTermsReader
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
name|codecs
operator|.
name|BlockTreeTermsWriter
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
name|codecs
operator|.
name|FieldsProducer
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
name|codecs
operator|.
name|PostingsBaseFormat
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
name|codecs
operator|.
name|PostingsFormat
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
name|codecs
operator|.
name|PostingsReaderBase
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
name|codecs
operator|.
name|PostingsWriterBase
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
name|SegmentInfo
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
name|SegmentReadState
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
name|SegmentWriteState
import|;
end_import
begin_comment
comment|/** This postings format "inlines" the postings for terms that have  *  low docFreq.  It wraps another postings format, which is used for  *  writing the non-inlined terms.  *  *  @lucene.experimental */
end_comment
begin_class
DECL|class|PulsingPostingsFormat
specifier|public
specifier|abstract
class|class
name|PulsingPostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|freqCutoff
specifier|private
specifier|final
name|int
name|freqCutoff
decl_stmt|;
DECL|field|minBlockSize
specifier|private
specifier|final
name|int
name|minBlockSize
decl_stmt|;
DECL|field|maxBlockSize
specifier|private
specifier|final
name|int
name|maxBlockSize
decl_stmt|;
DECL|field|wrappedPostingsBaseFormat
specifier|private
specifier|final
name|PostingsBaseFormat
name|wrappedPostingsBaseFormat
decl_stmt|;
DECL|method|PulsingPostingsFormat
specifier|public
name|PulsingPostingsFormat
parameter_list|(
name|String
name|name
parameter_list|,
name|PostingsBaseFormat
name|wrappedPostingsBaseFormat
parameter_list|,
name|int
name|freqCutoff
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|wrappedPostingsBaseFormat
argument_list|,
name|freqCutoff
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MIN_BLOCK_SIZE
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MAX_BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/** Terms with freq<= freqCutoff are inlined into terms    *  dict. */
DECL|method|PulsingPostingsFormat
specifier|public
name|PulsingPostingsFormat
parameter_list|(
name|String
name|name
parameter_list|,
name|PostingsBaseFormat
name|wrappedPostingsBaseFormat
parameter_list|,
name|int
name|freqCutoff
parameter_list|,
name|int
name|minBlockSize
parameter_list|,
name|int
name|maxBlockSize
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|freqCutoff
operator|=
name|freqCutoff
expr_stmt|;
name|this
operator|.
name|minBlockSize
operator|=
name|minBlockSize
expr_stmt|;
assert|assert
name|minBlockSize
operator|>
literal|1
assert|;
name|this
operator|.
name|maxBlockSize
operator|=
name|maxBlockSize
expr_stmt|;
name|this
operator|.
name|wrappedPostingsBaseFormat
operator|=
name|wrappedPostingsBaseFormat
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|+
literal|"(freqCutoff="
operator|+
name|freqCutoff
operator|+
literal|" minBlockSize="
operator|+
name|minBlockSize
operator|+
literal|" maxBlockSize="
operator|+
name|maxBlockSize
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|PostingsWriterBase
name|docsWriter
init|=
name|wrappedPostingsBaseFormat
operator|.
name|postingsWriterBase
argument_list|(
name|state
argument_list|)
decl_stmt|;
comment|// Terms that have<= freqCutoff number of docs are
comment|// "pulsed" (inlined):
name|PostingsWriterBase
name|pulsingWriter
init|=
operator|new
name|PulsingPostingsWriter
argument_list|(
name|freqCutoff
argument_list|,
name|docsWriter
argument_list|)
decl_stmt|;
comment|// Terms dict
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FieldsConsumer
name|ret
init|=
operator|new
name|BlockTreeTermsWriter
argument_list|(
name|state
argument_list|,
name|pulsingWriter
argument_list|,
name|minBlockSize
argument_list|,
name|maxBlockSize
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|pulsingWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|PostingsReaderBase
name|docsReader
init|=
name|wrappedPostingsBaseFormat
operator|.
name|postingsReaderBase
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|PostingsReaderBase
name|pulsingReader
init|=
operator|new
name|PulsingPostingsReader
argument_list|(
name|docsReader
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FieldsProducer
name|ret
init|=
operator|new
name|BlockTreeTermsReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|pulsingReader
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|state
operator|.
name|termsIndexDivisor
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|pulsingReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getFreqCutoff
specifier|public
name|int
name|getFreqCutoff
parameter_list|()
block|{
return|return
name|freqCutoff
return|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|wrappedPostingsBaseFormat
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|BlockTreeTermsReader
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
