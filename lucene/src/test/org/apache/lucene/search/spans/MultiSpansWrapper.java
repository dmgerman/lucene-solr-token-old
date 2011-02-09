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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collection
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DocsEnum
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
operator|.
name|ReaderContext
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
name|ReaderUtil
import|;
end_import
begin_comment
comment|/**  *   * A wrapper to perform span operations on a non-leaf reader context  *<p>  * NOTE: This should be used for testing purposes only  * @lucene.internal  */
end_comment
begin_class
DECL|class|MultiSpansWrapper
specifier|public
class|class
name|MultiSpansWrapper
extends|extends
name|Spans
block|{
comment|// can't be package private due to payloads
DECL|field|query
specifier|private
name|SpanQuery
name|query
decl_stmt|;
DECL|field|leaves
specifier|private
name|AtomicReaderContext
index|[]
name|leaves
decl_stmt|;
DECL|field|leafOrd
specifier|private
name|int
name|leafOrd
init|=
literal|0
decl_stmt|;
DECL|field|current
specifier|private
name|Spans
name|current
decl_stmt|;
DECL|method|MultiSpansWrapper
specifier|private
name|MultiSpansWrapper
parameter_list|(
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|,
name|SpanQuery
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|leaves
operator|=
name|leaves
expr_stmt|;
block|}
DECL|method|wrap
specifier|public
specifier|static
name|Spans
name|wrap
parameter_list|(
name|ReaderContext
name|topLevelReaderContext
parameter_list|,
name|SpanQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|AtomicReaderContext
index|[]
name|leaves
init|=
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|topLevelReaderContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|leaves
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|query
operator|.
name|getSpans
argument_list|(
name|leaves
index|[
literal|0
index|]
argument_list|)
return|;
block|}
return|return
operator|new
name|MultiSpansWrapper
argument_list|(
name|leaves
argument_list|,
name|query
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|leafOrd
operator|>=
name|leaves
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|current
operator|=
name|query
operator|.
name|getSpans
argument_list|(
name|leaves
index|[
name|leafOrd
index|]
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|++
name|leafOrd
operator|<
name|leaves
operator|.
name|length
condition|)
block|{
name|current
operator|=
name|query
operator|.
name|getSpans
argument_list|(
name|leaves
index|[
name|leafOrd
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
break|break;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|leafOrd
operator|>=
name|leaves
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|subIndex
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|target
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
assert|assert
name|subIndex
operator|>=
name|leafOrd
assert|;
if|if
condition|(
name|subIndex
operator|!=
name|leafOrd
condition|)
block|{
name|current
operator|=
name|query
operator|.
name|getSpans
argument_list|(
name|leaves
index|[
name|subIndex
index|]
argument_list|)
expr_stmt|;
name|leafOrd
operator|=
name|subIndex
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|current
operator|=
name|query
operator|.
name|getSpans
argument_list|(
name|leaves
index|[
name|leafOrd
index|]
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|skipTo
argument_list|(
name|target
operator|-
name|leaves
index|[
name|leafOrd
index|]
operator|.
name|docBase
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|++
name|leafOrd
operator|<
name|leaves
operator|.
name|length
condition|)
block|{
name|current
operator|=
name|query
operator|.
name|getSpans
argument_list|(
name|leaves
index|[
name|leafOrd
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
break|break;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
return|return
name|DocsEnum
operator|.
name|NO_MORE_DOCS
return|;
block|}
return|return
name|current
operator|.
name|doc
argument_list|()
operator|+
name|leaves
index|[
name|leafOrd
index|]
operator|.
name|docBase
return|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
name|int
name|start
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
return|return
name|DocsEnum
operator|.
name|NO_MORE_DOCS
return|;
block|}
return|return
name|current
operator|.
name|start
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|int
name|end
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
return|return
name|DocsEnum
operator|.
name|NO_MORE_DOCS
return|;
block|}
return|return
name|current
operator|.
name|end
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
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
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
return|return
name|current
operator|.
name|getPayload
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|current
operator|.
name|isPayloadAvailable
argument_list|()
return|;
block|}
block|}
end_class
end_unit
