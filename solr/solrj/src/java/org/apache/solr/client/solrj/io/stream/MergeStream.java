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
comment|/** * Merges two or more streams together ordering the Tuples based on a Comparator. * All streams must be sorted by the fields being compared - this will be validated on construction. **/
end_comment
begin_class
DECL|class|MergeStream
specifier|public
class|class
name|MergeStream
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
DECL|field|streams
specifier|private
name|PushBackStream
index|[]
name|streams
decl_stmt|;
DECL|field|comp
specifier|private
name|StreamComparator
name|comp
decl_stmt|;
DECL|method|MergeStream
specifier|public
name|MergeStream
parameter_list|(
name|TupleStream
name|streamA
parameter_list|,
name|TupleStream
name|streamB
parameter_list|,
name|StreamComparator
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|comp
argument_list|,
name|streamA
argument_list|,
name|streamB
argument_list|)
expr_stmt|;
block|}
DECL|method|MergeStream
specifier|public
name|MergeStream
parameter_list|(
name|StreamComparator
name|comp
parameter_list|,
name|TupleStream
modifier|...
name|streams
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|comp
argument_list|,
name|streams
argument_list|)
expr_stmt|;
block|}
DECL|method|MergeStream
specifier|public
name|MergeStream
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
name|onExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"on"
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
literal|1
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
name|streamExpressions
operator|.
name|size
argument_list|()
operator|<
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
literal|"Invalid expression %s - expecting at least two streams but found %d (must be PushBackStream types)"
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
name|onExpression
operator|||
operator|!
operator|(
name|onExpression
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
literal|"Invalid expression %s - expecting single 'on' parameter listing fields to merge on but didn't find one"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|TupleStream
index|[]
name|streams
init|=
operator|new
name|TupleStream
index|[
name|streamExpressions
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
name|streamExpressions
operator|.
name|size
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|streams
index|[
name|idx
index|]
operator|=
name|factory
operator|.
name|constructStream
argument_list|(
name|streamExpressions
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|init
argument_list|(
name|factory
operator|.
name|constructComparator
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|onExpression
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
argument_list|,
name|streams
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|StreamComparator
name|comp
parameter_list|,
name|TupleStream
modifier|...
name|streams
parameter_list|)
throws|throws
name|IOException
block|{
comment|// All streams must both be sorted so that comp can be derived from
for|for
control|(
name|TupleStream
name|stream
range|:
name|streams
control|)
block|{
if|if
condition|(
operator|!
name|comp
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
literal|"Invalid MergeStream - all substream comparators (sort) must be a superset of this stream's comparator."
argument_list|)
throw|;
block|}
block|}
comment|// Convert to PushBack streams so we can push back tuples
name|this
operator|.
name|streams
operator|=
operator|new
name|PushBackStream
index|[
name|streams
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|streams
operator|.
name|length
condition|;
operator|++
name|idx
control|)
block|{
name|this
operator|.
name|streams
index|[
name|idx
index|]
operator|=
operator|new
name|PushBackStream
argument_list|(
name|streams
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
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
comment|// streams
for|for
control|(
name|PushBackStream
name|stream
range|:
name|streams
control|)
block|{
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
block|}
comment|// on
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"on"
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
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
for|for
control|(
name|PushBackStream
name|stream
range|:
name|streams
control|)
block|{
name|stream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|PushBackStream
name|stream
range|:
name|streams
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|PushBackStream
name|stream
range|:
name|streams
control|)
block|{
name|stream
operator|.
name|open
argument_list|()
expr_stmt|;
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
for|for
control|(
name|PushBackStream
name|stream
range|:
name|streams
control|)
block|{
name|stream
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
comment|// might be able to optimize this by sorting the streams based on the next to read tuple from each.
comment|// if we can ensure the sort of the streams and update it in less than linear time then there would
comment|// be some performance gain. But, assuming the # of streams is kinda small then this might not be
comment|// worth it
name|Tuple
name|minimum
init|=
literal|null
decl_stmt|;
name|PushBackStream
name|minimumStream
init|=
literal|null
decl_stmt|;
for|for
control|(
name|PushBackStream
name|stream
range|:
name|streams
control|)
block|{
name|Tuple
name|current
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|EOF
condition|)
block|{
name|stream
operator|.
name|pushBack
argument_list|(
name|current
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
literal|null
operator|==
name|minimum
condition|)
block|{
name|minimum
operator|=
name|current
expr_stmt|;
name|minimumStream
operator|=
name|stream
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|comp
operator|.
name|compare
argument_list|(
name|current
argument_list|,
name|minimum
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// Push back on its stream
name|minimumStream
operator|.
name|pushBack
argument_list|(
name|minimum
argument_list|)
expr_stmt|;
name|minimum
operator|=
name|current
expr_stmt|;
name|minimumStream
operator|=
name|stream
expr_stmt|;
continue|continue;
block|}
else|else
block|{
name|stream
operator|.
name|pushBack
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If all EOF then min will be null, else min is the current minimum
if|if
condition|(
literal|null
operator|==
name|minimum
condition|)
block|{
comment|// return EOF, doesn't matter which cause we're done
return|return
name|streams
index|[
literal|0
index|]
operator|.
name|read
argument_list|()
return|;
block|}
return|return
name|minimum
return|;
comment|//    Tuple a = streamA.read();
comment|//    Tuple b = streamB.read();
comment|//
comment|//    if(a.EOF&& b.EOF) {
comment|//      return a;
comment|//    }
comment|//
comment|//    if(a.EOF) {
comment|//      streamA.pushBack(a);
comment|//      return b;
comment|//    }
comment|//
comment|//    if(b.EOF) {
comment|//      streamB.pushBack(b);
comment|//      return a;
comment|//    }
comment|//
comment|//    int c = comp.compare(a,b);
comment|//
comment|//    if(c< 0) {
comment|//      streamB.pushBack(b);
comment|//      return a;
comment|//    } else {
comment|//      streamA.pushBack(a);
comment|//      return b;
comment|//    }
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
block|}
end_class
end_unit
