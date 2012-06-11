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
name|PhraseQuery
block|{
DECL|field|n
specifier|private
specifier|final
name|int
name|n
decl_stmt|;
comment|/**    * Constructor that takes gram size.    * @param n    */
DECL|method|NGramPhraseQuery
specifier|public
name|NGramPhraseQuery
parameter_list|(
name|int
name|n
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
if|if
condition|(
name|getSlop
argument_list|()
operator|!=
literal|0
condition|)
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
comment|// check whether optimizable or not
if|if
condition|(
name|n
operator|<
literal|2
operator|||
comment|// non-overlap n-gram cannot be optimized
name|getTerms
argument_list|()
operator|.
name|length
operator|<
literal|3
condition|)
comment|// too short to optimize
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
comment|// check all posIncrement is 1
comment|// if not, cannot optimize
name|int
index|[]
name|positions
init|=
name|getPositions
argument_list|()
decl_stmt|;
name|Term
index|[]
name|terms
init|=
name|getTerms
argument_list|()
decl_stmt|;
name|int
name|prevPosition
init|=
name|positions
index|[
literal|0
index|]
decl_stmt|;
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
name|i
operator|++
control|)
block|{
name|int
name|pos
init|=
name|positions
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|prevPosition
operator|+
literal|1
operator|!=
name|pos
condition|)
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
name|prevPosition
operator|=
name|pos
expr_stmt|;
block|}
comment|// now create the new optimized phrase query for n-gram
name|PhraseQuery
name|optimized
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|lastPos
init|=
name|terms
operator|.
name|length
operator|-
literal|1
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
name|i
operator|++
control|)
block|{
if|if
condition|(
name|pos
operator|%
name|n
operator|==
literal|0
operator|||
name|pos
operator|>=
name|lastPos
condition|)
block|{
name|optimized
operator|.
name|add
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|,
name|positions
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|pos
operator|++
expr_stmt|;
block|}
return|return
name|optimized
return|;
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
operator|!
operator|(
name|o
operator|instanceof
name|NGramPhraseQuery
operator|)
condition|)
return|return
literal|false
return|;
name|NGramPhraseQuery
name|other
init|=
operator|(
name|NGramPhraseQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|n
operator|!=
name|other
operator|.
name|n
condition|)
return|return
literal|false
return|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object.*/
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
name|getSlop
argument_list|()
operator|^
name|getTerms
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|^
name|getPositions
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|^
name|n
return|;
block|}
block|}
end_class
end_unit
