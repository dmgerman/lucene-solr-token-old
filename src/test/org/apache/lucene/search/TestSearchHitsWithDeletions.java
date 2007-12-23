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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ConcurrentModificationException
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|WhitespaceAnalyzer
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
name|IndexWriter
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
name|search
operator|.
name|Hits
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
name|IndexSearcher
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
name|Query
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
begin_comment
comment|/**  * Test Hits searches with interleaved deletions.  *   * See {@link http://issues.apache.org/jira/browse/LUCENE-1096}.  */
end_comment
begin_class
DECL|class|TestSearchHitsWithDeletions
specifier|public
class|class
name|TestSearchHitsWithDeletions
extends|extends
name|TestCase
block|{
DECL|field|VERBOSE
specifier|private
specifier|static
name|boolean
name|VERBOSE
init|=
literal|false
decl_stmt|;
DECL|field|TEXT_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|TEXT_FIELD
init|=
literal|"text"
decl_stmt|;
DECL|field|N
specifier|private
specifier|static
specifier|final
name|int
name|N
init|=
literal|16100
decl_stmt|;
DECL|field|directory
specifier|private
specifier|static
name|Directory
name|directory
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create an index writer.
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
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
name|N
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|createDocument
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Deletions during search should not alter previously retrieved hits.    */
DECL|method|testSearchHitsDeleteAll
specifier|public
name|void
name|testSearchHitsDeleteAll
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSearchHitsDeleteEvery
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletions during search should not alter previously retrieved hits.    */
DECL|method|testSearchHitsDeleteEvery2ndHit
specifier|public
name|void
name|testSearchHitsDeleteEvery2ndHit
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSearchHitsDeleteEvery
argument_list|(
literal|2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletions during search should not alter previously retrieved hits.    */
DECL|method|testSearchHitsDeleteEvery4thHit
specifier|public
name|void
name|testSearchHitsDeleteEvery4thHit
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSearchHitsDeleteEvery
argument_list|(
literal|4
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletions during search should not alter previously retrieved hits.    */
DECL|method|testSearchHitsDeleteEvery8thHit
specifier|public
name|void
name|testSearchHitsDeleteEvery8thHit
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSearchHitsDeleteEvery
argument_list|(
literal|8
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletions during search should not alter previously retrieved hits.    */
DECL|method|testSearchHitsDeleteEvery90thHit
specifier|public
name|void
name|testSearchHitsDeleteEvery90thHit
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSearchHitsDeleteEvery
argument_list|(
literal|90
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletions during search should not alter previously retrieved hits,    * and deletions that affect total number of hits should throw the     * correct exception when trying to fetch "too many".    */
DECL|method|testSearchHitsDeleteEvery8thHitAndInAdvance
specifier|public
name|void
name|testSearchHitsDeleteEvery8thHitAndInAdvance
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSearchHitsDeleteEvery
argument_list|(
literal|8
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that ok also with no deletions at all.    */
DECL|method|testSearchHitsNoDeletes
specifier|public
name|void
name|testSearchHitsNoDeletes
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSearchHitsDeleteEvery
argument_list|(
name|N
operator|+
literal|100
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletions that affect total number of hits should throw the     * correct exception when trying to fetch "too many".    */
DECL|method|testSearchHitsDeleteInAdvance
specifier|public
name|void
name|testSearchHitsDeleteInAdvance
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSearchHitsDeleteEvery
argument_list|(
name|N
operator|+
literal|100
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Intermittent deletions during search, should not alter previously retrieved hits.    * (Using a debugger to verify that the check in Hits is performed only      */
DECL|method|testSearchHitsDeleteIntermittent
specifier|public
name|void
name|testSearchHitsDeleteIntermittent
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSearchHitsDeleteEvery
argument_list|(
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestSearchHitsDeleteEvery
specifier|private
name|void
name|doTestSearchHitsDeleteEvery
parameter_list|(
name|int
name|k
parameter_list|,
name|boolean
name|deleteInFront
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|intermittent
init|=
name|k
operator|<
literal|0
decl_stmt|;
name|log
argument_list|(
literal|"Test search hits with "
operator|+
operator|(
name|intermittent
condition|?
literal|"intermittent deletions."
else|:
literal|"deletions of every "
operator|+
name|k
operator|+
literal|" hit."
operator|)
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|IndexReader
name|reader
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|TEXT_FIELD
argument_list|,
literal|"text"
argument_list|)
argument_list|)
decl_stmt|;
comment|// matching all docs
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"Got "
operator|+
name|hits
operator|.
name|length
argument_list|()
operator|+
literal|" results"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"must match all "
operator|+
name|N
operator|+
literal|" docs, not only "
operator|+
name|hits
operator|.
name|length
argument_list|()
operator|+
literal|" docs!"
argument_list|,
name|N
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleteInFront
condition|)
block|{
name|log
argument_list|(
literal|"deleting hits that was not yet retrieved!"
argument_list|)
expr_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|-
literal|3
argument_list|)
expr_stmt|;
block|}
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
name|hits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|hits
operator|.
name|id
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Hit "
operator|+
name|i
operator|+
literal|" has doc id "
operator|+
name|hits
operator|.
name|id
argument_list|(
name|i
argument_list|)
operator|+
literal|" instead of "
operator|+
name|i
argument_list|,
name|i
argument_list|,
name|hits
operator|.
name|id
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|intermittent
operator|&&
operator|(
name|i
operator|==
literal|50
operator|||
name|i
operator|==
literal|250
operator|||
name|i
operator|==
literal|950
operator|)
operator|)
operator|||
comment|//100-yes, 200-no, 400-yes, 800-no, 1600-yes
operator|(
operator|!
name|intermittent
operator|&&
operator|(
name|k
operator|<
literal|2
operator|||
operator|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|%
name|k
operator|==
literal|0
operator|)
operator|)
operator|)
condition|)
block|{
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"Deleting hit "
operator|+
name|i
operator|+
literal|" - doc "
operator|+
name|doc
operator|+
literal|" with id "
operator|+
name|id
argument_list|)
expr_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|intermittent
condition|)
block|{
comment|// check internal behavior of Hits (go 50 ahead of getMoreDocs points because the deletions cause to use more of the available hits)
if|if
condition|(
name|i
operator|==
literal|150
operator|||
name|i
operator|==
literal|450
operator|||
name|i
operator|==
literal|1650
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Hit "
operator|+
name|i
operator|+
literal|": hits should have checked for deletions in last call to getMoreDocs()"
argument_list|,
name|hits
operator|.
name|debugCheckedForDeletions
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
literal|50
operator|||
name|i
operator|==
literal|250
operator|||
name|i
operator|==
literal|850
condition|)
block|{
name|assertFalse
argument_list|(
literal|"Hit "
operator|+
name|i
operator|+
literal|": hits should have NOT checked for deletions in last call to getMoreDocs()"
argument_list|,
name|hits
operator|.
name|debugCheckedForDeletions
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ConcurrentModificationException
name|e
parameter_list|)
block|{
comment|// this is the only valid exception, and only when deletng in front.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" not expected unless deleting hits that were not yet seen!"
argument_list|,
name|deleteInFront
argument_list|)
expr_stmt|;
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createDocument
specifier|private
specifier|static
name|Document
name|createDocument
parameter_list|(
name|int
name|id
parameter_list|)
block|{
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
operator|new
name|Field
argument_list|(
name|TEXT_FIELD
argument_list|,
literal|"text of document"
operator|+
name|id
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|log
specifier|private
specifier|static
name|void
name|log
parameter_list|(
name|String
name|s
parameter_list|)
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
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
