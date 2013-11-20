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
name|HashMap
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
name|simple
operator|.
name|SimpleFacetsCollector
operator|.
name|MatchingDocs
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
name|taxonomy
operator|.
name|FacetLabel
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
name|taxonomy
operator|.
name|TaxonomyReader
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
name|search
operator|.
name|Scorer
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
name|FixedBitSet
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
name|IntsRef
import|;
end_import
begin_comment
comment|/** Aggregates sum of values from a {@link ValueSource}, for  *  each facet label. */
end_comment
begin_class
DECL|class|TaxonomyFacetSumValueSource
specifier|public
class|class
name|TaxonomyFacetSumValueSource
extends|extends
name|TaxonomyFacets
block|{
DECL|field|values
specifier|private
specifier|final
name|float
index|[]
name|values
decl_stmt|;
DECL|field|ordinalsReader
specifier|private
specifier|final
name|OrdinalsReader
name|ordinalsReader
decl_stmt|;
comment|/** Aggreggates float facet values from the provided    *  {@link ValueSource}, pulling ordinals using {@link    *  DocValuesOrdinalsReader} against the default indexed    *  facet field {@link    *  FacetsConfig#DEFAULT_INDEX_FIELD_NAME}. */
DECL|method|TaxonomyFacetSumValueSource
specifier|public
name|TaxonomyFacetSumValueSource
parameter_list|(
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|SimpleFacetsCollector
name|fc
parameter_list|,
name|ValueSource
name|valueSource
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|DocValuesOrdinalsReader
argument_list|(
name|FacetsConfig
operator|.
name|DEFAULT_INDEX_FIELD_NAME
argument_list|)
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|,
name|valueSource
argument_list|)
expr_stmt|;
block|}
comment|/** Aggreggates float facet values from the provided    *  {@link ValueSource}, and pulls ordinals from the    *  provided {@link OrdinalsReader}. */
DECL|method|TaxonomyFacetSumValueSource
specifier|public
name|TaxonomyFacetSumValueSource
parameter_list|(
name|OrdinalsReader
name|ordinalsReader
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|SimpleFacetsCollector
name|fc
parameter_list|,
name|ValueSource
name|valueSource
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|ordinalsReader
operator|.
name|getIndexFieldName
argument_list|()
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|ordinalsReader
operator|=
name|ordinalsReader
expr_stmt|;
name|values
operator|=
operator|new
name|float
index|[
name|taxoReader
operator|.
name|getSize
argument_list|()
index|]
expr_stmt|;
name|sumValues
argument_list|(
name|fc
operator|.
name|getMatchingDocs
argument_list|()
argument_list|,
name|fc
operator|.
name|getKeepScores
argument_list|()
argument_list|,
name|valueSource
argument_list|)
expr_stmt|;
block|}
DECL|class|FakeScorer
specifier|private
specifier|static
specifier|final
class|class
name|FakeScorer
extends|extends
name|Scorer
block|{
DECL|field|score
name|float
name|score
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|method|FakeScorer
name|FakeScorer
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|score
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|score
return|;
block|}
DECL|method|freq
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|docID
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docID
return|;
block|}
DECL|method|nextDoc
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|advance
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|cost
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|method|sumValues
specifier|private
specifier|final
name|void
name|sumValues
parameter_list|(
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
parameter_list|,
name|boolean
name|keepScores
parameter_list|,
name|ValueSource
name|valueSource
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FakeScorer
name|scorer
init|=
operator|new
name|FakeScorer
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Scorer
argument_list|>
name|context
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Scorer
argument_list|>
argument_list|()
decl_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"scorer"
argument_list|,
name|scorer
argument_list|)
expr_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
for|for
control|(
name|MatchingDocs
name|hits
range|:
name|matchingDocs
control|)
block|{
name|OrdinalsReader
operator|.
name|OrdinalsSegmentReader
name|ords
init|=
name|ordinalsReader
operator|.
name|getReader
argument_list|(
name|hits
operator|.
name|context
argument_list|)
decl_stmt|;
name|FixedBitSet
name|bits
init|=
name|hits
operator|.
name|bits
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|hits
operator|.
name|bits
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
literal|0
decl_stmt|;
name|int
name|scoresIdx
init|=
literal|0
decl_stmt|;
name|float
index|[]
name|scores
init|=
name|hits
operator|.
name|scores
decl_stmt|;
name|FunctionValues
name|functionValues
init|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|hits
operator|.
name|context
argument_list|)
decl_stmt|;
while|while
condition|(
name|doc
operator|<
name|length
operator|&&
operator|(
name|doc
operator|=
name|bits
operator|.
name|nextSetBit
argument_list|(
name|doc
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|ords
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
if|if
condition|(
name|keepScores
condition|)
block|{
name|scorer
operator|.
name|docID
operator|=
name|doc
expr_stmt|;
name|scorer
operator|.
name|score
operator|=
name|scores
index|[
name|scoresIdx
operator|++
index|]
expr_stmt|;
block|}
name|float
name|value
init|=
operator|(
name|float
operator|)
name|functionValues
operator|.
name|doubleVal
argument_list|(
name|doc
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
name|scratch
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|scratch
operator|.
name|ints
index|[
name|i
index|]
index|]
operator|+=
name|value
expr_stmt|;
block|}
operator|++
name|doc
expr_stmt|;
block|}
block|}
comment|// nocommit we could do this lazily instead:
comment|// Rollup any necessary dims:
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FacetsConfig
operator|.
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
name|FacetsConfig
operator|.
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
assert|assert
name|dimRootOrd
operator|>
literal|0
assert|;
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
DECL|method|rollup
specifier|private
name|float
name|rollup
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
name|float
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
name|float
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
name|verifyDim
argument_list|(
name|dim
argument_list|)
expr_stmt|;
name|int
name|ord
init|=
name|taxoReader
operator|.
name|getOrdinal
argument_list|(
name|FacetLabel
operator|.
name|create
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
name|SimpleFacetResult
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
name|FacetsConfig
operator|.
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
name|FacetLabel
operator|.
name|create
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
name|TopOrdAndFloatQueue
name|q
init|=
operator|new
name|TopOrdAndFloatQueue
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
name|float
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
name|float
name|sumValues
init|=
literal|0
decl_stmt|;
name|TopOrdAndFloatQueue
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
name|sumValues
operator|+=
name|values
index|[
name|ord
index|]
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
name|TopOrdAndFloatQueue
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
name|sumValues
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
name|sumValues
operator|=
name|values
index|[
name|dimOrd
index|]
expr_stmt|;
block|}
else|else
block|{
comment|// Our sum'd count is not correct, in general:
name|sumValues
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Our sum'd dim count is accurate, so we keep it
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
name|TopOrdAndFloatQueue
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
name|SimpleFacetResult
argument_list|(
name|cp
argument_list|,
name|sumValues
argument_list|,
name|labelValues
argument_list|)
return|;
block|}
block|}
end_class
end_unit
