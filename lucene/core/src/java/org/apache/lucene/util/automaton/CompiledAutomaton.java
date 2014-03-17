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
name|List
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
name|Terms
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
name|TermsEnum
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
name|PrefixTermsEnum
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
name|SingleTermsEnum
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
begin_comment
comment|/**  * Immutable class holding compiled details for a given  * Automaton.  The Automaton is deterministic, must not have  * dead states but is not necessarily minimal.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|CompiledAutomaton
specifier|public
class|class
name|CompiledAutomaton
block|{
comment|/**    * Automata are compiled into different internal forms for the    * most efficient execution depending upon the language they accept.    */
DECL|enum|AUTOMATON_TYPE
specifier|public
enum|enum
name|AUTOMATON_TYPE
block|{
comment|/** Automaton that accepts no strings. */
DECL|enum constant|NONE
name|NONE
block|,
comment|/** Automaton that accepts all possible strings. */
DECL|enum constant|ALL
name|ALL
block|,
comment|/** Automaton that accepts only a single fixed string. */
DECL|enum constant|SINGLE
name|SINGLE
block|,
comment|/** Automaton that matches all Strings with a constant prefix. */
DECL|enum constant|PREFIX
name|PREFIX
block|,
comment|/** Catch-all for any other automata. */
DECL|enum constant|NORMAL
name|NORMAL
block|}
empty_stmt|;
DECL|field|type
specifier|public
specifier|final
name|AUTOMATON_TYPE
name|type
decl_stmt|;
comment|/**     * For {@link AUTOMATON_TYPE#PREFIX}, this is the prefix term;     * for {@link AUTOMATON_TYPE#SINGLE} this is the singleton term.    */
DECL|field|term
specifier|public
specifier|final
name|BytesRef
name|term
decl_stmt|;
comment|/**     * Matcher for quickly determining if a byte[] is accepted.    * only valid for {@link AUTOMATON_TYPE#NORMAL}.    */
DECL|field|runAutomaton
specifier|public
specifier|final
name|ByteRunAutomaton
name|runAutomaton
decl_stmt|;
comment|// TODO: would be nice if these sortedTransitions had "int
comment|// to;" instead of "State to;" somehow:
comment|/**    * Two dimensional array of transitions, indexed by state    * number for traversal. The state numbering is consistent with    * {@link #runAutomaton}.     * Only valid for {@link AUTOMATON_TYPE#NORMAL}.    */
DECL|field|sortedTransitions
specifier|public
specifier|final
name|Transition
index|[]
index|[]
name|sortedTransitions
decl_stmt|;
comment|/**    * Shared common suffix accepted by the automaton. Only valid    * for {@link AUTOMATON_TYPE#NORMAL}, and only when the    * automaton accepts an infinite language.    */
DECL|field|commonSuffixRef
specifier|public
specifier|final
name|BytesRef
name|commonSuffixRef
decl_stmt|;
comment|/**    * Indicates if the automaton accepts a finite set of strings.    * Null if this was not computed.    * Only valid for {@link AUTOMATON_TYPE#NORMAL}.    */
DECL|field|finite
specifier|public
specifier|final
name|Boolean
name|finite
decl_stmt|;
DECL|method|CompiledAutomaton
specifier|public
name|CompiledAutomaton
parameter_list|(
name|Automaton
name|automaton
parameter_list|)
block|{
name|this
argument_list|(
name|automaton
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|CompiledAutomaton
specifier|public
name|CompiledAutomaton
parameter_list|(
name|Automaton
name|automaton
parameter_list|,
name|Boolean
name|finite
parameter_list|,
name|boolean
name|simplify
parameter_list|)
block|{
if|if
condition|(
name|simplify
condition|)
block|{
comment|// Test whether the automaton is a "simple" form and
comment|// if so, don't create a runAutomaton.  Note that on a
comment|// large automaton these tests could be costly:
if|if
condition|(
name|BasicOperations
operator|.
name|isEmpty
argument_list|(
name|automaton
argument_list|)
condition|)
block|{
comment|// matches nothing
name|type
operator|=
name|AUTOMATON_TYPE
operator|.
name|NONE
expr_stmt|;
name|term
operator|=
literal|null
expr_stmt|;
name|commonSuffixRef
operator|=
literal|null
expr_stmt|;
name|runAutomaton
operator|=
literal|null
expr_stmt|;
name|sortedTransitions
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|finite
operator|=
literal|null
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|BasicOperations
operator|.
name|isTotal
argument_list|(
name|automaton
argument_list|)
condition|)
block|{
comment|// matches all possible strings
name|type
operator|=
name|AUTOMATON_TYPE
operator|.
name|ALL
expr_stmt|;
name|term
operator|=
literal|null
expr_stmt|;
name|commonSuffixRef
operator|=
literal|null
expr_stmt|;
name|runAutomaton
operator|=
literal|null
expr_stmt|;
name|sortedTransitions
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|finite
operator|=
literal|null
expr_stmt|;
return|return;
block|}
else|else
block|{
specifier|final
name|String
name|commonPrefix
decl_stmt|;
specifier|final
name|String
name|singleton
decl_stmt|;
if|if
condition|(
name|automaton
operator|.
name|getSingleton
argument_list|()
operator|==
literal|null
condition|)
block|{
name|commonPrefix
operator|=
name|SpecialOperations
operator|.
name|getCommonPrefix
argument_list|(
name|automaton
argument_list|)
expr_stmt|;
if|if
condition|(
name|commonPrefix
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|automaton
argument_list|,
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|commonPrefix
argument_list|)
argument_list|)
condition|)
block|{
name|singleton
operator|=
name|commonPrefix
expr_stmt|;
block|}
else|else
block|{
name|singleton
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|commonPrefix
operator|=
literal|null
expr_stmt|;
name|singleton
operator|=
name|automaton
operator|.
name|getSingleton
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|singleton
operator|!=
literal|null
condition|)
block|{
comment|// matches a fixed string in singleton or expanded
comment|// representation
name|type
operator|=
name|AUTOMATON_TYPE
operator|.
name|SINGLE
expr_stmt|;
name|term
operator|=
operator|new
name|BytesRef
argument_list|(
name|singleton
argument_list|)
expr_stmt|;
name|commonSuffixRef
operator|=
literal|null
expr_stmt|;
name|runAutomaton
operator|=
literal|null
expr_stmt|;
name|sortedTransitions
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|finite
operator|=
literal|null
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|BasicOperations
operator|.
name|sameLanguage
argument_list|(
name|automaton
argument_list|,
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|commonPrefix
argument_list|)
argument_list|,
name|BasicAutomata
operator|.
name|makeAnyString
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
comment|// matches a constant prefix
name|type
operator|=
name|AUTOMATON_TYPE
operator|.
name|PREFIX
expr_stmt|;
name|term
operator|=
operator|new
name|BytesRef
argument_list|(
name|commonPrefix
argument_list|)
expr_stmt|;
name|commonSuffixRef
operator|=
literal|null
expr_stmt|;
name|runAutomaton
operator|=
literal|null
expr_stmt|;
name|sortedTransitions
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|finite
operator|=
literal|null
expr_stmt|;
return|return;
block|}
block|}
block|}
name|type
operator|=
name|AUTOMATON_TYPE
operator|.
name|NORMAL
expr_stmt|;
name|term
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|finite
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|finite
operator|=
name|SpecialOperations
operator|.
name|isFinite
argument_list|(
name|automaton
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|finite
operator|=
name|finite
expr_stmt|;
block|}
name|Automaton
name|utf8
init|=
operator|new
name|UTF32ToUTF8
argument_list|()
operator|.
name|convert
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|finite
condition|)
block|{
name|commonSuffixRef
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|commonSuffixRef
operator|=
name|SpecialOperations
operator|.
name|getCommonSuffixBytesRef
argument_list|(
name|utf8
argument_list|)
expr_stmt|;
block|}
name|runAutomaton
operator|=
operator|new
name|ByteRunAutomaton
argument_list|(
name|utf8
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sortedTransitions
operator|=
name|utf8
operator|.
name|getSortedTransitions
argument_list|()
expr_stmt|;
block|}
comment|//private static final boolean DEBUG = BlockTreeTermsWriter.DEBUG;
DECL|method|addTail
specifier|private
name|BytesRef
name|addTail
parameter_list|(
name|int
name|state
parameter_list|,
name|BytesRef
name|term
parameter_list|,
name|int
name|idx
parameter_list|,
name|int
name|leadLabel
parameter_list|)
block|{
comment|// Find biggest transition that's< label
comment|// TODO: use binary search here
name|Transition
name|maxTransition
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Transition
name|transition
range|:
name|sortedTransitions
index|[
name|state
index|]
control|)
block|{
if|if
condition|(
name|transition
operator|.
name|min
operator|<
name|leadLabel
condition|)
block|{
name|maxTransition
operator|=
name|transition
expr_stmt|;
block|}
block|}
assert|assert
name|maxTransition
operator|!=
literal|null
assert|;
comment|// Append floorLabel
specifier|final
name|int
name|floorLabel
decl_stmt|;
if|if
condition|(
name|maxTransition
operator|.
name|max
operator|>
name|leadLabel
operator|-
literal|1
condition|)
block|{
name|floorLabel
operator|=
name|leadLabel
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|floorLabel
operator|=
name|maxTransition
operator|.
name|max
expr_stmt|;
block|}
if|if
condition|(
name|idx
operator|>=
name|term
operator|.
name|bytes
operator|.
name|length
condition|)
block|{
name|term
operator|.
name|grow
argument_list|(
literal|1
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
comment|//if (DEBUG) System.out.println("  add floorLabel=" + (char) floorLabel + " idx=" + idx);
name|term
operator|.
name|bytes
index|[
name|idx
index|]
operator|=
operator|(
name|byte
operator|)
name|floorLabel
expr_stmt|;
name|state
operator|=
name|maxTransition
operator|.
name|to
operator|.
name|getNumber
argument_list|()
expr_stmt|;
name|idx
operator|++
expr_stmt|;
comment|// Push down to last accept state
while|while
condition|(
literal|true
condition|)
block|{
name|Transition
index|[]
name|transitions
init|=
name|sortedTransitions
index|[
name|state
index|]
decl_stmt|;
if|if
condition|(
name|transitions
operator|.
name|length
operator|==
literal|0
condition|)
block|{
assert|assert
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
assert|;
name|term
operator|.
name|length
operator|=
name|idx
expr_stmt|;
comment|//if (DEBUG) System.out.println("  return " + term.utf8ToString());
return|return
name|term
return|;
block|}
else|else
block|{
comment|// We are pushing "top" -- so get last label of
comment|// last transition:
assert|assert
name|transitions
operator|.
name|length
operator|!=
literal|0
assert|;
name|Transition
name|lastTransition
init|=
name|transitions
index|[
name|transitions
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|idx
operator|>=
name|term
operator|.
name|bytes
operator|.
name|length
condition|)
block|{
name|term
operator|.
name|grow
argument_list|(
literal|1
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
comment|//if (DEBUG) System.out.println("  push maxLabel=" + (char) lastTransition.max + " idx=" + idx);
name|term
operator|.
name|bytes
index|[
name|idx
index|]
operator|=
operator|(
name|byte
operator|)
name|lastTransition
operator|.
name|max
expr_stmt|;
name|state
operator|=
name|lastTransition
operator|.
name|to
operator|.
name|getNumber
argument_list|()
expr_stmt|;
name|idx
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|// TODO: should this take startTerm too?  This way
comment|// Terms.intersect could forward to this method if type !=
comment|// NORMAL:
DECL|method|getTermsEnum
specifier|public
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|NONE
case|:
return|return
name|TermsEnum
operator|.
name|EMPTY
return|;
case|case
name|ALL
case|:
return|return
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
return|;
case|case
name|SINGLE
case|:
return|return
operator|new
name|SingleTermsEnum
argument_list|(
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|,
name|term
argument_list|)
return|;
case|case
name|PREFIX
case|:
comment|// TODO: this is very likely faster than .intersect,
comment|// but we should test and maybe cutover
return|return
operator|new
name|PrefixTermsEnum
argument_list|(
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|,
name|term
argument_list|)
return|;
case|case
name|NORMAL
case|:
return|return
name|terms
operator|.
name|intersect
argument_list|(
name|this
argument_list|,
literal|null
argument_list|)
return|;
default|default:
comment|// unreachable
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unhandled case"
argument_list|)
throw|;
block|}
block|}
comment|/** Finds largest term accepted by this Automaton, that's    *<= the provided input term.  The result is placed in    *  output; it's fine for output and input to point to    *  the same BytesRef.  The returned result is either the    *  provided output, or null if there is no floor term    *  (ie, the provided input term is before the first term    *  accepted by this Automaton). */
DECL|method|floor
specifier|public
name|BytesRef
name|floor
parameter_list|(
name|BytesRef
name|input
parameter_list|,
name|BytesRef
name|output
parameter_list|)
block|{
name|output
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
comment|//if (DEBUG) System.out.println("CA.floor input=" + input.utf8ToString());
name|int
name|state
init|=
name|runAutomaton
operator|.
name|getInitialState
argument_list|()
decl_stmt|;
comment|// Special case empty string:
if|if
condition|(
name|input
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
condition|)
block|{
name|output
operator|.
name|length
operator|=
literal|0
expr_stmt|;
return|return
name|output
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|stack
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|label
init|=
name|input
operator|.
name|bytes
index|[
name|input
operator|.
name|offset
operator|+
name|idx
index|]
operator|&
literal|0xff
decl_stmt|;
name|int
name|nextState
init|=
name|runAutomaton
operator|.
name|step
argument_list|(
name|state
argument_list|,
name|label
argument_list|)
decl_stmt|;
comment|//if (DEBUG) System.out.println("  cycle label=" + (char) label + " nextState=" + nextState);
if|if
condition|(
name|idx
operator|==
name|input
operator|.
name|length
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|nextState
operator|!=
operator|-
literal|1
operator|&&
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|nextState
argument_list|)
condition|)
block|{
comment|// Input string is accepted
if|if
condition|(
name|idx
operator|>=
name|output
operator|.
name|bytes
operator|.
name|length
condition|)
block|{
name|output
operator|.
name|grow
argument_list|(
literal|1
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|bytes
index|[
name|idx
index|]
operator|=
operator|(
name|byte
operator|)
name|label
expr_stmt|;
name|output
operator|.
name|length
operator|=
name|input
operator|.
name|length
expr_stmt|;
comment|//if (DEBUG) System.out.println("  input is accepted; return term=" + output.utf8ToString());
return|return
name|output
return|;
block|}
else|else
block|{
name|nextState
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nextState
operator|==
operator|-
literal|1
condition|)
block|{
comment|// Pop back to a state that has a transition
comment|//<= our label:
while|while
condition|(
literal|true
condition|)
block|{
name|Transition
index|[]
name|transitions
init|=
name|sortedTransitions
index|[
name|state
index|]
decl_stmt|;
if|if
condition|(
name|transitions
operator|.
name|length
operator|==
literal|0
condition|)
block|{
assert|assert
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
assert|;
name|output
operator|.
name|length
operator|=
name|idx
expr_stmt|;
comment|//if (DEBUG) System.out.println("  return " + output.utf8ToString());
return|return
name|output
return|;
block|}
elseif|else
if|if
condition|(
name|label
operator|-
literal|1
operator|<
name|transitions
index|[
literal|0
index|]
operator|.
name|min
condition|)
block|{
if|if
condition|(
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|state
argument_list|)
condition|)
block|{
name|output
operator|.
name|length
operator|=
name|idx
expr_stmt|;
comment|//if (DEBUG) System.out.println("  return " + output.utf8ToString());
return|return
name|output
return|;
block|}
comment|// pop
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|//if (DEBUG) System.out.println("  pop ord=" + idx + " return null");
return|return
literal|null
return|;
block|}
else|else
block|{
name|state
operator|=
name|stack
operator|.
name|remove
argument_list|(
name|stack
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|idx
operator|--
expr_stmt|;
comment|//if (DEBUG) System.out.println("  pop ord=" + (idx+1) + " label=" + (char) label + " first trans.min=" + (char) transitions[0].min);
name|label
operator|=
name|input
operator|.
name|bytes
index|[
name|input
operator|.
name|offset
operator|+
name|idx
index|]
operator|&
literal|0xff
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//if (DEBUG) System.out.println("  stop pop ord=" + idx + " first trans.min=" + (char) transitions[0].min);
break|break;
block|}
block|}
comment|//if (DEBUG) System.out.println("  label=" + (char) label + " idx=" + idx);
return|return
name|addTail
argument_list|(
name|state
argument_list|,
name|output
argument_list|,
name|idx
argument_list|,
name|label
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|idx
operator|>=
name|output
operator|.
name|bytes
operator|.
name|length
condition|)
block|{
name|output
operator|.
name|grow
argument_list|(
literal|1
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|bytes
index|[
name|idx
index|]
operator|=
operator|(
name|byte
operator|)
name|label
expr_stmt|;
name|stack
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|state
operator|=
name|nextState
expr_stmt|;
name|idx
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|method|toDot
specifier|public
name|String
name|toDot
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"digraph CompiledAutomaton {\n"
argument_list|)
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"  rankdir = LR;\n"
argument_list|)
expr_stmt|;
name|int
name|initial
init|=
name|runAutomaton
operator|.
name|getInitialState
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
name|sortedTransitions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|runAutomaton
operator|.
name|isAccept
argument_list|(
name|i
argument_list|)
condition|)
name|b
operator|.
name|append
argument_list|(
literal|" [shape=doublecircle,label=\"\"];\n"
argument_list|)
expr_stmt|;
else|else
name|b
operator|.
name|append
argument_list|(
literal|" [shape=circle,label=\"\"];\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|initial
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"  initial [shape=plaintext,label=\"\"];\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"  initial -> "
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sortedTransitions
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|sortedTransitions
index|[
name|i
index|]
index|[
name|j
index|]
operator|.
name|appendDot
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|b
operator|.
name|append
argument_list|(
literal|"}\n"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|runAutomaton
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|runAutomaton
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|term
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|term
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|type
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|type
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|CompiledAutomaton
name|other
init|=
operator|(
name|CompiledAutomaton
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|other
operator|.
name|type
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|type
operator|==
name|AUTOMATON_TYPE
operator|.
name|SINGLE
operator|||
name|type
operator|==
name|AUTOMATON_TYPE
operator|.
name|PREFIX
condition|)
block|{
if|if
condition|(
operator|!
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|AUTOMATON_TYPE
operator|.
name|NORMAL
condition|)
block|{
if|if
condition|(
operator|!
name|runAutomaton
operator|.
name|equals
argument_list|(
name|other
operator|.
name|runAutomaton
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
