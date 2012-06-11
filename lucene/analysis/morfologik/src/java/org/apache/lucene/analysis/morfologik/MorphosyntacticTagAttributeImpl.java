begin_unit
begin_comment
comment|// -*- c-basic-offset: 2 -*-
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.morfologik
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|morfologik
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
name|util
operator|.
name|AttributeImpl
import|;
end_import
begin_comment
comment|/**  * Morphosyntactic annotations for surface forms.  * @see MorphosyntacticTagAttribute  */
end_comment
begin_class
DECL|class|MorphosyntacticTagAttributeImpl
specifier|public
class|class
name|MorphosyntacticTagAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|MorphosyntacticTagAttribute
implements|,
name|Cloneable
block|{
comment|/**    * Either the original tag from WordData or a clone.    */
DECL|field|tag
specifier|private
name|CharSequence
name|tag
decl_stmt|;
comment|/**     * Set the tag.    */
DECL|method|setTag
specifier|public
name|void
name|setTag
parameter_list|(
name|CharSequence
name|pos
parameter_list|)
block|{
name|this
operator|.
name|tag
operator|=
operator|(
operator|(
name|pos
operator|==
literal|null
operator|||
name|pos
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|?
literal|null
else|:
name|pos
operator|)
expr_stmt|;
block|}
comment|/**    * Returns the POS tag of the term. If you need a copy of this char sequence, clone it    * because it may change with each new term!    */
DECL|method|getTag
specifier|public
name|CharSequence
name|getTag
parameter_list|()
block|{
return|return
name|tag
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|tag
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|MorphosyntacticTagAttribute
condition|)
block|{
return|return
name|equal
argument_list|(
name|this
operator|.
name|getTag
argument_list|()
argument_list|,
operator|(
operator|(
name|MorphosyntacticTagAttribute
operator|)
name|other
operator|)
operator|.
name|getTag
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Check if two char sequences are the same.    */
DECL|method|equal
specifier|private
name|boolean
name|equal
parameter_list|(
name|CharSequence
name|chs1
parameter_list|,
name|CharSequence
name|chs2
parameter_list|)
block|{
if|if
condition|(
name|chs1
operator|==
literal|null
operator|&&
name|chs2
operator|==
literal|null
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|chs1
operator|==
literal|null
operator|||
name|chs2
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|int
name|l1
init|=
name|chs1
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|l2
init|=
name|chs2
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|l1
operator|!=
name|l2
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|l1
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|chs1
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|!=
name|chs2
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|tag
operator|==
literal|null
condition|?
literal|0
else|:
name|tag
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
operator|(
operator|(
name|MorphosyntacticTagAttribute
operator|)
name|target
operator|)
operator|.
name|setTag
argument_list|(
name|this
operator|.
name|tag
argument_list|)
expr_stmt|;
block|}
DECL|method|clone
specifier|public
name|MorphosyntacticTagAttributeImpl
name|clone
parameter_list|()
block|{
name|MorphosyntacticTagAttributeImpl
name|cloned
init|=
operator|new
name|MorphosyntacticTagAttributeImpl
argument_list|()
decl_stmt|;
name|cloned
operator|.
name|tag
operator|=
operator|(
name|tag
operator|==
literal|null
condition|?
literal|null
else|:
name|tag
operator|.
name|toString
argument_list|()
operator|)
expr_stmt|;
return|return
name|cloned
return|;
block|}
block|}
end_class
end_unit
