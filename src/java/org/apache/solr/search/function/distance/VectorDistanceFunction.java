begin_unit
begin_package
DECL|package|org.apache.solr.search.function.distance
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|distance
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Searcher
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
name|SolrException
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
name|search
operator|.
name|function
operator|.
name|DocValues
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
name|search
operator|.
name|function
operator|.
name|ValueSource
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
name|search
operator|.
name|function
operator|.
name|MultiValueSource
import|;
end_import
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
name|Map
import|;
end_import
begin_comment
comment|/**  * Calculate the p-norm for a Vector.  See http://en.wikipedia.org/wiki/Lp_space  *<p/>  * Common cases:  *<ul>  *<li>0 = Sparseness calculation</li>  *<li>1 = Manhattan distance</li>  *<li>2 = Euclidean distance</li>  *<li>Integer.MAX_VALUE = infinite norm</li>  *</ul>  *  * @see SquaredEuclideanFunction for the special case  */
end_comment
begin_class
DECL|class|VectorDistanceFunction
specifier|public
class|class
name|VectorDistanceFunction
extends|extends
name|ValueSource
block|{
DECL|field|source1
DECL|field|source2
specifier|protected
name|MultiValueSource
name|source1
decl_stmt|,
name|source2
decl_stmt|;
DECL|field|power
specifier|protected
name|float
name|power
decl_stmt|;
DECL|field|oneOverPower
specifier|protected
name|float
name|oneOverPower
decl_stmt|;
DECL|method|VectorDistanceFunction
specifier|public
name|VectorDistanceFunction
parameter_list|(
name|float
name|power
parameter_list|,
name|MultiValueSource
name|source1
parameter_list|,
name|MultiValueSource
name|source2
parameter_list|)
block|{
if|if
condition|(
operator|(
name|source1
operator|.
name|dimension
argument_list|()
operator|!=
name|source2
operator|.
name|dimension
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Illegal number of sources"
argument_list|)
throw|;
block|}
name|this
operator|.
name|power
operator|=
name|power
expr_stmt|;
name|this
operator|.
name|oneOverPower
operator|=
literal|1
operator|/
name|power
expr_stmt|;
name|this
operator|.
name|source1
operator|=
name|source1
expr_stmt|;
name|this
operator|.
name|source2
operator|=
name|source2
expr_stmt|;
block|}
DECL|method|name
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
literal|"dist"
return|;
block|}
comment|/**    * Calculate the distance    *    * @param doc        The current doc    * @param dv1 The values from the first MultiValueSource    * @param dv2 The values from the second MultiValueSource    * @return The distance    */
DECL|method|distance
specifier|protected
name|double
name|distance
parameter_list|(
name|int
name|doc
parameter_list|,
name|DocValues
name|dv1
parameter_list|,
name|DocValues
name|dv2
parameter_list|)
block|{
name|double
name|result
init|=
literal|0
decl_stmt|;
comment|//Handle some special cases:
name|double
index|[]
name|vals1
init|=
operator|new
name|double
index|[
name|source1
operator|.
name|dimension
argument_list|()
index|]
decl_stmt|;
name|double
index|[]
name|vals2
init|=
operator|new
name|double
index|[
name|source1
operator|.
name|dimension
argument_list|()
index|]
decl_stmt|;
name|dv1
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|,
name|vals1
argument_list|)
expr_stmt|;
name|dv2
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|,
name|vals2
argument_list|)
expr_stmt|;
if|if
condition|(
name|power
operator|==
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|+=
name|vals1
index|[
name|i
index|]
operator|-
name|vals2
index|[
name|i
index|]
operator|==
literal|0
condition|?
literal|0
else|:
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|power
operator|==
literal|1.0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|+=
name|vals1
index|[
name|i
index|]
operator|-
name|vals2
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|power
operator|==
literal|2.0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|double
name|v
init|=
name|vals1
index|[
name|i
index|]
operator|-
name|vals2
index|[
name|i
index|]
decl_stmt|;
name|result
operator|+=
name|v
operator|*
name|v
expr_stmt|;
block|}
name|result
operator|=
name|Math
operator|.
name|sqrt
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|power
operator|==
name|Integer
operator|.
name|MAX_VALUE
operator|||
name|Double
operator|.
name|isInfinite
argument_list|(
name|power
argument_list|)
condition|)
block|{
comment|//infininte norm?
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|Math
operator|.
name|max
argument_list|(
name|vals1
index|[
name|i
index|]
argument_list|,
name|vals2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|+=
name|Math
operator|.
name|pow
argument_list|(
name|vals1
index|[
name|i
index|]
operator|-
name|vals2
index|[
name|i
index|]
argument_list|,
name|power
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|Math
operator|.
name|pow
argument_list|(
name|result
argument_list|,
name|oneOverPower
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocValues
name|vals1
init|=
name|source1
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
decl_stmt|;
specifier|final
name|DocValues
name|vals2
init|=
name|source2
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|byte
name|byteVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|short
name|shortVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|distance
argument_list|(
name|doc
argument_list|,
name|vals1
argument_list|,
name|vals2
argument_list|)
return|;
block|}
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|doubleVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
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
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|power
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|vals1
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|vals2
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|source1
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|source2
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|VectorDistanceFunction
operator|)
condition|)
return|return
literal|false
return|;
name|VectorDistanceFunction
name|that
init|=
operator|(
name|VectorDistanceFunction
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|compare
argument_list|(
name|that
operator|.
name|power
argument_list|,
name|power
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|source1
operator|.
name|equals
argument_list|(
name|that
operator|.
name|source1
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|source2
operator|.
name|equals
argument_list|(
name|that
operator|.
name|source2
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|source1
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|source2
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|power
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
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
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|power
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|source1
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|source2
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
