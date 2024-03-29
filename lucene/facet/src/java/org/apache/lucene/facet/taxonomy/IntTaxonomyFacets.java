begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
package|;
end_package
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|FacetResult
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
name|facet
operator|.
name|FacetsConfig
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
name|facet
operator|.
name|FacetsConfig
operator|.
name|DimConfig
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
name|facet
operator|.
name|LabelAndValue
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
name|facet
operator|.
name|TopOrdAndIntQueue
import|;
end_import
begin_comment
comment|/** Base class for all taxonomy-based facets that aggregate  *  to a per-ords int[]. */
end_comment
begin_class
DECL|class|IntTaxonomyFacets
specifier|public
specifier|abstract
class|class
name|IntTaxonomyFacets
extends|extends
name|TaxonomyFacets
block|{
comment|/** Per-ordinal value. */
DECL|field|values
specifier|protected
specifier|final
name|int
index|[]
name|values
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|IntTaxonomyFacets
specifier|protected
name|IntTaxonomyFacets
parameter_list|(
name|String
name|indexFieldName
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|indexFieldName
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|int
index|[
name|taxoReader
operator|.
name|getSize
argument_list|()
index|]
expr_stmt|;
block|}
comment|/** Rolls up any single-valued hierarchical dimensions. */
DECL|method|rollup
specifier|protected
name|void
name|rollup
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Rollup any necessary dims:
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DimConfig
argument_list|>
name|ent
range|:
name|config
operator|.
name|getDimConfigs
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|dim
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|DimConfig
name|ft
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|ft
operator|.
name|hierarchical
operator|&&
name|ft
operator|.
name|multiValued
operator|==
literal|false
condition|)
block|{
name|int
name|dimRootOrd
init|=
name|taxoReader
operator|.
name|getOrdinal
argument_list|(
operator|new
name|FacetLabel
argument_list|(
name|dim
argument_list|)
argument_list|)
decl_stmt|;
comment|// It can be -1 if this field was declared in the
comment|// config but never indexed:
if|if
condition|(
name|dimRootOrd
operator|>
literal|0
condition|)
block|{
name|values
index|[
name|dimRootOrd
index|]
operator|+=
name|rollup
argument_list|(
name|children
index|[
name|dimRootOrd
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|rollup
specifier|private
name|int
name|rollup
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
name|int
name|sum
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ord
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|int
name|childValue
init|=
name|values
index|[
name|ord
index|]
operator|+
name|rollup
argument_list|(
name|children
index|[
name|ord
index|]
argument_list|)
decl_stmt|;
name|values
index|[
name|ord
index|]
operator|=
name|childValue
expr_stmt|;
name|sum
operator|+=
name|childValue
expr_stmt|;
name|ord
operator|=
name|siblings
index|[
name|ord
index|]
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
annotation|@
name|Override
DECL|method|getSpecificValue
specifier|public
name|Number
name|getSpecificValue
parameter_list|(
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|DimConfig
name|dimConfig
init|=
name|verifyDim
argument_list|(
name|dim
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|dimConfig
operator|.
name|hierarchical
operator|&&
name|dimConfig
operator|.
name|multiValued
operator|==
literal|false
condition|)
block|{
comment|// ok: rolled up at search time
block|}
elseif|else
if|if
condition|(
name|dimConfig
operator|.
name|requireDimCount
operator|&&
name|dimConfig
operator|.
name|multiValued
condition|)
block|{
comment|// ok: we indexed all ords at index time
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot return dimension-level value alone; use getTopChildren instead"
argument_list|)
throw|;
block|}
block|}
name|int
name|ord
init|=
name|taxoReader
operator|.
name|getOrdinal
argument_list|(
operator|new
name|FacetLabel
argument_list|(
name|dim
argument_list|,
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|values
index|[
name|ord
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getTopChildren
specifier|public
name|FacetResult
name|getTopChildren
parameter_list|(
name|int
name|topN
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|topN
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"topN must be> 0 (got: "
operator|+
name|topN
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|DimConfig
name|dimConfig
init|=
name|verifyDim
argument_list|(
name|dim
argument_list|)
decl_stmt|;
name|FacetLabel
name|cp
init|=
operator|new
name|FacetLabel
argument_list|(
name|dim
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|int
name|dimOrd
init|=
name|taxoReader
operator|.
name|getOrdinal
argument_list|(
name|cp
argument_list|)
decl_stmt|;
if|if
condition|(
name|dimOrd
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TopOrdAndIntQueue
name|q
init|=
operator|new
name|TopOrdAndIntQueue
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|taxoReader
operator|.
name|getSize
argument_list|()
argument_list|,
name|topN
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|bottomValue
init|=
literal|0
decl_stmt|;
name|int
name|ord
init|=
name|children
index|[
name|dimOrd
index|]
decl_stmt|;
name|int
name|totValue
init|=
literal|0
decl_stmt|;
name|int
name|childCount
init|=
literal|0
decl_stmt|;
name|TopOrdAndIntQueue
operator|.
name|OrdAndValue
name|reuse
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|ord
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
if|if
condition|(
name|values
index|[
name|ord
index|]
operator|>
literal|0
condition|)
block|{
name|totValue
operator|+=
name|values
index|[
name|ord
index|]
expr_stmt|;
name|childCount
operator|++
expr_stmt|;
if|if
condition|(
name|values
index|[
name|ord
index|]
operator|>
name|bottomValue
condition|)
block|{
if|if
condition|(
name|reuse
operator|==
literal|null
condition|)
block|{
name|reuse
operator|=
operator|new
name|TopOrdAndIntQueue
operator|.
name|OrdAndValue
argument_list|()
expr_stmt|;
block|}
name|reuse
operator|.
name|ord
operator|=
name|ord
expr_stmt|;
name|reuse
operator|.
name|value
operator|=
name|values
index|[
name|ord
index|]
expr_stmt|;
name|reuse
operator|=
name|q
operator|.
name|insertWithOverflow
argument_list|(
name|reuse
argument_list|)
expr_stmt|;
if|if
condition|(
name|q
operator|.
name|size
argument_list|()
operator|==
name|topN
condition|)
block|{
name|bottomValue
operator|=
name|q
operator|.
name|top
argument_list|()
operator|.
name|value
expr_stmt|;
block|}
block|}
block|}
name|ord
operator|=
name|siblings
index|[
name|ord
index|]
expr_stmt|;
block|}
if|if
condition|(
name|totValue
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|dimConfig
operator|.
name|multiValued
condition|)
block|{
if|if
condition|(
name|dimConfig
operator|.
name|requireDimCount
condition|)
block|{
name|totValue
operator|=
name|values
index|[
name|dimOrd
index|]
expr_stmt|;
block|}
else|else
block|{
comment|// Our sum'd value is not correct, in general:
name|totValue
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Our sum'd dim value is accurate, so we keep it
block|}
name|LabelAndValue
index|[]
name|labelValues
init|=
operator|new
name|LabelAndValue
index|[
name|q
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|labelValues
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|TopOrdAndIntQueue
operator|.
name|OrdAndValue
name|ordAndValue
init|=
name|q
operator|.
name|pop
argument_list|()
decl_stmt|;
name|FacetLabel
name|child
init|=
name|taxoReader
operator|.
name|getPath
argument_list|(
name|ordAndValue
operator|.
name|ord
argument_list|)
decl_stmt|;
name|labelValues
index|[
name|i
index|]
operator|=
operator|new
name|LabelAndValue
argument_list|(
name|child
operator|.
name|components
index|[
name|cp
operator|.
name|length
index|]
argument_list|,
name|ordAndValue
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FacetResult
argument_list|(
name|dim
argument_list|,
name|path
argument_list|,
name|totValue
argument_list|,
name|labelValues
argument_list|,
name|childCount
argument_list|)
return|;
block|}
block|}
end_class
end_unit
