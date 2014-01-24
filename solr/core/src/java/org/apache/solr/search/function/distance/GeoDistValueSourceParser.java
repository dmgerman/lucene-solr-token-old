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
name|distance
operator|.
name|DistanceUtils
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
name|valuesource
operator|.
name|ConstNumberSource
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
name|valuesource
operator|.
name|DoubleConstValueSource
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
name|valuesource
operator|.
name|MultiValueSource
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
name|valuesource
operator|.
name|VectorValueSource
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
name|SpatialStrategy
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
name|params
operator|.
name|SpatialParams
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
name|schema
operator|.
name|AbstractSpatialFieldType
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
name|schema
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
name|solr
operator|.
name|schema
operator|.
name|SchemaField
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
name|FunctionQParser
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
name|SyntaxError
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
name|ValueSourceParser
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
name|util
operator|.
name|SpatialUtils
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
name|Collections
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
comment|/**  * Parses "geodist" creating {@link HaversineConstFunction} or {@link HaversineFunction}  * or calling {@link SpatialStrategy#makeDistanceValueSource(com.spatial4j.core.shape.Point,double)}.  */
end_comment
begin_class
DECL|class|GeoDistValueSourceParser
specifier|public
class|class
name|GeoDistValueSourceParser
extends|extends
name|ValueSourceParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|SyntaxError
block|{
comment|// TODO: dispatch through SpatialQueryable in the future?
comment|//note: parseValueSourceList can't handle a field reference to an AbstractSpatialFieldType,
comment|// so those fields are expressly handled via sfield=
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
init|=
name|fp
operator|.
name|parseValueSourceList
argument_list|()
decl_stmt|;
comment|// "m" is a multi-value source, "x" is a single-value source
comment|// allow (m,m) (m,x,x) (x,x,m) (x,x,x,x)
comment|// if not enough points are present, "pt" will be checked first, followed by "sfield".
name|MultiValueSource
name|mv1
init|=
literal|null
decl_stmt|;
name|MultiValueSource
name|mv2
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// nothing to do now
block|}
elseif|else
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|ValueSource
name|vs
init|=
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|vs
operator|instanceof
name|MultiValueSource
operator|)
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"geodist - invalid parameters:"
operator|+
name|sources
argument_list|)
throw|;
block|}
name|mv1
operator|=
operator|(
name|MultiValueSource
operator|)
name|vs
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
block|{
name|ValueSource
name|vs1
init|=
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ValueSource
name|vs2
init|=
name|sources
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|vs1
operator|instanceof
name|MultiValueSource
operator|&&
name|vs2
operator|instanceof
name|MultiValueSource
condition|)
block|{
name|mv1
operator|=
operator|(
name|MultiValueSource
operator|)
name|vs1
expr_stmt|;
name|mv2
operator|=
operator|(
name|MultiValueSource
operator|)
name|vs2
expr_stmt|;
block|}
else|else
block|{
name|mv1
operator|=
name|makeMV
argument_list|(
name|sources
argument_list|,
name|sources
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
block|{
name|ValueSource
name|vs1
init|=
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ValueSource
name|vs2
init|=
name|sources
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|vs1
operator|instanceof
name|MultiValueSource
condition|)
block|{
comment|// (m,x,x)
name|mv1
operator|=
operator|(
name|MultiValueSource
operator|)
name|vs1
expr_stmt|;
name|mv2
operator|=
name|makeMV
argument_list|(
name|sources
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
name|sources
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// (x,x,m)
name|mv1
operator|=
name|makeMV
argument_list|(
name|sources
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|sources
argument_list|)
expr_stmt|;
name|vs1
operator|=
name|sources
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|vs1
operator|instanceof
name|MultiValueSource
operator|)
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"geodist - invalid parameters:"
operator|+
name|sources
argument_list|)
throw|;
block|}
name|mv2
operator|=
operator|(
name|MultiValueSource
operator|)
name|vs1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|==
literal|4
condition|)
block|{
name|mv1
operator|=
name|makeMV
argument_list|(
name|sources
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|sources
argument_list|)
expr_stmt|;
name|mv2
operator|=
name|makeMV
argument_list|(
name|sources
operator|.
name|subList
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|,
name|sources
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|>
literal|4
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"geodist - invalid parameters:"
operator|+
name|sources
argument_list|)
throw|;
block|}
if|if
condition|(
name|mv1
operator|==
literal|null
condition|)
block|{
name|mv1
operator|=
name|parsePoint
argument_list|(
name|fp
argument_list|)
expr_stmt|;
name|mv2
operator|=
name|parseSfield
argument_list|(
name|fp
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mv2
operator|==
literal|null
condition|)
block|{
name|mv2
operator|=
name|parsePoint
argument_list|(
name|fp
argument_list|)
expr_stmt|;
if|if
condition|(
name|mv2
operator|==
literal|null
condition|)
name|mv2
operator|=
name|parseSfield
argument_list|(
name|fp
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mv1
operator|==
literal|null
operator|||
name|mv2
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"geodist - not enough parameters:"
operator|+
name|sources
argument_list|)
throw|;
block|}
comment|// We have all the parameters at this point, now check if one of the points is constant
name|double
index|[]
name|constants
decl_stmt|;
comment|//latLon
name|constants
operator|=
name|getConstants
argument_list|(
name|mv1
argument_list|)
expr_stmt|;
name|MultiValueSource
name|other
init|=
name|mv2
decl_stmt|;
if|if
condition|(
name|constants
operator|==
literal|null
condition|)
block|{
name|constants
operator|=
name|getConstants
argument_list|(
name|mv2
argument_list|)
expr_stmt|;
name|other
operator|=
name|mv1
expr_stmt|;
block|}
comment|// At this point we dispatch to one of:
comment|// * SpatialStrategy.makeDistanceValueSource
comment|// * HaversineConstFunction
comment|// * HaversineFunction
comment|// sfield can only be in mv2, according to the logic above
if|if
condition|(
name|mv2
operator|instanceof
name|SpatialStrategyMultiValueSource
condition|)
block|{
if|if
condition|(
name|constants
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"When using AbstractSpatialFieldType (e.g. RPT not LatLonType),"
operator|+
literal|" the point must be supplied as constants"
argument_list|)
throw|;
comment|// note: uses Haversine by default but can be changed via distCalc=...
name|SpatialStrategy
name|strategy
init|=
operator|(
operator|(
name|SpatialStrategyMultiValueSource
operator|)
name|mv2
operator|)
operator|.
name|strategy
decl_stmt|;
name|Point
name|queryPoint
init|=
name|strategy
operator|.
name|getSpatialContext
argument_list|()
operator|.
name|makePoint
argument_list|(
name|constants
index|[
literal|1
index|]
argument_list|,
name|constants
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
return|return
name|strategy
operator|.
name|makeDistanceValueSource
argument_list|(
name|queryPoint
argument_list|,
name|DistanceUtils
operator|.
name|DEG_TO_KM
argument_list|)
return|;
block|}
if|if
condition|(
name|constants
operator|!=
literal|null
operator|&&
name|other
operator|instanceof
name|VectorValueSource
condition|)
block|{
return|return
operator|new
name|HaversineConstFunction
argument_list|(
name|constants
index|[
literal|0
index|]
argument_list|,
name|constants
index|[
literal|1
index|]
argument_list|,
operator|(
name|VectorValueSource
operator|)
name|other
argument_list|)
return|;
block|}
return|return
operator|new
name|HaversineFunction
argument_list|(
name|mv1
argument_list|,
name|mv2
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** make a MultiValueSource from two non MultiValueSources */
DECL|method|makeMV
specifier|private
name|VectorValueSource
name|makeMV
parameter_list|(
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
parameter_list|,
name|List
argument_list|<
name|ValueSource
argument_list|>
name|orig
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|ValueSource
name|vs1
init|=
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ValueSource
name|vs2
init|=
name|sources
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|vs1
operator|instanceof
name|MultiValueSource
operator|||
name|vs2
operator|instanceof
name|MultiValueSource
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"geodist - invalid parameters:"
operator|+
name|orig
argument_list|)
throw|;
block|}
return|return
operator|new
name|VectorValueSource
argument_list|(
name|sources
argument_list|)
return|;
block|}
DECL|method|parsePoint
specifier|private
name|MultiValueSource
name|parsePoint
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|String
name|ptStr
init|=
name|fp
operator|.
name|getParam
argument_list|(
name|SpatialParams
operator|.
name|POINT
argument_list|)
decl_stmt|;
if|if
condition|(
name|ptStr
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Point
name|point
init|=
name|SpatialUtils
operator|.
name|parsePointSolrException
argument_list|(
name|ptStr
argument_list|,
name|SpatialContext
operator|.
name|GEO
argument_list|)
decl_stmt|;
comment|//assume Lat Lon order
return|return
operator|new
name|VectorValueSource
argument_list|(
name|Arrays
operator|.
expr|<
name|ValueSource
operator|>
name|asList
argument_list|(
operator|new
name|DoubleConstValueSource
argument_list|(
name|point
operator|.
name|getY
argument_list|()
argument_list|)
argument_list|,
operator|new
name|DoubleConstValueSource
argument_list|(
name|point
operator|.
name|getX
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getConstants
specifier|private
name|double
index|[]
name|getConstants
parameter_list|(
name|MultiValueSource
name|vs
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|vs
operator|instanceof
name|VectorValueSource
operator|)
condition|)
return|return
literal|null
return|;
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
init|=
operator|(
operator|(
name|VectorValueSource
operator|)
name|vs
operator|)
operator|.
name|getSources
argument_list|()
decl_stmt|;
if|if
condition|(
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|ConstNumberSource
operator|&&
name|sources
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|ConstNumberSource
condition|)
block|{
return|return
operator|new
name|double
index|[]
block|{
operator|(
operator|(
name|ConstNumberSource
operator|)
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getDouble
argument_list|()
block|,
operator|(
operator|(
name|ConstNumberSource
operator|)
name|sources
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getDouble
argument_list|()
block|}
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|parseSfield
specifier|private
name|MultiValueSource
name|parseSfield
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|String
name|sfield
init|=
name|fp
operator|.
name|getParam
argument_list|(
name|SpatialParams
operator|.
name|FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|sfield
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|SchemaField
name|sf
init|=
name|fp
operator|.
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|sfield
argument_list|)
decl_stmt|;
name|FieldType
name|type
init|=
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|AbstractSpatialFieldType
condition|)
block|{
name|AbstractSpatialFieldType
name|asft
init|=
operator|(
name|AbstractSpatialFieldType
operator|)
name|type
decl_stmt|;
return|return
operator|new
name|SpatialStrategyMultiValueSource
argument_list|(
name|asft
operator|.
name|getStrategy
argument_list|(
name|sfield
argument_list|)
argument_list|)
return|;
block|}
name|ValueSource
name|vs
init|=
name|type
operator|.
name|getValueSource
argument_list|(
name|sf
argument_list|,
name|fp
argument_list|)
decl_stmt|;
if|if
condition|(
name|vs
operator|instanceof
name|MultiValueSource
condition|)
block|{
return|return
operator|(
name|MultiValueSource
operator|)
name|vs
return|;
block|}
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"Spatial field must implement MultiValueSource or extend AbstractSpatialFieldType:"
operator|+
name|sf
argument_list|)
throw|;
block|}
comment|/** An unfortunate hack to use a {@link SpatialStrategy} instead of    * a ValueSource. */
DECL|class|SpatialStrategyMultiValueSource
specifier|private
specifier|static
class|class
name|SpatialStrategyMultiValueSource
extends|extends
name|VectorValueSource
block|{
DECL|field|strategy
specifier|final
name|SpatialStrategy
name|strategy
decl_stmt|;
DECL|method|SpatialStrategyMultiValueSource
specifier|public
name|SpatialStrategyMultiValueSource
parameter_list|(
name|SpatialStrategy
name|strategy
parameter_list|)
block|{
name|super
argument_list|(
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
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
DECL|method|getSources
specifier|public
name|List
argument_list|<
name|ValueSource
argument_list|>
name|getSources
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
block|}
end_class
end_unit
