begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|CannedBinaryTokenStream
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
name|LegacyDoubleField
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
name|LegacyFloatField
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
name|LegacyIntField
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
name|LegacyLongField
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
name|LegacyNumericUtils
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
begin_class
DECL|class|TestTerms
specifier|public
class|class
name|TestTerms
extends|extends
name|LuceneTestCase
block|{
DECL|method|testTermMinMaxBasic
specifier|public
name|void
name|testTermMinMaxBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
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
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|"a b c cc ddd"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|terms
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"ddd"
argument_list|)
argument_list|,
name|terms
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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
DECL|method|testTermMinMaxRandom
specifier|public
name|void
name|testTermMinMaxRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
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
literal|100
argument_list|)
decl_stmt|;
name|BytesRef
name|minTerm
init|=
literal|null
decl_stmt|;
name|BytesRef
name|maxTerm
init|=
literal|null
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
operator|new
name|TextField
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|//System.out.println("  doc " + i);
name|CannedBinaryTokenStream
operator|.
name|BinaryToken
index|[]
name|tokens
init|=
operator|new
name|CannedBinaryTokenStream
operator|.
name|BinaryToken
index|[
name|atLeast
argument_list|(
literal|10
argument_list|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|tokens
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|BytesRef
name|tokenBytes
init|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
comment|//System.out.println("    token " + tokenBytes);
if|if
condition|(
name|minTerm
operator|==
literal|null
operator|||
name|tokenBytes
operator|.
name|compareTo
argument_list|(
name|minTerm
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|//System.out.println("      ** new min");
name|minTerm
operator|=
name|tokenBytes
expr_stmt|;
block|}
if|if
condition|(
name|maxTerm
operator|==
literal|null
operator|||
name|tokenBytes
operator|.
name|compareTo
argument_list|(
name|maxTerm
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|//System.out.println("      ** new max");
name|maxTerm
operator|=
name|tokenBytes
expr_stmt|;
block|}
name|tokens
index|[
name|j
index|]
operator|=
operator|new
name|CannedBinaryTokenStream
operator|.
name|BinaryToken
argument_list|(
name|tokenBytes
argument_list|)
expr_stmt|;
block|}
name|field
operator|.
name|setTokenStream
argument_list|(
operator|new
name|CannedBinaryTokenStream
argument_list|(
name|tokens
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|minTerm
argument_list|,
name|terms
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|maxTerm
argument_list|,
name|terms
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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
DECL|method|testEmptyIntFieldMinMax
specifier|public
name|void
name|testEmptyIntFieldMinMax
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|LegacyNumericUtils
operator|.
name|getMinInt
argument_list|(
name|EMPTY_TERMS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|LegacyNumericUtils
operator|.
name|getMaxInt
argument_list|(
name|EMPTY_TERMS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntFieldMinMax
specifier|public
name|void
name|testIntFieldMinMax
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
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
literal|100
argument_list|)
decl_stmt|;
name|int
name|minValue
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|maxValue
init|=
name|Integer
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
name|numDocs
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
name|int
name|num
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|num
argument_list|,
name|minValue
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|num
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LegacyIntField
argument_list|(
literal|"field"
argument_list|,
name|num
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
name|minValue
argument_list|)
argument_list|,
name|LegacyNumericUtils
operator|.
name|getMinInt
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
name|maxValue
argument_list|)
argument_list|,
name|LegacyNumericUtils
operator|.
name|getMaxInt
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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
DECL|method|testEmptyLongFieldMinMax
specifier|public
name|void
name|testEmptyLongFieldMinMax
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|LegacyNumericUtils
operator|.
name|getMinLong
argument_list|(
name|EMPTY_TERMS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|LegacyNumericUtils
operator|.
name|getMaxLong
argument_list|(
name|EMPTY_TERMS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongFieldMinMax
specifier|public
name|void
name|testLongFieldMinMax
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
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
literal|100
argument_list|)
decl_stmt|;
name|long
name|minValue
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|maxValue
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
name|numDocs
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
name|long
name|num
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|num
argument_list|,
name|minValue
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|num
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LegacyLongField
argument_list|(
literal|"field"
argument_list|,
name|num
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
name|minValue
argument_list|)
argument_list|,
name|LegacyNumericUtils
operator|.
name|getMinLong
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
name|maxValue
argument_list|)
argument_list|,
name|LegacyNumericUtils
operator|.
name|getMaxLong
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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
DECL|method|testFloatFieldMinMax
specifier|public
name|void
name|testFloatFieldMinMax
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
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
literal|100
argument_list|)
decl_stmt|;
name|float
name|minValue
init|=
name|Float
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|float
name|maxValue
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|float
name|num
init|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|num
argument_list|,
name|minValue
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|num
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LegacyFloatField
argument_list|(
literal|"field"
argument_list|,
name|num
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|minValue
argument_list|,
name|LegacyNumericUtils
operator|.
name|sortableIntToFloat
argument_list|(
name|LegacyNumericUtils
operator|.
name|getMinInt
argument_list|(
name|terms
argument_list|)
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|maxValue
argument_list|,
name|LegacyNumericUtils
operator|.
name|sortableIntToFloat
argument_list|(
name|LegacyNumericUtils
operator|.
name|getMaxInt
argument_list|(
name|terms
argument_list|)
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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
DECL|method|testDoubleFieldMinMax
specifier|public
name|void
name|testDoubleFieldMinMax
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
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
literal|100
argument_list|)
decl_stmt|;
name|double
name|minValue
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxValue
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|double
name|num
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|num
argument_list|,
name|minValue
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|num
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LegacyDoubleField
argument_list|(
literal|"field"
argument_list|,
name|num
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|minValue
argument_list|,
name|LegacyNumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
name|LegacyNumericUtils
operator|.
name|getMinLong
argument_list|(
name|terms
argument_list|)
argument_list|)
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|maxValue
argument_list|,
name|LegacyNumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
name|LegacyNumericUtils
operator|.
name|getMaxLong
argument_list|(
name|terms
argument_list|)
argument_list|)
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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
comment|/**    * A complete empty Terms instance that has no terms in it and supports no optional statistics    */
DECL|field|EMPTY_TERMS
specifier|private
specifier|static
name|Terms
name|EMPTY_TERMS
init|=
operator|new
name|Terms
argument_list|()
block|{
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
block|{
return|return
name|TermsEnum
operator|.
name|EMPTY
return|;
block|}
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|long
name|getSumDocFreq
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|int
name|getDocCount
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|boolean
name|hasFreqs
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|hasOffsets
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|hasPositions
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
block|}
end_class
end_unit
