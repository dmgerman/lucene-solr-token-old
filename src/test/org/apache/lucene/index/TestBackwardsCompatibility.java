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
name|util
operator|.
name|Vector
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
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
name|io
operator|.
name|File
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
name|FSDirectory
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
name|io
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|*
import|;
end_import
begin_comment
comment|/*   Verify we can read the pre-2.1 file format, do searches   against it, and add documents to it. */
end_comment
begin_class
DECL|class|TestBackwardsCompatibility
specifier|public
class|class
name|TestBackwardsCompatibility
extends|extends
name|TestCase
block|{
comment|// Uncomment these cases& run in a pre-lockless checkout
comment|// to create indices:
comment|/*   public void testCreatePreLocklessCFS() throws IOException {     createIndex("src/test/org/apache/lucene/index/index.prelockless.cfs", true);   }    public void testCreatePreLocklessNoCFS() throws IOException {     createIndex("src/test/org/apache/lucene/index/index.prelockless.nocfs", false);   }   */
comment|/* Unzips dirName + ".zip" --> dirName, removing dirName      first */
DECL|method|unzip
specifier|public
name|void
name|unzip
parameter_list|(
name|String
name|zipName
parameter_list|,
name|String
name|destDirName
parameter_list|)
throws|throws
name|IOException
block|{
name|Enumeration
name|entries
decl_stmt|;
name|ZipFile
name|zipFile
decl_stmt|;
name|zipFile
operator|=
operator|new
name|ZipFile
argument_list|(
name|zipName
operator|+
literal|".zip"
argument_list|)
expr_stmt|;
name|entries
operator|=
name|zipFile
operator|.
name|entries
argument_list|()
expr_stmt|;
name|String
name|dirName
init|=
name|fullDir
argument_list|(
name|destDirName
argument_list|)
decl_stmt|;
name|File
name|fileDir
init|=
operator|new
name|File
argument_list|(
name|dirName
argument_list|)
decl_stmt|;
name|rmDir
argument_list|(
name|destDirName
argument_list|)
expr_stmt|;
name|fileDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
while|while
condition|(
name|entries
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|ZipEntry
name|entry
init|=
operator|(
name|ZipEntry
operator|)
name|entries
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|InputStream
name|in
init|=
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|OutputStream
name|out
init|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|fileDir
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|8192
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|zipFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCreateCFS
specifier|public
name|void
name|testCreateCFS
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|dirName
init|=
literal|"testindex.cfs"
decl_stmt|;
name|createIndex
argument_list|(
name|dirName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rmDir
argument_list|(
name|dirName
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateNoCFS
specifier|public
name|void
name|testCreateNoCFS
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|dirName
init|=
literal|"testindex.nocfs"
decl_stmt|;
name|createIndex
argument_list|(
name|dirName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rmDir
argument_list|(
name|dirName
argument_list|)
expr_stmt|;
block|}
DECL|method|testSearchOldIndex
specifier|public
name|void
name|testSearchOldIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|oldNames
init|=
block|{
literal|"prelockless.cfs"
block|,
literal|"prelockless.nocfs"
block|}
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
name|oldNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|dirName
init|=
literal|"src/test/org/apache/lucene/index/index."
operator|+
name|oldNames
index|[
name|i
index|]
decl_stmt|;
name|unzip
argument_list|(
name|dirName
argument_list|,
name|oldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|searchIndex
argument_list|(
name|oldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|rmDir
argument_list|(
name|oldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIndexOldIndexNoAdds
specifier|public
name|void
name|testIndexOldIndexNoAdds
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|oldNames
init|=
block|{
literal|"prelockless.cfs"
block|,
literal|"prelockless.nocfs"
block|}
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
name|oldNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|dirName
init|=
literal|"src/test/org/apache/lucene/index/index."
operator|+
name|oldNames
index|[
name|i
index|]
decl_stmt|;
name|unzip
argument_list|(
name|dirName
argument_list|,
name|oldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|changeIndexNoAdds
argument_list|(
name|oldNames
index|[
name|i
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rmDir
argument_list|(
name|oldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|unzip
argument_list|(
name|dirName
argument_list|,
name|oldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|changeIndexNoAdds
argument_list|(
name|oldNames
index|[
name|i
index|]
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rmDir
argument_list|(
name|oldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIndexOldIndex
specifier|public
name|void
name|testIndexOldIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|oldNames
init|=
block|{
literal|"prelockless.cfs"
block|,
literal|"prelockless.nocfs"
block|}
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
name|oldNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|dirName
init|=
literal|"src/test/org/apache/lucene/index/index."
operator|+
name|oldNames
index|[
name|i
index|]
decl_stmt|;
name|unzip
argument_list|(
name|dirName
argument_list|,
name|oldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|changeIndexWithAdds
argument_list|(
name|oldNames
index|[
name|i
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rmDir
argument_list|(
name|oldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|unzip
argument_list|(
name|dirName
argument_list|,
name|oldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|changeIndexWithAdds
argument_list|(
name|oldNames
index|[
name|i
index|]
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rmDir
argument_list|(
name|oldNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|searchIndex
specifier|public
name|void
name|searchIndex
parameter_list|(
name|String
name|dirName
parameter_list|)
throws|throws
name|IOException
block|{
comment|//QueryParser parser = new QueryParser("contents", new WhitespaceAnalyzer());
comment|//Query query = parser.parse("handle:1");
name|dirName
operator|=
name|fullDir
argument_list|(
name|dirName
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dirName
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|34
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|Document
name|d
init|=
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// First document should be #21 since it's norm was increased:
name|assertEquals
argument_list|(
literal|"didn't get the right document first"
argument_list|,
literal|"21"
argument_list|,
name|d
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|searcher
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
comment|/* Open pre-lockless index, add docs, do a delete&    * setNorm, and search */
DECL|method|changeIndexWithAdds
specifier|public
name|void
name|changeIndexWithAdds
parameter_list|(
name|String
name|dirName
parameter_list|,
name|boolean
name|autoCommit
parameter_list|)
throws|throws
name|IOException
block|{
name|dirName
operator|=
name|fullDir
argument_list|(
name|dirName
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dirName
argument_list|)
decl_stmt|;
comment|// open writer
name|IndexWriter
name|writer
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
literal|false
argument_list|)
decl_stmt|;
comment|// add 10 docs
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|35
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
comment|// make sure writer sees right total -- writer seems not to know about deletes in .del?
name|assertEquals
argument_list|(
literal|"wrong doc count"
argument_list|,
literal|45
argument_list|,
name|writer
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// make sure searching sees right # hits
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of hits"
argument_list|,
literal|44
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|Document
name|d
init|=
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong first document"
argument_list|,
literal|"21"
argument_list|,
name|d
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// make sure we can do delete& setNorm against this
comment|// pre-lockless segment:
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
name|Term
name|searchTerm
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|)
decl_stmt|;
name|int
name|delCount
init|=
name|reader
operator|.
name|deleteDocuments
argument_list|(
name|searchTerm
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong delete count"
argument_list|,
literal|1
argument_list|,
name|delCount
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setNorm
argument_list|(
literal|22
argument_list|,
literal|"content"
argument_list|,
operator|(
name|float
operator|)
literal|2.0
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// make sure they "took":
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
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of hits"
argument_list|,
literal|43
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|d
operator|=
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong first document"
argument_list|,
literal|"22"
argument_list|,
name|d
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// optimize
name|writer
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
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of hits"
argument_list|,
literal|43
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|d
operator|=
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong first document"
argument_list|,
literal|"22"
argument_list|,
name|d
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|searcher
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
comment|/* Open pre-lockless index, add docs, do a delete&    * setNorm, and search */
DECL|method|changeIndexNoAdds
specifier|public
name|void
name|changeIndexNoAdds
parameter_list|(
name|String
name|dirName
parameter_list|,
name|boolean
name|autoCommit
parameter_list|)
throws|throws
name|IOException
block|{
name|dirName
operator|=
name|fullDir
argument_list|(
name|dirName
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dirName
argument_list|)
decl_stmt|;
comment|// make sure searching sees right # hits
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of hits"
argument_list|,
literal|34
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|Document
name|d
init|=
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong first document"
argument_list|,
literal|"21"
argument_list|,
name|d
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// make sure we can do a delete& setNorm against this
comment|// pre-lockless segment:
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
name|Term
name|searchTerm
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|)
decl_stmt|;
name|int
name|delCount
init|=
name|reader
operator|.
name|deleteDocuments
argument_list|(
name|searchTerm
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong delete count"
argument_list|,
literal|1
argument_list|,
name|delCount
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setNorm
argument_list|(
literal|22
argument_list|,
literal|"content"
argument_list|,
operator|(
name|float
operator|)
literal|2.0
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// make sure they "took":
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
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of hits"
argument_list|,
literal|33
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|d
operator|=
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong first document"
argument_list|,
literal|"22"
argument_list|,
name|d
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// optimize
name|IndexWriter
name|writer
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
literal|false
argument_list|)
decl_stmt|;
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
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of hits"
argument_list|,
literal|33
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|d
operator|=
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong first document"
argument_list|,
literal|"22"
argument_list|,
name|d
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|searcher
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
DECL|method|createIndex
specifier|public
name|void
name|createIndex
parameter_list|(
name|String
name|dirName
parameter_list|,
name|boolean
name|doCFS
parameter_list|)
throws|throws
name|IOException
block|{
name|dirName
operator|=
name|fullDir
argument_list|(
name|dirName
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dirName
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
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
decl_stmt|;
name|writer
operator|.
name|setUseCompoundFile
argument_list|(
name|doCFS
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
literal|35
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"wrong doc count"
argument_list|,
literal|35
argument_list|,
name|writer
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Delete one doc so we get a .del file:
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
name|Term
name|searchTerm
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|)
decl_stmt|;
name|int
name|delCount
init|=
name|reader
operator|.
name|deleteDocuments
argument_list|(
name|searchTerm
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"didn't delete the right number of documents"
argument_list|,
literal|1
argument_list|,
name|delCount
argument_list|)
expr_stmt|;
comment|// Set one norm so we get a .s0 file:
name|reader
operator|.
name|setNorm
argument_list|(
literal|21
argument_list|,
literal|"content"
argument_list|,
operator|(
name|float
operator|)
literal|1.5
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/* Verifies that the expected file names were produced */
DECL|method|testExactFileNames
specifier|public
name|void
name|testExactFileNames
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
name|String
name|outputDir
init|=
literal|"lucene.backwardscompat0.index"
decl_stmt|;
try|try
block|{
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|fullDir
argument_list|(
name|outputDir
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|autoCommit
init|=
literal|0
operator|==
name|pass
decl_stmt|;
name|IndexWriter
name|writer
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
comment|//IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), true);
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|35
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"wrong doc count"
argument_list|,
literal|35
argument_list|,
name|writer
operator|.
name|docCount
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Delete one doc so we get a .del file:
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
name|Term
name|searchTerm
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|)
decl_stmt|;
name|int
name|delCount
init|=
name|reader
operator|.
name|deleteDocuments
argument_list|(
name|searchTerm
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"didn't delete the right number of documents"
argument_list|,
literal|1
argument_list|,
name|delCount
argument_list|)
expr_stmt|;
comment|// Set one norm so we get a .s0 file:
name|reader
operator|.
name|setNorm
argument_list|(
literal|21
argument_list|,
literal|"content"
argument_list|,
operator|(
name|float
operator|)
literal|1.5
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// The numbering of fields can vary depending on which
comment|// JRE is in use.  On some JREs we see content bound to
comment|// field 0; on others, field 1.  So, here we have to
comment|// figure out which field number corresponds to
comment|// "content", and then set our expected file names below
comment|// accordingly:
name|CompoundFileReader
name|cfsReader
init|=
operator|new
name|CompoundFileReader
argument_list|(
name|dir
argument_list|,
literal|"_2.cfs"
argument_list|)
decl_stmt|;
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|(
name|cfsReader
argument_list|,
literal|"_2.fnm"
argument_list|)
decl_stmt|;
name|int
name|contentFieldIndex
init|=
operator|-
literal|1
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
name|fieldInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|"content"
argument_list|)
condition|)
block|{
name|contentFieldIndex
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
name|cfsReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"could not locate the 'content' field number in the _2.cfs segment"
argument_list|,
name|contentFieldIndex
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Now verify file names:
name|String
index|[]
name|expected
init|=
block|{
literal|"_0.cfs"
block|,
literal|"_0_1.del"
block|,
literal|"_1.cfs"
block|,
literal|"_2.cfs"
block|,
literal|"_2_1.s"
operator|+
name|contentFieldIndex
block|,
literal|"_3.cfs"
block|,
literal|"segments_a"
block|,
literal|"segments.gen"
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|autoCommit
condition|)
block|{
name|expected
index|[
literal|6
index|]
operator|=
literal|"segments_3"
expr_stmt|;
block|}
name|String
index|[]
name|actual
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
name|expected
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|actual
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"incorrect filenames in index: expected:\n    "
operator|+
name|asString
argument_list|(
name|expected
argument_list|)
operator|+
literal|"\n  actual:\n    "
operator|+
name|asString
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|rmDir
argument_list|(
name|outputDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|asString
specifier|private
name|String
name|asString
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
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|int
name|id
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
name|Integer
operator|.
name|toString
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|rmDir
specifier|private
name|void
name|rmDir
parameter_list|(
name|String
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|fileDir
init|=
operator|new
name|File
argument_list|(
name|fullDir
argument_list|(
name|dir
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|fileDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|files
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
name|fileDir
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|fullDir
specifier|public
specifier|static
name|String
name|fullDir
parameter_list|(
name|String
name|dirName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|,
name|dirName
argument_list|)
operator|.
name|getCanonicalPath
argument_list|()
return|;
block|}
block|}
end_class
end_unit
