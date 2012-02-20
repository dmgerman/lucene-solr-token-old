begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.base.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|query
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
name|spatial
operator|.
name|base
operator|.
name|context
operator|.
name|SpatialContext
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
name|spatial
operator|.
name|base
operator|.
name|exception
operator|.
name|InvalidShapeException
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
name|spatial
operator|.
name|base
operator|.
name|exception
operator|.
name|InvalidSpatialArgument
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
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|Shape
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import
begin_class
DECL|class|SpatialArgsParser
specifier|public
class|class
name|SpatialArgsParser
block|{
DECL|method|parse
specifier|public
name|SpatialArgs
name|parse
parameter_list|(
name|String
name|v
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
throws|throws
name|InvalidSpatialArgument
throws|,
name|InvalidShapeException
block|{
name|int
name|idx
init|=
name|v
operator|.
name|indexOf
argument_list|(
literal|'('
argument_list|)
decl_stmt|;
name|int
name|edx
init|=
name|v
operator|.
name|lastIndexOf
argument_list|(
literal|')'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
argument_list|<
literal|0
operator|||
name|idx
argument_list|>
name|edx
condition|)
block|{
throw|throw
operator|new
name|InvalidSpatialArgument
argument_list|(
literal|"missing parens: "
operator|+
name|v
argument_list|,
literal|null
argument_list|)
throw|;
block|}
name|SpatialOperation
name|op
init|=
name|SpatialOperation
operator|.
name|get
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|body
init|=
name|v
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|,
name|edx
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|body
operator|.
name|length
argument_list|()
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|InvalidSpatialArgument
argument_list|(
literal|"missing body : "
operator|+
name|v
argument_list|,
literal|null
argument_list|)
throw|;
block|}
name|Shape
name|shape
init|=
name|ctx
operator|.
name|readShape
argument_list|(
name|body
argument_list|)
decl_stmt|;
name|SpatialArgs
name|args
init|=
operator|new
name|SpatialArgs
argument_list|(
name|op
argument_list|,
name|shape
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|length
argument_list|()
operator|>
operator|(
name|edx
operator|+
literal|1
operator|)
condition|)
block|{
name|body
operator|=
name|v
operator|.
name|substring
argument_list|(
name|edx
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|body
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|aa
init|=
name|parseMap
argument_list|(
name|body
argument_list|)
decl_stmt|;
name|args
operator|.
name|setMin
argument_list|(
name|readDouble
argument_list|(
name|aa
operator|.
name|remove
argument_list|(
literal|"min"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|.
name|setMax
argument_list|(
name|readDouble
argument_list|(
name|aa
operator|.
name|remove
argument_list|(
literal|"max"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|.
name|setDistPrecision
argument_list|(
name|readDouble
argument_list|(
name|aa
operator|.
name|remove
argument_list|(
literal|"distPrec"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|aa
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidSpatialArgument
argument_list|(
literal|"unused parameters: "
operator|+
name|aa
argument_list|,
literal|null
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|args
return|;
block|}
DECL|method|readDouble
specifier|protected
specifier|static
name|Double
name|readDouble
parameter_list|(
name|String
name|v
parameter_list|)
block|{
return|return
name|v
operator|==
literal|null
condition|?
literal|null
else|:
name|Double
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
DECL|method|readBool
specifier|protected
specifier|static
name|boolean
name|readBool
parameter_list|(
name|String
name|v
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
return|return
name|v
operator|==
literal|null
condition|?
name|defaultValue
else|:
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|v
argument_list|)
return|;
block|}
DECL|method|parseMap
specifier|protected
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parseMap
parameter_list|(
name|String
name|body
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|body
argument_list|,
literal|" \n\t"
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|a
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|int
name|idx
init|=
name|a
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|String
name|k
init|=
name|a
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|String
name|v
init|=
name|a
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|a
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
block|}
end_class
end_unit
