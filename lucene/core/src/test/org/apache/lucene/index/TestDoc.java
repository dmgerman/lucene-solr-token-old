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
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|util
operator|.
name|Collection
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
name|LinkedList
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|DocIdSetIterator
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
name|IOContext
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
name|store
operator|.
name|TrackingDirectoryWrapper
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
name|Constants
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
name|InfoStream
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
begin_comment
comment|/** JUnit adaptation of an older test case DocTest. */
end_comment
begin_class
DECL|class|TestDoc
specifier|public
class|class
name|TestDoc
extends|extends
name|LuceneTestCase
block|{
DECL|field|workDir
specifier|private
name|File
name|workDir
decl_stmt|;
DECL|field|indexDir
specifier|private
name|File
name|indexDir
decl_stmt|;
DECL|field|files
specifier|private
name|LinkedList
argument_list|<
name|File
argument_list|>
name|files
decl_stmt|;
comment|/** Set the test case. This test case needs      *  a few text files created in the current working directory.      */
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
literal|"TEST: setUp"
argument_list|)
expr_stmt|;
block|}
name|workDir
operator|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"TestDoc"
argument_list|)
expr_stmt|;
name|workDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|indexDir
operator|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"testIndex"
argument_list|)
expr_stmt|;
name|indexDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|Directory
name|directory
init|=
name|newFSDirectory
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|files
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|createOutput
argument_list|(
literal|"test.txt"
argument_list|,
literal|"This is the first test file"
argument_list|)
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|createOutput
argument_list|(
literal|"test2.txt"
argument_list|,
literal|"This is the second test file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createOutput
specifier|private
name|File
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|Writer
name|fw
init|=
literal|null
decl_stmt|;
name|PrintWriter
name|pw
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
name|fw
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|pw
operator|=
operator|new
name|PrintWriter
argument_list|(
name|fw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|pw
operator|!=
literal|null
condition|)
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|fw
operator|!=
literal|null
condition|)
name|fw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** This test executes a number of merges and compares the contents of      *  the segments created when using compound file or not using one.      *      *  TODO: the original test used to print the segment contents to System.out      *        for visual validation. To have the same effect, a new method      *        checkSegment(String name, ...) should be created that would      *        assert various things about the segment.      */
DECL|method|testIndexAndMerge
specifier|public
name|void
name|testIndexAndMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Directory
name|directory
init|=
name|newFSDirectory
argument_list|(
name|indexDir
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|directory
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// We create unreferenced files (we don't even write
comment|// a segments file):
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|directory
operator|)
operator|.
name|setAssertNoUnrefencedFilesOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SegmentCommitInfo
name|si1
init|=
name|indexDoc
argument_list|(
name|writer
argument_list|,
literal|"test.txt"
argument_list|)
decl_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
name|si1
argument_list|)
expr_stmt|;
name|SegmentCommitInfo
name|si2
init|=
name|indexDoc
argument_list|(
name|writer
argument_list|,
literal|"test2.txt"
argument_list|)
decl_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
name|si2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentCommitInfo
name|siMerge
init|=
name|merge
argument_list|(
name|directory
argument_list|,
name|si1
argument_list|,
name|si2
argument_list|,
literal|"_merge"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
name|siMerge
argument_list|)
expr_stmt|;
name|SegmentCommitInfo
name|siMerge2
init|=
name|merge
argument_list|(
name|directory
argument_list|,
name|si1
argument_list|,
name|si2
argument_list|,
literal|"_merge2"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
name|siMerge2
argument_list|)
expr_stmt|;
name|SegmentCommitInfo
name|siMerge3
init|=
name|merge
argument_list|(
name|directory
argument_list|,
name|siMerge
argument_list|,
name|siMerge2
argument_list|,
literal|"_merge3"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
name|siMerge3
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|sw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|multiFileOutput
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|//System.out.println(multiFileOutput);
name|sw
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|out
operator|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|directory
operator|=
name|newFSDirectory
argument_list|(
name|indexDir
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|directory
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
comment|// We create unreferenced files (we don't even write
comment|// a segments file):
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|directory
operator|)
operator|.
name|setAssertNoUnrefencedFilesOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|si1
operator|=
name|indexDoc
argument_list|(
name|writer
argument_list|,
literal|"test.txt"
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
name|si1
argument_list|)
expr_stmt|;
name|si2
operator|=
name|indexDoc
argument_list|(
name|writer
argument_list|,
literal|"test2.txt"
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
name|si2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|siMerge
operator|=
name|merge
argument_list|(
name|directory
argument_list|,
name|si1
argument_list|,
name|si2
argument_list|,
literal|"_merge"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
name|siMerge
argument_list|)
expr_stmt|;
name|siMerge2
operator|=
name|merge
argument_list|(
name|directory
argument_list|,
name|si1
argument_list|,
name|si2
argument_list|,
literal|"_merge2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
name|siMerge2
argument_list|)
expr_stmt|;
name|siMerge3
operator|=
name|merge
argument_list|(
name|directory
argument_list|,
name|siMerge
argument_list|,
name|siMerge2
argument_list|,
literal|"_merge3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
name|siMerge3
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|sw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|singleFileOutput
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|multiFileOutput
argument_list|,
name|singleFileOutput
argument_list|)
expr_stmt|;
block|}
DECL|method|indexDoc
specifier|private
name|SegmentCommitInfo
name|indexDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|InputStreamReader
name|is
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"contents"
argument_list|,
name|is
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
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|writer
operator|.
name|newestSegment
argument_list|()
return|;
block|}
DECL|method|merge
specifier|private
name|SegmentCommitInfo
name|merge
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentCommitInfo
name|si1
parameter_list|,
name|SegmentCommitInfo
name|si2
parameter_list|,
name|String
name|merged
parameter_list|,
name|boolean
name|useCompoundFile
parameter_list|)
throws|throws
name|Exception
block|{
name|IOContext
name|context
init|=
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|SegmentReader
name|r1
init|=
operator|new
name|SegmentReader
argument_list|(
name|si1
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|SegmentReader
name|r2
init|=
operator|new
name|SegmentReader
argument_list|(
name|si2
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|Codec
name|codec
init|=
name|Codec
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|TrackingDirectoryWrapper
name|trackingDir
init|=
operator|new
name|TrackingDirectoryWrapper
argument_list|(
name|si1
operator|.
name|info
operator|.
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|SegmentInfo
name|si
init|=
operator|new
name|SegmentInfo
argument_list|(
name|si1
operator|.
name|info
operator|.
name|dir
argument_list|,
name|Constants
operator|.
name|LUCENE_MAIN_VERSION
argument_list|,
name|merged
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|,
name|codec
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SegmentMerger
name|merger
init|=
operator|new
name|SegmentMerger
argument_list|(
name|Arrays
operator|.
expr|<
name|AtomicReader
operator|>
name|asList
argument_list|(
name|r1
argument_list|,
name|r2
argument_list|)
argument_list|,
name|si
argument_list|,
name|InfoStream
operator|.
name|getDefault
argument_list|()
argument_list|,
name|trackingDir
argument_list|,
name|MergeState
operator|.
name|CheckAbort
operator|.
name|NONE
argument_list|,
operator|new
name|FieldInfos
operator|.
name|FieldNumbers
argument_list|()
argument_list|,
name|context
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MergeState
name|mergeState
init|=
name|merger
operator|.
name|merge
argument_list|()
decl_stmt|;
name|r1
operator|.
name|close
argument_list|()
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|SegmentInfo
name|info
init|=
operator|new
name|SegmentInfo
argument_list|(
name|si1
operator|.
name|info
operator|.
name|dir
argument_list|,
name|Constants
operator|.
name|LUCENE_MAIN_VERSION
argument_list|,
name|merged
argument_list|,
name|si1
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
operator|+
name|si2
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
argument_list|,
literal|false
argument_list|,
name|codec
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|info
operator|.
name|setFiles
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|trackingDir
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|useCompoundFile
condition|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|filesToDelete
init|=
name|IndexWriter
operator|.
name|createCompoundFile
argument_list|(
name|InfoStream
operator|.
name|getDefault
argument_list|()
argument_list|,
name|dir
argument_list|,
name|MergeState
operator|.
name|CheckAbort
operator|.
name|NONE
argument_list|,
name|info
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|info
operator|.
name|setUseCompoundFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|fileToDelete
range|:
name|filesToDelete
control|)
block|{
name|si1
operator|.
name|info
operator|.
name|dir
operator|.
name|deleteFile
argument_list|(
name|fileToDelete
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|SegmentCommitInfo
argument_list|(
name|info
argument_list|,
literal|0
argument_list|,
operator|-
literal|1L
argument_list|,
operator|-
literal|1L
argument_list|)
return|;
block|}
DECL|method|printSegment
specifier|private
name|void
name|printSegment
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|SegmentCommitInfo
name|si
parameter_list|)
throws|throws
name|Exception
block|{
name|SegmentReader
name|reader
init|=
operator|new
name|SegmentReader
argument_list|(
name|si
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
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
name|reader
operator|.
name|numDocs
argument_list|()
condition|;
name|i
operator|++
control|)
name|out
operator|.
name|println
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|TermsEnum
name|tis
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
name|tis
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"  term="
operator|+
name|field
operator|+
literal|":"
operator|+
name|tis
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    DF="
operator|+
name|tis
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
name|DocsAndPositionsEnum
name|positions
init|=
name|tis
operator|.
name|docsAndPositions
argument_list|(
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
name|positions
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|" doc="
operator|+
name|positions
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|" TF="
operator|+
name|positions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|" pos="
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|positions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|positions
operator|.
name|freq
argument_list|()
condition|;
name|j
operator|++
control|)
name|out
operator|.
name|print
argument_list|(
literal|","
operator|+
name|positions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
