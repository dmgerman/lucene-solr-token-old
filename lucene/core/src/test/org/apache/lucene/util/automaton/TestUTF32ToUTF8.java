begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Random
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
name|IntsRef
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
name|IntsRefBuilder
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
name|UnicodeUtil
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
name|fst
operator|.
name|Util
import|;
end_import
begin_class
DECL|class|TestUTF32ToUTF8
specifier|public
class|class
name|TestUTF32ToUTF8
extends|extends
name|LuceneTestCase
block|{
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
block|}
DECL|field|MAX_UNICODE
specifier|private
specifier|static
specifier|final
name|int
name|MAX_UNICODE
init|=
literal|0x10FFFF
decl_stmt|;
DECL|method|matches
specifier|private
name|boolean
name|matches
parameter_list|(
name|ByteRunAutomaton
name|a
parameter_list|,
name|int
name|code
parameter_list|)
block|{
name|char
index|[]
name|chars
init|=
name|Character
operator|.
name|toChars
argument_list|(
name|code
argument_list|)
decl_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|UnicodeUtil
operator|.
name|MAX_UTF8_BYTES_PER_CHAR
operator|*
name|chars
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|,
name|b
argument_list|)
decl_stmt|;
return|return
name|a
operator|.
name|run
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|testOne
specifier|private
name|void
name|testOne
parameter_list|(
name|Random
name|r
parameter_list|,
name|ByteRunAutomaton
name|a
parameter_list|,
name|int
name|startCode
parameter_list|,
name|int
name|endCode
parameter_list|,
name|int
name|iters
parameter_list|)
block|{
comment|// Verify correct ints are accepted
specifier|final
name|int
name|nonSurrogateCount
decl_stmt|;
specifier|final
name|boolean
name|ovSurStart
decl_stmt|;
if|if
condition|(
name|endCode
argument_list|<
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|||
name|startCode
argument_list|>
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
condition|)
block|{
comment|// no overlap w/ surrogates
name|nonSurrogateCount
operator|=
name|endCode
operator|-
name|startCode
operator|+
literal|1
expr_stmt|;
name|ovSurStart
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isSurrogate
argument_list|(
name|startCode
argument_list|)
condition|)
block|{
comment|// start of range overlaps surrogates
name|nonSurrogateCount
operator|=
name|endCode
operator|-
name|startCode
operator|+
literal|1
operator|-
operator|(
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
operator|-
name|startCode
operator|+
literal|1
operator|)
expr_stmt|;
name|ovSurStart
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isSurrogate
argument_list|(
name|endCode
argument_list|)
condition|)
block|{
comment|// end of range overlaps surrogates
name|ovSurStart
operator|=
literal|true
expr_stmt|;
name|nonSurrogateCount
operator|=
name|endCode
operator|-
name|startCode
operator|+
literal|1
operator|-
operator|(
name|endCode
operator|-
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|+
literal|1
operator|)
expr_stmt|;
block|}
else|else
block|{
comment|// range completely subsumes surrogates
name|ovSurStart
operator|=
literal|true
expr_stmt|;
name|nonSurrogateCount
operator|=
name|endCode
operator|-
name|startCode
operator|+
literal|1
operator|-
operator|(
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
operator|-
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|+
literal|1
operator|)
expr_stmt|;
block|}
assert|assert
name|nonSurrogateCount
operator|>
literal|0
assert|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
comment|// pick random code point in-range
name|int
name|code
init|=
name|startCode
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|nonSurrogateCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|isSurrogate
argument_list|(
name|code
argument_list|)
condition|)
block|{
if|if
condition|(
name|ovSurStart
condition|)
block|{
name|code
operator|=
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
operator|+
literal|1
operator|+
operator|(
name|code
operator|-
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|)
expr_stmt|;
block|}
else|else
block|{
name|code
operator|=
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
operator|+
literal|1
operator|+
operator|(
name|code
operator|-
name|startCode
operator|)
expr_stmt|;
block|}
block|}
assert|assert
name|code
operator|>=
name|startCode
operator|&&
name|code
operator|<=
name|endCode
operator|:
literal|"code="
operator|+
name|code
operator|+
literal|" start="
operator|+
name|startCode
operator|+
literal|" end="
operator|+
name|endCode
assert|;
assert|assert
operator|!
name|isSurrogate
argument_list|(
name|code
argument_list|)
assert|;
name|assertTrue
argument_list|(
literal|"DFA for range "
operator|+
name|startCode
operator|+
literal|"-"
operator|+
name|endCode
operator|+
literal|" failed to match code="
operator|+
name|code
argument_list|,
name|matches
argument_list|(
name|a
argument_list|,
name|code
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Verify invalid ints are not accepted
specifier|final
name|int
name|invalidRange
init|=
name|MAX_UNICODE
operator|-
operator|(
name|endCode
operator|-
name|startCode
operator|+
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|invalidRange
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|x
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0
argument_list|,
name|invalidRange
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|code
decl_stmt|;
if|if
condition|(
name|x
operator|>=
name|startCode
condition|)
block|{
name|code
operator|=
name|endCode
operator|+
literal|1
operator|+
name|x
operator|-
name|startCode
expr_stmt|;
block|}
else|else
block|{
name|code
operator|=
name|x
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|code
operator|>=
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|&&
name|code
operator|<=
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_END
operator|)
operator||
operator|(
name|code
operator|>=
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_START
operator|&&
name|code
operator|<=
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
operator|)
condition|)
block|{
name|iter
operator|--
expr_stmt|;
continue|continue;
block|}
name|assertFalse
argument_list|(
literal|"DFA for range "
operator|+
name|startCode
operator|+
literal|"-"
operator|+
name|endCode
operator|+
literal|" matched invalid code="
operator|+
name|code
argument_list|,
name|matches
argument_list|(
name|a
argument_list|,
name|code
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Evenly picks random code point from the 4 "buckets"
comment|// (bucket = same #bytes when encoded to utf8)
DECL|method|getCodeStart
specifier|private
name|int
name|getCodeStart
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
switch|switch
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0
argument_list|,
literal|128
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|128
argument_list|,
literal|2048
argument_list|)
return|;
case|case
literal|2
case|:
return|return
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|2048
argument_list|,
literal|65536
argument_list|)
return|;
default|default:
return|return
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|65536
argument_list|,
literal|1
operator|+
name|MAX_UNICODE
argument_list|)
return|;
block|}
block|}
DECL|method|isSurrogate
specifier|private
specifier|static
name|boolean
name|isSurrogate
parameter_list|(
name|int
name|code
parameter_list|)
block|{
return|return
name|code
operator|>=
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|&&
name|code
operator|<=
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
return|;
block|}
DECL|method|testRandomRanges
specifier|public
name|void
name|testRandomRanges
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|ITERS
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|int
name|ITERS_PER_DFA
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|ITERS
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|x1
init|=
name|getCodeStart
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|int
name|x2
init|=
name|getCodeStart
argument_list|(
name|r
argument_list|)
decl_stmt|;
specifier|final
name|int
name|startCode
decl_stmt|,
name|endCode
decl_stmt|;
if|if
condition|(
name|x1
operator|<
name|x2
condition|)
block|{
name|startCode
operator|=
name|x1
expr_stmt|;
name|endCode
operator|=
name|x2
expr_stmt|;
block|}
else|else
block|{
name|startCode
operator|=
name|x2
expr_stmt|;
name|endCode
operator|=
name|x1
expr_stmt|;
block|}
if|if
condition|(
name|isSurrogate
argument_list|(
name|startCode
argument_list|)
operator|&&
name|isSurrogate
argument_list|(
name|endCode
argument_list|)
condition|)
block|{
name|iter
operator|--
expr_stmt|;
continue|continue;
block|}
name|Automaton
name|a
init|=
name|Automata
operator|.
name|makeCharRange
argument_list|(
name|startCode
argument_list|,
name|endCode
argument_list|)
decl_stmt|;
name|testOne
argument_list|(
name|r
argument_list|,
operator|new
name|ByteRunAutomaton
argument_list|(
name|a
argument_list|)
argument_list|,
name|startCode
argument_list|,
name|endCode
argument_list|,
name|ITERS_PER_DFA
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSpecialCase
specifier|public
name|void
name|testSpecialCase
parameter_list|()
block|{
name|RegExp
name|re
init|=
operator|new
name|RegExp
argument_list|(
literal|".?"
argument_list|)
decl_stmt|;
name|Automaton
name|automaton
init|=
name|re
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|CharacterRunAutomaton
name|cra
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
name|ByteRunAutomaton
name|bra
init|=
operator|new
name|ByteRunAutomaton
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
comment|// make sure character dfa accepts empty string
name|assertTrue
argument_list|(
name|cra
operator|.
name|isAccept
argument_list|(
name|cra
operator|.
name|getInitialState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cra
operator|.
name|run
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cra
operator|.
name|run
argument_list|(
operator|new
name|char
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// make sure byte dfa accepts empty string
name|assertTrue
argument_list|(
name|bra
operator|.
name|isAccept
argument_list|(
name|bra
operator|.
name|getInitialState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bra
operator|.
name|run
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpecialCase2
specifier|public
name|void
name|testSpecialCase2
parameter_list|()
throws|throws
name|Exception
block|{
name|RegExp
name|re
init|=
operator|new
name|RegExp
argument_list|(
literal|".+\u0775"
argument_list|)
decl_stmt|;
name|String
name|input
init|=
literal|"\ufadc\ufffd\ub80b\uda5a\udc68\uf234\u0056\uda5b\udcc1\ufffd\ufffd\u0775"
decl_stmt|;
name|Automaton
name|automaton
init|=
name|re
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|CharacterRunAutomaton
name|cra
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
name|ByteRunAutomaton
name|bra
init|=
operator|new
name|ByteRunAutomaton
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cra
operator|.
name|run
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|input
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bra
operator|.
name|run
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|// this one fails!
block|}
DECL|method|testSpecialCase3
specifier|public
name|void
name|testSpecialCase3
parameter_list|()
throws|throws
name|Exception
block|{
name|RegExp
name|re
init|=
operator|new
name|RegExp
argument_list|(
literal|"(\\é¯º)*(.)*\\Ó"
argument_list|)
decl_stmt|;
name|String
name|input
init|=
literal|"\u5cfd\ufffd\ub2f7\u0033\ue304\u51d7\u3692\udb50\udfb3\u0576\udae2\udc62\u0053\u0449\u04d4"
decl_stmt|;
name|Automaton
name|automaton
init|=
name|re
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|CharacterRunAutomaton
name|cra
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
name|ByteRunAutomaton
name|bra
init|=
operator|new
name|ByteRunAutomaton
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cra
operator|.
name|run
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|input
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bra
operator|.
name|run
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomRegexes
specifier|public
name|void
name|testRandomRegexes
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|250
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
DECL|method|testSingleton
specifier|public
name|void
name|testSingleton
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Automaton
name|a
init|=
name|Automata
operator|.
name|makeString
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|Automaton
name|utf8
init|=
operator|new
name|UTF32ToUTF8
argument_list|()
operator|.
name|convert
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|IntsRefBuilder
name|ints
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
name|Util
operator|.
name|toIntsRef
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
argument_list|,
name|ints
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|ints
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set
argument_list|,
name|TestOperations
operator|.
name|getFiniteStrings
argument_list|(
name|utf8
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertAutomaton
specifier|private
name|void
name|assertAutomaton
parameter_list|(
name|Automaton
name|automaton
parameter_list|)
throws|throws
name|Exception
block|{
name|CharacterRunAutomaton
name|cra
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
name|ByteRunAutomaton
name|bra
init|=
operator|new
name|ByteRunAutomaton
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
specifier|final
name|AutomatonTestUtil
operator|.
name|RandomAcceptedStrings
name|ras
init|=
operator|new
name|AutomatonTestUtil
operator|.
name|RandomAcceptedStrings
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|1000
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
specifier|final
name|String
name|string
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// likely not accepted
name|string
operator|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// will be accepted
name|int
index|[]
name|codepoints
init|=
name|ras
operator|.
name|getRandomAcceptedString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|string
operator|=
name|UnicodeUtil
operator|.
name|newString
argument_list|(
name|codepoints
argument_list|,
literal|0
argument_list|,
name|codepoints
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|codepoints
operator|.
name|length
operator|+
literal|" codepoints:"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|codepoints
operator|.
name|length
condition|;
name|j
operator|++
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
name|Integer
operator|.
name|toHexString
argument_list|(
name|codepoints
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
name|byte
name|bytes
index|[]
init|=
name|string
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|cra
operator|.
name|run
argument_list|(
name|string
argument_list|)
argument_list|,
name|bra
operator|.
name|run
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
