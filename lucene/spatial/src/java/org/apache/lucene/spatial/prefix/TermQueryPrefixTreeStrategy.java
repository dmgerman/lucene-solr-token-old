begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
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
name|queries
operator|.
name|TermsFilter
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
name|Filter
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
name|prefix
operator|.
name|tree
operator|.
name|Node
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
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTree
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
name|SpatialArgs
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
name|UnsupportedSpatialOperation
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
name|util
operator|.
name|BytesRef
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
begin_comment
comment|/**  * A basic implementation of {@link PrefixTreeStrategy} using a large {@link  * TermsFilter} of all the nodes from {@link SpatialPrefixTree#getNodes(com.spatial4j.core.shape.Shape,  * int, boolean)}. It only supports the search of indexed Point shapes.  *<p/>  * The precision of query shapes (distErrPct) is an important factor in using  * this Strategy. If the precision is too precise then it will result in many  * terms which will amount to a slower query.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TermQueryPrefixTreeStrategy
specifier|public
class|class
name|TermQueryPrefixTreeStrategy
extends|extends
name|PrefixTreeStrategy
block|{
DECL|method|TermQueryPrefixTreeStrategy
specifier|public
name|TermQueryPrefixTreeStrategy
parameter_list|(
name|SpatialPrefixTree
name|grid
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|grid
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|makeFilter
specifier|public
name|Filter
name|makeFilter
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
specifier|final
name|SpatialOperation
name|op
init|=
name|args
operator|.
name|getOperation
argument_list|()
decl_stmt|;
if|if
condition|(
name|op
operator|!=
name|SpatialOperation
operator|.
name|Intersects
condition|)
throw|throw
operator|new
name|UnsupportedSpatialOperation
argument_list|(
name|op
argument_list|)
throw|;
name|Shape
name|shape
init|=
name|args
operator|.
name|getShape
argument_list|()
decl_stmt|;
name|int
name|detailLevel
init|=
name|grid
operator|.
name|getLevelForDistance
argument_list|(
name|args
operator|.
name|resolveDistErr
argument_list|(
name|ctx
argument_list|,
name|distErrPct
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|cells
init|=
name|grid
operator|.
name|getNodes
argument_list|(
name|shape
argument_list|,
name|detailLevel
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|BytesRef
index|[]
name|terms
init|=
operator|new
name|BytesRef
index|[
name|cells
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Node
name|cell
range|:
name|cells
control|)
block|{
name|terms
index|[
name|i
operator|++
index|]
operator|=
operator|new
name|BytesRef
argument_list|(
name|cell
operator|.
name|getTokenString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TermsFilter
argument_list|(
name|getFieldName
argument_list|()
argument_list|,
name|terms
argument_list|)
return|;
block|}
block|}
end_class
end_unit
