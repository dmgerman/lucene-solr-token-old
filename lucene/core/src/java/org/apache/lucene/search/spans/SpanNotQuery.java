begin_unit
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|AtomicReaderContext
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
name|IndexReader
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
name|search
operator|.
name|Query
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
name|Bits
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
name|TermContext
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
name|ToStringUtils
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
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
begin_comment
comment|/** Removes matches which overlap with another SpanQuery. */
end_comment
begin_class
DECL|class|SpanNotQuery
specifier|public
class|class
name|SpanNotQuery
extends|extends
name|SpanQuery
implements|implements
name|Cloneable
block|{
DECL|field|include
specifier|private
name|SpanQuery
name|include
decl_stmt|;
DECL|field|exclude
specifier|private
name|SpanQuery
name|exclude
decl_stmt|;
comment|/** Construct a SpanNotQuery matching spans from<code>include</code> which    * have no overlap with spans from<code>exclude</code>.*/
DECL|method|SpanNotQuery
specifier|public
name|SpanNotQuery
parameter_list|(
name|SpanQuery
name|include
parameter_list|,
name|SpanQuery
name|exclude
parameter_list|)
block|{
name|this
operator|.
name|include
operator|=
name|include
expr_stmt|;
name|this
operator|.
name|exclude
operator|=
name|exclude
expr_stmt|;
if|if
condition|(
operator|!
name|include
operator|.
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|exclude
operator|.
name|getField
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Clauses must have same field."
argument_list|)
throw|;
block|}
comment|/** Return the SpanQuery whose matches are filtered. */
DECL|method|getInclude
specifier|public
name|SpanQuery
name|getInclude
parameter_list|()
block|{
return|return
name|include
return|;
block|}
comment|/** Return the SpanQuery whose matches must not overlap those returned. */
DECL|method|getExclude
specifier|public
name|SpanQuery
name|getExclude
parameter_list|()
block|{
return|return
name|exclude
return|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|include
operator|.
name|getField
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|include
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"spanNot("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|include
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|exclude
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SpanNotQuery
name|clone
parameter_list|()
block|{
name|SpanNotQuery
name|spanNotQuery
init|=
operator|new
name|SpanNotQuery
argument_list|(
operator|(
name|SpanQuery
operator|)
name|include
operator|.
name|clone
argument_list|()
argument_list|,
operator|(
name|SpanQuery
operator|)
name|exclude
operator|.
name|clone
argument_list|()
argument_list|)
decl_stmt|;
name|spanNotQuery
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spanNotQuery
return|;
block|}
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|AtomicReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|termContexts
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Spans
argument_list|()
block|{
specifier|private
name|Spans
name|includeSpans
init|=
name|include
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|termContexts
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|moreInclude
init|=
literal|true
decl_stmt|;
specifier|private
name|Spans
name|excludeSpans
init|=
name|exclude
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|termContexts
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|moreExclude
init|=
name|excludeSpans
operator|.
name|next
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|moreInclude
condition|)
comment|// move to next include
name|moreInclude
operator|=
name|includeSpans
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|moreInclude
operator|&&
name|moreExclude
condition|)
block|{
if|if
condition|(
name|includeSpans
operator|.
name|doc
argument_list|()
operator|>
name|excludeSpans
operator|.
name|doc
argument_list|()
condition|)
comment|// skip exclude
name|moreExclude
operator|=
name|excludeSpans
operator|.
name|skipTo
argument_list|(
name|includeSpans
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|moreExclude
comment|// while exclude is before
operator|&&
name|includeSpans
operator|.
name|doc
argument_list|()
operator|==
name|excludeSpans
operator|.
name|doc
argument_list|()
operator|&&
name|excludeSpans
operator|.
name|end
argument_list|()
operator|<=
name|includeSpans
operator|.
name|start
argument_list|()
condition|)
block|{
name|moreExclude
operator|=
name|excludeSpans
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// increment exclude
block|}
if|if
condition|(
operator|!
name|moreExclude
comment|// if no intersection
operator|||
name|includeSpans
operator|.
name|doc
argument_list|()
operator|!=
name|excludeSpans
operator|.
name|doc
argument_list|()
operator|||
name|includeSpans
operator|.
name|end
argument_list|()
operator|<=
name|excludeSpans
operator|.
name|start
argument_list|()
condition|)
break|break;
comment|// we found a match
name|moreInclude
operator|=
name|includeSpans
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// intersected: keep scanning
block|}
return|return
name|moreInclude
return|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|moreInclude
condition|)
comment|// skip include
name|moreInclude
operator|=
name|includeSpans
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|moreInclude
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|moreExclude
comment|// skip exclude
operator|&&
name|includeSpans
operator|.
name|doc
argument_list|()
operator|>
name|excludeSpans
operator|.
name|doc
argument_list|()
condition|)
name|moreExclude
operator|=
name|excludeSpans
operator|.
name|skipTo
argument_list|(
name|includeSpans
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|moreExclude
comment|// while exclude is before
operator|&&
name|includeSpans
operator|.
name|doc
argument_list|()
operator|==
name|excludeSpans
operator|.
name|doc
argument_list|()
operator|&&
name|excludeSpans
operator|.
name|end
argument_list|()
operator|<=
name|includeSpans
operator|.
name|start
argument_list|()
condition|)
block|{
name|moreExclude
operator|=
name|excludeSpans
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// increment exclude
block|}
if|if
condition|(
operator|!
name|moreExclude
comment|// if no intersection
operator|||
name|includeSpans
operator|.
name|doc
argument_list|()
operator|!=
name|excludeSpans
operator|.
name|doc
argument_list|()
operator|||
name|includeSpans
operator|.
name|end
argument_list|()
operator|<=
name|excludeSpans
operator|.
name|start
argument_list|()
condition|)
return|return
literal|true
return|;
comment|// we found a match
return|return
name|next
argument_list|()
return|;
comment|// scan to next match
block|}
annotation|@
name|Override
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|includeSpans
operator|.
name|doc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
name|includeSpans
operator|.
name|start
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|end
parameter_list|()
block|{
return|return
name|includeSpans
operator|.
name|end
argument_list|()
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|includeSpans
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|result
operator|=
operator|new
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|(
name|includeSpans
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
annotation|@
name|Override
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
return|return
name|includeSpans
operator|.
name|isPayloadAvailable
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"spans("
operator|+
name|SpanNotQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanNotQuery
name|clone
init|=
literal|null
decl_stmt|;
name|SpanQuery
name|rewrittenInclude
init|=
operator|(
name|SpanQuery
operator|)
name|include
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewrittenInclude
operator|!=
name|include
condition|)
block|{
name|clone
operator|=
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|include
operator|=
name|rewrittenInclude
expr_stmt|;
block|}
name|SpanQuery
name|rewrittenExclude
init|=
operator|(
name|SpanQuery
operator|)
name|exclude
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewrittenExclude
operator|!=
name|exclude
condition|)
block|{
if|if
condition|(
name|clone
operator|==
literal|null
condition|)
name|clone
operator|=
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|exclude
operator|=
name|rewrittenExclude
expr_stmt|;
block|}
if|if
condition|(
name|clone
operator|!=
literal|null
condition|)
block|{
return|return
name|clone
return|;
comment|// some clauses rewrote
block|}
else|else
block|{
return|return
name|this
return|;
comment|// no clauses rewrote
block|}
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SpanNotQuery
operator|)
condition|)
return|return
literal|false
return|;
name|SpanNotQuery
name|other
init|=
operator|(
name|SpanNotQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|include
operator|.
name|equals
argument_list|(
name|other
operator|.
name|include
argument_list|)
operator|&&
name|this
operator|.
name|exclude
operator|.
name|equals
argument_list|(
name|other
operator|.
name|exclude
argument_list|)
operator|&&
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
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
name|int
name|h
init|=
name|include
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|=
operator|(
name|h
operator|<<
literal|1
operator|)
operator||
operator|(
name|h
operator|>>>
literal|31
operator|)
expr_stmt|;
comment|// rotate left
name|h
operator|^=
name|exclude
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
operator|(
name|h
operator|<<
literal|1
operator|)
operator||
operator|(
name|h
operator|>>>
literal|31
operator|)
expr_stmt|;
comment|// rotate left
name|h
operator|^=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class
end_unit
