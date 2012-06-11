begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_comment
comment|/**  * SmartChineseAnalyzer internal node representation  *<p>  * Used by {@link BiSegGraph} to maximize the segmentation with the Viterbi algorithm.  *</p>  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PathNode
class|class
name|PathNode
implements|implements
name|Comparable
argument_list|<
name|PathNode
argument_list|>
block|{
DECL|field|weight
specifier|public
name|double
name|weight
decl_stmt|;
DECL|field|preNode
specifier|public
name|int
name|preNode
decl_stmt|;
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|PathNode
name|pn
parameter_list|)
block|{
if|if
condition|(
name|weight
operator|<
name|pn
operator|.
name|weight
condition|)
return|return
operator|-
literal|1
return|;
elseif|else
if|if
condition|(
name|weight
operator|==
name|pn
operator|.
name|weight
condition|)
return|return
literal|0
return|;
else|else
return|return
literal|1
return|;
block|}
comment|/**    * @see java.lang.Object#hashCode()    */
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
name|preNode
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
name|PathNode
name|other
init|=
operator|(
name|PathNode
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|preNode
operator|!=
name|other
operator|.
name|preNode
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
