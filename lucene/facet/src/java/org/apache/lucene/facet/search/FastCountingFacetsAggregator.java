begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|encoding
operator|.
name|DGapVInt8IntDecoder
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
name|encoding
operator|.
name|DGapVInt8IntEncoder
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
name|params
operator|.
name|CategoryListParams
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
name|params
operator|.
name|FacetSearchParams
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
name|search
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
name|BinaryDocValues
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link FacetsAggregator} which counts the number of times each category  * appears in the given set of documents. This aggregator reads the categories  * from the {@link BinaryDocValues} field defined by  * {@link CategoryListParams#field}, and assumes that the category ordinals were  * encoded with {@link DGapVInt8IntEncoder}.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FastCountingFacetsAggregator
specifier|public
specifier|final
class|class
name|FastCountingFacetsAggregator
implements|implements
name|FacetsAggregator
block|{
DECL|field|buf
specifier|private
specifier|final
name|BytesRef
name|buf
init|=
operator|new
name|BytesRef
argument_list|(
literal|32
argument_list|)
decl_stmt|;
comment|/**    * Asserts that this {@link FacetsCollector} can handle the given    * {@link FacetSearchParams}. Returns {@code null} if true, otherwise an error    * message.    */
DECL|method|verifySearchParams
specifier|final
specifier|static
name|boolean
name|verifySearchParams
parameter_list|(
name|FacetSearchParams
name|fsp
parameter_list|)
block|{
comment|// verify that all category lists were encoded with DGapVInt
for|for
control|(
name|FacetRequest
name|fr
range|:
name|fsp
operator|.
name|facetRequests
control|)
block|{
name|CategoryListParams
name|clp
init|=
name|fsp
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|clp
operator|.
name|createEncoder
argument_list|()
operator|.
name|createMatchingDecoder
argument_list|()
operator|.
name|getClass
argument_list|()
operator|!=
name|DGapVInt8IntDecoder
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|aggregate
specifier|public
specifier|final
name|void
name|aggregate
parameter_list|(
name|MatchingDocs
name|matchingDocs
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|clp
operator|.
name|createEncoder
argument_list|()
operator|.
name|createMatchingDecoder
argument_list|()
operator|.
name|getClass
argument_list|()
operator|==
name|DGapVInt8IntDecoder
operator|.
name|class
operator|:
literal|"this aggregator assumes ordinals were encoded as dgap+vint"
assert|;
specifier|final
name|BinaryDocValues
name|dv
init|=
name|matchingDocs
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|clp
operator|.
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dv
operator|==
literal|null
condition|)
block|{
comment|// this reader does not have DocValues for the requested category list
return|return;
block|}
specifier|final
name|int
name|length
init|=
name|matchingDocs
operator|.
name|bits
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|counts
init|=
name|facetArrays
operator|.
name|getIntArray
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
literal|0
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
name|matchingDocs
operator|.
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
name|dv
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|buf
argument_list|)
expr_stmt|;
if|if
condition|(
name|buf
operator|.
name|length
operator|>
literal|0
condition|)
block|{
comment|// this document has facets
specifier|final
name|int
name|upto
init|=
name|buf
operator|.
name|offset
operator|+
name|buf
operator|.
name|length
decl_stmt|;
name|int
name|ord
init|=
literal|0
decl_stmt|;
name|int
name|offset
init|=
name|buf
operator|.
name|offset
decl_stmt|;
name|int
name|prev
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|offset
operator|<
name|upto
condition|)
block|{
name|byte
name|b
init|=
name|buf
operator|.
name|bytes
index|[
name|offset
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
block|{
name|prev
operator|=
name|ord
operator|=
operator|(
operator|(
name|ord
operator|<<
literal|7
operator|)
operator||
name|b
operator|)
operator|+
name|prev
expr_stmt|;
operator|++
name|counts
index|[
name|ord
index|]
expr_stmt|;
name|ord
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|ord
operator|=
operator|(
name|ord
operator|<<
literal|7
operator|)
operator||
operator|(
name|b
operator|&
literal|0x7F
operator|)
expr_stmt|;
block|}
block|}
block|}
operator|++
name|doc
expr_stmt|;
block|}
block|}
DECL|method|rollupCounts
specifier|private
name|int
name|rollupCounts
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|children
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|int
index|[]
name|counts
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ordinal
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|int
name|childCount
init|=
name|counts
index|[
name|ordinal
index|]
decl_stmt|;
name|childCount
operator|+=
name|rollupCounts
argument_list|(
name|children
index|[
name|ordinal
index|]
argument_list|,
name|children
argument_list|,
name|siblings
argument_list|,
name|counts
argument_list|)
expr_stmt|;
name|counts
index|[
name|ordinal
index|]
operator|=
name|childCount
expr_stmt|;
name|count
operator|+=
name|childCount
expr_stmt|;
name|ordinal
operator|=
name|siblings
index|[
name|ordinal
index|]
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|rollupValues
specifier|public
specifier|final
name|void
name|rollupValues
parameter_list|(
name|FacetRequest
name|fr
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|children
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|counts
init|=
name|facetArrays
operator|.
name|getIntArray
argument_list|()
decl_stmt|;
name|counts
index|[
name|ordinal
index|]
operator|+=
name|rollupCounts
argument_list|(
name|children
index|[
name|ordinal
index|]
argument_list|,
name|children
argument_list|,
name|siblings
argument_list|,
name|counts
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|requiresDocScores
specifier|public
specifier|final
name|boolean
name|requiresDocScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
