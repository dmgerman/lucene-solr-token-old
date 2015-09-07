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
name|Objects
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
begin_comment
comment|/**  * This is a {@link PhraseQuery} which is optimized for n-gram phrase query.  * For example, when you query "ABCD" on a 2-gram field, you may want to use  * NGramPhraseQuery rather than {@link PhraseQuery}, because NGramPhraseQuery  * will {@link #rewrite(IndexReader)} the query to "AB/0 CD/2", while {@link PhraseQuery}  * will query "AB/0 BC/1 CD/2" (where term/position).  *  */
end_comment
begin_class
DECL|class|NGramPhraseQuery
specifier|public
class|class
name|NGramPhraseQuery
extends|extends
name|Query
block|{
DECL|field|n
specifier|private
specifier|final
name|int
name|n
decl_stmt|;
DECL|field|phraseQuery
specifier|private
specifier|final
name|PhraseQuery
name|phraseQuery
decl_stmt|;
comment|/**    * Constructor that takes gram size.    * @param n n-gram size    */
DECL|method|NGramPhraseQuery
specifier|public
name|NGramPhraseQuery
parameter_list|(
name|int
name|n
parameter_list|,
name|PhraseQuery
name|query
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|n
operator|=
name|n
expr_stmt|;
name|this
operator|.
name|phraseQuery
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|query
argument_list|)
expr_stmt|;
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
specifier|final
name|Term
index|[]
name|terms
init|=
name|phraseQuery
operator|.
name|getTerms
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|positions
init|=
name|phraseQuery
operator|.
name|getPositions
argument_list|()
decl_stmt|;
name|boolean
name|isOptimizable
init|=
name|phraseQuery
operator|.
name|getSlop
argument_list|()
operator|==
literal|0
operator|&&
name|n
operator|>=
literal|2
comment|// non-overlap n-gram cannot be optimized
operator|&&
name|terms
operator|.
name|length
operator|>=
literal|3
decl_stmt|;
comment|// short ones can't be optimized
if|if
condition|(
name|isOptimizable
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|positions
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|positions
index|[
name|i
index|]
operator|!=
name|positions
index|[
name|i
operator|-
literal|1
index|]
operator|+
literal|1
condition|)
block|{
name|isOptimizable
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|isOptimizable
operator|==
literal|false
condition|)
block|{
return|return
name|phraseQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
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
name|terms
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|i
operator|%
name|n
operator|==
literal|0
operator|||
name|i
operator|==
name|terms
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NGramPhraseQuery
name|other
init|=
operator|(
name|NGramPhraseQuery
operator|)
name|o
decl_stmt|;
return|return
name|n
operator|==
name|other
operator|.
name|n
operator|&&
name|phraseQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|phraseQuery
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
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|phraseQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|n
expr_stmt|;
return|return
name|h
return|;
block|}
comment|/** Return the list of terms. */
DECL|method|getTerms
specifier|public
name|Term
index|[]
name|getTerms
parameter_list|()
block|{
return|return
name|phraseQuery
operator|.
name|getTerms
argument_list|()
return|;
block|}
comment|/** Return the list of relative positions that each term should appear at. */
DECL|method|getPositions
specifier|public
name|int
index|[]
name|getPositions
parameter_list|()
block|{
return|return
name|phraseQuery
operator|.
name|getPositions
argument_list|()
return|;
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
return|return
name|phraseQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
end_class
end_unit
