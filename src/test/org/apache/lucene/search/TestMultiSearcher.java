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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
comment|/**  * FIXME: Describe class<code>TestMultiSearcher</code> here.  *  * @version $Id$  */
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
comment|/** 	 * Return a new instance of the concrete MultiSearcher class 	 * used in this test 	 */
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
name|Field
operator|.
name|Text
argument_list|(
literal|"fulltext"
argument_list|,
literal|"Once upon a time....."
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"id"
argument_list|,
literal|"doc1"
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"handle"
argument_list|,
literal|"1"
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
name|Field
operator|.
name|Text
argument_list|(
literal|"fulltext"
argument_list|,
literal|"in a galaxy far far away....."
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc2
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"id"
argument_list|,
literal|"doc2"
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc2
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"handle"
argument_list|,
literal|"1"
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
name|Field
operator|.
name|Text
argument_list|(
literal|"fulltext"
argument_list|,
literal|"a bizarre bug manifested itself...."
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc3
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"id"
argument_list|,
literal|"doc3"
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc3
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"handle"
argument_list|,
literal|"1"
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
name|close
argument_list|()
expr_stmt|;
name|writerA
operator|.
name|optimize
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
try|try
block|{
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
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"ArrayIndexOutOfBoundsException thrown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|mSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
try|try
block|{
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
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Exception thrown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|mSearcher2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
try|try
block|{
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IOException thrown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|mSearcher3
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
