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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|standard
operator|.
name|StandardAnalyzer
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
name|queryParser
operator|.
name|QueryParser
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
name|Searcher
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
begin_comment
comment|/**  * Tests {@link MultiSearcher} class.  *  * @version $Id$  */
end_comment
begin_class
DECL|class|TestMultiSearcher
specifier|public
class|class
name|TestMultiSearcher
extends|extends
name|TestCase
block|{
DECL|method|TestMultiSearcher
specifier|public
name|TestMultiSearcher
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * ReturnS a new instance of the concrete MultiSearcher class 	 * used in this test. 	 */
DECL|method|getMultiSearcherInstance
specifier|protected
name|MultiSearcher
name|getMultiSearcherInstance
parameter_list|(
name|Searcher
index|[]
name|searchers
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MultiSearcher
argument_list|(
name|searchers
argument_list|)
return|;
block|}
DECL|method|testEmptyIndex
specifier|public
name|void
name|testEmptyIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// creating two directories for indices
name|Directory
name|indexStoreA
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|Directory
name|indexStoreB
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// creating a document to store
name|Document
name|lDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|lDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"fulltext"
argument_list|,
literal|"Once upon a time....."
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
name|lDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"doc1"
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
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"handle"
argument_list|,
literal|"1"
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
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// creating a document to store
name|Document
name|lDoc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|lDoc2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"fulltext"
argument_list|,
literal|"in a galaxy far far away....."
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
name|lDoc2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"doc2"
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
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"handle"
argument_list|,
literal|"1"
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
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// creating a document to store
name|Document
name|lDoc3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|lDoc3
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"fulltext"
argument_list|,
literal|"a bizarre bug manifested itself...."
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
name|lDoc3
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"doc3"
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
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc3
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"handle"
argument_list|,
literal|"1"
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
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// creating an index writer for the first index
name|IndexWriter
name|writerA
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreA
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// creating an index writer for the second index, but writing nothing
name|IndexWriter
name|writerB
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreB
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//--------------------------------------------------------------------
comment|// scenario 1
comment|//--------------------------------------------------------------------
comment|// writing the documents to the first index
name|writerA
operator|.
name|addDocument
argument_list|(
name|lDoc
argument_list|)
expr_stmt|;
name|writerA
operator|.
name|addDocument
argument_list|(
name|lDoc2
argument_list|)
expr_stmt|;
name|writerA
operator|.
name|addDocument
argument_list|(
name|lDoc3
argument_list|)
expr_stmt|;
name|writerA
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writerA
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// closing the second index
name|writerB
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// creating the query
name|Query
name|query
init|=
name|QueryParser
operator|.
name|parse
argument_list|(
literal|"handle:1"
argument_list|,
literal|"fulltext"
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
comment|// building the searchables
name|Searcher
index|[]
name|searchers
init|=
operator|new
name|Searcher
index|[
literal|2
index|]
decl_stmt|;
comment|// VITAL STEP:adding the searcher for the empty index first, before the searcher for the populated index
name|searchers
index|[
literal|0
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreB
argument_list|)
expr_stmt|;
name|searchers
index|[
literal|1
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreA
argument_list|)
expr_stmt|;
comment|// creating the multiSearcher
name|Searcher
name|mSearcher
init|=
name|getMultiSearcherInstance
argument_list|(
name|searchers
argument_list|)
decl_stmt|;
comment|// performing the search
name|Hits
name|hits
init|=
name|mSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// iterating over the hit documents
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
name|Document
name|d
init|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
block|}
name|mSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//--------------------------------------------------------------------
comment|// scenario 2
comment|//--------------------------------------------------------------------
comment|// adding one document to the empty index
name|writerB
operator|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreB
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writerB
operator|.
name|addDocument
argument_list|(
name|lDoc
argument_list|)
expr_stmt|;
name|writerB
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writerB
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// building the searchables
name|Searcher
index|[]
name|searchers2
init|=
operator|new
name|Searcher
index|[
literal|2
index|]
decl_stmt|;
comment|// VITAL STEP:adding the searcher for the empty index first, before the searcher for the populated index
name|searchers2
index|[
literal|0
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreB
argument_list|)
expr_stmt|;
name|searchers2
index|[
literal|1
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreA
argument_list|)
expr_stmt|;
comment|// creating the mulitSearcher
name|Searcher
name|mSearcher2
init|=
name|getMultiSearcherInstance
argument_list|(
name|searchers2
argument_list|)
decl_stmt|;
comment|// performing the same search
name|Hits
name|hits2
init|=
name|mSearcher2
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|hits2
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// iterating over the hit documents
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits2
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// no exception should happen at this point
name|Document
name|d
init|=
name|hits2
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
block|}
name|mSearcher2
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//--------------------------------------------------------------------
comment|// scenario 3
comment|//--------------------------------------------------------------------
comment|// deleting the document just added, this will cause a different exception to take place
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"doc1"
argument_list|)
decl_stmt|;
name|IndexReader
name|readerB
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexStoreB
argument_list|)
decl_stmt|;
name|readerB
operator|.
name|delete
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|readerB
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// optimizing the index with the writer
name|writerB
operator|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreB
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writerB
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writerB
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// building the searchables
name|Searcher
index|[]
name|searchers3
init|=
operator|new
name|Searcher
index|[
literal|2
index|]
decl_stmt|;
name|searchers3
index|[
literal|0
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreB
argument_list|)
expr_stmt|;
name|searchers3
index|[
literal|1
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreA
argument_list|)
expr_stmt|;
comment|// creating the mulitSearcher
name|Searcher
name|mSearcher3
init|=
name|getMultiSearcherInstance
argument_list|(
name|searchers3
argument_list|)
decl_stmt|;
comment|// performing the same search
name|Hits
name|hits3
init|=
name|mSearcher3
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits3
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// iterating over the hit documents
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits3
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
name|hits3
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
block|}
name|mSearcher3
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
