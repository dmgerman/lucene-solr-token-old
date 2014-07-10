begin_unit
begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Name
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ParametersFactory
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import
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
name|document
operator|.
name|FieldType
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
name|bbox
operator|.
name|BBoxStrategy
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
name|RecursivePrefixTreeStrategy
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
name|TermQueryPrefixTreeStrategy
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
name|GeohashPrefixTree
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
name|QuadPrefixTree
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
name|serialized
operator|.
name|SerializedDVStrategy
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
name|vector
operator|.
name|PointVectorStrategy
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
begin_class
DECL|class|DistanceStrategyTest
specifier|public
class|class
name|DistanceStrategyTest
extends|extends
name|StrategyTestCase
block|{
annotation|@
name|ParametersFactory
DECL|method|parameters
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|parameters
parameter_list|()
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|ctorArgs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|SpatialContext
name|ctx
init|=
name|SpatialContext
operator|.
name|GEO
decl_stmt|;
name|SpatialPrefixTree
name|grid
decl_stmt|;
name|SpatialStrategy
name|strategy
decl_stmt|;
name|grid
operator|=
operator|new
name|QuadPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
literal|"recursive_quad"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|grid
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|TermQueryPrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
literal|"termquery_geohash"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|PointVectorStrategy
argument_list|(
name|ctx
argument_list|,
literal|"pointvector"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|BBoxStrategy
argument_list|(
name|ctx
argument_list|,
literal|"bbox"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|SerializedDVStrategy
argument_list|(
name|ctx
argument_list|,
literal|"serialized"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
return|return
name|ctorArgs
return|;
block|}
comment|// this is a hack for clover!
DECL|class|Param
specifier|static
class|class
name|Param
block|{
DECL|field|strategy
name|SpatialStrategy
name|strategy
decl_stmt|;
DECL|method|Param
name|Param
parameter_list|(
name|SpatialStrategy
name|strategy
parameter_list|)
block|{
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|strategy
operator|.
name|getFieldName
argument_list|()
return|;
block|}
block|}
comment|//  private String fieldName;
DECL|method|DistanceStrategyTest
specifier|public
name|DistanceStrategyTest
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"strategy"
argument_list|)
name|Param
name|param
parameter_list|)
block|{
name|SpatialStrategy
name|strategy
init|=
name|param
operator|.
name|strategy
decl_stmt|;
name|this
operator|.
name|ctx
operator|=
name|strategy
operator|.
name|getSpatialContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
if|if
condition|(
name|strategy
operator|instanceof
name|BBoxStrategy
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|//disable indexing sometimes
name|BBoxStrategy
name|bboxStrategy
init|=
operator|(
name|BBoxStrategy
operator|)
name|strategy
decl_stmt|;
specifier|final
name|FieldType
name|fieldType
init|=
operator|new
name|FieldType
argument_list|(
name|bboxStrategy
operator|.
name|getFieldType
argument_list|()
argument_list|)
decl_stmt|;
name|fieldType
operator|.
name|setIndexed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|bboxStrategy
operator|.
name|setFieldType
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|needsDocValues
specifier|protected
name|boolean
name|needsDocValues
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Test
DECL|method|testDistanceOrder
specifier|public
name|void
name|testDistanceOrder
parameter_list|()
throws|throws
name|IOException
block|{
name|adoc
argument_list|(
literal|"100"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"101"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"103"
argument_list|,
operator|(
name|Shape
operator|)
literal|null
argument_list|)
expr_stmt|;
comment|//test score for nothing
name|adoc
argument_list|(
literal|"999"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//test deleted
name|commit
argument_list|()
expr_stmt|;
name|deleteDoc
argument_list|(
literal|"999"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
comment|//FYI distances are in docid order
name|checkDistValueSource
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|4
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|2.8274937f
argument_list|,
literal|5.0898066f
argument_list|,
literal|180f
argument_list|)
expr_stmt|;
name|checkDistValueSource
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|3.6043684f
argument_list|,
literal|0.9975641f
argument_list|,
literal|180f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRecipScore
specifier|public
name|void
name|testRecipScore
parameter_list|()
throws|throws
name|IOException
block|{
name|Point
name|p100
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|adoc
argument_list|(
literal|"100"
argument_list|,
name|p100
argument_list|)
expr_stmt|;
name|Point
name|p101
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|1
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|adoc
argument_list|(
literal|"101"
argument_list|,
name|p101
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"103"
argument_list|,
operator|(
name|Shape
operator|)
literal|null
argument_list|)
expr_stmt|;
comment|//test score for nothing
name|adoc
argument_list|(
literal|"999"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//test deleted
name|commit
argument_list|()
expr_stmt|;
name|deleteDoc
argument_list|(
literal|"999"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|double
name|dist
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|p100
argument_list|,
name|p101
argument_list|)
decl_stmt|;
name|Shape
name|queryShape
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
literal|2.01
argument_list|,
literal|0.99
argument_list|,
name|dist
argument_list|)
decl_stmt|;
name|checkValueSource
argument_list|(
name|strategy
operator|.
name|makeRecipDistanceValueSource
argument_list|(
name|queryShape
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|1.00f
block|,
literal|0.10f
block|,
literal|0f
block|}
argument_list|,
literal|0.09f
argument_list|)
expr_stmt|;
block|}
DECL|method|checkDistValueSource
name|void
name|checkDistValueSource
parameter_list|(
name|Point
name|pt
parameter_list|,
name|float
modifier|...
name|distances
parameter_list|)
throws|throws
name|IOException
block|{
name|float
name|multiplier
init|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|*
literal|100f
decl_stmt|;
name|float
index|[]
name|dists2
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|distances
argument_list|,
name|distances
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dists2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dists2
index|[
name|i
index|]
operator|*=
name|multiplier
expr_stmt|;
block|}
name|checkValueSource
argument_list|(
name|strategy
operator|.
name|makeDistanceValueSource
argument_list|(
name|pt
argument_list|,
name|multiplier
argument_list|)
argument_list|,
name|dists2
argument_list|,
literal|1.0e-3f
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
