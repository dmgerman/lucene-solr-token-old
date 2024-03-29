begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.expressions
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
package|;
end_package
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
name|search
operator|.
name|FieldComparator
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
name|SortField
import|;
end_import
begin_comment
comment|/** A {@link SortField} which sorts documents by the evaluated value of an expression for each document */
end_comment
begin_class
DECL|class|ExpressionSortField
class|class
name|ExpressionSortField
extends|extends
name|SortField
block|{
DECL|field|source
specifier|private
specifier|final
name|ExpressionValueSource
name|source
decl_stmt|;
DECL|method|ExpressionSortField
name|ExpressionSortField
parameter_list|(
name|String
name|name
parameter_list|,
name|ExpressionValueSource
name|source
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|Type
operator|.
name|CUSTOM
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|getComparator
parameter_list|(
specifier|final
name|int
name|numHits
parameter_list|,
specifier|final
name|int
name|sortPos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ExpressionComparator
argument_list|(
name|source
argument_list|,
name|numHits
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
name|source
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|source
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
name|ExpressionSortField
name|other
init|=
operator|(
name|ExpressionSortField
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|source
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
name|source
operator|.
name|equals
argument_list|(
name|other
operator|.
name|source
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
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
literal|"<expr \""
argument_list|)
expr_stmt|;
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
literal|"\">"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getReverse
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|'!'
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
name|source
operator|.
name|needsScores
argument_list|()
return|;
block|}
block|}
end_class
end_unit
