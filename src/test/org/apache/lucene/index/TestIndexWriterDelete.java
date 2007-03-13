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
name|Arrays
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
name|MockRAMDirectory
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
DECL|class|TestIndexWriterDelete
specifier|public
class|class
name|TestIndexWriterDelete
extends|extends
name|TestCase
block|{
comment|// test the simple case
DECL|method|testSimpleCase
specifier|public
name|void
name|testSimpleCase
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|keywords
init|=
block|{
literal|"1"
block|,
literal|"2"
block|}
decl_stmt|;
name|String
index|[]
name|unindexed
init|=
block|{
literal|"Netherlands"
block|,
literal|"Italy"
block|}
decl_stmt|;
name|String
index|[]
name|unstored
init|=
block|{
literal|"Amsterdam has lots of bridges"
block|,
literal|"Venice has lots of canals"
block|}
decl_stmt|;
name|String
index|[]
name|text
init|=
block|{
literal|"Amsterdam"
block|,
literal|"Venice"
block|}
decl_stmt|;
for|for
control|(
name|int
name|pass
init|=
literal|0
init|;
name|pass
operator|<
literal|2
condition|;
name|pass
operator|++
control|)
block|{
name|boolean
name|autoCommit
init|=
operator|(
literal|0
operator|==
name|pass
operator|)
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|modifier
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|autoCommit
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|modifier
operator|.
name|setUseCompoundFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
literal|1
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
name|keywords
operator|.
name|length
condition|;
name|i
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|keywords
index|[
name|i
index|]
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
literal|"country"
argument_list|,
name|unindexed
index|[
name|i
index|]
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
name|NO
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
literal|"contents"
argument_list|,
name|unstored
index|[
name|i
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
literal|"city"
argument_list|,
name|text
index|[
name|i
index|]
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
name|modifier
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|modifier
operator|.
name|optimize
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"city"
argument_list|,
literal|"Amsterdam"
argument_list|)
decl_stmt|;
name|int
name|hitCount
init|=
name|getHitCount
argument_list|(
name|dir
argument_list|,
name|term
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hitCount
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|autoCommit
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setUseCompoundFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|modifier
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|hitCount
operator|=
name|getHitCount
argument_list|(
name|dir
argument_list|,
name|term
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hitCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// test when delete terms only apply to disk segments
DECL|method|testNonRAMDelete
specifier|public
name|void
name|testNonRAMDelete
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|pass
init|=
literal|0
init|;
name|pass
operator|<
literal|2
condition|;
name|pass
operator|++
control|)
block|{
name|boolean
name|autoCommit
init|=
operator|(
literal|0
operator|==
name|pass
operator|)
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|modifier
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|autoCommit
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|modifier
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|int
name|id
init|=
literal|0
decl_stmt|;
name|int
name|value
init|=
literal|100
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
literal|7
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|modifier
argument_list|,
operator|++
name|id
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|modifier
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|modifier
operator|.
name|getRamSegmentCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|0
operator|<
name|modifier
operator|.
name|getSegmentCount
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|autoCommit
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|modifier
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"value"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"value"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// test when delete terms only apply to ram segments
DECL|method|testRAMDeletes
specifier|public
name|void
name|testRAMDeletes
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|pass
init|=
literal|0
init|;
name|pass
operator|<
literal|2
condition|;
name|pass
operator|++
control|)
block|{
name|boolean
name|autoCommit
init|=
operator|(
literal|0
operator|==
name|pass
operator|)
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|modifier
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|autoCommit
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|modifier
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|int
name|id
init|=
literal|0
decl_stmt|;
name|int
name|value
init|=
literal|100
decl_stmt|;
name|addDoc
argument_list|(
name|modifier
argument_list|,
operator|++
name|id
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"value"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|modifier
argument_list|,
operator|++
name|id
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"value"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|modifier
operator|.
name|getNumBufferedDeleteTerms
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|modifier
operator|.
name|getBufferedDeleteTermsSize
argument_list|()
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|modifier
argument_list|,
operator|++
name|id
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|modifier
operator|.
name|getSegmentCount
argument_list|()
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|hitCount
init|=
name|getHitCount
argument_list|(
name|dir
argument_list|,
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hitCount
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// test when delete terms apply to both disk and ram segments
DECL|method|testBothDeletes
specifier|public
name|void
name|testBothDeletes
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|pass
init|=
literal|0
init|;
name|pass
operator|<
literal|2
condition|;
name|pass
operator|++
control|)
block|{
name|boolean
name|autoCommit
init|=
operator|(
literal|0
operator|==
name|pass
operator|)
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|modifier
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|autoCommit
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|modifier
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|int
name|id
init|=
literal|0
decl_stmt|;
name|int
name|value
init|=
literal|100
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|modifier
argument_list|,
operator|++
name|id
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|value
operator|=
literal|200
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|modifier
argument_list|,
operator|++
name|id
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|modifier
operator|.
name|flush
argument_list|()
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|modifier
argument_list|,
operator|++
name|id
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|modifier
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"value"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// test that batched delete terms are flushed together
DECL|method|testBatchDeletes
specifier|public
name|void
name|testBatchDeletes
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|pass
init|=
literal|0
init|;
name|pass
operator|<
literal|2
condition|;
name|pass
operator|++
control|)
block|{
name|boolean
name|autoCommit
init|=
operator|(
literal|0
operator|==
name|pass
operator|)
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|modifier
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|autoCommit
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|modifier
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|int
name|id
init|=
literal|0
decl_stmt|;
name|int
name|value
init|=
literal|100
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
literal|7
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|modifier
argument_list|,
operator|++
name|id
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|modifier
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|autoCommit
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|id
operator|=
literal|0
expr_stmt|;
name|modifier
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
operator|++
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
operator|++
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|Term
index|[]
name|terms
init|=
operator|new
name|Term
index|[
literal|3
index|]
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|terms
index|[
name|i
index|]
operator|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
operator|++
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|autoCommit
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|modifier
operator|.
name|deleteDocuments
argument_list|(
name|terms
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|autoCommit
condition|)
block|{
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|modifier
parameter_list|,
name|int
name|id
parameter_list|,
name|int
name|value
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
literal|"content"
argument_list|,
literal|"aaa"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
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
literal|"value"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|getHitCount
specifier|private
name|int
name|getHitCount
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|int
name|hitCount
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
argument_list|)
operator|.
name|length
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|hitCount
return|;
block|}
DECL|method|testDeletesOnDiskFull
specifier|public
name|void
name|testDeletesOnDiskFull
parameter_list|()
throws|throws
name|IOException
block|{
name|testOperationsOnDiskFull
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testUpdatesOnDiskFull
specifier|public
name|void
name|testUpdatesOnDiskFull
parameter_list|()
throws|throws
name|IOException
block|{
name|testOperationsOnDiskFull
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make sure if modifier tries to commit but hits disk full that modifier    * remains consistent and usable. Similar to TestIndexReader.testDiskFull().    */
DECL|method|testOperationsOnDiskFull
specifier|private
name|void
name|testOperationsOnDiskFull
parameter_list|(
name|boolean
name|updates
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|debug
init|=
literal|false
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
name|int
name|START_COUNT
init|=
literal|157
decl_stmt|;
name|int
name|END_COUNT
init|=
literal|144
decl_stmt|;
for|for
control|(
name|int
name|pass
init|=
literal|0
init|;
name|pass
operator|<
literal|2
condition|;
name|pass
operator|++
control|)
block|{
name|boolean
name|autoCommit
init|=
operator|(
literal|0
operator|==
name|pass
operator|)
decl_stmt|;
comment|// First build up a starting index:
name|RAMDirectory
name|startDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|startDir
argument_list|,
name|autoCommit
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
literal|157
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"aaa "
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|diskUsage
init|=
name|startDir
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
name|long
name|diskFree
init|=
name|diskUsage
operator|+
literal|10
decl_stmt|;
name|IOException
name|err
init|=
literal|null
decl_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
comment|// Iterate w/ ever increasing free disk space:
while|while
condition|(
operator|!
name|done
condition|)
block|{
name|MockRAMDirectory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|(
name|startDir
argument_list|)
decl_stmt|;
name|IndexWriter
name|modifier
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|autoCommit
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|modifier
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// use flush or close
name|modifier
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// use flush or close
comment|// For each disk size, first try to commit against
comment|// dir that will hit random IOExceptions& disk
comment|// full; after, give it infinite disk space& turn
comment|// off random IOExceptions& retry w/ same reader:
name|boolean
name|success
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|2
condition|;
name|x
operator|++
control|)
block|{
name|double
name|rate
init|=
literal|0.1
decl_stmt|;
name|double
name|diskRatio
init|=
operator|(
operator|(
name|double
operator|)
name|diskFree
operator|)
operator|/
name|diskUsage
decl_stmt|;
name|long
name|thisDiskFree
decl_stmt|;
name|String
name|testName
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|x
condition|)
block|{
name|thisDiskFree
operator|=
name|diskFree
expr_stmt|;
if|if
condition|(
name|diskRatio
operator|>=
literal|2.0
condition|)
block|{
name|rate
operator|/=
literal|2
expr_stmt|;
block|}
if|if
condition|(
name|diskRatio
operator|>=
literal|4.0
condition|)
block|{
name|rate
operator|/=
literal|2
expr_stmt|;
block|}
if|if
condition|(
name|diskRatio
operator|>=
literal|6.0
condition|)
block|{
name|rate
operator|=
literal|0.0
expr_stmt|;
block|}
if|if
condition|(
name|debug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\ncycle: "
operator|+
name|diskFree
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
block|}
name|testName
operator|=
literal|"disk full during reader.close() @ "
operator|+
name|thisDiskFree
operator|+
literal|" bytes"
expr_stmt|;
block|}
else|else
block|{
name|thisDiskFree
operator|=
literal|0
expr_stmt|;
name|rate
operator|=
literal|0.0
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\ncycle: same writer: unlimited disk space"
argument_list|)
expr_stmt|;
block|}
name|testName
operator|=
literal|"reader re-use after disk full"
expr_stmt|;
block|}
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
name|thisDiskFree
argument_list|)
expr_stmt|;
name|dir
operator|.
name|setRandomIOExceptionRate
argument_list|(
name|rate
argument_list|,
name|diskFree
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
literal|0
operator|==
name|x
condition|)
block|{
name|int
name|docId
init|=
literal|12
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
literal|13
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|updates
condition|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"bbb "
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|docId
argument_list|)
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// deletes
name|modifier
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|docId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// modifier.setNorm(docId, "contents", (float)2.0);
block|}
name|docId
operator|+=
literal|12
expr_stmt|;
block|}
block|}
name|modifier
operator|.
name|close
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|x
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|debug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  hit IOException: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|err
operator|=
name|e
expr_stmt|;
if|if
condition|(
literal|1
operator|==
name|x
condition|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|testName
operator|+
literal|" hit IOException after disk space was freed up"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Whether we succeeded or failed, check that all
comment|// un-referenced files were in fact deleted (ie,
comment|// we did not create garbage). Just create a
comment|// new IndexFileDeleter, have it delete
comment|// unreferenced files, then verify that in fact
comment|// no files were deleted:
name|String
index|[]
name|startFiles
init|=
name|dir
operator|.
name|list
argument_list|()
decl_stmt|;
name|SegmentInfos
name|infos
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|infos
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|IndexFileDeleter
name|d
init|=
operator|new
name|IndexFileDeleter
argument_list|(
name|dir
argument_list|,
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|,
name|infos
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
index|[]
name|endFiles
init|=
name|dir
operator|.
name|list
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|startFiles
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|endFiles
argument_list|)
expr_stmt|;
comment|// for(int i=0;i<startFiles.length;i++) {
comment|// System.out.println(" startFiles: " + i + ": " + startFiles[i]);
comment|// }
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|startFiles
argument_list|,
name|endFiles
argument_list|)
condition|)
block|{
name|String
name|successStr
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|successStr
operator|=
literal|"success"
expr_stmt|;
block|}
else|else
block|{
name|successStr
operator|=
literal|"IOException"
expr_stmt|;
name|err
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"reader.close() failed to delete unreferenced files after "
operator|+
name|successStr
operator|+
literal|" ("
operator|+
name|diskFree
operator|+
literal|" bytes): before delete:\n    "
operator|+
name|arrayToString
argument_list|(
name|startFiles
argument_list|)
operator|+
literal|"\n  after delete:\n    "
operator|+
name|arrayToString
argument_list|(
name|endFiles
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Finally, verify index is not corrupt, and, if
comment|// we succeeded, we see all docs changed, and if
comment|// we failed, we see either all docs or no docs
comment|// changed (transactional semantics):
name|IndexReader
name|newReader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|newReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
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
name|fail
argument_list|(
name|testName
operator|+
literal|":exception when creating IndexReader after disk full during close: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|newReader
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
literal|null
decl_stmt|;
try|try
block|{
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
name|fail
argument_list|(
name|testName
operator|+
literal|": exception when searching: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|int
name|result2
init|=
name|hits
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
if|if
condition|(
name|result2
operator|!=
name|END_COUNT
condition|)
block|{
name|fail
argument_list|(
name|testName
operator|+
literal|": method did not throw exception but hits.length for search on term 'aaa' is "
operator|+
name|result2
operator|+
literal|" instead of expected "
operator|+
name|END_COUNT
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// On hitting exception we still may have added
comment|// all docs:
if|if
condition|(
name|result2
operator|!=
name|START_COUNT
operator|&&
name|result2
operator|!=
name|END_COUNT
condition|)
block|{
name|err
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|testName
operator|+
literal|": method did throw exception but hits.length for search on term 'aaa' is "
operator|+
name|result2
operator|+
literal|" instead of expected "
operator|+
name|START_COUNT
argument_list|)
expr_stmt|;
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|newReader
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|result2
operator|==
name|END_COUNT
condition|)
block|{
break|break;
block|}
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Try again with 10 more bytes of free space:
name|diskFree
operator|+=
literal|10
expr_stmt|;
block|}
block|}
block|}
DECL|method|arrayToString
specifier|private
name|String
name|arrayToString
parameter_list|(
name|String
index|[]
name|l
parameter_list|)
block|{
name|String
name|s
init|=
literal|""
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
name|l
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|s
operator|+=
literal|"\n    "
expr_stmt|;
block|}
name|s
operator|+=
name|l
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
block|}
end_class
end_unit
