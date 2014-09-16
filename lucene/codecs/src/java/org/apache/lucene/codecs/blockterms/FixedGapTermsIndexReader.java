begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.blockterms
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blockterms
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Directory
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
name|IOContext
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
name|IndexInput
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
name|CodecUtil
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
name|CorruptIndexException
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
name|FieldInfos
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
name|Accountable
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
name|Accountables
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
name|PagedBytes
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
name|MonotonicBlockPackedReader
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
name|HashMap
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
name|IndexFileNames
import|;
end_import
begin_comment
comment|/**   * TermsIndexReader for simple every Nth terms indexes.  *  * @see FixedGapTermsIndexWriter  * @lucene.experimental   */
end_comment
begin_class
DECL|class|FixedGapTermsIndexReader
specifier|public
class|class
name|FixedGapTermsIndexReader
extends|extends
name|TermsIndexReaderBase
block|{
comment|// NOTE: long is overkill here, but we use this in a
comment|// number of places to multiply out the actual ord, and we
comment|// will overflow int during those multiplies.  So to avoid
comment|// having to upgrade each multiple to long in multiple
comment|// places (error prone), we use long here:
DECL|field|indexInterval
specifier|private
specifier|final
name|long
name|indexInterval
decl_stmt|;
DECL|field|packedIntsVersion
specifier|private
specifier|final
name|int
name|packedIntsVersion
decl_stmt|;
DECL|field|blocksize
specifier|private
specifier|final
name|int
name|blocksize
decl_stmt|;
DECL|field|termComp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
decl_stmt|;
DECL|field|PAGED_BYTES_BITS
specifier|private
specifier|final
specifier|static
name|int
name|PAGED_BYTES_BITS
init|=
literal|15
decl_stmt|;
comment|// all fields share this single logical byte[]
DECL|field|termBytesReader
specifier|private
specifier|final
name|PagedBytes
operator|.
name|Reader
name|termBytesReader
decl_stmt|;
DECL|field|fields
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldIndexData
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// start of the field info data
DECL|field|dirOffset
specifier|private
name|long
name|dirOffset
decl_stmt|;
DECL|field|version
specifier|private
name|int
name|version
decl_stmt|;
DECL|method|FixedGapTermsIndexReader
specifier|public
name|FixedGapTermsIndexReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|String
name|segment
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|PagedBytes
name|termBytes
init|=
operator|new
name|PagedBytes
argument_list|(
name|PAGED_BYTES_BITS
argument_list|)
decl_stmt|;
name|this
operator|.
name|termComp
operator|=
name|termComp
expr_stmt|;
specifier|final
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|segmentSuffix
argument_list|,
name|FixedGapTermsIndexWriter
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|readHeader
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|version
operator|>=
name|FixedGapTermsIndexWriter
operator|.
name|VERSION_CHECKSUM
condition|)
block|{
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|indexInterval
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexInterval
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid indexInterval: "
operator|+
name|indexInterval
operator|+
literal|" (resource="
operator|+
name|in
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|packedIntsVersion
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|blocksize
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|seekDir
argument_list|(
name|in
argument_list|,
name|dirOffset
argument_list|)
expr_stmt|;
comment|// Read directory
specifier|final
name|int
name|numFields
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|numFields
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid numFields: "
operator|+
name|numFields
operator|+
literal|" (resource="
operator|+
name|in
operator|+
literal|")"
argument_list|)
throw|;
block|}
comment|//System.out.println("FGR: init seg=" + segment + " div=" + indexDivisor + " nF=" + numFields);
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
specifier|final
name|int
name|field
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|long
name|numIndexTerms
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|// TODO: change this to a vLong if we fix writer to support> 2B index terms
if|if
condition|(
name|numIndexTerms
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid numIndexTerms: "
operator|+
name|numIndexTerms
operator|+
literal|" (resource="
operator|+
name|in
operator|+
literal|")"
argument_list|)
throw|;
block|}
specifier|final
name|long
name|termsStart
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|indexStart
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|packedIndexStart
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|packedOffsetsStart
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|packedIndexStart
operator|<
name|indexStart
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid packedIndexStart: "
operator|+
name|packedIndexStart
operator|+
literal|" indexStart: "
operator|+
name|indexStart
operator|+
literal|"numIndexTerms: "
operator|+
name|numIndexTerms
operator|+
literal|" (resource="
operator|+
name|in
operator|+
literal|")"
argument_list|)
throw|;
block|}
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|FieldIndexData
name|previous
init|=
name|fields
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
operator|new
name|FieldIndexData
argument_list|(
name|in
argument_list|,
name|termBytes
argument_list|,
name|indexStart
argument_list|,
name|termsStart
argument_list|,
name|packedIndexStart
argument_list|,
name|packedOffsetsStart
argument_list|,
name|numIndexTerms
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"duplicate field: "
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|" (resource="
operator|+
name|in
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|termBytesReader
operator|=
name|termBytes
operator|.
name|freeze
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readHeader
specifier|private
name|void
name|readHeader
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|version
operator|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
name|FixedGapTermsIndexWriter
operator|.
name|CODEC_NAME
argument_list|,
name|FixedGapTermsIndexWriter
operator|.
name|VERSION_CURRENT
argument_list|,
name|FixedGapTermsIndexWriter
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
DECL|class|IndexEnum
specifier|private
class|class
name|IndexEnum
extends|extends
name|FieldIndexEnum
block|{
DECL|field|fieldIndex
specifier|private
specifier|final
name|FieldIndexData
name|fieldIndex
decl_stmt|;
DECL|field|term
specifier|private
specifier|final
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|ord
specifier|private
name|long
name|ord
decl_stmt|;
DECL|method|IndexEnum
specifier|public
name|IndexEnum
parameter_list|(
name|FieldIndexData
name|fieldIndex
parameter_list|)
block|{
name|this
operator|.
name|fieldIndex
operator|=
name|fieldIndex
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
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|long
name|seek
parameter_list|(
name|BytesRef
name|target
parameter_list|)
block|{
name|long
name|lo
init|=
literal|0
decl_stmt|;
comment|// binary search
name|long
name|hi
init|=
name|fieldIndex
operator|.
name|numIndexTerms
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|long
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
specifier|final
name|long
name|offset
init|=
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
name|mid
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
literal|1
operator|+
name|mid
argument_list|)
operator|-
name|offset
argument_list|)
decl_stmt|;
name|termBytesReader
operator|.
name|fillSlice
argument_list|(
name|term
argument_list|,
name|fieldIndex
operator|.
name|termBytesStart
operator|+
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|int
name|delta
init|=
name|termComp
operator|.
name|compare
argument_list|(
name|target
argument_list|,
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|delta
operator|<
literal|0
condition|)
block|{
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|delta
operator|>
literal|0
condition|)
block|{
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|mid
operator|>=
literal|0
assert|;
name|ord
operator|=
name|mid
operator|*
name|indexInterval
expr_stmt|;
return|return
name|fieldIndex
operator|.
name|termsStart
operator|+
name|fieldIndex
operator|.
name|termsDictOffsets
operator|.
name|get
argument_list|(
name|mid
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|hi
operator|<
literal|0
condition|)
block|{
assert|assert
name|hi
operator|==
operator|-
literal|1
assert|;
name|hi
operator|=
literal|0
expr_stmt|;
block|}
specifier|final
name|long
name|offset
init|=
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
name|hi
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
literal|1
operator|+
name|hi
argument_list|)
operator|-
name|offset
argument_list|)
decl_stmt|;
name|termBytesReader
operator|.
name|fillSlice
argument_list|(
name|term
argument_list|,
name|fieldIndex
operator|.
name|termBytesStart
operator|+
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|ord
operator|=
name|hi
operator|*
name|indexInterval
expr_stmt|;
return|return
name|fieldIndex
operator|.
name|termsStart
operator|+
name|fieldIndex
operator|.
name|termsDictOffsets
operator|.
name|get
argument_list|(
name|hi
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|long
name|next
parameter_list|()
block|{
specifier|final
name|long
name|idx
init|=
literal|1
operator|+
operator|(
name|ord
operator|/
name|indexInterval
operator|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>=
name|fieldIndex
operator|.
name|numIndexTerms
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|ord
operator|+=
name|indexInterval
expr_stmt|;
specifier|final
name|long
name|offset
init|=
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
literal|1
operator|+
name|idx
argument_list|)
operator|-
name|offset
argument_list|)
decl_stmt|;
name|termBytesReader
operator|.
name|fillSlice
argument_list|(
name|term
argument_list|,
name|fieldIndex
operator|.
name|termBytesStart
operator|+
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|fieldIndex
operator|.
name|termsStart
operator|+
name|fieldIndex
operator|.
name|termsDictOffsets
operator|.
name|get
argument_list|(
name|idx
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
return|return
name|ord
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|long
name|seek
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
name|long
name|idx
init|=
name|ord
operator|/
name|indexInterval
decl_stmt|;
comment|// caller must ensure ord is in bounds
assert|assert
name|idx
operator|<
name|fieldIndex
operator|.
name|numIndexTerms
assert|;
specifier|final
name|long
name|offset
init|=
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
literal|1
operator|+
name|idx
argument_list|)
operator|-
name|offset
argument_list|)
decl_stmt|;
name|termBytesReader
operator|.
name|fillSlice
argument_list|(
name|term
argument_list|,
name|fieldIndex
operator|.
name|termBytesStart
operator|+
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|ord
operator|=
name|idx
operator|*
name|indexInterval
expr_stmt|;
return|return
name|fieldIndex
operator|.
name|termsStart
operator|+
name|fieldIndex
operator|.
name|termsDictOffsets
operator|.
name|get
argument_list|(
name|idx
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|supportsOrd
specifier|public
name|boolean
name|supportsOrd
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|class|FieldIndexData
specifier|private
specifier|final
class|class
name|FieldIndexData
implements|implements
name|Accountable
block|{
comment|// where this field's terms begin in the packed byte[]
comment|// data
DECL|field|termBytesStart
specifier|final
name|long
name|termBytesStart
decl_stmt|;
comment|// offset into index termBytes
DECL|field|termOffsets
specifier|final
name|MonotonicBlockPackedReader
name|termOffsets
decl_stmt|;
comment|// index pointers into main terms dict
DECL|field|termsDictOffsets
specifier|final
name|MonotonicBlockPackedReader
name|termsDictOffsets
decl_stmt|;
DECL|field|numIndexTerms
specifier|final
name|long
name|numIndexTerms
decl_stmt|;
DECL|field|termsStart
specifier|final
name|long
name|termsStart
decl_stmt|;
DECL|method|FieldIndexData
specifier|public
name|FieldIndexData
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|PagedBytes
name|termBytes
parameter_list|,
name|long
name|indexStart
parameter_list|,
name|long
name|termsStart
parameter_list|,
name|long
name|packedIndexStart
parameter_list|,
name|long
name|packedOffsetsStart
parameter_list|,
name|long
name|numIndexTerms
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|termsStart
operator|=
name|termsStart
expr_stmt|;
name|termBytesStart
operator|=
name|termBytes
operator|.
name|getPointer
argument_list|()
expr_stmt|;
name|IndexInput
name|clone
init|=
name|in
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|seek
argument_list|(
name|indexStart
argument_list|)
expr_stmt|;
name|this
operator|.
name|numIndexTerms
operator|=
name|numIndexTerms
expr_stmt|;
assert|assert
name|this
operator|.
name|numIndexTerms
operator|>
literal|0
operator|:
literal|"numIndexTerms="
operator|+
name|numIndexTerms
assert|;
comment|// slurp in the images from disk:
try|try
block|{
specifier|final
name|long
name|numTermBytes
init|=
name|packedIndexStart
operator|-
name|indexStart
decl_stmt|;
name|termBytes
operator|.
name|copy
argument_list|(
name|clone
argument_list|,
name|numTermBytes
argument_list|)
expr_stmt|;
comment|// records offsets into main terms dict file
name|termsDictOffsets
operator|=
name|MonotonicBlockPackedReader
operator|.
name|of
argument_list|(
name|clone
argument_list|,
name|packedIntsVersion
argument_list|,
name|blocksize
argument_list|,
name|numIndexTerms
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// records offsets into byte[] term data
name|termOffsets
operator|=
name|MonotonicBlockPackedReader
operator|.
name|of
argument_list|(
name|clone
argument_list|,
name|packedIntsVersion
argument_list|,
name|blocksize
argument_list|,
literal|1
operator|+
name|numIndexTerms
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|clone
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
operator|(
operator|(
name|termOffsets
operator|!=
literal|null
operator|)
condition|?
name|termOffsets
operator|.
name|ramBytesUsed
argument_list|()
else|:
literal|0
operator|)
operator|+
operator|(
operator|(
name|termsDictOffsets
operator|!=
literal|null
operator|)
condition|?
name|termsDictOffsets
operator|.
name|ramBytesUsed
argument_list|()
else|:
literal|0
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|List
argument_list|<
name|Accountable
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|termOffsets
operator|!=
literal|null
condition|)
block|{
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"term lengths"
argument_list|,
name|termOffsets
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|termsDictOffsets
operator|!=
literal|null
condition|)
block|{
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"offsets"
argument_list|,
name|termsDictOffsets
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|resources
return|;
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
literal|"FixedGapTermIndex(indexterms="
operator|+
name|numIndexTerms
operator|+
literal|")"
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFieldEnum
specifier|public
name|FieldIndexEnum
name|getFieldEnum
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
operator|new
name|IndexEnum
argument_list|(
name|fields
operator|.
name|get
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
DECL|method|seekDir
specifier|private
name|void
name|seekDir
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|long
name|dirOffset
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|version
operator|>=
name|FixedGapTermsIndexWriter
operator|.
name|VERSION_CHECKSUM
condition|)
block|{
name|input
operator|.
name|seek
argument_list|(
name|input
operator|.
name|length
argument_list|()
operator|-
name|CodecUtil
operator|.
name|footerLength
argument_list|()
operator|-
literal|8
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|input
operator|.
name|seek
argument_list|(
name|input
operator|.
name|length
argument_list|()
operator|-
literal|8
argument_list|)
expr_stmt|;
block|}
name|dirOffset
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|input
operator|.
name|seek
argument_list|(
name|dirOffset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|sizeInBytes
init|=
operator|(
operator|(
name|termBytesReader
operator|!=
literal|null
operator|)
condition|?
name|termBytesReader
operator|.
name|ramBytesUsed
argument_list|()
else|:
literal|0
operator|)
decl_stmt|;
for|for
control|(
name|FieldIndexData
name|entry
range|:
name|fields
operator|.
name|values
argument_list|()
control|)
block|{
name|sizeInBytes
operator|+=
name|entry
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|sizeInBytes
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Accountables
operator|.
name|namedAccountables
argument_list|(
literal|"field"
argument_list|,
name|fields
argument_list|)
return|;
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(fields="
operator|+
name|fields
operator|.
name|size
argument_list|()
operator|+
literal|",interval="
operator|+
name|indexInterval
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
