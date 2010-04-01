begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|analysis
operator|.
name|Token
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
name|analysis
operator|.
name|TokenFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TypeAttribute
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
name|AttributeSource
import|;
end_import
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
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_comment
comment|/** SynonymFilter handles multi-token synonyms with variable position increment offsets.  *<p>  * The matched tokens from the input stream may be optionally passed through (includeOrig=true)  * or discarded.  If the original tokens are included, the position increments may be modified  * to retain absolute positions after merging with the synonym tokenstream.  *<p>  * Generated synonyms will start at the same position as the first matched source token.  *  * @version $Id$  */
end_comment
begin_class
DECL|class|SynonymFilter
specifier|public
class|class
name|SynonymFilter
extends|extends
name|TokenFilter
block|{
DECL|field|map
specifier|private
specifier|final
name|SynonymMap
name|map
decl_stmt|;
comment|// Map<String, SynonymMap>
DECL|field|replacement
specifier|private
name|Iterator
argument_list|<
name|AttributeSource
argument_list|>
name|replacement
decl_stmt|;
comment|// iterator over generated tokens
DECL|method|SynonymFilter
specifier|public
name|SynonymFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|SynonymMap
name|map
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
comment|// just ensuring these attributes exist...
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/*    * Need to worry about multiple scenarios:    *  - need to go for the longest match    *    a b => foo      #shouldn't match if "a b" is followed by "c d"    *    a b c d => bar    *  - need to backtrack - retry matches for tokens already read    *     a b c d => foo    *       b c => bar    *     If the input stream is "a b c x", one will consume "a b c d"    *     trying to match the first rule... all but "a" should be    *     pushed back so a match may be made on "b c".    *  - don't try and match generated tokens (thus need separate queue)    *    matching is not recursive.    *  - handle optional generation of original tokens in all these cases,    *    merging token streams to preserve token positions.    *  - preserve original positionIncrement of first matched token    */
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
comment|// if there are any generated tokens, return them... don't try any
comment|// matches against them, as we specifically don't want recursion.
if|if
condition|(
name|replacement
operator|!=
literal|null
operator|&&
name|replacement
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|copy
argument_list|(
name|this
argument_list|,
name|replacement
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// common case fast-path of first token not matching anything
name|AttributeSource
name|firstTok
init|=
name|nextTok
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstTok
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|TermAttribute
name|termAtt
init|=
name|firstTok
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|SynonymMap
name|result
init|=
name|map
operator|.
name|submap
operator|!=
literal|null
condition|?
name|map
operator|.
name|submap
operator|.
name|get
argument_list|(
name|termAtt
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|termLength
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|copy
argument_list|(
name|this
argument_list|,
name|firstTok
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// fast-path failed, clone ourselves if needed
if|if
condition|(
name|firstTok
operator|==
name|this
condition|)
name|firstTok
operator|=
name|cloneAttributes
argument_list|()
expr_stmt|;
comment|// OK, we matched a token, so find the longest match.
name|matched
operator|=
operator|new
name|LinkedList
argument_list|<
name|AttributeSource
argument_list|>
argument_list|()
expr_stmt|;
name|result
operator|=
name|match
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
comment|// no match, simply return the first token read.
name|copy
argument_list|(
name|this
argument_list|,
name|firstTok
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// reuse, or create new one each time?
name|ArrayList
argument_list|<
name|AttributeSource
argument_list|>
name|generated
init|=
operator|new
name|ArrayList
argument_list|<
name|AttributeSource
argument_list|>
argument_list|(
name|result
operator|.
name|synonyms
operator|.
name|length
operator|+
name|matched
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|//
comment|// there was a match... let's generate the new tokens, merging
comment|// in the matched tokens (position increments need adjusting)
comment|//
name|AttributeSource
name|lastTok
init|=
name|matched
operator|.
name|isEmpty
argument_list|()
condition|?
name|firstTok
else|:
name|matched
operator|.
name|getLast
argument_list|()
decl_stmt|;
name|boolean
name|includeOrig
init|=
name|result
operator|.
name|includeOrig
argument_list|()
decl_stmt|;
name|AttributeSource
name|origTok
init|=
name|includeOrig
condition|?
name|firstTok
else|:
literal|null
decl_stmt|;
name|PositionIncrementAttribute
name|firstPosIncAtt
init|=
name|firstTok
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|origPos
init|=
name|firstPosIncAtt
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
comment|// position of origTok in the original stream
name|int
name|repPos
init|=
literal|0
decl_stmt|;
comment|// curr position in replacement token stream
name|int
name|pos
init|=
literal|0
decl_stmt|;
comment|// current position in merged token stream
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|synonyms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Token
name|repTok
init|=
name|result
operator|.
name|synonyms
index|[
name|i
index|]
decl_stmt|;
name|AttributeSource
name|newTok
init|=
name|firstTok
operator|.
name|cloneAttributes
argument_list|()
decl_stmt|;
name|TermAttribute
name|newTermAtt
init|=
name|newTok
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|newOffsetAtt
init|=
name|newTok
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|newPosIncAtt
init|=
name|newTok
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|lastOffsetAtt
init|=
name|lastTok
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|newOffsetAtt
operator|.
name|setOffset
argument_list|(
name|newOffsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|lastOffsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|newTermAtt
operator|.
name|setTermBuffer
argument_list|(
name|repTok
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|repTok
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|repPos
operator|+=
name|repTok
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
name|repPos
operator|=
name|origPos
expr_stmt|;
comment|// make position of first token equal to original
comment|// if necessary, insert original tokens and adjust position increment
while|while
condition|(
name|origTok
operator|!=
literal|null
operator|&&
name|origPos
operator|<=
name|repPos
condition|)
block|{
name|PositionIncrementAttribute
name|origPosInc
init|=
name|origTok
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|origPosInc
operator|.
name|setPositionIncrement
argument_list|(
name|origPos
operator|-
name|pos
argument_list|)
expr_stmt|;
name|generated
operator|.
name|add
argument_list|(
name|origTok
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|origPosInc
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
name|origTok
operator|=
name|matched
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|matched
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
if|if
condition|(
name|origTok
operator|!=
literal|null
condition|)
block|{
name|origPosInc
operator|=
name|origTok
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|origPos
operator|+=
name|origPosInc
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
name|newPosIncAtt
operator|.
name|setPositionIncrement
argument_list|(
name|repPos
operator|-
name|pos
argument_list|)
expr_stmt|;
name|generated
operator|.
name|add
argument_list|(
name|newTok
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|newPosIncAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
comment|// finish up any leftover original tokens
while|while
condition|(
name|origTok
operator|!=
literal|null
condition|)
block|{
name|PositionIncrementAttribute
name|origPosInc
init|=
name|origTok
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|origPosInc
operator|.
name|setPositionIncrement
argument_list|(
name|origPos
operator|-
name|pos
argument_list|)
expr_stmt|;
name|generated
operator|.
name|add
argument_list|(
name|origTok
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|origPosInc
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
name|origTok
operator|=
name|matched
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|matched
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
if|if
condition|(
name|origTok
operator|!=
literal|null
condition|)
block|{
name|origPosInc
operator|=
name|origTok
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|origPos
operator|+=
name|origPosInc
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
comment|// what if we replaced a longer sequence with a shorter one?
comment|// a/0 b/5 =>  foo/0
comment|// should I re-create the gap on the next buffered token?
name|replacement
operator|=
name|generated
operator|.
name|iterator
argument_list|()
expr_stmt|;
comment|// Now return to the top of the loop to read and return the first
comment|// generated token.. The reason this is done is that we may have generated
comment|// nothing at all, and may need to continue with more matching logic.
block|}
block|}
comment|//
comment|// Defer creation of the buffer until the first time it is used to
comment|// optimize short fields with no matches.
comment|//
DECL|field|buffer
specifier|private
name|LinkedList
argument_list|<
name|AttributeSource
argument_list|>
name|buffer
decl_stmt|;
DECL|field|matched
specifier|private
name|LinkedList
argument_list|<
name|AttributeSource
argument_list|>
name|matched
decl_stmt|;
DECL|method|nextTok
specifier|private
name|AttributeSource
name|nextTok
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|buffer
operator|!=
literal|null
operator|&&
operator|!
name|buffer
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|buffer
operator|.
name|removeFirst
argument_list|()
return|;
block|}
else|else
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
block|}
DECL|method|pushTok
specifier|private
name|void
name|pushTok
parameter_list|(
name|AttributeSource
name|t
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
name|buffer
operator|=
operator|new
name|LinkedList
argument_list|<
name|AttributeSource
argument_list|>
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|addFirst
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
DECL|method|match
specifier|private
name|SynonymMap
name|match
parameter_list|(
name|SynonymMap
name|map
parameter_list|)
throws|throws
name|IOException
block|{
name|SynonymMap
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|submap
operator|!=
literal|null
condition|)
block|{
name|AttributeSource
name|tok
init|=
name|nextTok
argument_list|()
decl_stmt|;
if|if
condition|(
name|tok
operator|!=
literal|null
condition|)
block|{
comment|// clone ourselves.
if|if
condition|(
name|tok
operator|==
name|this
condition|)
name|tok
operator|=
name|cloneAttributes
argument_list|()
expr_stmt|;
comment|// check for positionIncrement!=1?  if>1, should not match, if==0, check multiple at this level?
name|TermAttribute
name|termAtt
init|=
name|tok
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|SynonymMap
name|subMap
init|=
name|map
operator|.
name|submap
operator|.
name|get
argument_list|(
name|termAtt
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|termLength
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|subMap
operator|!=
literal|null
condition|)
block|{
comment|// recurse
name|result
operator|=
name|match
argument_list|(
name|subMap
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|matched
operator|.
name|addFirst
argument_list|(
name|tok
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// push back unmatched token
name|pushTok
argument_list|(
name|tok
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// if no longer sequence matched, so if this node has synonyms, it's the match.
if|if
condition|(
name|result
operator|==
literal|null
operator|&&
name|map
operator|.
name|synonyms
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|map
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|copy
specifier|private
name|void
name|copy
parameter_list|(
name|AttributeSource
name|target
parameter_list|,
name|AttributeSource
name|source
parameter_list|)
block|{
if|if
condition|(
name|target
operator|!=
name|source
condition|)
name|source
operator|.
name|copyTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
name|replacement
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
