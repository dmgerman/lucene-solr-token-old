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
name|HashSet
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
name|index
operator|.
name|TermDocs
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
begin_class
DECL|class|DuplicateFilterTest
specifier|public
class|class
name|DuplicateFilterTest
extends|extends
name|TestCase
block|{
DECL|field|KEY_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|KEY_FIELD
init|=
literal|"url"
decl_stmt|;
DECL|field|directory
specifier|private
name|RAMDirectory
name|directory
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|tq
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
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
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//Add series of docs with filterable fields : url, text and dates  flags
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://lucene.apache.org"
argument_list|,
literal|"lucene 1.4.3 available"
argument_list|,
literal|"20040101"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://lucene.apache.org"
argument_list|,
literal|"New release pending"
argument_list|,
literal|"20040102"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://lucene.apache.org"
argument_list|,
literal|"Lucene 1.9 out now"
argument_list|,
literal|"20050101"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://www.bar.com"
argument_list|,
literal|"Local man bites dog"
argument_list|,
literal|"20040101"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://www.bar.com"
argument_list|,
literal|"Dog bites local man"
argument_list|,
literal|"20040102"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://www.bar.com"
argument_list|,
literal|"Dog uses Lucene"
argument_list|,
literal|"20050101"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://lucene.apache.org"
argument_list|,
literal|"Lucene 2.0 out"
argument_list|,
literal|"20050101"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://lucene.apache.org"
argument_list|,
literal|"Oops. Lucene 2.1 out"
argument_list|,
literal|"20050102"
argument_list|)
expr_stmt|;
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
name|directory
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
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
name|url
parameter_list|,
name|String
name|text
parameter_list|,
name|String
name|date
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
operator|new
name|Field
argument_list|(
name|KEY_FIELD
argument_list|,
name|url
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"text"
argument_list|,
name|text
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"date"
argument_list|,
name|date
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultFilter
specifier|public
name|void
name|testDefaultFilter
parameter_list|()
throws|throws
name|Throwable
block|{
name|DuplicateFilter
name|df
init|=
operator|new
name|DuplicateFilter
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|HashSet
name|results
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|Hits
name|h
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
name|df
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
name|h
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
name|h
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|d
operator|.
name|get
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"No duplicate urls should be returned"
argument_list|,
name|results
operator|.
name|contains
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNoFilter
specifier|public
name|void
name|testNoFilter
parameter_list|()
throws|throws
name|Throwable
block|{
name|HashSet
name|results
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|Hits
name|h
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Default searching should have found some matches"
argument_list|,
name|h
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|boolean
name|dupsFound
init|=
literal|false
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
name|h
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
name|h
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|d
operator|.
name|get
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dupsFound
condition|)
name|dupsFound
operator|=
name|results
operator|.
name|contains
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Default searching should have found duplicate urls"
argument_list|,
name|dupsFound
argument_list|)
expr_stmt|;
block|}
DECL|method|testFastFilter
specifier|public
name|void
name|testFastFilter
parameter_list|()
throws|throws
name|Throwable
block|{
name|DuplicateFilter
name|df
init|=
operator|new
name|DuplicateFilter
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|df
operator|.
name|setProcessingMode
argument_list|(
name|DuplicateFilter
operator|.
name|PM_FAST_INVALIDATION
argument_list|)
expr_stmt|;
name|HashSet
name|results
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|Hits
name|h
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
name|df
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Filtered searching should have found some matches"
argument_list|,
name|h
operator|.
name|length
argument_list|()
operator|>
literal|0
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
name|h
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
name|h
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|d
operator|.
name|get
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"No duplicate urls should be returned"
argument_list|,
name|results
operator|.
name|contains
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Two urls found"
argument_list|,
literal|2
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeepsLastFilter
specifier|public
name|void
name|testKeepsLastFilter
parameter_list|()
throws|throws
name|Throwable
block|{
name|DuplicateFilter
name|df
init|=
operator|new
name|DuplicateFilter
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|df
operator|.
name|setKeepMode
argument_list|(
name|DuplicateFilter
operator|.
name|KM_USE_LAST_OCCURRENCE
argument_list|)
expr_stmt|;
name|Hits
name|h
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
name|df
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Filtered searching should have found some matches"
argument_list|,
name|h
operator|.
name|length
argument_list|()
operator|>
literal|0
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
name|h
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
name|h
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|d
operator|.
name|get
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|TermDocs
name|td
init|=
name|reader
operator|.
name|termDocs
argument_list|(
operator|new
name|Term
argument_list|(
name|KEY_FIELD
argument_list|,
name|url
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|lastDoc
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|td
operator|.
name|next
argument_list|()
condition|)
block|{
name|lastDoc
operator|=
name|td
operator|.
name|doc
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Duplicate urls should return last doc"
argument_list|,
name|lastDoc
argument_list|,
name|h
operator|.
name|id
argument_list|(
operator|(
name|i
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testKeepsFirstFilter
specifier|public
name|void
name|testKeepsFirstFilter
parameter_list|()
throws|throws
name|Throwable
block|{
name|DuplicateFilter
name|df
init|=
operator|new
name|DuplicateFilter
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|df
operator|.
name|setKeepMode
argument_list|(
name|DuplicateFilter
operator|.
name|KM_USE_FIRST_OCCURRENCE
argument_list|)
expr_stmt|;
name|Hits
name|h
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
name|df
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Filtered searching should have found some matches"
argument_list|,
name|h
operator|.
name|length
argument_list|()
operator|>
literal|0
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
name|h
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
name|h
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|d
operator|.
name|get
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|TermDocs
name|td
init|=
name|reader
operator|.
name|termDocs
argument_list|(
operator|new
name|Term
argument_list|(
name|KEY_FIELD
argument_list|,
name|url
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|lastDoc
init|=
literal|0
decl_stmt|;
name|td
operator|.
name|next
argument_list|()
expr_stmt|;
name|lastDoc
operator|=
name|td
operator|.
name|doc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Duplicate urls should return first doc"
argument_list|,
name|lastDoc
argument_list|,
name|h
operator|.
name|id
argument_list|(
operator|(
name|i
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
