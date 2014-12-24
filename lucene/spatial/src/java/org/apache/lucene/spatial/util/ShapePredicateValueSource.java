begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
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
name|index
operator|.
name|LeafReaderContext
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
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
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|BoolDocValues
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
name|Explanation
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|query
operator|.
name|SpatialOperation
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
comment|/**  * A boolean ValueSource that compares a shape from a provided ValueSource with a given Shape and sees  * if it matches a given {@link SpatialOperation} (the predicate).  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|ShapePredicateValueSource
specifier|public
class|class
name|ShapePredicateValueSource
extends|extends
name|ValueSource
block|{
DECL|field|shapeValuesource
specifier|private
specifier|final
name|ValueSource
name|shapeValuesource
decl_stmt|;
comment|//the left hand side
DECL|field|op
specifier|private
specifier|final
name|SpatialOperation
name|op
decl_stmt|;
DECL|field|queryShape
specifier|private
specifier|final
name|Shape
name|queryShape
decl_stmt|;
comment|//the right hand side (constant)
comment|/**    *    * @param shapeValuesource Must yield {@link Shape} instances from its objectVal(doc). If null    *                         then the result is false. This is the left-hand (indexed) side.    * @param op the predicate    * @param queryShape The shape on the right-hand (query) side.    */
DECL|method|ShapePredicateValueSource
specifier|public
name|ShapePredicateValueSource
parameter_list|(
name|ValueSource
name|shapeValuesource
parameter_list|,
name|SpatialOperation
name|op
parameter_list|,
name|Shape
name|queryShape
parameter_list|)
block|{
name|this
operator|.
name|shapeValuesource
operator|=
name|shapeValuesource
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|this
operator|.
name|queryShape
operator|=
name|queryShape
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|shapeValuesource
operator|+
literal|" "
operator|+
name|op
operator|+
literal|" "
operator|+
name|queryShape
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
name|shapeValuesource
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
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FunctionValues
name|shapeValues
init|=
name|shapeValuesource
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|BoolDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|boolVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|Shape
name|indexedShape
init|=
operator|(
name|Shape
operator|)
name|shapeValues
operator|.
name|objectVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexedShape
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|op
operator|.
name|evaluate
argument_list|(
name|indexedShape
argument_list|,
name|queryShape
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|Explanation
name|exp
init|=
name|super
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|shapeValues
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|exp
return|;
block|}
block|}
return|;
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
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ShapePredicateValueSource
name|that
init|=
operator|(
name|ShapePredicateValueSource
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|shapeValuesource
operator|.
name|equals
argument_list|(
name|that
operator|.
name|shapeValuesource
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|op
operator|.
name|equals
argument_list|(
name|that
operator|.
name|op
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|queryShape
operator|.
name|equals
argument_list|(
name|that
operator|.
name|queryShape
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
name|shapeValuesource
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
name|op
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
name|queryShape
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
