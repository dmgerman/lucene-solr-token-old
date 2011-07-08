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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Collections
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
name|analysis
operator|.
name|MockTokenizer
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
name|search
operator|.
name|DefaultSimilarity
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
name|search
operator|.
name|DefaultSimilarityProvider
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
name|search
operator|.
name|Similarity
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
name|_TestUtil
import|;
end_import
begin_comment
comment|/**  * Tests the maxTermFrequency statistic in FieldInvertState  */
end_comment
begin_class
DECL|class|TestMaxTermFrequency
specifier|public
class|class
name|TestMaxTermFrequency
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
comment|/* expected maxTermFrequency values for our documents */
DECL|field|expected
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|expected
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|config
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|config
operator|.
name|setSimilarityProvider
argument_list|(
operator|new
name|DefaultSimilarityProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|TestSimilarity
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|foo
init|=
name|newField
argument_list|(
literal|"foo"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|foo
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|foo
operator|.
name|setValue
argument_list|(
name|addValue
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
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
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
name|fooNorms
index|[]
init|=
name|MultiNorms
operator|.
name|norms
argument_list|(
name|reader
argument_list|,
literal|"foo"
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
name|assertEquals
argument_list|(
name|expected
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|fooNorms
index|[
name|i
index|]
operator|&
literal|0xff
argument_list|)
expr_stmt|;
block|}
comment|/**    * Makes a bunch of single-char tokens (the max freq will at most be 255).    * shuffles them around, and returns the whole list with Arrays.toString().    * This works fine because we use lettertokenizer.    * puts the max-frequency term into expected, to be checked against the norm.    */
DECL|method|addValue
specifier|private
name|String
name|addValue
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|maxCeiling
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|0
argument_list|,
literal|255
argument_list|)
decl_stmt|;
name|int
name|max
init|=
literal|0
decl_stmt|;
for|for
control|(
name|char
name|ch
init|=
literal|'a'
init|;
name|ch
operator|<=
literal|'z'
condition|;
name|ch
operator|++
control|)
block|{
name|int
name|num
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|0
argument_list|,
name|maxCeiling
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
name|num
condition|;
name|i
operator|++
control|)
name|terms
operator|.
name|add
argument_list|(
name|Character
operator|.
name|toString
argument_list|(
name|ch
argument_list|)
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
name|expected
operator|.
name|add
argument_list|(
name|max
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|terms
argument_list|,
name|random
argument_list|)
expr_stmt|;
return|return
name|Arrays
operator|.
name|toString
argument_list|(
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Simple similarity that encodes maxTermFrequency directly as a byte    */
DECL|class|TestSimilarity
class|class
name|TestSimilarity
extends|extends
name|DefaultSimilarity
block|{
annotation|@
name|Override
DECL|method|encodeNormValue
specifier|public
name|byte
name|encodeNormValue
parameter_list|(
name|float
name|f
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|f
return|;
block|}
annotation|@
name|Override
DECL|method|decodeNormValue
specifier|public
name|float
name|decodeNormValue
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|b
return|;
block|}
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|byte
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
name|encodeNormValue
argument_list|(
operator|(
name|float
operator|)
name|state
operator|.
name|getMaxTermFrequency
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
