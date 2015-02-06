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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|PostingsEnum
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
name|LeafReaderContext
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
begin_comment
comment|/** A {@link Rescorer} that uses a provided Query to assign  *  scores to the first-pass hits.  *  * @lucene.experimental */
end_comment
begin_class
DECL|class|QueryRescorer
specifier|public
specifier|abstract
class|class
name|QueryRescorer
extends|extends
name|Rescorer
block|{
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
comment|/** Sole constructor, passing the 2nd pass query to    *  assign scores to the 1st pass hits.  */
DECL|method|QueryRescorer
specifier|public
name|QueryRescorer
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
comment|/**    * Implement this in a subclass to combine the first pass and    * second pass scores.  If secondPassMatches is false then    * the second pass query failed to match a hit from the    * first pass query, and you should ignore the    * secondPassScore.    */
DECL|method|combine
specifier|protected
specifier|abstract
name|float
name|combine
parameter_list|(
name|float
name|firstPassScore
parameter_list|,
name|boolean
name|secondPassMatches
parameter_list|,
name|float
name|secondPassScore
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|rescore
specifier|public
name|TopDocs
name|rescore
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|TopDocs
name|firstPassTopDocs
parameter_list|,
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
name|ScoreDoc
index|[]
name|hits
init|=
name|firstPassTopDocs
operator|.
name|scoreDocs
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|hits
argument_list|,
operator|new
name|Comparator
argument_list|<
name|ScoreDoc
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|ScoreDoc
name|a
parameter_list|,
name|ScoreDoc
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|doc
operator|-
name|b
operator|.
name|doc
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|Weight
name|weight
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|// Now merge sort docIDs from hits, with reader's leaves:
name|int
name|hitUpto
init|=
literal|0
decl_stmt|;
name|int
name|readerUpto
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|endDoc
init|=
literal|0
decl_stmt|;
name|int
name|docBase
init|=
literal|0
decl_stmt|;
name|Scorer
name|scorer
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|hitUpto
operator|<
name|hits
operator|.
name|length
condition|)
block|{
name|ScoreDoc
name|hit
init|=
name|hits
index|[
name|hitUpto
index|]
decl_stmt|;
name|int
name|docID
init|=
name|hit
operator|.
name|doc
decl_stmt|;
name|LeafReaderContext
name|readerContext
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|docID
operator|>=
name|endDoc
condition|)
block|{
name|readerUpto
operator|++
expr_stmt|;
name|readerContext
operator|=
name|leaves
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|endDoc
operator|=
name|readerContext
operator|.
name|docBase
operator|+
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|readerContext
operator|!=
literal|null
condition|)
block|{
comment|// We advanced to another segment:
name|docBase
operator|=
name|readerContext
operator|.
name|docBase
expr_stmt|;
name|scorer
operator|=
name|weight
operator|.
name|scorer
argument_list|(
name|readerContext
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
name|int
name|targetDoc
init|=
name|docID
operator|-
name|docBase
decl_stmt|;
name|int
name|actualDoc
init|=
name|scorer
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|actualDoc
operator|<
name|targetDoc
condition|)
block|{
name|actualDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|targetDoc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|actualDoc
operator|==
name|targetDoc
condition|)
block|{
comment|// Query did match this doc:
name|hit
operator|.
name|score
operator|=
name|combine
argument_list|(
name|hit
operator|.
name|score
argument_list|,
literal|true
argument_list|,
name|scorer
operator|.
name|score
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Query did not match this doc:
assert|assert
name|actualDoc
operator|>
name|targetDoc
assert|;
name|hit
operator|.
name|score
operator|=
name|combine
argument_list|(
name|hit
operator|.
name|score
argument_list|,
literal|false
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Query did not match this doc:
name|hit
operator|.
name|score
operator|=
name|combine
argument_list|(
name|hit
operator|.
name|score
argument_list|,
literal|false
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|hitUpto
operator|++
expr_stmt|;
block|}
comment|// TODO: we should do a partial sort (of only topN)
comment|// instead, but typically the number of hits is
comment|// smallish:
name|Arrays
operator|.
name|sort
argument_list|(
name|hits
argument_list|,
operator|new
name|Comparator
argument_list|<
name|ScoreDoc
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|ScoreDoc
name|a
parameter_list|,
name|ScoreDoc
name|b
parameter_list|)
block|{
comment|// Sort by score descending, then docID ascending:
if|if
condition|(
name|a
operator|.
name|score
operator|>
name|b
operator|.
name|score
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|.
name|score
operator|<
name|b
operator|.
name|score
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
comment|// This subtraction can't overflow int
comment|// because docIDs are>= 0:
return|return
name|a
operator|.
name|doc
operator|-
name|b
operator|.
name|doc
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|topN
operator|<
name|hits
operator|.
name|length
condition|)
block|{
name|ScoreDoc
index|[]
name|subset
init|=
operator|new
name|ScoreDoc
index|[
name|topN
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|hits
argument_list|,
literal|0
argument_list|,
name|subset
argument_list|,
literal|0
argument_list|,
name|topN
argument_list|)
expr_stmt|;
name|hits
operator|=
name|subset
expr_stmt|;
block|}
return|return
operator|new
name|TopDocs
argument_list|(
name|firstPassTopDocs
operator|.
name|totalHits
argument_list|,
name|hits
argument_list|,
name|hits
index|[
literal|0
index|]
operator|.
name|score
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Explanation
name|firstPassExplanation
parameter_list|,
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|secondPassExplanation
init|=
name|searcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|docID
argument_list|)
decl_stmt|;
name|Float
name|secondPassScore
init|=
name|secondPassExplanation
operator|.
name|isMatch
argument_list|()
condition|?
name|secondPassExplanation
operator|.
name|getValue
argument_list|()
else|:
literal|null
decl_stmt|;
name|float
name|score
decl_stmt|;
if|if
condition|(
name|secondPassScore
operator|==
literal|null
condition|)
block|{
name|score
operator|=
name|combine
argument_list|(
name|firstPassExplanation
operator|.
name|getValue
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|score
operator|=
name|combine
argument_list|(
name|firstPassExplanation
operator|.
name|getValue
argument_list|()
argument_list|,
literal|true
argument_list|,
name|secondPassScore
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|(
name|score
argument_list|,
literal|"combined first and second pass score using "
operator|+
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|Explanation
name|first
init|=
operator|new
name|Explanation
argument_list|(
name|firstPassExplanation
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"first pass score"
argument_list|)
decl_stmt|;
name|first
operator|.
name|addDetail
argument_list|(
name|firstPassExplanation
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|first
argument_list|)
expr_stmt|;
name|Explanation
name|second
decl_stmt|;
if|if
condition|(
name|secondPassScore
operator|==
literal|null
condition|)
block|{
name|second
operator|=
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"no second pass score"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|second
operator|=
operator|new
name|Explanation
argument_list|(
name|secondPassScore
argument_list|,
literal|"second pass score"
argument_list|)
expr_stmt|;
block|}
name|second
operator|.
name|addDetail
argument_list|(
name|secondPassExplanation
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|second
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Sugar API, calling {#rescore} using a simple linear    *  combination of firstPassScore + weight * secondPassScore */
DECL|method|rescore
specifier|public
specifier|static
name|TopDocs
name|rescore
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|TopDocs
name|topDocs
parameter_list|,
name|Query
name|query
parameter_list|,
specifier|final
name|double
name|weight
parameter_list|,
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|QueryRescorer
argument_list|(
name|query
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|float
name|combine
parameter_list|(
name|float
name|firstPassScore
parameter_list|,
name|boolean
name|secondPassMatches
parameter_list|,
name|float
name|secondPassScore
parameter_list|)
block|{
name|float
name|score
init|=
name|firstPassScore
decl_stmt|;
if|if
condition|(
name|secondPassMatches
condition|)
block|{
name|score
operator|+=
name|weight
operator|*
name|secondPassScore
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
block|}
operator|.
name|rescore
argument_list|(
name|searcher
argument_list|,
name|topDocs
argument_list|,
name|topN
argument_list|)
return|;
block|}
block|}
end_class
end_unit
