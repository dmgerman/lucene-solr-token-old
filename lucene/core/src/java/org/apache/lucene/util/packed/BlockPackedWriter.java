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
name|DataOutput
import|;
end_import
begin_comment
comment|/**  * A writer for large sequences of longs.  *<p>  * The sequence is divided into fixed-size blocks and for each block, the  * difference between each value and the minimum value of the block is encoded  * using as few bits as possible. Memory usage of this class is proportional to  * the block size. Each block has an overhead between 1 and 10 bytes to store  * the minimum value and the number of bits per value of the block.  * @see BlockPackedReaderIterator  * @lucene.internal  */
end_comment
begin_class
DECL|class|BlockPackedWriter
specifier|public
specifier|final
class|class
name|BlockPackedWriter
block|{
DECL|field|MAX_BLOCK_SIZE
specifier|static
specifier|final
name|int
name|MAX_BLOCK_SIZE
init|=
literal|1
operator|<<
operator|(
literal|30
operator|-
literal|3
operator|)
decl_stmt|;
DECL|field|MIN_VALUE_EQUALS_0
specifier|static
specifier|final
name|int
name|MIN_VALUE_EQUALS_0
init|=
literal|1
operator|<<
literal|0
decl_stmt|;
DECL|field|BPV_SHIFT
specifier|static
specifier|final
name|int
name|BPV_SHIFT
init|=
literal|1
decl_stmt|;
DECL|method|checkBlockSize
specifier|static
name|void
name|checkBlockSize
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
if|if
condition|(
name|blockSize
operator|<=
literal|0
operator|||
name|blockSize
operator|>
name|MAX_BLOCK_SIZE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"blockSize must be> 0 and< "
operator|+
name|MAX_BLOCK_SIZE
operator|+
literal|", got "
operator|+
name|blockSize
argument_list|)
throw|;
block|}
if|if
condition|(
name|blockSize
operator|<
literal|64
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"blockSize must be>= 64, got "
operator|+
name|blockSize
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|blockSize
operator|&
operator|(
name|blockSize
operator|-
literal|1
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"blockSize must be a power of two, got "
operator|+
name|blockSize
argument_list|)
throw|;
block|}
block|}
DECL|method|zigZagEncode
specifier|static
name|long
name|zigZagEncode
parameter_list|(
name|long
name|n
parameter_list|)
block|{
return|return
operator|(
name|n
operator|>>
literal|63
operator|)
operator|^
operator|(
name|n
operator|<<
literal|1
operator|)
return|;
block|}
comment|// same as DataOutput.writeVLong but accepts negative values
DECL|method|writeVLong
specifier|static
name|void
name|writeVLong
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|long
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|k
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|i
operator|&
operator|~
literal|0x7FL
operator|)
operator|!=
literal|0L
operator|&&
name|k
operator|++
operator|<
literal|8
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x7FL
operator|)
operator||
literal|0x80L
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|>>>=
literal|7
expr_stmt|;
block|}
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
DECL|field|out
name|DataOutput
name|out
decl_stmt|;
DECL|field|values
specifier|final
name|long
index|[]
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
DECL|field|finished
name|boolean
name|finished
decl_stmt|;
comment|/**    * Sole constructor.    * @param blockSize the number of values of a single block, must be a multiple of<tt>64</tt>    */
DECL|method|BlockPackedWriter
specifier|public
name|BlockPackedWriter
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|int
name|blockSize
parameter_list|)
block|{
name|checkBlockSize
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
name|reset
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|long
index|[
name|blockSize
index|]
expr_stmt|;
block|}
comment|/** Reset this writer to wrap<code>out</code>. The block size remains unchanged. */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|DataOutput
name|out
parameter_list|)
block|{
assert|assert
name|out
operator|!=
literal|null
assert|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|off
operator|=
literal|0
expr_stmt|;
name|ord
operator|=
literal|0L
expr_stmt|;
name|finished
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|checkNotFinished
specifier|private
name|void
name|checkNotFinished
parameter_list|()
block|{
if|if
condition|(
name|finished
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Already finished"
argument_list|)
throw|;
block|}
block|}
comment|/** Append a new long. */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|long
name|l
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotFinished
argument_list|()
expr_stmt|;
if|if
condition|(
name|off
operator|==
name|values
operator|.
name|length
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
name|values
index|[
name|off
operator|++
index|]
operator|=
name|l
expr_stmt|;
operator|++
name|ord
expr_stmt|;
block|}
comment|/** Flush all buffered data to disk. This instance is not usable anymore    *  after this method has been called until {@link #reset(DataOutput)} has    *  been called. */
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|checkNotFinished
argument_list|()
expr_stmt|;
if|if
condition|(
name|off
operator|>
literal|0
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
name|finished
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|flush
specifier|private
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|off
operator|>
literal|0
assert|;
name|long
name|min
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|,
name|max
init|=
name|Long
operator|.
name|MIN_VALUE
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
name|off
condition|;
operator|++
name|i
control|)
block|{
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|min
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|delta
init|=
name|max
operator|-
name|min
decl_stmt|;
specifier|final
name|int
name|bitsRequired
init|=
name|delta
operator|<
literal|0
condition|?
literal|64
else|:
name|delta
operator|==
literal|0L
condition|?
literal|0
else|:
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|delta
argument_list|)
decl_stmt|;
if|if
condition|(
name|bitsRequired
operator|==
literal|64
condition|)
block|{
comment|// no need to delta-encode
name|min
operator|=
literal|0L
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|min
operator|>
literal|0L
condition|)
block|{
comment|// make min as small as possible so that writeVLong requires fewer bytes
name|min
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0L
argument_list|,
name|max
operator|-
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|bitsRequired
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|token
init|=
operator|(
name|bitsRequired
operator|<<
name|BPV_SHIFT
operator|)
operator||
operator|(
name|min
operator|==
literal|0
condition|?
name|MIN_VALUE_EQUALS_0
else|:
literal|0
operator|)
decl_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|min
operator|!=
literal|0
condition|)
block|{
name|writeVLong
argument_list|(
name|out
argument_list|,
name|zigZagEncode
argument_list|(
name|min
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bitsRequired
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|min
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
name|off
condition|;
operator|++
name|i
control|)
block|{
name|values
index|[
name|i
index|]
operator|-=
name|min
expr_stmt|;
block|}
block|}
specifier|final
name|PackedInts
operator|.
name|Encoder
name|encoder
init|=
name|PackedInts
operator|.
name|getEncoder
argument_list|(
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|,
name|bitsRequired
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
name|values
operator|.
name|length
operator|/
name|encoder
operator|.
name|valueCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|blockSize
init|=
name|encoder
operator|.
name|blockCount
argument_list|()
operator|*
literal|8
operator|*
name|iterations
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
name|blockSize
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
if|if
condition|(
name|off
operator|<
name|values
operator|.
name|length
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
name|off
argument_list|,
name|values
operator|.
name|length
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
name|encoder
operator|.
name|encode
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|blocks
argument_list|,
literal|0
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
specifier|final
name|int
name|blockCount
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
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|,
name|off
argument_list|,
name|bitsRequired
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|blocks
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
block|}
name|off
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Return the number of values which have been added. */
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
