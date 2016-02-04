begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
package|;
end_package
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
name|Explanation
import|;
end_import
begin_comment
comment|/**  * Model of the information gain based on Laplace's law of succession.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|AfterEffectL
specifier|public
class|class
name|AfterEffectL
extends|extends
name|AfterEffect
block|{
comment|/** Sole constructor: parameter-free */
DECL|method|AfterEffectL
specifier|public
name|AfterEffectL
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tfn
parameter_list|)
block|{
return|return
literal|1
operator|/
operator|(
name|tfn
operator|+
literal|1
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
specifier|final
name|Explanation
name|explain
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tfn
parameter_list|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|score
argument_list|(
name|stats
argument_list|,
name|tfn
argument_list|)
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|", computed from: "
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|tfn
argument_list|,
literal|"tfn"
argument_list|)
argument_list|)
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
literal|"L"
return|;
block|}
block|}
end_class
end_unit
