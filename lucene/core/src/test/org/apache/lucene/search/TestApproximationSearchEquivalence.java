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
begin_comment
comment|/**  * Basic equivalence tests for approximations.  */
end_comment
begin_class
DECL|class|TestApproximationSearchEquivalence
specifier|public
class|class
name|TestApproximationSearchEquivalence
extends|extends
name|SearchEquivalenceTestBase
block|{
DECL|method|testConjunction
specifier|public
name|void
name|testConjunction
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q1
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q2
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
name|bq2
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedConjunction
specifier|public
name|void
name|testNestedConjunction
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
decl_stmt|;
name|TermQuery
name|q3
init|=
operator|new
name|TermQuery
argument_list|(
name|t3
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq3
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q1
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q2
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq4
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|bq3
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq2
operator|.
name|build
argument_list|()
argument_list|,
name|bq4
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDisjunction
specifier|public
name|void
name|testDisjunction
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q1
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q2
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
name|bq2
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedDisjunction
specifier|public
name|void
name|testNestedDisjunction
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
decl_stmt|;
name|TermQuery
name|q3
init|=
operator|new
name|TermQuery
argument_list|(
name|t3
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq3
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q1
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q2
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq4
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|bq3
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq2
operator|.
name|build
argument_list|()
argument_list|,
name|bq4
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDisjunctionInConjunction
specifier|public
name|void
name|testDisjunctionInConjunction
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
decl_stmt|;
name|TermQuery
name|q3
init|=
operator|new
name|TermQuery
argument_list|(
name|t3
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq3
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q1
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q2
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq4
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|bq3
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq2
operator|.
name|build
argument_list|()
argument_list|,
name|bq4
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testConjunctionInDisjunction
specifier|public
name|void
name|testConjunctionInDisjunction
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
decl_stmt|;
name|TermQuery
name|q3
init|=
operator|new
name|TermQuery
argument_list|(
name|t3
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq3
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q1
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q2
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq4
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|bq3
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq2
operator|.
name|build
argument_list|()
argument_list|,
name|bq4
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testConstantScore
specifier|public
name|void
name|testConstantScore
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|q1
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|q2
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q1
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q2
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
name|bq2
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testExclusion
specifier|public
name|void
name|testExclusion
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q1
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q2
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
name|bq2
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedExclusion
specifier|public
name|void
name|testNestedExclusion
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
decl_stmt|;
name|TermQuery
name|q3
init|=
operator|new
name|TermQuery
argument_list|(
name|t3
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
comment|// Both req and excl have approximations
name|BooleanQuery
operator|.
name|Builder
name|bq3
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q1
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q2
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq4
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|bq3
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq2
operator|.
name|build
argument_list|()
argument_list|,
name|bq4
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// Only req has an approximation
name|bq3
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
expr_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q1
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq3
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|bq4
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
expr_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|bq3
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq2
operator|.
name|build
argument_list|()
argument_list|,
name|bq4
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// Only excl has an approximation
name|bq3
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
expr_stmt|;
name|bq3
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q2
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|bq4
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
expr_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|bq3
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq2
operator|.
name|build
argument_list|()
argument_list|,
name|bq4
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testReqOpt
specifier|public
name|void
name|testReqOpt
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t1
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t2
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
decl_stmt|;
name|TermQuery
name|q3
init|=
operator|new
name|TermQuery
argument_list|(
name|t3
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|q2
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq3
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q1
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq3
operator|.
name|add
argument_list|(
operator|new
name|RandomApproximationQuery
argument_list|(
name|q2
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq4
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|bq3
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq4
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|bq2
operator|.
name|build
argument_list|()
argument_list|,
name|bq4
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
