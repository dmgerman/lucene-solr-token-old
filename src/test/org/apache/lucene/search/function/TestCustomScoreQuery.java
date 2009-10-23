begin_unit
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
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
name|HashMap
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
name|queryParser
operator|.
name|QueryParser
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
name|Explanation
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
name|search
operator|.
name|QueryUtils
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
name|TopDocs
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
name|Version
import|;
end_import
begin_comment
comment|/**  * Test CustomScoreQuery search.  */
end_comment
begin_class
DECL|class|TestCustomScoreQuery
specifier|public
class|class
name|TestCustomScoreQuery
extends|extends
name|FunctionTestSetup
block|{
comment|/* @override constructor */
DECL|method|TestCustomScoreQuery
specifier|public
name|TestCustomScoreQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** Test that CustomScoreQuery of Type.BYTE returns the expected scores. */
DECL|method|testCustomScoreByte
specifier|public
name|void
name|testCustomScoreByte
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field values are small enough to be parsed as byte
name|doTestCustomScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|BYTE
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|doTestCustomScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|BYTE
argument_list|,
literal|2.0
argument_list|)
expr_stmt|;
block|}
comment|/** Test that CustomScoreQuery of Type.SHORT returns the expected scores. */
DECL|method|testCustomScoreShort
specifier|public
name|void
name|testCustomScoreShort
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field values are small enough to be parsed as short
name|doTestCustomScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|SHORT
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|doTestCustomScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|SHORT
argument_list|,
literal|3.0
argument_list|)
expr_stmt|;
block|}
comment|/** Test that CustomScoreQuery of Type.INT returns the expected scores. */
DECL|method|testCustomScoreInt
specifier|public
name|void
name|testCustomScoreInt
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|doTestCustomScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|INT
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|doTestCustomScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|INT
argument_list|,
literal|4.0
argument_list|)
expr_stmt|;
block|}
comment|/** Test that CustomScoreQuery of Type.FLOAT returns the expected scores. */
DECL|method|testCustomScoreFloat
specifier|public
name|void
name|testCustomScoreFloat
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
comment|// INT field can be parsed as float
name|doTestCustomScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|doTestCustomScore
argument_list|(
name|INT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
literal|5.0
argument_list|)
expr_stmt|;
comment|// same values, but in flot format
name|doTestCustomScore
argument_list|(
name|FLOAT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|doTestCustomScore
argument_list|(
name|FLOAT_FIELD
argument_list|,
name|FieldScoreQuery
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
literal|6.0
argument_list|)
expr_stmt|;
block|}
comment|// must have static class otherwise serialization tests fail
DECL|class|CustomAddQuery
specifier|private
specifier|static
class|class
name|CustomAddQuery
extends|extends
name|CustomScoreQuery
block|{
comment|// constructor
DECL|method|CustomAddQuery
name|CustomAddQuery
parameter_list|(
name|Query
name|q
parameter_list|,
name|ValueSourceQuery
name|qValSrc
parameter_list|)
block|{
name|super
argument_list|(
name|q
argument_list|,
name|qValSrc
argument_list|)
expr_stmt|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.CustomScoreQuery#name() */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"customAdd"
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.CustomScoreQuery#customScore(int, float, float) */
DECL|method|customScore
specifier|public
name|float
name|customScore
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|subQueryScore
parameter_list|,
name|float
name|valSrcScore
parameter_list|)
block|{
return|return
name|subQueryScore
operator|+
name|valSrcScore
return|;
block|}
comment|/* (non-Javadoc)@see org.apache.lucene.search.function.CustomScoreQuery#customExplain(int, org.apache.lucene.search.Explanation, org.apache.lucene.search.Explanation)*/
DECL|method|customExplain
specifier|public
name|Explanation
name|customExplain
parameter_list|(
name|int
name|doc
parameter_list|,
name|Explanation
name|subQueryExpl
parameter_list|,
name|Explanation
name|valSrcExpl
parameter_list|)
block|{
name|float
name|valSrcScore
init|=
name|valSrcExpl
operator|==
literal|null
condition|?
literal|0
else|:
name|valSrcExpl
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Explanation
name|exp
init|=
operator|new
name|Explanation
argument_list|(
name|valSrcScore
operator|+
name|subQueryExpl
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"custom score: sum of:"
argument_list|)
decl_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|subQueryExpl
argument_list|)
expr_stmt|;
if|if
condition|(
name|valSrcExpl
operator|!=
literal|null
condition|)
block|{
name|exp
operator|.
name|addDetail
argument_list|(
name|valSrcExpl
argument_list|)
expr_stmt|;
block|}
return|return
name|exp
return|;
block|}
block|}
comment|// must have static class otherwise serialization tests fail
DECL|class|CustomMulAddQuery
specifier|private
specifier|static
class|class
name|CustomMulAddQuery
extends|extends
name|CustomScoreQuery
block|{
comment|// constructor
DECL|method|CustomMulAddQuery
name|CustomMulAddQuery
parameter_list|(
name|Query
name|q
parameter_list|,
name|ValueSourceQuery
name|qValSrc1
parameter_list|,
name|ValueSourceQuery
name|qValSrc2
parameter_list|)
block|{
name|super
argument_list|(
name|q
argument_list|,
operator|new
name|ValueSourceQuery
index|[]
block|{
name|qValSrc1
block|,
name|qValSrc2
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.CustomScoreQuery#name() */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"customMulAdd"
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.CustomScoreQuery#customScore(int, float, float) */
DECL|method|customScore
specifier|public
name|float
name|customScore
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|subQueryScore
parameter_list|,
name|float
name|valSrcScores
index|[]
parameter_list|)
block|{
if|if
condition|(
name|valSrcScores
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|subQueryScore
return|;
block|}
if|if
condition|(
name|valSrcScores
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|subQueryScore
operator|+
name|valSrcScores
index|[
literal|0
index|]
return|;
block|}
return|return
operator|(
name|subQueryScore
operator|+
name|valSrcScores
index|[
literal|0
index|]
operator|)
operator|*
name|valSrcScores
index|[
literal|1
index|]
return|;
comment|// we know there are two
block|}
comment|/* (non-Javadoc)@see org.apache.lucene.search.function.CustomScoreQuery#customExplain(int, org.apache.lucene.search.Explanation, org.apache.lucene.search.Explanation)*/
DECL|method|customExplain
specifier|public
name|Explanation
name|customExplain
parameter_list|(
name|int
name|doc
parameter_list|,
name|Explanation
name|subQueryExpl
parameter_list|,
name|Explanation
name|valSrcExpls
index|[]
parameter_list|)
block|{
if|if
condition|(
name|valSrcExpls
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|subQueryExpl
return|;
block|}
name|Explanation
name|exp
init|=
operator|new
name|Explanation
argument_list|(
name|valSrcExpls
index|[
literal|0
index|]
operator|.
name|getValue
argument_list|()
operator|+
name|subQueryExpl
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"sum of:"
argument_list|)
decl_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|subQueryExpl
argument_list|)
expr_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|valSrcExpls
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|valSrcExpls
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|exp
operator|.
name|setDescription
argument_list|(
literal|"CustomMulAdd, sum of:"
argument_list|)
expr_stmt|;
return|return
name|exp
return|;
block|}
name|Explanation
name|exp2
init|=
operator|new
name|Explanation
argument_list|(
name|valSrcExpls
index|[
literal|1
index|]
operator|.
name|getValue
argument_list|()
operator|*
name|exp
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"custom score: product of:"
argument_list|)
decl_stmt|;
name|exp2
operator|.
name|addDetail
argument_list|(
name|valSrcExpls
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|exp2
operator|.
name|addDetail
argument_list|(
name|exp
argument_list|)
expr_stmt|;
return|return
name|exp2
return|;
block|}
block|}
comment|// Test that FieldScoreQuery returns docs with expected score.
DECL|method|doTestCustomScore
specifier|private
name|void
name|doTestCustomScore
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldScoreQuery
operator|.
name|Type
name|tp
parameter_list|,
name|double
name|dboost
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|Exception
block|{
name|float
name|boost
init|=
operator|(
name|float
operator|)
name|dboost
decl_stmt|;
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldScoreQuery
name|qValSrc
init|=
operator|new
name|FieldScoreQuery
argument_list|(
name|field
argument_list|,
name|tp
argument_list|)
decl_stmt|;
comment|// a query that would score by the field
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|TEXT_FIELD
argument_list|,
name|anlzr
argument_list|)
decl_stmt|;
name|String
name|qtxt
init|=
literal|"first aid text"
decl_stmt|;
comment|// from the doc texts in FunctionQuerySetup.
comment|// regular (boolean) query.
name|Query
name|q1
init|=
name|qp
operator|.
name|parse
argument_list|(
name|qtxt
argument_list|)
decl_stmt|;
name|log
argument_list|(
name|q1
argument_list|)
expr_stmt|;
comment|// custom query, that should score the same as q1.
name|CustomScoreQuery
name|q2CustomNeutral
init|=
operator|new
name|CustomScoreQuery
argument_list|(
name|q1
argument_list|)
decl_stmt|;
name|q2CustomNeutral
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|q2CustomNeutral
argument_list|)
expr_stmt|;
comment|// custom query, that should (by default) multiply the scores of q1 by that of the field
name|CustomScoreQuery
name|q3CustomMul
init|=
operator|new
name|CustomScoreQuery
argument_list|(
name|q1
argument_list|,
name|qValSrc
argument_list|)
decl_stmt|;
name|q3CustomMul
operator|.
name|setStrict
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|q3CustomMul
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|q3CustomMul
argument_list|)
expr_stmt|;
comment|// custom query, that should add the scores of q1 to that of the field
name|CustomScoreQuery
name|q4CustomAdd
init|=
operator|new
name|CustomAddQuery
argument_list|(
name|q1
argument_list|,
name|qValSrc
argument_list|)
decl_stmt|;
name|q4CustomAdd
operator|.
name|setStrict
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|q4CustomAdd
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|q4CustomAdd
argument_list|)
expr_stmt|;
comment|// custom query, that multiplies and adds the field score to that of q1
name|CustomScoreQuery
name|q5CustomMulAdd
init|=
operator|new
name|CustomMulAddQuery
argument_list|(
name|q1
argument_list|,
name|qValSrc
argument_list|,
name|qValSrc
argument_list|)
decl_stmt|;
name|q5CustomMulAdd
operator|.
name|setStrict
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|q5CustomMulAdd
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|q5CustomMulAdd
argument_list|)
expr_stmt|;
comment|// do al the searches
name|TopDocs
name|td1
init|=
name|s
operator|.
name|search
argument_list|(
name|q1
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|TopDocs
name|td2CustomNeutral
init|=
name|s
operator|.
name|search
argument_list|(
name|q2CustomNeutral
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|TopDocs
name|td3CustomMul
init|=
name|s
operator|.
name|search
argument_list|(
name|q3CustomMul
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|TopDocs
name|td4CustomAdd
init|=
name|s
operator|.
name|search
argument_list|(
name|q4CustomAdd
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|TopDocs
name|td5CustomMulAdd
init|=
name|s
operator|.
name|search
argument_list|(
name|q5CustomMulAdd
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
comment|// put results in map so we can verify the scores although they have changed
name|HashMap
name|h1
init|=
name|topDocsToMap
argument_list|(
name|td1
argument_list|)
decl_stmt|;
name|HashMap
name|h2CustomNeutral
init|=
name|topDocsToMap
argument_list|(
name|td2CustomNeutral
argument_list|)
decl_stmt|;
name|HashMap
name|h3CustomMul
init|=
name|topDocsToMap
argument_list|(
name|td3CustomMul
argument_list|)
decl_stmt|;
name|HashMap
name|h4CustomAdd
init|=
name|topDocsToMap
argument_list|(
name|td4CustomAdd
argument_list|)
decl_stmt|;
name|HashMap
name|h5CustomMulAdd
init|=
name|topDocsToMap
argument_list|(
name|td5CustomMulAdd
argument_list|)
decl_stmt|;
name|verifyResults
argument_list|(
name|boost
argument_list|,
name|s
argument_list|,
name|h1
argument_list|,
name|h2CustomNeutral
argument_list|,
name|h3CustomMul
argument_list|,
name|h4CustomAdd
argument_list|,
name|h5CustomMulAdd
argument_list|,
name|q1
argument_list|,
name|q2CustomNeutral
argument_list|,
name|q3CustomMul
argument_list|,
name|q4CustomAdd
argument_list|,
name|q5CustomMulAdd
argument_list|)
expr_stmt|;
block|}
comment|// verify results are as expected.
DECL|method|verifyResults
specifier|private
name|void
name|verifyResults
parameter_list|(
name|float
name|boost
parameter_list|,
name|IndexSearcher
name|s
parameter_list|,
name|HashMap
name|h1
parameter_list|,
name|HashMap
name|h2customNeutral
parameter_list|,
name|HashMap
name|h3CustomMul
parameter_list|,
name|HashMap
name|h4CustomAdd
parameter_list|,
name|HashMap
name|h5CustomMulAdd
parameter_list|,
name|Query
name|q1
parameter_list|,
name|Query
name|q2
parameter_list|,
name|Query
name|q3
parameter_list|,
name|Query
name|q4
parameter_list|,
name|Query
name|q5
parameter_list|)
throws|throws
name|Exception
block|{
comment|// verify numbers of matches
name|log
argument_list|(
literal|"#hits = "
operator|+
name|h1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"queries should have same #hits"
argument_list|,
name|h1
operator|.
name|size
argument_list|()
argument_list|,
name|h2customNeutral
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"queries should have same #hits"
argument_list|,
name|h1
operator|.
name|size
argument_list|()
argument_list|,
name|h3CustomMul
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"queries should have same #hits"
argument_list|,
name|h1
operator|.
name|size
argument_list|()
argument_list|,
name|h4CustomAdd
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"queries should have same #hits"
argument_list|,
name|h1
operator|.
name|size
argument_list|()
argument_list|,
name|h5CustomMulAdd
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify scores ratios
for|for
control|(
name|Iterator
name|it
init|=
name|h1
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Integer
name|x
init|=
operator|(
name|Integer
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
name|x
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|log
argument_list|(
literal|"doc = "
operator|+
name|doc
argument_list|)
expr_stmt|;
name|float
name|fieldScore
init|=
name|expectedFieldScore
argument_list|(
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|document
argument_list|(
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|ID_FIELD
argument_list|)
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"fieldScore = "
operator|+
name|fieldScore
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"fieldScore should not be 0"
argument_list|,
name|fieldScore
operator|>
literal|0
argument_list|)
expr_stmt|;
name|float
name|score1
init|=
operator|(
operator|(
name|Float
operator|)
name|h1
operator|.
name|get
argument_list|(
name|x
argument_list|)
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|logResult
argument_list|(
literal|"score1="
argument_list|,
name|s
argument_list|,
name|q1
argument_list|,
name|doc
argument_list|,
name|score1
argument_list|)
expr_stmt|;
name|float
name|score2
init|=
operator|(
operator|(
name|Float
operator|)
name|h2customNeutral
operator|.
name|get
argument_list|(
name|x
argument_list|)
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|logResult
argument_list|(
literal|"score2="
argument_list|,
name|s
argument_list|,
name|q2
argument_list|,
name|doc
argument_list|,
name|score2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"same score (just boosted) for neutral"
argument_list|,
name|boost
operator|*
name|score1
argument_list|,
name|score2
argument_list|,
name|TEST_SCORE_TOLERANCE_DELTA
argument_list|)
expr_stmt|;
name|float
name|score3
init|=
operator|(
operator|(
name|Float
operator|)
name|h3CustomMul
operator|.
name|get
argument_list|(
name|x
argument_list|)
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|logResult
argument_list|(
literal|"score3="
argument_list|,
name|s
argument_list|,
name|q3
argument_list|,
name|doc
argument_list|,
name|score3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new score for custom mul"
argument_list|,
name|boost
operator|*
name|fieldScore
operator|*
name|score1
argument_list|,
name|score3
argument_list|,
name|TEST_SCORE_TOLERANCE_DELTA
argument_list|)
expr_stmt|;
name|float
name|score4
init|=
operator|(
operator|(
name|Float
operator|)
name|h4CustomAdd
operator|.
name|get
argument_list|(
name|x
argument_list|)
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|logResult
argument_list|(
literal|"score4="
argument_list|,
name|s
argument_list|,
name|q4
argument_list|,
name|doc
argument_list|,
name|score4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new score for custom add"
argument_list|,
name|boost
operator|*
operator|(
name|fieldScore
operator|+
name|score1
operator|)
argument_list|,
name|score4
argument_list|,
name|TEST_SCORE_TOLERANCE_DELTA
argument_list|)
expr_stmt|;
name|float
name|score5
init|=
operator|(
operator|(
name|Float
operator|)
name|h5CustomMulAdd
operator|.
name|get
argument_list|(
name|x
argument_list|)
operator|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|logResult
argument_list|(
literal|"score5="
argument_list|,
name|s
argument_list|,
name|q5
argument_list|,
name|doc
argument_list|,
name|score5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new score for custom mul add"
argument_list|,
name|boost
operator|*
name|fieldScore
operator|*
operator|(
name|score1
operator|+
name|fieldScore
operator|)
argument_list|,
name|score5
argument_list|,
name|TEST_SCORE_TOLERANCE_DELTA
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|logResult
specifier|private
name|void
name|logResult
parameter_list|(
name|String
name|msg
parameter_list|,
name|IndexSearcher
name|s
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|doc
parameter_list|,
name|float
name|score1
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryUtils
operator|.
name|check
argument_list|(
name|q
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|msg
operator|+
literal|" "
operator|+
name|score1
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Explain by: "
operator|+
name|q
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|s
operator|.
name|explain
argument_list|(
name|q
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// since custom scoring modifies the order of docs, map results
comment|// by doc ids so that we can later compare/verify them
DECL|method|topDocsToMap
specifier|private
name|HashMap
name|topDocsToMap
parameter_list|(
name|TopDocs
name|td
parameter_list|)
block|{
name|HashMap
name|h
init|=
operator|new
name|HashMap
argument_list|()
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
name|td
operator|.
name|totalHits
condition|;
name|i
operator|++
control|)
block|{
name|h
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|td
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
name|td
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|h
return|;
block|}
block|}
end_class
end_unit
