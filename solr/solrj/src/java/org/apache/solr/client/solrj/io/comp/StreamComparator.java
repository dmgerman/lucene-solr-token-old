begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io.comp
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
name|comp
package|;
end_package
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
name|Comparator
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
comment|/**  *  An equality field Comparator which compares a field of two Tuples and determines sort order.  **/
end_comment
begin_class
DECL|class|StreamComparator
specifier|public
class|class
name|StreamComparator
implements|implements
name|Comparator
argument_list|<
name|Tuple
argument_list|>
implements|,
name|Expressible
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
literal|1
decl_stmt|;
DECL|field|leftField
specifier|private
name|String
name|leftField
decl_stmt|;
DECL|field|rightField
specifier|private
name|String
name|rightField
decl_stmt|;
DECL|field|order
specifier|private
specifier|final
name|ComparatorOrder
name|order
decl_stmt|;
DECL|field|comparator
specifier|private
name|ComparatorLambda
name|comparator
decl_stmt|;
DECL|method|StreamComparator
specifier|public
name|StreamComparator
parameter_list|(
name|String
name|field
parameter_list|,
name|ComparatorOrder
name|order
parameter_list|)
block|{
name|this
operator|.
name|leftField
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|rightField
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
name|assignComparator
argument_list|()
expr_stmt|;
block|}
DECL|method|StreamComparator
specifier|public
name|StreamComparator
parameter_list|(
name|String
name|leftField
parameter_list|,
name|String
name|rightField
parameter_list|,
name|ComparatorOrder
name|order
parameter_list|)
block|{
name|this
operator|.
name|leftField
operator|=
name|leftField
expr_stmt|;
name|this
operator|.
name|rightField
operator|=
name|rightField
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
name|assignComparator
argument_list|()
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
name|leftField
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|leftField
operator|.
name|equals
argument_list|(
name|rightField
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|rightField
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|order
argument_list|)
expr_stmt|;
return|return
operator|new
name|StreamExpressionValue
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/*    * What're we doing here messing around with lambdas for the comparator logic?    * We want the compare(...) function to run as fast as possible because it will be called many many    * times over the lifetime of this object. For that reason we want to limit the number of comparisons    * taking place in the compare(...) function. Because this class supports both ascending and    * descending comparisons and the logic for each is slightly different, we want to do the     *   if(ascending){ compare like this } else { compare like this }    * check only once - we can do that in the constructor of this class, create a lambda, and then execute     * that lambda in the compare function. A little bit of branch prediction savings right here.    */
DECL|method|assignComparator
specifier|private
name|void
name|assignComparator
parameter_list|()
block|{
if|if
condition|(
name|ComparatorOrder
operator|.
name|DESCENDING
operator|==
name|order
condition|)
block|{
name|comparator
operator|=
operator|new
name|ComparatorLambda
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Tuple
name|leftTuple
parameter_list|,
name|Tuple
name|rightTuple
parameter_list|)
block|{
name|Comparable
name|leftComp
init|=
operator|(
name|Comparable
operator|)
name|leftTuple
operator|.
name|get
argument_list|(
name|leftField
argument_list|)
decl_stmt|;
name|Comparable
name|rightComp
init|=
operator|(
name|Comparable
operator|)
name|rightTuple
operator|.
name|get
argument_list|(
name|rightField
argument_list|)
decl_stmt|;
if|if
condition|(
name|leftComp
operator|==
name|rightComp
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// if both null then they are equal. if both are same ref then are equal
if|if
condition|(
literal|null
operator|==
name|leftComp
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
literal|null
operator|==
name|rightComp
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|rightComp
operator|.
name|compareTo
argument_list|(
name|leftComp
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
comment|// See above for black magic reasoning.
name|comparator
operator|=
operator|new
name|ComparatorLambda
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Tuple
name|leftTuple
parameter_list|,
name|Tuple
name|rightTuple
parameter_list|)
block|{
name|Comparable
name|leftComp
init|=
operator|(
name|Comparable
operator|)
name|leftTuple
operator|.
name|get
argument_list|(
name|leftField
argument_list|)
decl_stmt|;
name|Comparable
name|rightComp
init|=
operator|(
name|Comparable
operator|)
name|rightTuple
operator|.
name|get
argument_list|(
name|rightField
argument_list|)
decl_stmt|;
if|if
condition|(
name|leftComp
operator|==
name|rightComp
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// if both null then they are equal. if both are same ref then are equal
if|if
condition|(
literal|null
operator|==
name|leftComp
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
literal|null
operator|==
name|rightComp
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
name|leftComp
operator|.
name|compareTo
argument_list|(
name|rightComp
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Tuple
name|leftTuple
parameter_list|,
name|Tuple
name|rightTuple
parameter_list|)
block|{
return|return
name|comparator
operator|.
name|compare
argument_list|(
name|leftTuple
argument_list|,
name|rightTuple
argument_list|)
return|;
block|}
block|}
end_class
end_unit
