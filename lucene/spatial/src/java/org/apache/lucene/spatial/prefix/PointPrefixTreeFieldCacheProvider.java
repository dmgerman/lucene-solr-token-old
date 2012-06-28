begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Point
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
name|util
operator|.
name|ShapeFieldCacheProvider
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
begin_comment
comment|/**  * @lucene.internal  */
end_comment
begin_class
DECL|class|PointPrefixTreeFieldCacheProvider
specifier|public
class|class
name|PointPrefixTreeFieldCacheProvider
extends|extends
name|ShapeFieldCacheProvider
argument_list|<
name|Point
argument_list|>
block|{
DECL|field|grid
specifier|final
name|SpatialPrefixTree
name|grid
decl_stmt|;
comment|//
DECL|method|PointPrefixTreeFieldCacheProvider
specifier|public
name|PointPrefixTreeFieldCacheProvider
parameter_list|(
name|SpatialPrefixTree
name|grid
parameter_list|,
name|String
name|shapeField
parameter_list|,
name|int
name|defaultSize
parameter_list|)
block|{
name|super
argument_list|(
name|shapeField
argument_list|,
name|defaultSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|grid
operator|=
name|grid
expr_stmt|;
block|}
comment|//A kluge that this is a field
DECL|field|scanCell
specifier|private
name|Node
name|scanCell
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|readShape
specifier|protected
name|Point
name|readShape
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
name|scanCell
operator|=
name|grid
operator|.
name|getNode
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|offset
argument_list|,
name|term
operator|.
name|length
argument_list|,
name|scanCell
argument_list|)
expr_stmt|;
return|return
name|scanCell
operator|.
name|isLeaf
argument_list|()
condition|?
name|scanCell
operator|.
name|getShape
argument_list|()
operator|.
name|getCenter
argument_list|()
else|:
literal|null
return|;
block|}
block|}
end_class
end_unit
