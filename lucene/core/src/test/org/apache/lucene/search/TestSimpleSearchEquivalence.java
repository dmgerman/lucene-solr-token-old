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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Basic equivalence tests for core queries  */
end_comment
begin_class
DECL|class|TestSimpleSearchEquivalence
specifier|public
class|class
name|TestSimpleSearchEquivalence
extends|extends
name|SearchEquivalenceTestBase
block|{
comment|// TODO: we could go a little crazy for a lot of these,
comment|// but these are just simple minimal cases in case something
comment|// goes horribly wrong. Put more intense tests elsewhere.
comment|/** A â (A B) */
DECL|method|testTermVersusBooleanOr
specifier|public
name|void
name|testTermVersusBooleanOr
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
name|BooleanQuery
operator|.
name|Builder
name|q2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** A â (+A B) */
DECL|method|testTermVersusBooleanReqOpt
specifier|public
name|void
name|testTermVersusBooleanReqOpt
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
name|BooleanQuery
operator|.
name|Builder
name|q2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** (A -B) â A */
DECL|method|testBooleanReqExclVersusTerm
specifier|public
name|void
name|testBooleanReqExclVersusTerm
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
name|BooleanQuery
operator|.
name|Builder
name|q1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
operator|.
name|build
argument_list|()
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** (+A +B) â (A B) */
DECL|method|testBooleanAndVersusBooleanOr
specifier|public
name|void
name|testBooleanAndVersusBooleanOr
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
name|BooleanQuery
operator|.
name|Builder
name|q1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
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
name|q2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
operator|.
name|build
argument_list|()
argument_list|,
name|q2
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** (A B) = (A | B) */
DECL|method|testDisjunctionSumVersusDisjunctionMax
specifier|public
name|void
name|testDisjunctionSumVersusDisjunctionMax
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
name|BooleanQuery
operator|.
name|Builder
name|q1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|q2
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
argument_list|)
expr_stmt|;
name|assertSameSet
argument_list|(
name|q1
operator|.
name|build
argument_list|()
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** "A B" â (+A +B) */
DECL|method|testExactPhraseVersusBooleanAnd
specifier|public
name|void
name|testExactPhraseVersusBooleanAnd
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
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|(
name|t1
operator|.
name|field
argument_list|()
argument_list|,
name|t1
operator|.
name|bytes
argument_list|()
argument_list|,
name|t2
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|q2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** same as above, with posincs */
DECL|method|testExactPhraseVersusBooleanAndWithHoles
specifier|public
name|void
name|testExactPhraseVersusBooleanAndWithHoles
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
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|t1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|t2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q1
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|q2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** "A B" â "A B"~1 */
DECL|method|testPhraseVersusSloppyPhrase
specifier|public
name|void
name|testPhraseVersusSloppyPhrase
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
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|(
name|t1
operator|.
name|field
argument_list|()
argument_list|,
name|t1
operator|.
name|bytes
argument_list|()
argument_list|,
name|t2
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|PhraseQuery
name|q2
init|=
operator|new
name|PhraseQuery
argument_list|(
literal|1
argument_list|,
name|t1
operator|.
name|field
argument_list|()
argument_list|,
name|t1
operator|.
name|bytes
argument_list|()
argument_list|,
name|t2
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** same as above, with posincs */
DECL|method|testPhraseVersusSloppyPhraseWithHoles
specifier|public
name|void
name|testPhraseVersusSloppyPhraseWithHoles
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
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|t1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|t2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q1
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setSlop
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q2
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** "A B" â "A (B C)" */
DECL|method|testExactPhraseVersusMultiPhrase
specifier|public
name|void
name|testExactPhraseVersusMultiPhrase
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
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|(
name|t1
operator|.
name|field
argument_list|()
argument_list|,
name|t1
operator|.
name|bytes
argument_list|()
argument_list|,
name|t2
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|MultiPhraseQuery
name|q2
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
name|t2
block|,
name|t3
block|}
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** same as above, with posincs */
DECL|method|testExactPhraseVersusMultiPhraseWithHoles
specifier|public
name|void
name|testExactPhraseVersusMultiPhraseWithHoles
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
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|t1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|t2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q1
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|Term
name|t3
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|MultiPhraseQuery
name|q2
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
name|t2
block|,
name|t3
block|}
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** "A B"~â = +A +B if A != B */
DECL|method|testSloppyPhraseVersusBooleanAnd
specifier|public
name|void
name|testSloppyPhraseVersusBooleanAnd
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
literal|null
decl_stmt|;
comment|// semantics differ from SpanNear: SloppyPhrase handles repeats,
comment|// so we must ensure t1 != t2
do|do
block|{
name|t2
operator|=
name|randomTerm
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|t1
operator|.
name|equals
argument_list|(
name|t2
argument_list|)
condition|)
do|;
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|t1
operator|.
name|field
argument_list|()
argument_list|,
name|t1
operator|.
name|bytes
argument_list|()
argument_list|,
name|t2
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|q2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t1
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t2
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameSet
argument_list|(
name|q1
argument_list|,
name|q2
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Phrase positions are relative. */
DECL|method|testPhraseRelativePositions
specifier|public
name|void
name|testPhraseRelativePositions
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
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|(
name|t1
operator|.
name|field
argument_list|()
argument_list|,
name|t1
operator|.
name|bytes
argument_list|()
argument_list|,
name|t2
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|t1
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|t2
argument_list|,
literal|10001
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q2
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertSameScores
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
comment|/** Sloppy-phrase positions are relative. */
DECL|method|testSloppyPhraseRelativePositions
specifier|public
name|void
name|testSloppyPhraseRelativePositions
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
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|(
literal|2
argument_list|,
name|t1
operator|.
name|field
argument_list|()
argument_list|,
name|t1
operator|.
name|bytes
argument_list|()
argument_list|,
name|t2
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|t1
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|t2
argument_list|,
literal|10001
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setSlop
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q2
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertSameScores
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoostQuerySimplification
specifier|public
name|void
name|testBoostQuerySimplification
parameter_list|()
throws|throws
name|Exception
block|{
name|float
name|b1
init|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|*
literal|10
decl_stmt|;
name|float
name|b2
init|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|*
literal|10
decl_stmt|;
name|Term
name|term
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|Query
name|q1
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|BoostQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
argument_list|,
name|b2
argument_list|)
argument_list|,
name|b1
argument_list|)
decl_stmt|;
comment|// Use AssertingQuery to prevent BoostQuery from merging inner and outer boosts
name|Query
name|q2
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|AssertingQuery
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|BoostQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
argument_list|,
name|b2
argument_list|)
argument_list|)
argument_list|,
name|b1
argument_list|)
decl_stmt|;
name|assertSameScores
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
