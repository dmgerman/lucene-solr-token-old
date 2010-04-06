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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|LuceneTestCase
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
name|java
operator|.
name|util
operator|.
name|Random
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
begin_class
DECL|class|TestPackedInts
specifier|public
class|class
name|TestPackedInts
extends|extends
name|LuceneTestCase
block|{
DECL|field|rnd
specifier|private
name|Random
name|rnd
decl_stmt|;
DECL|method|testBitsRequired
specifier|public
name|void
name|testBitsRequired
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|61
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
operator|(
name|long
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
literal|61
argument_list|)
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|61
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
literal|0x1FFFFFFFFFFFFFFFL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|62
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
literal|0x3FFFFFFFFFFFFFFFL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|63
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
literal|0x7FFFFFFFFFFFFFFFL
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxValues
specifier|public
name|void
name|testMaxValues
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"1 bit -> max == 1"
argument_list|,
literal|1
argument_list|,
name|PackedInts
operator|.
name|maxValue
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2 bit -> max == 3"
argument_list|,
literal|3
argument_list|,
name|PackedInts
operator|.
name|maxValue
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"8 bit -> max == 255"
argument_list|,
literal|255
argument_list|,
name|PackedInts
operator|.
name|maxValue
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"63 bit -> max == Long.MAX_VALUE"
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|PackedInts
operator|.
name|maxValue
argument_list|(
literal|63
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"64 bit -> max == Long.MAX_VALUE (same as for 63 bit)"
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|PackedInts
operator|.
name|maxValue
argument_list|(
literal|64
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPackedInts
specifier|public
name|void
name|testPackedInts
parameter_list|()
throws|throws
name|IOException
block|{
name|rnd
operator|=
name|newRandom
argument_list|()
expr_stmt|;
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
condition|;
name|iter
operator|++
control|)
block|{
name|long
name|ceil
init|=
literal|2
decl_stmt|;
for|for
control|(
name|int
name|nbits
init|=
literal|1
init|;
name|nbits
operator|<
literal|63
condition|;
name|nbits
operator|++
control|)
block|{
specifier|final
name|int
name|valueCount
init|=
literal|100
operator|+
name|rnd
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
decl_stmt|;
specifier|final
name|Directory
name|d
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|out
init|=
name|d
operator|.
name|createOutput
argument_list|(
literal|"out.bin"
argument_list|)
decl_stmt|;
name|PackedInts
operator|.
name|Writer
name|w
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|out
argument_list|,
name|valueCount
argument_list|,
name|nbits
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
name|valueCount
index|]
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
name|valueCount
condition|;
name|i
operator|++
control|)
block|{
name|long
name|v
init|=
name|rnd
operator|.
name|nextLong
argument_list|()
operator|%
name|ceil
decl_stmt|;
if|if
condition|(
name|v
operator|<
literal|0
condition|)
block|{
name|v
operator|=
operator|-
name|v
expr_stmt|;
block|}
name|values
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|w
operator|.
name|add
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
specifier|final
name|long
name|fp
init|=
name|out
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|in
init|=
name|d
operator|.
name|openInput
argument_list|(
literal|"out.bin"
argument_list|)
decl_stmt|;
name|PackedInts
operator|.
name|Reader
name|r
init|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fp
argument_list|,
name|in
operator|.
name|getFilePointer
argument_list|()
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
name|valueCount
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"index="
operator|+
name|i
operator|+
literal|" ceil="
operator|+
name|ceil
operator|+
literal|" valueCount="
operator|+
name|valueCount
operator|+
literal|" nbits="
operator|+
name|nbits
operator|+
literal|" for "
operator|+
name|r
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|values
index|[
name|i
index|]
argument_list|,
name|r
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|=
name|d
operator|.
name|openInput
argument_list|(
literal|"out.bin"
argument_list|)
expr_stmt|;
name|PackedInts
operator|.
name|ReaderIterator
name|r2
init|=
name|PackedInts
operator|.
name|getReaderIterator
argument_list|(
name|in
argument_list|)
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
name|valueCount
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"index="
operator|+
name|i
operator|+
literal|" ceil="
operator|+
name|ceil
operator|+
literal|" valueCount="
operator|+
name|valueCount
operator|+
literal|" nbits="
operator|+
name|nbits
operator|+
literal|" for "
operator|+
name|r
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|values
index|[
name|i
index|]
argument_list|,
name|r2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|fp
argument_list|,
name|in
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|ceil
operator|*=
literal|2
expr_stmt|;
block|}
block|}
block|}
DECL|method|testControlledEquality
specifier|public
name|void
name|testControlledEquality
parameter_list|()
block|{
specifier|final
name|int
name|VALUE_COUNT
init|=
literal|255
decl_stmt|;
specifier|final
name|int
name|BITS_PER_VALUE
init|=
literal|8
decl_stmt|;
name|List
argument_list|<
name|PackedInts
operator|.
name|Mutable
argument_list|>
name|packedInts
init|=
name|createPackedInts
argument_list|(
name|VALUE_COUNT
argument_list|,
name|BITS_PER_VALUE
argument_list|)
decl_stmt|;
for|for
control|(
name|PackedInts
operator|.
name|Mutable
name|packedInt
range|:
name|packedInts
control|)
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
name|packedInt
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|packedInt
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|assertListEquality
argument_list|(
name|packedInts
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomEquality
specifier|public
name|void
name|testRandomEquality
parameter_list|()
block|{
specifier|final
name|int
index|[]
name|VALUE_COUNTS
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|5
block|,
literal|8
block|,
literal|100
block|,
literal|500
block|}
decl_stmt|;
specifier|final
name|int
name|MIN_BITS_PER_VALUE
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|MAX_BITS_PER_VALUE
init|=
literal|64
decl_stmt|;
name|rnd
operator|=
name|newRandom
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|valueCount
range|:
name|VALUE_COUNTS
control|)
block|{
for|for
control|(
name|int
name|bitsPerValue
init|=
name|MIN_BITS_PER_VALUE
init|;
name|bitsPerValue
operator|<=
name|MAX_BITS_PER_VALUE
condition|;
name|bitsPerValue
operator|++
control|)
block|{
name|assertRandomEquality
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|,
name|rnd
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertRandomEquality
specifier|private
name|void
name|assertRandomEquality
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|,
name|long
name|randomSeed
parameter_list|)
block|{
name|List
argument_list|<
name|PackedInts
operator|.
name|Mutable
argument_list|>
name|packedInts
init|=
name|createPackedInts
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
for|for
control|(
name|PackedInts
operator|.
name|Mutable
name|packedInt
range|:
name|packedInts
control|)
block|{
try|try
block|{
name|fill
argument_list|(
name|packedInt
argument_list|,
call|(
name|long
call|)
argument_list|(
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|bitsPerValue
argument_list|)
operator|-
literal|1
argument_list|)
argument_list|,
name|randomSeed
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Exception while filling %s: valueCount=%d, bitsPerValue=%s"
argument_list|,
name|packedInt
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertListEquality
argument_list|(
name|packedInts
argument_list|)
expr_stmt|;
block|}
DECL|method|createPackedInts
specifier|private
name|List
argument_list|<
name|PackedInts
operator|.
name|Mutable
argument_list|>
name|createPackedInts
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
name|List
argument_list|<
name|PackedInts
operator|.
name|Mutable
argument_list|>
name|packedInts
init|=
operator|new
name|ArrayList
argument_list|<
name|PackedInts
operator|.
name|Mutable
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitsPerValue
operator|<=
literal|8
condition|)
block|{
name|packedInts
operator|.
name|add
argument_list|(
operator|new
name|Direct8
argument_list|(
name|valueCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bitsPerValue
operator|<=
literal|16
condition|)
block|{
name|packedInts
operator|.
name|add
argument_list|(
operator|new
name|Direct16
argument_list|(
name|valueCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bitsPerValue
operator|<=
literal|31
condition|)
block|{
name|packedInts
operator|.
name|add
argument_list|(
operator|new
name|Packed32
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bitsPerValue
operator|<=
literal|32
condition|)
block|{
name|packedInts
operator|.
name|add
argument_list|(
operator|new
name|Direct32
argument_list|(
name|valueCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bitsPerValue
operator|<=
literal|63
condition|)
block|{
name|packedInts
operator|.
name|add
argument_list|(
operator|new
name|Packed64
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|packedInts
operator|.
name|add
argument_list|(
operator|new
name|Direct64
argument_list|(
name|valueCount
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|packedInts
return|;
block|}
DECL|method|fill
specifier|private
name|void
name|fill
parameter_list|(
name|PackedInts
operator|.
name|Mutable
name|packedInt
parameter_list|,
name|long
name|maxValue
parameter_list|,
name|long
name|randomSeed
parameter_list|)
block|{
name|Random
name|rnd2
init|=
operator|new
name|Random
argument_list|(
name|randomSeed
argument_list|)
decl_stmt|;
name|maxValue
operator|++
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
name|packedInt
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|long
name|value
init|=
name|Math
operator|.
name|abs
argument_list|(
name|rnd2
operator|.
name|nextLong
argument_list|()
operator|%
name|maxValue
argument_list|)
decl_stmt|;
name|packedInt
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"The set/get of the value at index %d should match for %s"
argument_list|,
name|i
argument_list|,
name|packedInt
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|,
name|packedInt
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertListEquality
specifier|private
name|void
name|assertListEquality
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|PackedInts
operator|.
name|Reader
argument_list|>
name|packedInts
parameter_list|)
block|{
name|assertListEquality
argument_list|(
literal|""
argument_list|,
name|packedInts
argument_list|)
expr_stmt|;
block|}
DECL|method|assertListEquality
specifier|private
name|void
name|assertListEquality
parameter_list|(
name|String
name|message
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|PackedInts
operator|.
name|Reader
argument_list|>
name|packedInts
parameter_list|)
block|{
if|if
condition|(
name|packedInts
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|PackedInts
operator|.
name|Reader
name|base
init|=
name|packedInts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|valueCount
init|=
name|base
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|PackedInts
operator|.
name|Reader
name|packedInt
range|:
name|packedInts
control|)
block|{
name|assertEquals
argument_list|(
name|message
operator|+
literal|". The number of values should be the same "
argument_list|,
name|valueCount
argument_list|,
name|packedInt
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|packedInts
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s. The value at index %d should be the same for %s and %s"
argument_list|,
name|message
argument_list|,
name|i
argument_list|,
name|base
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|packedInts
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|,
name|base
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|packedInts
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSingleValue
specifier|public
name|void
name|testSingleValue
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"out"
argument_list|)
decl_stmt|;
name|PackedInts
operator|.
name|Writer
name|w
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|out
argument_list|,
literal|1
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|w
operator|.
name|add
argument_list|(
literal|17
argument_list|)
expr_stmt|;
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
specifier|final
name|long
name|end
init|=
name|out
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"out"
argument_list|)
decl_stmt|;
name|PackedInts
operator|.
name|Reader
name|r
init|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|end
argument_list|,
name|in
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
