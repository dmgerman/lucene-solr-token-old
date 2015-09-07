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
name|BooleanClause
operator|.
name|Occur
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
name|DefaultSimilarity
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
name|spans
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * TestExplanations subclass that builds up super crazy complex queries  * on the assumption that if the explanations work out right for them,  * they should work for anything.  */
end_comment
begin_class
DECL|class|TestComplexExplanations
specifier|public
class|class
name|TestComplexExplanations
extends|extends
name|BaseExplanationTestCase
block|{
comment|/**    * Override the Similarity used in our searcher with one that plays    * nice with boosts of 0.0    */
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
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|createQnorm1Similarity
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|IndexSearcher
operator|.
name|getDefaultSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|// must be static for weight serialization tests
DECL|method|createQnorm1Similarity
specifier|private
specifier|static
name|DefaultSimilarity
name|createQnorm1Similarity
parameter_list|()
block|{
return|return
operator|new
name|DefaultSimilarity
argument_list|()
block|{
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
literal|1.0f
return|;
comment|// / (float) Math.sqrt(1.0f + sumOfSquaredWeights);
block|}
block|}
return|;
block|}
DECL|method|test1
specifier|public
name|void
name|test1
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
operator|.
name|Builder
name|q
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|PhraseQuery
name|phraseQuery
init|=
operator|new
name|PhraseQuery
argument_list|(
literal|1
argument_list|,
name|FIELD
argument_list|,
literal|"w1"
argument_list|,
literal|"w2"
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|phraseQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|snear
argument_list|(
name|st
argument_list|(
literal|"w2"
argument_list|)
argument_list|,
name|sor
argument_list|(
literal|"w5"
argument_list|,
literal|"zz"
argument_list|)
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|snear
argument_list|(
name|sf
argument_list|(
literal|"w3"
argument_list|,
literal|2
argument_list|)
argument_list|,
name|st
argument_list|(
literal|"w2"
argument_list|)
argument_list|,
name|st
argument_list|(
literal|"w3"
argument_list|)
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Query
name|t
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|add
argument_list|(
name|matchTheseItems
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|t
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|matchTheseItems
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|t
argument_list|,
literal|30
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|dm
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.2f
argument_list|)
decl_stmt|;
name|dm
operator|.
name|add
argument_list|(
name|snear
argument_list|(
name|st
argument_list|(
literal|"w2"
argument_list|)
argument_list|,
name|sor
argument_list|(
literal|"w5"
argument_list|,
literal|"zz"
argument_list|)
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|dm
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"QQ"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|xxYYZZ
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|xxYYZZ
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|xxYYZZ
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"yy"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|xxYYZZ
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"zz"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|dm
operator|.
name|add
argument_list|(
name|xxYYZZ
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|xxW1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|xxW1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|xxW1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|dm
operator|.
name|add
argument_list|(
name|xxW1
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|dm2
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|dm2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dm2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dm2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dm
operator|.
name|add
argument_list|(
name|dm2
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|dm
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|b
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|b
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|b
operator|.
name|add
argument_list|(
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"w2"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|b
operator|.
name|add
argument_list|(
name|snear
argument_list|(
literal|"w2"
argument_list|,
literal|"w3"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|b
operator|.
name|add
argument_list|(
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"w3"
argument_list|,
literal|3
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|b
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|test2
specifier|public
name|void
name|test2
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
operator|.
name|Builder
name|q
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|PhraseQuery
name|phraseQuery
init|=
operator|new
name|PhraseQuery
argument_list|(
literal|1
argument_list|,
name|FIELD
argument_list|,
literal|"w1"
argument_list|,
literal|"w2"
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|phraseQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|snear
argument_list|(
name|st
argument_list|(
literal|"w2"
argument_list|)
argument_list|,
name|sor
argument_list|(
literal|"w5"
argument_list|,
literal|"zz"
argument_list|)
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|snear
argument_list|(
name|sf
argument_list|(
literal|"w3"
argument_list|,
literal|2
argument_list|)
argument_list|,
name|st
argument_list|(
literal|"w2"
argument_list|)
argument_list|,
name|st
argument_list|(
literal|"w3"
argument_list|)
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Query
name|t
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|add
argument_list|(
name|matchTheseItems
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|t
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|matchTheseItems
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|t
argument_list|,
operator|-
literal|20
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|dm
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.2f
argument_list|)
decl_stmt|;
name|dm
operator|.
name|add
argument_list|(
name|snear
argument_list|(
name|st
argument_list|(
literal|"w2"
argument_list|)
argument_list|,
name|sor
argument_list|(
literal|"w5"
argument_list|,
literal|"zz"
argument_list|)
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|dm
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"QQ"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|xxYYZZ
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|xxYYZZ
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|xxYYZZ
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"yy"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|xxYYZZ
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"zz"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|dm
operator|.
name|add
argument_list|(
name|xxYYZZ
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|xxW1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|xxW1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|xxW1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|dm
operator|.
name|add
argument_list|(
name|xxW1
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|dm2
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|dm2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dm2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dm2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dm
operator|.
name|add
argument_list|(
name|dm2
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|dm
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|builder
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"w2"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|snear
argument_list|(
literal|"w2"
argument_list|,
literal|"w3"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"w3"
argument_list|,
literal|3
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
name|b
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|b
argument_list|,
literal|0
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
comment|// :TODO: we really need more crazy complex cases.
comment|// //////////////////////////////////////////////////////////////////
comment|// The rest of these aren't that complex, but they are<i>somewhat</i>
comment|// complex, and they expose weakness in dealing with queries that match
comment|// with scores of 0 wrapped in other queries
DECL|method|testT3
specifier|public
name|void
name|testT3
parameter_list|()
throws|throws
name|Exception
block|{
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
decl_stmt|;
name|bqtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|query
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMA3
specifier|public
name|void
name|testMA3
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|bqtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFQ5
specifier|public
name|void
name|testFQ5
parameter_list|()
throws|throws
name|Exception
block|{
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|filtered
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|query
argument_list|,
literal|0
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|add
argument_list|(
name|matchTheseItems
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|bqtest
argument_list|(
name|filtered
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCSQ4
specifier|public
name|void
name|testCSQ4
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|matchTheseItems
argument_list|(
operator|new
name|int
index|[]
block|{
literal|3
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|bqtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDMQ10
specifier|public
name|void
name|testDMQ10
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"yy"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|TermQuery
name|boostedQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w5"
argument_list|)
argument_list|)
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|boostedQuery
argument_list|,
literal|100
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|query
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|TermQuery
name|xxBoostedQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|xxBoostedQuery
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|bqtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMPQ7
specifier|public
name|void
name|testMPQ7
parameter_list|()
throws|throws
name|Exception
block|{
name|MultiPhraseQuery
name|q
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w1"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w2"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|bqtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ12
specifier|public
name|void
name|testBQ12
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NOTE: using qtest not bqtest
name|BooleanQuery
operator|.
name|Builder
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|TermQuery
name|boostedQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|boostedQuery
argument_list|,
literal|0
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|query
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ13
specifier|public
name|void
name|testBQ13
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NOTE: using qtest not bqtest
name|BooleanQuery
operator|.
name|Builder
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|TermQuery
name|boostedQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w5"
argument_list|)
argument_list|)
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|boostedQuery
argument_list|,
literal|0
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|query
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ18
specifier|public
name|void
name|testBQ18
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NOTE: using qtest not bqtest
name|BooleanQuery
operator|.
name|Builder
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|TermQuery
name|boostedQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|boostedQuery
argument_list|,
literal|0
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|query
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ21
specifier|public
name|void
name|testBQ21
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|bqtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|query
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ22
specifier|public
name|void
name|testBQ22
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
empty_stmt|;
name|TermQuery
name|boostedQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|boostedQuery
argument_list|,
literal|0
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
name|query
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|bqtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|query
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testST3
specifier|public
name|void
name|testST3
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|st
argument_list|(
literal|"w1"
argument_list|)
decl_stmt|;
name|bqtest
argument_list|(
operator|new
name|SpanBoostQuery
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testST6
specifier|public
name|void
name|testST6
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|st
argument_list|(
literal|"xx"
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
operator|new
name|SpanBoostQuery
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSF3
specifier|public
name|void
name|testSF3
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|sf
argument_list|(
operator|(
literal|"w1"
operator|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|bqtest
argument_list|(
operator|new
name|SpanBoostQuery
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSF7
specifier|public
name|void
name|testSF7
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|sf
argument_list|(
operator|(
literal|"xx"
operator|)
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|bqtest
argument_list|(
operator|new
name|SpanBoostQuery
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNot3
specifier|public
name|void
name|testSNot3
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snot
argument_list|(
name|sf
argument_list|(
literal|"w1"
argument_list|,
literal|10
argument_list|)
argument_list|,
name|st
argument_list|(
literal|"QQ"
argument_list|)
argument_list|)
decl_stmt|;
name|bqtest
argument_list|(
operator|new
name|SpanBoostQuery
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNot6
specifier|public
name|void
name|testSNot6
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snot
argument_list|(
name|sf
argument_list|(
literal|"w1"
argument_list|,
literal|10
argument_list|)
argument_list|,
name|st
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
decl_stmt|;
name|bqtest
argument_list|(
operator|new
name|SpanBoostQuery
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNot8
specifier|public
name|void
name|testSNot8
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NOTE: using qtest not bqtest
name|SpanQuery
name|f
init|=
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"w3"
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|f
operator|=
operator|new
name|SpanBoostQuery
argument_list|(
name|f
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|SpanQuery
name|q
init|=
name|snot
argument_list|(
name|f
argument_list|,
name|st
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNot9
specifier|public
name|void
name|testSNot9
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NOTE: using qtest not bqtest
name|SpanQuery
name|t
init|=
name|st
argument_list|(
literal|"xx"
argument_list|)
decl_stmt|;
name|t
operator|=
operator|new
name|SpanBoostQuery
argument_list|(
name|t
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|SpanQuery
name|q
init|=
name|snot
argument_list|(
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"w3"
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
