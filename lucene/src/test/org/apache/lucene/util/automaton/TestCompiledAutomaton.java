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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|_TestUtil
import|;
end_import
begin_class
DECL|class|TestCompiledAutomaton
specifier|public
class|class
name|TestCompiledAutomaton
extends|extends
name|LuceneTestCase
block|{
DECL|method|build
specifier|private
name|CompiledAutomaton
name|build
parameter_list|(
name|String
modifier|...
name|strings
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|strings
control|)
block|{
name|terms
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|terms
argument_list|)
expr_stmt|;
specifier|final
name|Automaton
name|a
init|=
name|DaciukMihovAutomatonBuilder
operator|.
name|build
argument_list|(
name|terms
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompiledAutomaton
argument_list|(
name|a
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|testFloor
specifier|private
name|void
name|testFloor
parameter_list|(
name|CompiledAutomaton
name|c
parameter_list|,
name|String
name|input
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
specifier|final
name|BytesRef
name|b
init|=
operator|new
name|BytesRef
argument_list|(
name|input
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|result
init|=
name|c
operator|.
name|floor
argument_list|(
name|b
argument_list|,
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"actual="
operator|+
name|result
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" vs expected="
operator|+
name|expected
operator|+
literal|" (input="
operator|+
name|input
operator|+
literal|")"
argument_list|,
name|result
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTerms
specifier|private
name|void
name|testTerms
parameter_list|(
name|String
index|[]
name|terms
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|CompiledAutomaton
name|c
init|=
name|build
argument_list|(
name|terms
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
index|[]
name|termBytes
init|=
operator|new
name|BytesRef
index|[
name|terms
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|terms
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|termBytes
index|[
name|idx
index|]
operator|=
operator|new
name|BytesRef
argument_list|(
name|terms
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|termBytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: terms in unicode order"
argument_list|)
expr_stmt|;
for|for
control|(
name|BytesRef
name|t
range|:
name|termBytes
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|t
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println(c.utf8.toDot());
block|}
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|100
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|String
name|s
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|1
condition|?
name|terms
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|length
argument_list|)
index|]
else|:
name|randomString
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: floor("
operator|+
name|s
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|int
name|loc
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|termBytes
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected
decl_stmt|;
if|if
condition|(
name|loc
operator|>=
literal|0
condition|)
block|{
name|expected
operator|=
name|s
expr_stmt|;
block|}
else|else
block|{
comment|// term doesn't exist
name|loc
operator|=
operator|-
operator|(
name|loc
operator|+
literal|1
operator|)
expr_stmt|;
if|if
condition|(
name|loc
operator|==
literal|0
condition|)
block|{
name|expected
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|expected
operator|=
name|termBytes
index|[
name|loc
operator|-
literal|1
index|]
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  expected="
operator|+
name|expected
argument_list|)
expr_stmt|;
block|}
name|testFloor
argument_list|(
name|c
argument_list|,
name|s
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numTerms
init|=
name|atLeast
argument_list|(
literal|400
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|!=
name|numTerms
condition|)
block|{
name|terms
operator|.
name|add
argument_list|(
name|randomString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|testTerms
argument_list|(
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|randomString
specifier|private
name|String
name|randomString
parameter_list|()
block|{
comment|// return _TestUtil.randomSimpleString(random);
return|return
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|)
return|;
block|}
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|CompiledAutomaton
name|c
init|=
name|build
argument_list|(
literal|"fob"
argument_list|,
literal|"foo"
argument_list|,
literal|"goo"
argument_list|)
decl_stmt|;
name|testFloor
argument_list|(
name|c
argument_list|,
literal|"goo"
argument_list|,
literal|"goo"
argument_list|)
expr_stmt|;
name|testFloor
argument_list|(
name|c
argument_list|,
literal|"ga"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|testFloor
argument_list|(
name|c
argument_list|,
literal|"g"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|testFloor
argument_list|(
name|c
argument_list|,
literal|"foc"
argument_list|,
literal|"fob"
argument_list|)
expr_stmt|;
name|testFloor
argument_list|(
name|c
argument_list|,
literal|"foz"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|testFloor
argument_list|(
name|c
argument_list|,
literal|"f"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testFloor
argument_list|(
name|c
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testFloor
argument_list|(
name|c
argument_list|,
literal|"aa"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testFloor
argument_list|(
name|c
argument_list|,
literal|"zzz"
argument_list|,
literal|"goo"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
