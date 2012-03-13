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
name|codecs
operator|.
name|Codec
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
name|MockDirectoryWrapper
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
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_comment
comment|/**  *   */
end_comment
begin_class
DECL|class|TestCustomNorms
specifier|public
class|class
name|TestCustomNorms
extends|extends
name|LuceneTestCase
block|{
DECL|field|floatTestField
specifier|final
name|String
name|floatTestField
init|=
literal|"normsTestFloat"
decl_stmt|;
DECL|field|exceptionTestField
specifier|final
name|String
name|exceptionTestField
init|=
literal|"normsTestExcp"
decl_stmt|;
annotation|@
name|Before
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
name|assumeFalse
argument_list|(
literal|"cannot work with preflex codec"
argument_list|,
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Lucene3x"
argument_list|)
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
literal|"cannot work with simple text codec"
argument_list|,
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"SimpleText"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFloatNorms
specifier|public
name|void
name|testFloatNorms
parameter_list|()
throws|throws
name|IOException
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// can't set sim to checkindex yet
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
argument_list|)
argument_list|)
decl_stmt|;
name|Similarity
name|provider
init|=
operator|new
name|MySimProvider
argument_list|()
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
name|float
name|nextFloat
init|=
name|random
operator|.
name|nextFloat
argument_list|()
decl_stmt|;
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
name|floatTestField
argument_list|,
literal|""
operator|+
name|nextFloat
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
name|nextFloat
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
name|floatTestField
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
name|AtomicReader
name|open
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
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
name|floatTestField
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
name|FLOAT_32
argument_list|,
name|normValues
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|float
index|[]
name|norms
init|=
operator|(
name|float
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
name|float
name|expected
init|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|document
operator|.
name|get
argument_list|(
name|floatTestField
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|norms
index|[
name|i
index|]
argument_list|,
literal|0.0f
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
DECL|method|testExceptionOnRandomType
specifier|public
name|void
name|testExceptionOnRandomType
parameter_list|()
throws|throws
name|IOException
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// can't set sim to checkindex yet
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
argument_list|)
argument_list|)
decl_stmt|;
name|Similarity
name|provider
init|=
operator|new
name|MySimProvider
argument_list|()
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
try|try
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
name|float
name|nextFloat
init|=
name|random
operator|.
name|nextFloat
argument_list|()
decl_stmt|;
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
name|exceptionTestField
argument_list|,
literal|""
operator|+
name|nextFloat
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
name|nextFloat
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
name|exceptionTestField
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
name|fail
argument_list|(
literal|"expected exception - incompatible types"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
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
name|dir
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
name|floatTestField
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
operator|new
name|FloatEncodingBoostSimilarity
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|exceptionTestField
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
operator|new
name|RandomTypeSimilarity
argument_list|(
name|random
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
DECL|class|FloatEncodingBoostSimilarity
specifier|public
specifier|static
class|class
name|FloatEncodingBoostSimilarity
extends|extends
name|DefaultSimilarity
block|{
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
name|float
name|boost
init|=
name|state
operator|.
name|getBoost
argument_list|()
decl_stmt|;
name|norm
operator|.
name|setFloat
argument_list|(
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|RandomTypeSimilarity
specifier|public
specifier|static
class|class
name|RandomTypeSimilarity
extends|extends
name|DefaultSimilarity
block|{
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|RandomTypeSimilarity
specifier|public
name|RandomTypeSimilarity
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
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
name|float
name|boost
init|=
name|state
operator|.
name|getBoost
argument_list|()
decl_stmt|;
name|int
name|nextInt
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|nextInt
condition|)
block|{
case|case
literal|0
case|:
name|norm
operator|.
name|setDouble
argument_list|(
operator|(
name|double
operator|)
name|boost
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|norm
operator|.
name|setFloat
argument_list|(
name|boost
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|norm
operator|.
name|setLong
argument_list|(
operator|(
name|long
operator|)
name|boost
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|norm
operator|.
name|setBytes
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[
literal|6
index|]
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|norm
operator|.
name|setInt
argument_list|(
operator|(
name|int
operator|)
name|boost
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|norm
operator|.
name|setShort
argument_list|(
operator|(
name|short
operator|)
name|boost
argument_list|)
expr_stmt|;
break|break;
default|default:
name|norm
operator|.
name|setByte
argument_list|(
operator|(
name|byte
operator|)
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
