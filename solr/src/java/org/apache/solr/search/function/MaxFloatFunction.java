begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.function
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
package|;
end_package
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
name|IndexSearcher
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
comment|/**  * Returns the max of a ValueSource and a float  * (which is useful for "bottoming out" another function at 0.0,  * or some positive number).  *<br>  * Normally Used as an argument to a {@link FunctionQuery}  *  * @version $Id$  */
end_comment
begin_class
DECL|class|MaxFloatFunction
specifier|public
class|class
name|MaxFloatFunction
extends|extends
name|ValueSource
block|{
DECL|field|source
specifier|protected
specifier|final
name|ValueSource
name|source
decl_stmt|;
DECL|field|fval
specifier|protected
specifier|final
name|float
name|fval
decl_stmt|;
DECL|method|MaxFloatFunction
specifier|public
name|MaxFloatFunction
parameter_list|(
name|ValueSource
name|source
parameter_list|,
name|float
name|fval
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|fval
operator|=
name|fval
expr_stmt|;
block|}
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"max("
operator|+
name|source
operator|.
name|description
argument_list|()
operator|+
literal|","
operator|+
name|fval
operator|+
literal|")"
return|;
block|}
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
name|vals
init|=
name|source
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
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|float
name|v
init|=
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|v
operator|<
name|fval
condition|?
name|fval
else|:
name|v
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
name|floatVal
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
name|floatVal
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
operator|(
name|double
operator|)
name|floatVal
argument_list|(
name|doc
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
name|Float
operator|.
name|toString
argument_list|(
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
literal|"max("
operator|+
name|vals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|","
operator|+
name|fval
operator|+
literal|")"
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
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|source
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|fval
argument_list|)
decl_stmt|;
name|h
operator|=
operator|(
name|h
operator|>>>
literal|2
operator|)
operator||
operator|(
name|h
operator|<<
literal|30
operator|)
expr_stmt|;
return|return
name|h
operator|+
name|source
operator|.
name|hashCode
argument_list|()
return|;
block|}
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
name|MaxFloatFunction
operator|.
name|class
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|MaxFloatFunction
name|other
init|=
operator|(
name|MaxFloatFunction
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|fval
operator|==
name|other
operator|.
name|fval
operator|&&
name|this
operator|.
name|source
operator|.
name|equals
argument_list|(
name|other
operator|.
name|source
argument_list|)
return|;
block|}
block|}
end_class
end_unit
