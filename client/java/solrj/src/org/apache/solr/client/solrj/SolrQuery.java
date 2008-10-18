begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
package|;
end_package
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
name|CommonParams
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
name|FacetParams
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
name|HighlightParams
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
name|ModifiableSolrParams
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
name|StatsParams
import|;
end_import
begin_comment
comment|/**  * This is an augmented SolrParams with get/set/add fields for common fields used  * in the Standard and Dismax request handlers  *   * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrQuery
specifier|public
class|class
name|SolrQuery
extends|extends
name|ModifiableSolrParams
block|{
DECL|enum|ORDER
DECL|enum constant|desc
DECL|enum constant|asc
specifier|public
enum|enum
name|ORDER
block|{
name|desc
block|,
name|asc
block|;
DECL|method|reverse
specifier|public
name|ORDER
name|reverse
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|asc
operator|)
condition|?
name|desc
else|:
name|asc
return|;
block|}
block|}
DECL|method|SolrQuery
specifier|public
name|SolrQuery
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** Create a new SolrQuery    *     * @param q query string    */
DECL|method|SolrQuery
specifier|public
name|SolrQuery
parameter_list|(
name|String
name|q
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
comment|/** add a field for facet computation    *     * @param fields the field name from the IndexSchema    * @return this    */
DECL|method|addFacetField
specifier|public
name|SolrQuery
name|addFacetField
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
for|for
control|(
name|String
name|f
range|:
name|fields
control|)
block|{
name|this
operator|.
name|add
argument_list|(
name|FacetParams
operator|.
name|FACET_FIELD
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|set
argument_list|(
name|FacetParams
operator|.
name|FACET
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** get the facet fields    *     * @return string array of facet fields or null if not set/empty    */
DECL|method|getFacetFields
specifier|public
name|String
index|[]
name|getFacetFields
parameter_list|()
block|{
return|return
name|this
operator|.
name|getParams
argument_list|(
name|FacetParams
operator|.
name|FACET_FIELD
argument_list|)
return|;
block|}
comment|/** remove a facet field    *     */
DECL|method|removeFacetField
specifier|public
name|boolean
name|removeFacetField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|boolean
name|b
init|=
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_FIELD
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|get
argument_list|(
name|FacetParams
operator|.
name|FACET_FIELD
argument_list|)
operator|==
literal|null
operator|&&
name|this
operator|.
name|get
argument_list|(
name|FacetParams
operator|.
name|FACET_QUERY
argument_list|)
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|setFacet
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
comment|/** enable/disable faceting.      *     * @param b flag to indicate faceting should be enabled.  if b==false removes all other faceting parameters    * @return this    */
DECL|method|setFacet
specifier|public
name|SolrQuery
name|setFacet
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
condition|)
block|{
name|this
operator|.
name|set
argument_list|(
name|FacetParams
operator|.
name|FACET
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_MINCOUNT
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_FIELD
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_LIMIT
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_MISSING
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_OFFSET
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_PREFIX
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_QUERY
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_SORT
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_ZEROS
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_PREFIX
argument_list|)
expr_stmt|;
comment|// does not include the individual fields...
block|}
return|return
name|this
return|;
block|}
DECL|method|setFacetPrefix
specifier|public
name|SolrQuery
name|setFacetPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|FacetParams
operator|.
name|FACET_PREFIX
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFacetPrefix
specifier|public
name|SolrQuery
name|setFacetPrefix
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
literal|"f."
operator|+
name|field
operator|+
literal|"."
operator|+
name|FacetParams
operator|.
name|FACET_PREFIX
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** add a faceting query    *     * @param f facet query    */
DECL|method|addFacetQuery
specifier|public
name|SolrQuery
name|addFacetQuery
parameter_list|(
name|String
name|f
parameter_list|)
block|{
name|this
operator|.
name|add
argument_list|(
name|FacetParams
operator|.
name|FACET_QUERY
argument_list|,
name|f
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** get facet queries    *     * @return all facet queries or null if not set/empty    */
DECL|method|getFacetQuery
specifier|public
name|String
index|[]
name|getFacetQuery
parameter_list|()
block|{
return|return
name|this
operator|.
name|getParams
argument_list|(
name|FacetParams
operator|.
name|FACET_QUERY
argument_list|)
return|;
block|}
comment|/** remove a facet query    *     * @param q the facet query to remove    * @return true if the facet query was removed false otherwise    */
DECL|method|removeFacetQuery
specifier|public
name|boolean
name|removeFacetQuery
parameter_list|(
name|String
name|q
parameter_list|)
block|{
name|boolean
name|b
init|=
name|this
operator|.
name|remove
argument_list|(
name|FacetParams
operator|.
name|FACET_QUERY
argument_list|,
name|q
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|get
argument_list|(
name|FacetParams
operator|.
name|FACET_FIELD
argument_list|)
operator|==
literal|null
operator|&&
name|this
operator|.
name|get
argument_list|(
name|FacetParams
operator|.
name|FACET_QUERY
argument_list|)
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|setFacet
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
comment|/** se the facet limit    *     * @param lim number facet items to return    */
DECL|method|setFacetLimit
specifier|public
name|SolrQuery
name|setFacetLimit
parameter_list|(
name|int
name|lim
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|FacetParams
operator|.
name|FACET_LIMIT
argument_list|,
name|lim
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** get current facet limit    *     * @return facet limit or default of 25    */
DECL|method|getFacetLimit
specifier|public
name|int
name|getFacetLimit
parameter_list|()
block|{
return|return
name|this
operator|.
name|getInt
argument_list|(
name|FacetParams
operator|.
name|FACET_LIMIT
argument_list|,
literal|25
argument_list|)
return|;
block|}
comment|/** set facet minimum count    *     * @param cnt facets having less that cnt hits will be excluded from teh facet list    */
DECL|method|setFacetMinCount
specifier|public
name|SolrQuery
name|setFacetMinCount
parameter_list|(
name|int
name|cnt
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|FacetParams
operator|.
name|FACET_MINCOUNT
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** get facet minimum count    *     * @return facet minimum count or default of 1    */
DECL|method|getFacetMinCount
specifier|public
name|int
name|getFacetMinCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|getInt
argument_list|(
name|FacetParams
operator|.
name|FACET_MINCOUNT
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|setFacetMissing
specifier|public
name|SolrQuery
name|setFacetMissing
parameter_list|(
name|Boolean
name|v
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|FacetParams
operator|.
name|FACET_MISSING
argument_list|,
name|v
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * @deprecated use {@link #setFacetMissing(String)}    */
DECL|method|setMissing
specifier|public
name|SolrQuery
name|setMissing
parameter_list|(
name|String
name|fld
parameter_list|)
block|{
return|return
name|setFacetMissing
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|fld
argument_list|)
argument_list|)
return|;
block|}
comment|/** get facet sort    *     * @return facet sort or default of true    */
DECL|method|getFacetSort
specifier|public
name|boolean
name|getFacetSort
parameter_list|()
block|{
return|return
name|this
operator|.
name|getBool
argument_list|(
name|FacetParams
operator|.
name|FACET_SORT
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** set facet sort    *     * @param sort sort facets    * @return this    */
DECL|method|setFacetSort
specifier|public
name|SolrQuery
name|setFacetSort
parameter_list|(
name|Boolean
name|sort
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|FacetParams
operator|.
name|FACET_SORT
argument_list|,
name|sort
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** add highlight field    *     * @param f field to enable for highlighting    */
DECL|method|addHighlightField
specifier|public
name|SolrQuery
name|addHighlightField
parameter_list|(
name|String
name|f
parameter_list|)
block|{
name|this
operator|.
name|add
argument_list|(
name|HighlightParams
operator|.
name|FIELDS
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|this
operator|.
name|set
argument_list|(
name|HighlightParams
operator|.
name|HIGHLIGHT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** remove a field for highlighting    *     * @param f field name to not highlight    * @return true if removed, false otherwise    */
DECL|method|removeHighlightField
specifier|public
name|boolean
name|removeHighlightField
parameter_list|(
name|String
name|f
parameter_list|)
block|{
name|boolean
name|b
init|=
name|this
operator|.
name|remove
argument_list|(
name|HighlightParams
operator|.
name|FIELDS
argument_list|,
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|get
argument_list|(
name|HighlightParams
operator|.
name|FIELDS
argument_list|)
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|setHighlight
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
comment|/** get list of hl fields    *     * @return highlight fields or null if not set/empty    */
DECL|method|getHighlightFields
specifier|public
name|String
index|[]
name|getHighlightFields
parameter_list|()
block|{
return|return
name|this
operator|.
name|getParams
argument_list|(
name|HighlightParams
operator|.
name|FIELDS
argument_list|)
return|;
block|}
DECL|method|setHighlightSnippets
specifier|public
name|SolrQuery
name|setHighlightSnippets
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|HighlightParams
operator|.
name|SNIPPETS
argument_list|,
name|num
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getHighlightSnippets
specifier|public
name|int
name|getHighlightSnippets
parameter_list|()
block|{
return|return
name|this
operator|.
name|getInt
argument_list|(
name|HighlightParams
operator|.
name|SNIPPETS
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|setHighlightFragsize
specifier|public
name|SolrQuery
name|setHighlightFragsize
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|HighlightParams
operator|.
name|FRAGSIZE
argument_list|,
name|num
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getHighlightFragsize
specifier|public
name|int
name|getHighlightFragsize
parameter_list|()
block|{
return|return
name|this
operator|.
name|getInt
argument_list|(
name|HighlightParams
operator|.
name|FRAGSIZE
argument_list|,
literal|100
argument_list|)
return|;
block|}
DECL|method|setHighlightRequireFieldMatch
specifier|public
name|SolrQuery
name|setHighlightRequireFieldMatch
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|HighlightParams
operator|.
name|FIELD_MATCH
argument_list|,
name|flag
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getHighlightRequireFieldMatch
specifier|public
name|boolean
name|getHighlightRequireFieldMatch
parameter_list|()
block|{
return|return
name|this
operator|.
name|getBool
argument_list|(
name|HighlightParams
operator|.
name|FIELD_MATCH
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|setHighlightSimplePre
specifier|public
name|SolrQuery
name|setHighlightSimplePre
parameter_list|(
name|String
name|f
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|HighlightParams
operator|.
name|SIMPLE_PRE
argument_list|,
name|f
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getHighlightSimplePre
specifier|public
name|String
name|getHighlightSimplePre
parameter_list|()
block|{
return|return
name|this
operator|.
name|get
argument_list|(
name|HighlightParams
operator|.
name|SIMPLE_PRE
argument_list|,
literal|""
argument_list|)
return|;
block|}
DECL|method|setHighlightSimplePost
specifier|public
name|SolrQuery
name|setHighlightSimplePost
parameter_list|(
name|String
name|f
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|HighlightParams
operator|.
name|SIMPLE_POST
argument_list|,
name|f
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getHighlightSimplePost
specifier|public
name|String
name|getHighlightSimplePost
parameter_list|()
block|{
return|return
name|this
operator|.
name|get
argument_list|(
name|HighlightParams
operator|.
name|SIMPLE_POST
argument_list|,
literal|""
argument_list|)
return|;
block|}
DECL|method|setSortField
specifier|public
name|SolrQuery
name|setSortField
parameter_list|(
name|String
name|field
parameter_list|,
name|ORDER
name|order
parameter_list|)
block|{
name|this
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|)
expr_stmt|;
name|addValueToParam
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|,
name|toSortString
argument_list|(
name|field
argument_list|,
name|order
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addSortField
specifier|public
name|SolrQuery
name|addSortField
parameter_list|(
name|String
name|field
parameter_list|,
name|ORDER
name|order
parameter_list|)
block|{
return|return
name|addValueToParam
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|,
name|toSortString
argument_list|(
name|field
argument_list|,
name|order
argument_list|)
argument_list|)
return|;
block|}
DECL|method|removeSortField
specifier|public
name|SolrQuery
name|removeSortField
parameter_list|(
name|String
name|field
parameter_list|,
name|ORDER
name|order
parameter_list|)
block|{
name|String
name|s
init|=
name|this
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|)
decl_stmt|;
name|String
name|removeSort
init|=
name|toSortString
argument_list|(
name|field
argument_list|,
name|order
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|sorts
init|=
name|s
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|s
operator|=
name|join
argument_list|(
name|sorts
argument_list|,
literal|", "
argument_list|,
name|removeSort
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|s
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|getSortFields
specifier|public
name|String
index|[]
name|getSortFields
parameter_list|()
block|{
name|String
name|s
init|=
name|getSortField
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|s
operator|.
name|split
argument_list|(
literal|","
argument_list|)
return|;
block|}
DECL|method|getSortField
specifier|public
name|String
name|getSortField
parameter_list|()
block|{
return|return
name|this
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|)
return|;
block|}
DECL|method|setGetFieldStatistics
specifier|public
name|void
name|setGetFieldStatistics
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|StatsParams
operator|.
name|STATS
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
DECL|method|setGetFieldStatistics
specifier|public
name|void
name|setGetFieldStatistics
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|twopass
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|StatsParams
operator|.
name|STATS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|add
argument_list|(
name|StatsParams
operator|.
name|STATS_FIELD
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|set
argument_list|(
literal|"f."
operator|+
name|field
operator|+
literal|"."
operator|+
name|StatsParams
operator|.
name|STATS_TWOPASS
argument_list|,
name|twopass
operator|+
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|addStatsFieldFacets
specifier|public
name|void
name|addStatsFieldFacets
parameter_list|(
name|String
name|field
parameter_list|,
name|String
modifier|...
name|facets
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|add
argument_list|(
name|StatsParams
operator|.
name|STATS_FACET
argument_list|,
name|facets
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|f
range|:
name|facets
control|)
block|{
name|this
operator|.
name|add
argument_list|(
literal|"f."
operator|+
name|field
operator|+
literal|"."
operator|+
name|StatsParams
operator|.
name|STATS_FACET
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setFilterQueries
specifier|public
name|SolrQuery
name|setFilterQueries
parameter_list|(
name|String
modifier|...
name|fq
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|FQ
argument_list|,
name|fq
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addFilterQuery
specifier|public
name|SolrQuery
name|addFilterQuery
parameter_list|(
name|String
modifier|...
name|fq
parameter_list|)
block|{
name|this
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|FQ
argument_list|,
name|fq
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|removeFilterQuery
specifier|public
name|boolean
name|removeFilterQuery
parameter_list|(
name|String
name|fq
parameter_list|)
block|{
return|return
name|this
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|FQ
argument_list|,
name|fq
argument_list|)
return|;
block|}
DECL|method|getFilterQueries
specifier|public
name|String
index|[]
name|getFilterQueries
parameter_list|()
block|{
return|return
name|this
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|FQ
argument_list|)
return|;
block|}
DECL|method|getHighlight
specifier|public
name|boolean
name|getHighlight
parameter_list|()
block|{
return|return
name|this
operator|.
name|getBool
argument_list|(
name|HighlightParams
operator|.
name|HIGHLIGHT
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|setHighlight
specifier|public
name|SolrQuery
name|setHighlight
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
condition|)
block|{
name|this
operator|.
name|set
argument_list|(
name|HighlightParams
operator|.
name|HIGHLIGHT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|remove
argument_list|(
name|HighlightParams
operator|.
name|HIGHLIGHT
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|HighlightParams
operator|.
name|FIELD_MATCH
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|HighlightParams
operator|.
name|FIELDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|HighlightParams
operator|.
name|FORMATTER
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|HighlightParams
operator|.
name|FRAGSIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|HighlightParams
operator|.
name|SIMPLE_POST
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|HighlightParams
operator|.
name|SIMPLE_PRE
argument_list|)
expr_stmt|;
name|this
operator|.
name|remove
argument_list|(
name|HighlightParams
operator|.
name|SNIPPETS
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|setFields
specifier|public
name|SolrQuery
name|setFields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|)
expr_stmt|;
block|}
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
name|fields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addField
specifier|public
name|SolrQuery
name|addField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|addValueToParam
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|method|getFields
specifier|public
name|String
name|getFields
parameter_list|()
block|{
name|String
name|fields
init|=
name|this
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|equals
argument_list|(
literal|"score"
argument_list|)
condition|)
block|{
name|fields
operator|=
literal|"*, score"
expr_stmt|;
block|}
return|return
name|fields
return|;
block|}
DECL|method|setIncludeScore
specifier|public
name|SolrQuery
name|setIncludeScore
parameter_list|(
name|boolean
name|includeScore
parameter_list|)
block|{
if|if
condition|(
name|includeScore
condition|)
block|{
name|this
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"score"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"score"
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|setQuery
specifier|public
name|SolrQuery
name|setQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
name|query
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getQuery
specifier|public
name|String
name|getQuery
parameter_list|()
block|{
return|return
name|this
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|)
return|;
block|}
DECL|method|setRows
specifier|public
name|SolrQuery
name|setRows
parameter_list|(
name|Integer
name|rows
parameter_list|)
block|{
if|if
condition|(
name|rows
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|,
name|rows
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|getRows
specifier|public
name|Integer
name|getRows
parameter_list|()
block|{
return|return
name|this
operator|.
name|getInt
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|)
return|;
block|}
DECL|method|setShowDebugInfo
specifier|public
name|void
name|setShowDebugInfo
parameter_list|(
name|boolean
name|showDebugInfo
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|showDebugInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// use addSortField( sort, order
comment|//  public void setSort(String ... sort) {
comment|//    this.set(CommonParams.SORT, sort);
comment|//  }
DECL|method|setStart
specifier|public
name|SolrQuery
name|setStart
parameter_list|(
name|Integer
name|start
parameter_list|)
block|{
if|if
condition|(
name|start
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|,
name|start
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|getStart
specifier|public
name|Integer
name|getStart
parameter_list|()
block|{
return|return
name|this
operator|.
name|getInt
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|)
return|;
block|}
DECL|method|setQueryType
specifier|public
name|SolrQuery
name|setQueryType
parameter_list|(
name|String
name|qt
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
name|qt
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getQueryType
specifier|public
name|String
name|getQueryType
parameter_list|()
block|{
return|return
name|this
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
return|;
block|}
DECL|method|setParam
specifier|public
name|SolrQuery
name|setParam
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|values
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setParam
specifier|public
name|SolrQuery
name|setParam
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
name|this
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** get a deep copy of this object * */
DECL|method|getCopy
specifier|public
name|SolrQuery
name|getCopy
parameter_list|()
block|{
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|this
operator|.
name|getParameterNames
argument_list|()
control|)
block|{
name|q
operator|.
name|setParam
argument_list|(
name|name
argument_list|,
name|this
operator|.
name|getParams
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
comment|/**   * Set the maximum time allowed for this query. If the query takes more time   * than the specified milliseconds, a timeout occurs and partial (or no)   * results may be returned.   *    * If given Integer is null, then this parameter is removed from the request   *    *@param milliseconds the time in milliseconds allowed for this query   */
DECL|method|setTimeAllowed
specifier|public
name|SolrQuery
name|setTimeAllowed
parameter_list|(
name|Integer
name|milliseconds
parameter_list|)
block|{
if|if
condition|(
name|milliseconds
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|TIME_ALLOWED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|TIME_ALLOWED
argument_list|,
name|milliseconds
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**   * Get the maximum time allowed for this query.   */
DECL|method|getTimeAllowed
specifier|public
name|Integer
name|getTimeAllowed
parameter_list|()
block|{
return|return
name|this
operator|.
name|getInt
argument_list|(
name|CommonParams
operator|.
name|TIME_ALLOWED
argument_list|)
return|;
block|}
comment|///////////////////////
comment|//  Utility functions
comment|///////////////////////
DECL|method|toSortString
specifier|private
name|String
name|toSortString
parameter_list|(
name|String
name|field
parameter_list|,
name|ORDER
name|order
parameter_list|)
block|{
return|return
name|field
operator|.
name|trim
argument_list|()
operator|+
literal|' '
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|order
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
DECL|method|join
specifier|private
name|String
name|join
parameter_list|(
name|String
name|a
parameter_list|,
name|String
name|b
parameter_list|,
name|String
name|sep
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
operator|&&
name|a
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|b
operator|!=
literal|null
operator|&&
name|b
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
DECL|method|addValueToParam
specifier|private
name|SolrQuery
name|addValueToParam
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|String
name|tmp
init|=
name|this
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|tmp
operator|=
name|join
argument_list|(
name|tmp
argument_list|,
name|value
argument_list|,
literal|","
argument_list|)
expr_stmt|;
name|this
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|join
specifier|private
name|String
name|join
parameter_list|(
name|String
index|[]
name|vals
parameter_list|,
name|String
name|sep
parameter_list|,
name|String
name|removeVal
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|removeVal
operator|==
literal|null
operator|||
operator|!
name|vals
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|removeVal
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|vals
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
end_class
end_unit
