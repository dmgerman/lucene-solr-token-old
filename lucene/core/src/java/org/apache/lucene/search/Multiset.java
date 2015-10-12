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
name|util
operator|.
name|AbstractCollection
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
begin_comment
comment|/**  * A {@link Multiset} is a set that allows for duplicate elements. Two  * {@link Multiset}s are equal if they contain the same unique elements and if  * each unique element has as many occurrences in both multisets.  * Iteration order is not specified.  * @lucene.internal  */
end_comment
begin_class
DECL|class|Multiset
specifier|final
class|class
name|Multiset
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractCollection
argument_list|<
name|T
argument_list|>
block|{
DECL|field|map
specifier|private
specifier|final
name|Map
argument_list|<
name|T
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
comment|/** Create an empty {@link Multiset}. */
DECL|method|Multiset
name|Multiset
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|T
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|mapIterator
init|=
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
name|T
name|current
decl_stmt|;
name|int
name|remaining
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|remaining
operator|>
literal|0
operator|||
name|mapIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|next
parameter_list|()
block|{
if|if
condition|(
name|remaining
operator|==
literal|0
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|T
argument_list|,
name|Integer
argument_list|>
name|next
init|=
name|mapIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|current
operator|=
name|next
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|remaining
operator|=
name|next
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
assert|assert
name|remaining
operator|>
literal|0
assert|;
name|remaining
operator|-=
literal|1
expr_stmt|;
return|return
name|current
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|T
name|e
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|e
argument_list|,
name|map
operator|.
name|getOrDefault
argument_list|(
name|e
argument_list|,
literal|0
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|size
operator|+=
literal|1
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|boolean
name|remove
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
specifier|final
name|Integer
name|count
init|=
name|map
operator|.
name|get
argument_list|(
name|o
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
literal|1
operator|==
name|count
operator|.
name|intValue
argument_list|()
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
operator|(
name|T
operator|)
name|o
argument_list|,
name|count
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|size
operator|-=
literal|1
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|o
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Multiset
argument_list|<
name|?
argument_list|>
name|that
init|=
operator|(
name|Multiset
argument_list|<
name|?
argument_list|>
operator|)
name|obj
decl_stmt|;
return|return
name|size
operator|==
name|that
operator|.
name|size
comment|// not necessary but helps escaping early
operator|&&
name|map
operator|.
name|equals
argument_list|(
name|that
operator|.
name|map
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
return|return
literal|31
operator|*
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|map
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
