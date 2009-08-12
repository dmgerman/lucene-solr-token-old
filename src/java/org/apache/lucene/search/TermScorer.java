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
begin_comment
comment|/** Expert: A<code>Scorer</code> for documents matching a<code>Term</code>.  */
end_comment
begin_class
DECL|class|TermScorer
specifier|final
class|class
name|TermScorer
extends|extends
name|Scorer
block|{
DECL|field|SIM_NORM_DECODER
specifier|private
specifier|static
specifier|final
name|float
index|[]
name|SIM_NORM_DECODER
init|=
name|Similarity
operator|.
name|getNormDecoder
argument_list|()
decl_stmt|;
DECL|field|weight
specifier|private
name|Weight
name|weight
decl_stmt|;
DECL|field|termDocs
specifier|private
name|TermDocs
name|termDocs
decl_stmt|;
DECL|field|norms
specifier|private
name|byte
index|[]
name|norms
decl_stmt|;
DECL|field|weightValue
specifier|private
name|float
name|weightValue
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|docs
specifier|private
specifier|final
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
literal|32
index|]
decl_stmt|;
comment|// buffered doc numbers
DECL|field|freqs
specifier|private
specifier|final
name|int
index|[]
name|freqs
init|=
operator|new
name|int
index|[
literal|32
index|]
decl_stmt|;
comment|// buffered term freqs
DECL|field|pointer
specifier|private
name|int
name|pointer
decl_stmt|;
DECL|field|pointerMax
specifier|private
name|int
name|pointerMax
decl_stmt|;
DECL|field|SCORE_CACHE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SCORE_CACHE_SIZE
init|=
literal|32
decl_stmt|;
DECL|field|scoreCache
specifier|private
name|float
index|[]
name|scoreCache
init|=
operator|new
name|float
index|[
name|SCORE_CACHE_SIZE
index|]
decl_stmt|;
comment|/**    * Construct a<code>TermScorer</code>.    *     * @param weight    *          The weight of the<code>Term</code> in the query.    * @param td    *          An iterator over the documents matching the<code>Term</code>.    * @param similarity    *          The</code>Similarity</code> implementation to be used for score    *          computations.    * @param norms    *          The field norms of the document fields for the<code>Term</code>.    */
DECL|method|TermScorer
name|TermScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|TermDocs
name|td
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|byte
index|[]
name|norms
parameter_list|)
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|termDocs
operator|=
name|td
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
name|this
operator|.
name|weightValue
operator|=
name|weight
operator|.
name|getValue
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
name|SCORE_CACHE_SIZE
condition|;
name|i
operator|++
control|)
name|scoreCache
index|[
name|i
index|]
operator|=
name|getSimilarity
argument_list|()
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|*
name|weightValue
expr_stmt|;
block|}
comment|/** @deprecated use {@link #score(Collector)} instead. */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|HitCollector
name|hc
parameter_list|)
throws|throws
name|IOException
block|{
name|score
argument_list|(
operator|new
name|HitCollectorWrapper
argument_list|(
name|hc
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|Collector
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|score
argument_list|(
name|c
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** @deprecated use {@link #score(Collector, int)} instead. */
DECL|method|score
specifier|protected
name|boolean
name|score
parameter_list|(
name|HitCollector
name|c
parameter_list|,
name|int
name|end
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|score
argument_list|(
operator|new
name|HitCollectorWrapper
argument_list|(
name|c
argument_list|)
argument_list|,
name|end
argument_list|,
name|doc
argument_list|)
return|;
block|}
comment|// firstDocID is ignored since nextDoc() sets 'doc'
DECL|method|score
specifier|protected
name|boolean
name|score
parameter_list|(
name|Collector
name|c
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|firstDocID
parameter_list|)
throws|throws
name|IOException
block|{
name|c
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
name|doc
operator|<
name|end
condition|)
block|{
comment|// for docs in window
name|c
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// collect score
if|if
condition|(
operator|++
name|pointer
operator|>=
name|pointerMax
condition|)
block|{
name|pointerMax
operator|=
name|termDocs
operator|.
name|read
argument_list|(
name|docs
argument_list|,
name|freqs
argument_list|)
expr_stmt|;
comment|// refill buffers
if|if
condition|(
name|pointerMax
operator|!=
literal|0
condition|)
block|{
name|pointer
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close stream
name|doc
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// set to sentinel value
return|return
literal|false
return|;
block|}
block|}
name|doc
operator|=
name|docs
index|[
name|pointer
index|]
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/** @deprecated use {@link #docID()} instead. */
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
comment|/**    * Advances to the next document matching the query.<br>    * The iterator over the matching documents is buffered using    * {@link TermDocs#read(int[],int[])}.    *     * @return true iff there is another document matching the query.    * @deprecated use {@link #nextDoc()} instead.    */
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|nextDoc
argument_list|()
operator|!=
name|NO_MORE_DOCS
return|;
block|}
comment|/**    * Advances to the next document matching the query.<br>    * The iterator over the matching documents is buffered using    * {@link TermDocs#read(int[],int[])}.    *     * @return the document matching the query or -1 if there are no more documents.    */
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|pointer
operator|++
expr_stmt|;
if|if
condition|(
name|pointer
operator|>=
name|pointerMax
condition|)
block|{
name|pointerMax
operator|=
name|termDocs
operator|.
name|read
argument_list|(
name|docs
argument_list|,
name|freqs
argument_list|)
expr_stmt|;
comment|// refill buffer
if|if
condition|(
name|pointerMax
operator|!=
literal|0
condition|)
block|{
name|pointer
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close stream
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
name|doc
operator|=
name|docs
index|[
name|pointer
index|]
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
block|{
assert|assert
name|doc
operator|!=
operator|-
literal|1
assert|;
name|int
name|f
init|=
name|freqs
index|[
name|pointer
index|]
decl_stmt|;
name|float
name|raw
init|=
comment|// compute tf(f)*weight
name|f
operator|<
name|SCORE_CACHE_SIZE
comment|// check cache
condition|?
name|scoreCache
index|[
name|f
index|]
comment|// cache hit
else|:
name|getSimilarity
argument_list|()
operator|.
name|tf
argument_list|(
name|f
argument_list|)
operator|*
name|weightValue
decl_stmt|;
comment|// cache miss
return|return
name|norms
operator|==
literal|null
condition|?
name|raw
else|:
name|raw
operator|*
name|SIM_NORM_DECODER
index|[
name|norms
index|[
name|doc
index|]
operator|&
literal|0xFF
index|]
return|;
comment|// normalize for field
block|}
comment|/**    * Skips to the first match beyond the current whose document number is    * greater than or equal to a given target.<br>    * The implementation uses {@link TermDocs#skipTo(int)}.    *     * @param target    *          The target document number.    * @return true iff there is such a match.    * @deprecated use {@link #advance(int)} instead.    */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|advance
argument_list|(
name|target
argument_list|)
operator|!=
name|NO_MORE_DOCS
return|;
block|}
comment|/**    * Advances to the first match beyond the current whose document number is    * greater than or equal to a given target.<br>    * The implementation uses {@link TermDocs#skipTo(int)}.    *     * @param target    *          The target document number.    * @return the matching document or -1 if none exist.    */
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|// first scan in cache
for|for
control|(
name|pointer
operator|++
init|;
name|pointer
operator|<
name|pointerMax
condition|;
name|pointer
operator|++
control|)
block|{
if|if
condition|(
name|docs
index|[
name|pointer
index|]
operator|>=
name|target
condition|)
block|{
return|return
name|doc
operator|=
name|docs
index|[
name|pointer
index|]
return|;
block|}
block|}
comment|// not found in cache, seek underlying stream
name|boolean
name|result
init|=
name|termDocs
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|pointerMax
operator|=
literal|1
expr_stmt|;
name|pointer
operator|=
literal|0
expr_stmt|;
name|docs
index|[
name|pointer
index|]
operator|=
name|doc
operator|=
name|termDocs
operator|.
name|doc
argument_list|()
expr_stmt|;
name|freqs
index|[
name|pointer
index|]
operator|=
name|termDocs
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
comment|/** Returns an explanation of the score for a document.    *<br>When this method is used, the {@link #next()} method    * and the {@link #score(HitCollector)} method should not be used.    * @param doc The document number for the explanation.    */
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|TermQuery
name|query
init|=
operator|(
name|TermQuery
operator|)
name|weight
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|Explanation
name|tfExplanation
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|int
name|tf
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pointer
operator|<
name|pointerMax
condition|)
block|{
if|if
condition|(
name|docs
index|[
name|pointer
index|]
operator|==
name|doc
condition|)
name|tf
operator|=
name|freqs
index|[
name|pointer
index|]
expr_stmt|;
name|pointer
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|tf
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|termDocs
operator|.
name|skipTo
argument_list|(
name|doc
argument_list|)
condition|)
block|{
if|if
condition|(
name|termDocs
operator|.
name|doc
argument_list|()
operator|==
name|doc
condition|)
block|{
name|tf
operator|=
name|termDocs
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|tfExplanation
operator|.
name|setValue
argument_list|(
name|getSimilarity
argument_list|()
operator|.
name|tf
argument_list|(
name|tf
argument_list|)
argument_list|)
expr_stmt|;
name|tfExplanation
operator|.
name|setDescription
argument_list|(
literal|"tf(termFreq("
operator|+
name|query
operator|.
name|getTerm
argument_list|()
operator|+
literal|")="
operator|+
name|tf
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|tfExplanation
return|;
block|}
comment|/** Returns a string representation of this<code>TermScorer</code>. */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"scorer("
operator|+
name|weight
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
