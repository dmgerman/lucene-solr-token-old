begin_unit
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
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
name|analysis
operator|.
name|Token
import|;
end_import
begin_class
DECL|class|ResultEntry
specifier|public
class|class
name|ResultEntry
block|{
DECL|field|token
specifier|public
name|Token
name|token
decl_stmt|;
DECL|field|suggestion
specifier|public
name|String
name|suggestion
decl_stmt|;
DECL|field|freq
specifier|public
name|int
name|freq
decl_stmt|;
DECL|method|ResultEntry
name|ResultEntry
parameter_list|(
name|Token
name|t
parameter_list|,
name|String
name|s
parameter_list|,
name|int
name|f
parameter_list|)
block|{
name|token
operator|=
name|t
expr_stmt|;
name|suggestion
operator|=
name|s
expr_stmt|;
name|freq
operator|=
name|f
expr_stmt|;
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
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|freq
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|suggestion
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|suggestion
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|token
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|token
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
name|obj
operator|==
literal|null
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
name|ResultEntry
name|other
init|=
operator|(
name|ResultEntry
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|freq
operator|!=
name|other
operator|.
name|freq
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|suggestion
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|suggestion
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
name|suggestion
operator|.
name|equals
argument_list|(
name|other
operator|.
name|suggestion
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|token
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
name|token
operator|.
name|equals
argument_list|(
name|other
operator|.
name|token
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
