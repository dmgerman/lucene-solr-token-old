begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.idversion
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|idversion
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
name|codecs
operator|.
name|blocktree
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
name|LiveFieldValues
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
name|IOUtils
import|;
end_import
begin_comment
comment|/** A PostingsFormat optimized for primary-key (ID) fields that also  *  record a version (long) for each ID, delivered as a payload  *  created by {@link #longToBytes} during indexing.  At search time,  *  the TermsEnum implementation {@link IDVersionSegmentTermsEnum}  *  enables fast (using only the terms index when possible) lookup for  *  whether a given ID was previously indexed with version&gt; N (see  *  {@link IDVersionSegmentTermsEnum#seekExact(BytesRef,long)}.  *  *<p>This is most effective if the app assigns monotonically  *  increasing global version to each indexed doc.  Then, during  *  indexing, use {@link  *  IDVersionSegmentTermsEnum#seekExact(BytesRef,long)} (along with  *  {@link LiveFieldValues}) to decide whether the document you are  *  about to index was already indexed with a higher version, and skip  *  it if so.  *  *<p>The field is effectively indexed as DOCS_ONLY and the docID is  *  pulsed into the terms dictionary, but the user must feed in the  *  version as a payload on the first token.  *  *<p>NOTE: term vectors cannot be indexed with this field (not that  *  you should really ever want to do this).  *  *  @lucene.experimental */
end_comment
begin_class
DECL|class|IDVersionPostingsFormat
specifier|public
class|class
name|IDVersionPostingsFormat
extends|extends
name|PostingsFormat
block|{
comment|/** version must be&gt;= this. */
DECL|field|MIN_VERSION
specifier|public
specifier|static
specifier|final
name|long
name|MIN_VERSION
init|=
literal|0
decl_stmt|;
comment|// TODO: we could delta encode instead, and keep the last bit:
comment|/** version must be&lt;= this, because we encode with ZigZag. */
DECL|field|MAX_VERSION
specifier|public
specifier|static
specifier|final
name|long
name|MAX_VERSION
init|=
literal|0x3fffffffffffffffL
decl_stmt|;
DECL|field|minTermsInBlock
specifier|private
specifier|final
name|int
name|minTermsInBlock
decl_stmt|;
DECL|field|maxTermsInBlock
specifier|private
specifier|final
name|int
name|maxTermsInBlock
decl_stmt|;
DECL|method|IDVersionPostingsFormat
specifier|public
name|IDVersionPostingsFormat
parameter_list|()
block|{
name|this
argument_list|(
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
DECL|method|IDVersionPostingsFormat
specifier|public
name|IDVersionPostingsFormat
parameter_list|(
name|int
name|minTermsInBlock
parameter_list|,
name|int
name|maxTermsInBlock
parameter_list|)
block|{
name|super
argument_list|(
literal|"IDVersion"
argument_list|)
expr_stmt|;
name|this
operator|.
name|minTermsInBlock
operator|=
name|minTermsInBlock
expr_stmt|;
name|this
operator|.
name|maxTermsInBlock
operator|=
name|maxTermsInBlock
expr_stmt|;
name|BlockTreeTermsWriter
operator|.
name|validateSettings
argument_list|(
name|minTermsInBlock
argument_list|,
name|maxTermsInBlock
argument_list|)
expr_stmt|;
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
name|postingsWriter
init|=
operator|new
name|IDVersionPostingsWriter
argument_list|(
name|state
operator|.
name|liveDocs
argument_list|)
decl_stmt|;
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
name|VersionBlockTreeTermsWriter
argument_list|(
name|state
argument_list|,
name|postingsWriter
argument_list|,
name|minTermsInBlock
argument_list|,
name|maxTermsInBlock
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|postingsWriter
argument_list|)
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
name|postingsReader
init|=
operator|new
name|IDVersionPostingsReader
argument_list|()
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
name|VersionBlockTreeTermsReader
argument_list|(
name|postingsReader
argument_list|,
name|state
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|postingsReader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|bytesToLong
specifier|public
specifier|static
name|long
name|bytesToLong
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
return|return
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
index|]
operator|&
literal|0xFFL
operator|)
operator|<<
literal|56
operator|)
operator||
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xFFL
operator|)
operator|<<
literal|48
operator|)
operator||
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xFFL
operator|)
operator|<<
literal|40
operator|)
operator||
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|+
literal|3
index|]
operator|&
literal|0xFFL
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|+
literal|4
index|]
operator|&
literal|0xFFL
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|+
literal|5
index|]
operator|&
literal|0xFFL
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|+
literal|6
index|]
operator|&
literal|0xFFL
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|+
literal|7
index|]
operator|&
literal|0xFFL
operator|)
return|;
block|}
DECL|method|longToBytes
specifier|public
specifier|static
name|void
name|longToBytes
parameter_list|(
name|long
name|v
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|v
operator|>
name|MAX_VERSION
operator|||
name|v
operator|<
name|MIN_VERSION
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"version must be>= MIN_VERSION="
operator|+
name|MIN_VERSION
operator|+
literal|" and<= MAX_VERSION="
operator|+
name|MAX_VERSION
operator|+
literal|" (got: "
operator|+
name|v
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|bytes
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
literal|8
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|56
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|48
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|40
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|32
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
literal|4
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
literal|5
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
literal|6
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
literal|7
index|]
operator|=
operator|(
name|byte
operator|)
name|v
expr_stmt|;
assert|assert
name|bytesToLong
argument_list|(
name|bytes
argument_list|)
operator|==
name|v
operator|:
name|bytesToLong
argument_list|(
name|bytes
argument_list|)
operator|+
literal|" vs "
operator|+
name|v
operator|+
literal|" bytes="
operator|+
name|bytes
assert|;
block|}
block|}
end_class
end_unit
