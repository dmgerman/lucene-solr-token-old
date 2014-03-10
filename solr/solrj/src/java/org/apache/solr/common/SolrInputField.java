begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|Iterator
import|;
end_import
begin_comment
comment|/**  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrInputField
specifier|public
class|class
name|SolrInputField
implements|implements
name|Iterable
argument_list|<
name|Object
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|value
name|Object
name|value
init|=
literal|null
decl_stmt|;
DECL|field|boost
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
DECL|method|SolrInputField
specifier|public
name|SolrInputField
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|n
expr_stmt|;
block|}
comment|//---------------------------------------------------------------
comment|//---------------------------------------------------------------
comment|/**    * Set the value for a field.  Arrays will be converted to a collection. If    * a collection is given, then that collection will be used as the backing    * collection for the values.    */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|Object
name|v
parameter_list|,
name|float
name|b
parameter_list|)
block|{
name|boost
operator|=
name|b
expr_stmt|;
if|if
condition|(
name|v
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|Object
index|[]
name|arr
init|=
operator|(
name|Object
index|[]
operator|)
name|v
decl_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|c
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
name|arr
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|arr
control|)
block|{
name|c
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
name|value
operator|=
name|c
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|v
expr_stmt|;
block|}
block|}
comment|/**    * Add values to a field.  If the added value is a collection, each value    * will be added individually.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|addValue
specifier|public
name|void
name|addValue
parameter_list|(
name|Object
name|v
parameter_list|,
name|float
name|b
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|v
operator|instanceof
name|Collection
condition|)
block|{
name|Collection
argument_list|<
name|Object
argument_list|>
name|c
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
operator|(
name|Collection
argument_list|<
name|Object
argument_list|>
operator|)
name|v
control|)
block|{
name|c
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
name|setValue
argument_list|(
name|c
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setValue
argument_list|(
name|v
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
comment|// The lucene API and solr XML field specification make it possible to set boosts
comment|// on multi-value fields even though lucene indexing does not support this.
comment|// To keep behavior consistent with what happens in the lucene index, we accumulate
comment|// the product of all boosts specified for this field.
name|boost
operator|*=
name|b
expr_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|vals
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|Collection
condition|)
block|{
name|vals
operator|=
operator|(
name|Collection
argument_list|<
name|Object
argument_list|>
operator|)
name|value
expr_stmt|;
block|}
else|else
block|{
name|vals
operator|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
name|vals
expr_stmt|;
block|}
comment|// Add the new values to a collection
if|if
condition|(
name|v
operator|instanceof
name|Iterable
condition|)
block|{
for|for
control|(
name|Object
name|o
range|:
operator|(
name|Iterable
argument_list|<
name|Object
argument_list|>
operator|)
name|v
control|)
block|{
name|vals
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|v
operator|instanceof
name|Object
index|[]
condition|)
block|{
for|for
control|(
name|Object
name|o
range|:
operator|(
name|Object
index|[]
operator|)
name|v
control|)
block|{
name|vals
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|vals
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
comment|//---------------------------------------------------------------
comment|//---------------------------------------------------------------
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getFirstValue
specifier|public
name|Object
name|getFirstValue
parameter_list|()
block|{
if|if
condition|(
name|value
operator|instanceof
name|Collection
condition|)
block|{
name|Collection
name|c
init|=
operator|(
name|Collection
argument_list|<
name|Object
argument_list|>
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|c
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
return|return
name|value
return|;
block|}
comment|/**    * @return the value for this field.  If the field has multiple values, this    * will be a collection.    */
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**    * @return the values for this field.  This will return a collection even    * if the field is not multi-valued    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getValues
specifier|public
name|Collection
argument_list|<
name|Object
argument_list|>
name|getValues
parameter_list|()
block|{
if|if
condition|(
name|value
operator|instanceof
name|Collection
condition|)
block|{
return|return
operator|(
name|Collection
argument_list|<
name|Object
argument_list|>
operator|)
name|value
return|;
block|}
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|Collection
argument_list|<
name|Object
argument_list|>
name|vals
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|vals
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * @return the number of values for this field    */
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
if|if
condition|(
name|value
operator|instanceof
name|Collection
condition|)
block|{
return|return
operator|(
operator|(
name|Collection
operator|)
name|value
operator|)
operator|.
name|size
argument_list|()
return|;
block|}
return|return
operator|(
name|value
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
literal|1
return|;
block|}
comment|//---------------------------------------------------------------
comment|//---------------------------------------------------------------
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iterator
parameter_list|()
block|{
if|if
condition|(
name|value
operator|instanceof
name|Collection
condition|)
block|{
return|return
operator|(
operator|(
name|Collection
operator|)
name|value
operator|)
operator|.
name|iterator
argument_list|()
return|;
block|}
return|return
operator|new
name|Iterator
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
name|boolean
name|nxt
init|=
operator|(
name|value
operator|!=
literal|null
operator|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nxt
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|next
parameter_list|()
block|{
name|nxt
operator|=
literal|false
expr_stmt|;
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
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
return|return
name|name
operator|+
operator|(
operator|(
name|boost
operator|==
literal|1.0
operator|)
condition|?
literal|"="
else|:
operator|(
literal|"("
operator|+
name|boost
operator|+
literal|")="
operator|)
operator|)
operator|+
name|value
return|;
block|}
DECL|method|deepCopy
specifier|public
name|SolrInputField
name|deepCopy
parameter_list|()
block|{
name|SolrInputField
name|clone
init|=
operator|new
name|SolrInputField
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|clone
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
comment|// We can't clone here, so we rely on simple primitives
if|if
condition|(
name|value
operator|instanceof
name|Collection
condition|)
block|{
name|Collection
argument_list|<
name|Object
argument_list|>
name|values
init|=
operator|(
name|Collection
argument_list|<
name|Object
argument_list|>
operator|)
name|value
decl_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|cloneValues
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|cloneValues
operator|.
name|addAll
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|clone
operator|.
name|value
operator|=
name|cloneValues
expr_stmt|;
block|}
else|else
block|{
name|clone
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
return|return
name|clone
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
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SolrInputField
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SolrInputField
name|sif
init|=
operator|(
name|SolrInputField
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|sif
operator|.
name|name
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|value
operator|.
name|equals
argument_list|(
name|sif
operator|.
name|value
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|Float
operator|.
name|compare
argument_list|(
name|sif
operator|.
name|boost
argument_list|,
name|boost
argument_list|)
operator|!=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
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
name|result
init|=
name|name
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|value
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|boost
operator|!=
operator|+
literal|0.0f
condition|?
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|boost
argument_list|)
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
