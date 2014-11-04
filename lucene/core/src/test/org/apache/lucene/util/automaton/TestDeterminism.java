begin_unit
begin_package
DECL|package|org.apache.lucene.util.automaton
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|Operations
operator|.
name|DEFAULT_MAX_DETERMINIZED_STATES
import|;
end_import
begin_comment
comment|/**  * Not completely thorough, but tries to test determinism correctness  * somewhat randomly.  */
end_comment
begin_class
DECL|class|TestDeterminism
specifier|public
class|class
name|TestDeterminism
extends|extends
name|LuceneTestCase
block|{
comment|/** test a bunch of random regular expressions */
DECL|method|testRegexps
specifier|public
name|void
name|testRegexps
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|500
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|assertAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
name|AutomatonTestUtil
operator|.
name|randomRegexp
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** test against a simple, unoptimized det */
DECL|method|testAgainstSimple
specifier|public
name|void
name|testAgainstSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|200
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|Automaton
name|a
init|=
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|a
operator|=
name|AutomatonTestUtil
operator|.
name|determinizeSimple
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|Automaton
name|b
init|=
name|Operations
operator|.
name|determinize
argument_list|(
name|a
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
decl_stmt|;
comment|// TODO: more verifications possible?
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertAutomaton
specifier|private
specifier|static
name|void
name|assertAutomaton
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
name|a
operator|=
name|Operations
operator|.
name|determinize
argument_list|(
name|Operations
operator|.
name|removeDeadStates
argument_list|(
name|a
argument_list|)
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
expr_stmt|;
comment|// complement(complement(a)) = a
name|Automaton
name|equivalent
init|=
name|Operations
operator|.
name|complement
argument_list|(
name|Operations
operator|.
name|complement
argument_list|(
name|a
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|a
argument_list|,
name|equivalent
argument_list|)
argument_list|)
expr_stmt|;
comment|// a union a = a
name|equivalent
operator|=
name|Operations
operator|.
name|determinize
argument_list|(
name|Operations
operator|.
name|removeDeadStates
argument_list|(
name|Operations
operator|.
name|union
argument_list|(
name|a
argument_list|,
name|a
argument_list|)
argument_list|)
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|a
argument_list|,
name|equivalent
argument_list|)
argument_list|)
expr_stmt|;
comment|// a intersect a = a
name|equivalent
operator|=
name|Operations
operator|.
name|determinize
argument_list|(
name|Operations
operator|.
name|removeDeadStates
argument_list|(
name|Operations
operator|.
name|intersection
argument_list|(
name|a
argument_list|,
name|a
argument_list|)
argument_list|)
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|a
argument_list|,
name|equivalent
argument_list|)
argument_list|)
expr_stmt|;
comment|// a minus a = empty
name|Automaton
name|empty
init|=
name|Operations
operator|.
name|minus
argument_list|(
name|a
argument_list|,
name|a
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|isEmpty
argument_list|(
name|empty
argument_list|)
argument_list|)
expr_stmt|;
comment|// as long as don't accept the empty string
comment|// then optional(a) - empty = a
if|if
condition|(
operator|!
name|Operations
operator|.
name|run
argument_list|(
name|a
argument_list|,
literal|""
argument_list|)
condition|)
block|{
comment|//System.out.println("test " + a);
name|Automaton
name|optional
init|=
name|Operations
operator|.
name|optional
argument_list|(
name|a
argument_list|)
decl_stmt|;
comment|//System.out.println("optional " + optional);
name|equivalent
operator|=
name|Operations
operator|.
name|minus
argument_list|(
name|optional
argument_list|,
name|Automata
operator|.
name|makeEmptyString
argument_list|()
argument_list|,
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
expr_stmt|;
comment|//System.out.println("equiv " + equivalent);
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|a
argument_list|,
name|equivalent
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
