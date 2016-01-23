begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package
begin_comment
comment|/**  * SolrParams wrapper which acts similar to DefaultSolrParams except that  * it "appends" the values of multi-value params from both sub instances, so  * that all of the values are returned.   */
end_comment
begin_class
DECL|class|AppendedSolrParams
specifier|public
class|class
name|AppendedSolrParams
extends|extends
name|DefaultSolrParams
block|{
DECL|method|AppendedSolrParams
specifier|public
name|AppendedSolrParams
parameter_list|(
name|SolrParams
name|main
parameter_list|,
name|SolrParams
name|extra
parameter_list|)
block|{
name|super
argument_list|(
name|main
argument_list|,
name|extra
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|String
index|[]
name|getParams
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
index|[]
name|main
init|=
name|params
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
decl_stmt|;
name|String
index|[]
name|extra
init|=
name|defaults
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|extra
operator|||
literal|0
operator|==
name|extra
operator|.
name|length
condition|)
block|{
return|return
name|main
return|;
block|}
if|if
condition|(
literal|null
operator|==
name|main
operator|||
literal|0
operator|==
name|main
operator|.
name|length
condition|)
block|{
return|return
name|extra
return|;
block|}
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|main
operator|.
name|length
operator|+
name|extra
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|main
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
literal|0
argument_list|,
name|main
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|extra
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
name|main
operator|.
name|length
argument_list|,
name|extra
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|result
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
literal|"{main("
operator|+
name|params
operator|+
literal|"),extra("
operator|+
name|defaults
operator|+
literal|")}"
return|;
block|}
block|}
end_class
end_unit
