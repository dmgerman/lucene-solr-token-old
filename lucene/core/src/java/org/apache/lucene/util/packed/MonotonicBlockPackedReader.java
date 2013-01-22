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
name|AbstractBlockPackedWriter
operator|.
name|checkBlockSize
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
name|BlockPackedReaderIterator
operator|.
name|zigZagDecode
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
name|store
operator|.
name|IndexInput
import|;
end_import
begin_comment
comment|/**  * Provides random access to a stream written with  * {@link MonotonicBlockPackedWriter}.  * @lucene.internal  */
end_comment
begin_class
DECL|class|MonotonicBlockPackedReader
specifier|public
specifier|final
class|class
name|MonotonicBlockPackedReader
block|{
DECL|field|blockShift
DECL|field|blockMask
specifier|private
specifier|final
name|int
name|blockShift
decl_stmt|,
name|blockMask
decl_stmt|;
DECL|field|valueCount
specifier|private
specifier|final
name|long
name|valueCount
decl_stmt|;
DECL|field|minValues
specifier|private
specifier|final
name|long
index|[]
name|minValues
decl_stmt|;
DECL|field|averages
specifier|private
specifier|final
name|float
index|[]
name|averages
decl_stmt|;
DECL|field|subReaders
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
index|[]
name|subReaders
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|MonotonicBlockPackedReader
specifier|public
name|MonotonicBlockPackedReader
parameter_list|(
name|IndexInput
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
parameter_list|,
name|boolean
name|direct
parameter_list|)
throws|throws
name|IOException
block|{
name|checkBlockSize
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueCount
operator|=
name|valueCount
expr_stmt|;
name|blockShift
operator|=
name|Integer
operator|.
name|numberOfTrailingZeros
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
name|blockMask
operator|=
name|blockSize
operator|-
literal|1
expr_stmt|;
specifier|final
name|int
name|numBlocks
init|=
call|(
name|int
call|)
argument_list|(
name|valueCount
operator|/
name|blockSize
argument_list|)
operator|+
operator|(
name|valueCount
operator|%
name|blockSize
operator|==
literal|0
condition|?
literal|0
else|:
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|numBlocks
operator|*
name|blockSize
operator|<
name|valueCount
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"valueCount is too large for this block size"
argument_list|)
throw|;
block|}
name|minValues
operator|=
operator|new
name|long
index|[
name|numBlocks
index|]
expr_stmt|;
name|averages
operator|=
operator|new
name|float
index|[
name|numBlocks
index|]
expr_stmt|;
name|subReaders
operator|=
operator|new
name|PackedInts
operator|.
name|Reader
index|[
name|numBlocks
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
name|numBlocks
condition|;
operator|++
name|i
control|)
block|{
name|minValues
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|averages
index|[
name|i
index|]
operator|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bitsPerValue
init|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|bitsPerValue
operator|==
literal|0
condition|)
block|{
name|subReaders
index|[
name|i
index|]
operator|=
operator|new
name|PackedInts
operator|.
name|NullReader
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|size
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|blockSize
argument_list|,
name|valueCount
operator|-
operator|(
name|long
operator|)
name|i
operator|*
name|blockSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|direct
condition|)
block|{
specifier|final
name|long
name|pointer
init|=
name|in
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|subReaders
index|[
name|i
index|]
operator|=
name|PackedInts
operator|.
name|getDirectReaderNoHeader
argument_list|(
name|in
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|size
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|pointer
operator|+
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
name|size
argument_list|,
name|bitsPerValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|subReaders
index|[
name|i
index|]
operator|=
name|PackedInts
operator|.
name|getReaderNoHeader
argument_list|(
name|in
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|size
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Get value at<code>index</code>. */
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|valueCount
assert|;
specifier|final
name|int
name|block
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>>
name|blockShift
argument_list|)
decl_stmt|;
specifier|final
name|int
name|idx
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|&
name|blockMask
argument_list|)
decl_stmt|;
return|return
name|minValues
index|[
name|block
index|]
operator|+
call|(
name|long
call|)
argument_list|(
name|idx
operator|*
name|averages
index|[
name|block
index|]
argument_list|)
operator|+
name|zigZagDecode
argument_list|(
name|subReaders
index|[
name|block
index|]
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
