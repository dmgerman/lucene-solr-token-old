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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|PrintStream
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
name|store
operator|.
name|Lock
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
name|InputStream
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
name|OutputStream
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
name|Similarity
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
name|analysis
operator|.
name|Analyzer
import|;
end_import
begin_comment
comment|/**   An IndexWriter creates and maintains an index.    The third argument to the<a href="#IndexWriter"><b>constructor</b></a>   determines whether a new index is created, or whether an existing index is   opened for the addition of new documents.    In either case, documents are added with the<a   href="#addDocument"><b>addDocument</b></a> method.  When finished adding   documents,<a href="#close"><b>close</b></a> should be called.    If an index will not have more documents added for a while and optimal search   performance is desired, then the<a href="#optimize"><b>optimize</b></a>   method should be called before the index is closed.   */
end_comment
begin_class
DECL|class|IndexWriter
specifier|public
class|class
name|IndexWriter
block|{
comment|/**    * Default value is 1000.  Use<code>org.apache.lucene.writeLockTimeout</code>    * system property to override.    */
DECL|field|WRITE_LOCK_TIMEOUT
specifier|public
specifier|static
name|long
name|WRITE_LOCK_TIMEOUT
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.writeLockTimeout"
argument_list|,
literal|"1000"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Default value is 10000.  Use<code>org.apache.lucene.commitLockTimeout</code>    * system property to override.    */
DECL|field|COMMIT_LOCK_TIMEOUT
specifier|public
specifier|static
name|long
name|COMMIT_LOCK_TIMEOUT
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.commitLockTimeout"
argument_list|,
literal|"10000"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|WRITE_LOCK_NAME
specifier|public
specifier|static
specifier|final
name|String
name|WRITE_LOCK_NAME
init|=
literal|"write.lock"
decl_stmt|;
DECL|field|COMMIT_LOCK_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_LOCK_NAME
init|=
literal|"commit.lock"
decl_stmt|;
comment|/**    * Default value is 10.  Use<code>org.apache.lucene.mergeFactor</code>    * system property to override.    */
DECL|field|DEFAULT_MERGE_FACTOR
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MERGE_FACTOR
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.mergeFactor"
argument_list|,
literal|"10"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Default value is 10.  Use<code>org.apache.lucene.minMergeDocs</code>    * system property to override.    */
DECL|field|DEFAULT_MIN_MERGE_DOCS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_MERGE_DOCS
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.minMergeDocs"
argument_list|,
literal|"10"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Default value is {@link Integer#MAX_VALUE}.    * Use<code>org.apache.lucene.maxMergeDocs</code> system property to override.    */
DECL|field|DEFAULT_MAX_MERGE_DOCS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_MERGE_DOCS
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.maxMergeDocs"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Default value is 10000.  Use<code>org.apache.lucene.maxFieldLength</code>    * system property to override.    */
DECL|field|DEFAULT_MAX_FIELD_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_FIELD_LENGTH
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.maxFieldLength"
argument_list|,
literal|"10000"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
comment|// where this index resides
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
comment|// how to analyze text
DECL|field|similarity
specifier|private
name|Similarity
name|similarity
init|=
name|Similarity
operator|.
name|getDefault
argument_list|()
decl_stmt|;
comment|// how to normalize
DECL|field|segmentInfos
specifier|private
name|SegmentInfos
name|segmentInfos
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
comment|// the segments
DECL|field|ramDirectory
specifier|private
specifier|final
name|Directory
name|ramDirectory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// for temp segs
DECL|field|writeLock
specifier|private
name|Lock
name|writeLock
decl_stmt|;
comment|/** Use compound file setting. Defaults to false to maintain multiple files    *  per segment behavior.    */
DECL|field|useCompoundFile
specifier|private
name|boolean
name|useCompoundFile
init|=
literal|false
decl_stmt|;
comment|/** Setting to turn on usage of a compound file. When on, multiple files    *  for each segment are merged into a single file once the segment creation    *  is finished. This is done regardless of what directory is in use.    */
DECL|method|getUseCompoundFile
specifier|public
name|boolean
name|getUseCompoundFile
parameter_list|()
block|{
return|return
name|useCompoundFile
return|;
block|}
comment|/** Setting to turn on usage of a compound file. When on, multiple files    *  for each segment are merged into a single file once the segment creation    *  is finished. This is done regardless of what directory is in use.    */
DECL|method|setUseCompoundFile
specifier|public
name|void
name|setUseCompoundFile
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|useCompoundFile
operator|=
name|value
expr_stmt|;
block|}
comment|/** Expert: Set the Similarity implementation used by this IndexWriter.    *    * @see Similarity#setDefault(Similarity)    */
DECL|method|setSimilarity
specifier|public
name|void
name|setSimilarity
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
block|}
comment|/** Expert: Return the Similarity implementation used by this IndexWriter.    *    *<p>This defaults to the current value of {@link Similarity#getDefault()}.    */
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
return|return
name|this
operator|.
name|similarity
return|;
block|}
comment|/**    * Constructs an IndexWriter for the index in<code>path</code>.    * Text will be analyzed with<code>a</code>.  If<code>create</code>    * is true, then a new, empty index will be created in    *<code>path</code>, replacing the index already there, if any.    *    * @param path the path to the index directory    * @param a the analyzer to use    * @param create<code>true</code> to create the index or overwrite    *  the existing one;<code>false</code> to append to the existing    *  index    * @param IOException if the directory cannot be read/written to, or    *  if it does not exist, and<code>create</code> is    *<code>false</code>    */
DECL|method|IndexWriter
specifier|public
name|IndexWriter
parameter_list|(
name|String
name|path
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|path
argument_list|,
name|create
argument_list|)
argument_list|,
name|a
argument_list|,
name|create
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs an IndexWriter for the index in<code>path</code>.    * Text will be analyzed with<code>a</code>.  If<code>create</code>    * is true, then a new, empty index will be created in    *<code>path</code>, replacing the index already there, if any.    *    * @param path the path to the index directory    * @param a the analyzer to use    * @param create<code>true</code> to create the index or overwrite    *  the existing one;<code>false</code> to append to the existing    *  index    * @param IOException if the directory cannot be read/written to, or    *  if it does not exist, and<code>create</code> is    *<code>false</code>    */
DECL|method|IndexWriter
specifier|public
name|IndexWriter
parameter_list|(
name|File
name|path
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|path
argument_list|,
name|create
argument_list|)
argument_list|,
name|a
argument_list|,
name|create
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs an IndexWriter for the index in<code>d</code>.    * Text will be analyzed with<code>a</code>.  If<code>create</code>    * is true, then a new, empty index will be created in    *<code>d</code>, replacing the index already there, if any.    *    * @param path the path to the index directory    * @param a the analyzer to use    * @param create<code>true</code> to create the index or overwrite    *  the existing one;<code>false</code> to append to the existing    *  index    * @param IOException if the directory cannot be read/written to, or    *  if it does not exist, and<code>create</code> is    *<code>false</code>    */
DECL|method|IndexWriter
specifier|public
name|IndexWriter
parameter_list|(
name|Directory
name|d
parameter_list|,
name|Analyzer
name|a
parameter_list|,
specifier|final
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|directory
operator|=
name|d
expr_stmt|;
name|analyzer
operator|=
name|a
expr_stmt|;
name|Lock
name|writeLock
init|=
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|writeLock
operator|.
name|obtain
argument_list|(
name|WRITE_LOCK_TIMEOUT
argument_list|)
condition|)
comment|// obtain write lock
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Index locked for write: "
operator|+
name|writeLock
argument_list|)
throw|;
name|this
operator|.
name|writeLock
operator|=
name|writeLock
expr_stmt|;
comment|// save it
synchronized|synchronized
init|(
name|directory
init|)
block|{
comment|// in-& inter-process sync
operator|new
name|Lock
operator|.
name|With
argument_list|(
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|COMMIT_LOCK_NAME
argument_list|)
argument_list|,
name|COMMIT_LOCK_TIMEOUT
argument_list|)
block|{
specifier|public
name|Object
name|doBody
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|create
condition|)
name|segmentInfos
operator|.
name|write
argument_list|(
name|directory
argument_list|)
expr_stmt|;
else|else
name|segmentInfos
operator|.
name|read
argument_list|(
name|directory
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Flushes all changes to an index, closes all associated files, and closes     the directory that the index is stored in. */
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|flushRamSegments
argument_list|()
expr_stmt|;
name|ramDirectory
operator|.
name|close
argument_list|()
expr_stmt|;
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// release write lock
name|writeLock
operator|=
literal|null
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Release the write lock, if needed. */
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writeLock
operator|!=
literal|null
condition|)
block|{
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// release write lock
name|writeLock
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Returns the analyzer used by this index. */
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
comment|/** Returns the number of documents currently in this index. */
DECL|method|docCount
specifier|public
specifier|synchronized
name|int
name|docCount
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
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
name|segmentInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentInfo
name|si
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|count
operator|+=
name|si
operator|.
name|docCount
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/**    * The maximum number of terms that will be indexed for a single field in a    * document.  This limits the amount of memory required for indexing, so that    * collections with very large files will not crash the indexing process by    * running out of memory.<p/>    * Note that this effectively truncates large documents, excluding from the    * index terms that occur further in the document.  If you know your source    * documents are large, be sure to set this value high enough to accomodate    * the expected size.  If you set it to Integer.MAX_VALUE, then the only limit    * is your memory, but you should anticipate an OutOfMemoryError.<p/>    * By default, no more than 10,000 terms will be indexed for a field.   */
DECL|field|maxFieldLength
specifier|public
name|int
name|maxFieldLength
init|=
name|DEFAULT_MAX_FIELD_LENGTH
decl_stmt|;
comment|/**    * Adds a document to this index.  If the document contains more than    * {@link #maxFieldLength} terms for a given field, the remainder are    * discarded.    */
DECL|method|addDocument
specifier|public
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|addDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a document to this index, using the provided analyzer instead of the    * value of {@link #getAnalyzer()}.  If the document contains more than    * {@link #maxFieldLength} terms for a given field, the remainder are    * discarded.    */
DECL|method|addDocument
specifier|public
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|DocumentWriter
name|dw
init|=
operator|new
name|DocumentWriter
argument_list|(
name|ramDirectory
argument_list|,
name|analyzer
argument_list|,
name|similarity
argument_list|,
name|maxFieldLength
argument_list|)
decl_stmt|;
name|String
name|segmentName
init|=
name|newSegmentName
argument_list|()
decl_stmt|;
name|dw
operator|.
name|addDocument
argument_list|(
name|segmentName
argument_list|,
name|doc
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|segmentInfos
operator|.
name|addElement
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|segmentName
argument_list|,
literal|1
argument_list|,
name|ramDirectory
argument_list|)
argument_list|)
expr_stmt|;
name|maybeMergeSegments
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|newSegmentName
specifier|private
specifier|final
specifier|synchronized
name|String
name|newSegmentName
parameter_list|()
block|{
return|return
literal|"_"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|segmentInfos
operator|.
name|counter
operator|++
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
return|;
block|}
comment|/** Determines how often segment indices are merged by addDocument().  With    * smaller values, less RAM is used while indexing, and searches on    * unoptimized indices are faster, but indexing speed is slower.  With larger    * values, more RAM is used during indexing, and while searches on unoptimized    * indices are slower, indexing is faster.  Thus larger values (> 10) are best    * for batch index creation, and smaller values (< 10) for indices that are    * interactively maintained.    *    *<p>This must never be less than 2.  The default value is 10.*/
DECL|field|mergeFactor
specifier|public
name|int
name|mergeFactor
init|=
name|DEFAULT_MERGE_FACTOR
decl_stmt|;
comment|/** Determines the minimal number of documents required before the buffered    * in-memory documents are merging and a new Segment is created.    * Since Documents are merged in a {@link org.apache.lucene.store.RAMDirectory},    * large value gives faster indexing.  At the same time, mergeFactor limits    * the number of files open in a FSDirectory.    *    *<p> The default value is 10.*/
DECL|field|minMergeDocs
specifier|public
name|int
name|minMergeDocs
init|=
name|DEFAULT_MIN_MERGE_DOCS
decl_stmt|;
comment|/** Determines the largest number of documents ever merged by addDocument().    * Small values (e.g., less than 10,000) are best for interactive indexing,    * as this limits the length of pauses while indexing to a few seconds.    * Larger values are best for batched indexing and speedier searches.    *    *<p>The default value is {@link Integer#MAX_VALUE}. */
DECL|field|maxMergeDocs
specifier|public
name|int
name|maxMergeDocs
init|=
name|DEFAULT_MAX_MERGE_DOCS
decl_stmt|;
comment|/** If non-null, information about merges will be printed to this. */
DECL|field|infoStream
specifier|public
name|PrintStream
name|infoStream
init|=
literal|null
decl_stmt|;
comment|/** Merges all segments together into a single segment, optimizing an index       for search. */
DECL|method|optimize
specifier|public
specifier|synchronized
name|void
name|optimize
parameter_list|()
throws|throws
name|IOException
block|{
name|flushRamSegments
argument_list|()
expr_stmt|;
while|while
condition|(
name|segmentInfos
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|||
operator|(
name|segmentInfos
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|(
name|SegmentReader
operator|.
name|hasDeletions
argument_list|(
name|segmentInfos
operator|.
name|info
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|||
operator|(
name|useCompoundFile
operator|&&
operator|!
name|SegmentReader
operator|.
name|usesCompoundFile
argument_list|(
name|segmentInfos
operator|.
name|info
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|)
operator|||
name|segmentInfos
operator|.
name|info
argument_list|(
literal|0
argument_list|)
operator|.
name|dir
operator|!=
name|directory
operator|)
operator|)
condition|)
block|{
name|int
name|minSegment
init|=
name|segmentInfos
operator|.
name|size
argument_list|()
operator|-
name|mergeFactor
decl_stmt|;
name|mergeSegments
argument_list|(
name|minSegment
operator|<
literal|0
condition|?
literal|0
else|:
name|minSegment
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Merges all segments from an array of indexes into this index.    *    *<p>This may be used to parallelize batch indexing.  A large document    * collection can be broken into sub-collections.  Each sub-collection can be    * indexed in parallel, on a different thread, process or machine.  The    * complete index can then be created by merging sub-collection indexes    * with this method.    *    *<p>After this completes, the index is optimized. */
DECL|method|addIndexes
specifier|public
specifier|synchronized
name|void
name|addIndexes
parameter_list|(
name|Directory
index|[]
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
name|optimize
argument_list|()
expr_stmt|;
comment|// start with zero or 1 seg
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
comment|// read infos from dir
name|sis
operator|.
name|read
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sis
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|segmentInfos
operator|.
name|addElement
argument_list|(
name|sis
operator|.
name|info
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
comment|// add each info
block|}
block|}
name|optimize
argument_list|()
expr_stmt|;
comment|// final cleanup
block|}
comment|/** Merges the provided indexes into this index.    *<p>After this completes, the index is optimized. */
DECL|method|addIndexes
specifier|public
specifier|synchronized
name|void
name|addIndexes
parameter_list|(
name|IndexReader
index|[]
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
name|optimize
argument_list|()
expr_stmt|;
comment|// start with zero or 1 seg
name|String
name|mergedName
init|=
name|newSegmentName
argument_list|()
decl_stmt|;
name|SegmentMerger
name|merger
init|=
operator|new
name|SegmentMerger
argument_list|(
name|directory
argument_list|,
name|mergedName
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentInfos
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
comment|// add existing index, if any
name|merger
operator|.
name|add
argument_list|(
operator|new
name|SegmentReader
argument_list|(
name|segmentInfos
operator|.
name|info
argument_list|(
literal|0
argument_list|)
argument_list|)
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
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
comment|// add new indexes
name|merger
operator|.
name|add
argument_list|(
name|readers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|int
name|docCount
init|=
name|merger
operator|.
name|merge
argument_list|()
decl_stmt|;
comment|// merge 'em
name|segmentInfos
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// pop old infos& add new
name|segmentInfos
operator|.
name|addElement
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|mergedName
argument_list|,
name|docCount
argument_list|,
name|directory
argument_list|)
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|directory
init|)
block|{
comment|// in-& inter-process sync
operator|new
name|Lock
operator|.
name|With
argument_list|(
name|directory
operator|.
name|makeLock
argument_list|(
literal|"commit.lock"
argument_list|)
argument_list|,
name|COMMIT_LOCK_TIMEOUT
argument_list|)
block|{
specifier|public
name|Object
name|doBody
parameter_list|()
throws|throws
name|IOException
block|{
name|segmentInfos
operator|.
name|write
argument_list|(
name|directory
argument_list|)
expr_stmt|;
comment|// commit changes
return|return
literal|null
return|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Merges all RAM-resident segments. */
DECL|method|flushRamSegments
specifier|private
specifier|final
name|void
name|flushRamSegments
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|minSegment
init|=
name|segmentInfos
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
name|int
name|docCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|minSegment
operator|>=
literal|0
operator|&&
operator|(
name|segmentInfos
operator|.
name|info
argument_list|(
name|minSegment
argument_list|)
operator|)
operator|.
name|dir
operator|==
name|ramDirectory
condition|)
block|{
name|docCount
operator|+=
name|segmentInfos
operator|.
name|info
argument_list|(
name|minSegment
argument_list|)
operator|.
name|docCount
expr_stmt|;
name|minSegment
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|minSegment
operator|<
literal|0
operator|||
comment|// add one FS segment?
operator|(
name|docCount
operator|+
name|segmentInfos
operator|.
name|info
argument_list|(
name|minSegment
argument_list|)
operator|.
name|docCount
operator|)
operator|>
name|mergeFactor
operator|||
operator|!
operator|(
name|segmentInfos
operator|.
name|info
argument_list|(
name|segmentInfos
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|dir
operator|==
name|ramDirectory
operator|)
condition|)
name|minSegment
operator|++
expr_stmt|;
if|if
condition|(
name|minSegment
operator|>=
name|segmentInfos
operator|.
name|size
argument_list|()
condition|)
return|return;
comment|// none to merge
name|mergeSegments
argument_list|(
name|minSegment
argument_list|)
expr_stmt|;
block|}
comment|/** Incremental segment merger.  */
DECL|method|maybeMergeSegments
specifier|private
specifier|final
name|void
name|maybeMergeSegments
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|targetMergeDocs
init|=
name|minMergeDocs
decl_stmt|;
while|while
condition|(
name|targetMergeDocs
operator|<=
name|maxMergeDocs
condition|)
block|{
comment|// find segments smaller than current target size
name|int
name|minSegment
init|=
name|segmentInfos
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|mergeDocs
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|--
name|minSegment
operator|>=
literal|0
condition|)
block|{
name|SegmentInfo
name|si
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|minSegment
argument_list|)
decl_stmt|;
if|if
condition|(
name|si
operator|.
name|docCount
operator|>=
name|targetMergeDocs
condition|)
break|break;
name|mergeDocs
operator|+=
name|si
operator|.
name|docCount
expr_stmt|;
block|}
if|if
condition|(
name|mergeDocs
operator|>=
name|targetMergeDocs
condition|)
comment|// found a merge to do
name|mergeSegments
argument_list|(
name|minSegment
operator|+
literal|1
argument_list|)
expr_stmt|;
else|else
break|break;
name|targetMergeDocs
operator|*=
name|mergeFactor
expr_stmt|;
comment|// increase target size
block|}
block|}
comment|/** Pops segments off of segmentInfos stack down to minSegment, merges them,     and pushes the merged index onto the top of the segmentInfos stack. */
DECL|method|mergeSegments
specifier|private
specifier|final
name|void
name|mergeSegments
parameter_list|(
name|int
name|minSegment
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|mergedName
init|=
name|newSegmentName
argument_list|()
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|infoStream
operator|.
name|print
argument_list|(
literal|"merging segments"
argument_list|)
expr_stmt|;
name|SegmentMerger
name|merger
init|=
operator|new
name|SegmentMerger
argument_list|(
name|directory
argument_list|,
name|mergedName
argument_list|,
name|useCompoundFile
argument_list|)
decl_stmt|;
specifier|final
name|Vector
name|segmentsToDelete
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|minSegment
init|;
name|i
operator|<
name|segmentInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentInfo
name|si
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|infoStream
operator|.
name|print
argument_list|(
literal|" "
operator|+
name|si
operator|.
name|name
operator|+
literal|" ("
operator|+
name|si
operator|.
name|docCount
operator|+
literal|" docs)"
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|new
name|SegmentReader
argument_list|(
name|si
argument_list|)
decl_stmt|;
name|merger
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|reader
operator|.
name|directory
argument_list|()
operator|==
name|this
operator|.
name|directory
operator|)
operator|||
comment|// if we own the directory
operator|(
name|reader
operator|.
name|directory
argument_list|()
operator|==
name|this
operator|.
name|ramDirectory
operator|)
condition|)
name|segmentsToDelete
operator|.
name|addElement
argument_list|(
name|reader
argument_list|)
expr_stmt|;
comment|// queue segment for deletion
block|}
name|int
name|mergedDocCount
init|=
name|merger
operator|.
name|merge
argument_list|()
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|infoStream
operator|.
name|println
argument_list|()
expr_stmt|;
name|infoStream
operator|.
name|println
argument_list|(
literal|" into "
operator|+
name|mergedName
operator|+
literal|" ("
operator|+
name|mergedDocCount
operator|+
literal|" docs)"
argument_list|)
expr_stmt|;
block|}
name|segmentInfos
operator|.
name|setSize
argument_list|(
name|minSegment
argument_list|)
expr_stmt|;
comment|// pop old infos& add new
name|segmentInfos
operator|.
name|addElement
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|mergedName
argument_list|,
name|mergedDocCount
argument_list|,
name|directory
argument_list|)
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|directory
init|)
block|{
comment|// in-& inter-process sync
operator|new
name|Lock
operator|.
name|With
argument_list|(
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|COMMIT_LOCK_NAME
argument_list|)
argument_list|,
name|COMMIT_LOCK_TIMEOUT
argument_list|)
block|{
specifier|public
name|Object
name|doBody
parameter_list|()
throws|throws
name|IOException
block|{
name|segmentInfos
operator|.
name|write
argument_list|(
name|directory
argument_list|)
expr_stmt|;
comment|// commit before deleting
name|deleteSegments
argument_list|(
name|segmentsToDelete
argument_list|)
expr_stmt|;
comment|// delete now-unused segments
return|return
literal|null
return|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
comment|/* Some operating systems (e.g. Windows) don't permit a file to be deleted      while it is opened for read (e.g. by another process or thread).  So we      assume that when a delete fails it is because the file is open in another      process, and queue the file for subsequent deletion. */
DECL|method|deleteSegments
specifier|private
specifier|final
name|void
name|deleteSegments
parameter_list|(
name|Vector
name|segments
parameter_list|)
throws|throws
name|IOException
block|{
name|Vector
name|deletable
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|deleteFiles
argument_list|(
name|readDeleteableFiles
argument_list|()
argument_list|,
name|deletable
argument_list|)
expr_stmt|;
comment|// try to delete deleteable
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
operator|(
name|SegmentReader
operator|)
name|segments
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|directory
argument_list|()
operator|==
name|this
operator|.
name|directory
condition|)
name|deleteFiles
argument_list|(
name|reader
operator|.
name|files
argument_list|()
argument_list|,
name|deletable
argument_list|)
expr_stmt|;
comment|// try to delete our files
else|else
name|deleteFiles
argument_list|(
name|reader
operator|.
name|files
argument_list|()
argument_list|,
name|reader
operator|.
name|directory
argument_list|()
argument_list|)
expr_stmt|;
comment|// delete other files
block|}
name|writeDeleteableFiles
argument_list|(
name|deletable
argument_list|)
expr_stmt|;
comment|// note files we can't delete
block|}
DECL|method|deleteFiles
specifier|private
specifier|final
name|void
name|deleteFiles
parameter_list|(
name|Vector
name|files
parameter_list|,
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|directory
operator|.
name|deleteFile
argument_list|(
operator|(
name|String
operator|)
name|files
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteFiles
specifier|private
specifier|final
name|void
name|deleteFiles
parameter_list|(
name|Vector
name|files
parameter_list|,
name|Vector
name|deletable
parameter_list|)
throws|throws
name|IOException
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|file
init|=
operator|(
name|String
operator|)
name|files
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|// try to delete each file
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// if delete fails
if|if
condition|(
name|directory
operator|.
name|fileExists
argument_list|(
name|file
argument_list|)
condition|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|infoStream
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"; Will re-try later."
argument_list|)
expr_stmt|;
name|deletable
operator|.
name|addElement
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|// add to deletable
block|}
block|}
block|}
block|}
DECL|method|readDeleteableFiles
specifier|private
specifier|final
name|Vector
name|readDeleteableFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|Vector
name|result
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|directory
operator|.
name|fileExists
argument_list|(
literal|"deletable"
argument_list|)
condition|)
return|return
name|result
return|;
name|InputStream
name|input
init|=
name|directory
operator|.
name|openFile
argument_list|(
literal|"deletable"
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
name|input
operator|.
name|readInt
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
comment|// read file names
name|result
operator|.
name|addElement
argument_list|(
name|input
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|writeDeleteableFiles
specifier|private
specifier|final
name|void
name|writeDeleteableFiles
parameter_list|(
name|Vector
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|output
init|=
name|directory
operator|.
name|createFile
argument_list|(
literal|"deleteable.new"
argument_list|)
decl_stmt|;
try|try
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|files
operator|.
name|size
argument_list|()
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
name|files
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|output
operator|.
name|writeString
argument_list|(
operator|(
name|String
operator|)
name|files
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|directory
operator|.
name|renameFile
argument_list|(
literal|"deleteable.new"
argument_list|,
literal|"deletable"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
