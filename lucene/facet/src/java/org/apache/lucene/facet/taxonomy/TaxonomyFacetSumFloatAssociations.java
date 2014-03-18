begin_unit
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
name|List
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
name|search
operator|.
name|DocIdSetIterator
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
comment|/** Aggregates sum of int values previously indexed with  *  {@link FloatAssociationFacetField}, assuming the default  *  encoding.  *  *  @lucene.experimental */
end_comment
begin_class
DECL|class|TaxonomyFacetSumFloatAssociations
specifier|public
class|class
name|TaxonomyFacetSumFloatAssociations
extends|extends
name|FloatTaxonomyFacets
block|{
comment|/** Create {@code TaxonomyFacetSumFloatAssociations} against    *  the default index field. */
DECL|method|TaxonomyFacetSumFloatAssociations
specifier|public
name|TaxonomyFacetSumFloatAssociations
parameter_list|(
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|FacetsCollector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FacetsConfig
operator|.
name|DEFAULT_INDEX_FIELD_NAME
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
expr_stmt|;
block|}
comment|/** Create {@code TaxonomyFacetSumFloatAssociations} against    *  the specified index field. */
DECL|method|TaxonomyFacetSumFloatAssociations
specifier|public
name|TaxonomyFacetSumFloatAssociations
parameter_list|(
name|String
name|indexFieldName
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|FacetsCollector
name|fc
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
name|sumValues
argument_list|(
name|fc
operator|.
name|getMatchingDocs
argument_list|()
argument_list|)
expr_stmt|;
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
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("count matchingDocs=" + matchingDocs + " facetsField=" + facetsFieldName);
for|for
control|(
name|MatchingDocs
name|hits
range|:
name|matchingDocs
control|)
block|{
name|BinaryDocValues
name|dv
init|=
name|hits
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|indexFieldName
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
continue|continue;
block|}
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|DocIdSetIterator
name|docs
init|=
name|hits
operator|.
name|bits
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|docs
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
comment|//System.out.println("  doc=" + doc);
comment|// TODO: use OrdinalsReader?  we'd need to add a
comment|// BytesRef getAssociation()?
name|dv
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|scratch
operator|.
name|bytes
decl_stmt|;
name|int
name|end
init|=
name|scratch
operator|.
name|offset
operator|+
name|scratch
operator|.
name|length
decl_stmt|;
name|int
name|offset
init|=
name|scratch
operator|.
name|offset
decl_stmt|;
while|while
condition|(
name|offset
operator|<
name|end
condition|)
block|{
name|int
name|ord
init|=
operator|(
operator|(
name|bytes
index|[
name|offset
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
index|[
name|offset
operator|+
literal|3
index|]
operator|&
literal|0xFF
operator|)
decl_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
name|int
name|value
init|=
operator|(
operator|(
name|bytes
index|[
name|offset
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
index|[
name|offset
operator|+
literal|3
index|]
operator|&
literal|0xFF
operator|)
decl_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
name|values
index|[
name|ord
index|]
operator|+=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
