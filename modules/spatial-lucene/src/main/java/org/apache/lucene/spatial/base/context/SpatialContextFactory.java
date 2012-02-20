begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.base.context
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
name|context
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
name|simple
operator|.
name|SimpleSpatialContext
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
name|context
operator|.
name|simple
operator|.
name|SimpleSpatialContextFactory
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
name|distance
operator|.
name|*
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
name|Rectangle
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
comment|/**  * Factory for a SpatialContext.  * is   * @author dsmiley  */
end_comment
begin_class
DECL|class|SpatialContextFactory
specifier|public
specifier|abstract
class|class
name|SpatialContextFactory
block|{
DECL|field|args
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
decl_stmt|;
DECL|field|classLoader
specifier|protected
name|ClassLoader
name|classLoader
decl_stmt|;
DECL|field|units
specifier|protected
name|DistanceUnits
name|units
decl_stmt|;
DECL|field|calculator
specifier|protected
name|DistanceCalculator
name|calculator
decl_stmt|;
DECL|field|worldBounds
specifier|protected
name|Rectangle
name|worldBounds
decl_stmt|;
comment|/**    * The factory class is lookuped up via "spatialContextFactory" in args    * then falling back to a Java system property (with initial caps). If neither are specified    * then {@link SimpleSpatialContextFactory} is chosen.    * @param args    * @param classLoader    * @return    */
DECL|method|makeSpatialContext
specifier|public
specifier|static
name|SpatialContext
name|makeSpatialContext
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|)
block|{
name|SpatialContextFactory
name|instance
decl_stmt|;
name|String
name|cname
init|=
name|args
operator|.
name|get
argument_list|(
literal|"spatialContextFactory"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cname
operator|==
literal|null
condition|)
name|cname
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"SpatialContextFactory"
argument_list|)
expr_stmt|;
if|if
condition|(
name|cname
operator|==
literal|null
condition|)
name|instance
operator|=
operator|new
name|SimpleSpatialContextFactory
argument_list|()
expr_stmt|;
else|else
block|{
try|try
block|{
name|Class
name|c
init|=
name|classLoader
operator|.
name|loadClass
argument_list|(
name|cname
argument_list|)
decl_stmt|;
name|instance
operator|=
operator|(
name|SpatialContextFactory
operator|)
name|c
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|instance
operator|.
name|init
argument_list|(
name|args
argument_list|,
name|classLoader
argument_list|)
expr_stmt|;
return|return
name|instance
operator|.
name|newSpatialContext
argument_list|()
return|;
block|}
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
name|this
operator|.
name|classLoader
operator|=
name|classLoader
expr_stmt|;
name|initUnits
argument_list|()
expr_stmt|;
name|initCalculator
argument_list|()
expr_stmt|;
name|initWorldBounds
argument_list|()
expr_stmt|;
block|}
DECL|method|initUnits
specifier|protected
name|void
name|initUnits
parameter_list|()
block|{
name|String
name|unitsStr
init|=
name|args
operator|.
name|get
argument_list|(
literal|"units"
argument_list|)
decl_stmt|;
if|if
condition|(
name|unitsStr
operator|!=
literal|null
condition|)
name|units
operator|=
name|DistanceUnits
operator|.
name|findDistanceUnit
argument_list|(
name|unitsStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|units
operator|==
literal|null
condition|)
name|units
operator|=
name|DistanceUnits
operator|.
name|KILOMETERS
expr_stmt|;
block|}
DECL|method|initCalculator
specifier|protected
name|void
name|initCalculator
parameter_list|()
block|{
name|String
name|calcStr
init|=
name|args
operator|.
name|get
argument_list|(
literal|"distCalculator"
argument_list|)
decl_stmt|;
if|if
condition|(
name|calcStr
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|calcStr
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"haversine"
argument_list|)
condition|)
block|{
name|calculator
operator|=
operator|new
name|GeodesicSphereDistCalc
operator|.
name|Haversine
argument_list|(
name|units
operator|.
name|earthRadius
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|calcStr
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"lawOfCosines"
argument_list|)
condition|)
block|{
name|calculator
operator|=
operator|new
name|GeodesicSphereDistCalc
operator|.
name|LawOfCosines
argument_list|(
name|units
operator|.
name|earthRadius
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|calcStr
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"vincentySphere"
argument_list|)
condition|)
block|{
name|calculator
operator|=
operator|new
name|GeodesicSphereDistCalc
operator|.
name|Vincenty
argument_list|(
name|units
operator|.
name|earthRadius
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|calcStr
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"cartesian"
argument_list|)
condition|)
block|{
name|calculator
operator|=
operator|new
name|CartesianDistCalc
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|calcStr
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"cartesian^2"
argument_list|)
condition|)
block|{
name|calculator
operator|=
operator|new
name|CartesianDistCalc
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown calculator: "
operator|+
name|calcStr
argument_list|)
throw|;
block|}
block|}
DECL|method|initWorldBounds
specifier|protected
name|void
name|initWorldBounds
parameter_list|()
block|{
name|String
name|worldBoundsStr
init|=
name|args
operator|.
name|get
argument_list|(
literal|"worldBounds"
argument_list|)
decl_stmt|;
if|if
condition|(
name|worldBoundsStr
operator|==
literal|null
condition|)
return|return;
comment|//kinda ugly we do this just to read a rectangle.  TODO refactor
name|SimpleSpatialContext
name|simpleCtx
init|=
operator|new
name|SimpleSpatialContext
argument_list|(
name|units
argument_list|,
name|calculator
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|worldBounds
operator|=
operator|(
name|Rectangle
operator|)
name|simpleCtx
operator|.
name|readShape
argument_list|(
name|worldBoundsStr
argument_list|)
expr_stmt|;
block|}
comment|/** Subclasses should simply construct the instance from the initialized configuration. */
DECL|method|newSpatialContext
specifier|protected
specifier|abstract
name|SpatialContext
name|newSpatialContext
parameter_list|()
function_decl|;
block|}
end_class
end_unit
