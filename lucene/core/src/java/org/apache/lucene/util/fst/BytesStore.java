begin_unit
begin_package
DECL|package|org.apache.lucene.util.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
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
name|ArrayList
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
name|DataOutput
import|;
end_import
begin_comment
comment|// TODO: merge with PagedBytes, except PagedBytes doesn't
end_comment
begin_comment
comment|// let you read while writing which FST needs
end_comment
begin_class
DECL|class|BytesStore
class|class
name|BytesStore
extends|extends
name|DataOutput
block|{
DECL|field|blocks
specifier|private
specifier|final
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|blocks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|field|blockBits
specifier|private
specifier|final
name|int
name|blockBits
decl_stmt|;
DECL|field|blockMask
specifier|private
specifier|final
name|int
name|blockMask
decl_stmt|;
DECL|field|current
specifier|private
name|byte
index|[]
name|current
decl_stmt|;
DECL|field|nextWrite
specifier|private
name|int
name|nextWrite
decl_stmt|;
DECL|method|BytesStore
specifier|public
name|BytesStore
parameter_list|(
name|int
name|blockBits
parameter_list|)
block|{
name|this
operator|.
name|blockBits
operator|=
name|blockBits
expr_stmt|;
name|blockSize
operator|=
literal|1
operator|<<
name|blockBits
expr_stmt|;
name|blockMask
operator|=
name|blockSize
operator|-
literal|1
expr_stmt|;
name|nextWrite
operator|=
name|blockSize
expr_stmt|;
block|}
comment|/** Pulls bytes from the provided IndexInput.  */
DECL|method|BytesStore
specifier|public
name|BytesStore
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|long
name|numBytes
parameter_list|,
name|int
name|maxBlockSize
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|blockSize
init|=
literal|2
decl_stmt|;
name|int
name|blockBits
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|blockSize
operator|<
name|numBytes
operator|&&
name|blockSize
operator|<
name|maxBlockSize
condition|)
block|{
name|blockSize
operator|*=
literal|2
expr_stmt|;
name|blockBits
operator|++
expr_stmt|;
block|}
name|this
operator|.
name|blockBits
operator|=
name|blockBits
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
name|this
operator|.
name|blockMask
operator|=
name|blockSize
operator|-
literal|1
expr_stmt|;
name|long
name|left
init|=
name|numBytes
decl_stmt|;
while|while
condition|(
name|left
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|chunk
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
name|left
argument_list|)
decl_stmt|;
name|byte
index|[]
name|block
init|=
operator|new
name|byte
index|[
name|chunk
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|block
argument_list|,
literal|0
argument_list|,
name|block
operator|.
name|length
argument_list|)
expr_stmt|;
name|blocks
operator|.
name|add
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|left
operator|-=
name|chunk
expr_stmt|;
block|}
comment|// So .getPosition still works
name|nextWrite
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|blocks
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|length
expr_stmt|;
block|}
comment|/** Absolute write byte; you must ensure dest is< max    *  position written so far. */
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|int
name|dest
parameter_list|,
name|byte
name|b
parameter_list|)
block|{
name|int
name|blockIndex
init|=
name|dest
operator|>>
name|blockBits
decl_stmt|;
name|byte
index|[]
name|block
init|=
name|blocks
operator|.
name|get
argument_list|(
name|blockIndex
argument_list|)
decl_stmt|;
name|block
index|[
name|dest
operator|&
name|blockMask
index|]
operator|=
name|b
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
if|if
condition|(
name|nextWrite
operator|==
name|blockSize
condition|)
block|{
name|current
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
name|blocks
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|nextWrite
operator|=
literal|0
expr_stmt|;
block|}
name|current
index|[
name|nextWrite
operator|++
index|]
operator|=
name|b
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|chunk
init|=
name|blockSize
operator|-
name|nextWrite
decl_stmt|;
if|if
condition|(
name|len
operator|<=
name|chunk
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|current
argument_list|,
name|nextWrite
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|nextWrite
operator|+=
name|len
expr_stmt|;
break|break;
block|}
else|else
block|{
if|if
condition|(
name|chunk
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|current
argument_list|,
name|nextWrite
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|chunk
expr_stmt|;
name|len
operator|-=
name|chunk
expr_stmt|;
block|}
name|current
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
name|blocks
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|nextWrite
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
DECL|method|getBlockBits
name|int
name|getBlockBits
parameter_list|()
block|{
return|return
name|blockBits
return|;
block|}
comment|/** Absolute writeBytes without changing the current    *  position.  Note: this cannot "grow" the bytes, so you    *  must only call it on already written parts. */
DECL|method|writeBytes
name|void
name|writeBytes
parameter_list|(
name|long
name|dest
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|//System.out.println("  BS.writeBytes dest=" + dest + " offset=" + offset + " len=" + len);
assert|assert
name|dest
operator|+
name|len
operator|<=
name|getPosition
argument_list|()
operator|:
literal|"dest="
operator|+
name|dest
operator|+
literal|" pos="
operator|+
name|getPosition
argument_list|()
operator|+
literal|" len="
operator|+
name|len
assert|;
comment|// Note: weird: must go "backwards" because copyBytes
comment|// calls us with overlapping src/dest.  If we
comment|// go forwards then we overwrite bytes before we can
comment|// copy them:
comment|/*     int blockIndex = dest>> blockBits;     int upto = dest& blockMask;     byte[] block = blocks.get(blockIndex);     while (len> 0) {       int chunk = blockSize - upto;       System.out.println("    cycle chunk=" + chunk + " len=" + len);       if (len<= chunk) {         System.arraycopy(b, offset, block, upto, len);         break;       } else {         System.arraycopy(b, offset, block, upto, chunk);         offset += chunk;         len -= chunk;         blockIndex++;         block = blocks.get(blockIndex);         upto = 0;       }     }     */
specifier|final
name|long
name|end
init|=
name|dest
operator|+
name|len
decl_stmt|;
name|int
name|blockIndex
init|=
call|(
name|int
call|)
argument_list|(
name|end
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
name|int
name|downTo
init|=
call|(
name|int
call|)
argument_list|(
name|end
operator|&
name|blockMask
argument_list|)
decl_stmt|;
if|if
condition|(
name|downTo
operator|==
literal|0
condition|)
block|{
name|blockIndex
operator|--
expr_stmt|;
name|downTo
operator|=
name|blockSize
expr_stmt|;
block|}
name|byte
index|[]
name|block
init|=
name|blocks
operator|.
name|get
argument_list|(
name|blockIndex
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
comment|//System.out.println("    cycle downTo=" + downTo + " len=" + len);
if|if
condition|(
name|len
operator|<=
name|downTo
condition|)
block|{
comment|//System.out.println("      final: offset=" + offset + " len=" + len + " dest=" + (downTo-len));
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|block
argument_list|,
name|downTo
operator|-
name|len
argument_list|,
name|len
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
name|len
operator|-=
name|downTo
expr_stmt|;
comment|//System.out.println("      partial: offset=" + (offset + len) + " len=" + downTo + " dest=0");
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
operator|+
name|len
argument_list|,
name|block
argument_list|,
literal|0
argument_list|,
name|downTo
argument_list|)
expr_stmt|;
name|blockIndex
operator|--
expr_stmt|;
name|block
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|blockIndex
argument_list|)
expr_stmt|;
name|downTo
operator|=
name|blockSize
expr_stmt|;
block|}
block|}
block|}
comment|/** Absolute copy bytes self to self, without changing the    *  position. Note: this cannot "grow" the bytes, so must    *  only call it on already written parts. */
DECL|method|copyBytes
specifier|public
name|void
name|copyBytes
parameter_list|(
name|long
name|src
parameter_list|,
name|long
name|dest
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|//System.out.println("BS.copyBytes src=" + src + " dest=" + dest + " len=" + len);
assert|assert
name|src
operator|<
name|dest
assert|;
comment|// Note: weird: must go "backwards" because copyBytes
comment|// calls us with overlapping src/dest.  If we
comment|// go forwards then we overwrite bytes before we can
comment|// copy them:
comment|/*     int blockIndex = src>> blockBits;     int upto = src& blockMask;     byte[] block = blocks.get(blockIndex);     while (len> 0) {       int chunk = blockSize - upto;       System.out.println("  cycle: chunk=" + chunk + " len=" + len);       if (len<= chunk) {         writeBytes(dest, block, upto, len);         break;       } else {         writeBytes(dest, block, upto, chunk);         blockIndex++;         block = blocks.get(blockIndex);         upto = 0;         len -= chunk;         dest += chunk;       }     }     */
name|long
name|end
init|=
name|src
operator|+
name|len
decl_stmt|;
name|int
name|blockIndex
init|=
call|(
name|int
call|)
argument_list|(
name|end
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
name|int
name|downTo
init|=
call|(
name|int
call|)
argument_list|(
name|end
operator|&
name|blockMask
argument_list|)
decl_stmt|;
if|if
condition|(
name|downTo
operator|==
literal|0
condition|)
block|{
name|blockIndex
operator|--
expr_stmt|;
name|downTo
operator|=
name|blockSize
expr_stmt|;
block|}
name|byte
index|[]
name|block
init|=
name|blocks
operator|.
name|get
argument_list|(
name|blockIndex
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
comment|//System.out.println("  cycle downTo=" + downTo);
if|if
condition|(
name|len
operator|<=
name|downTo
condition|)
block|{
comment|//System.out.println("    finish");
name|writeBytes
argument_list|(
name|dest
argument_list|,
name|block
argument_list|,
name|downTo
operator|-
name|len
argument_list|,
name|len
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
comment|//System.out.println("    partial");
name|len
operator|-=
name|downTo
expr_stmt|;
name|writeBytes
argument_list|(
name|dest
operator|+
name|len
argument_list|,
name|block
argument_list|,
literal|0
argument_list|,
name|downTo
argument_list|)
expr_stmt|;
name|blockIndex
operator|--
expr_stmt|;
name|block
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|blockIndex
argument_list|)
expr_stmt|;
name|downTo
operator|=
name|blockSize
expr_stmt|;
block|}
block|}
block|}
comment|/** Writes an int at the absolute position without    *  changing the current pointer. */
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|long
name|pos
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|int
name|blockIndex
init|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
name|int
name|upto
init|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|&
name|blockMask
argument_list|)
decl_stmt|;
name|byte
index|[]
name|block
init|=
name|blocks
operator|.
name|get
argument_list|(
name|blockIndex
argument_list|)
decl_stmt|;
name|int
name|shift
init|=
literal|24
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|block
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|>>
name|shift
argument_list|)
expr_stmt|;
name|shift
operator|-=
literal|8
expr_stmt|;
if|if
condition|(
name|upto
operator|==
name|blockSize
condition|)
block|{
name|upto
operator|=
literal|0
expr_stmt|;
name|blockIndex
operator|++
expr_stmt|;
name|block
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|blockIndex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Reverse from srcPos, inclusive, to destPos, inclusive. */
DECL|method|reverse
specifier|public
name|void
name|reverse
parameter_list|(
name|long
name|srcPos
parameter_list|,
name|long
name|destPos
parameter_list|)
block|{
assert|assert
name|srcPos
operator|<
name|destPos
assert|;
assert|assert
name|destPos
operator|<
name|getPosition
argument_list|()
assert|;
comment|//System.out.println("reverse src=" + srcPos + " dest=" + destPos);
name|int
name|srcBlockIndex
init|=
call|(
name|int
call|)
argument_list|(
name|srcPos
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
name|int
name|src
init|=
call|(
name|int
call|)
argument_list|(
name|srcPos
operator|&
name|blockMask
argument_list|)
decl_stmt|;
name|byte
index|[]
name|srcBlock
init|=
name|blocks
operator|.
name|get
argument_list|(
name|srcBlockIndex
argument_list|)
decl_stmt|;
name|int
name|destBlockIndex
init|=
call|(
name|int
call|)
argument_list|(
name|destPos
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
name|int
name|dest
init|=
call|(
name|int
call|)
argument_list|(
name|destPos
operator|&
name|blockMask
argument_list|)
decl_stmt|;
name|byte
index|[]
name|destBlock
init|=
name|blocks
operator|.
name|get
argument_list|(
name|destBlockIndex
argument_list|)
decl_stmt|;
comment|//System.out.println("  srcBlock=" + srcBlockIndex + " destBlock=" + destBlockIndex);
name|int
name|limit
init|=
call|(
name|int
call|)
argument_list|(
name|destPos
operator|-
name|srcPos
operator|+
literal|1
argument_list|)
operator|/
literal|2
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
name|limit
condition|;
name|i
operator|++
control|)
block|{
comment|//System.out.println("  cycle src=" + src + " dest=" + dest);
name|byte
name|b
init|=
name|srcBlock
index|[
name|src
index|]
decl_stmt|;
name|srcBlock
index|[
name|src
index|]
operator|=
name|destBlock
index|[
name|dest
index|]
expr_stmt|;
name|destBlock
index|[
name|dest
index|]
operator|=
name|b
expr_stmt|;
name|src
operator|++
expr_stmt|;
if|if
condition|(
name|src
operator|==
name|blockSize
condition|)
block|{
name|srcBlockIndex
operator|++
expr_stmt|;
name|srcBlock
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|srcBlockIndex
argument_list|)
expr_stmt|;
comment|//System.out.println("  set destBlock=" + destBlock + " srcBlock=" + srcBlock);
name|src
operator|=
literal|0
expr_stmt|;
block|}
name|dest
operator|--
expr_stmt|;
if|if
condition|(
name|dest
operator|==
operator|-
literal|1
condition|)
block|{
name|destBlockIndex
operator|--
expr_stmt|;
name|destBlock
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|destBlockIndex
argument_list|)
expr_stmt|;
comment|//System.out.println("  set destBlock=" + destBlock + " srcBlock=" + srcBlock);
name|dest
operator|=
name|blockSize
operator|-
literal|1
expr_stmt|;
block|}
block|}
block|}
DECL|method|skipBytes
specifier|public
name|void
name|skipBytes
parameter_list|(
name|int
name|len
parameter_list|)
block|{
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|chunk
init|=
name|blockSize
operator|-
name|nextWrite
decl_stmt|;
if|if
condition|(
name|len
operator|<=
name|chunk
condition|)
block|{
name|nextWrite
operator|+=
name|len
expr_stmt|;
break|break;
block|}
else|else
block|{
name|len
operator|-=
name|chunk
expr_stmt|;
name|current
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
name|blocks
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|nextWrite
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
DECL|method|getPosition
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
operator|(
operator|(
name|long
operator|)
name|blocks
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
operator|*
name|blockSize
operator|+
name|nextWrite
return|;
block|}
comment|/** Pos must be less than the max position written so far!    *  Ie, you cannot "grow" the file with this! */
DECL|method|truncate
specifier|public
name|void
name|truncate
parameter_list|(
name|long
name|newLen
parameter_list|)
block|{
assert|assert
name|newLen
operator|<=
name|getPosition
argument_list|()
assert|;
assert|assert
name|newLen
operator|>=
literal|0
assert|;
name|int
name|blockIndex
init|=
call|(
name|int
call|)
argument_list|(
name|newLen
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
name|nextWrite
operator|=
call|(
name|int
call|)
argument_list|(
name|newLen
operator|&
name|blockMask
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextWrite
operator|==
literal|0
condition|)
block|{
name|blockIndex
operator|--
expr_stmt|;
name|nextWrite
operator|=
name|blockSize
expr_stmt|;
block|}
name|blocks
operator|.
name|subList
argument_list|(
name|blockIndex
operator|+
literal|1
argument_list|,
name|blocks
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|newLen
operator|==
literal|0
condition|)
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|blockIndex
argument_list|)
expr_stmt|;
block|}
assert|assert
name|newLen
operator|==
name|getPosition
argument_list|()
assert|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
block|{
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|lastBuffer
init|=
operator|new
name|byte
index|[
name|nextWrite
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|current
argument_list|,
literal|0
argument_list|,
name|lastBuffer
argument_list|,
literal|0
argument_list|,
name|nextWrite
argument_list|)
expr_stmt|;
name|blocks
operator|.
name|set
argument_list|(
name|blocks
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|lastBuffer
argument_list|)
expr_stmt|;
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Writes all of our bytes to the target {@link DataOutput}. */
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|byte
index|[]
name|block
range|:
name|blocks
control|)
block|{
name|out
operator|.
name|writeBytes
argument_list|(
name|block
argument_list|,
literal|0
argument_list|,
name|block
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getForwardReader
specifier|public
name|FST
operator|.
name|BytesReader
name|getForwardReader
parameter_list|()
block|{
if|if
condition|(
name|blocks
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|ForwardBytesReader
argument_list|(
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|FST
operator|.
name|BytesReader
argument_list|()
block|{
specifier|private
name|byte
index|[]
name|current
decl_stmt|;
specifier|private
name|int
name|nextBuffer
decl_stmt|;
specifier|private
name|int
name|nextRead
init|=
name|blockSize
decl_stmt|;
annotation|@
name|Override
specifier|public
name|byte
name|readByte
parameter_list|()
block|{
if|if
condition|(
name|nextRead
operator|==
name|blockSize
condition|)
block|{
name|current
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|nextBuffer
operator|++
argument_list|)
expr_stmt|;
name|nextRead
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|current
index|[
name|nextRead
operator|++
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|skipBytes
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|setPosition
argument_list|(
name|getPosition
argument_list|()
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|chunkLeft
init|=
name|blockSize
operator|-
name|nextRead
decl_stmt|;
if|if
condition|(
name|len
operator|<=
name|chunkLeft
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|current
argument_list|,
name|nextRead
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|nextRead
operator|+=
name|len
expr_stmt|;
break|break;
block|}
else|else
block|{
if|if
condition|(
name|chunkLeft
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|current
argument_list|,
name|nextRead
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|chunkLeft
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|chunkLeft
expr_stmt|;
name|len
operator|-=
name|chunkLeft
expr_stmt|;
block|}
name|current
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|nextBuffer
operator|++
argument_list|)
expr_stmt|;
name|nextRead
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
operator|(
operator|(
name|long
operator|)
name|nextBuffer
operator|-
literal|1
operator|)
operator|*
name|blockSize
operator|+
name|nextRead
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPosition
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
name|int
name|bufferIndex
init|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
name|nextBuffer
operator|=
name|bufferIndex
operator|+
literal|1
expr_stmt|;
name|current
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|bufferIndex
argument_list|)
expr_stmt|;
name|nextRead
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|&
name|blockMask
argument_list|)
expr_stmt|;
assert|assert
name|getPosition
argument_list|()
operator|==
name|pos
assert|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|reversed
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
DECL|method|getReverseReader
specifier|public
name|FST
operator|.
name|BytesReader
name|getReverseReader
parameter_list|()
block|{
return|return
name|getReverseReader
argument_list|(
literal|true
argument_list|)
return|;
block|}
DECL|method|getReverseReader
name|FST
operator|.
name|BytesReader
name|getReverseReader
parameter_list|(
name|boolean
name|allowSingle
parameter_list|)
block|{
if|if
condition|(
name|allowSingle
operator|&&
name|blocks
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|ReverseBytesReader
argument_list|(
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|FST
operator|.
name|BytesReader
argument_list|()
block|{
specifier|private
name|byte
index|[]
name|current
init|=
name|blocks
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|?
literal|null
else|:
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|int
name|nextBuffer
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|nextRead
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|byte
name|readByte
parameter_list|()
block|{
if|if
condition|(
name|nextRead
operator|==
operator|-
literal|1
condition|)
block|{
name|current
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|nextBuffer
operator|--
argument_list|)
expr_stmt|;
name|nextRead
operator|=
name|blockSize
operator|-
literal|1
expr_stmt|;
block|}
return|return
name|current
index|[
name|nextRead
operator|--
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|skipBytes
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|setPosition
argument_list|(
name|getPosition
argument_list|()
operator|-
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|b
index|[
name|offset
operator|+
name|i
index|]
operator|=
name|readByte
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
operator|(
operator|(
name|long
operator|)
name|nextBuffer
operator|+
literal|1
operator|)
operator|*
name|blockSize
operator|+
name|nextRead
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPosition
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
comment|// NOTE: a little weird because if you
comment|// setPosition(0), the next byte you read is
comment|// bytes[0] ... but I would expect bytes[-1] (ie,
comment|// EOF)...?
name|int
name|bufferIndex
init|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|>>
name|blockBits
argument_list|)
decl_stmt|;
name|nextBuffer
operator|=
name|bufferIndex
operator|-
literal|1
expr_stmt|;
name|current
operator|=
name|blocks
operator|.
name|get
argument_list|(
name|bufferIndex
argument_list|)
expr_stmt|;
name|nextRead
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|&
name|blockMask
argument_list|)
expr_stmt|;
assert|assert
name|getPosition
argument_list|()
operator|==
name|pos
operator|:
literal|"pos="
operator|+
name|pos
operator|+
literal|" getPos()="
operator|+
name|getPosition
argument_list|()
assert|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|reversed
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
