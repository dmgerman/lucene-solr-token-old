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
name|search
operator|.
name|similarities
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
name|similarities
operator|.
name|PerFieldSimilarityWrapper
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
name|similarities
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
name|LineFileDocs
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
comment|/**  * Test that norms info is preserved during index life - including  * separate norms, addDocument, addIndexes, forceMerge.  */
end_comment
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"SimpleText"
block|,
literal|"Memory"
block|}
argument_list|)
DECL|class|TestNorms
specifier|public
class|class
name|TestNorms
extends|extends
name|LuceneTestCase
block|{
DECL|field|byteTestField
specifier|final
name|String
name|byteTestField
init|=
literal|"normsTestByte"
decl_stmt|;
DECL|class|CustomNormEncodingSimilarity
class|class
name|CustomNormEncodingSimilarity
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
name|void
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|,
name|Norm
name|norm
parameter_list|)
block|{
name|norm
operator|.
name|setByte
argument_list|(
name|encodeNormValue
argument_list|(
operator|(
name|float
operator|)
name|state
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// LUCENE-1260
DECL|method|testCustomEncoder
specifier|public
name|void
name|testCustomEncoder
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
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|config
operator|.
name|setSimilarity
argument_list|(
operator|new
name|CustomNormEncodingSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
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
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|Field
name|bar
init|=
name|newField
argument_list|(
literal|"bar"
argument_list|,
literal|""
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|bar
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
name|bar
operator|.
name|setStringValue
argument_list|(
literal|"singleton"
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
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
name|fooNorms
index|[]
init|=
operator|(
name|byte
index|[]
operator|)
name|MultiDocValues
operator|.
name|getNormDocValues
argument_list|(
name|reader
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|getSource
argument_list|()
operator|.
name|getArray
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
literal|0
argument_list|,
name|fooNorms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|byte
name|barNorms
index|[]
init|=
operator|(
name|byte
index|[]
operator|)
name|MultiDocValues
operator|.
name|getNormDocValues
argument_list|(
name|reader
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|getSource
argument_list|()
operator|.
name|getArray
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
literal|1
argument_list|,
name|barNorms
index|[
name|i
index|]
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
DECL|method|testMaxByteNorms
specifier|public
name|void
name|testMaxByteNorms
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"TestNorms.testMaxByteNorms"
argument_list|)
argument_list|)
decl_stmt|;
name|buildIndex
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|AtomicReader
name|open
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|)
decl_stmt|;
name|DocValues
name|normValues
init|=
name|open
operator|.
name|normValues
argument_list|(
name|byteTestField
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|normValues
argument_list|)
expr_stmt|;
name|Source
name|source
init|=
name|normValues
operator|.
name|getSource
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|source
operator|.
name|hasArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|FIXED_INTS_8
argument_list|,
name|normValues
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|norms
init|=
operator|(
name|byte
index|[]
operator|)
name|source
operator|.
name|getArray
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
name|open
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|document
init|=
name|open
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|document
operator|.
name|get
argument_list|(
name|byteTestField
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|expected
argument_list|,
name|norms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|open
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
comment|/**    * this test randomly creates segments with or without norms but not omitting    * norms. The similarity used doesn't write a norm value if writeNorms = false is    * passed. This differs from omitNorm since norms are simply not written for this segment    * while merging fills in default values based on the Norm {@link Type}    */
DECL|method|testNormsNotPresent
specifier|public
name|void
name|testNormsNotPresent
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"TestNorms.testNormsNotPresent.1"
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|firstWriteNorm
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|buildIndex
argument_list|(
name|dir
argument_list|,
name|firstWriteNorm
argument_list|)
expr_stmt|;
name|Directory
name|otherDir
init|=
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"TestNorms.testNormsNotPresent.2"
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|secondWriteNorm
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|buildIndex
argument_list|(
name|otherDir
argument_list|,
name|secondWriteNorm
argument_list|)
expr_stmt|;
name|AtomicReader
name|reader
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|otherDir
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfos
name|fieldInfos
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|byteTestField
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fieldInfo
operator|.
name|omitsNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldInfo
operator|.
name|isIndexed
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|secondWriteNorm
condition|)
block|{
name|assertTrue
argument_list|(
name|fieldInfo
operator|.
name|hasNorms
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|fieldInfo
operator|.
name|hasNorms
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addIndexes
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|AtomicReader
name|mergedReader
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|firstWriteNorm
operator|&&
operator|!
name|secondWriteNorm
condition|)
block|{
name|DocValues
name|normValues
init|=
name|mergedReader
operator|.
name|normValues
argument_list|(
name|byteTestField
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|normValues
argument_list|)
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|mergedReader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|byteTestField
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fi
operator|.
name|omitsNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fi
operator|.
name|isIndexed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fi
operator|.
name|hasNorms
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FieldInfo
name|fi
init|=
name|mergedReader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|byteTestField
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fi
operator|.
name|omitsNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fi
operator|.
name|isIndexed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fi
operator|.
name|hasNorms
argument_list|()
argument_list|)
expr_stmt|;
name|DocValues
name|normValues
init|=
name|mergedReader
operator|.
name|normValues
argument_list|(
name|byteTestField
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|normValues
argument_list|)
expr_stmt|;
name|Source
name|source
init|=
name|normValues
operator|.
name|getSource
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|source
operator|.
name|hasArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|FIXED_INTS_8
argument_list|,
name|normValues
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|norms
init|=
operator|(
name|byte
index|[]
operator|)
name|source
operator|.
name|getArray
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
name|mergedReader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|document
init|=
name|mergedReader
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|document
operator|.
name|get
argument_list|(
name|byteTestField
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|expected
argument_list|,
name|norms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|mergedReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|otherDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|buildIndex
specifier|public
name|void
name|buildIndex
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|boolean
name|writeNorms
parameter_list|)
throws|throws
name|IOException
throws|,
name|CorruptIndexException
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
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
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Similarity
name|provider
init|=
operator|new
name|MySimProvider
argument_list|(
name|writeNorms
argument_list|)
decl_stmt|;
name|config
operator|.
name|setSimilarity
argument_list|(
name|provider
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
specifier|final
name|LineFileDocs
name|docs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|100
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
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|int
name|boost
init|=
name|writeNorms
condition|?
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|255
argument_list|)
else|:
literal|0
decl_stmt|;
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
name|byteTestField
argument_list|,
literal|""
operator|+
name|boost
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|f
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
name|byteTestField
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|docs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|MySimProvider
specifier|public
class|class
name|MySimProvider
extends|extends
name|PerFieldSimilarityWrapper
block|{
DECL|field|delegate
name|Similarity
name|delegate
init|=
operator|new
name|DefaultSimilarity
argument_list|()
decl_stmt|;
DECL|field|writeNorms
specifier|private
name|boolean
name|writeNorms
decl_stmt|;
DECL|method|MySimProvider
specifier|public
name|MySimProvider
parameter_list|(
name|boolean
name|writeNorms
parameter_list|)
block|{
name|this
operator|.
name|writeNorms
operator|=
name|writeNorms
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|queryNorm
argument_list|(
name|sumOfSquaredWeights
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|byteTestField
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
operator|new
name|ByteEncodingBoostSimilarity
argument_list|(
name|writeNorms
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|delegate
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|coord
argument_list|(
name|overlap
argument_list|,
name|maxOverlap
argument_list|)
return|;
block|}
block|}
DECL|class|ByteEncodingBoostSimilarity
specifier|public
specifier|static
class|class
name|ByteEncodingBoostSimilarity
extends|extends
name|DefaultSimilarity
block|{
DECL|field|writeNorms
specifier|private
name|boolean
name|writeNorms
decl_stmt|;
DECL|method|ByteEncodingBoostSimilarity
specifier|public
name|ByteEncodingBoostSimilarity
parameter_list|(
name|boolean
name|writeNorms
parameter_list|)
block|{
name|this
operator|.
name|writeNorms
operator|=
name|writeNorms
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|void
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|,
name|Norm
name|norm
parameter_list|)
block|{
if|if
condition|(
name|writeNorms
condition|)
block|{
name|int
name|boost
init|=
operator|(
name|int
operator|)
name|state
operator|.
name|getBoost
argument_list|()
decl_stmt|;
name|norm
operator|.
name|setByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0xFF
operator|&
name|boost
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
