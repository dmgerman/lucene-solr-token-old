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
name|util
operator|.
name|AttributeSource
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
begin_comment
comment|/** A Query that matches documents containing terms with a specified prefix. A PrefixQuery  * is built by QueryParser for input like<code>app*</code>.  *  *<p>This query uses the {@link  * MultiTermQuery#CONSTANT_SCORE_REWRITE}  * rewrite method. */
end_comment
begin_class
DECL|class|PrefixQuery
specifier|public
class|class
name|PrefixQuery
extends|extends
name|MultiTermQuery
block|{
DECL|field|prefix
specifier|private
name|Term
name|prefix
decl_stmt|;
comment|/** Constructs a query for terms starting with<code>prefix</code>. */
DECL|method|PrefixQuery
specifier|public
name|PrefixQuery
parameter_list|(
name|Term
name|prefix
parameter_list|)
block|{
name|super
argument_list|(
name|prefix
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
comment|/** Returns the prefix of this query. */
DECL|method|getPrefix
specifier|public
name|Term
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|)
throws|throws
name|IOException
block|{
name|TermsEnum
name|tenum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|.
name|bytes
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// no prefix -- match all terms for this field:
return|return
name|tenum
return|;
block|}
return|return
operator|new
name|PrefixTermsEnum
argument_list|(
name|tenum
argument_list|,
name|prefix
operator|.
name|bytes
argument_list|()
argument_list|)
return|;
block|}
comment|/** Prints a user-readable version of this query. */
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
operator|!
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|prefix
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'*'
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
operator|(
operator|(
name|prefix
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|prefix
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
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
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
name|PrefixQuery
name|other
init|=
operator|(
name|PrefixQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|prefix
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|prefix
operator|.
name|equals
argument_list|(
name|other
operator|.
name|prefix
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
