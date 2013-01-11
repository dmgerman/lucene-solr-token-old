begin_unit
begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import static
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
name|BlockPackedWriter
operator|.
name|BPV_SHIFT
import|;
end_import
begin_import
import|import static
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
name|BlockPackedWriter
operator|.
name|MIN_VALUE_EQUALS_0
import|;
end_import
begin_import
import|import static
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
name|BlockPackedWriter
operator|.
name|checkBlockSize
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|Arrays
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
name|DataInput
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
name|util
operator|.
name|LongsRef
import|;
end_import
begin_comment
comment|/**  * Reader for sequences of longs written with {@link BlockPackedWriter}.  * @see BlockPackedWriter  * @lucene.internal  */
end_comment
begin_class
DECL|class|BlockPackedReader
specifier|public
specifier|final
class|class
name|BlockPackedReader
block|{
DECL|method|zigZagDecode
specifier|static
name|long
name|zigZagDecode
parameter_list|(
name|long
name|n
parameter_list|)
block|{
return|return
operator|(
operator|(
name|n
operator|>>>
literal|1
operator|)
operator|^
operator|-
operator|(
name|n
operator|&
literal|1
operator|)
operator|)
return|;
block|}
comment|// same as DataInput.readVLong but supports negative values
DECL|method|readVLong
specifier|static
name|long
name|readVLong
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|b
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|b
return|;
name|long
name|i
init|=
name|b
operator|&
literal|0x7FL
decl_stmt|;
name|b
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|7
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|14
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|21
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|28
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|35
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|42
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|49
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0xFFL
operator|)
operator|<<
literal|56
expr_stmt|;
return|return
name|i
return|;
block|}
DECL|field|in
specifier|final
name|DataInput
name|in
decl_stmt|;
DECL|field|packedIntsVersion
specifier|final
name|int
name|packedIntsVersion
decl_stmt|;
DECL|field|valueCount
specifier|final
name|long
name|valueCount
decl_stmt|;
DECL|field|blockSize
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|field|values
specifier|final
name|LongsRef
name|values
decl_stmt|;
DECL|field|blocks
name|byte
index|[]
name|blocks
decl_stmt|;
DECL|field|off
name|int
name|off
decl_stmt|;
DECL|field|ord
name|long
name|ord
decl_stmt|;
comment|/** Sole constructor.    * @param blockSize the number of values of a block, must be equal to the    *                  block size of the {@link BlockPackedWriter} which has    *                  been used to write the stream    */
DECL|method|BlockPackedReader
specifier|public
name|BlockPackedReader
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|packedIntsVersion
parameter_list|,
name|int
name|blockSize
parameter_list|,
name|long
name|valueCount
parameter_list|)
block|{
name|checkBlockSize
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|packedIntsVersion
operator|=
name|packedIntsVersion
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
name|this
operator|.
name|values
operator|=
operator|new
name|LongsRef
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
assert|assert
name|valueCount
operator|>=
literal|0
assert|;
name|this
operator|.
name|valueCount
operator|=
name|valueCount
expr_stmt|;
name|off
operator|=
name|blockSize
expr_stmt|;
name|ord
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Skip exactly<code>count</code> values. */
DECL|method|skip
specifier|public
name|void
name|skip
parameter_list|(
name|long
name|count
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|count
operator|>=
literal|0
assert|;
if|if
condition|(
name|ord
operator|+
name|count
operator|>
name|valueCount
operator|||
name|ord
operator|+
name|count
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
comment|// 1. skip buffered values
specifier|final
name|int
name|skipBuffer
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|count
argument_list|,
name|blockSize
operator|-
name|off
argument_list|)
decl_stmt|;
name|off
operator|+=
name|skipBuffer
expr_stmt|;
name|ord
operator|+=
name|skipBuffer
expr_stmt|;
name|count
operator|-=
name|skipBuffer
expr_stmt|;
if|if
condition|(
name|count
operator|==
literal|0L
condition|)
block|{
return|return;
block|}
comment|// 2. skip as many blocks as necessary
assert|assert
name|off
operator|==
name|blockSize
assert|;
while|while
condition|(
name|count
operator|>=
name|blockSize
condition|)
block|{
specifier|final
name|int
name|token
init|=
name|in
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|bitsPerValue
init|=
name|token
operator|>>>
name|BPV_SHIFT
decl_stmt|;
if|if
condition|(
name|bitsPerValue
operator|>
literal|64
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Corrupted"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|token
operator|&
name|MIN_VALUE_EQUALS_0
operator|)
operator|==
literal|0
condition|)
block|{
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|blockBytes
init|=
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
operator|.
name|byteCount
argument_list|(
name|packedIntsVersion
argument_list|,
name|blockSize
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
name|skipBytes
argument_list|(
name|blockBytes
argument_list|)
expr_stmt|;
name|ord
operator|+=
name|blockSize
expr_stmt|;
name|count
operator|-=
name|blockSize
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|==
literal|0L
condition|)
block|{
return|return;
block|}
comment|// 3. skip last values
assert|assert
name|count
operator|<
name|blockSize
assert|;
name|refill
argument_list|()
expr_stmt|;
name|ord
operator|+=
name|count
expr_stmt|;
name|off
operator|+=
name|count
expr_stmt|;
block|}
DECL|method|skipBytes
specifier|private
name|void
name|skipBytes
parameter_list|(
name|long
name|count
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|instanceof
name|IndexInput
condition|)
block|{
specifier|final
name|IndexInput
name|iin
init|=
operator|(
name|IndexInput
operator|)
name|in
decl_stmt|;
name|iin
operator|.
name|seek
argument_list|(
name|iin
operator|.
name|getFilePointer
argument_list|()
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|blocks
operator|==
literal|null
condition|)
block|{
name|blocks
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
block|}
name|long
name|skipped
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|skipped
operator|<
name|count
condition|)
block|{
specifier|final
name|int
name|toSkip
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|blocks
operator|.
name|length
argument_list|,
name|count
operator|-
name|skipped
argument_list|)
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|blocks
argument_list|,
literal|0
argument_list|,
name|toSkip
argument_list|)
expr_stmt|;
name|skipped
operator|+=
name|toSkip
expr_stmt|;
block|}
block|}
block|}
comment|/** Read the next value. */
DECL|method|next
specifier|public
name|long
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|next
argument_list|(
literal|1
argument_list|)
expr_stmt|;
assert|assert
name|values
operator|.
name|length
operator|==
literal|1
assert|;
return|return
name|values
operator|.
name|longs
index|[
name|values
operator|.
name|offset
index|]
return|;
block|}
comment|/** Read between<tt>1</tt> and<code>count</code> values. */
DECL|method|next
specifier|public
name|LongsRef
name|next
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|count
operator|>
literal|0
assert|;
if|if
condition|(
name|ord
operator|==
name|valueCount
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
if|if
condition|(
name|off
operator|==
name|blockSize
condition|)
block|{
name|refill
argument_list|()
expr_stmt|;
block|}
name|count
operator|=
name|Math
operator|.
name|min
argument_list|(
name|count
argument_list|,
name|blockSize
operator|-
name|off
argument_list|)
expr_stmt|;
name|count
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|count
argument_list|,
name|valueCount
operator|-
name|ord
argument_list|)
expr_stmt|;
name|values
operator|.
name|offset
operator|=
name|off
expr_stmt|;
name|values
operator|.
name|length
operator|=
name|count
expr_stmt|;
name|off
operator|+=
name|count
expr_stmt|;
name|ord
operator|+=
name|count
expr_stmt|;
return|return
name|values
return|;
block|}
DECL|method|refill
specifier|private
name|void
name|refill
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|token
init|=
name|in
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|boolean
name|minEquals0
init|=
operator|(
name|token
operator|&
name|MIN_VALUE_EQUALS_0
operator|)
operator|!=
literal|0
decl_stmt|;
specifier|final
name|int
name|bitsPerValue
init|=
name|token
operator|>>>
name|BPV_SHIFT
decl_stmt|;
if|if
condition|(
name|bitsPerValue
operator|>
literal|64
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Corrupted"
argument_list|)
throw|;
block|}
specifier|final
name|long
name|minValue
init|=
name|minEquals0
condition|?
literal|0L
else|:
name|zigZagDecode
argument_list|(
literal|1L
operator|+
name|readVLong
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|minEquals0
operator|||
name|minValue
operator|!=
literal|0
assert|;
if|if
condition|(
name|bitsPerValue
operator|==
literal|0
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|values
operator|.
name|longs
argument_list|,
name|minValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|PackedInts
operator|.
name|Decoder
name|decoder
init|=
name|PackedInts
operator|.
name|getDecoder
argument_list|(
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
name|blockSize
operator|/
name|decoder
operator|.
name|valueCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|blocksSize
init|=
name|iterations
operator|*
literal|8
operator|*
name|decoder
operator|.
name|blockCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|blocks
operator|==
literal|null
operator|||
name|blocks
operator|.
name|length
operator|<
name|blocksSize
condition|)
block|{
name|blocks
operator|=
operator|new
name|byte
index|[
name|blocksSize
index|]
expr_stmt|;
block|}
specifier|final
name|int
name|valueCount
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|this
operator|.
name|valueCount
operator|-
name|ord
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
specifier|final
name|int
name|blocksCount
init|=
operator|(
name|int
operator|)
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
operator|.
name|byteCount
argument_list|(
name|packedIntsVersion
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|blocks
argument_list|,
literal|0
argument_list|,
name|blocksCount
argument_list|)
expr_stmt|;
name|decoder
operator|.
name|decode
argument_list|(
name|blocks
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|longs
argument_list|,
literal|0
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
if|if
condition|(
name|minValue
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|valueCount
condition|;
operator|++
name|i
control|)
block|{
name|values
operator|.
name|longs
index|[
name|i
index|]
operator|+=
name|minValue
expr_stmt|;
block|}
block|}
block|}
name|off
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Return the offset of the next value to read. */
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
block|}
end_class
end_unit
