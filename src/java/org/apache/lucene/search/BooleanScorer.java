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
name|index
operator|.
name|*
import|;
end_import
begin_class
DECL|class|BooleanScorer
specifier|final
class|class
name|BooleanScorer
extends|extends
name|Scorer
block|{
DECL|field|currentDoc
specifier|private
name|int
name|currentDoc
decl_stmt|;
DECL|field|scorers
specifier|private
name|SubScorer
name|scorers
init|=
literal|null
decl_stmt|;
DECL|field|bucketTable
specifier|private
name|BucketTable
name|bucketTable
init|=
operator|new
name|BucketTable
argument_list|(
name|this
argument_list|)
decl_stmt|;
DECL|field|maxCoord
specifier|private
name|int
name|maxCoord
init|=
literal|1
decl_stmt|;
DECL|field|coordFactors
specifier|private
name|float
index|[]
name|coordFactors
init|=
literal|null
decl_stmt|;
DECL|field|requiredMask
specifier|private
name|int
name|requiredMask
init|=
literal|0
decl_stmt|;
DECL|field|prohibitedMask
specifier|private
name|int
name|prohibitedMask
init|=
literal|0
decl_stmt|;
DECL|field|nextMask
specifier|private
name|int
name|nextMask
init|=
literal|1
decl_stmt|;
DECL|method|BooleanScorer
name|BooleanScorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
block|}
DECL|class|SubScorer
specifier|static
specifier|final
class|class
name|SubScorer
block|{
DECL|field|scorer
specifier|public
name|Scorer
name|scorer
decl_stmt|;
DECL|field|required
specifier|public
name|boolean
name|required
init|=
literal|false
decl_stmt|;
DECL|field|prohibited
specifier|public
name|boolean
name|prohibited
init|=
literal|false
decl_stmt|;
DECL|field|collector
specifier|public
name|HitCollector
name|collector
decl_stmt|;
DECL|field|next
specifier|public
name|SubScorer
name|next
decl_stmt|;
DECL|method|SubScorer
specifier|public
name|SubScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|,
name|boolean
name|required
parameter_list|,
name|boolean
name|prohibited
parameter_list|,
name|HitCollector
name|collector
parameter_list|,
name|SubScorer
name|next
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|this
operator|.
name|required
operator|=
name|required
expr_stmt|;
name|this
operator|.
name|prohibited
operator|=
name|prohibited
expr_stmt|;
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
block|}
DECL|method|add
specifier|final
name|void
name|add
parameter_list|(
name|Scorer
name|scorer
parameter_list|,
name|boolean
name|required
parameter_list|,
name|boolean
name|prohibited
parameter_list|)
block|{
name|int
name|mask
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|required
operator|||
name|prohibited
condition|)
block|{
if|if
condition|(
name|nextMask
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"More than 32 required/prohibited clauses in query."
argument_list|)
throw|;
name|mask
operator|=
name|nextMask
expr_stmt|;
name|nextMask
operator|=
name|nextMask
operator|<<
literal|1
expr_stmt|;
block|}
else|else
name|mask
operator|=
literal|0
expr_stmt|;
if|if
condition|(
operator|!
name|prohibited
condition|)
name|maxCoord
operator|++
expr_stmt|;
if|if
condition|(
name|prohibited
condition|)
name|prohibitedMask
operator||=
name|mask
expr_stmt|;
comment|// update prohibited mask
elseif|else
if|if
condition|(
name|required
condition|)
name|requiredMask
operator||=
name|mask
expr_stmt|;
comment|// update required mask
name|scorers
operator|=
operator|new
name|SubScorer
argument_list|(
name|scorer
argument_list|,
name|required
argument_list|,
name|prohibited
argument_list|,
name|bucketTable
operator|.
name|newCollector
argument_list|(
name|mask
argument_list|)
argument_list|,
name|scorers
argument_list|)
expr_stmt|;
block|}
DECL|method|computeCoordFactors
specifier|private
specifier|final
name|void
name|computeCoordFactors
parameter_list|()
throws|throws
name|IOException
block|{
name|coordFactors
operator|=
operator|new
name|float
index|[
name|maxCoord
index|]
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
name|maxCoord
condition|;
name|i
operator|++
control|)
name|coordFactors
index|[
name|i
index|]
operator|=
name|getSimilarity
argument_list|()
operator|.
name|coord
argument_list|(
name|i
argument_list|,
name|maxCoord
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|score
specifier|public
specifier|final
name|void
name|score
parameter_list|(
name|HitCollector
name|results
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|coordFactors
operator|==
literal|null
condition|)
name|computeCoordFactors
argument_list|()
expr_stmt|;
while|while
condition|(
name|currentDoc
operator|<
name|maxDoc
condition|)
block|{
name|currentDoc
operator|=
name|Math
operator|.
name|min
argument_list|(
name|currentDoc
operator|+
name|BucketTable
operator|.
name|SIZE
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
for|for
control|(
name|SubScorer
name|t
init|=
name|scorers
init|;
name|t
operator|!=
literal|null
condition|;
name|t
operator|=
name|t
operator|.
name|next
control|)
name|t
operator|.
name|scorer
operator|.
name|score
argument_list|(
name|t
operator|.
name|collector
argument_list|,
name|currentDoc
argument_list|)
expr_stmt|;
name|bucketTable
operator|.
name|collectHits
argument_list|(
name|results
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Bucket
specifier|static
specifier|final
class|class
name|Bucket
block|{
DECL|field|doc
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
comment|// tells if bucket is valid
DECL|field|score
name|float
name|score
decl_stmt|;
comment|// incremental score
DECL|field|bits
name|int
name|bits
decl_stmt|;
comment|// used for bool constraints
DECL|field|coord
name|int
name|coord
decl_stmt|;
comment|// count of terms in score
DECL|field|next
name|Bucket
name|next
decl_stmt|;
comment|// next valid bucket
block|}
comment|/** A simple hash table of document scores within a range. */
DECL|class|BucketTable
specifier|static
specifier|final
class|class
name|BucketTable
block|{
DECL|field|SIZE
specifier|public
specifier|static
specifier|final
name|int
name|SIZE
init|=
literal|1
operator|<<
literal|10
decl_stmt|;
DECL|field|MASK
specifier|public
specifier|static
specifier|final
name|int
name|MASK
init|=
name|SIZE
operator|-
literal|1
decl_stmt|;
DECL|field|buckets
specifier|final
name|Bucket
index|[]
name|buckets
init|=
operator|new
name|Bucket
index|[
name|SIZE
index|]
decl_stmt|;
DECL|field|first
name|Bucket
name|first
init|=
literal|null
decl_stmt|;
comment|// head of valid list
DECL|field|scorer
specifier|private
name|BooleanScorer
name|scorer
decl_stmt|;
DECL|method|BucketTable
specifier|public
name|BucketTable
parameter_list|(
name|BooleanScorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
DECL|method|collectHits
specifier|public
specifier|final
name|void
name|collectHits
parameter_list|(
name|HitCollector
name|results
parameter_list|)
block|{
specifier|final
name|int
name|required
init|=
name|scorer
operator|.
name|requiredMask
decl_stmt|;
specifier|final
name|int
name|prohibited
init|=
name|scorer
operator|.
name|prohibitedMask
decl_stmt|;
specifier|final
name|float
index|[]
name|coord
init|=
name|scorer
operator|.
name|coordFactors
decl_stmt|;
for|for
control|(
name|Bucket
name|bucket
init|=
name|first
init|;
name|bucket
operator|!=
literal|null
condition|;
name|bucket
operator|=
name|bucket
operator|.
name|next
control|)
block|{
if|if
condition|(
operator|(
name|bucket
operator|.
name|bits
operator|&
name|prohibited
operator|)
operator|==
literal|0
operator|&&
comment|// check prohibited
operator|(
name|bucket
operator|.
name|bits
operator|&
name|required
operator|)
operator|==
name|required
condition|)
block|{
comment|// check required
name|results
operator|.
name|collect
argument_list|(
name|bucket
operator|.
name|doc
argument_list|,
comment|// add to results
name|bucket
operator|.
name|score
operator|*
name|coord
index|[
name|bucket
operator|.
name|coord
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|first
operator|=
literal|null
expr_stmt|;
comment|// reset for next round
block|}
DECL|method|size
specifier|public
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|SIZE
return|;
block|}
DECL|method|newCollector
specifier|public
name|HitCollector
name|newCollector
parameter_list|(
name|int
name|mask
parameter_list|)
block|{
return|return
operator|new
name|Collector
argument_list|(
name|mask
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
DECL|class|Collector
specifier|static
specifier|final
class|class
name|Collector
extends|extends
name|HitCollector
block|{
DECL|field|bucketTable
specifier|private
name|BucketTable
name|bucketTable
decl_stmt|;
DECL|field|mask
specifier|private
name|int
name|mask
decl_stmt|;
DECL|method|Collector
specifier|public
name|Collector
parameter_list|(
name|int
name|mask
parameter_list|,
name|BucketTable
name|bucketTable
parameter_list|)
block|{
name|this
operator|.
name|mask
operator|=
name|mask
expr_stmt|;
name|this
operator|.
name|bucketTable
operator|=
name|bucketTable
expr_stmt|;
block|}
DECL|method|collect
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|,
specifier|final
name|float
name|score
parameter_list|)
block|{
specifier|final
name|BucketTable
name|table
init|=
name|bucketTable
decl_stmt|;
specifier|final
name|int
name|i
init|=
name|doc
operator|&
name|BucketTable
operator|.
name|MASK
decl_stmt|;
name|Bucket
name|bucket
init|=
name|table
operator|.
name|buckets
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|bucket
operator|==
literal|null
condition|)
name|table
operator|.
name|buckets
index|[
name|i
index|]
operator|=
name|bucket
operator|=
operator|new
name|Bucket
argument_list|()
expr_stmt|;
if|if
condition|(
name|bucket
operator|.
name|doc
operator|!=
name|doc
condition|)
block|{
comment|// invalid bucket
name|bucket
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
comment|// set doc
name|bucket
operator|.
name|score
operator|=
name|score
expr_stmt|;
comment|// initialize score
name|bucket
operator|.
name|bits
operator|=
name|mask
expr_stmt|;
comment|// initialize mask
name|bucket
operator|.
name|coord
operator|=
literal|1
expr_stmt|;
comment|// initialize coord
name|bucket
operator|.
name|next
operator|=
name|table
operator|.
name|first
expr_stmt|;
comment|// push onto valid list
name|table
operator|.
name|first
operator|=
name|bucket
expr_stmt|;
block|}
else|else
block|{
comment|// valid bucket
name|bucket
operator|.
name|score
operator|+=
name|score
expr_stmt|;
comment|// increment score
name|bucket
operator|.
name|bits
operator||=
name|mask
expr_stmt|;
comment|// add bits in mask
name|bucket
operator|.
name|coord
operator|++
expr_stmt|;
comment|// increment coord
block|}
block|}
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|sumExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|sumExpl
operator|.
name|setDescription
argument_list|(
literal|"sum of:"
argument_list|)
expr_stmt|;
name|int
name|coord
init|=
literal|0
decl_stmt|;
name|float
name|sum
init|=
literal|0.0f
decl_stmt|;
for|for
control|(
name|SubScorer
name|s
init|=
name|scorers
init|;
name|s
operator|!=
literal|null
condition|;
name|s
operator|=
name|s
operator|.
name|next
control|)
block|{
name|Explanation
name|e
init|=
name|s
operator|.
name|scorer
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|s
operator|.
name|prohibited
condition|)
block|{
name|sumExpl
operator|.
name|addDetail
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|sum
operator|+=
name|e
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|coord
operator|++
expr_stmt|;
block|}
else|else
block|{
return|return
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"match prohibited"
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|required
condition|)
block|{
return|return
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"match required"
argument_list|)
return|;
block|}
block|}
name|sumExpl
operator|.
name|setValue
argument_list|(
name|sum
argument_list|)
expr_stmt|;
if|if
condition|(
name|coord
operator|==
literal|1
condition|)
comment|// only one clause matched
name|sumExpl
operator|=
name|sumExpl
operator|.
name|getDetails
argument_list|()
index|[
literal|0
index|]
expr_stmt|;
comment|// eliminate wrapper
name|float
name|coordFactor
init|=
name|getSimilarity
argument_list|()
operator|.
name|coord
argument_list|(
name|coord
argument_list|,
name|maxCoord
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|coordFactor
operator|==
literal|1.0f
condition|)
comment|// coord is no-op
return|return
name|sumExpl
return|;
comment|// eliminate wrapper
else|else
block|{
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"product of:"
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|sumExpl
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|coordFactor
argument_list|,
literal|"coord("
operator|+
name|coord
operator|+
literal|"/"
operator|+
operator|(
name|maxCoord
operator|-
literal|1
operator|)
operator|+
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|sum
operator|*
name|coordFactor
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class
end_unit
