begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream
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
name|io
operator|.
name|stream
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Locale
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrClient
operator|.
name|Builder
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|SolrClientCache
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|StreamComparator
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
operator|.
name|ExpressionType
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Expressible
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExplanation
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpression
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionNamedParameter
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParameter
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionValue
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|Metric
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|QueryRequest
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
name|common
operator|.
name|SolrDocumentList
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
name|common
operator|.
name|params
operator|.
name|MapSolrParams
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
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
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
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_class
DECL|class|StatsStream
specifier|public
class|class
name|StatsStream
extends|extends
name|TupleStream
implements|implements
name|Expressible
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|metrics
specifier|private
name|Metric
index|[]
name|metrics
decl_stmt|;
DECL|field|zkHost
specifier|private
name|String
name|zkHost
decl_stmt|;
DECL|field|tuple
specifier|private
name|Tuple
name|tuple
decl_stmt|;
DECL|field|params
specifier|private
name|SolrParams
name|params
decl_stmt|;
DECL|field|collection
specifier|private
name|String
name|collection
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
decl_stmt|;
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
DECL|field|doCount
specifier|private
name|boolean
name|doCount
decl_stmt|;
DECL|field|cache
specifier|protected
specifier|transient
name|SolrClientCache
name|cache
decl_stmt|;
DECL|field|cloudSolrClient
specifier|protected
specifier|transient
name|CloudSolrClient
name|cloudSolrClient
decl_stmt|;
comment|// Use StatsStream(String, String, SolrParams, Metric[]
annotation|@
name|Deprecated
DECL|method|StatsStream
specifier|public
name|StatsStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
parameter_list|,
name|Metric
index|[]
name|metrics
parameter_list|)
block|{
name|init
argument_list|(
name|zkHost
argument_list|,
name|collection
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|props
argument_list|)
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
DECL|method|StatsStream
specifier|public
name|StatsStream
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|Metric
index|[]
name|metrics
parameter_list|)
block|{
name|init
argument_list|(
name|zkHost
argument_list|,
name|collection
argument_list|,
name|params
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|Metric
index|[]
name|metrics
parameter_list|)
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
block|}
DECL|method|StatsStream
specifier|public
name|StatsStream
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// grab all parameters out
name|String
name|collectionName
init|=
name|factory
operator|.
name|getValueOperand
argument_list|(
name|expression
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StreamExpressionNamedParameter
argument_list|>
name|namedParams
init|=
name|factory
operator|.
name|getNamedOperands
argument_list|(
name|expression
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|metricExpressions
init|=
name|factory
operator|.
name|getExpressionOperandsRepresentingTypes
argument_list|(
name|expression
argument_list|,
name|Metric
operator|.
name|class
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|zkHostExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"zkHost"
argument_list|)
decl_stmt|;
comment|// Validate there are no unknown parameters - zkHost is namedParameter so we don't need to count it twice
if|if
condition|(
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
operator|+
name|namedParams
operator|.
name|size
argument_list|()
operator|+
name|metricExpressions
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - unknown operands found"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
comment|// Collection Name
if|if
condition|(
literal|null
operator|==
name|collectionName
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - collectionName expected as first operand"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
comment|// Named parameters - passed directly to solr as solrparams
if|if
condition|(
literal|0
operator|==
name|namedParams
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - at least one named parameter expected. eg. 'q=*:*'"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
for|for
control|(
name|StreamExpressionNamedParameter
name|namedParam
range|:
name|namedParams
control|)
block|{
if|if
condition|(
operator|!
name|namedParam
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"zkHost"
argument_list|)
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|namedParam
operator|.
name|getName
argument_list|()
argument_list|,
name|namedParam
operator|.
name|getParameter
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// zkHost, optional - if not provided then will look into factory list to get
name|String
name|zkHost
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|zkHostExpression
condition|)
block|{
name|zkHost
operator|=
name|factory
operator|.
name|getCollectionZkHost
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
if|if
condition|(
name|zkHost
operator|==
literal|null
condition|)
block|{
name|zkHost
operator|=
name|factory
operator|.
name|getDefaultZkHost
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|zkHostExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
name|zkHost
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|zkHostExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|zkHost
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"invalid expression %s - zkHost not found for collection '%s'"
argument_list|,
name|expression
argument_list|,
name|collectionName
argument_list|)
argument_list|)
throw|;
block|}
comment|// metrics, optional - if not provided then why are you using this?
name|Metric
index|[]
name|metrics
init|=
operator|new
name|Metric
index|[
name|metricExpressions
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|metricExpressions
operator|.
name|size
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|metrics
index|[
name|idx
index|]
operator|=
name|factory
operator|.
name|constructMetric
argument_list|(
name|metricExpressions
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// We've got all the required items
name|init
argument_list|(
name|zkHost
argument_list|,
name|collectionName
argument_list|,
name|params
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpressionParameter
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// functionName(collectionName, param1, param2, ..., paramN, sort="comp", sum(fieldA), avg(fieldB))
comment|// function name
name|StreamExpression
name|expression
init|=
operator|new
name|StreamExpression
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// collection
name|expression
operator|.
name|addParameter
argument_list|(
name|collection
argument_list|)
expr_stmt|;
comment|// parameters
name|ModifiableSolrParams
name|mParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|param
range|:
name|mParams
operator|.
name|getMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
name|param
operator|.
name|getKey
argument_list|()
argument_list|,
name|String
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|param
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// zkHost
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"zkHost"
argument_list|,
name|zkHost
argument_list|)
argument_list|)
expr_stmt|;
comment|// metrics
for|for
control|(
name|Metric
name|metric
range|:
name|metrics
control|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
name|metric
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|expression
return|;
block|}
annotation|@
name|Override
DECL|method|toExplanation
specifier|public
name|Explanation
name|toExplanation
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|StreamExplanation
name|explanation
init|=
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|explanation
operator|.
name|setFunctionName
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|setImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|setExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_SOURCE
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|setExpression
argument_list|(
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|StreamExplanation
name|child
init|=
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|+
literal|"-datastore"
argument_list|)
decl_stmt|;
name|child
operator|.
name|setFunctionName
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"solr (worker ? of ?)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: fix this so we know the # of workers - check with Joel about a Stat's ability to be in a
comment|// parallel stream.
name|child
operator|.
name|setImplementingClass
argument_list|(
literal|"Solr/Lucene"
argument_list|)
expr_stmt|;
name|child
operator|.
name|setExpressionType
argument_list|(
name|ExpressionType
operator|.
name|DATASTORE
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|mParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|child
operator|.
name|setExpression
argument_list|(
name|mParams
operator|.
name|getMap
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|e
lambda|->
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s=%s"
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|","
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|explanation
operator|.
name|addChild
argument_list|(
name|child
argument_list|)
expr_stmt|;
return|return
name|explanation
return|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
name|cache
operator|=
name|context
operator|.
name|getSolrClientCache
argument_list|()
expr_stmt|;
block|}
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
name|List
argument_list|<
name|TupleStream
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
return|return
name|l
return|;
block|}
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|cloudSolrClient
operator|=
name|cache
operator|.
name|getCloudSolrClient
argument_list|(
name|zkHost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cloudSolrClient
operator|=
operator|new
name|Builder
argument_list|()
operator|.
name|withZkHost
argument_list|(
name|zkHost
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|ModifiableSolrParams
name|paramsLoc
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|this
operator|.
name|params
argument_list|)
decl_stmt|;
name|addStats
argument_list|(
name|paramsLoc
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
name|paramsLoc
operator|.
name|set
argument_list|(
literal|"stats"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|paramsLoc
operator|.
name|set
argument_list|(
literal|"rows"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|paramsLoc
argument_list|)
decl_stmt|;
try|try
block|{
name|NamedList
name|response
init|=
name|cloudSolrClient
operator|.
name|request
argument_list|(
name|request
argument_list|,
name|collection
argument_list|)
decl_stmt|;
name|this
operator|.
name|tuple
operator|=
name|getTuple
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|cloudSolrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|done
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
return|return
name|tuple
return|;
block|}
else|else
block|{
name|Map
name|fields
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Tuple
name|tuple
init|=
operator|new
name|Tuple
argument_list|(
name|fields
argument_list|)
decl_stmt|;
return|return
name|tuple
return|;
block|}
block|}
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|addStats
specifier|private
name|void
name|addStats
parameter_list|(
name|ModifiableSolrParams
name|params
parameter_list|,
name|Metric
index|[]
name|_metrics
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Metric
name|metric
range|:
name|_metrics
control|)
block|{
name|String
name|metricId
init|=
name|metric
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
if|if
condition|(
name|metricId
operator|.
name|contains
argument_list|(
literal|"("
argument_list|)
condition|)
block|{
name|metricId
operator|=
name|metricId
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|metricId
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|String
index|[]
name|parts
init|=
name|metricId
operator|.
name|split
argument_list|(
literal|"\\("
argument_list|)
decl_stmt|;
name|String
name|function
init|=
name|parts
index|[
literal|0
index|]
decl_stmt|;
name|String
name|column
init|=
name|parts
index|[
literal|1
index|]
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|stats
init|=
name|m
operator|.
name|get
argument_list|(
name|column
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
operator|&&
operator|!
name|column
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|stats
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|column
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|function
operator|.
name|equals
argument_list|(
literal|"min"
argument_list|)
condition|)
block|{
name|stats
operator|.
name|add
argument_list|(
literal|"min"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|function
operator|.
name|equals
argument_list|(
literal|"max"
argument_list|)
condition|)
block|{
name|stats
operator|.
name|add
argument_list|(
literal|"max"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|function
operator|.
name|equals
argument_list|(
literal|"sum"
argument_list|)
condition|)
block|{
name|stats
operator|.
name|add
argument_list|(
literal|"sum"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|function
operator|.
name|equals
argument_list|(
literal|"avg"
argument_list|)
condition|)
block|{
name|stats
operator|.
name|add
argument_list|(
literal|"mean"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|function
operator|.
name|equals
argument_list|(
literal|"count"
argument_list|)
condition|)
block|{
name|this
operator|.
name|doCount
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|String
name|field
range|:
name|m
operator|.
name|keySet
argument_list|()
control|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|stats
init|=
name|m
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"{!"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|stat
range|:
name|stats
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|stat
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
literal|"true "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"stats.field"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getTuple
specifier|private
name|Tuple
name|getTuple
parameter_list|(
name|NamedList
name|response
parameter_list|)
block|{
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|doCount
condition|)
block|{
name|SolrDocumentList
name|solrDocumentList
init|=
operator|(
name|SolrDocumentList
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
decl_stmt|;
name|this
operator|.
name|count
operator|=
name|solrDocumentList
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"count(*)"
argument_list|,
name|this
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
name|NamedList
name|stats
init|=
operator|(
name|NamedList
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"stats"
argument_list|)
decl_stmt|;
name|NamedList
name|statsFields
init|=
operator|(
name|NamedList
operator|)
name|stats
operator|.
name|get
argument_list|(
literal|"stats_fields"
argument_list|)
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
name|statsFields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|field
init|=
name|statsFields
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|NamedList
name|theStats
init|=
operator|(
name|NamedList
operator|)
name|statsFields
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|0
init|;
name|s
operator|<
name|theStats
operator|.
name|size
argument_list|()
condition|;
name|s
operator|++
control|)
block|{
name|addStat
argument_list|(
name|map
argument_list|,
name|field
argument_list|,
name|theStats
operator|.
name|getName
argument_list|(
name|s
argument_list|)
argument_list|,
name|theStats
operator|.
name|getVal
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Tuple
name|tuple
init|=
operator|new
name|Tuple
argument_list|(
name|map
argument_list|)
decl_stmt|;
return|return
name|tuple
return|;
block|}
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|addStat
specifier|private
name|void
name|addStat
parameter_list|(
name|Map
name|map
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|stat
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"mean"
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
literal|"avg("
operator|+
name|field
operator|+
literal|")"
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|stat
operator|+
literal|"("
operator|+
name|field
operator|+
literal|")"
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
