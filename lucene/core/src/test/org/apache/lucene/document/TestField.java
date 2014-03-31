begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|CannedTokenStream
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
name|Token
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
begin_comment
comment|// sanity check some basics of fields
end_comment
begin_class
DECL|class|TestField
specifier|public
class|class
name|TestField
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDoubleField
specifier|public
name|void
name|testDoubleField
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|fields
index|[]
init|=
operator|new
name|Field
index|[]
block|{
operator|new
name|DoubleField
argument_list|(
literal|"foo"
argument_list|,
literal|5d
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
block|,
operator|new
name|DoubleField
argument_list|(
literal|"foo"
argument_list|,
literal|5d
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setDoubleValue
argument_list|(
literal|6d
argument_list|)
expr_stmt|;
comment|// ok
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6d
argument_list|,
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|doubleValue
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDoubleDocValuesField
specifier|public
name|void
name|testDoubleDocValuesField
parameter_list|()
throws|throws
name|Exception
block|{
name|DoubleDocValuesField
name|field
init|=
operator|new
name|DoubleDocValuesField
argument_list|(
literal|"foo"
argument_list|,
literal|5d
argument_list|)
decl_stmt|;
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setDoubleValue
argument_list|(
literal|6d
argument_list|)
expr_stmt|;
comment|// ok
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6d
argument_list|,
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
block|}
DECL|method|testFloatDocValuesField
specifier|public
name|void
name|testFloatDocValuesField
parameter_list|()
throws|throws
name|Exception
block|{
name|FloatDocValuesField
name|field
init|=
operator|new
name|FloatDocValuesField
argument_list|(
literal|"foo"
argument_list|,
literal|5f
argument_list|)
decl_stmt|;
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setFloatValue
argument_list|(
literal|6f
argument_list|)
expr_stmt|;
comment|// ok
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6f
argument_list|,
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
DECL|method|testFloatField
specifier|public
name|void
name|testFloatField
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|fields
index|[]
init|=
operator|new
name|Field
index|[]
block|{
operator|new
name|FloatField
argument_list|(
literal|"foo"
argument_list|,
literal|5f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
block|,
operator|new
name|FloatField
argument_list|(
literal|"foo"
argument_list|,
literal|5f
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setFloatValue
argument_list|(
literal|6f
argument_list|)
expr_stmt|;
comment|// ok
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6f
argument_list|,
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|floatValue
argument_list|()
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIntField
specifier|public
name|void
name|testIntField
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|fields
index|[]
init|=
operator|new
name|Field
index|[]
block|{
operator|new
name|IntField
argument_list|(
literal|"foo"
argument_list|,
literal|5
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
block|,
operator|new
name|IntField
argument_list|(
literal|"foo"
argument_list|,
literal|5
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setIntValue
argument_list|(
literal|6
argument_list|)
expr_stmt|;
comment|// ok
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNumericDocValuesField
specifier|public
name|void
name|testNumericDocValuesField
parameter_list|()
throws|throws
name|Exception
block|{
name|NumericDocValuesField
name|field
init|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"foo"
argument_list|,
literal|5L
argument_list|)
decl_stmt|;
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setLongValue
argument_list|(
literal|6
argument_list|)
expr_stmt|;
comment|// ok
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6L
argument_list|,
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongField
specifier|public
name|void
name|testLongField
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|fields
index|[]
init|=
operator|new
name|Field
index|[]
block|{
operator|new
name|LongField
argument_list|(
literal|"foo"
argument_list|,
literal|5L
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
block|,
operator|new
name|LongField
argument_list|(
literal|"foo"
argument_list|,
literal|5L
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setLongValue
argument_list|(
literal|6
argument_list|)
expr_stmt|;
comment|// ok
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6L
argument_list|,
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortedBytesDocValuesField
specifier|public
name|void
name|testSortedBytesDocValuesField
parameter_list|()
throws|throws
name|Exception
block|{
name|SortedDocValuesField
name|field
init|=
operator|new
name|SortedDocValuesField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setBytesValue
argument_list|(
literal|"fubar"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|field
operator|.
name|setBytesValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"baz"
argument_list|)
argument_list|,
name|field
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBinaryDocValuesField
specifier|public
name|void
name|testBinaryDocValuesField
parameter_list|()
throws|throws
name|Exception
block|{
name|BinaryDocValuesField
name|field
init|=
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setBytesValue
argument_list|(
literal|"fubar"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|field
operator|.
name|setBytesValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"baz"
argument_list|)
argument_list|,
name|field
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringField
specifier|public
name|void
name|testStringField
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|fields
index|[]
init|=
operator|new
name|Field
index|[]
block|{
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
block|,
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"baz"
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"baz"
argument_list|,
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTextFieldString
specifier|public
name|void
name|testTextFieldString
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|fields
index|[]
init|=
operator|new
name|Field
index|[]
block|{
operator|new
name|TextField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
block|,
operator|new
name|TextField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
name|field
operator|.
name|setBoost
argument_list|(
literal|5f
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"baz"
argument_list|)
expr_stmt|;
name|field
operator|.
name|setTokenStream
argument_list|(
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"baz"
argument_list|,
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5f
argument_list|,
name|field
operator|.
name|boost
argument_list|()
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTextFieldReader
specifier|public
name|void
name|testTextFieldReader
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|field
init|=
operator|new
name|TextField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|field
operator|.
name|setBoost
argument_list|(
literal|5f
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setReaderValue
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setTokenStream
argument_list|(
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|field
operator|.
name|readerValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5f
argument_list|,
name|field
operator|.
name|boost
argument_list|()
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
block|}
comment|/* TODO: this is pretty expert and crazy    * see if we can fix it up later   public void testTextFieldTokenStream() throws Exception {   }   */
DECL|method|testStoredFieldBytes
specifier|public
name|void
name|testStoredFieldBytes
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|fields
index|[]
init|=
operator|new
name|Field
index|[]
block|{
operator|new
name|StoredField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
block|,
operator|new
name|StoredField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
operator|new
name|StoredField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
block|,     }
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setBytesValue
argument_list|(
literal|"baz"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|field
operator|.
name|setBytesValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"baz"
argument_list|)
argument_list|,
name|field
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testStoredFieldString
specifier|public
name|void
name|testStoredFieldString
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|field
init|=
operator|new
name|StoredField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"baz"
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"baz"
argument_list|,
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStoredFieldInt
specifier|public
name|void
name|testStoredFieldInt
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|field
init|=
operator|new
name|StoredField
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setIntValue
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStoredFieldDouble
specifier|public
name|void
name|testStoredFieldDouble
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|field
init|=
operator|new
name|StoredField
argument_list|(
literal|"foo"
argument_list|,
literal|1D
argument_list|)
decl_stmt|;
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setDoubleValue
argument_list|(
literal|5D
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5D
argument_list|,
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|doubleValue
argument_list|()
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
block|}
DECL|method|testStoredFieldFloat
specifier|public
name|void
name|testStoredFieldFloat
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|field
init|=
operator|new
name|StoredField
argument_list|(
literal|"foo"
argument_list|,
literal|1F
argument_list|)
decl_stmt|;
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setFloatValue
argument_list|(
literal|5f
argument_list|)
expr_stmt|;
name|trySetLongValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5f
argument_list|,
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|floatValue
argument_list|()
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
DECL|method|testStoredFieldLong
specifier|public
name|void
name|testStoredFieldLong
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|field
init|=
operator|new
name|StoredField
argument_list|(
literal|"foo"
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
name|trySetBoost
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetByteValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetBytesRefValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetDoubleValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetIntValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetFloatValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setLongValue
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|trySetReaderValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetShortValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetStringValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|trySetTokenStreamValue
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5L
argument_list|,
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|trySetByteValue
specifier|private
name|void
name|trySetByteValue
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setByteValue
argument_list|(
operator|(
name|byte
operator|)
literal|10
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|trySetBytesValue
specifier|private
name|void
name|trySetBytesValue
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setBytesValue
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|5
block|,
literal|5
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|trySetBytesRefValue
specifier|private
name|void
name|trySetBytesRefValue
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setBytesValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"bogus"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|trySetDoubleValue
specifier|private
name|void
name|trySetDoubleValue
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setDoubleValue
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|trySetIntValue
specifier|private
name|void
name|trySetIntValue
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setIntValue
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|trySetLongValue
specifier|private
name|void
name|trySetLongValue
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setLongValue
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|trySetFloatValue
specifier|private
name|void
name|trySetFloatValue
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setFloatValue
argument_list|(
name|Float
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|trySetReaderValue
specifier|private
name|void
name|trySetReaderValue
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setReaderValue
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"BOO!"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|trySetShortValue
specifier|private
name|void
name|trySetShortValue
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setShortValue
argument_list|(
name|Short
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|trySetStringValue
specifier|private
name|void
name|trySetStringValue
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setStringValue
argument_list|(
literal|"BOO!"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|trySetTokenStreamValue
specifier|private
name|void
name|trySetTokenStreamValue
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setTokenStream
argument_list|(
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|trySetBoost
specifier|private
name|void
name|trySetBoost
parameter_list|(
name|Field
name|f
parameter_list|)
block|{
try|try
block|{
name|f
operator|.
name|setBoost
argument_list|(
literal|5.0f
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class
end_unit
