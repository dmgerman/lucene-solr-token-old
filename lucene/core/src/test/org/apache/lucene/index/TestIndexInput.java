begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|util
operator|.
name|LuceneTestCase
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
name|ByteArrayDataInput
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
name|ByteArrayDataOutput
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
name|store
operator|.
name|IndexOutput
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
name|RAMDirectory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|Random
import|;
end_import
begin_class
DECL|class|TestIndexInput
specifier|public
class|class
name|TestIndexInput
extends|extends
name|LuceneTestCase
block|{
DECL|field|READ_TEST_BYTES
specifier|static
specifier|final
name|byte
index|[]
name|READ_TEST_BYTES
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0x80
block|,
literal|0x01
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
literal|0x7F
block|,
operator|(
name|byte
operator|)
literal|0x80
block|,
operator|(
name|byte
operator|)
literal|0x80
block|,
literal|0x01
block|,
operator|(
name|byte
operator|)
literal|0x81
block|,
operator|(
name|byte
operator|)
literal|0x80
block|,
literal|0x01
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0x07
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0x0F
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0x07
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0x7F
block|,
literal|0x06
block|,
literal|'L'
block|,
literal|'u'
block|,
literal|'c'
block|,
literal|'e'
block|,
literal|'n'
block|,
literal|'e'
block|,
comment|// 2-byte UTF-8 (U+00BF "INVERTED QUESTION MARK")
literal|0x02
block|,
operator|(
name|byte
operator|)
literal|0xC2
block|,
operator|(
name|byte
operator|)
literal|0xBF
block|,
literal|0x0A
block|,
literal|'L'
block|,
literal|'u'
block|,
operator|(
name|byte
operator|)
literal|0xC2
block|,
operator|(
name|byte
operator|)
literal|0xBF
block|,
literal|'c'
block|,
literal|'e'
block|,
operator|(
name|byte
operator|)
literal|0xC2
block|,
operator|(
name|byte
operator|)
literal|0xBF
block|,
literal|'n'
block|,
literal|'e'
block|,
comment|// 3-byte UTF-8 (U+2620 "SKULL AND CROSSBONES")
literal|0x03
block|,
operator|(
name|byte
operator|)
literal|0xE2
block|,
operator|(
name|byte
operator|)
literal|0x98
block|,
operator|(
name|byte
operator|)
literal|0xA0
block|,
literal|0x0C
block|,
literal|'L'
block|,
literal|'u'
block|,
operator|(
name|byte
operator|)
literal|0xE2
block|,
operator|(
name|byte
operator|)
literal|0x98
block|,
operator|(
name|byte
operator|)
literal|0xA0
block|,
literal|'c'
block|,
literal|'e'
block|,
operator|(
name|byte
operator|)
literal|0xE2
block|,
operator|(
name|byte
operator|)
literal|0x98
block|,
operator|(
name|byte
operator|)
literal|0xA0
block|,
literal|'n'
block|,
literal|'e'
block|,
comment|// surrogate pairs
comment|// (U+1D11E "MUSICAL SYMBOL G CLEF")
comment|// (U+1D160 "MUSICAL SYMBOL EIGHTH NOTE")
literal|0x04
block|,
operator|(
name|byte
operator|)
literal|0xF0
block|,
operator|(
name|byte
operator|)
literal|0x9D
block|,
operator|(
name|byte
operator|)
literal|0x84
block|,
operator|(
name|byte
operator|)
literal|0x9E
block|,
literal|0x08
block|,
operator|(
name|byte
operator|)
literal|0xF0
block|,
operator|(
name|byte
operator|)
literal|0x9D
block|,
operator|(
name|byte
operator|)
literal|0x84
block|,
operator|(
name|byte
operator|)
literal|0x9E
block|,
operator|(
name|byte
operator|)
literal|0xF0
block|,
operator|(
name|byte
operator|)
literal|0x9D
block|,
operator|(
name|byte
operator|)
literal|0x85
block|,
operator|(
name|byte
operator|)
literal|0xA0
block|,
literal|0x0E
block|,
literal|'L'
block|,
literal|'u'
block|,
operator|(
name|byte
operator|)
literal|0xF0
block|,
operator|(
name|byte
operator|)
literal|0x9D
block|,
operator|(
name|byte
operator|)
literal|0x84
block|,
operator|(
name|byte
operator|)
literal|0x9E
block|,
literal|'c'
block|,
literal|'e'
block|,
operator|(
name|byte
operator|)
literal|0xF0
block|,
operator|(
name|byte
operator|)
literal|0x9D
block|,
operator|(
name|byte
operator|)
literal|0x85
block|,
operator|(
name|byte
operator|)
literal|0xA0
block|,
literal|'n'
block|,
literal|'e'
block|,
comment|// null bytes
literal|0x01
block|,
literal|0x00
block|,
literal|0x08
block|,
literal|'L'
block|,
literal|'u'
block|,
literal|0x00
block|,
literal|'c'
block|,
literal|'e'
block|,
literal|0x00
block|,
literal|'n'
block|,
literal|'e'
block|,
comment|// tests for Exceptions on invalid values
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0x17
block|,
operator|(
name|byte
operator|)
literal|0x01
block|,
comment|// guard value
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0x01
block|,
comment|// guard value
block|}
decl_stmt|;
DECL|field|COUNT
specifier|static
specifier|final
name|int
name|COUNT
init|=
name|RANDOM_MULTIPLIER
operator|*
literal|65536
decl_stmt|;
DECL|field|INTS
specifier|static
name|int
index|[]
name|INTS
decl_stmt|;
DECL|field|LONGS
specifier|static
name|long
index|[]
name|LONGS
decl_stmt|;
DECL|field|RANDOM_TEST_BYTES
specifier|static
name|byte
index|[]
name|RANDOM_TEST_BYTES
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|INTS
operator|=
operator|new
name|int
index|[
name|COUNT
index|]
expr_stmt|;
name|LONGS
operator|=
operator|new
name|long
index|[
name|COUNT
index|]
expr_stmt|;
name|RANDOM_TEST_BYTES
operator|=
operator|new
name|byte
index|[
name|COUNT
operator|*
operator|(
literal|5
operator|+
literal|4
operator|+
literal|9
operator|+
literal|8
operator|)
index|]
expr_stmt|;
specifier|final
name|ByteArrayDataOutput
name|bdo
init|=
operator|new
name|ByteArrayDataOutput
argument_list|(
name|RANDOM_TEST_BYTES
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
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|i1
init|=
name|INTS
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|bdo
operator|.
name|writeVInt
argument_list|(
name|i1
argument_list|)
expr_stmt|;
name|bdo
operator|.
name|writeInt
argument_list|(
name|i1
argument_list|)
expr_stmt|;
specifier|final
name|long
name|l1
decl_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
comment|// a long with lots of zeroes at the end
name|l1
operator|=
name|LONGS
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|long
operator|)
name|Math
operator|.
name|abs
argument_list|(
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
operator|)
operator|<<
literal|32
expr_stmt|;
block|}
else|else
block|{
name|l1
operator|=
name|LONGS
index|[
name|i
index|]
operator|=
name|Math
operator|.
name|abs
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bdo
operator|.
name|writeVLong
argument_list|(
name|l1
argument_list|)
expr_stmt|;
name|bdo
operator|.
name|writeLong
argument_list|(
name|l1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|INTS
operator|=
literal|null
expr_stmt|;
name|LONGS
operator|=
literal|null
expr_stmt|;
name|RANDOM_TEST_BYTES
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|checkReads
specifier|private
name|void
name|checkReads
parameter_list|(
name|DataInput
name|is
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
name|expectedEx
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|is
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16383
argument_list|,
name|is
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16384
argument_list|,
name|is
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16385
argument_list|,
name|is
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|is
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|is
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|is
operator|.
name|readVLong
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|is
operator|.
name|readVLong
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lucene"
argument_list|,
name|is
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\u00BF"
argument_list|,
name|is
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lu\u00BFce\u00BFne"
argument_list|,
name|is
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\u2620"
argument_list|,
name|is
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lu\u2620ce\u2620ne"
argument_list|,
name|is
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\uD834\uDD1E"
argument_list|,
name|is
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\uD834\uDD1E\uD834\uDD60"
argument_list|,
name|is
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lu\uD834\uDD1Ece\uD834\uDD60ne"
argument_list|,
name|is
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\u0000"
argument_list|,
name|is
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lu\u0000ce\u0000ne"
argument_list|,
name|is
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|is
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw "
operator|+
name|expectedEx
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Invalid vInt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expectedEx
operator|.
name|isInstance
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|is
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
comment|// guard value
try|try
block|{
name|is
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw "
operator|+
name|expectedEx
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Invalid vLong"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expectedEx
operator|.
name|isInstance
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|is
operator|.
name|readVLong
argument_list|()
argument_list|)
expr_stmt|;
comment|// guard value
block|}
DECL|method|checkRandomReads
specifier|private
name|void
name|checkRandomReads
parameter_list|(
name|DataInput
name|is
parameter_list|)
throws|throws
name|IOException
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
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|INTS
index|[
name|i
index|]
argument_list|,
name|is
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|INTS
index|[
name|i
index|]
argument_list|,
name|is
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LONGS
index|[
name|i
index|]
argument_list|,
name|is
operator|.
name|readVLong
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LONGS
index|[
name|i
index|]
argument_list|,
name|is
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// this test only checks BufferedIndexInput because MockIndexInput extends BufferedIndexInput
DECL|method|testBufferedIndexInputRead
specifier|public
name|void
name|testBufferedIndexInputRead
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexInput
name|is
init|=
operator|new
name|MockIndexInput
argument_list|(
name|READ_TEST_BYTES
argument_list|)
decl_stmt|;
name|checkReads
argument_list|(
name|is
argument_list|,
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|is
operator|=
operator|new
name|MockIndexInput
argument_list|(
name|RANDOM_TEST_BYTES
argument_list|)
expr_stmt|;
name|checkRandomReads
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// this test checks the raw IndexInput methods as it uses RAMIndexInput which extends IndexInput directly
DECL|method|testRawIndexInputRead
specifier|public
name|void
name|testRawIndexInputRead
parameter_list|()
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|os
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeBytes
argument_list|(
name|READ_TEST_BYTES
argument_list|,
name|READ_TEST_BYTES
operator|.
name|length
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|is
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"foo"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|checkReads
argument_list|(
name|is
argument_list|,
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|os
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"bar"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeBytes
argument_list|(
name|RANDOM_TEST_BYTES
argument_list|,
name|RANDOM_TEST_BYTES
operator|.
name|length
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|is
operator|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"bar"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
name|checkRandomReads
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|is
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
DECL|method|testByteArrayDataInput
specifier|public
name|void
name|testByteArrayDataInput
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayDataInput
name|is
init|=
operator|new
name|ByteArrayDataInput
argument_list|(
name|READ_TEST_BYTES
argument_list|)
decl_stmt|;
name|checkReads
argument_list|(
name|is
argument_list|,
name|RuntimeException
operator|.
name|class
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|ByteArrayDataInput
argument_list|(
name|RANDOM_TEST_BYTES
argument_list|)
expr_stmt|;
name|checkRandomReads
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
