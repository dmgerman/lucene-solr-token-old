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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001, 2002, 2003 The Apache Software Foundation.  * All rights reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
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
name|TermQuery
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
name|java
operator|.
name|util
operator|.
name|Collection
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
begin_class
DECL|class|TestIndexReader
specifier|public
class|class
name|TestIndexReader
extends|extends
name|TestCase
block|{
comment|/**      * Tests the IndexReader.getFieldNames implementation      * @throws Exception on error      */
DECL|method|testGetFieldNames
specifier|public
name|void
name|testGetFieldNames
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|d
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// set up writer
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|addDocumentWithFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// set up reader
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|Collection
name|fieldNames
init|=
name|reader
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"keyword"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"unindexed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"unstored"
argument_list|)
argument_list|)
expr_stmt|;
comment|// add more documents
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// want to get some more segments here
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
operator|*
name|writer
operator|.
name|mergeFactor
condition|;
name|i
operator|++
control|)
block|{
name|addDocumentWithFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
comment|// new fields are in some different segments (we hope)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
operator|*
name|writer
operator|.
name|mergeFactor
condition|;
name|i
operator|++
control|)
block|{
name|addDocumentWithDifferentFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify fields again
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|fieldNames
operator|=
name|reader
operator|.
name|getFieldNames
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"keyword"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"unindexed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"unstored"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"keyword2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"text2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"unindexed2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"unstored2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify that only indexed fields were returned
name|Collection
name|indexedFieldNames
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"keyword"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"unstored"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"keyword2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"text2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"unindexed2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"unstored2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify that only unindexed fields were returned
name|Collection
name|unindexedFieldNames
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
literal|"unindexed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeleteReaderWriterConflict
specifier|public
name|void
name|testDeleteReaderWriterConflict
parameter_list|()
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
name|Searcher
name|searcher
init|=
literal|null
decl_stmt|;
name|Term
name|searchTerm
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//  add 100 documents with term : aaa
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
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
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"aaa"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|//  add 100 documents with term : bbb
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|false
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
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"bbb"
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
try|try
block|{
comment|// delete documents containing term: aaa
name|reader
operator|.
name|delete
argument_list|(
name|searchTerm
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
try|try
block|{
comment|// if reader throws IOException try once more to delete documents with a new reader
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|reader
operator|.
name|delete
argument_list|(
name|searchTerm
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|e1
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|searchTerm
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|e1
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testDeleteReaderReaderConflict
specifier|public
name|void
name|testDeleteReaderReaderConflict
parameter_list|()
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
name|IndexReader
name|reader1
init|=
literal|null
decl_stmt|;
name|IndexReader
name|reader2
init|=
literal|null
decl_stmt|;
name|Searcher
name|searcher
init|=
literal|null
decl_stmt|;
name|Hits
name|hits
init|=
literal|null
decl_stmt|;
name|Term
name|searchTerm1
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
decl_stmt|;
name|Term
name|searchTerm2
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bbb"
argument_list|)
decl_stmt|;
try|try
block|{
comment|//  add 100 documents with term : aaa
comment|//  add 100 documents with term : bbb
comment|//  add 100 documents with term : ccc
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
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
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"aaa"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"bbb"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"ccc"
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|reader1
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|reader2
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|// delete documents containing term: aaa
name|reader2
operator|.
name|delete
argument_list|(
name|searchTerm1
argument_list|)
expr_stmt|;
name|reader2
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// delete documents containing term: bbb
name|reader1
operator|.
name|delete
argument_list|(
name|searchTerm2
argument_list|)
expr_stmt|;
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
try|try
block|{
comment|// if reader throws IOException try once more to delete documents with a new reader
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader1
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|reader1
operator|.
name|delete
argument_list|(
name|searchTerm2
argument_list|)
expr_stmt|;
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|e1
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|searchTerm1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|searchTerm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|e1
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addDocumentWithFields
specifier|private
name|void
name|addDocumentWithFields
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
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
name|Field
operator|.
name|Keyword
argument_list|(
literal|"keyword"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"text"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnIndexed
argument_list|(
literal|"unindexed"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnStored
argument_list|(
literal|"unstored"
argument_list|,
literal|"test1"
argument_list|)
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
DECL|method|addDocumentWithDifferentFields
specifier|private
name|void
name|addDocumentWithDifferentFields
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
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
name|Field
operator|.
name|Keyword
argument_list|(
literal|"keyword2"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"text2"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnIndexed
argument_list|(
literal|"unindexed2"
argument_list|,
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|UnStored
argument_list|(
literal|"unstored2"
argument_list|,
literal|"test1"
argument_list|)
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
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|value
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
name|Field
operator|.
name|UnStored
argument_list|(
literal|"content"
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
