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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentLinkedQueue
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|FieldType
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
name|FieldInfo
operator|.
name|IndexOptions
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
comment|/**  * Simple test that adds numeric terms, where each term has the   * totalTermFreq of its integer value, and checks that the totalTermFreq is correct.   */
end_comment
begin_comment
comment|// TODO: somehow factor this with BagOfPostings? its almost the same
end_comment
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Direct"
block|,
literal|"Memory"
block|}
argument_list|)
comment|// at night this makes like 200k/300k docs and will make Direct's heart beat!
DECL|class|TestBagOfPositions
specifier|public
class|class
name|TestBagOfPositions
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|postingsList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|numTerms
init|=
name|atLeast
argument_list|(
literal|300
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxTermsPerDoc
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|boolean
name|isSimpleText
init|=
literal|"SimpleText"
operator|.
name|equals
argument_list|(
name|_TestUtil
operator|.
name|getPostingsFormat
argument_list|(
literal|"field"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isSimpleText
operator|&&
operator|(
name|TEST_NIGHTLY
operator|||
name|RANDOM_MULTIPLIER
operator|>
literal|1
operator|)
condition|)
block|{
comment|// Otherwise test can take way too long (> 2 hours)
name|numTerms
operator|/=
literal|2
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
literal|"maxTermsPerDoc="
operator|+
name|maxTermsPerDoc
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"numTerms="
operator|+
name|numTerms
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
name|numTerms
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
name|i
condition|;
name|j
operator|++
control|)
block|{
name|postingsList
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|postingsList
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|String
argument_list|>
name|postings
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|String
argument_list|>
argument_list|(
name|postingsList
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"bagofpositions"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
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
name|threadCount
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
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
literal|"config: "
operator|+
name|iw
operator|.
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"threadCount="
operator|+
name|threadCount
argument_list|)
expr_stmt|;
block|}
name|Field
name|prototype
init|=
name|newTextField
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
name|FieldType
name|fieldType
init|=
operator|new
name|FieldType
argument_list|(
name|prototype
operator|.
name|fieldType
argument_list|()
argument_list|)
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
name|fieldType
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|int
name|options
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|==
literal|0
condition|)
block|{
name|fieldType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
expr_stmt|;
comment|// we dont actually need positions
name|fieldType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// but enforce term vectors when we do this so we check SOMETHING
block|}
elseif|else
if|if
condition|(
name|options
operator|==
literal|1
operator|&&
operator|!
name|doesntSupportOffsets
operator|.
name|contains
argument_list|(
name|_TestUtil
operator|.
name|getPostingsFormat
argument_list|(
literal|"field"
argument_list|)
argument_list|)
condition|)
block|{
name|fieldType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
expr_stmt|;
block|}
comment|// else just positions
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|threadCount
index|]
decl_stmt|;
specifier|final
name|CountDownLatch
name|startingGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|threadID
init|=
literal|0
init|;
name|threadID
operator|<
name|threadCount
condition|;
name|threadID
operator|++
control|)
block|{
specifier|final
name|Random
name|threadRandom
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|fieldType
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|threads
index|[
name|threadID
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|startingGun
operator|.
name|await
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|postings
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|numTerms
init|=
name|threadRandom
operator|.
name|nextInt
argument_list|(
name|maxTermsPerDoc
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
name|numTerms
condition|;
name|i
operator|++
control|)
block|{
name|String
name|token
init|=
name|postings
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|text
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|text
operator|.
name|append
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
name|field
operator|.
name|setStringValue
argument_list|(
name|text
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|threadID
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|startingGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ir
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|AtomicReader
name|air
init|=
name|ir
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|air
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
comment|// numTerms-1 because there cannot be a term 0 with 0 postings:
name|assertEquals
argument_list|(
name|numTerms
operator|-
literal|1
argument_list|,
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|BytesRef
name|term
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|int
name|value
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
expr_stmt|;
comment|// don't really need to check more than this, as CheckIndex
comment|// will verify that totalTermFreq == total number of positions seen
comment|// from a docsAndPositionsEnum.
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
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
