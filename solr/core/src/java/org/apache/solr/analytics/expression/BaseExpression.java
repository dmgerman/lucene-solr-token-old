begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.expression
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|expression
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|statistics
operator|.
name|StatsCollector
import|;
end_import
begin_comment
comment|/**  *<code>BaseExpression</code> returns the value returned by the {@link StatsCollector} for the specified stat.  */
end_comment
begin_class
DECL|class|BaseExpression
specifier|public
class|class
name|BaseExpression
extends|extends
name|Expression
block|{
DECL|field|statsCollector
specifier|protected
specifier|final
name|StatsCollector
name|statsCollector
decl_stmt|;
DECL|field|stat
specifier|protected
specifier|final
name|String
name|stat
decl_stmt|;
DECL|method|BaseExpression
specifier|public
name|BaseExpression
parameter_list|(
name|StatsCollector
name|statsCollector
parameter_list|,
name|String
name|stat
parameter_list|)
block|{
name|this
operator|.
name|statsCollector
operator|=
name|statsCollector
expr_stmt|;
name|this
operator|.
name|stat
operator|=
name|stat
expr_stmt|;
block|}
DECL|method|getValue
specifier|public
name|Comparable
name|getValue
parameter_list|()
block|{
if|if
condition|(
name|statsCollector
operator|.
name|getStatsList
argument_list|()
operator|.
name|contains
argument_list|(
name|stat
argument_list|)
condition|)
block|{
return|return
name|statsCollector
operator|.
name|getStat
argument_list|(
name|stat
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class
begin_comment
comment|/**  *<code>ConstantStringExpression</code> returns the specified constant double.  */
end_comment
begin_class
DECL|class|ConstantNumberExpression
class|class
name|ConstantNumberExpression
extends|extends
name|Expression
block|{
DECL|field|constant
specifier|protected
specifier|final
name|Double
name|constant
decl_stmt|;
DECL|method|ConstantNumberExpression
specifier|public
name|ConstantNumberExpression
parameter_list|(
name|double
name|d
parameter_list|)
block|{
name|constant
operator|=
operator|new
name|Double
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
DECL|method|getValue
specifier|public
name|Comparable
name|getValue
parameter_list|()
block|{
return|return
name|constant
return|;
block|}
block|}
end_class
begin_comment
comment|/**  *<code>ConstantStringExpression</code> returns the specified constant date.  */
end_comment
begin_class
DECL|class|ConstantDateExpression
class|class
name|ConstantDateExpression
extends|extends
name|Expression
block|{
DECL|field|constant
specifier|protected
specifier|final
name|Date
name|constant
decl_stmt|;
DECL|method|ConstantDateExpression
specifier|public
name|ConstantDateExpression
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
name|constant
operator|=
name|date
expr_stmt|;
block|}
DECL|method|ConstantDateExpression
specifier|public
name|ConstantDateExpression
parameter_list|(
name|Long
name|date
parameter_list|)
block|{
name|constant
operator|=
operator|new
name|Date
argument_list|(
name|date
argument_list|)
expr_stmt|;
block|}
DECL|method|getValue
specifier|public
name|Comparable
name|getValue
parameter_list|()
block|{
return|return
name|constant
return|;
block|}
block|}
end_class
begin_comment
comment|/**  *<code>ConstantStringExpression</code> returns the specified constant string.  */
end_comment
begin_class
DECL|class|ConstantStringExpression
class|class
name|ConstantStringExpression
extends|extends
name|Expression
block|{
DECL|field|constant
specifier|protected
specifier|final
name|String
name|constant
decl_stmt|;
DECL|method|ConstantStringExpression
specifier|public
name|ConstantStringExpression
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|constant
operator|=
name|str
expr_stmt|;
block|}
DECL|method|getValue
specifier|public
name|Comparable
name|getValue
parameter_list|()
block|{
return|return
name|constant
return|;
block|}
block|}
end_class
end_unit
