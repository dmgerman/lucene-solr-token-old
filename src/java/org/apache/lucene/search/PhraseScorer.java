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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|*
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
name|*
import|;
end_import
begin_class
DECL|class|PhraseScorer
specifier|abstract
class|class
name|PhraseScorer
extends|extends
name|Scorer
block|{
DECL|field|weight
specifier|private
name|Weight
name|weight
decl_stmt|;
DECL|field|norms
specifier|protected
name|byte
index|[]
name|norms
decl_stmt|;
DECL|field|value
specifier|protected
name|float
name|value
decl_stmt|;
DECL|field|firstTime
specifier|private
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
DECL|field|more
specifier|private
name|boolean
name|more
init|=
literal|true
decl_stmt|;
DECL|field|pq
specifier|protected
name|PhraseQueue
name|pq
decl_stmt|;
DECL|field|first
DECL|field|last
specifier|protected
name|PhrasePositions
name|first
decl_stmt|,
name|last
decl_stmt|;
DECL|field|freq
specifier|private
name|float
name|freq
decl_stmt|;
DECL|method|PhraseScorer
name|PhraseScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|TermPositions
index|[]
name|tps
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|byte
index|[]
name|norms
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|weight
operator|.
name|getValue
argument_list|()
expr_stmt|;
comment|// convert tps to a list
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tps
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PhrasePositions
name|pp
init|=
operator|new
name|PhrasePositions
argument_list|(
name|tps
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
comment|// add next to end of list
name|last
operator|.
name|next
operator|=
name|pp
expr_stmt|;
block|}
else|else
name|first
operator|=
name|pp
expr_stmt|;
name|last
operator|=
name|pp
expr_stmt|;
block|}
name|pq
operator|=
operator|new
name|PhraseQueue
argument_list|(
name|tps
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// construct empty pq
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|first
operator|.
name|doc
return|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|firstTime
condition|)
block|{
name|sort
argument_list|()
expr_stmt|;
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|more
condition|)
block|{
name|more
operator|=
name|last
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// trigger further scanning
block|}
while|while
condition|(
name|more
condition|)
block|{
while|while
condition|(
name|more
operator|&&
name|first
operator|.
name|doc
operator|<
name|last
operator|.
name|doc
condition|)
block|{
comment|// find doc w/ all the terms
name|more
operator|=
name|first
operator|.
name|skipTo
argument_list|(
name|last
operator|.
name|doc
argument_list|)
expr_stmt|;
comment|// skip first upto last
name|firstToLast
argument_list|()
expr_stmt|;
comment|// and move it to the end
block|}
if|if
condition|(
name|more
condition|)
block|{
comment|// found a doc with all of the terms
name|freq
operator|=
name|phraseFreq
argument_list|()
expr_stmt|;
comment|// check for phrase
if|if
condition|(
name|freq
operator|==
literal|0.0f
condition|)
comment|// no match
name|more
operator|=
name|last
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// trigger further scanning
else|else
return|return
literal|true
return|;
comment|// found a match
block|}
block|}
return|return
literal|false
return|;
comment|// no more matches
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("scoring " + first.doc);
name|float
name|raw
init|=
name|getSimilarity
argument_list|()
operator|.
name|tf
argument_list|(
name|freq
argument_list|)
operator|*
name|value
decl_stmt|;
comment|// raw score
return|return
name|raw
operator|*
name|Similarity
operator|.
name|decodeNorm
argument_list|(
name|norms
index|[
name|first
operator|.
name|doc
index|]
argument_list|)
return|;
comment|// normalize
block|}
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|first
init|;
name|more
operator|&&
name|pp
operator|!=
literal|null
condition|;
name|pp
operator|=
name|pp
operator|.
name|next
control|)
block|{
name|more
operator|=
name|pp
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|more
condition|)
name|sort
argument_list|()
expr_stmt|;
comment|// re-sort
return|return
name|more
return|;
block|}
DECL|method|phraseFreq
specifier|protected
specifier|abstract
name|float
name|phraseFreq
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|sort
specifier|private
name|void
name|sort
parameter_list|()
throws|throws
name|IOException
block|{
name|pq
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|first
init|;
name|more
operator|&&
name|pp
operator|!=
literal|null
condition|;
name|pp
operator|=
name|pp
operator|.
name|next
control|)
block|{
name|more
operator|=
name|pp
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|more
condition|)
block|{
name|pq
operator|.
name|put
argument_list|(
name|pp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
block|}
name|pqToList
argument_list|()
expr_stmt|;
block|}
DECL|method|pqToList
specifier|protected
specifier|final
name|void
name|pqToList
parameter_list|()
block|{
name|last
operator|=
name|first
operator|=
literal|null
expr_stmt|;
while|while
condition|(
name|pq
operator|.
name|top
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|PhrasePositions
name|pp
init|=
operator|(
name|PhrasePositions
operator|)
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
comment|// add next to end of list
name|last
operator|.
name|next
operator|=
name|pp
expr_stmt|;
block|}
else|else
name|first
operator|=
name|pp
expr_stmt|;
name|last
operator|=
name|pp
expr_stmt|;
name|pp
operator|.
name|next
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|firstToLast
specifier|protected
specifier|final
name|void
name|firstToLast
parameter_list|()
block|{
name|last
operator|.
name|next
operator|=
name|first
expr_stmt|;
comment|// move first to end of list
name|last
operator|=
name|first
expr_stmt|;
name|first
operator|=
name|first
operator|.
name|next
expr_stmt|;
name|last
operator|.
name|next
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|tfExplanation
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
argument_list|()
operator|&&
name|doc
argument_list|()
operator|<
name|doc
condition|)
block|{}
name|float
name|phraseFreq
init|=
operator|(
name|doc
argument_list|()
operator|==
name|doc
operator|)
condition|?
name|freq
else|:
literal|0.0f
decl_stmt|;
name|tfExplanation
operator|.
name|setValue
argument_list|(
name|getSimilarity
argument_list|()
operator|.
name|tf
argument_list|(
name|phraseFreq
argument_list|)
argument_list|)
expr_stmt|;
name|tfExplanation
operator|.
name|setDescription
argument_list|(
literal|"tf(phraseFreq="
operator|+
name|phraseFreq
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|tfExplanation
return|;
block|}
block|}
end_class
end_unit
