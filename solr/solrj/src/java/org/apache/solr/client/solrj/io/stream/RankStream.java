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
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|PriorityQueue
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
name|FieldComparator
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
begin_comment
comment|/** *  Iterates over a TupleStream and Ranks the topN tuples based on a Comparator. **/
end_comment
begin_class
DECL|class|RankStream
specifier|public
class|class
name|RankStream
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
DECL|field|stream
specifier|private
name|TupleStream
name|stream
decl_stmt|;
DECL|field|comp
specifier|private
name|StreamComparator
name|comp
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|top
specifier|private
specifier|transient
name|PriorityQueue
argument_list|<
name|Tuple
argument_list|>
name|top
decl_stmt|;
DECL|field|finished
specifier|private
specifier|transient
name|boolean
name|finished
init|=
literal|false
decl_stmt|;
DECL|field|topList
specifier|private
specifier|transient
name|LinkedList
argument_list|<
name|Tuple
argument_list|>
name|topList
decl_stmt|;
DECL|method|RankStream
specifier|public
name|RankStream
parameter_list|(
name|TupleStream
name|tupleStream
parameter_list|,
name|int
name|size
parameter_list|,
name|StreamComparator
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|tupleStream
argument_list|,
name|size
argument_list|,
name|comp
argument_list|)
expr_stmt|;
block|}
DECL|method|RankStream
specifier|public
name|RankStream
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
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|streamExpressions
init|=
name|factory
operator|.
name|getExpressionOperandsRepresentingTypes
argument_list|(
name|expression
argument_list|,
name|Expressible
operator|.
name|class
argument_list|,
name|TupleStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|nParam
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"n"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|sortExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"sort"
argument_list|)
decl_stmt|;
comment|// validate expression contains only what we want.
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
name|streamExpressions
operator|.
name|size
argument_list|()
operator|+
literal|2
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
literal|"Invalid expression %s - unknown operands found"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|==
name|nParam
operator|||
literal|null
operator|==
name|nParam
operator|.
name|getParameter
argument_list|()
operator|||
operator|!
operator|(
name|nParam
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
operator|)
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
literal|"Invalid expression %s - expecting a single 'n' parameter of type positive integer but didn't find one"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|nStr
init|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|nParam
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|nInt
init|=
literal|0
decl_stmt|;
try|try
block|{
name|nInt
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|nStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|nInt
operator|<=
literal|0
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
literal|"invalid expression %s - topN '%s' must be greater than 0."
argument_list|,
name|expression
argument_list|,
name|nStr
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
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
literal|"invalid expression %s - topN '%s' is not a valid integer."
argument_list|,
name|expression
argument_list|,
name|nStr
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
literal|1
operator|!=
name|streamExpressions
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
literal|"Invalid expression %s - expecting a single stream but found %d"
argument_list|,
name|expression
argument_list|,
name|streamExpressions
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|==
name|sortExpression
operator|||
operator|!
operator|(
name|sortExpression
operator|.
name|getParameter
argument_list|()
operator|instanceof
name|StreamExpressionValue
operator|)
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
literal|"Invalid expression %s - expecting single 'over' parameter listing fields to unique over but didn't find one"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|TupleStream
name|stream
init|=
name|factory
operator|.
name|constructStream
argument_list|(
name|streamExpressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|StreamComparator
name|comp
init|=
name|factory
operator|.
name|constructComparator
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|sortExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|FieldComparator
operator|.
name|class
argument_list|)
decl_stmt|;
name|init
argument_list|(
name|stream
argument_list|,
name|nInt
argument_list|,
name|comp
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|TupleStream
name|tupleStream
parameter_list|,
name|int
name|size
parameter_list|,
name|StreamComparator
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|stream
operator|=
name|tupleStream
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
comment|// Rank stream does not demand that its order is derivable from the order of the incoming stream. No derivation check required
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpression
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|toExpression
argument_list|(
name|factory
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|toExpression
specifier|private
name|StreamExpression
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|,
name|boolean
name|includeStreams
parameter_list|)
throws|throws
name|IOException
block|{
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
comment|// n
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"n"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|size
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeStreams
condition|)
block|{
comment|// stream
if|if
condition|(
name|stream
operator|instanceof
name|Expressible
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|(
operator|(
name|Expressible
operator|)
name|stream
operator|)
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This RankStream contains a non-expressible TupleStream - it cannot be converted to an expression"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|expression
operator|.
name|addParameter
argument_list|(
literal|"<stream>"
argument_list|)
expr_stmt|;
block|}
comment|// sort
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"sort"
argument_list|,
name|comp
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
return|return
operator|new
name|StreamExplanation
argument_list|(
name|getStreamNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withChildren
argument_list|(
operator|new
name|Explanation
index|[]
block|{
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
block|}
argument_list|)
operator|.
name|withFunctionName
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
operator|.
name|withImplementingClass
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withExpressionType
argument_list|(
name|ExpressionType
operator|.
name|STREAM_DECORATOR
argument_list|)
operator|.
name|withExpression
argument_list|(
name|toExpression
argument_list|(
name|factory
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withHelper
argument_list|(
name|comp
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
argument_list|)
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
name|this
operator|.
name|stream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
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
argument_list|<
name|TupleStream
argument_list|>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|stream
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|top
operator|=
operator|new
name|PriorityQueue
argument_list|<
name|Tuple
argument_list|>
argument_list|(
name|size
argument_list|,
operator|new
name|ReverseComp
argument_list|(
name|comp
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|topList
operator|=
operator|new
name|LinkedList
argument_list|<
name|Tuple
argument_list|>
argument_list|()
expr_stmt|;
name|stream
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getComparator
specifier|public
name|StreamComparator
name|getComparator
parameter_list|()
block|{
return|return
name|this
operator|.
name|comp
return|;
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
name|finished
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Tuple
name|tuple
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
name|finished
operator|=
literal|true
expr_stmt|;
name|int
name|s
init|=
name|top
operator|.
name|size
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
name|s
condition|;
name|i
operator|++
control|)
block|{
name|Tuple
name|t
init|=
name|top
operator|.
name|poll
argument_list|()
decl_stmt|;
name|topList
operator|.
name|addFirst
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|topList
operator|.
name|addLast
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
if|if
condition|(
name|top
operator|.
name|size
argument_list|()
operator|>=
name|size
condition|)
block|{
name|Tuple
name|peek
init|=
name|top
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|comp
operator|.
name|compare
argument_list|(
name|tuple
argument_list|,
name|peek
argument_list|)
operator|<
literal|0
condition|)
block|{
name|top
operator|.
name|poll
argument_list|()
expr_stmt|;
name|top
operator|.
name|add
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|top
operator|.
name|add
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|topList
operator|.
name|pollFirst
argument_list|()
return|;
block|}
comment|/** Return the stream sort - ie, the order in which records are returned */
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
name|comp
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
DECL|class|ReverseComp
class|class
name|ReverseComp
implements|implements
name|Comparator
argument_list|<
name|Tuple
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|comp
specifier|private
name|StreamComparator
name|comp
decl_stmt|;
DECL|method|ReverseComp
specifier|public
name|ReverseComp
parameter_list|(
name|StreamComparator
name|comp
parameter_list|)
block|{
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Tuple
name|t1
parameter_list|,
name|Tuple
name|t2
parameter_list|)
block|{
return|return
name|comp
operator|.
name|compare
argument_list|(
name|t1
argument_list|,
name|t2
argument_list|)
operator|*
operator|(
operator|-
literal|1
operator|)
return|;
block|}
block|}
block|}
end_class
end_unit
