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
name|Closeable
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
name|util
operator|.
name|Iterator
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
name|NullInfoStream
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
comment|/** Silly class that randomizes the indexing experience.  EG  *  it may swap in a different merge policy/scheduler; may  *  commit periodically; may or may not forceMerge in the end,  *  may flush by doc count instead of RAM, etc.   */
end_comment
begin_class
DECL|class|RandomIndexWriter
specifier|public
class|class
name|RandomIndexWriter
implements|implements
name|Closeable
block|{
DECL|field|w
specifier|public
name|IndexWriter
name|w
decl_stmt|;
DECL|field|r
specifier|private
specifier|final
name|Random
name|r
decl_stmt|;
DECL|field|docCount
name|int
name|docCount
decl_stmt|;
DECL|field|flushAt
name|int
name|flushAt
decl_stmt|;
DECL|field|flushAtFactor
specifier|private
name|double
name|flushAtFactor
init|=
literal|1.0
decl_stmt|;
DECL|field|getReaderCalled
specifier|private
name|boolean
name|getReaderCalled
decl_stmt|;
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
decl_stmt|;
comment|// sugar
DECL|method|mockIndexWriter
specifier|public
specifier|static
name|IndexWriter
name|mockIndexWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|IndexWriterConfig
name|conf
parameter_list|,
name|Random
name|r
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Randomly calls Thread.yield so we mixup thread scheduling
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|mockIndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|,
operator|new
name|TestPoint
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|2
condition|)
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|mockIndexWriter
specifier|public
specifier|static
name|IndexWriter
name|mockIndexWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|IndexWriterConfig
name|conf
parameter_list|,
name|TestPoint
name|testPoint
parameter_list|)
throws|throws
name|IOException
block|{
name|conf
operator|.
name|setInfoStream
argument_list|(
operator|new
name|TestPointInfoStream
argument_list|(
name|conf
operator|.
name|getInfoStream
argument_list|()
argument_list|,
name|testPoint
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/** create a RandomIndexWriter with a random config: Uses MockAnalyzer */
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|LuceneTestCase
operator|.
name|newIndexWriterConfig
argument_list|(
name|r
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** create a RandomIndexWriter with a random config */
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|LuceneTestCase
operator|.
name|newIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** create a RandomIndexWriter with the provided config */
DECL|method|RandomIndexWriter
specifier|public
name|RandomIndexWriter
parameter_list|(
name|Random
name|r
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|IndexWriterConfig
name|c
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: this should be solved in a different way; Random should not be shared (!).
name|this
operator|.
name|r
operator|=
operator|new
name|Random
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|=
name|mockIndexWriter
argument_list|(
name|dir
argument_list|,
name|c
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|flushAt
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|codec
operator|=
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getCodec
argument_list|()
expr_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW dir="
operator|+
name|dir
operator|+
literal|" config="
operator|+
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
literal|"codec default="
operator|+
name|codec
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Make sure we sometimes test indices that don't get
comment|// any forced merges:
name|doRandomForceMerge
operator|=
operator|!
operator|(
name|c
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|NoMergePolicy
operator|)
operator|&&
name|r
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds a Document.    * @see IndexWriter#addDocument(org.apache.lucene.index.IndexDocument)    */
DECL|method|addDocument
specifier|public
parameter_list|<
name|T
extends|extends
name|IndexableField
parameter_list|>
name|void
name|addDocument
parameter_list|(
specifier|final
name|IndexDocument
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|addDocument
argument_list|(
name|doc
argument_list|,
name|w
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addDocument
specifier|public
parameter_list|<
name|T
extends|extends
name|IndexableField
parameter_list|>
name|void
name|addDocument
parameter_list|(
specifier|final
name|IndexDocument
name|doc
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|3
condition|)
block|{
comment|// TODO: maybe, we should simply buffer up added docs
comment|// (but we need to clone them), and only when
comment|// getReader, commit, etc. are called, we do an
comment|// addDocuments?  Would be better testing.
name|w
operator|.
name|addDocuments
argument_list|(
operator|new
name|Iterable
argument_list|<
name|IndexDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|IndexDocument
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|IndexDocument
argument_list|>
argument_list|()
block|{
name|boolean
name|done
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|!
name|done
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|IndexDocument
name|next
parameter_list|()
block|{
if|if
condition|(
name|done
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
name|done
operator|=
literal|true
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
name|maybeCommit
argument_list|()
expr_stmt|;
block|}
DECL|method|maybeCommit
specifier|private
name|void
name|maybeCommit
parameter_list|()
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|docCount
operator|++
operator|==
name|flushAt
condition|)
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW.add/updateDocument: now doing a commit at docCount="
operator|+
name|docCount
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|flushAt
operator|+=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
call|(
name|int
call|)
argument_list|(
name|flushAtFactor
operator|*
literal|10
argument_list|)
argument_list|,
call|(
name|int
call|)
argument_list|(
name|flushAtFactor
operator|*
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|flushAtFactor
operator|<
literal|2e6
condition|)
block|{
comment|// gradually but exponentially increase time b/w flushes
name|flushAtFactor
operator|*=
literal|1.05
expr_stmt|;
block|}
block|}
block|}
DECL|method|addDocuments
specifier|public
name|void
name|addDocuments
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexDocument
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|maybeCommit
argument_list|()
expr_stmt|;
block|}
DECL|method|updateDocuments
specifier|public
name|void
name|updateDocuments
parameter_list|(
name|Term
name|delTerm
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexDocument
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|updateDocuments
argument_list|(
name|delTerm
argument_list|,
name|docs
argument_list|)
expr_stmt|;
name|maybeCommit
argument_list|()
expr_stmt|;
block|}
comment|/**    * Updates a document.    * @see IndexWriter#updateDocument(Term, org.apache.lucene.index.IndexDocument)    */
DECL|method|updateDocument
specifier|public
parameter_list|<
name|T
extends|extends
name|IndexableField
parameter_list|>
name|void
name|updateDocument
parameter_list|(
name|Term
name|t
parameter_list|,
specifier|final
name|IndexDocument
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|3
condition|)
block|{
name|w
operator|.
name|updateDocuments
argument_list|(
name|t
argument_list|,
operator|new
name|Iterable
argument_list|<
name|IndexDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|IndexDocument
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|IndexDocument
argument_list|>
argument_list|()
block|{
name|boolean
name|done
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|!
name|done
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|IndexDocument
name|next
parameter_list|()
block|{
if|if
condition|(
name|done
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
name|done
operator|=
literal|true
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|w
operator|.
name|updateDocument
argument_list|(
name|t
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|maybeCommit
argument_list|()
expr_stmt|;
block|}
DECL|method|addIndexes
specifier|public
name|void
name|addIndexes
parameter_list|(
name|Directory
modifier|...
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|addIndexes
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
block|}
DECL|method|addIndexes
specifier|public
name|void
name|addIndexes
parameter_list|(
name|IndexReader
modifier|...
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|addIndexes
argument_list|(
name|readers
argument_list|)
expr_stmt|;
block|}
DECL|method|updateNumericDocValue
specifier|public
name|void
name|updateNumericDocValue
parameter_list|(
name|Term
name|term
parameter_list|,
name|String
name|field
parameter_list|,
name|Long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|updateNumericDocValue
argument_list|(
name|term
argument_list|,
name|field
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|updateBinaryDocValue
specifier|public
name|void
name|updateBinaryDocValue
parameter_list|(
name|Term
name|term
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|updateBinaryDocValue
argument_list|(
name|term
argument_list|,
name|field
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|updateDocValues
specifier|public
name|void
name|updateDocValues
parameter_list|(
name|Term
name|term
parameter_list|,
name|Field
modifier|...
name|updates
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|updateDocValues
argument_list|(
name|term
argument_list|,
name|updates
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDocuments
specifier|public
name|void
name|deleteDocuments
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDocuments
specifier|public
name|void
name|deleteDocuments
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|w
operator|.
name|numDocs
argument_list|()
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|w
operator|.
name|maxDoc
argument_list|()
return|;
block|}
DECL|method|deleteAll
specifier|public
name|void
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|w
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
block|}
DECL|method|getReader
specifier|public
name|DirectoryReader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|getReader
argument_list|(
literal|true
argument_list|)
return|;
block|}
DECL|field|doRandomForceMerge
specifier|private
name|boolean
name|doRandomForceMerge
init|=
literal|true
decl_stmt|;
DECL|field|doRandomForceMergeAssert
specifier|private
name|boolean
name|doRandomForceMergeAssert
init|=
literal|true
decl_stmt|;
DECL|method|forceMergeDeletes
specifier|public
name|void
name|forceMergeDeletes
parameter_list|(
name|boolean
name|doWait
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|forceMergeDeletes
argument_list|(
name|doWait
argument_list|)
expr_stmt|;
block|}
DECL|method|forceMergeDeletes
specifier|public
name|void
name|forceMergeDeletes
parameter_list|()
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|forceMergeDeletes
argument_list|()
expr_stmt|;
block|}
DECL|method|setDoRandomForceMerge
specifier|public
name|void
name|setDoRandomForceMerge
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|doRandomForceMerge
operator|=
name|v
expr_stmt|;
block|}
DECL|method|setDoRandomForceMergeAssert
specifier|public
name|void
name|setDoRandomForceMergeAssert
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|doRandomForceMergeAssert
operator|=
name|v
expr_stmt|;
block|}
DECL|method|doRandomForceMerge
specifier|private
name|void
name|doRandomForceMerge
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|doRandomForceMerge
condition|)
block|{
specifier|final
name|int
name|segCount
init|=
name|w
operator|.
name|getSegmentCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
operator|||
name|segCount
operator|==
literal|0
condition|)
block|{
comment|// full forceMerge
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW: doRandomForceMerge(1)"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// partial forceMerge
specifier|final
name|int
name|limit
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
name|segCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW: doRandomForceMerge("
operator|+
name|limit
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
name|limit
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|doRandomForceMergeAssert
operator|||
name|w
operator|.
name|getSegmentCount
argument_list|()
operator|<=
name|limit
operator|:
literal|"limit="
operator|+
name|limit
operator|+
literal|" actual="
operator|+
name|w
operator|.
name|getSegmentCount
argument_list|()
assert|;
block|}
else|else
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW: do random forceMergeDeletes()"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMergeDeletes
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getReader
specifier|public
name|DirectoryReader
name|getReader
parameter_list|(
name|boolean
name|applyDeletions
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|getReaderCalled
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|2
condition|)
block|{
name|doRandomForceMerge
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|applyDeletions
operator|||
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW.getReader: use NRT reader"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|1
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
return|return
name|w
operator|.
name|getReader
argument_list|(
name|applyDeletions
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RIW.getReader: open new reader"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
operator|.
name|getDirectory
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|w
operator|.
name|getReader
argument_list|(
name|applyDeletions
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Close this writer.    * @see IndexWriter#close()    */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|w
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// if someone isn't using getReader() API, we want to be sure to
comment|// forceMerge since presumably they might open a reader on the dir.
if|if
condition|(
name|getReaderCalled
operator|==
literal|false
operator|&&
name|r
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
operator|==
literal|2
condition|)
block|{
name|doRandomForceMerge
argument_list|()
expr_stmt|;
if|if
condition|(
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getCommitOnClose
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// index may have changed, must commit the changes, or otherwise they are discarded by the call to close()
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Forces a forceMerge.    *<p>    * NOTE: this should be avoided in tests unless absolutely necessary,    * as it will result in less test coverage.    * @see IndexWriter#forceMerge(int)    */
DECL|method|forceMerge
specifier|public
name|void
name|forceMerge
parameter_list|(
name|int
name|maxSegmentCount
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneTestCase
operator|.
name|maybeChangeLiveIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|w
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|forceMerge
argument_list|(
name|maxSegmentCount
argument_list|)
expr_stmt|;
block|}
DECL|class|TestPointInfoStream
specifier|static
specifier|final
class|class
name|TestPointInfoStream
extends|extends
name|InfoStream
block|{
DECL|field|delegate
specifier|private
specifier|final
name|InfoStream
name|delegate
decl_stmt|;
DECL|field|testPoint
specifier|private
specifier|final
name|TestPoint
name|testPoint
decl_stmt|;
DECL|method|TestPointInfoStream
specifier|public
name|TestPointInfoStream
parameter_list|(
name|InfoStream
name|delegate
parameter_list|,
name|TestPoint
name|testPoint
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
operator|==
literal|null
condition|?
operator|new
name|NullInfoStream
argument_list|()
else|:
name|delegate
expr_stmt|;
name|this
operator|.
name|testPoint
operator|=
name|testPoint
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|message
specifier|public
name|void
name|message
parameter_list|(
name|String
name|component
parameter_list|,
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
literal|"TP"
operator|.
name|equals
argument_list|(
name|component
argument_list|)
condition|)
block|{
name|testPoint
operator|.
name|apply
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|delegate
operator|.
name|isEnabled
argument_list|(
name|component
argument_list|)
condition|)
block|{
name|delegate
operator|.
name|message
argument_list|(
name|component
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|(
name|String
name|component
parameter_list|)
block|{
return|return
literal|"TP"
operator|.
name|equals
argument_list|(
name|component
argument_list|)
operator|||
name|delegate
operator|.
name|isEnabled
argument_list|(
name|component
argument_list|)
return|;
block|}
block|}
comment|/**    * Simple interface that is executed for each<tt>TP</tt> {@link InfoStream} component    * message. See also {@link RandomIndexWriter#mockIndexWriter(Directory, IndexWriterConfig, TestPoint)}    */
DECL|interface|TestPoint
specifier|public
specifier|static
interface|interface
name|TestPoint
block|{
DECL|method|apply
specifier|public
specifier|abstract
name|void
name|apply
parameter_list|(
name|String
name|message
parameter_list|)
function_decl|;
block|}
block|}
end_class
end_unit
