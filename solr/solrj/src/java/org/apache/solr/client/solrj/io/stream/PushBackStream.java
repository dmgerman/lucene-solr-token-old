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
name|StreamFactory
import|;
end_import
begin_comment
comment|/**  * A TupleStream that allows a single Tuple to be pushed back onto the stream after it's been read.  * This is a useful class when building streams that maintain the order of Tuples between multiple  * substreams.  **/
end_comment
begin_class
DECL|class|PushBackStream
specifier|public
class|class
name|PushBackStream
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
DECL|field|tuple
specifier|private
name|Tuple
name|tuple
decl_stmt|;
DECL|method|PushBackStream
specifier|public
name|PushBackStream
parameter_list|(
name|TupleStream
name|stream
parameter_list|)
block|{
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
block|}
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
if|if
condition|(
name|stream
operator|instanceof
name|Expressible
condition|)
block|{
return|return
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
return|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This PushBackStream contains a non-expressible TupleStream - it cannot be converted to an expression"
argument_list|)
throw|;
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
DECL|method|pushBack
specifier|public
name|void
name|pushBack
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
block|{
name|this
operator|.
name|tuple
operator|=
name|tuple
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
if|if
condition|(
name|tuple
operator|!=
literal|null
condition|)
block|{
name|Tuple
name|t
init|=
name|tuple
decl_stmt|;
name|tuple
operator|=
literal|null
expr_stmt|;
return|return
name|t
return|;
block|}
else|else
block|{
return|return
name|stream
operator|.
name|read
argument_list|()
return|;
block|}
block|}
comment|/** Return the stream sort - ie, the order in which records are returned    *  This returns the streamSort of the substream */
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
