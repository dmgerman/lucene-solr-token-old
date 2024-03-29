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
name|store
operator|.
name|RAMDirectory
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
name|packed
operator|.
name|PackedInts
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
import|;
end_import
begin_comment
comment|/** Extends {@link BaseDocValuesFormatTestCase} to add compression checks. */
end_comment
begin_class
DECL|class|BaseCompressingDocValuesFormatTestCase
specifier|public
specifier|abstract
class|class
name|BaseCompressingDocValuesFormatTestCase
extends|extends
name|BaseDocValuesFormatTestCase
block|{
DECL|method|dirSize
specifier|static
name|long
name|dirSize
parameter_list|(
name|Directory
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|d
operator|.
name|listAll
argument_list|()
control|)
block|{
name|size
operator|+=
name|d
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
DECL|method|testUniqueValuesCompression
specifier|public
name|void
name|testUniqueValuesCompression
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|IndexWriter
name|iwriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
specifier|final
name|int
name|uniqueValueCount
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
literal|256
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|NumericDocValuesField
name|dvf
init|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dvf
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
literal|300
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|value
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|<
name|uniqueValueCount
condition|)
block|{
name|value
operator|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
name|dvf
operator|.
name|setLongValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iwriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|long
name|size1
init|=
name|dirSize
argument_list|(
name|dir
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
literal|20
condition|;
operator|++
name|i
control|)
block|{
name|dvf
operator|.
name|setLongValue
argument_list|(
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iwriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|long
name|size2
init|=
name|dirSize
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|// make sure the new longs did not cost 8 bytes each
name|assertTrue
argument_list|(
name|size2
operator|<
name|size1
operator|+
literal|8
operator|*
literal|20
argument_list|)
expr_stmt|;
block|}
DECL|method|testDateCompression
specifier|public
name|void
name|testDateCompression
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|IndexWriter
name|iwriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
specifier|final
name|long
name|base
init|=
literal|13
decl_stmt|;
comment|// prime
specifier|final
name|long
name|day
init|=
literal|1000L
operator|*
literal|60
operator|*
literal|60
operator|*
literal|24
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|NumericDocValuesField
name|dvf
init|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dvf
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
literal|300
condition|;
operator|++
name|i
control|)
block|{
name|dvf
operator|.
name|setLongValue
argument_list|(
name|base
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
operator|*
name|day
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iwriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|long
name|size1
init|=
name|dirSize
argument_list|(
name|dir
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
literal|50
condition|;
operator|++
name|i
control|)
block|{
name|dvf
operator|.
name|setLongValue
argument_list|(
name|base
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
operator|*
name|day
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iwriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|long
name|size2
init|=
name|dirSize
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|// make sure the new longs costed less than if they had only been packed
name|assertTrue
argument_list|(
name|size2
operator|<
name|size1
operator|+
operator|(
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|day
argument_list|)
operator|*
literal|50
operator|)
operator|/
literal|8
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleBigValueCompression
specifier|public
name|void
name|testSingleBigValueCompression
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|IndexWriter
name|iwriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|NumericDocValuesField
name|dvf
init|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dvf
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
literal|20000
condition|;
operator|++
name|i
control|)
block|{
name|dvf
operator|.
name|setLongValue
argument_list|(
name|i
operator|&
literal|1023
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iwriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|long
name|size1
init|=
name|dirSize
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|dvf
operator|.
name|setLongValue
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|long
name|size2
init|=
name|dirSize
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|// make sure the new value did not grow the bpv for every other value
name|assertTrue
argument_list|(
name|size2
operator|<
name|size1
operator|+
operator|(
literal|20000
operator|*
operator|(
literal|63
operator|-
literal|10
operator|)
operator|)
operator|/
literal|8
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
