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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|codecs
operator|.
name|PostingsFormat
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
name|NumericField
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
name|StringField
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
name|DocTermOrds
operator|.
name|TermOrdsIterator
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
name|FieldCache
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
name|StringHelper
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
comment|// TODO:
end_comment
begin_comment
comment|//   - test w/ del docs
end_comment
begin_comment
comment|//   - test prefix
end_comment
begin_comment
comment|//   - test w/ cutoff
end_comment
begin_comment
comment|//   - crank docs way up so we get some merging sometimes
end_comment
begin_class
DECL|class|TestDocTermOrds
specifier|public
class|class
name|TestDocTermOrds
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
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
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
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
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
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
name|field
init|=
name|newField
argument_list|(
literal|"field"
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
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"a b c"
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"d e f"
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"a f"
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|DocTermOrds
name|dto
init|=
operator|new
name|DocTermOrds
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|TermOrdsIterator
name|iter
init|=
name|dto
operator|.
name|lookup
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|buffer
init|=
operator|new
name|int
index|[
literal|5
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|iter
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|buffer
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|buffer
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|buffer
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|iter
operator|=
name|dto
operator|.
name|lookup
argument_list|(
literal|1
argument_list|,
name|iter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|iter
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|buffer
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|buffer
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|buffer
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|iter
operator|=
name|dto
operator|.
name|lookup
argument_list|(
literal|2
argument_list|,
name|iter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|iter
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|buffer
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|buffer
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|r
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
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|int
name|NUM_TERMS
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|terms
init|=
operator|new
name|HashSet
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|<
name|NUM_TERMS
condition|)
block|{
specifier|final
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
comment|//final String s = _TestUtil.randomSimpleString(random);
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|terms
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|BytesRef
index|[]
name|termsArray
init|=
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|BytesRef
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|termsArray
argument_list|)
expr_stmt|;
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|conf
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
comment|// Sometimes swap in codec that impls ord():
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
comment|// Make sure terms index has ords:
name|Codec
name|codec
init|=
name|_TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
name|PostingsFormat
operator|.
name|forName
argument_list|(
literal|"Lucene40WithOrds"
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
index|[]
name|idToOrds
init|=
operator|new
name|int
index|[
name|NUM_DOCS
index|]
index|[]
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|ordsForDocSet
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|NUM_DOCS
condition|;
name|id
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
name|NumericField
name|idField
init|=
operator|new
name|NumericField
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
operator|.
name|setIntValue
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|termCount
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|0
argument_list|,
literal|20
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
decl_stmt|;
while|while
condition|(
name|ordsForDocSet
operator|.
name|size
argument_list|()
operator|<
name|termCount
condition|)
block|{
name|ordsForDocSet
operator|.
name|add
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|termsArray
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
index|[]
name|ordsForDoc
init|=
operator|new
name|int
index|[
name|termCount
index|]
decl_stmt|;
name|int
name|upto
init|=
literal|0
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
literal|"TEST: doc id="
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|ord
range|:
name|ordsForDocSet
control|)
block|{
name|ordsForDoc
index|[
name|upto
operator|++
index|]
operator|=
name|ord
expr_stmt|;
name|Field
name|field
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
name|termsArray
index|[
name|ord
index|]
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|StringField
operator|.
name|TYPE_UNSTORED
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
literal|"  f="
operator|+
name|termsArray
index|[
name|ord
index|]
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|ordsForDocSet
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|ordsForDoc
argument_list|)
expr_stmt|;
name|idToOrds
index|[
name|id
index|]
operator|=
name|ordsForDoc
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
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
literal|"TEST: reader="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|IndexReader
name|subR
range|:
name|r
operator|.
name|getSequentialSubReaders
argument_list|()
control|)
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
literal|"\nTEST: sub="
operator|+
name|subR
argument_list|)
expr_stmt|;
block|}
name|verify
argument_list|(
name|subR
argument_list|,
name|idToOrds
argument_list|,
name|termsArray
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// Also test top-level reader: its enum does not support
comment|// ord, so this forces the OrdWrapper to run:
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
literal|"TEST: top reader"
argument_list|)
expr_stmt|;
block|}
name|verify
argument_list|(
name|r
argument_list|,
name|idToOrds
argument_list|,
name|termsArray
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|purge
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
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
DECL|method|testRandomWithPrefix
specifier|public
name|void
name|testRandomWithPrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|prefixes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numPrefix
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|7
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
literal|"TEST: use "
operator|+
name|numPrefix
operator|+
literal|" prefixes"
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|prefixes
operator|.
name|size
argument_list|()
operator|<
name|numPrefix
condition|)
block|{
name|prefixes
operator|.
name|add
argument_list|(
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|)
argument_list|)
expr_stmt|;
comment|//prefixes.add(_TestUtil.randomSimpleString(random));
block|}
specifier|final
name|String
index|[]
name|prefixesArray
init|=
name|prefixes
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|prefixes
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|NUM_TERMS
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|terms
init|=
operator|new
name|HashSet
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|<
name|NUM_TERMS
condition|)
block|{
specifier|final
name|String
name|s
init|=
name|prefixesArray
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|prefixesArray
operator|.
name|length
argument_list|)
index|]
operator|+
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
comment|//final String s = prefixesArray[random.nextInt(prefixesArray.length)] + _TestUtil.randomSimpleString(random);
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|terms
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|BytesRef
index|[]
name|termsArray
init|=
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|BytesRef
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|termsArray
argument_list|)
expr_stmt|;
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|conf
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
comment|// Sometimes swap in codec that impls ord():
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|7
condition|)
block|{
name|Codec
name|codec
init|=
name|_TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
name|PostingsFormat
operator|.
name|forName
argument_list|(
literal|"Lucene40WithOrds"
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
index|[]
name|idToOrds
init|=
operator|new
name|int
index|[
name|NUM_DOCS
index|]
index|[]
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|ordsForDocSet
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|NUM_DOCS
condition|;
name|id
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
name|NumericField
name|idField
init|=
operator|new
name|NumericField
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
operator|.
name|setIntValue
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|termCount
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|0
argument_list|,
literal|20
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
decl_stmt|;
while|while
condition|(
name|ordsForDocSet
operator|.
name|size
argument_list|()
operator|<
name|termCount
condition|)
block|{
name|ordsForDocSet
operator|.
name|add
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|termsArray
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
index|[]
name|ordsForDoc
init|=
operator|new
name|int
index|[
name|termCount
index|]
decl_stmt|;
name|int
name|upto
init|=
literal|0
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
literal|"TEST: doc id="
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|ord
range|:
name|ordsForDocSet
control|)
block|{
name|ordsForDoc
index|[
name|upto
operator|++
index|]
operator|=
name|ord
expr_stmt|;
name|Field
name|field
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
name|termsArray
index|[
name|ord
index|]
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|StringField
operator|.
name|TYPE_UNSTORED
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
literal|"  f="
operator|+
name|termsArray
index|[
name|ord
index|]
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|ordsForDocSet
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|ordsForDoc
argument_list|)
expr_stmt|;
name|idToOrds
index|[
name|id
index|]
operator|=
name|ordsForDoc
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
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
literal|"TEST: reader="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|prefix
range|:
name|prefixesArray
control|)
block|{
specifier|final
name|BytesRef
name|prefixRef
init|=
name|prefix
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|BytesRef
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
index|[]
name|idToOrdsPrefix
init|=
operator|new
name|int
index|[
name|NUM_DOCS
index|]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|NUM_DOCS
condition|;
name|id
operator|++
control|)
block|{
specifier|final
name|int
index|[]
name|docOrds
init|=
name|idToOrds
index|[
name|id
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|newOrds
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ord
range|:
name|idToOrds
index|[
name|id
index|]
control|)
block|{
if|if
condition|(
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|termsArray
index|[
name|ord
index|]
argument_list|,
name|prefixRef
argument_list|)
condition|)
block|{
name|newOrds
operator|.
name|add
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|int
index|[]
name|newOrdsArray
init|=
operator|new
name|int
index|[
name|newOrds
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|ord
range|:
name|newOrds
control|)
block|{
name|newOrdsArray
index|[
name|upto
operator|++
index|]
operator|=
name|ord
expr_stmt|;
block|}
name|idToOrdsPrefix
index|[
name|id
index|]
operator|=
name|newOrdsArray
expr_stmt|;
block|}
for|for
control|(
name|IndexReader
name|subR
range|:
name|r
operator|.
name|getSequentialSubReaders
argument_list|()
control|)
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
literal|"\nTEST: sub="
operator|+
name|subR
argument_list|)
expr_stmt|;
block|}
name|verify
argument_list|(
name|subR
argument_list|,
name|idToOrdsPrefix
argument_list|,
name|termsArray
argument_list|,
name|prefixRef
argument_list|)
expr_stmt|;
block|}
comment|// Also test top-level reader: its enum does not support
comment|// ord, so this forces the OrdWrapper to run:
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
literal|"TEST: top reader"
argument_list|)
expr_stmt|;
block|}
name|verify
argument_list|(
name|r
argument_list|,
name|idToOrdsPrefix
argument_list|,
name|termsArray
argument_list|,
name|prefixRef
argument_list|)
expr_stmt|;
block|}
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|purge
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
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
DECL|method|verify
specifier|private
name|void
name|verify
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|int
index|[]
index|[]
name|idToOrds
parameter_list|,
name|BytesRef
index|[]
name|termsArray
parameter_list|,
name|BytesRef
name|prefixRef
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|DocTermOrds
name|dto
init|=
operator|new
name|DocTermOrds
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|,
name|prefixRef
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|docIDToID
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getInts
argument_list|(
name|r
argument_list|,
literal|"id"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/*       for(int docID=0;docID<subR.maxDoc();docID++) {       System.out.println("  docID=" + docID + " id=" + docIDToID[docID]);       }     */
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
literal|"TEST: verify prefix="
operator|+
operator|(
name|prefixRef
operator|==
literal|null
condition|?
literal|"null"
else|:
name|prefixRef
operator|.
name|utf8ToString
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: all TERMS:"
argument_list|)
expr_stmt|;
name|TermsEnum
name|allTE
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|int
name|ord
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|allTE
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  ord="
operator|+
operator|(
name|ord
operator|++
operator|)
operator|+
literal|" term="
operator|+
name|allTE
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//final TermsEnum te = subR.fields().terms("field").iterator();
specifier|final
name|TermsEnum
name|te
init|=
name|dto
operator|.
name|getOrdTermsEnum
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|te
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|prefixRef
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|r
argument_list|,
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|TermsEnum
operator|.
name|SeekStatus
name|result
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|prefixRef
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
block|{
name|assertFalse
argument_list|(
literal|"term="
operator|+
name|termsEnum
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" matches prefix="
operator|+
name|prefixRef
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|,
name|prefixRef
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// ok
block|}
block|}
else|else
block|{
comment|// ok
block|}
block|}
return|return;
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
literal|"TEST: TERMS:"
argument_list|)
expr_stmt|;
name|te
operator|.
name|seekExact
argument_list|(
literal|0
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  ord="
operator|+
name|te
operator|.
name|ord
argument_list|()
operator|+
literal|" term="
operator|+
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|te
operator|.
name|next
argument_list|()
operator|==
literal|null
condition|)
block|{
break|break;
block|}
block|}
block|}
name|TermOrdsIterator
name|iter
init|=
literal|null
decl_stmt|;
specifier|final
name|int
index|[]
name|buffer
init|=
operator|new
name|int
index|[
literal|5
index|]
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|r
operator|.
name|maxDoc
argument_list|()
condition|;
name|docID
operator|++
control|)
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
literal|"TEST: docID="
operator|+
name|docID
operator|+
literal|" of "
operator|+
name|r
operator|.
name|maxDoc
argument_list|()
operator|+
literal|" (id="
operator|+
name|docIDToID
index|[
name|docID
index|]
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|iter
operator|=
name|dto
operator|.
name|lookup
argument_list|(
name|docID
argument_list|,
name|iter
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
name|answers
init|=
name|idToOrds
index|[
name|docIDToID
index|[
name|docID
index|]
index|]
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|chunk
init|=
name|iter
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|chunk
condition|;
name|idx
operator|++
control|)
block|{
name|te
operator|.
name|seekExact
argument_list|(
operator|(
name|long
operator|)
name|buffer
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|expected
init|=
name|termsArray
index|[
name|answers
index|[
name|upto
operator|++
index|]
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
literal|"  exp="
operator|+
name|expected
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" actual="
operator|+
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"expected="
operator|+
name|expected
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" actual="
operator|+
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" ord="
operator|+
name|buffer
index|[
name|idx
index|]
argument_list|,
name|expected
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|chunk
operator|<
name|buffer
operator|.
name|length
condition|)
block|{
name|assertEquals
argument_list|(
name|answers
operator|.
name|length
argument_list|,
name|upto
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
