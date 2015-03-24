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
name|util
operator|.
name|TestUtil
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
operator|.
name|AwaitsFix
import|;
end_import
begin_comment
comment|/**  * random sloppy phrase query tests  */
end_comment
begin_class
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/LUCENE-6369"
argument_list|)
DECL|class|TestSloppyPhraseQuery2
specifier|public
class|class
name|TestSloppyPhraseQuery2
extends|extends
name|SearchEquivalenceTestBase
block|{
comment|/** "A B"~N â "A B"~N+1 */
DECL|method|testIncreasingSloppiness
specifier|public
name|void
name|testIncreasingSloppiness
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
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q2
init|=
operator|new
name|PhraseQuery
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
name|t2
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|q1
operator|.
name|setSlop
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|q2
operator|.
name|setSlop
argument_list|(
name|i
operator|+
literal|1
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
block|}
comment|/** same as the above with posincr */
DECL|method|testIncreasingSloppinessWithHoles
specifier|public
name|void
name|testIncreasingSloppinessWithHoles
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
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q2
init|=
operator|new
name|PhraseQuery
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
name|t2
argument_list|,
literal|2
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|q1
operator|.
name|setSlop
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|q2
operator|.
name|setSlop
argument_list|(
name|i
operator|+
literal|1
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
block|}
comment|/** "A B C"~N â "A B C"~N+1 */
DECL|method|testIncreasingSloppiness3
specifier|public
name|void
name|testIncreasingSloppiness3
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
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t3
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q2
init|=
operator|new
name|PhraseQuery
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
name|t2
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t3
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|q1
operator|.
name|setSlop
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|q2
operator|.
name|setSlop
argument_list|(
name|i
operator|+
literal|1
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
block|}
comment|/** same as the above with posincr */
DECL|method|testIncreasingSloppiness3WithHoles
specifier|public
name|void
name|testIncreasingSloppiness3WithHoles
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
name|int
name|pos1
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|int
name|pos2
init|=
name|pos1
operator|+
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t2
argument_list|,
name|pos1
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t3
argument_list|,
name|pos2
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q2
init|=
operator|new
name|PhraseQuery
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
name|t2
argument_list|,
name|pos1
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t3
argument_list|,
name|pos2
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|q1
operator|.
name|setSlop
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|q2
operator|.
name|setSlop
argument_list|(
name|i
operator|+
literal|1
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
block|}
comment|/** "A A"~N â "A A"~N+1 */
DECL|method|testRepetitiveIncreasingSloppiness
specifier|public
name|void
name|testRepetitiveIncreasingSloppiness
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q2
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|q1
operator|.
name|setSlop
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|q2
operator|.
name|setSlop
argument_list|(
name|i
operator|+
literal|1
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
block|}
comment|/** same as the above with posincr */
DECL|method|testRepetitiveIncreasingSloppinessWithHoles
specifier|public
name|void
name|testRepetitiveIncreasingSloppinessWithHoles
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q2
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t
argument_list|,
literal|2
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|q1
operator|.
name|setSlop
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|q2
operator|.
name|setSlop
argument_list|(
name|i
operator|+
literal|1
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
block|}
comment|/** "A A A"~N â "A A A"~N+1 */
DECL|method|testRepetitiveIncreasingSloppiness3
specifier|public
name|void
name|testRepetitiveIncreasingSloppiness3
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q2
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|q1
operator|.
name|setSlop
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|q2
operator|.
name|setSlop
argument_list|(
name|i
operator|+
literal|1
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
block|}
comment|/** same as the above with posincr */
DECL|method|testRepetitiveIncreasingSloppiness3WithHoles
specifier|public
name|void
name|testRepetitiveIncreasingSloppiness3WithHoles
parameter_list|()
throws|throws
name|Exception
block|{
name|Term
name|t
init|=
name|randomTerm
argument_list|()
decl_stmt|;
name|int
name|pos1
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|int
name|pos2
init|=
name|pos1
operator|+
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|PhraseQuery
name|q1
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t
argument_list|,
name|pos1
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
name|t
argument_list|,
name|pos2
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q2
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t
argument_list|,
name|pos1
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|t
argument_list|,
name|pos2
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|q1
operator|.
name|setSlop
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|q2
operator|.
name|setSlop
argument_list|(
name|i
operator|+
literal|1
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
block|}
comment|/** MultiPhraseQuery~N â MultiPhraseQuery~N+1 */
DECL|method|testRandomIncreasingSloppiness
specifier|public
name|void
name|testRandomIncreasingSloppiness
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|MultiPhraseQuery
name|q1
init|=
name|randomPhraseQuery
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|MultiPhraseQuery
name|q2
init|=
name|randomPhraseQuery
argument_list|(
name|seed
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|q1
operator|.
name|setSlop
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|q2
operator|.
name|setSlop
argument_list|(
name|i
operator|+
literal|1
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
block|}
DECL|method|randomPhraseQuery
specifier|private
name|MultiPhraseQuery
name|randomPhraseQuery
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|MultiPhraseQuery
name|pq
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|int
name|position
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|depth
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|Term
name|terms
index|[]
init|=
operator|new
name|Term
index|[
name|depth
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|depth
condition|;
name|j
operator|++
control|)
block|{
name|terms
index|[
name|j
index|]
operator|=
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|""
operator|+
operator|(
name|char
operator|)
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|pq
operator|.
name|add
argument_list|(
name|terms
argument_list|,
name|position
argument_list|)
expr_stmt|;
name|position
operator|+=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
return|return
name|pq
return|;
block|}
block|}
end_class
end_unit
