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
name|IndexReaderContext
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
name|LeafReaderContext
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
name|PostingsEnum
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
name|ReaderUtil
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
name|index
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
name|index
operator|.
name|TermState
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
name|IndexSearcher
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
name|Collections
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
name|Objects
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
comment|/** Matches spans containing a term.  * This should not be used for terms that are indexed at position Integer.MAX_VALUE.  */
end_comment
begin_class
DECL|class|SpanTermQuery
specifier|public
class|class
name|SpanTermQuery
extends|extends
name|SpanQuery
block|{
DECL|field|term
specifier|protected
specifier|final
name|Term
name|term
decl_stmt|;
DECL|field|termContext
specifier|protected
specifier|final
name|TermContext
name|termContext
decl_stmt|;
comment|/** Construct a SpanTermQuery matching the named term's spans. */
DECL|method|SpanTermQuery
specifier|public
name|SpanTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|this
operator|.
name|termContext
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Expert: Construct a SpanTermQuery matching the named term's spans, using    * the provided TermContext    */
DECL|method|SpanTermQuery
specifier|public
name|SpanTermQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|TermContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|this
operator|.
name|termContext
operator|=
name|context
expr_stmt|;
block|}
comment|/** Return the term whose spans are matched. */
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
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
name|term
operator|.
name|field
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|SpanWeight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermContext
name|context
decl_stmt|;
specifier|final
name|IndexReaderContext
name|topContext
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|termContext
operator|==
literal|null
operator|||
name|termContext
operator|.
name|topReaderContext
operator|!=
name|topContext
condition|)
block|{
name|context
operator|=
name|TermContext
operator|.
name|build
argument_list|(
name|topContext
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|=
name|termContext
expr_stmt|;
block|}
return|return
operator|new
name|SpanTermWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|,
name|needsScores
condition|?
name|Collections
operator|.
name|singletonMap
argument_list|(
name|term
argument_list|,
name|context
argument_list|)
else|:
literal|null
argument_list|)
return|;
block|}
DECL|class|SpanTermWeight
specifier|public
class|class
name|SpanTermWeight
extends|extends
name|SpanWeight
block|{
DECL|field|termContext
specifier|final
name|TermContext
name|termContext
decl_stmt|;
DECL|method|SpanTermWeight
specifier|public
name|SpanTermWeight
parameter_list|(
name|TermContext
name|termContext
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|SpanTermQuery
operator|.
name|this
argument_list|,
name|searcher
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|this
operator|.
name|termContext
operator|=
name|termContext
expr_stmt|;
assert|assert
name|termContext
operator|!=
literal|null
operator|:
literal|"TermContext must not be null"
assert|;
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
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractTermContexts
specifier|public
name|void
name|extractTermContexts
parameter_list|(
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|contexts
parameter_list|)
block|{
name|contexts
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|termContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
name|Postings
name|requiredPostings
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|termContext
operator|.
name|topReaderContext
operator|==
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|context
argument_list|)
operator|:
literal|"The top-reader used to create Weight ("
operator|+
name|termContext
operator|.
name|topReaderContext
operator|+
literal|") is not the same as the current reader's top-reader ("
operator|+
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|context
argument_list|)
assert|;
specifier|final
name|TermState
name|state
init|=
name|termContext
operator|.
name|get
argument_list|(
name|context
operator|.
name|ord
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
comment|// term is not present in that reader
assert|assert
name|context
operator|.
name|reader
argument_list|()
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
operator|==
literal|0
operator|:
literal|"no termstate found but term exists in reader term="
operator|+
name|term
assert|;
return|return
literal|null
return|;
block|}
specifier|final
name|Terms
name|terms
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|terms
operator|.
name|hasPositions
argument_list|()
operator|==
literal|false
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"field \""
operator|+
name|term
operator|.
name|field
argument_list|()
operator|+
literal|"\" was indexed without position data; cannot run SpanTermQuery (term="
operator|+
name|term
operator|.
name|text
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
specifier|final
name|PostingsEnum
name|postings
init|=
name|termsEnum
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
name|requiredPostings
operator|.
name|getRequiredPostings
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|TermSpans
argument_list|(
name|postings
argument_list|,
name|term
argument_list|)
return|;
block|}
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
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|toString
argument_list|()
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
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|term
operator|.
name|hashCode
argument_list|()
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
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SpanTermQuery
name|other
init|=
operator|(
name|SpanTermQuery
operator|)
name|obj
decl_stmt|;
return|return
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
argument_list|)
return|;
block|}
block|}
end_class
end_unit
