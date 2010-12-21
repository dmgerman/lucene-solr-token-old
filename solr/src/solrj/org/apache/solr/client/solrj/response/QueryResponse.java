begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.response
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
operator|.
name|response
package|;
end_package
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
name|Date
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
name|LinkedHashMap
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
name|solr
operator|.
name|common
operator|.
name|SolrDocumentList
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
name|util
operator|.
name|NamedList
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
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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
name|client
operator|.
name|solrj
operator|.
name|beans
operator|.
name|DocumentObjectBinder
import|;
end_import
begin_comment
comment|/**  *   * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|QueryResponse
specifier|public
class|class
name|QueryResponse
extends|extends
name|SolrResponseBase
block|{
comment|// Direct pointers to known types
DECL|field|_header
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|_header
init|=
literal|null
decl_stmt|;
DECL|field|_results
specifier|private
name|SolrDocumentList
name|_results
init|=
literal|null
decl_stmt|;
DECL|field|_sortvalues
specifier|private
name|NamedList
argument_list|<
name|ArrayList
argument_list|>
name|_sortvalues
init|=
literal|null
decl_stmt|;
DECL|field|_facetInfo
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|_facetInfo
init|=
literal|null
decl_stmt|;
DECL|field|_debugInfo
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|_debugInfo
init|=
literal|null
decl_stmt|;
DECL|field|_highlightingInfo
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|_highlightingInfo
init|=
literal|null
decl_stmt|;
DECL|field|_spellInfo
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|_spellInfo
init|=
literal|null
decl_stmt|;
DECL|field|_statsInfo
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|_statsInfo
init|=
literal|null
decl_stmt|;
DECL|field|_termsInfo
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|_termsInfo
init|=
literal|null
decl_stmt|;
comment|// Facet stuff
DECL|field|_facetQuery
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|_facetQuery
init|=
literal|null
decl_stmt|;
DECL|field|_facetFields
specifier|private
name|List
argument_list|<
name|FacetField
argument_list|>
name|_facetFields
init|=
literal|null
decl_stmt|;
DECL|field|_limitingFacets
specifier|private
name|List
argument_list|<
name|FacetField
argument_list|>
name|_limitingFacets
init|=
literal|null
decl_stmt|;
DECL|field|_facetDates
specifier|private
name|List
argument_list|<
name|FacetField
argument_list|>
name|_facetDates
init|=
literal|null
decl_stmt|;
DECL|field|_facetPivot
specifier|private
name|NamedList
argument_list|<
name|List
argument_list|<
name|PivotField
argument_list|>
argument_list|>
name|_facetPivot
init|=
literal|null
decl_stmt|;
comment|// Highlight Info
DECL|field|_highlighting
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|_highlighting
init|=
literal|null
decl_stmt|;
comment|// SpellCheck Response
DECL|field|_spellResponse
specifier|private
name|SpellCheckResponse
name|_spellResponse
init|=
literal|null
decl_stmt|;
comment|// Terms Response
DECL|field|_termsResponse
specifier|private
name|TermsResponse
name|_termsResponse
init|=
literal|null
decl_stmt|;
comment|// Field stats Response
DECL|field|_fieldStatsInfo
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|FieldStatsInfo
argument_list|>
name|_fieldStatsInfo
init|=
literal|null
decl_stmt|;
comment|// Debug Info
DECL|field|_debugMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|_debugMap
init|=
literal|null
decl_stmt|;
DECL|field|_explainMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|_explainMap
init|=
literal|null
decl_stmt|;
comment|// utility variable used for automatic binding -- it should not be serialized
DECL|field|solrServer
specifier|private
specifier|transient
specifier|final
name|SolrServer
name|solrServer
decl_stmt|;
DECL|method|QueryResponse
specifier|public
name|QueryResponse
parameter_list|()
block|{
name|solrServer
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Utility constructor to set the solrServer and namedList    */
DECL|method|QueryResponse
specifier|public
name|QueryResponse
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
parameter_list|,
name|SolrServer
name|solrServer
parameter_list|)
block|{
name|this
operator|.
name|setResponse
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|this
operator|.
name|solrServer
operator|=
name|solrServer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setResponse
specifier|public
name|void
name|setResponse
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
parameter_list|)
block|{
name|super
operator|.
name|setResponse
argument_list|(
name|res
argument_list|)
expr_stmt|;
comment|// Look for known things
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|res
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|n
init|=
name|res
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"responseHeader"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_header
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"response"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_results
operator|=
operator|(
name|SolrDocumentList
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"sort_values"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_sortvalues
operator|=
operator|(
name|NamedList
argument_list|<
name|ArrayList
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"facet_counts"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_facetInfo
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|extractFacetInfo
argument_list|(
name|_facetInfo
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"debug"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_debugInfo
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|extractDebugInfo
argument_list|(
name|_debugInfo
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"highlighting"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_highlightingInfo
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|extractHighlightingInfo
argument_list|(
name|_highlightingInfo
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"spellcheck"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_spellInfo
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|extractSpellCheckInfo
argument_list|(
name|_spellInfo
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"stats"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_statsInfo
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|extractStatsInfo
argument_list|(
name|_statsInfo
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"terms"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|_termsInfo
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|extractTermsInfo
argument_list|(
name|_termsInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|extractSpellCheckInfo
specifier|private
name|void
name|extractSpellCheckInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|spellInfo
parameter_list|)
block|{
name|_spellResponse
operator|=
operator|new
name|SpellCheckResponse
argument_list|(
name|spellInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|extractTermsInfo
specifier|private
name|void
name|extractTermsInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|termsInfo
parameter_list|)
block|{
name|_termsResponse
operator|=
operator|new
name|TermsResponse
argument_list|(
name|termsInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|extractStatsInfo
specifier|private
name|void
name|extractStatsInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|_fieldStatsInfo
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldStatsInfo
argument_list|>
argument_list|()
expr_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|ff
init|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"stats_fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ff
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|entry
range|:
name|ff
control|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|v
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|_fieldStatsInfo
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|FieldStatsInfo
argument_list|(
name|v
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|extractDebugInfo
specifier|private
name|void
name|extractDebugInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|debug
parameter_list|)
block|{
name|_debugMap
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
comment|// keep the order
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
range|:
name|debug
control|)
block|{
name|_debugMap
operator|.
name|put
argument_list|(
name|info
operator|.
name|getKey
argument_list|()
argument_list|,
name|info
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Parse out interesting bits from the debug info
name|_explainMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|explain
init|=
operator|(
name|NamedList
argument_list|<
name|String
argument_list|>
operator|)
name|_debugMap
operator|.
name|get
argument_list|(
literal|"explain"
argument_list|)
decl_stmt|;
if|if
condition|(
name|explain
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|info
range|:
name|explain
control|)
block|{
name|String
name|key
init|=
name|info
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|_explainMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|info
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|extractHighlightingInfo
specifier|private
name|void
name|extractHighlightingInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|info
parameter_list|)
block|{
name|_highlighting
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
range|:
name|info
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|fieldMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|_highlighting
operator|.
name|put
argument_list|(
name|doc
operator|.
name|getKey
argument_list|()
argument_list|,
name|fieldMap
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|fnl
init|=
operator|(
name|NamedList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
operator|)
name|doc
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|field
range|:
name|fnl
control|)
block|{
name|fieldMap
operator|.
name|put
argument_list|(
name|field
operator|.
name|getKey
argument_list|()
argument_list|,
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|extractFacetInfo
specifier|private
name|void
name|extractFacetInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|info
parameter_list|)
block|{
comment|// Parse the queries
name|_facetQuery
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|fq
init|=
operator|(
name|NamedList
argument_list|<
name|Integer
argument_list|>
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"facet_queries"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fq
operator|!=
literal|null
condition|)
block|{
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
name|entry
range|:
name|fq
control|)
block|{
name|_facetQuery
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Parse the facet info into fields
comment|// TODO?? The list could be<int> or<long>?  If always<long> then we can switch to<Long>
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Number
argument_list|>
argument_list|>
name|ff
init|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Number
argument_list|>
argument_list|>
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"facet_fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ff
operator|!=
literal|null
condition|)
block|{
name|_facetFields
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetField
argument_list|>
argument_list|(
name|ff
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|_limitingFacets
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetField
argument_list|>
argument_list|(
name|ff
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|minsize
init|=
name|_results
operator|==
literal|null
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
name|_results
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Number
argument_list|>
argument_list|>
name|facet
range|:
name|ff
control|)
block|{
name|FacetField
name|f
init|=
operator|new
name|FacetField
argument_list|(
name|facet
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Number
argument_list|>
name|entry
range|:
name|facet
operator|.
name|getValue
argument_list|()
control|)
block|{
name|f
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|_facetFields
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|FacetField
name|nl
init|=
name|f
operator|.
name|getLimitingFields
argument_list|(
name|minsize
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|.
name|getValueCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|_limitingFacets
operator|.
name|add
argument_list|(
name|nl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//Parse date facets
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|df
init|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"facet_dates"
argument_list|)
decl_stmt|;
if|if
condition|(
name|df
operator|!=
literal|null
condition|)
block|{
comment|// System.out.println(df);
name|_facetDates
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetField
argument_list|>
argument_list|(
name|df
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|facet
range|:
name|df
control|)
block|{
comment|// System.out.println("Key: " + facet.getKey() + " Value: " + facet.getValue());
name|NamedList
argument_list|<
name|Object
argument_list|>
name|values
init|=
name|facet
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|gap
init|=
operator|(
name|String
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"gap"
argument_list|)
decl_stmt|;
name|Date
name|end
init|=
operator|(
name|Date
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"end"
argument_list|)
decl_stmt|;
name|FacetField
name|f
init|=
operator|new
name|FacetField
argument_list|(
name|facet
operator|.
name|getKey
argument_list|()
argument_list|,
name|gap
argument_list|,
name|end
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|values
control|)
block|{
try|try
block|{
name|f
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//Ignore for non-number responses which are already handled above
block|}
block|}
name|_facetDates
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Parse pivot facets
name|NamedList
name|pf
init|=
operator|(
name|NamedList
operator|)
name|info
operator|.
name|get
argument_list|(
literal|"facet_pivot"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pf
operator|!=
literal|null
condition|)
block|{
name|_facetPivot
operator|=
operator|new
name|NamedList
argument_list|<
name|List
argument_list|<
name|PivotField
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pf
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|_facetPivot
operator|.
name|add
argument_list|(
name|pf
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|readPivots
argument_list|(
operator|(
name|List
argument_list|<
name|NamedList
argument_list|>
operator|)
name|pf
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readPivots
specifier|protected
name|List
argument_list|<
name|PivotField
argument_list|>
name|readPivots
parameter_list|(
name|List
argument_list|<
name|NamedList
argument_list|>
name|list
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|PivotField
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|PivotField
argument_list|>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|NamedList
name|nl
range|:
name|list
control|)
block|{
comment|// NOTE, this is cheating, but we know the order they are written in, so no need to check
name|String
name|f
init|=
operator|(
name|String
operator|)
name|nl
operator|.
name|getVal
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Object
name|v
init|=
name|nl
operator|.
name|getVal
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|cnt
init|=
operator|(
operator|(
name|Integer
operator|)
name|nl
operator|.
name|getVal
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PivotField
argument_list|>
name|p
init|=
operator|(
name|nl
operator|.
name|size
argument_list|()
operator|<
literal|4
operator|)
condition|?
literal|null
else|:
name|readPivots
argument_list|(
operator|(
name|List
argument_list|<
name|NamedList
argument_list|>
operator|)
name|nl
operator|.
name|getVal
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|PivotField
argument_list|(
name|f
argument_list|,
name|v
argument_list|,
name|cnt
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
comment|//------------------------------------------------------
comment|//------------------------------------------------------
comment|/**    * Remove the field facet info    */
DECL|method|removeFacets
specifier|public
name|void
name|removeFacets
parameter_list|()
block|{
name|_facetFields
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetField
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|//------------------------------------------------------
comment|//------------------------------------------------------
DECL|method|getHeader
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getHeader
parameter_list|()
block|{
return|return
name|_header
return|;
block|}
DECL|method|getResults
specifier|public
name|SolrDocumentList
name|getResults
parameter_list|()
block|{
return|return
name|_results
return|;
block|}
DECL|method|getSortValues
specifier|public
name|NamedList
argument_list|<
name|ArrayList
argument_list|>
name|getSortValues
parameter_list|()
block|{
return|return
name|_sortvalues
return|;
block|}
DECL|method|getDebugMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getDebugMap
parameter_list|()
block|{
return|return
name|_debugMap
return|;
block|}
DECL|method|getExplainMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getExplainMap
parameter_list|()
block|{
return|return
name|_explainMap
return|;
block|}
DECL|method|getFacetQuery
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getFacetQuery
parameter_list|()
block|{
return|return
name|_facetQuery
return|;
block|}
DECL|method|getHighlighting
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|getHighlighting
parameter_list|()
block|{
return|return
name|_highlighting
return|;
block|}
DECL|method|getSpellCheckResponse
specifier|public
name|SpellCheckResponse
name|getSpellCheckResponse
parameter_list|()
block|{
return|return
name|_spellResponse
return|;
block|}
DECL|method|getTermsResponse
specifier|public
name|TermsResponse
name|getTermsResponse
parameter_list|()
block|{
return|return
name|_termsResponse
return|;
block|}
comment|/**    * See also: {@link #getLimitingFacets()}    */
DECL|method|getFacetFields
specifier|public
name|List
argument_list|<
name|FacetField
argument_list|>
name|getFacetFields
parameter_list|()
block|{
return|return
name|_facetFields
return|;
block|}
DECL|method|getFacetDates
specifier|public
name|List
argument_list|<
name|FacetField
argument_list|>
name|getFacetDates
parameter_list|()
block|{
return|return
name|_facetDates
return|;
block|}
DECL|method|getFacetPivot
specifier|public
name|NamedList
argument_list|<
name|List
argument_list|<
name|PivotField
argument_list|>
argument_list|>
name|getFacetPivot
parameter_list|()
block|{
return|return
name|_facetPivot
return|;
block|}
comment|/** get     *     * @param name the name of the     * @return the FacetField by name or null if it does not exist    */
DECL|method|getFacetField
specifier|public
name|FacetField
name|getFacetField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|_facetFields
operator|==
literal|null
condition|)
return|return
literal|null
return|;
for|for
control|(
name|FacetField
name|f
range|:
name|_facetFields
control|)
block|{
if|if
condition|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|f
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getFacetDate
specifier|public
name|FacetField
name|getFacetDate
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|_facetDates
operator|==
literal|null
condition|)
return|return
literal|null
return|;
for|for
control|(
name|FacetField
name|f
range|:
name|_facetDates
control|)
if|if
condition|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|f
return|;
return|return
literal|null
return|;
block|}
comment|/**    * @return a list of FacetFields where the count is less then    * then #getResults() {@link SolrDocumentList#getNumFound()}    *     * If you want all results exactly as returned by solr, use:    * {@link #getFacetFields()}    */
DECL|method|getLimitingFacets
specifier|public
name|List
argument_list|<
name|FacetField
argument_list|>
name|getLimitingFacets
parameter_list|()
block|{
return|return
name|_limitingFacets
return|;
block|}
DECL|method|getBeans
specifier|public
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|getBeans
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|solrServer
operator|==
literal|null
condition|?
operator|new
name|DocumentObjectBinder
argument_list|()
operator|.
name|getBeans
argument_list|(
name|type
argument_list|,
name|_results
argument_list|)
else|:
name|solrServer
operator|.
name|getBinder
argument_list|()
operator|.
name|getBeans
argument_list|(
name|type
argument_list|,
name|_results
argument_list|)
return|;
block|}
DECL|method|getFieldStatsInfo
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|FieldStatsInfo
argument_list|>
name|getFieldStatsInfo
parameter_list|()
block|{
return|return
name|_fieldStatsInfo
return|;
block|}
block|}
end_class
end_unit
