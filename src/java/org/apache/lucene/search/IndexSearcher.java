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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|FieldSelector
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
name|CorruptIndexException
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
name|ReaderUtil
import|;
end_import
begin_comment
comment|/** Implements search over a single IndexReader.  *  *<p>Applications usually need only call the inherited  * {@link #search(Query,int)}  * or {@link #search(Query,Filter,int)} methods. For performance reasons it is   * recommended to open only one IndexSearcher and use it for all of your searches.  *   *<a name="thread-safety"></a><p><b>NOTE</b>: {@link  *<code>IndexSearcher</code>} instances are completely  * thread safe, meaning multiple threads can call any of its  * methods, concurrently.  If your application requires  * external synchronization, you should<b>not</b>  * synchronize on the<code>IndexSearcher</code> instance;  * use your own (non-Lucene) objects instead.</p>  */
end_comment
begin_class
DECL|class|IndexSearcher
specifier|public
class|class
name|IndexSearcher
extends|extends
name|Searcher
block|{
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|closeReader
specifier|private
name|boolean
name|closeReader
decl_stmt|;
comment|// NOTE: these members might change in incompatible ways
comment|// in the next release
DECL|field|subReaders
specifier|protected
name|IndexReader
index|[]
name|subReaders
decl_stmt|;
DECL|field|docStarts
specifier|protected
name|int
index|[]
name|docStarts
decl_stmt|;
comment|/** Creates a searcher searching the index in the named    *  directory, with readOnly=true    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @param path directory where IndexReader will be opened    */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|Directory
name|path
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a searcher searching the index in the named    *  directory.  You should pass readOnly=true, since it    *  gives much better concurrent performance, unless you    *  intend to do write operations (delete documents or    *  change norms) with the underlying IndexReader.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @param path directory where IndexReader will be opened    * @param readOnly if true, the underlying IndexReader    * will be opened readOnly    */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|Directory
name|path
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|path
argument_list|,
name|readOnly
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a searcher searching the provided index. */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
name|this
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: directly specify the reader, subReaders and    *  their docID starts.    *     *<p><b>NOTE:</b> This API is experimental and    * might change in incompatible ways in the next    * release.</font></p> */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|IndexReader
index|[]
name|subReaders
parameter_list|,
name|int
index|[]
name|docStarts
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|subReaders
operator|=
name|subReaders
expr_stmt|;
name|this
operator|.
name|docStarts
operator|=
name|docStarts
expr_stmt|;
name|closeReader
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|IndexSearcher
specifier|private
name|IndexSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|boolean
name|closeReader
parameter_list|)
block|{
name|reader
operator|=
name|r
expr_stmt|;
name|this
operator|.
name|closeReader
operator|=
name|closeReader
expr_stmt|;
name|List
argument_list|<
name|IndexReader
argument_list|>
name|subReadersList
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexReader
argument_list|>
argument_list|()
decl_stmt|;
name|gatherSubReaders
argument_list|(
name|subReadersList
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|subReaders
operator|=
name|subReadersList
operator|.
name|toArray
argument_list|(
operator|new
name|IndexReader
index|[
name|subReadersList
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|docStarts
operator|=
operator|new
name|int
index|[
name|subReaders
operator|.
name|length
index|]
expr_stmt|;
name|int
name|maxDoc
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docStarts
index|[
name|i
index|]
operator|=
name|maxDoc
expr_stmt|;
name|maxDoc
operator|+=
name|subReaders
index|[
name|i
index|]
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|gatherSubReaders
specifier|protected
name|void
name|gatherSubReaders
parameter_list|(
name|List
argument_list|<
name|IndexReader
argument_list|>
name|allSubReaders
parameter_list|,
name|IndexReader
name|r
parameter_list|)
block|{
name|ReaderUtil
operator|.
name|gatherSubReaders
argument_list|(
name|allSubReaders
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
comment|/** Return the {@link IndexReader} this searches. */
DECL|method|getIndexReader
specifier|public
name|IndexReader
name|getIndexReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
comment|/**    * Note that the underlying IndexReader is not closed, if    * IndexSearcher was constructed with IndexSearcher(IndexReader r).    * If the IndexReader was supplied implicitly by specifying a directory, then    * the IndexReader gets closed.    */
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
name|closeReader
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
return|;
block|}
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
return|;
block|}
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|,
name|fieldSelector
argument_list|)
return|;
block|}
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|maxDoc
argument_list|()
return|;
block|}
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|int
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nDocs
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"nDocs must be> 0"
argument_list|)
throw|;
block|}
name|TopScoreDocCollector
name|collector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|nDocs
argument_list|,
operator|!
name|weight
operator|.
name|scoresDocsOutOfOrder
argument_list|()
argument_list|)
decl_stmt|;
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|collector
argument_list|)
expr_stmt|;
return|return
name|collector
operator|.
name|topDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|int
name|nDocs
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Just like {@link #search(Weight, Filter, int, Sort)}, but you choose    * whether or not the fields in the returned {@link FieldDoc} instances should    * be set by specifying fillFields.    *    *<p>NOTE: this does not compute scores by default.  If you    * need scores, create a {@link TopFieldCollector}    * instance by calling {@link TopFieldCollector#create} and    * then pass that to {@link #search(Weight, Filter,    * Collector)}.</p>    */
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|int
name|nDocs
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|boolean
name|fillFields
parameter_list|)
throws|throws
name|IOException
block|{
name|TopFieldCollector
name|collector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|nDocs
argument_list|,
name|fillFields
argument_list|,
name|fieldSortDoTrackScores
argument_list|,
name|fieldSortDoMaxScore
argument_list|,
operator|!
name|weight
operator|.
name|scoresDocsOutOfOrder
argument_list|()
argument_list|)
decl_stmt|;
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|collector
argument_list|)
expr_stmt|;
return|return
operator|(
name|TopFieldDocs
operator|)
name|collector
operator|.
name|topDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|filter
operator|==
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// search each subreader
name|collector
operator|.
name|setNextReader
argument_list|(
name|subReaders
index|[
name|i
index|]
argument_list|,
name|docStarts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|subReaders
index|[
name|i
index|]
argument_list|,
operator|!
name|collector
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
name|scorer
operator|.
name|score
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// search each subreader
name|collector
operator|.
name|setNextReader
argument_list|(
name|subReaders
index|[
name|i
index|]
argument_list|,
name|docStarts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|searchWithFilter
argument_list|(
name|subReaders
index|[
name|i
index|]
argument_list|,
name|weight
argument_list|,
name|filter
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|searchWithFilter
specifier|private
name|void
name|searchWithFilter
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Weight
name|weight
parameter_list|,
specifier|final
name|Filter
name|filter
parameter_list|,
specifier|final
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|filter
operator|!=
literal|null
assert|;
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|docID
init|=
name|scorer
operator|.
name|docID
argument_list|()
decl_stmt|;
assert|assert
name|docID
operator|==
operator|-
literal|1
operator|||
name|docID
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
assert|;
comment|// CHECKME: use ConjunctionScorer here?
name|DocIdSet
name|filterDocIdSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterDocIdSet
operator|==
literal|null
condition|)
block|{
comment|// this means the filter does not accept any documents.
return|return;
block|}
name|DocIdSetIterator
name|filterIter
init|=
name|filterDocIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|filterIter
operator|==
literal|null
condition|)
block|{
comment|// this means the filter does not accept any documents.
return|return;
block|}
name|int
name|filterDoc
init|=
name|filterIter
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|int
name|scorerDoc
init|=
name|scorer
operator|.
name|advance
argument_list|(
name|filterDoc
argument_list|)
decl_stmt|;
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|scorerDoc
operator|==
name|filterDoc
condition|)
block|{
comment|// Check if scorer has exhausted, only before collecting.
if|if
condition|(
name|scorerDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|collector
operator|.
name|collect
argument_list|(
name|scorerDoc
argument_list|)
expr_stmt|;
name|filterDoc
operator|=
name|filterIter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|filterDoc
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|scorerDoc
operator|>
name|filterDoc
condition|)
block|{
name|filterDoc
operator|=
name|filterIter
operator|.
name|advance
argument_list|(
name|scorerDoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|filterDoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
name|original
decl_stmt|;
for|for
control|(
name|Query
name|rewrittenQuery
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
init|;
name|rewrittenQuery
operator|!=
name|query
condition|;
name|rewrittenQuery
operator|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
control|)
block|{
name|query
operator|=
name|rewrittenQuery
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|n
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|docStarts
argument_list|)
decl_stmt|;
name|int
name|deBasedDoc
init|=
name|doc
operator|-
name|docStarts
index|[
name|n
index|]
decl_stmt|;
return|return
name|weight
operator|.
name|explain
argument_list|(
name|subReaders
index|[
name|n
index|]
argument_list|,
name|deBasedDoc
argument_list|)
return|;
block|}
DECL|field|fieldSortDoTrackScores
specifier|private
name|boolean
name|fieldSortDoTrackScores
decl_stmt|;
DECL|field|fieldSortDoMaxScore
specifier|private
name|boolean
name|fieldSortDoMaxScore
decl_stmt|;
comment|/** By default, no scores are computed when sorting by    *  field (using {@link #search(Query,Filter,int,Sort)}).    *  You can change that, per IndexSearcher instance, by    *  calling this method.  Note that this will incur a CPU    *  cost.    *     *  @param doTrackScores If true, then scores are    *  returned for every matching document in {@link    *  TopFieldDocs}.    *    *  @param doMaxScore If true, then the max score for all    *  matching docs is computed. */
DECL|method|setDefaultFieldSortScoring
specifier|public
name|void
name|setDefaultFieldSortScoring
parameter_list|(
name|boolean
name|doTrackScores
parameter_list|,
name|boolean
name|doMaxScore
parameter_list|)
block|{
name|fieldSortDoTrackScores
operator|=
name|doTrackScores
expr_stmt|;
name|fieldSortDoMaxScore
operator|=
name|doMaxScore
expr_stmt|;
block|}
block|}
end_class
end_unit
