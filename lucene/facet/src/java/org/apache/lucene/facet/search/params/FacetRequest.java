begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search.params
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
operator|.
name|params
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
name|index
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
name|search
operator|.
name|CategoryListIterator
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
name|FacetArrays
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
name|FacetResultsHandler
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
name|TopKFacetResultsHandler
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
name|TopKInEachNodeHandler
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
name|aggregator
operator|.
name|Aggregator
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
name|cache
operator|.
name|CategoryListCache
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
name|cache
operator|.
name|CategoryListData
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
name|CategoryPath
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Request to accumulate facet information for a specified facet and possibly   * also some of its descendants, upto a specified depth.  *<p>  * The facet request additionally defines what information should   * be computed within the facet results, if and how should results  * be ordered, etc.  *<P>  * An example facet request is to look at all sub-categories of "Author", and  * return the 10 with the highest counts (sorted by decreasing count).   *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FacetRequest
specifier|public
specifier|abstract
class|class
name|FacetRequest
implements|implements
name|Cloneable
block|{
comment|/**    * Default depth for facets accumulation.    * @see #getDepth()    */
DECL|field|DEFAULT_DEPTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_DEPTH
init|=
literal|1
decl_stmt|;
comment|/**    * Default sort mode.    * @see #getSortBy()    */
DECL|field|DEFAULT_SORT_BY
specifier|public
specifier|static
specifier|final
name|SortBy
name|DEFAULT_SORT_BY
init|=
name|SortBy
operator|.
name|VALUE
decl_stmt|;
comment|/**    * Default result mode    * @see #getResultMode()    */
DECL|field|DEFAULT_RESULT_MODE
specifier|public
specifier|static
specifier|final
name|ResultMode
name|DEFAULT_RESULT_MODE
init|=
name|ResultMode
operator|.
name|PER_NODE_IN_TREE
decl_stmt|;
DECL|field|categoryPath
specifier|private
specifier|final
name|CategoryPath
name|categoryPath
decl_stmt|;
DECL|field|numResults
specifier|private
specifier|final
name|int
name|numResults
decl_stmt|;
DECL|field|numLabel
specifier|private
name|int
name|numLabel
decl_stmt|;
DECL|field|depth
specifier|private
name|int
name|depth
decl_stmt|;
DECL|field|sortOrder
specifier|private
name|SortOrder
name|sortOrder
decl_stmt|;
DECL|field|sortBy
specifier|private
name|SortBy
name|sortBy
decl_stmt|;
comment|/**    * Computed at construction, this hashCode is based on two final members    * {@link CategoryPath} and<code>numResults</code>    */
DECL|field|hashCode
specifier|private
specifier|final
name|int
name|hashCode
decl_stmt|;
DECL|field|resultMode
specifier|private
name|ResultMode
name|resultMode
init|=
name|DEFAULT_RESULT_MODE
decl_stmt|;
comment|/**    * Initialize the request with a given path, and a requested number of facets    * results. By default, all returned results would be labeled - to alter this    * default see {@link #setNumLabel(int)}.    *<p>    *<b>NOTE:</b> if<code>numResults</code> is given as    *<code>Integer.MAX_VALUE</code> than all the facet results would be    * returned, without any limit.    *<p>    *<b>NOTE:</b> it is assumed that the given {@link CategoryPath} is not    * modified after construction of this object. Otherwise, some things may not    * function properly, e.g. {@link #hashCode()}.    *     * @throws IllegalArgumentException if numResults is&le; 0    */
DECL|method|FacetRequest
specifier|public
name|FacetRequest
parameter_list|(
name|CategoryPath
name|path
parameter_list|,
name|int
name|numResults
parameter_list|)
block|{
if|if
condition|(
name|numResults
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"num results must be a positive (>0) number: "
operator|+
name|numResults
argument_list|)
throw|;
block|}
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"category path cannot be null!"
argument_list|)
throw|;
block|}
name|categoryPath
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|numResults
operator|=
name|numResults
expr_stmt|;
name|numLabel
operator|=
name|numResults
expr_stmt|;
name|depth
operator|=
name|DEFAULT_DEPTH
expr_stmt|;
name|sortBy
operator|=
name|DEFAULT_SORT_BY
expr_stmt|;
name|sortOrder
operator|=
name|SortOrder
operator|.
name|DESCENDING
expr_stmt|;
name|hashCode
operator|=
name|categoryPath
operator|.
name|hashCode
argument_list|()
operator|^
name|this
operator|.
name|numResults
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|FacetRequest
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
comment|// Overridden to make it public
return|return
operator|(
name|FacetRequest
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|setNumLabel
specifier|public
name|void
name|setNumLabel
parameter_list|(
name|int
name|numLabel
parameter_list|)
block|{
name|this
operator|.
name|numLabel
operator|=
name|numLabel
expr_stmt|;
block|}
DECL|method|setDepth
specifier|public
name|void
name|setDepth
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
block|}
DECL|method|setSortOrder
specifier|public
name|void
name|setSortOrder
parameter_list|(
name|SortOrder
name|sortOrder
parameter_list|)
block|{
name|this
operator|.
name|sortOrder
operator|=
name|sortOrder
expr_stmt|;
block|}
DECL|method|setSortBy
specifier|public
name|void
name|setSortBy
parameter_list|(
name|SortBy
name|sortBy
parameter_list|)
block|{
name|this
operator|.
name|sortBy
operator|=
name|sortBy
expr_stmt|;
block|}
comment|/**    * The root category of this facet request. The categories that are returned    * as a result of this request will all be descendants of this root.    *<p>    *<b>NOTE:</b> you should not modify the returned {@link CategoryPath}, or    * otherwise some methonds may not work properly, e.g. {@link #hashCode()}.    */
DECL|method|getCategoryPath
specifier|public
specifier|final
name|CategoryPath
name|getCategoryPath
parameter_list|()
block|{
return|return
name|categoryPath
return|;
block|}
comment|/**    * How deeply to look under the given category. If the depth is 0,    * only the category itself is counted. If the depth is 1, its immediate    * children are also counted, and so on. If the depth is Integer.MAX_VALUE,    * all the category's descendants are counted.<br>    * TODO (Facet): add AUTO_EXPAND option      */
DECL|method|getDepth
specifier|public
specifier|final
name|int
name|getDepth
parameter_list|()
block|{
return|return
name|depth
return|;
block|}
comment|/**    * If getNumLabel()&lt; getNumResults(), only the first getNumLabel() results    * will have their category paths calculated, and the rest will only be    * available as ordinals (category numbers) and will have null paths.    *<P>    * If Integer.MAX_VALUE is specified, all     * results are labled.    *<P>    * The purpose of this parameter is to avoid having to run the whole    * faceted search again when the user asks for more values for the facet;    * The application can ask (getNumResults()) for more values than it needs    * to show, but keep getNumLabel() only the number it wants to immediately    * show. The slow-down caused by finding more values is negligible, because    * the slowest part - finding the categories' paths, is avoided.    *<p>    * Depending on the {@link #getResultMode() LimitsMode},    * this limit is applied globally or per results node.    * In the global mode, if this limit is 3,     * only 3 top results would be labeled.    * In the per-node mode, if this limit is 3,    * 3 top children of {@link #getCategoryPath() the target category} would be labeled,    * as well as 3 top children of each of them, and so forth, until the depth defined     * by {@link #getDepth()}.    * @see #getResultMode()    */
DECL|method|getNumLabel
specifier|public
specifier|final
name|int
name|getNumLabel
parameter_list|()
block|{
return|return
name|numLabel
return|;
block|}
comment|/**    * The number of sub-categories to return (at most).    * If the sub-categories are returned.    *<p>    * If Integer.MAX_VALUE is specified, all     * sub-categories are returned.    *<p>    * Depending on the {@link #getResultMode() LimitsMode},    * this limit is applied globally or per results node.    * In the global mode, if this limit is 3,     * only 3 top results would be computed.    * In the per-node mode, if this limit is 3,    * 3 top children of {@link #getCategoryPath() the target category} would be returned,    * as well as 3 top children of each of them, and so forth, until the depth defined     * by {@link #getDepth()}.    * @see #getResultMode()    */
DECL|method|getNumResults
specifier|public
specifier|final
name|int
name|getNumResults
parameter_list|()
block|{
return|return
name|numResults
return|;
block|}
comment|/**    * Sort options for facet results.    */
DECL|enum|SortBy
specifier|public
enum|enum
name|SortBy
block|{
comment|/** sort by category ordinal with the taxonomy */
DECL|enum constant|ORDINAL
name|ORDINAL
block|,
comment|/** sort by computed category value */
DECL|enum constant|VALUE
name|VALUE
block|}
comment|/** Specify how should results be sorted. */
DECL|method|getSortBy
specifier|public
specifier|final
name|SortBy
name|getSortBy
parameter_list|()
block|{
return|return
name|sortBy
return|;
block|}
comment|/** Requested sort order for the results. */
DECL|enum|SortOrder
DECL|enum constant|ASCENDING
DECL|enum constant|DESCENDING
specifier|public
enum|enum
name|SortOrder
block|{
name|ASCENDING
block|,
name|DESCENDING
block|}
comment|/** Return the requested order of results. */
DECL|method|getSortOrder
specifier|public
specifier|final
name|SortOrder
name|getSortOrder
parameter_list|()
block|{
return|return
name|sortOrder
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|categoryPath
operator|.
name|toString
argument_list|()
operator|+
literal|" nRes="
operator|+
name|numResults
operator|+
literal|" nLbl="
operator|+
name|numLabel
return|;
block|}
comment|/**    * Creates a new {@link FacetResultsHandler} that matches the request logic    * and current settings, such as {@link #getDepth() depth},    * {@link #getResultMode() limits-mode}, etc, as well as the passed in    * {@link TaxonomyReader}.    *     * @param taxonomyReader taxonomy reader is needed e.g. for knowing the    *        taxonomy size.    */
DECL|method|createFacetResultsHandler
specifier|public
name|FacetResultsHandler
name|createFacetResultsHandler
parameter_list|(
name|TaxonomyReader
name|taxonomyReader
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|resultMode
operator|==
name|ResultMode
operator|.
name|PER_NODE_IN_TREE
condition|)
block|{
return|return
operator|new
name|TopKInEachNodeHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|clone
argument_list|()
argument_list|)
return|;
block|}
return|return
operator|new
name|TopKFacetResultsHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|clone
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
comment|// Shouldn't happen since we implement Cloneable. If it does happen, it is
comment|// probably because the class was changed to not implement Cloneable
comment|// anymore.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Result structure manner of applying request's limits such as     * {@link #getNumLabel()} and    * {@link #getNumResults()}.    */
DECL|enum|ResultMode
specifier|public
enum|enum
name|ResultMode
block|{
comment|/** Limits are applied per node, and the result has a full tree structure. */
DECL|enum constant|PER_NODE_IN_TREE
name|PER_NODE_IN_TREE
block|,
comment|/** Limits are applied globally, on total number of results, and the result has a flat structure. */
DECL|enum constant|GLOBAL_FLAT
name|GLOBAL_FLAT
block|}
comment|/** Return the requested result mode. */
DECL|method|getResultMode
specifier|public
specifier|final
name|ResultMode
name|getResultMode
parameter_list|()
block|{
return|return
name|resultMode
return|;
block|}
comment|/**    * @param resultMode the resultMode to set    * @see #getResultMode()    */
DECL|method|setResultMode
specifier|public
name|void
name|setResultMode
parameter_list|(
name|ResultMode
name|resultMode
parameter_list|)
block|{
name|this
operator|.
name|resultMode
operator|=
name|resultMode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hashCode
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|FacetRequest
condition|)
block|{
name|FacetRequest
name|that
init|=
operator|(
name|FacetRequest
operator|)
name|o
decl_stmt|;
return|return
name|that
operator|.
name|hashCode
operator|==
name|this
operator|.
name|hashCode
operator|&&
name|that
operator|.
name|categoryPath
operator|.
name|equals
argument_list|(
name|this
operator|.
name|categoryPath
argument_list|)
operator|&&
name|that
operator|.
name|numResults
operator|==
name|this
operator|.
name|numResults
operator|&&
name|that
operator|.
name|depth
operator|==
name|this
operator|.
name|depth
operator|&&
name|that
operator|.
name|resultMode
operator|==
name|this
operator|.
name|resultMode
operator|&&
name|that
operator|.
name|numLabel
operator|==
name|this
operator|.
name|numLabel
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Create an aggregator for this facet request. Aggregator action depends on    * request definition. For a count request, it will usually increment the    * count for that facet.    *     * @param useComplements    *          whether the complements optimization is being used for current    *          computation.    * @param arrays    *          provider for facet arrays in use for current computation.    * @param taxonomy    *          reader of taxonomy in effect.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|createAggregator
specifier|public
specifier|abstract
name|Aggregator
name|createAggregator
parameter_list|(
name|boolean
name|useComplements
parameter_list|,
name|FacetArrays
name|arrays
parameter_list|,
name|TaxonomyReader
name|taxonomy
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create the category list iterator for the specified partition. If a non    * null cache is provided which contains the required data, use it for the    * iteration.    */
DECL|method|createCategoryListIterator
specifier|public
name|CategoryListIterator
name|createCategoryListIterator
parameter_list|(
name|TaxonomyReader
name|taxo
parameter_list|,
name|FacetSearchParams
name|sParams
parameter_list|,
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
block|{
name|CategoryListCache
name|clCache
init|=
name|sParams
operator|.
name|getCategoryListCache
argument_list|()
decl_stmt|;
name|CategoryListParams
name|clParams
init|=
name|sParams
operator|.
name|getFacetIndexingParams
argument_list|()
operator|.
name|getCategoryListParams
argument_list|(
name|categoryPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|clCache
operator|!=
literal|null
condition|)
block|{
name|CategoryListData
name|clData
init|=
name|clCache
operator|.
name|get
argument_list|(
name|clParams
argument_list|)
decl_stmt|;
if|if
condition|(
name|clData
operator|!=
literal|null
condition|)
block|{
return|return
name|clData
operator|.
name|iterator
argument_list|(
name|partition
argument_list|)
return|;
block|}
block|}
return|return
name|clParams
operator|.
name|createCategoryListIterator
argument_list|(
name|partition
argument_list|)
return|;
block|}
comment|/**    * Return the value of a category used for facets computations for this    * request. For a count request this would be the count for that facet, i.e.    * an integer number. but for other requests this can be the result of a more    * complex operation, and the result can be any double precision number.    * Having this method with a general name<b>value</b> which is double    * precision allows to have more compact API and code for handling counts and    * perhaps other requests (such as for associations) very similarly, and by    * the same code and API, avoiding code duplication.    *     * @param arrays    *          provider for facet arrays in use for current computation.    * @param idx    *          an index into the count arrays now in effect in    *<code>arrays</code>. E.g., for ordinal number<i>n</i>, with    *          partition, of size<i>partitionSize</i>, now covering<i>n</i>,    *<code>getValueOf</code> would be invoked with<code>idx</code>    *          being<i>n</i> %<i>partitionSize</i>.    */
DECL|method|getValueOf
specifier|public
specifier|abstract
name|double
name|getValueOf
parameter_list|(
name|FacetArrays
name|arrays
parameter_list|,
name|int
name|idx
parameter_list|)
function_decl|;
comment|/**    * Indicates whether this facet request is eligible for applying the complements optimization.    */
DECL|method|supportsComplements
specifier|public
name|boolean
name|supportsComplements
parameter_list|()
block|{
return|return
literal|false
return|;
comment|// by default: no
block|}
comment|/** Indicates whether the results of this request depends on each result document's score */
DECL|method|requireDocumentScore
specifier|public
specifier|abstract
name|boolean
name|requireDocumentScore
parameter_list|()
function_decl|;
block|}
end_class
end_unit
