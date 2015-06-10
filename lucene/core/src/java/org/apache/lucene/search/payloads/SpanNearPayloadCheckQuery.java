begin_unit
begin_package
DECL|package|org.apache.lucene.search.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|payloads
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
name|search
operator|.
name|spans
operator|.
name|SpanNearQuery
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_comment
comment|/**  * Only return those matches that have a specific payload at  * the given position.  *  * @deprecated Use {@link SpanPayloadCheckQuery}  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|SpanNearPayloadCheckQuery
specifier|public
class|class
name|SpanNearPayloadCheckQuery
extends|extends
name|SpanPayloadCheckQuery
block|{
comment|/**    * @param match          The underlying {@link org.apache.lucene.search.spans.SpanQuery} to check    * @param payloadToMatch The {@link java.util.Collection} of payloads to match    */
DECL|method|SpanNearPayloadCheckQuery
specifier|public
name|SpanNearPayloadCheckQuery
parameter_list|(
name|SpanNearQuery
name|match
parameter_list|,
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|payloadToMatch
parameter_list|)
block|{
name|super
argument_list|(
name|match
argument_list|,
name|payloadToMatch
argument_list|)
expr_stmt|;
block|}
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
name|buffer
operator|.
name|append
argument_list|(
literal|"spanPayCheck("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|match
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", payloadRef: "
argument_list|)
expr_stmt|;
for|for
control|(
name|byte
index|[]
name|bytes
range|:
name|payloadToMatch
control|)
block|{
name|ToStringUtils
operator|.
name|byteArray
argument_list|(
name|buffer
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|")"
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
DECL|method|clone
specifier|public
name|SpanNearPayloadCheckQuery
name|clone
parameter_list|()
block|{
name|SpanNearPayloadCheckQuery
name|result
init|=
operator|new
name|SpanNearPayloadCheckQuery
argument_list|(
operator|(
name|SpanNearQuery
operator|)
name|match
operator|.
name|clone
argument_list|()
argument_list|,
name|payloadToMatch
argument_list|)
decl_stmt|;
name|result
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
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
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SpanNearPayloadCheckQuery
name|other
init|=
operator|(
name|SpanNearPayloadCheckQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|payloadToMatch
operator|.
name|equals
argument_list|(
name|other
operator|.
name|payloadToMatch
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
name|int
name|h
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|=
operator|(
name|h
operator|*
literal|15
operator|)
operator|^
name|payloadToMatch
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class
end_unit
