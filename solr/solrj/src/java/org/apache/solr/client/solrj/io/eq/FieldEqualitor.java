begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io.eq
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
name|eq
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
comment|/**  *  An equality field Equalitor which compares a field of two Tuples and determines if they are equal.  **/
end_comment
begin_class
DECL|class|FieldEqualitor
specifier|public
class|class
name|FieldEqualitor
implements|implements
name|StreamEqualitor
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
DECL|field|leftFieldName
specifier|private
name|String
name|leftFieldName
decl_stmt|;
DECL|field|rightFieldName
specifier|private
name|String
name|rightFieldName
decl_stmt|;
DECL|method|FieldEqualitor
specifier|public
name|FieldEqualitor
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|init
argument_list|(
name|fieldName
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldEqualitor
specifier|public
name|FieldEqualitor
parameter_list|(
name|String
name|leftFieldName
parameter_list|,
name|String
name|rightFieldName
parameter_list|)
block|{
name|init
argument_list|(
name|leftFieldName
argument_list|,
name|rightFieldName
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|leftFieldName
parameter_list|,
name|String
name|rightFieldName
parameter_list|)
block|{
name|this
operator|.
name|leftFieldName
operator|=
name|leftFieldName
expr_stmt|;
name|this
operator|.
name|rightFieldName
operator|=
name|rightFieldName
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
name|leftFieldName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|leftFieldName
operator|.
name|equals
argument_list|(
name|rightFieldName
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
name|rightFieldName
argument_list|)
expr_stmt|;
block|}
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
DECL|method|test
specifier|public
name|boolean
name|test
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
name|leftFieldName
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
name|rightFieldName
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
literal|true
return|;
block|}
comment|// if both null then they are equal. if both are same ref then are equal
if|if
condition|(
literal|null
operator|==
name|leftComp
operator|||
literal|null
operator|==
name|rightComp
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|0
operator|==
name|leftComp
operator|.
name|compareTo
argument_list|(
name|rightComp
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isDerivedFrom
specifier|public
name|boolean
name|isDerivedFrom
parameter_list|(
name|StreamEqualitor
name|base
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|base
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|base
operator|instanceof
name|FieldEqualitor
condition|)
block|{
name|FieldEqualitor
name|baseEq
init|=
operator|(
name|FieldEqualitor
operator|)
name|base
decl_stmt|;
return|return
name|leftFieldName
operator|.
name|equals
argument_list|(
name|baseEq
operator|.
name|leftFieldName
argument_list|)
operator|&&
name|rightFieldName
operator|.
name|equals
argument_list|(
name|baseEq
operator|.
name|rightFieldName
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|base
operator|instanceof
name|MultipleFieldEqualitor
condition|)
block|{
comment|// must equal the first one
name|MultipleFieldEqualitor
name|baseEqs
init|=
operator|(
name|MultipleFieldEqualitor
operator|)
name|base
decl_stmt|;
if|if
condition|(
name|baseEqs
operator|.
name|getEqs
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
return|return
name|isDerivedFrom
argument_list|(
name|baseEqs
operator|.
name|getEqs
argument_list|()
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isDerivedFrom
specifier|public
name|boolean
name|isDerivedFrom
parameter_list|(
name|StreamComparator
name|base
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|base
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|base
operator|instanceof
name|FieldComparator
condition|)
block|{
name|FieldComparator
name|baseComp
init|=
operator|(
name|FieldComparator
operator|)
name|base
decl_stmt|;
return|return
name|leftFieldName
operator|.
name|equals
argument_list|(
name|baseComp
operator|.
name|getFieldName
argument_list|()
argument_list|)
operator|&&
name|rightFieldName
operator|.
name|equals
argument_list|(
name|baseComp
operator|.
name|getFieldName
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|base
operator|instanceof
name|MultipleFieldComparator
condition|)
block|{
comment|// must equal the first one
name|MultipleFieldComparator
name|baseComps
init|=
operator|(
name|MultipleFieldComparator
operator|)
name|base
decl_stmt|;
if|if
condition|(
name|baseComps
operator|.
name|getComps
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
return|return
name|isDerivedFrom
argument_list|(
name|baseComps
operator|.
name|getComps
argument_list|()
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
