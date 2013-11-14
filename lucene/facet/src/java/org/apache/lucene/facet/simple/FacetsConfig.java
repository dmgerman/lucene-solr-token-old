begin_unit
begin_package
DECL|package|org.apache.lucene.facet.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|simple
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import
begin_comment
comment|/** By default a dimension is flat and single valued; use  *  the setters in this class to change that for any dims */
end_comment
begin_class
DECL|class|FacetsConfig
specifier|public
class|class
name|FacetsConfig
block|{
DECL|field|DEFAULT_INDEXED_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_INDEXED_FIELD_NAME
init|=
literal|"$facets"
decl_stmt|;
comment|// nocommit pull the delim char into there?
comment|// nocommit pull DimType into here (shai?)
comment|// nocommit pull facet field ($facets) into here, instead
comment|// of optionally setting it on FacetField
DECL|field|fieldTypes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DimConfig
argument_list|>
name|fieldTypes
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|DimConfig
argument_list|>
argument_list|()
decl_stmt|;
comment|/** @lucene.internal */
comment|// nocommit expose this to the user, vs the setters?
DECL|class|DimConfig
specifier|public
specifier|static
specifier|final
class|class
name|DimConfig
block|{
DECL|field|hierarchical
name|boolean
name|hierarchical
decl_stmt|;
DECL|field|multiValued
name|boolean
name|multiValued
decl_stmt|;
comment|/** Actual field where this dimension's facet labels      *  should be indexed */
DECL|field|indexedFieldName
name|String
name|indexedFieldName
init|=
name|DEFAULT_INDEXED_FIELD_NAME
decl_stmt|;
block|}
DECL|field|DEFAULT_DIM_CONFIG
specifier|public
specifier|final
specifier|static
name|DimConfig
name|DEFAULT_DIM_CONFIG
init|=
operator|new
name|DimConfig
argument_list|()
decl_stmt|;
DECL|method|getDimConfig
specifier|public
name|DimConfig
name|getDimConfig
parameter_list|(
name|String
name|dimName
parameter_list|)
block|{
name|DimConfig
name|ft
init|=
name|fieldTypes
operator|.
name|get
argument_list|(
name|dimName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
name|ft
operator|=
name|DEFAULT_DIM_CONFIG
expr_stmt|;
block|}
return|return
name|ft
return|;
block|}
comment|// nocommit maybe setDimConfig instead?
DECL|method|setHierarchical
specifier|public
specifier|synchronized
name|void
name|setHierarchical
parameter_list|(
name|String
name|dimName
parameter_list|)
block|{
name|DimConfig
name|ft
init|=
name|fieldTypes
operator|.
name|get
argument_list|(
name|dimName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
name|ft
operator|=
operator|new
name|DimConfig
argument_list|()
expr_stmt|;
name|fieldTypes
operator|.
name|put
argument_list|(
name|dimName
argument_list|,
name|ft
argument_list|)
expr_stmt|;
block|}
name|ft
operator|.
name|hierarchical
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|setMultiValued
specifier|public
specifier|synchronized
name|void
name|setMultiValued
parameter_list|(
name|String
name|dimName
parameter_list|)
block|{
name|DimConfig
name|ft
init|=
name|fieldTypes
operator|.
name|get
argument_list|(
name|dimName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
name|ft
operator|=
operator|new
name|DimConfig
argument_list|()
expr_stmt|;
name|fieldTypes
operator|.
name|put
argument_list|(
name|dimName
argument_list|,
name|ft
argument_list|)
expr_stmt|;
block|}
name|ft
operator|.
name|multiValued
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|setIndexedFieldName
specifier|public
specifier|synchronized
name|void
name|setIndexedFieldName
parameter_list|(
name|String
name|dimName
parameter_list|,
name|String
name|indexedFieldName
parameter_list|)
block|{
name|DimConfig
name|ft
init|=
name|fieldTypes
operator|.
name|get
argument_list|(
name|dimName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
name|ft
operator|=
operator|new
name|DimConfig
argument_list|()
expr_stmt|;
name|fieldTypes
operator|.
name|put
argument_list|(
name|dimName
argument_list|,
name|ft
argument_list|)
expr_stmt|;
block|}
name|ft
operator|.
name|indexedFieldName
operator|=
name|indexedFieldName
expr_stmt|;
block|}
DECL|method|getDimConfigs
name|Map
argument_list|<
name|String
argument_list|,
name|DimConfig
argument_list|>
name|getDimConfigs
parameter_list|()
block|{
return|return
name|fieldTypes
return|;
block|}
block|}
end_class
end_unit
