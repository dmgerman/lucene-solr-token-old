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
name|ConcurrentModificationException
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
name|Iterator
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
name|index
operator|.
name|CorruptIndexException
import|;
end_import
begin_comment
comment|/** A ranked list of documents, used to hold search results.  *<p>  *<b>Caution:</b> Iterate only over the hits needed.  Iterating over all  * hits is generally not desirable and may be the source of  * performance issues. If you need to iterate over many or all hits, consider  * using the search method that takes a {@link HitCollector}.  *</p>  *<p><b>Note:</b> Deleting matching documents concurrently with traversing   * the hits, might, when deleting hits that were not yet retrieved, decrease  * {@link #length()}. In such case,   * {@link java.util.ConcurrentModificationException ConcurrentModificationException}  * is thrown when accessing hit<code>n</code>&ge; current_{@link #length()}   * (but<code>n</code>&lt; {@link #length()}_at_start).   *   * @deprecated Hits will be removed in Lucene 3.0.<p>  * Instead e. g. {@link TopDocCollector} and {@link TopDocs} can be used:<br>  *<pre>  *   TopDocCollector collector = new TopDocCollector(hitsPerPage);  *   searcher.search(query, collector);  *   ScoreDoc[] hits = collector.topDocs().scoreDocs;  *   for (int i = 0; i< hits.length; i++) {  *     int docId = hits[i].doc;  *     Document d = searcher.doc(docId);  *     // do something with current hit  *     ...  *</pre>  */
end_comment
begin_class
DECL|class|Hits
specifier|public
specifier|final
class|class
name|Hits
block|{
DECL|field|weight
specifier|private
name|Weight
name|weight
decl_stmt|;
DECL|field|searcher
specifier|private
name|Searcher
name|searcher
decl_stmt|;
DECL|field|filter
specifier|private
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
DECL|field|sort
specifier|private
name|Sort
name|sort
init|=
literal|null
decl_stmt|;
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
comment|// the total number of hits
DECL|field|hitDocs
specifier|private
name|Vector
name|hitDocs
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
comment|// cache of hits retrieved
DECL|field|first
specifier|private
name|HitDoc
name|first
decl_stmt|;
comment|// head of LRU cache
DECL|field|last
specifier|private
name|HitDoc
name|last
decl_stmt|;
comment|// tail of LRU cache
DECL|field|numDocs
specifier|private
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
comment|// number cached
DECL|field|maxDocs
specifier|private
name|int
name|maxDocs
init|=
literal|200
decl_stmt|;
comment|// max to cache
DECL|field|nDeletions
specifier|private
name|int
name|nDeletions
decl_stmt|;
comment|// # deleted docs in the index.
DECL|field|lengthAtStart
specifier|private
name|int
name|lengthAtStart
decl_stmt|;
comment|// this is the number apps usually count on (although deletions can bring it down).
DECL|field|nDeletedHits
specifier|private
name|int
name|nDeletedHits
init|=
literal|0
decl_stmt|;
comment|// # of already collected hits that were meanwhile deleted.
DECL|field|debugCheckedForDeletions
name|boolean
name|debugCheckedForDeletions
init|=
literal|false
decl_stmt|;
comment|// for test purposes.
DECL|method|Hits
name|Hits
parameter_list|(
name|Searcher
name|s
parameter_list|,
name|Query
name|q
parameter_list|,
name|Filter
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|weight
operator|=
name|q
operator|.
name|weight
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|s
expr_stmt|;
name|filter
operator|=
name|f
expr_stmt|;
name|nDeletions
operator|=
name|countDeletions
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|getMoreDocs
argument_list|(
literal|50
argument_list|)
expr_stmt|;
comment|// retrieve 100 initially
name|lengthAtStart
operator|=
name|length
expr_stmt|;
block|}
DECL|method|Hits
name|Hits
parameter_list|(
name|Searcher
name|s
parameter_list|,
name|Query
name|q
parameter_list|,
name|Filter
name|f
parameter_list|,
name|Sort
name|o
parameter_list|)
throws|throws
name|IOException
block|{
name|weight
operator|=
name|q
operator|.
name|weight
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|s
expr_stmt|;
name|filter
operator|=
name|f
expr_stmt|;
name|sort
operator|=
name|o
expr_stmt|;
name|nDeletions
operator|=
name|countDeletions
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|getMoreDocs
argument_list|(
literal|50
argument_list|)
expr_stmt|;
comment|// retrieve 100 initially
name|lengthAtStart
operator|=
name|length
expr_stmt|;
block|}
comment|// count # deletions, return -1 if unknown.
DECL|method|countDeletions
specifier|private
name|int
name|countDeletions
parameter_list|(
name|Searcher
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|cnt
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|s
operator|instanceof
name|IndexSearcher
condition|)
block|{
name|cnt
operator|=
name|s
operator|.
name|maxDoc
argument_list|()
operator|-
operator|(
operator|(
name|IndexSearcher
operator|)
name|s
operator|)
operator|.
name|getIndexReader
argument_list|()
operator|.
name|numDocs
argument_list|()
expr_stmt|;
block|}
return|return
name|cnt
return|;
block|}
comment|/**    * Tries to add new documents to hitDocs.    * Ensures that the hit numbered<code>min</code> has been retrieved.    */
DECL|method|getMoreDocs
specifier|private
specifier|final
name|void
name|getMoreDocs
parameter_list|(
name|int
name|min
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hitDocs
operator|.
name|size
argument_list|()
operator|>
name|min
condition|)
block|{
name|min
operator|=
name|hitDocs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|int
name|n
init|=
name|min
operator|*
literal|2
decl_stmt|;
comment|// double # retrieved
name|TopDocs
name|topDocs
init|=
operator|(
name|sort
operator|==
literal|null
operator|)
condition|?
name|searcher
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|n
argument_list|)
else|:
name|searcher
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|n
argument_list|,
name|sort
argument_list|)
decl_stmt|;
name|length
operator|=
name|topDocs
operator|.
name|totalHits
expr_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|float
name|scoreNorm
init|=
literal|1.0f
decl_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
operator|&&
name|topDocs
operator|.
name|getMaxScore
argument_list|()
operator|>
literal|1.0f
condition|)
block|{
name|scoreNorm
operator|=
literal|1.0f
operator|/
name|topDocs
operator|.
name|getMaxScore
argument_list|()
expr_stmt|;
block|}
name|int
name|start
init|=
name|hitDocs
operator|.
name|size
argument_list|()
operator|-
name|nDeletedHits
decl_stmt|;
comment|// any new deletions?
name|int
name|nDels2
init|=
name|countDeletions
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|debugCheckedForDeletions
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|nDeletions
argument_list|<
literal|0
operator|||
name|nDels2
argument_list|>
name|nDeletions
condition|)
block|{
comment|// either we cannot count deletions, or some "previously valid hits" might have been deleted, so find exact start point
name|nDeletedHits
operator|=
literal|0
expr_stmt|;
name|debugCheckedForDeletions
operator|=
literal|true
expr_stmt|;
name|int
name|i2
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i1
init|=
literal|0
init|;
name|i1
operator|<
name|hitDocs
operator|.
name|size
argument_list|()
operator|&&
name|i2
operator|<
name|scoreDocs
operator|.
name|length
condition|;
name|i1
operator|++
control|)
block|{
name|int
name|id1
init|=
operator|(
operator|(
name|HitDoc
operator|)
name|hitDocs
operator|.
name|get
argument_list|(
name|i1
argument_list|)
operator|)
operator|.
name|id
decl_stmt|;
name|int
name|id2
init|=
name|scoreDocs
index|[
name|i2
index|]
operator|.
name|doc
decl_stmt|;
if|if
condition|(
name|id1
operator|==
name|id2
condition|)
block|{
name|i2
operator|++
expr_stmt|;
block|}
else|else
block|{
name|nDeletedHits
operator|++
expr_stmt|;
block|}
block|}
name|start
operator|=
name|i2
expr_stmt|;
block|}
name|int
name|end
init|=
name|scoreDocs
operator|.
name|length
operator|<
name|length
condition|?
name|scoreDocs
operator|.
name|length
else|:
name|length
decl_stmt|;
name|length
operator|+=
name|nDeletedHits
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|hitDocs
operator|.
name|addElement
argument_list|(
operator|new
name|HitDoc
argument_list|(
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
operator|*
name|scoreNorm
argument_list|,
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|nDeletions
operator|=
name|nDels2
expr_stmt|;
block|}
comment|/** Returns the total number of hits available in this set. */
DECL|method|length
specifier|public
specifier|final
name|int
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/** Returns the stored fields of the n<sup>th</sup> document in this set.    *<p>Documents are cached, so that repeated requests for the same element may    * return the same Document object.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|doc
specifier|public
specifier|final
name|Document
name|doc
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|HitDoc
name|hitDoc
init|=
name|hitDoc
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// Update LRU cache of documents
name|remove
argument_list|(
name|hitDoc
argument_list|)
expr_stmt|;
comment|// remove from list, if there
name|addToFront
argument_list|(
name|hitDoc
argument_list|)
expr_stmt|;
comment|// add to front of list
if|if
condition|(
name|numDocs
operator|>
name|maxDocs
condition|)
block|{
comment|// if cache is full
name|HitDoc
name|oldLast
init|=
name|last
decl_stmt|;
name|remove
argument_list|(
name|last
argument_list|)
expr_stmt|;
comment|// flush last
name|oldLast
operator|.
name|doc
operator|=
literal|null
expr_stmt|;
comment|// let doc get gc'd
block|}
if|if
condition|(
name|hitDoc
operator|.
name|doc
operator|==
literal|null
condition|)
block|{
name|hitDoc
operator|.
name|doc
operator|=
name|searcher
operator|.
name|doc
argument_list|(
name|hitDoc
operator|.
name|id
argument_list|)
expr_stmt|;
comment|// cache miss: read document
block|}
return|return
name|hitDoc
operator|.
name|doc
return|;
block|}
comment|/** Returns the score for the n<sup>th</sup> document in this set. */
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hitDoc
argument_list|(
name|n
argument_list|)
operator|.
name|score
return|;
block|}
comment|/** Returns the id for the n<sup>th</sup> document in this set.    * Note that ids may change when the index changes, so you cannot    * rely on the id to be stable.    */
DECL|method|id
specifier|public
specifier|final
name|int
name|id
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hitDoc
argument_list|(
name|n
argument_list|)
operator|.
name|id
return|;
block|}
comment|/**    * Returns a {@link HitIterator} to navigate the Hits.  Each item returned    * from {@link Iterator#next()} is a {@link Hit}.    *<p>    *<b>Caution:</b> Iterate only over the hits needed.  Iterating over all    * hits is generally not desirable and may be the source of    * performance issues. If you need to iterate over many or all hits, consider    * using a search method that takes a {@link HitCollector}.    *</p>    */
DECL|method|iterator
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|HitIterator
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|hitDoc
specifier|private
specifier|final
name|HitDoc
name|hitDoc
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|n
operator|>=
name|lengthAtStart
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"Not a valid hit number: "
operator|+
name|n
argument_list|)
throw|;
block|}
if|if
condition|(
name|n
operator|>=
name|hitDocs
operator|.
name|size
argument_list|()
condition|)
block|{
name|getMoreDocs
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|>=
name|length
condition|)
block|{
throw|throw
operator|new
name|ConcurrentModificationException
argument_list|(
literal|"Not a valid hit number: "
operator|+
name|n
argument_list|)
throw|;
block|}
return|return
operator|(
name|HitDoc
operator|)
name|hitDocs
operator|.
name|elementAt
argument_list|(
name|n
argument_list|)
return|;
block|}
DECL|method|addToFront
specifier|private
specifier|final
name|void
name|addToFront
parameter_list|(
name|HitDoc
name|hitDoc
parameter_list|)
block|{
comment|// insert at front of cache
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
name|last
operator|=
name|hitDoc
expr_stmt|;
block|}
else|else
block|{
name|first
operator|.
name|prev
operator|=
name|hitDoc
expr_stmt|;
block|}
name|hitDoc
operator|.
name|next
operator|=
name|first
expr_stmt|;
name|first
operator|=
name|hitDoc
expr_stmt|;
name|hitDoc
operator|.
name|prev
operator|=
literal|null
expr_stmt|;
name|numDocs
operator|++
expr_stmt|;
block|}
DECL|method|remove
specifier|private
specifier|final
name|void
name|remove
parameter_list|(
name|HitDoc
name|hitDoc
parameter_list|)
block|{
comment|// remove from cache
if|if
condition|(
name|hitDoc
operator|.
name|doc
operator|==
literal|null
condition|)
block|{
comment|// it's not in the list
return|return;
comment|// abort
block|}
if|if
condition|(
name|hitDoc
operator|.
name|next
operator|==
literal|null
condition|)
block|{
name|last
operator|=
name|hitDoc
operator|.
name|prev
expr_stmt|;
block|}
else|else
block|{
name|hitDoc
operator|.
name|next
operator|.
name|prev
operator|=
name|hitDoc
operator|.
name|prev
expr_stmt|;
block|}
if|if
condition|(
name|hitDoc
operator|.
name|prev
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|hitDoc
operator|.
name|next
expr_stmt|;
block|}
else|else
block|{
name|hitDoc
operator|.
name|prev
operator|.
name|next
operator|=
name|hitDoc
operator|.
name|next
expr_stmt|;
block|}
name|numDocs
operator|--
expr_stmt|;
block|}
block|}
end_class
begin_class
DECL|class|HitDoc
specifier|final
class|class
name|HitDoc
block|{
DECL|field|score
name|float
name|score
decl_stmt|;
DECL|field|id
name|int
name|id
decl_stmt|;
DECL|field|doc
name|Document
name|doc
init|=
literal|null
decl_stmt|;
DECL|field|next
name|HitDoc
name|next
decl_stmt|;
comment|// in doubly-linked cache
DECL|field|prev
name|HitDoc
name|prev
decl_stmt|;
comment|// in doubly-linked cache
DECL|method|HitDoc
name|HitDoc
parameter_list|(
name|float
name|s
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|score
operator|=
name|s
expr_stmt|;
name|id
operator|=
name|i
expr_stmt|;
block|}
block|}
end_class
end_unit
