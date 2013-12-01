begin_unit
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|FacetsCollector
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
comment|/** Aggregates sum of values from {@link  *  FunctionValues#doubleVal}, for each facet label.  *  *  @lucene.experimental */
end_comment
begin_class
DECL|class|TaxonomyFacetSumValueSource
specifier|public
class|class
name|TaxonomyFacetSumValueSource
extends|extends
name|FloatTaxonomyFacets
block|{
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
name|FacetsCollector
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
name|FacetsCollector
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
if|if
condition|(
name|keepScores
condition|)
block|{
name|context
operator|.
name|put
argument_list|(
literal|"scorer"
argument_list|,
name|scorer
argument_list|)
expr_stmt|;
block|}
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
name|rollup
argument_list|()
expr_stmt|;
block|}
comment|/** {@link ValueSource} that returns the score for each    *  hit; use this to aggregate the sum of all hit scores    *  for each facet label.  */
DECL|class|ScoreValueSource
specifier|public
specifier|static
class|class
name|ScoreValueSource
extends|extends
name|ValueSource
block|{
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
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
name|Scorer
name|scorer
init|=
operator|(
name|Scorer
operator|)
name|context
operator|.
name|get
argument_list|(
literal|"scorer"
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"scores are missing; be sure to pass keepScores=true to FacetsCollector"
argument_list|)
throw|;
block|}
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
name|document
parameter_list|)
block|{
try|try
block|{
return|return
name|scorer
operator|.
name|score
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|exception
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|exception
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
DECL|method|equals
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|==
name|this
return|;
block|}
DECL|method|hashCode
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|description
annotation|@
name|Override
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"score()"
return|;
block|}
block|}
empty_stmt|;
block|}
end_class
end_unit
