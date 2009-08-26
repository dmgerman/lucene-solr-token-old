begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.cn.smart.hhmm
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|hhmm
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/**  * A pair of tokens in {@link SegGraph}  *<p><font color="#FF0000">  * WARNING: The status of the analyzers/smartcn<b>analysis.cn</b> package is experimental.   * The APIs introduced here might change in the future and will not be   * supported anymore in such a case.</font>  *</p>  */
end_comment
begin_class
DECL|class|SegTokenPair
class|class
name|SegTokenPair
block|{
DECL|field|charArray
specifier|public
name|char
index|[]
name|charArray
decl_stmt|;
comment|/**    * index of the first token in {@link SegGraph}    */
DECL|field|from
specifier|public
name|int
name|from
decl_stmt|;
comment|/**    * index of the second token in {@link SegGraph}    */
DECL|field|to
specifier|public
name|int
name|to
decl_stmt|;
DECL|field|weight
specifier|public
name|double
name|weight
decl_stmt|;
DECL|method|SegTokenPair
specifier|public
name|SegTokenPair
parameter_list|(
name|char
index|[]
name|idArray
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|,
name|double
name|weight
parameter_list|)
block|{
name|this
operator|.
name|charArray
operator|=
name|idArray
expr_stmt|;
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
comment|/**    * @see java.lang.Object#hashCode()    */
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|charArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|charArray
index|[
name|i
index|]
expr_stmt|;
block|}
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|from
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|to
expr_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * @see java.lang.Object#equals(java.lang.Object)    */
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
name|SegTokenPair
name|other
init|=
operator|(
name|SegTokenPair
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|charArray
argument_list|,
name|other
operator|.
name|charArray
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|from
operator|!=
name|other
operator|.
name|from
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|to
operator|!=
name|other
operator|.
name|to
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|weight
argument_list|)
operator|!=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|weight
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
