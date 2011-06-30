begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * This class is used as a wrapper to a byte array, extending  * {@link OutputStream}. Data is written in the given byte[] buffer, until its  * length is insufficient. Than the buffer size is doubled and the data is  * written.  *   * This class is Unsafe as it is using a buffer which potentially can be changed  * from the outside. Moreover, when {@link #toByteArray()} is called, the buffer  * itself is returned, and not a copy.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|UnsafeByteArrayOutputStream
specifier|public
class|class
name|UnsafeByteArrayOutputStream
extends|extends
name|OutputStream
block|{
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|index
specifier|private
name|int
name|index
decl_stmt|;
DECL|field|startIndex
specifier|private
name|int
name|startIndex
decl_stmt|;
comment|/**    * Constructs a new output stream, with a default allocated buffer which can    * later be obtained via {@link #toByteArray()}.    */
DECL|method|UnsafeByteArrayOutputStream
specifier|public
name|UnsafeByteArrayOutputStream
parameter_list|()
block|{
name|reInit
argument_list|(
operator|new
name|byte
index|[
literal|32
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new output stream, with a given buffer. Writing will start    * at index 0 as a default.    *     * @param buffer    *            some space to which writing will be made    */
DECL|method|UnsafeByteArrayOutputStream
specifier|public
name|UnsafeByteArrayOutputStream
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|)
block|{
name|reInit
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new output stream, with a given buffer. Writing will start    * at a given index.    *     * @param buffer    *            some space to which writing will be made.    * @param startPos    *            an index (inclusive) from white data will be written.    */
DECL|method|UnsafeByteArrayOutputStream
specifier|public
name|UnsafeByteArrayOutputStream
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|startPos
parameter_list|)
block|{
name|reInit
argument_list|(
name|buffer
argument_list|,
name|startPos
argument_list|)
expr_stmt|;
block|}
DECL|method|grow
specifier|private
name|void
name|grow
parameter_list|(
name|int
name|newLength
parameter_list|)
block|{
comment|// It actually should be: (Java 1.7, when its intrinsic on all machines)
comment|// buffer = Arrays.copyOf(buffer, newLength);
name|byte
index|[]
name|newBuffer
init|=
operator|new
name|byte
index|[
name|newLength
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|newBuffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|newBuffer
expr_stmt|;
block|}
comment|/**    * For reuse-ability, this stream object can be re-initialized with another    * given buffer and starting position.    *     * @param buffer some space to which writing will be made.    * @param startPos an index (inclusive) from white data will be written.    */
DECL|method|reInit
specifier|public
name|void
name|reInit
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|startPos
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"initial buffer length must be greater than 0."
argument_list|)
throw|;
block|}
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
name|startIndex
operator|=
name|startPos
expr_stmt|;
name|index
operator|=
name|startIndex
expr_stmt|;
block|}
comment|/**    * For reuse-ability, this stream object can be re-initialized with another    * given buffer, using 0 as default starting position.    *     * @param buffer some space to which writing will be made.    */
DECL|method|reInit
specifier|public
name|void
name|reInit
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|)
block|{
name|reInit
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * writes a given byte(at the form of an int) to the buffer. If the buffer's    * empty space is insufficient, the buffer is doubled.    *     * @param value byte value to be written    */
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|index
operator|>=
name|buffer
operator|.
name|length
condition|)
block|{
name|grow
argument_list|(
name|buffer
operator|.
name|length
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
name|buffer
index|[
name|index
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|value
expr_stmt|;
block|}
comment|/**    * writes a given byte[], with offset and length to the buffer. If the    * buffer's empty space is insufficient, the buffer is doubled until it    * could contain all the data.    *     * @param b    *            byte buffer, containing the source data to be written    * @param off    *            index from which data from the buffer b should be written    * @param len    *            number of bytes that should be written    */
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
comment|// If there's not enough space for the data
name|int
name|targetLength
init|=
name|index
operator|+
name|len
decl_stmt|;
if|if
condition|(
name|targetLength
operator|>=
name|buffer
operator|.
name|length
condition|)
block|{
comment|// Calculating the new required length of the array, keeping the array
comment|// size a power of 2 if it was initialized like that.
name|int
name|newlen
init|=
name|buffer
operator|.
name|length
decl_stmt|;
while|while
condition|(
operator|(
name|newlen
operator|<<=
literal|1
operator|)
operator|<
name|targetLength
condition|)
block|{}
name|grow
argument_list|(
name|newlen
argument_list|)
expr_stmt|;
block|}
comment|// Now that we have enough spare space, we could copy the rest of the
comment|// data
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|buffer
argument_list|,
name|index
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|// Updating the index to next available index.
name|index
operator|+=
name|len
expr_stmt|;
block|}
comment|/**    * Returns the byte array saved within the buffer AS IS.    *     * @return the actual inner buffer - not a copy of it.    */
DECL|method|toByteArray
specifier|public
name|byte
index|[]
name|toByteArray
parameter_list|()
block|{
return|return
name|buffer
return|;
block|}
comment|/**    * Returns the number of relevant bytes. This objects makes sure the buffer    * is at least the size of it's data. But it can also be twice as big. The    * user would want to process the relevant bytes only. For that he would    * need the count.    *     * @return number of relevant bytes    */
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|index
return|;
block|}
comment|/**    * Returns the start position data was written to. This is useful in case you    * used {@link #reInit(byte[], int)} or    * {@link #UnsafeByteArrayOutputStream(byte[], int)} and passed a start    * position which is not 0.    */
DECL|method|getStartPos
specifier|public
name|int
name|getStartPos
parameter_list|()
block|{
return|return
name|startIndex
return|;
block|}
block|}
end_class
end_unit
