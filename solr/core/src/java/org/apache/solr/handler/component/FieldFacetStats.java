begin_unit
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|search
operator|.
name|FieldCache
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
begin_comment
comment|/**  * FieldFacetStats is a utility to accumulate statistics on a set of values in one field,  * for facet values present in another field.  *<p>  * 9/10/2009 - Moved out of StatsComponent to allow open access to UnInvertedField  *<p/>  * @see org.apache.solr.handler.component.StatsComponent  *  */
end_comment
begin_class
DECL|class|FieldFacetStats
specifier|public
class|class
name|FieldFacetStats
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|si
specifier|final
name|FieldCache
operator|.
name|DocTermsIndex
name|si
decl_stmt|;
DECL|field|facet_sf
specifier|final
name|SchemaField
name|facet_sf
decl_stmt|;
DECL|field|field_sf
specifier|final
name|SchemaField
name|field_sf
decl_stmt|;
DECL|field|startTermIndex
specifier|final
name|int
name|startTermIndex
decl_stmt|;
DECL|field|endTermIndex
specifier|final
name|int
name|endTermIndex
decl_stmt|;
DECL|field|nTerms
specifier|final
name|int
name|nTerms
decl_stmt|;
DECL|field|numStatsTerms
specifier|final
name|int
name|numStatsTerms
decl_stmt|;
DECL|field|facetStatsValues
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
name|facetStatsValues
decl_stmt|;
DECL|field|facetStatsTerms
specifier|final
name|List
argument_list|<
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|facetStatsTerms
decl_stmt|;
DECL|field|tempBR
specifier|private
specifier|final
name|BytesRef
name|tempBR
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|FieldFacetStats
specifier|public
name|FieldFacetStats
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldCache
operator|.
name|DocTermsIndex
name|si
parameter_list|,
name|SchemaField
name|field_sf
parameter_list|,
name|SchemaField
name|facet_sf
parameter_list|,
name|int
name|numStatsTerms
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|si
operator|=
name|si
expr_stmt|;
name|this
operator|.
name|field_sf
operator|=
name|field_sf
expr_stmt|;
name|this
operator|.
name|facet_sf
operator|=
name|facet_sf
expr_stmt|;
name|this
operator|.
name|numStatsTerms
operator|=
name|numStatsTerms
expr_stmt|;
name|startTermIndex
operator|=
literal|1
expr_stmt|;
name|endTermIndex
operator|=
name|si
operator|.
name|numOrd
argument_list|()
expr_stmt|;
name|nTerms
operator|=
name|endTermIndex
operator|-
name|startTermIndex
expr_stmt|;
name|facetStatsValues
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
argument_list|()
expr_stmt|;
comment|// for mv stats field, we'll want to keep track of terms
name|facetStatsTerms
operator|=
operator|new
name|ArrayList
argument_list|<
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|numStatsTerms
operator|==
literal|0
condition|)
return|return;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|numStatsTerms
condition|;
name|i
operator|++
control|)
block|{
name|facetStatsTerms
operator|.
name|add
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getTermText
name|BytesRef
name|getTermText
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ret
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|si
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|si
operator|.
name|lookup
argument_list|(
name|ord
argument_list|,
name|ret
argument_list|)
return|;
block|}
block|}
DECL|method|facet
specifier|public
name|boolean
name|facet
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|v
parameter_list|)
block|{
name|int
name|term
init|=
name|si
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|int
name|arrIdx
init|=
name|term
operator|-
name|startTermIndex
decl_stmt|;
if|if
condition|(
name|arrIdx
operator|>=
literal|0
operator|&&
name|arrIdx
operator|<
name|nTerms
condition|)
block|{
specifier|final
name|BytesRef
name|br
init|=
name|si
operator|.
name|lookup
argument_list|(
name|term
argument_list|,
name|tempBR
argument_list|)
decl_stmt|;
name|String
name|key
init|=
operator|(
name|br
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|facet_sf
operator|.
name|getType
argument_list|()
operator|.
name|indexedToReadable
argument_list|(
name|br
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
name|StatsValues
name|stats
init|=
name|facetStatsValues
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|stats
operator|=
name|StatsValuesFactory
operator|.
name|createStatsValues
argument_list|(
name|field_sf
argument_list|)
expr_stmt|;
name|facetStatsValues
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|v
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|stats
operator|.
name|accumulate
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stats
operator|.
name|missing
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|// Function to keep track of facet counts for term number.
comment|// Currently only used by UnInvertedField stats
DECL|method|facetTermNum
specifier|public
name|boolean
name|facetTermNum
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|statsTermNum
parameter_list|)
block|{
name|int
name|term
init|=
name|si
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|int
name|arrIdx
init|=
name|term
operator|-
name|startTermIndex
decl_stmt|;
if|if
condition|(
name|arrIdx
operator|>=
literal|0
operator|&&
name|arrIdx
operator|<
name|nTerms
condition|)
block|{
specifier|final
name|BytesRef
name|br
init|=
name|si
operator|.
name|lookup
argument_list|(
name|term
argument_list|,
name|tempBR
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|br
operator|==
literal|null
condition|?
literal|null
else|:
name|br
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|statsTermCounts
init|=
name|facetStatsTerms
operator|.
name|get
argument_list|(
name|statsTermNum
argument_list|)
decl_stmt|;
name|Integer
name|statsTermCount
init|=
name|statsTermCounts
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|statsTermCount
operator|==
literal|null
condition|)
block|{
name|statsTermCounts
operator|.
name|put
argument_list|(
name|key
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statsTermCounts
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|statsTermCount
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|//function to accumulate counts for statsTermNum to specified value
DECL|method|accumulateTermNum
specifier|public
name|boolean
name|accumulateTermNum
parameter_list|(
name|int
name|statsTermNum
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
literal|false
return|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|stringIntegerEntry
range|:
name|facetStatsTerms
operator|.
name|get
argument_list|(
name|statsTermNum
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
operator|.
name|Entry
name|pairs
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|stringIntegerEntry
decl_stmt|;
name|String
name|key
init|=
operator|(
name|String
operator|)
name|pairs
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|StatsValues
name|facetStats
init|=
name|facetStatsValues
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetStats
operator|==
literal|null
condition|)
block|{
name|facetStats
operator|=
name|StatsValuesFactory
operator|.
name|createStatsValues
argument_list|(
name|field_sf
argument_list|)
expr_stmt|;
name|facetStatsValues
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|facetStats
argument_list|)
expr_stmt|;
block|}
name|Integer
name|count
init|=
operator|(
name|Integer
operator|)
name|pairs
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|facetStats
operator|.
name|accumulate
argument_list|(
name|value
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
