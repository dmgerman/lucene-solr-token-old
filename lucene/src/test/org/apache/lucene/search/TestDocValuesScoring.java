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
name|DocValuesField
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
name|AtomicReaderContext
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
name|DocValues
operator|.
name|Source
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
name|DocValues
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
name|FieldInvertState
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
name|Norm
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
name|RandomIndexWriter
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
name|search
operator|.
name|similarities
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
name|search
operator|.
name|similarities
operator|.
name|SimilarityProvider
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
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**  * Tests the use of indexdocvalues in scoring.  *   * In the example, a docvalues field is used as a per-document boost (separate from the norm)  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TestDocValuesScoring
specifier|public
class|class
name|TestDocValuesScoring
extends|extends
name|LuceneTestCase
block|{
DECL|field|SCORE_EPSILON
specifier|private
specifier|static
specifier|final
name|float
name|SCORE_EPSILON
init|=
literal|0.001f
decl_stmt|;
comment|/* for comparing floats */
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"PreFlex codec cannot work with DocValues!"
argument_list|,
literal|"Lucene3x"
operator|.
name|equals
argument_list|(
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
name|newField
argument_list|(
literal|"foo"
argument_list|,
literal|""
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|DocValuesField
name|dvField
init|=
operator|new
name|DocValuesField
argument_list|(
literal|"foo_boost"
argument_list|,
literal|0.0f
argument_list|,
name|DocValues
operator|.
name|Type
operator|.
name|FLOAT_32
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dvField
argument_list|)
expr_stmt|;
name|Field
name|field2
init|=
name|newField
argument_list|(
literal|"bar"
argument_list|,
literal|""
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field2
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"quick brown fox"
argument_list|)
expr_stmt|;
name|field2
operator|.
name|setValue
argument_list|(
literal|"quick brown fox"
argument_list|)
expr_stmt|;
name|dvField
operator|.
name|setValue
argument_list|(
literal|2f
argument_list|)
expr_stmt|;
comment|// boost x2
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"jumps over lazy brown dog"
argument_list|)
expr_stmt|;
name|field2
operator|.
name|setValue
argument_list|(
literal|"jumps over lazy brown dog"
argument_list|)
expr_stmt|;
name|dvField
operator|.
name|setValue
argument_list|(
literal|4f
argument_list|)
expr_stmt|;
comment|// boost x4
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// no boosting
name|IndexSearcher
name|searcher1
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
specifier|final
name|SimilarityProvider
name|base
init|=
name|searcher1
operator|.
name|getSimilarityProvider
argument_list|()
decl_stmt|;
comment|// boosting
name|IndexSearcher
name|searcher2
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|searcher2
operator|.
name|setSimilarityProvider
argument_list|(
operator|new
name|SimilarityProvider
argument_list|()
block|{
specifier|final
name|Similarity
name|fooSim
init|=
operator|new
name|BoostingSimilarity
argument_list|(
name|base
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"foo_boost"
argument_list|)
decl_stmt|;
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"foo"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|?
name|fooSim
else|:
name|base
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
name|base
operator|.
name|coord
argument_list|(
name|overlap
argument_list|,
name|maxOverlap
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
name|base
operator|.
name|queryNorm
argument_list|(
name|sumOfSquaredWeights
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// in this case, we searched on field "foo". first document should have 2x the score.
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"quick"
argument_list|)
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|tq
argument_list|,
name|searcher1
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|tq
argument_list|,
name|searcher2
argument_list|)
expr_stmt|;
name|TopDocs
name|noboost
init|=
name|searcher1
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|TopDocs
name|boost
init|=
name|searcher2
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|noboost
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|boost
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//System.out.println(searcher2.explain(tq, boost.scoreDocs[0].doc));
name|assertEquals
argument_list|(
name|boost
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
argument_list|,
name|noboost
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
operator|*
literal|2f
argument_list|,
name|SCORE_EPSILON
argument_list|)
expr_stmt|;
comment|// this query matches only the second document, which should have 4x the score.
name|tq
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"jumps"
argument_list|)
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|tq
argument_list|,
name|searcher1
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|tq
argument_list|,
name|searcher2
argument_list|)
expr_stmt|;
name|noboost
operator|=
name|searcher1
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|boost
operator|=
name|searcher2
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|noboost
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|boost
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|boost
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
argument_list|,
name|noboost
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
operator|*
literal|4f
argument_list|,
name|SCORE_EPSILON
argument_list|)
expr_stmt|;
comment|// search on on field bar just for kicks, nothing should happen, since we setup
comment|// our sim provider to only use foo_boost for field foo.
name|tq
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"bar"
argument_list|,
literal|"quick"
argument_list|)
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|tq
argument_list|,
name|searcher1
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|tq
argument_list|,
name|searcher2
argument_list|)
expr_stmt|;
name|noboost
operator|=
name|searcher1
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|boost
operator|=
name|searcher2
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|noboost
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|boost
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|boost
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
argument_list|,
name|noboost
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
argument_list|,
name|SCORE_EPSILON
argument_list|)
expr_stmt|;
name|ir
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
comment|/**    * Similarity that wraps another similarity and boosts the final score    * according to whats in a docvalues field.    *     * @lucene.experimental    */
DECL|class|BoostingSimilarity
specifier|static
class|class
name|BoostingSimilarity
extends|extends
name|Similarity
block|{
DECL|field|sim
specifier|private
specifier|final
name|Similarity
name|sim
decl_stmt|;
DECL|field|boostField
specifier|private
specifier|final
name|String
name|boostField
decl_stmt|;
DECL|method|BoostingSimilarity
specifier|public
name|BoostingSimilarity
parameter_list|(
name|Similarity
name|sim
parameter_list|,
name|String
name|boostField
parameter_list|)
block|{
name|this
operator|.
name|sim
operator|=
name|sim
expr_stmt|;
name|this
operator|.
name|boostField
operator|=
name|boostField
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|void
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|,
name|Norm
name|norm
parameter_list|)
block|{
name|sim
operator|.
name|computeNorm
argument_list|(
name|state
argument_list|,
name|norm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeStats
specifier|public
name|Stats
name|computeStats
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|float
name|queryBoost
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
block|{
return|return
name|sim
operator|.
name|computeStats
argument_list|(
name|collectionStats
argument_list|,
name|queryBoost
argument_list|,
name|termStats
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|exactDocScorer
specifier|public
name|ExactDocScorer
name|exactDocScorer
parameter_list|(
name|Stats
name|stats
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ExactDocScorer
name|sub
init|=
name|sim
operator|.
name|exactDocScorer
argument_list|(
name|stats
argument_list|,
name|fieldName
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|Source
name|values
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|docValues
argument_list|(
name|boostField
argument_list|)
operator|.
name|getSource
argument_list|()
decl_stmt|;
return|return
operator|new
name|ExactDocScorer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|values
operator|.
name|getFloat
argument_list|(
name|doc
argument_list|)
operator|*
name|sub
operator|.
name|score
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|,
name|Explanation
name|freq
parameter_list|)
block|{
name|Explanation
name|boostExplanation
init|=
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|values
operator|.
name|getFloat
argument_list|(
name|doc
argument_list|)
argument_list|,
literal|"indexDocValue("
operator|+
name|boostField
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|Explanation
name|simExplanation
init|=
name|sub
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|)
decl_stmt|;
name|Explanation
name|expl
init|=
operator|new
name|Explanation
argument_list|(
name|boostExplanation
operator|.
name|getValue
argument_list|()
operator|*
name|simExplanation
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"product of:"
argument_list|)
decl_stmt|;
name|expl
operator|.
name|addDetail
argument_list|(
name|boostExplanation
argument_list|)
expr_stmt|;
name|expl
operator|.
name|addDetail
argument_list|(
name|simExplanation
argument_list|)
expr_stmt|;
return|return
name|expl
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|sloppyDocScorer
specifier|public
name|SloppyDocScorer
name|sloppyDocScorer
parameter_list|(
name|Stats
name|stats
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SloppyDocScorer
name|sub
init|=
name|sim
operator|.
name|sloppyDocScorer
argument_list|(
name|stats
argument_list|,
name|fieldName
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|Source
name|values
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|docValues
argument_list|(
name|boostField
argument_list|)
operator|.
name|getSource
argument_list|()
decl_stmt|;
return|return
operator|new
name|SloppyDocScorer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|values
operator|.
name|getFloat
argument_list|(
name|doc
argument_list|)
operator|*
name|sub
operator|.
name|score
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|computeSlopFactor
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
name|sub
operator|.
name|computeSlopFactor
argument_list|(
name|distance
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|computePayloadFactor
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
return|return
name|sub
operator|.
name|computePayloadFactor
argument_list|(
name|doc
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|payload
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|,
name|Explanation
name|freq
parameter_list|)
block|{
name|Explanation
name|boostExplanation
init|=
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|values
operator|.
name|getFloat
argument_list|(
name|doc
argument_list|)
argument_list|,
literal|"indexDocValue("
operator|+
name|boostField
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|Explanation
name|simExplanation
init|=
name|sub
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|)
decl_stmt|;
name|Explanation
name|expl
init|=
operator|new
name|Explanation
argument_list|(
name|boostExplanation
operator|.
name|getValue
argument_list|()
operator|*
name|simExplanation
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"product of:"
argument_list|)
decl_stmt|;
name|expl
operator|.
name|addDetail
argument_list|(
name|boostExplanation
argument_list|)
expr_stmt|;
name|expl
operator|.
name|addDetail
argument_list|(
name|simExplanation
argument_list|)
expr_stmt|;
return|return
name|expl
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class
end_unit
