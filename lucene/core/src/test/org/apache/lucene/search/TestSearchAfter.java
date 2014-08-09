begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|Arrays
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
name|document
operator|.
name|BinaryDocValuesField
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
name|DoubleField
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
name|FloatField
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
name|IntField
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
name|LongField
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
name|NumericDocValuesField
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
name|SortedDocValuesField
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
name|StoredField
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
name|IndexReader
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
name|RandomIndexWriter
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
name|Term
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
name|English
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
name|TestUtil
import|;
end_import
begin_comment
comment|/**  * Tests IndexSearcher's searchAfter() method  */
end_comment
begin_class
DECL|class|TestSearchAfter
specifier|public
class|class
name|TestSearchAfter
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|iter
specifier|private
name|int
name|iter
decl_stmt|;
DECL|field|allSortFields
specifier|private
name|List
argument_list|<
name|SortField
argument_list|>
name|allSortFields
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|allSortFields
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|SortField
index|[]
block|{
operator|new
name|SortField
argument_list|(
literal|"int"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"long"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"float"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"double"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"bytes"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"bytesval"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING_VAL
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"intdocvalues"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"floatdocvalues"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"sortedbytesdocvalues"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"sortedbytesdocvaluesval"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING_VAL
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"straightbytesdocvalues"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING_VAL
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"int"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"long"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"float"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"double"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"bytes"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"bytesval"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING_VAL
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"intdocvalues"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"floatdocvalues"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"sortedbytesdocvalues"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"sortedbytesdocvaluesval"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING_VAL
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"straightbytesdocvalues"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING_VAL
argument_list|,
literal|true
argument_list|)
block|,
name|SortField
operator|.
name|FIELD_SCORE
block|,
name|SortField
operator|.
name|FIELD_DOC
block|,         }
argument_list|)
argument_list|)
expr_stmt|;
comment|// Also test missing first / last for the "string" sorts:
for|for
control|(
name|String
name|field
range|:
operator|new
name|String
index|[]
block|{
literal|"bytes"
block|,
literal|"sortedbytesdocvalues"
block|}
control|)
block|{
for|for
control|(
name|int
name|rev
init|=
literal|0
init|;
name|rev
operator|<
literal|2
condition|;
name|rev
operator|++
control|)
block|{
name|boolean
name|reversed
init|=
name|rev
operator|==
literal|0
decl_stmt|;
name|SortField
name|sf
init|=
operator|new
name|SortField
argument_list|(
name|field
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
name|reversed
argument_list|)
decl_stmt|;
name|sf
operator|.
name|setMissingValue
argument_list|(
name|SortField
operator|.
name|STRING_FIRST
argument_list|)
expr_stmt|;
name|allSortFields
operator|.
name|add
argument_list|(
name|sf
argument_list|)
expr_stmt|;
name|sf
operator|=
operator|new
name|SortField
argument_list|(
name|field
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
name|reversed
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setMissingValue
argument_list|(
name|SortField
operator|.
name|STRING_LAST
argument_list|)
expr_stmt|;
name|allSortFields
operator|.
name|add
argument_list|(
name|sf
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Also test missing first / last for the "string_val" sorts:
for|for
control|(
name|String
name|field
range|:
operator|new
name|String
index|[]
block|{
literal|"sortedbytesdocvaluesval"
block|,
literal|"straightbytesdocvalues"
block|}
control|)
block|{
for|for
control|(
name|int
name|rev
init|=
literal|0
init|;
name|rev
operator|<
literal|2
condition|;
name|rev
operator|++
control|)
block|{
name|boolean
name|reversed
init|=
name|rev
operator|==
literal|0
decl_stmt|;
name|SortField
name|sf
init|=
operator|new
name|SortField
argument_list|(
name|field
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING_VAL
argument_list|,
name|reversed
argument_list|)
decl_stmt|;
name|sf
operator|.
name|setMissingValue
argument_list|(
name|SortField
operator|.
name|STRING_FIRST
argument_list|)
expr_stmt|;
name|allSortFields
operator|.
name|add
argument_list|(
name|sf
argument_list|)
expr_stmt|;
name|sf
operator|=
operator|new
name|SortField
argument_list|(
name|field
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING_VAL
argument_list|,
name|reversed
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setMissingValue
argument_list|(
name|SortField
operator|.
name|STRING_LAST
argument_list|)
expr_stmt|;
name|allSortFields
operator|.
name|add
argument_list|(
name|sf
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|limit
init|=
name|allSortFields
operator|.
name|size
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
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|SortField
name|sf
init|=
name|allSortFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|Type
operator|.
name|INT
condition|)
block|{
name|SortField
name|sf2
init|=
operator|new
name|SortField
argument_list|(
name|sf
operator|.
name|getField
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
name|sf
operator|.
name|getReverse
argument_list|()
argument_list|)
decl_stmt|;
name|sf2
operator|.
name|setMissingValue
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
name|allSortFields
operator|.
name|add
argument_list|(
name|sf2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sf
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|Type
operator|.
name|LONG
condition|)
block|{
name|SortField
name|sf2
init|=
operator|new
name|SortField
argument_list|(
name|sf
operator|.
name|getField
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
name|sf
operator|.
name|getReverse
argument_list|()
argument_list|)
decl_stmt|;
name|sf2
operator|.
name|setMissingValue
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|allSortFields
operator|.
name|add
argument_list|(
name|sf2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sf
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
condition|)
block|{
name|SortField
name|sf2
init|=
operator|new
name|SortField
argument_list|(
name|sf
operator|.
name|getField
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
name|sf
operator|.
name|getReverse
argument_list|()
argument_list|)
decl_stmt|;
name|sf2
operator|.
name|setMissingValue
argument_list|(
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
argument_list|)
expr_stmt|;
name|allSortFields
operator|.
name|add
argument_list|(
name|sf2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sf
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
condition|)
block|{
name|SortField
name|sf2
init|=
operator|new
name|SortField
argument_list|(
name|sf
operator|.
name|getField
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
argument_list|,
name|sf
operator|.
name|getReverse
argument_list|()
argument_list|)
decl_stmt|;
name|sf2
operator|.
name|setMissingValue
argument_list|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
argument_list|)
expr_stmt|;
name|allSortFields
operator|.
name|add
argument_list|(
name|sf2
argument_list|)
expr_stmt|;
block|}
block|}
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|200
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|Field
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"english"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"oddeven"
argument_list|,
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
operator|)
condition|?
literal|"even"
else|:
literal|"odd"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"byte"
argument_list|,
literal|""
operator|+
operator|(
operator|(
name|byte
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"short"
argument_list|,
literal|""
operator|+
operator|(
operator|(
name|short
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|IntField
argument_list|(
literal|"int"
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|LongField
argument_list|(
literal|"long"
argument_list|,
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"float"
argument_list|,
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|DoubleField
argument_list|(
literal|"double"
argument_list|,
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"bytes"
argument_list|,
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"bytesval"
argument_list|,
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|DoubleField
argument_list|(
literal|"double"
argument_list|,
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"intdocvalues"
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|FloatDocValuesField
argument_list|(
literal|"floatdocvalues"
argument_list|,
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
literal|"sortedbytesdocvalues"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
literal|"sortedbytesdocvaluesval"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"straightbytesdocvalues"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"  add doc id="
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
comment|// So we are sometimes missing that field:
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|!=
literal|4
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
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
literal|"    "
operator|+
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
operator|==
literal|17
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
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
literal|"  searcher="
operator|+
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testQueries
specifier|public
name|void
name|testQueries
parameter_list|()
throws|throws
name|Exception
block|{
comment|// because the first page has a null 'after', we get a normal collector.
comment|// so we need to run the test a few times to ensure we will collect multiple
comment|// pages.
name|int
name|n
init|=
name|atLeast
argument_list|(
literal|20
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|Filter
name|odd
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"oddeven"
argument_list|,
literal|"odd"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"english"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|odd
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"english"
argument_list|,
literal|"four"
argument_list|)
argument_list|)
argument_list|,
name|odd
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"english"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"oddeven"
argument_list|,
literal|"even"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|bq
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertQuery
name|void
name|assertQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|)
throws|throws
name|Exception
block|{
name|assertQuery
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|Sort
operator|.
name|RELEVANCE
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|Sort
operator|.
name|INDEXORDER
argument_list|)
expr_stmt|;
for|for
control|(
name|SortField
name|sortField
range|:
name|allSortFields
control|)
block|{
name|assertQuery
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
index|[]
block|{
name|sortField
block|}
argument_list|)
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|assertQuery
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|getRandomSort
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getRandomSort
name|Sort
name|getRandomSort
parameter_list|()
block|{
name|SortField
index|[]
name|sortFields
init|=
operator|new
name|SortField
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|7
argument_list|)
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
name|sortFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sortFields
index|[
name|i
index|]
operator|=
name|allSortFields
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|allSortFields
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Sort
argument_list|(
name|sortFields
argument_list|)
return|;
block|}
DECL|method|assertQuery
name|void
name|assertQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|maxDoc
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|TopDocs
name|all
decl_stmt|;
name|int
name|pageSize
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|maxDoc
operator|*
literal|2
argument_list|)
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
literal|"\nassertQuery "
operator|+
operator|(
name|iter
operator|++
operator|)
operator|+
literal|": query="
operator|+
name|query
operator|+
literal|" filter="
operator|+
name|filter
operator|+
literal|" sort="
operator|+
name|sort
operator|+
literal|" pageSize="
operator|+
name|pageSize
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
name|doMaxScore
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|doScores
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
name|all
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sort
operator|==
name|Sort
operator|.
name|RELEVANCE
condition|)
block|{
name|all
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|maxDoc
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
name|doMaxScore
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|all
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|maxDoc
argument_list|,
name|sort
argument_list|,
name|doScores
argument_list|,
name|doMaxScore
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
literal|"  all.totalHits="
operator|+
name|all
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|all
operator|.
name|scoreDocs
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    hit "
operator|+
operator|(
name|upto
operator|++
operator|)
operator|+
literal|": id="
operator|+
name|searcher
operator|.
name|doc
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|+
literal|" "
operator|+
name|scoreDoc
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|pageStart
init|=
literal|0
decl_stmt|;
name|ScoreDoc
name|lastBottom
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|pageStart
operator|<
name|all
operator|.
name|totalHits
condition|)
block|{
name|TopDocs
name|paged
decl_stmt|;
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
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
literal|"  iter lastBottom="
operator|+
name|lastBottom
argument_list|)
expr_stmt|;
block|}
name|paged
operator|=
name|searcher
operator|.
name|searchAfter
argument_list|(
name|lastBottom
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|pageSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
literal|"  iter lastBottom="
operator|+
name|lastBottom
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sort
operator|==
name|Sort
operator|.
name|RELEVANCE
condition|)
block|{
name|paged
operator|=
name|searcher
operator|.
name|searchAfter
argument_list|(
name|lastBottom
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|pageSize
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
name|doMaxScore
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|paged
operator|=
name|searcher
operator|.
name|searchAfter
argument_list|(
name|lastBottom
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|pageSize
argument_list|,
name|sort
argument_list|,
name|doScores
argument_list|,
name|doMaxScore
argument_list|)
expr_stmt|;
block|}
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
literal|"    "
operator|+
name|paged
operator|.
name|scoreDocs
operator|.
name|length
operator|+
literal|" hits on page"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|paged
operator|.
name|scoreDocs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|assertPage
argument_list|(
name|pageStart
argument_list|,
name|all
argument_list|,
name|paged
argument_list|)
expr_stmt|;
name|pageStart
operator|+=
name|paged
operator|.
name|scoreDocs
operator|.
name|length
expr_stmt|;
name|lastBottom
operator|=
name|paged
operator|.
name|scoreDocs
index|[
name|paged
operator|.
name|scoreDocs
operator|.
name|length
operator|-
literal|1
index|]
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|all
operator|.
name|scoreDocs
operator|.
name|length
argument_list|,
name|pageStart
argument_list|)
expr_stmt|;
block|}
DECL|method|assertPage
name|void
name|assertPage
parameter_list|(
name|int
name|pageStart
parameter_list|,
name|TopDocs
name|all
parameter_list|,
name|TopDocs
name|paged
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|all
operator|.
name|totalHits
argument_list|,
name|paged
operator|.
name|totalHits
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
name|paged
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ScoreDoc
name|sd1
init|=
name|all
operator|.
name|scoreDocs
index|[
name|pageStart
operator|+
name|i
index|]
decl_stmt|;
name|ScoreDoc
name|sd2
init|=
name|paged
operator|.
name|scoreDocs
index|[
name|i
index|]
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
literal|"    hit "
operator|+
operator|(
name|pageStart
operator|+
name|i
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"      expected id="
operator|+
name|searcher
operator|.
name|doc
argument_list|(
name|sd1
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|+
literal|" "
operator|+
name|sd1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"        actual id="
operator|+
name|searcher
operator|.
name|doc
argument_list|(
name|sd2
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|+
literal|" "
operator|+
name|sd2
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|sd1
operator|.
name|doc
argument_list|,
name|sd2
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sd1
operator|.
name|score
argument_list|,
name|sd2
operator|.
name|score
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
if|if
condition|(
name|sd1
operator|instanceof
name|FieldDoc
condition|)
block|{
name|assertTrue
argument_list|(
name|sd2
operator|instanceof
name|FieldDoc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|FieldDoc
operator|)
name|sd1
operator|)
operator|.
name|fields
argument_list|,
operator|(
operator|(
name|FieldDoc
operator|)
name|sd2
operator|)
operator|.
name|fields
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
