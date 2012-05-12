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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|EnumSet
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockAnalyzer
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
name|document
operator|.
name|ByteDocValuesField
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
name|document
operator|.
name|DerefBytesDocValuesField
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|DoubleDocValuesField
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FloatDocValuesField
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
name|document
operator|.
name|IntDocValuesField
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
name|document
operator|.
name|LongDocValuesField
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
name|document
operator|.
name|PackedLongDocValuesField
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
name|document
operator|.
name|ShortDocValuesField
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
name|document
operator|.
name|SortedBytesDocValuesField
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
name|document
operator|.
name|StraightBytesDocValuesField
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
name|document
operator|.
name|TextField
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
name|DocValues
operator|.
name|Source
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
name|DocValues
operator|.
name|Type
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressCodecs
import|;
end_import
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
literal|"Lucene3x"
argument_list|)
DECL|class|TestTypePromotion
specifier|public
class|class
name|TestTypePromotion
extends|extends
name|LuceneTestCase
block|{
DECL|field|INTEGERS
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|Type
argument_list|>
name|INTEGERS
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|Type
operator|.
name|VAR_INTS
argument_list|,
name|Type
operator|.
name|FIXED_INTS_16
argument_list|,
name|Type
operator|.
name|FIXED_INTS_32
argument_list|,
name|Type
operator|.
name|FIXED_INTS_64
argument_list|,
name|Type
operator|.
name|FIXED_INTS_8
argument_list|)
decl_stmt|;
DECL|field|FLOATS
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|Type
argument_list|>
name|FLOATS
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|Type
operator|.
name|FLOAT_32
argument_list|,
name|Type
operator|.
name|FLOAT_64
argument_list|)
decl_stmt|;
DECL|field|UNSORTED_BYTES
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|Type
argument_list|>
name|UNSORTED_BYTES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|Type
operator|.
name|BYTES_FIXED_DEREF
argument_list|,
name|Type
operator|.
name|BYTES_FIXED_STRAIGHT
argument_list|,
name|Type
operator|.
name|BYTES_VAR_STRAIGHT
argument_list|,
name|Type
operator|.
name|BYTES_VAR_DEREF
argument_list|)
decl_stmt|;
DECL|field|SORTED_BYTES
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|Type
argument_list|>
name|SORTED_BYTES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|Type
operator|.
name|BYTES_FIXED_SORTED
argument_list|,
name|Type
operator|.
name|BYTES_VAR_SORTED
argument_list|)
decl_stmt|;
DECL|method|randomValueType
specifier|public
name|Type
name|randomValueType
parameter_list|(
name|EnumSet
argument_list|<
name|Type
argument_list|>
name|typeEnum
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|Type
index|[]
name|array
init|=
name|typeEnum
operator|.
name|toArray
argument_list|(
operator|new
name|Type
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
return|return
name|array
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|array
operator|.
name|length
argument_list|)
index|]
return|;
block|}
DECL|enum|TestType
specifier|private
specifier|static
enum|enum
name|TestType
block|{
DECL|enum constant|Int
DECL|enum constant|Float
DECL|enum constant|Byte
name|Int
block|,
name|Float
block|,
name|Byte
block|}
DECL|method|runTest
specifier|private
name|void
name|runTest
parameter_list|(
name|EnumSet
argument_list|<
name|Type
argument_list|>
name|types
parameter_list|,
name|TestType
name|type
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|num_1
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|int
name|num_2
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|int
name|num_3
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
name|num_1
operator|+
name|num_2
operator|+
name|num_3
index|]
decl_stmt|;
name|index
argument_list|(
name|writer
argument_list|,
name|randomValueType
argument_list|(
name|types
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|values
argument_list|,
literal|0
argument_list|,
name|num_1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|index
argument_list|(
name|writer
argument_list|,
name|randomValueType
argument_list|(
name|types
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|values
argument_list|,
name|num_1
argument_list|,
name|num_2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// once in a while use addIndexes
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Directory
name|dir_2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer_2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir_2
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|index
argument_list|(
name|writer_2
argument_list|,
name|randomValueType
argument_list|(
name|types
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|values
argument_list|,
name|num_1
operator|+
name|num_2
argument_list|,
name|num_3
argument_list|)
expr_stmt|;
name|writer_2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer_2
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|dir_2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// do a real merge here
name|IndexReader
name|open
init|=
name|maybeWrapReader
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir_2
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addIndexes
argument_list|(
name|open
argument_list|)
expr_stmt|;
name|open
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir_2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|index
argument_list|(
name|writer
argument_list|,
name|randomValueType
argument_list|(
name|types
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|values
argument_list|,
name|num_1
operator|+
name|num_2
argument_list|,
name|num_3
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertValues
argument_list|(
name|type
argument_list|,
name|dir
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertValues
specifier|private
name|void
name|assertValues
parameter_list|(
name|TestType
name|type
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|long
index|[]
name|values
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|getSequentialSubReaders
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexReaderContext
name|topReaderContext
init|=
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|AtomicReaderContext
index|[]
name|children
init|=
name|topReaderContext
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|children
operator|.
name|length
argument_list|)
expr_stmt|;
name|DocValues
name|docValues
init|=
name|children
index|[
literal|0
index|]
operator|.
name|reader
argument_list|()
operator|.
name|docValues
argument_list|(
literal|"promote"
argument_list|)
decl_stmt|;
name|Source
name|directSource
init|=
name|docValues
operator|.
name|getDirectSource
argument_list|()
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
literal|"id: "
operator|+
name|id
operator|+
literal|" doc: "
operator|+
name|i
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Byte
case|:
name|BytesRef
name|bytes
init|=
name|directSource
operator|.
name|getBytes
argument_list|(
name|i
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|value
init|=
literal|0
decl_stmt|;
switch|switch
condition|(
name|bytes
operator|.
name|length
condition|)
block|{
case|case
literal|1
case|:
name|value
operator|=
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
index|]
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|value
operator|=
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
literal|0xFF
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
literal|1
index|]
operator|&
literal|0xFF
operator|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|value
operator|=
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
literal|0xFF
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
literal|1
index|]
operator|&
literal|0xFF
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
literal|2
index|]
operator|&
literal|0xFF
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
literal|3
index|]
operator|&
literal|0xFF
operator|)
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|value
operator|=
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
index|]
operator|&
literal|0xff
argument_list|)
operator|<<
literal|56
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
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
literal|0xff
argument_list|)
operator|<<
literal|48
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
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
literal|0xff
argument_list|)
operator|<<
literal|40
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
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
literal|0xff
argument_list|)
operator|<<
literal|32
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
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
literal|0xff
argument_list|)
operator|<<
literal|24
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
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
literal|0xff
argument_list|)
operator|<<
literal|16
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
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
literal|0xff
argument_list|)
operator|<<
literal|8
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
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
literal|0xff
argument_list|)
operator|)
operator|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
name|msg
operator|+
literal|" bytessize: "
operator|+
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|msg
operator|+
literal|" byteSize: "
operator|+
name|bytes
operator|.
name|length
argument_list|,
name|values
index|[
name|id
index|]
argument_list|,
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|Float
case|:
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|values
index|[
name|id
index|]
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|directSource
operator|.
name|getFloat
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Int
case|:
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|values
index|[
name|id
index|]
argument_list|,
name|directSource
operator|.
name|getInt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
name|docValues
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|void
name|index
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|Type
name|valueType
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
specifier|final
name|Field
name|valField
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: add docs "
operator|+
name|offset
operator|+
literal|"-"
operator|+
operator|(
name|offset
operator|+
name|num
operator|)
operator|+
literal|" valType="
operator|+
name|valueType
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|valueType
condition|)
block|{
case|case
name|VAR_INTS
case|:
name|valField
operator|=
operator|new
name|PackedLongDocValuesField
argument_list|(
literal|"promote"
argument_list|,
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|valField
operator|=
operator|new
name|ByteDocValuesField
argument_list|(
literal|"promote"
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
name|valField
operator|=
operator|new
name|ShortDocValuesField
argument_list|(
literal|"promote"
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|valField
operator|=
operator|new
name|IntDocValuesField
argument_list|(
literal|"promote"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
name|valField
operator|=
operator|new
name|LongDocValuesField
argument_list|(
literal|"promote"
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|valField
operator|=
operator|new
name|FloatDocValuesField
argument_list|(
literal|"promote"
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|valField
operator|=
operator|new
name|DoubleDocValuesField
argument_list|(
literal|"promote"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
name|valField
operator|=
operator|new
name|StraightBytesDocValuesField
argument_list|(
literal|"promote"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|valField
operator|=
operator|new
name|StraightBytesDocValuesField
argument_list|(
literal|"promote"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_DEREF
case|:
name|valField
operator|=
operator|new
name|DerefBytesDocValuesField
argument_list|(
literal|"promote"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_DEREF
case|:
name|valField
operator|=
operator|new
name|DerefBytesDocValuesField
argument_list|(
literal|"promote"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_SORTED
case|:
name|valField
operator|=
operator|new
name|SortedBytesDocValuesField
argument_list|(
literal|"promote"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_SORTED
case|:
name|valField
operator|=
operator|new
name|SortedBytesDocValuesField
argument_list|(
literal|"promote"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unknown Type: "
operator|+
name|valueType
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|offset
operator|+
name|num
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|i
operator|+
literal|""
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|valueType
condition|)
block|{
case|case
name|VAR_INTS
case|:
comment|// TODO: can we do nextLong()?
name|values
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|valField
operator|.
name|setLongValue
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
comment|// TODO: negatives too?
name|values
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Short
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|valField
operator|.
name|setShortValue
argument_list|(
operator|(
name|short
operator|)
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|values
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|valField
operator|.
name|setIntValue
argument_list|(
operator|(
name|int
operator|)
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
name|values
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|valField
operator|.
name|setLongValue
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
specifier|final
name|double
name|nextDouble
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|nextDouble
argument_list|)
expr_stmt|;
name|valField
operator|.
name|setDoubleValue
argument_list|(
name|nextDouble
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
specifier|final
name|float
name|nextFloat
init|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|nextFloat
argument_list|)
expr_stmt|;
name|valField
operator|.
name|setFloatValue
argument_list|(
name|nextFloat
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|values
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
name|valField
operator|.
name|setByteValue
argument_list|(
operator|(
name|byte
operator|)
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
name|values
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|ByteArrayDataOutput
name|out
init|=
operator|new
name|ByteArrayDataOutput
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|valField
operator|.
name|setBytesValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|byte
name|lbytes
index|[]
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|ByteArrayDataOutput
name|lout
init|=
operator|new
name|ByteArrayDataOutput
argument_list|(
name|lbytes
argument_list|,
literal|0
argument_list|,
literal|8
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|lout
operator|.
name|writeInt
argument_list|(
operator|(
name|int
operator|)
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|len
operator|=
literal|4
expr_stmt|;
block|}
else|else
block|{
name|values
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|lout
operator|.
name|writeLong
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|len
operator|=
literal|8
expr_stmt|;
block|}
name|valField
operator|.
name|setBytesValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|lbytes
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
literal|"unexpected value "
operator|+
name|valueType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  doc "
operator|+
name|i
operator|+
literal|" has val="
operator|+
name|valField
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|valField
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
literal|0
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testPromoteBytes
specifier|public
name|void
name|testPromoteBytes
parameter_list|()
throws|throws
name|IOException
block|{
name|runTest
argument_list|(
name|UNSORTED_BYTES
argument_list|,
name|TestType
operator|.
name|Byte
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortedPromoteBytes
specifier|public
name|void
name|testSortedPromoteBytes
parameter_list|()
throws|throws
name|IOException
block|{
name|runTest
argument_list|(
name|SORTED_BYTES
argument_list|,
name|TestType
operator|.
name|Byte
argument_list|)
expr_stmt|;
block|}
DECL|method|testPromoteInteger
specifier|public
name|void
name|testPromoteInteger
parameter_list|()
throws|throws
name|IOException
block|{
name|runTest
argument_list|(
name|INTEGERS
argument_list|,
name|TestType
operator|.
name|Int
argument_list|)
expr_stmt|;
block|}
DECL|method|testPromotFloatingPoint
specifier|public
name|void
name|testPromotFloatingPoint
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|runTest
argument_list|(
name|FLOATS
argument_list|,
name|TestType
operator|.
name|Float
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergeIncompatibleTypes
specifier|public
name|void
name|testMergeIncompatibleTypes
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|writerConfig
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|writerConfig
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|NO_COMPOUND_FILES
argument_list|)
expr_stmt|;
comment|// no merges until we are done with adding values
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|writerConfig
argument_list|)
decl_stmt|;
name|int
name|num_1
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|int
name|num_2
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
name|num_1
operator|+
name|num_2
index|]
decl_stmt|;
name|index
argument_list|(
name|writer
argument_list|,
name|randomValueType
argument_list|(
name|INTEGERS
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|values
argument_list|,
literal|0
argument_list|,
name|num_1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// once in a while use addIndexes
name|Directory
name|dir_2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer_2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir_2
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|index
argument_list|(
name|writer_2
argument_list|,
name|randomValueType
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|UNSORTED_BYTES
else|:
name|SORTED_BYTES
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|values
argument_list|,
name|num_1
argument_list|,
name|num_2
argument_list|)
expr_stmt|;
name|writer_2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer_2
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|dir_2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// do a real merge here
name|IndexReader
name|open
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir_2
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addIndexes
argument_list|(
name|open
argument_list|)
expr_stmt|;
name|open
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir_2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|index
argument_list|(
name|writer
argument_list|,
name|randomValueType
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|UNSORTED_BYTES
else|:
name|SORTED_BYTES
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|values
argument_list|,
name|num_1
argument_list|,
name|num_2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|writerConfig
operator|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|writerConfig
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|NoMergePolicy
condition|)
block|{
name|writerConfig
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure we merge to one segment (merge everything together)
block|}
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|writerConfig
argument_list|)
expr_stmt|;
comment|// now merge
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|getSequentialSubReaders
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexReaderContext
name|topReaderContext
init|=
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|AtomicReaderContext
index|[]
name|children
init|=
name|topReaderContext
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|DocValues
name|docValues
init|=
name|children
index|[
literal|0
index|]
operator|.
name|reader
argument_list|()
operator|.
name|docValues
argument_list|(
literal|"promote"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|docValues
argument_list|)
expr_stmt|;
name|assertValues
argument_list|(
name|TestType
operator|.
name|Byte
argument_list|,
name|dir
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|BYTES_VAR_STRAIGHT
argument_list|,
name|docValues
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|reader
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
