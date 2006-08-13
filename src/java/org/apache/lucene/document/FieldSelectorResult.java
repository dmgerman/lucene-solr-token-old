begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *  Provides information about what should be done with this Field   *  **/
end_comment
begin_comment
comment|//Replace with an enumerated type in 1.5
end_comment
begin_class
DECL|class|FieldSelectorResult
specifier|public
specifier|final
class|class
name|FieldSelectorResult
block|{
DECL|field|LOAD
specifier|public
specifier|static
specifier|final
name|FieldSelectorResult
name|LOAD
init|=
operator|new
name|FieldSelectorResult
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|LAZY_LOAD
specifier|public
specifier|static
specifier|final
name|FieldSelectorResult
name|LAZY_LOAD
init|=
operator|new
name|FieldSelectorResult
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|NO_LOAD
specifier|public
specifier|static
specifier|final
name|FieldSelectorResult
name|NO_LOAD
init|=
operator|new
name|FieldSelectorResult
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|field|LOAD_AND_BREAK
specifier|public
specifier|static
specifier|final
name|FieldSelectorResult
name|LOAD_AND_BREAK
init|=
operator|new
name|FieldSelectorResult
argument_list|(
literal|3
argument_list|)
decl_stmt|;
DECL|field|LOAD_FOR_MERGE
specifier|public
specifier|static
specifier|final
name|FieldSelectorResult
name|LOAD_FOR_MERGE
init|=
operator|new
name|FieldSelectorResult
argument_list|(
literal|4
argument_list|)
decl_stmt|;
DECL|field|id
specifier|private
name|int
name|id
decl_stmt|;
DECL|method|FieldSelectorResult
specifier|private
name|FieldSelectorResult
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
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
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|FieldSelectorResult
name|that
init|=
operator|(
name|FieldSelectorResult
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|id
operator|!=
name|that
operator|.
name|id
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
name|id
return|;
block|}
block|}
end_class
end_unit
