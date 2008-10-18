begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * Holds stats info  *  * @version $Id: SpellCheckResponse.java 693622 2008-09-09 21:21:06Z gsingers $  * @since solr 1.4  */
end_comment
begin_class
DECL|class|FieldStatsInfo
specifier|public
class|class
name|FieldStatsInfo
implements|implements
name|Serializable
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|min
name|Double
name|min
decl_stmt|;
DECL|field|max
name|Double
name|max
decl_stmt|;
DECL|field|sum
name|Double
name|sum
decl_stmt|;
DECL|field|count
name|Long
name|count
decl_stmt|;
DECL|field|missing
name|Long
name|missing
decl_stmt|;
DECL|field|mean
name|Double
name|mean
init|=
literal|null
decl_stmt|;
DECL|field|sumOfSquares
name|Double
name|sumOfSquares
init|=
literal|null
decl_stmt|;
DECL|field|stddev
name|Double
name|stddev
init|=
literal|null
decl_stmt|;
DECL|field|median
name|Double
name|median
init|=
literal|null
decl_stmt|;
DECL|field|facets
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FieldStatsInfo
argument_list|>
argument_list|>
name|facets
decl_stmt|;
DECL|method|FieldStatsInfo
specifier|public
name|FieldStatsInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
parameter_list|,
name|String
name|fname
parameter_list|)
block|{
name|name
operator|=
name|fname
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|nl
control|)
block|{
if|if
condition|(
literal|"min"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|min
operator|=
operator|(
name|Double
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"max"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|max
operator|=
operator|(
name|Double
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"sum"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|sum
operator|=
operator|(
name|Double
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"count"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|count
operator|=
operator|(
name|Long
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"missing"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|missing
operator|=
operator|(
name|Long
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"mean"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|mean
operator|=
operator|(
name|Double
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"sumOfSquares"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|sumOfSquares
operator|=
operator|(
name|Double
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"stddev"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|stddev
operator|=
operator|(
name|Double
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"median"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|median
operator|=
operator|(
name|Double
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"facets"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|fields
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|facets
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FieldStatsInfo
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ev
range|:
name|fields
control|)
block|{
name|List
argument_list|<
name|FieldStatsInfo
argument_list|>
name|vals
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldStatsInfo
argument_list|>
argument_list|()
decl_stmt|;
name|facets
operator|.
name|put
argument_list|(
name|ev
operator|.
name|getKey
argument_list|()
argument_list|,
name|vals
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|vnl
init|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
operator|)
name|ev
operator|.
name|getValue
argument_list|()
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
name|vnl
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|n
init|=
name|vnl
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|vals
operator|.
name|add
argument_list|(
operator|new
name|FieldStatsInfo
argument_list|(
name|vnl
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|,
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown key: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" ["
operator|+
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|": {"
argument_list|)
expr_stmt|;
if|if
condition|(
name|min
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" min:"
argument_list|)
operator|.
name|append
argument_list|(
name|min
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|max
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" max:"
argument_list|)
operator|.
name|append
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sum
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" sum:"
argument_list|)
operator|.
name|append
argument_list|(
name|sum
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" count:"
argument_list|)
operator|.
name|append
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|missing
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" missing:"
argument_list|)
operator|.
name|append
argument_list|(
name|missing
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mean
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" mean:"
argument_list|)
operator|.
name|append
argument_list|(
name|mean
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|median
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" median:"
argument_list|)
operator|.
name|append
argument_list|(
name|median
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stddev
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" stddev:"
argument_list|)
operator|.
name|append
argument_list|(
name|stddev
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
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
DECL|method|getMin
specifier|public
name|Double
name|getMin
parameter_list|()
block|{
return|return
name|min
return|;
block|}
DECL|method|getMax
specifier|public
name|Double
name|getMax
parameter_list|()
block|{
return|return
name|max
return|;
block|}
DECL|method|getSum
specifier|public
name|Double
name|getSum
parameter_list|()
block|{
return|return
name|sum
return|;
block|}
DECL|method|getCount
specifier|public
name|Long
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
DECL|method|getMissing
specifier|public
name|Long
name|getMissing
parameter_list|()
block|{
return|return
name|missing
return|;
block|}
DECL|method|getMean
specifier|public
name|Double
name|getMean
parameter_list|()
block|{
return|return
name|mean
return|;
block|}
DECL|method|getStddev
specifier|public
name|Double
name|getStddev
parameter_list|()
block|{
return|return
name|stddev
return|;
block|}
DECL|method|getMedian
specifier|public
name|Double
name|getMedian
parameter_list|()
block|{
return|return
name|median
return|;
block|}
DECL|method|getFacets
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FieldStatsInfo
argument_list|>
argument_list|>
name|getFacets
parameter_list|()
block|{
return|return
name|facets
return|;
block|}
block|}
end_class
end_unit
