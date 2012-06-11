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
name|DoubleDocValues
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
name|AtomicReaderContext
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
name|context
operator|.
name|simple
operator|.
name|SimpleSpatialContext
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
name|DistanceCalculator
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
name|DistanceUnits
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
name|GeodesicSphereDistCalc
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
name|util
operator|.
name|GeohashUtils
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
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  *  Calculate the Haversine distance between two geo hash codes.  *  *<p/>  * Ex: ghhsin(ValueSource, ValueSource, radius)  *<p/>  *  * @see org.apache.solr.search.function.distance.HaversineFunction for more details on the implementation  *  **/
end_comment
begin_class
DECL|class|GeohashHaversineFunction
specifier|public
class|class
name|GeohashHaversineFunction
extends|extends
name|ValueSource
block|{
DECL|field|geoHash1
DECL|field|geoHash2
specifier|private
name|ValueSource
name|geoHash1
decl_stmt|,
name|geoHash2
decl_stmt|;
DECL|field|radius
specifier|private
name|double
name|radius
decl_stmt|;
DECL|field|ctx
specifier|private
specifier|final
name|SpatialContext
name|ctx
decl_stmt|;
DECL|method|GeohashHaversineFunction
specifier|public
name|GeohashHaversineFunction
parameter_list|(
name|ValueSource
name|geoHash1
parameter_list|,
name|ValueSource
name|geoHash2
parameter_list|,
name|double
name|radius
parameter_list|)
block|{
name|this
operator|.
name|geoHash1
operator|=
name|geoHash1
expr_stmt|;
name|this
operator|.
name|geoHash2
operator|=
name|geoHash2
expr_stmt|;
name|this
operator|.
name|radius
operator|=
name|radius
expr_stmt|;
name|DistanceCalculator
name|distCalc
init|=
operator|new
name|GeodesicSphereDistCalc
operator|.
name|Haversine
argument_list|(
name|radius
argument_list|)
decl_stmt|;
name|this
operator|.
name|ctx
operator|=
operator|new
name|SimpleSpatialContext
argument_list|(
name|DistanceUnits
operator|.
name|KILOMETERS
argument_list|,
name|distCalc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|name
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
literal|"ghhsin"
return|;
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
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FunctionValues
name|gh1DV
init|=
name|geoHash1
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
specifier|final
name|FunctionValues
name|gh2DV
init|=
name|geoHash2
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
name|DoubleDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
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
name|gh1DV
argument_list|,
name|gh2DV
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
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|gh1DV
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
operator|.
name|append
argument_list|(
name|gh2DV
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
DECL|method|distance
specifier|protected
name|double
name|distance
parameter_list|(
name|int
name|doc
parameter_list|,
name|FunctionValues
name|gh1DV
parameter_list|,
name|FunctionValues
name|gh2DV
parameter_list|)
block|{
name|double
name|result
init|=
literal|0
decl_stmt|;
name|String
name|h1
init|=
name|gh1DV
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|String
name|h2
init|=
name|gh2DV
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|h1
operator|!=
literal|null
operator|&&
name|h2
operator|!=
literal|null
operator|&&
name|h1
operator|.
name|equals
argument_list|(
name|h2
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|//TODO: If one of the hashes is a literal value source, seems like we could cache it
comment|//and avoid decoding every time
name|Point
name|p1
init|=
name|GeohashUtils
operator|.
name|decode
argument_list|(
name|h1
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|Point
name|p2
init|=
name|GeohashUtils
operator|.
name|decode
argument_list|(
name|h2
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|result
operator|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|h1
operator|==
literal|null
operator|||
name|h2
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|Double
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
return|return
name|result
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
name|geoHash1
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|geoHash2
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
operator|.
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
name|GeohashHaversineFunction
name|other
init|=
operator|(
name|GeohashHaversineFunction
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
name|geoHash1
operator|.
name|equals
argument_list|(
name|other
operator|.
name|geoHash1
argument_list|)
operator|&&
name|geoHash2
operator|.
name|equals
argument_list|(
name|other
operator|.
name|geoHash2
argument_list|)
operator|&&
name|radius
operator|==
name|other
operator|.
name|radius
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
decl_stmt|;
name|result
operator|=
name|geoHash1
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
name|geoHash2
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
name|name
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|long
name|temp
init|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|radius
argument_list|)
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
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
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|geoHash1
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|geoHash2
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
