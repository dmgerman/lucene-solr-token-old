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
name|MultipleFieldComparator
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
name|eq
operator|.
name|FieldEqualitor
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
name|eq
operator|.
name|MultipleFieldEqualitor
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
name|eq
operator|.
name|StreamEqualitor
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
name|ops
operator|.
name|ReduceOperation
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
name|ops
operator|.
name|StreamOperation
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
comment|/**  *  Iterates over a TupleStream and buffers Tuples that are equal based on a comparator.  *  This allows tuples to be grouped by common field(s).  *  *  The read() method emits one tuple per group. The fields of the emitted Tuple reflect the first tuple  *  encountered in the group.  *  *  Use the Tuple.getMaps() method to return all the Tuples in the group. This method returns  *  a list of maps (including the group head), which hold the data for each Tuple in the group.  *  *  Note: The ReducerStream requires that it's underlying stream be sorted and partitioned by the same  *  fields as it's comparator.  *  **/
end_comment
begin_class
DECL|class|ReducerStream
specifier|public
class|class
name|ReducerStream
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
name|PushBackStream
name|stream
decl_stmt|;
DECL|field|eq
specifier|private
name|StreamEqualitor
name|eq
decl_stmt|;
DECL|field|op
specifier|private
name|ReduceOperation
name|op
decl_stmt|;
DECL|field|needsReduce
specifier|private
name|boolean
name|needsReduce
decl_stmt|;
DECL|field|currentGroupHead
specifier|private
specifier|transient
name|Tuple
name|currentGroupHead
decl_stmt|;
DECL|method|ReducerStream
specifier|public
name|ReducerStream
parameter_list|(
name|TupleStream
name|stream
parameter_list|,
name|StreamEqualitor
name|eq
parameter_list|,
name|ReduceOperation
name|op
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|stream
argument_list|,
name|eq
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
DECL|method|ReducerStream
specifier|public
name|ReducerStream
parameter_list|(
name|TupleStream
name|stream
parameter_list|,
name|StreamComparator
name|comp
parameter_list|,
name|ReduceOperation
name|op
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|stream
argument_list|,
name|convertToEqualitor
argument_list|(
name|comp
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
DECL|method|convertToEqualitor
specifier|private
name|StreamEqualitor
name|convertToEqualitor
parameter_list|(
name|StreamComparator
name|comp
parameter_list|)
block|{
if|if
condition|(
name|comp
operator|instanceof
name|MultipleFieldComparator
condition|)
block|{
name|MultipleFieldComparator
name|mComp
init|=
operator|(
name|MultipleFieldComparator
operator|)
name|comp
decl_stmt|;
name|StreamEqualitor
index|[]
name|eqs
init|=
operator|new
name|StreamEqualitor
index|[
name|mComp
operator|.
name|getComps
argument_list|()
operator|.
name|length
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
name|mComp
operator|.
name|getComps
argument_list|()
operator|.
name|length
condition|;
operator|++
name|idx
control|)
block|{
name|eqs
index|[
name|idx
index|]
operator|=
name|convertToEqualitor
argument_list|(
name|mComp
operator|.
name|getComps
argument_list|()
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MultipleFieldEqualitor
argument_list|(
name|eqs
argument_list|)
return|;
block|}
else|else
block|{
name|FieldComparator
name|fComp
init|=
operator|(
name|FieldComparator
operator|)
name|comp
decl_stmt|;
return|return
operator|new
name|FieldEqualitor
argument_list|(
name|fComp
operator|.
name|getLeftFieldName
argument_list|()
argument_list|,
name|fComp
operator|.
name|getRightFieldName
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|ReducerStream
specifier|public
name|ReducerStream
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
name|byExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"by"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|operationExpressions
init|=
name|factory
operator|.
name|getExpressionOperandsRepresentingTypes
argument_list|(
name|expression
argument_list|,
name|ReduceOperation
operator|.
name|class
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
name|byExpression
operator|||
operator|!
operator|(
name|byExpression
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
literal|"Invalid expression %s - expecting single 'by' parameter listing fields to group by but didn't find one"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|ReduceOperation
name|reduceOperation
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|operationExpressions
operator|!=
literal|null
operator|&&
name|operationExpressions
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|StreamExpression
name|ex
init|=
name|operationExpressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|StreamOperation
name|operation
init|=
name|factory
operator|.
name|constructOperation
argument_list|(
name|ex
argument_list|)
decl_stmt|;
if|if
condition|(
name|operation
operator|instanceof
name|ReduceOperation
condition|)
block|{
name|reduceOperation
operator|=
operator|(
name|ReduceOperation
operator|)
name|operation
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The ReducerStream requires a ReduceOperation. A StreamOperation was provided."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The ReducerStream requires a ReduceOperation."
argument_list|)
throw|;
block|}
name|init
argument_list|(
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
argument_list|,
name|factory
operator|.
name|constructEqualitor
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|byExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|FieldEqualitor
operator|.
name|class
argument_list|)
argument_list|,
name|reduceOperation
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|TupleStream
name|stream
parameter_list|,
name|StreamEqualitor
name|eq
parameter_list|,
name|ReduceOperation
name|op
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|stream
operator|=
operator|new
name|PushBackStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|this
operator|.
name|eq
operator|=
name|eq
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
if|if
condition|(
operator|!
name|eq
operator|.
name|isDerivedFrom
argument_list|(
name|stream
operator|.
name|getStreamSort
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid ReducerStream - substream comparator (sort) must be a superset of this stream's comparator."
argument_list|)
throw|;
block|}
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
comment|// stream
name|expression
operator|.
name|addParameter
argument_list|(
name|stream
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
comment|// over
if|if
condition|(
name|eq
operator|instanceof
name|Expressible
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"by"
argument_list|,
operator|(
operator|(
name|Expressible
operator|)
name|eq
operator|)
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
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
literal|"This ReducerStream contains a non-expressible comparator - it cannot be converted to an expression"
argument_list|)
throw|;
block|}
if|if
condition|(
name|op
operator|instanceof
name|Expressible
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
name|op
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
literal|"This ReducerStream contains a non-expressible operation - it cannot be converted to an expression"
argument_list|)
throw|;
block|}
return|return
name|expression
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
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Tuple
name|t
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|EOF
condition|)
block|{
if|if
condition|(
name|needsReduce
condition|)
block|{
name|stream
operator|.
name|pushBack
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|needsReduce
operator|=
literal|false
expr_stmt|;
return|return
name|op
operator|.
name|reduce
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|t
return|;
block|}
block|}
if|if
condition|(
name|currentGroupHead
operator|==
literal|null
condition|)
block|{
name|currentGroupHead
operator|=
name|t
expr_stmt|;
name|op
operator|.
name|operate
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|needsReduce
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|eq
operator|.
name|test
argument_list|(
name|currentGroupHead
argument_list|,
name|t
argument_list|)
condition|)
block|{
name|op
operator|.
name|operate
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|needsReduce
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|stream
operator|.
name|pushBack
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|currentGroupHead
operator|=
literal|null
expr_stmt|;
name|needsReduce
operator|=
literal|false
expr_stmt|;
return|return
name|op
operator|.
name|reduce
argument_list|()
return|;
block|}
block|}
block|}
block|}
comment|/** Return the stream sort - ie, the order in which records are returned */
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
name|stream
operator|.
name|getStreamSort
argument_list|()
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
block|}
end_class
end_unit
