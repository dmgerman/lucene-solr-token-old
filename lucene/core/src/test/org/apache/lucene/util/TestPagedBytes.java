begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|util
operator|.
name|*
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
begin_class
DECL|class|TestPagedBytes
specifier|public
class|class
name|TestPagedBytes
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDataInputOutput
specifier|public
name|void
name|testDataInputOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|5
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|int
name|blockBits
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|blockSize
init|=
literal|1
operator|<<
name|blockBits
decl_stmt|;
specifier|final
name|PagedBytes
name|p
init|=
operator|new
name|PagedBytes
argument_list|(
name|blockBits
argument_list|)
decl_stmt|;
specifier|final
name|DataOutput
name|out
init|=
name|p
operator|.
name|getDataOutput
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numBytes
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000000
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|answer
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|answer
argument_list|)
expr_stmt|;
name|int
name|written
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|written
operator|<
name|numBytes
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|answer
index|[
name|written
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|chunk
init|=
name|Math
operator|.
name|min
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|numBytes
operator|-
name|written
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|answer
argument_list|,
name|written
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|written
operator|+=
name|chunk
expr_stmt|;
block|}
block|}
specifier|final
name|PagedBytes
operator|.
name|Reader
name|reader
init|=
name|p
operator|.
name|freeze
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|DataInput
name|in
init|=
name|p
operator|.
name|getDataInput
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|verify
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
name|int
name|read
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|read
operator|<
name|numBytes
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
name|verify
index|[
name|read
operator|++
index|]
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|int
name|chunk
init|=
name|Math
operator|.
name|min
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|numBytes
operator|-
name|read
argument_list|)
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|verify
argument_list|,
name|read
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|read
operator|+=
name|chunk
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|answer
argument_list|,
name|verify
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|slice
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iter2
init|=
literal|0
init|;
name|iter2
operator|<
literal|100
condition|;
name|iter2
operator|++
control|)
block|{
specifier|final
name|int
name|pos
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|numBytes
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|blockSize
operator|+
literal|1
argument_list|,
name|numBytes
operator|-
name|pos
argument_list|)
argument_list|)
decl_stmt|;
name|reader
operator|.
name|fillSlice
argument_list|(
name|slice
argument_list|,
name|pos
argument_list|,
name|len
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|byteUpto
init|=
literal|0
init|;
name|byteUpto
operator|<
name|len
condition|;
name|byteUpto
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|answer
index|[
name|pos
operator|+
name|byteUpto
index|]
argument_list|,
name|slice
operator|.
name|bytes
index|[
name|slice
operator|.
name|offset
operator|+
name|byteUpto
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testLengthPrefix
specifier|public
name|void
name|testLengthPrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|5
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|int
name|blockBits
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|blockSize
init|=
literal|1
operator|<<
name|blockBits
decl_stmt|;
specifier|final
name|PagedBytes
name|p
init|=
operator|new
name|PagedBytes
argument_list|(
name|blockBits
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|addresses
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|answers
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|totBytes
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|totBytes
operator|<
literal|10000000
operator|&&
name|answers
operator|.
name|size
argument_list|()
operator|<
literal|100000
condition|)
block|{
specifier|final
name|int
name|len
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|blockSize
operator|-
literal|2
argument_list|,
literal|32768
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|b
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|b
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
name|b
operator|.
name|length
operator|=
name|len
expr_stmt|;
name|b
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|b
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|answers
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|addresses
operator|.
name|add
argument_list|(
operator|(
name|int
operator|)
name|p
operator|.
name|copyUsingLengthPrefix
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|totBytes
operator|+=
name|len
expr_stmt|;
block|}
specifier|final
name|PagedBytes
operator|.
name|Reader
name|reader
init|=
name|p
operator|.
name|freeze
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|slice
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|answers
operator|.
name|size
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
name|reader
operator|.
name|fillSliceWithPrefix
argument_list|(
name|slice
argument_list|,
name|addresses
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|answers
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|,
name|slice
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// LUCENE-3841: even though
comment|// copyUsingLengthPrefix will never span two blocks, make
comment|// sure if caller writes their own prefix followed by the
comment|// bytes, it still works:
DECL|method|testLengthPrefixAcrossTwoBlocks
specifier|public
name|void
name|testLengthPrefixAcrossTwoBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|PagedBytes
name|p
init|=
operator|new
name|PagedBytes
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|DataOutput
name|out
init|=
name|p
operator|.
name|getDataOutput
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes1
init|=
operator|new
name|byte
index|[
literal|1000
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|bytes1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|bytes1
argument_list|,
literal|0
argument_list|,
name|bytes1
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|40
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|bytes2
init|=
operator|new
name|byte
index|[
literal|40
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|bytes2
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|bytes2
argument_list|,
literal|0
argument_list|,
name|bytes2
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|PagedBytes
operator|.
name|Reader
name|reader
init|=
name|p
operator|.
name|freeze
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|answer
init|=
name|reader
operator|.
name|fillSliceWithPrefix
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|answer
operator|.
name|length
argument_list|)
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
literal|40
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|bytes2
index|[
name|i
index|]
argument_list|,
name|answer
operator|.
name|bytes
index|[
name|answer
operator|.
name|offset
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
