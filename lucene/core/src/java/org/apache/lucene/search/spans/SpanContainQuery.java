begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|search
operator|.
name|Query
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
begin_class
DECL|class|SpanContainQuery
specifier|abstract
class|class
name|SpanContainQuery
extends|extends
name|SpanQuery
implements|implements
name|Cloneable
block|{
DECL|field|big
name|SpanQuery
name|big
decl_stmt|;
DECL|field|little
name|SpanQuery
name|little
decl_stmt|;
DECL|method|SpanContainQuery
name|SpanContainQuery
parameter_list|(
name|SpanQuery
name|big
parameter_list|,
name|SpanQuery
name|little
parameter_list|)
block|{
name|this
operator|.
name|big
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|big
argument_list|)
expr_stmt|;
name|this
operator|.
name|little
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|little
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|big
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|little
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|big
operator|.
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|little
operator|.
name|getField
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"big and little not same field"
argument_list|)
throw|;
block|}
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
name|big
operator|.
name|getField
argument_list|()
return|;
block|}
DECL|method|getBig
specifier|public
name|SpanQuery
name|getBig
parameter_list|()
block|{
return|return
name|big
return|;
block|}
DECL|method|getLittle
specifier|public
name|SpanQuery
name|getLittle
parameter_list|()
block|{
return|return
name|little
return|;
block|}
DECL|class|SpanContainWeight
specifier|public
specifier|abstract
class|class
name|SpanContainWeight
extends|extends
name|SpanWeight
block|{
DECL|field|bigWeight
specifier|final
name|SpanWeight
name|bigWeight
decl_stmt|;
DECL|field|littleWeight
specifier|final
name|SpanWeight
name|littleWeight
decl_stmt|;
DECL|method|SpanContainWeight
specifier|public
name|SpanContainWeight
parameter_list|(
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
parameter_list|,
name|SpanWeight
name|bigWeight
parameter_list|,
name|SpanWeight
name|littleWeight
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|SpanContainQuery
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
name|bigWeight
operator|=
name|bigWeight
expr_stmt|;
name|this
operator|.
name|littleWeight
operator|=
name|littleWeight
expr_stmt|;
block|}
comment|/**      * Extract terms from both<code>big</code> and<code>little</code>.      */
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
name|bigWeight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|littleWeight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
DECL|method|prepareConjunction
name|ArrayList
argument_list|<
name|Spans
argument_list|>
name|prepareConjunction
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
name|Postings
name|postings
parameter_list|)
throws|throws
name|IOException
block|{
name|Spans
name|bigSpans
init|=
name|bigWeight
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|postings
argument_list|)
decl_stmt|;
if|if
condition|(
name|bigSpans
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Spans
name|littleSpans
init|=
name|littleWeight
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|postings
argument_list|)
decl_stmt|;
if|if
condition|(
name|littleSpans
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ArrayList
argument_list|<
name|Spans
argument_list|>
name|bigAndLittle
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|bigAndLittle
operator|.
name|add
argument_list|(
name|bigSpans
argument_list|)
expr_stmt|;
name|bigAndLittle
operator|.
name|add
argument_list|(
name|littleSpans
argument_list|)
expr_stmt|;
return|return
name|bigAndLittle
return|;
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
name|bigWeight
operator|.
name|extractTermContexts
argument_list|(
name|contexts
argument_list|)
expr_stmt|;
name|littleWeight
operator|.
name|extractTermContexts
argument_list|(
name|contexts
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|name
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
name|name
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|big
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
name|little
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
return|return
name|buffer
operator|.
name|toString
argument_list|()
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
name|SpanQuery
name|rewrittenBig
init|=
operator|(
name|SpanQuery
operator|)
name|big
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|SpanQuery
name|rewrittenLittle
init|=
operator|(
name|SpanQuery
operator|)
name|little
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|big
operator|!=
name|rewrittenBig
operator|||
name|little
operator|!=
name|rewrittenLittle
condition|)
block|{
try|try
block|{
name|SpanContainQuery
name|clone
init|=
operator|(
name|SpanContainQuery
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|big
operator|=
name|rewrittenBig
expr_stmt|;
name|clone
operator|.
name|little
operator|=
name|rewrittenLittle
expr_stmt|;
return|return
name|clone
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
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
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|SpanContainQuery
name|other
parameter_list|)
block|{
return|return
name|big
operator|.
name|equals
argument_list|(
name|other
operator|.
name|big
argument_list|)
operator|&&
name|little
operator|.
name|equals
argument_list|(
name|other
operator|.
name|little
argument_list|)
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
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|classHash
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|h
operator|^=
name|big
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|h
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|h
operator|^=
name|little
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class
end_unit
