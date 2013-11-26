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
name|Arrays
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
name|index
operator|.
name|IndexReader
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
name|Term
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
name|BooleanClause
operator|.
name|Occur
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
name|BooleanClause
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
name|BooleanQuery
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
name|ConstantScoreQuery
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
name|Filter
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
name|FilteredQuery
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
name|MatchAllDocsQuery
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
name|Query
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
name|TermQuery
import|;
end_import
begin_comment
comment|/**  * A {@link Query} for drill-down over {@link FacetLabel categories}. You  * should call {@link #add(FacetLabel...)} for every group of categories you  * want to drill-down over. Each category in the group is {@code OR'ed} with  * the others, and groups are {@code AND'ed}.  *<p>  *<b>NOTE:</b> if you choose to create your own {@link Query} by calling  * {@link #term}, it is recommended to wrap it with {@link ConstantScoreQuery}  * and set the {@link ConstantScoreQuery#setBoost(float) boost} to {@code 0.0f},  * so that it does not affect the scores of the documents.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|SimpleDrillDownQuery
specifier|public
specifier|final
class|class
name|SimpleDrillDownQuery
extends|extends
name|Query
block|{
DECL|method|term
specifier|public
specifier|static
name|Term
name|term
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|FacetsConfig
operator|.
name|pathToString
argument_list|(
name|dim
argument_list|,
name|path
argument_list|)
argument_list|)
return|;
block|}
DECL|field|config
specifier|private
specifier|final
name|FacetsConfig
name|config
decl_stmt|;
DECL|field|query
specifier|private
specifier|final
name|BooleanQuery
name|query
decl_stmt|;
DECL|field|drillDownDims
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Used by clone() */
DECL|method|SimpleDrillDownQuery
name|SimpleDrillDownQuery
parameter_list|(
name|FacetsConfig
name|config
parameter_list|,
name|BooleanQuery
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|drillDownDims
operator|.
name|putAll
argument_list|(
name|drillDownDims
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|/** Used by DrillSideways */
DECL|method|SimpleDrillDownQuery
name|SimpleDrillDownQuery
parameter_list|(
name|FacetsConfig
name|config
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|SimpleDrillDownQuery
name|other
parameter_list|)
block|{
name|query
operator|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// disable coord
name|BooleanClause
index|[]
name|clauses
init|=
name|other
operator|.
name|query
operator|.
name|getClauses
argument_list|()
decl_stmt|;
if|if
condition|(
name|clauses
operator|.
name|length
operator|==
name|other
operator|.
name|drillDownDims
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot apply filter unless baseQuery isn't null; pass ConstantScoreQuery instead"
argument_list|)
throw|;
block|}
assert|assert
name|clauses
operator|.
name|length
operator|==
literal|1
operator|+
name|other
operator|.
name|drillDownDims
operator|.
name|size
argument_list|()
operator|:
name|clauses
operator|.
name|length
operator|+
literal|" vs "
operator|+
operator|(
literal|1
operator|+
name|other
operator|.
name|drillDownDims
operator|.
name|size
argument_list|()
operator|)
assert|;
name|drillDownDims
operator|.
name|putAll
argument_list|(
name|other
operator|.
name|drillDownDims
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|FilteredQuery
argument_list|(
name|clauses
index|[
literal|0
index|]
operator|.
name|getQuery
argument_list|()
argument_list|,
name|filter
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
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
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|clauses
index|[
name|i
index|]
operator|.
name|getQuery
argument_list|()
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|/** Used by DrillSideways */
DECL|method|SimpleDrillDownQuery
name|SimpleDrillDownQuery
parameter_list|(
name|FacetsConfig
name|config
parameter_list|,
name|Query
name|baseQuery
parameter_list|,
name|List
argument_list|<
name|Query
argument_list|>
name|clauses
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|baseQuery
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|add
argument_list|(
name|baseQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Query
name|clause
range|:
name|clauses
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|clause
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|drillDownDims
operator|.
name|putAll
argument_list|(
name|drillDownDims
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|/**    * Creates a new {@code SimpleDrillDownQuery} without a base query,     * to perform a pure browsing query (equivalent to using    * {@link MatchAllDocsQuery} as base).    */
DECL|method|SimpleDrillDownQuery
specifier|public
name|SimpleDrillDownQuery
parameter_list|(
name|FacetsConfig
name|config
parameter_list|)
block|{
name|this
argument_list|(
name|config
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@code SimpleDrillDownQuery} over the given base query. Can be    * {@code null}, in which case the result {@link Query} from    * {@link #rewrite(IndexReader)} will be a pure browsing query, filtering on    * the added categories only.    */
DECL|method|SimpleDrillDownQuery
specifier|public
name|SimpleDrillDownQuery
parameter_list|(
name|FacetsConfig
name|config
parameter_list|,
name|Query
name|baseQuery
parameter_list|)
block|{
name|query
operator|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// disable coord
if|if
condition|(
name|baseQuery
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|add
argument_list|(
name|baseQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|/** Merges (ORs) a new path into an existing AND'd    *  clause. */
DECL|method|merge
specifier|private
name|void
name|merge
parameter_list|(
name|String
name|dim
parameter_list|,
name|String
index|[]
name|path
parameter_list|)
block|{
name|int
name|index
init|=
name|drillDownDims
operator|.
name|get
argument_list|(
name|dim
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|.
name|getClauses
argument_list|()
operator|.
name|length
operator|==
name|drillDownDims
operator|.
name|size
argument_list|()
operator|+
literal|1
condition|)
block|{
name|index
operator|++
expr_stmt|;
block|}
name|ConstantScoreQuery
name|q
init|=
operator|(
name|ConstantScoreQuery
operator|)
name|query
operator|.
name|clauses
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|q
operator|.
name|getQuery
argument_list|()
operator|instanceof
name|BooleanQuery
operator|)
operator|==
literal|false
condition|)
block|{
comment|// App called .add(dim, customQuery) and then tried to
comment|// merge a facet label in:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot merge with custom Query"
argument_list|)
throw|;
block|}
name|String
name|indexedField
init|=
name|config
operator|.
name|getDimConfig
argument_list|(
name|dim
argument_list|)
operator|.
name|indexFieldName
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|q
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|(
name|indexedField
argument_list|,
name|dim
argument_list|,
name|path
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
comment|/** Adds one dimension of drill downs; if you pass the same    *  dimension again, it's OR'd with the previous    *  constraints on that dimension, and all dimensions are    *  AND'd against each other and the base query. */
comment|// nocommit can we remove FacetLabel here?
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
if|if
condition|(
name|drillDownDims
operator|.
name|containsKey
argument_list|(
name|dim
argument_list|)
condition|)
block|{
name|merge
argument_list|(
name|dim
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|indexedField
init|=
name|config
operator|.
name|getDimConfig
argument_list|(
name|dim
argument_list|)
operator|.
name|indexFieldName
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// disable coord
comment|// nocommit too anal?
comment|/*     if (path.length == 0) {       throw new IllegalArgumentException("must have at least one facet label under dim");     }     */
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|(
name|indexedField
argument_list|,
name|dim
argument_list|,
name|path
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|dim
argument_list|,
name|bq
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: add a custom drill-down subQuery.  Use this    *  when you have a separate way to drill-down on the    *  dimension than the indexed facet ordinals. */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|dim
parameter_list|,
name|Query
name|subQuery
parameter_list|)
block|{
comment|// TODO: we should use FilteredQuery?
comment|// So scores of the drill-down query don't have an
comment|// effect:
specifier|final
name|ConstantScoreQuery
name|drillDownQuery
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|subQuery
argument_list|)
decl_stmt|;
name|drillDownQuery
operator|.
name|setBoost
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|drillDownQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|drillDownDims
operator|.
name|put
argument_list|(
name|dim
argument_list|,
name|drillDownDims
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SimpleDrillDownQuery
name|clone
parameter_list|()
block|{
return|return
operator|new
name|SimpleDrillDownQuery
argument_list|(
name|config
argument_list|,
name|query
argument_list|,
name|drillDownDims
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
return|return
name|prime
operator|*
name|result
operator|+
name|query
operator|.
name|hashCode
argument_list|()
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
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|SimpleDrillDownQuery
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SimpleDrillDownQuery
name|other
init|=
operator|(
name|SimpleDrillDownQuery
operator|)
name|obj
decl_stmt|;
return|return
name|query
operator|.
name|equals
argument_list|(
name|other
operator|.
name|query
argument_list|)
operator|&&
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|query
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|MatchAllDocsQuery
argument_list|()
return|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|getBooleanQuery
name|BooleanQuery
name|getBooleanQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
DECL|method|getDims
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getDims
parameter_list|()
block|{
return|return
name|drillDownDims
return|;
block|}
block|}
end_class
end_unit
