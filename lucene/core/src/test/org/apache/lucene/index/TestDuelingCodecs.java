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
name|SortedNumericDocValuesField
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
name|SortedSetDocValuesField
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
name|IOUtils
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
begin_comment
comment|/**  * Compares one codec against another  */
end_comment
begin_class
DECL|class|TestDuelingCodecs
specifier|public
class|class
name|TestDuelingCodecs
extends|extends
name|LuceneTestCase
block|{
DECL|field|leftDir
specifier|private
name|Directory
name|leftDir
decl_stmt|;
DECL|field|leftReader
specifier|private
name|IndexReader
name|leftReader
decl_stmt|;
DECL|field|leftCodec
specifier|private
name|Codec
name|leftCodec
decl_stmt|;
DECL|field|rightDir
specifier|private
name|Directory
name|rightDir
decl_stmt|;
DECL|field|rightReader
specifier|private
name|IndexReader
name|rightReader
decl_stmt|;
DECL|field|rightCodec
specifier|private
name|Codec
name|rightCodec
decl_stmt|;
DECL|field|leftWriter
specifier|private
name|RandomIndexWriter
name|leftWriter
decl_stmt|;
DECL|field|rightWriter
specifier|private
name|RandomIndexWriter
name|rightWriter
decl_stmt|;
DECL|field|seed
specifier|private
name|long
name|seed
decl_stmt|;
DECL|field|info
specifier|private
name|String
name|info
decl_stmt|;
comment|// for debugging
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
comment|// for now it's SimpleText vs Default(random postings format)
comment|// as this gives the best overall coverage. when we have more
comment|// codecs we should probably pick 2 from Codec.availableCodecs()
name|leftCodec
operator|=
name|Codec
operator|.
name|forName
argument_list|(
literal|"SimpleText"
argument_list|)
expr_stmt|;
name|rightCodec
operator|=
operator|new
name|RandomCodec
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|leftDir
operator|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"leftDir"
argument_list|)
argument_list|)
expr_stmt|;
name|rightDir
operator|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"rightDir"
argument_list|)
argument_list|)
expr_stmt|;
name|seed
operator|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
expr_stmt|;
comment|// must use same seed because of random payloads, etc
name|int
name|maxTermLength
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
name|IndexWriter
operator|.
name|MAX_TERM_LENGTH
argument_list|)
decl_stmt|;
name|MockAnalyzer
name|leftAnalyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
decl_stmt|;
name|leftAnalyzer
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTermLength
argument_list|)
expr_stmt|;
name|MockAnalyzer
name|rightAnalyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
decl_stmt|;
name|rightAnalyzer
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTermLength
argument_list|)
expr_stmt|;
comment|// but these can be different
comment|// TODO: this turns this into a really big test of Multi*, is that what we want?
name|IndexWriterConfig
name|leftConfig
init|=
name|newIndexWriterConfig
argument_list|(
name|leftAnalyzer
argument_list|)
decl_stmt|;
name|leftConfig
operator|.
name|setCodec
argument_list|(
name|leftCodec
argument_list|)
expr_stmt|;
comment|// preserve docids
name|leftConfig
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|rightConfig
init|=
name|newIndexWriterConfig
argument_list|(
name|rightAnalyzer
argument_list|)
decl_stmt|;
name|rightConfig
operator|.
name|setCodec
argument_list|(
name|rightCodec
argument_list|)
expr_stmt|;
comment|// preserve docids
name|rightConfig
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
comment|// must use same seed because of random docvalues fields, etc
name|leftWriter
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|,
name|leftDir
argument_list|,
name|leftConfig
argument_list|)
expr_stmt|;
name|rightWriter
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|,
name|rightDir
argument_list|,
name|rightConfig
argument_list|)
expr_stmt|;
name|info
operator|=
literal|"left: "
operator|+
name|leftCodec
operator|.
name|toString
argument_list|()
operator|+
literal|" / right: "
operator|+
name|rightCodec
operator|.
name|toString
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
name|IOUtils
operator|.
name|close
argument_list|(
name|leftWriter
argument_list|,
name|rightWriter
argument_list|,
name|leftReader
argument_list|,
name|rightReader
argument_list|,
name|leftDir
argument_list|,
name|rightDir
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**    * populates a writer with random stuff. this must be fully reproducable with the seed!    */
DECL|method|createRandomIndex
specifier|public
specifier|static
name|void
name|createRandomIndex
parameter_list|(
name|int
name|numdocs
parameter_list|,
name|RandomIndexWriter
name|writer
parameter_list|,
name|long
name|seed
parameter_list|)
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
comment|// primary source for our data is from linefiledocs, it's realistic.
name|LineFileDocs
name|lineFileDocs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|)
decl_stmt|;
comment|// TODO: we should add other fields that use things like docs&freqs but omit positions,
comment|// because linefiledocs doesn't cover all the possibilities.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numdocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|document
init|=
name|lineFileDocs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
comment|// grab the title and add some SortedSet instances for fun
name|String
name|title
init|=
name|document
operator|.
name|get
argument_list|(
literal|"titleTokenized"
argument_list|)
decl_stmt|;
name|String
name|split
index|[]
init|=
name|title
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
name|document
operator|.
name|removeFields
argument_list|(
literal|"sortedset"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|trash
range|:
name|split
control|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"sortedset"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|trash
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// add a numeric dv field sometimes
name|document
operator|.
name|removeFields
argument_list|(
literal|"sparsenumeric"
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|2
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"sparsenumeric"
argument_list|,
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// add sortednumeric sometimes
name|document
operator|.
name|removeFields
argument_list|(
literal|"sparsesortednum"
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|1
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"sparsesortednum"
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"sparsesortednum"
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|lineFileDocs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * checks the two indexes are equivalent    */
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numdocs
init|=
name|TEST_NIGHTLY
condition|?
name|atLeast
argument_list|(
literal|2000
argument_list|)
else|:
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|createRandomIndex
argument_list|(
name|numdocs
argument_list|,
name|leftWriter
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|createRandomIndex
argument_list|(
name|numdocs
argument_list|,
name|rightWriter
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|leftReader
operator|=
name|leftWriter
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|rightReader
operator|=
name|rightWriter
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|assertReaderEquals
argument_list|(
name|info
argument_list|,
name|leftReader
argument_list|,
name|rightReader
argument_list|)
expr_stmt|;
block|}
DECL|method|testCrazyReaderEquals
specifier|public
name|void
name|testCrazyReaderEquals
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numdocs
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|createRandomIndex
argument_list|(
name|numdocs
argument_list|,
name|leftWriter
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|createRandomIndex
argument_list|(
name|numdocs
argument_list|,
name|rightWriter
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|leftReader
operator|=
name|wrapReader
argument_list|(
name|leftWriter
operator|.
name|getReader
argument_list|()
argument_list|)
expr_stmt|;
name|rightReader
operator|=
name|wrapReader
argument_list|(
name|rightWriter
operator|.
name|getReader
argument_list|()
argument_list|)
expr_stmt|;
comment|// check that our readers are valid
name|TestUtil
operator|.
name|checkReader
argument_list|(
name|leftReader
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|checkReader
argument_list|(
name|rightReader
argument_list|)
expr_stmt|;
name|assertReaderEquals
argument_list|(
name|info
argument_list|,
name|leftReader
argument_list|,
name|rightReader
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
