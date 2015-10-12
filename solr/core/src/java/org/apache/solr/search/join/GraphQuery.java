begin_unit
begin_package
DECL|package|org.apache.solr.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
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
name|ArrayList
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|LeafReaderContext
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
name|queries
operator|.
name|TermsQuery
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
name|AutomatonQuery
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
name|DocIdSet
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
name|search
operator|.
name|Explanation
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
name|IndexSearcher
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
name|search
operator|.
name|TermQuery
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
name|Weight
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
name|WildcardQuery
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
name|lucene
operator|.
name|util
operator|.
name|BytesRefHash
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
name|automaton
operator|.
name|Automaton
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
name|automaton
operator|.
name|DaciukMihovAutomatonBuilder
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|search
operator|.
name|BitDocSet
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
name|search
operator|.
name|DocSet
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
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_comment
comment|/**  * GraphQuery - search for nodes and traverse edges in an index.  *   * Params:  * fromField = the field that contains the node id  * toField = the field that contains the edge ids  * traversalFilter = a query that can be applied for each hop in the graph.  * maxDepth = the max depth to traverse.  (start nodes is depth=1)  * onlyLeafNodes = only return documents that have no edge id values.  * returnRoot = if false, the documents matching the initial query will not be returned.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GraphQuery
specifier|public
class|class
name|GraphQuery
extends|extends
name|Query
block|{
comment|/** The inital node matching query */
DECL|field|q
specifier|private
name|Query
name|q
decl_stmt|;
comment|/** the field with the node id */
DECL|field|fromField
specifier|private
name|String
name|fromField
decl_stmt|;
comment|/** the field containing the edge ids */
DECL|field|toField
specifier|private
name|String
name|toField
decl_stmt|;
comment|/** A query to apply while traversing the graph to filter out edges */
DECL|field|traversalFilter
specifier|private
name|Query
name|traversalFilter
decl_stmt|;
comment|/** The max depth to traverse the graph, -1 means no limit. */
DECL|field|maxDepth
specifier|private
name|int
name|maxDepth
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Use automaton compilation for graph query traversal (experimental + expert use only) */
DECL|field|useAutn
specifier|private
name|boolean
name|useAutn
init|=
literal|true
decl_stmt|;
comment|/** If this is true, the graph traversal result will only return documents that     * do not have a value in the edge field. (Only leaf nodes returned from the graph) */
DECL|field|onlyLeafNodes
specifier|private
name|boolean
name|onlyLeafNodes
init|=
literal|false
decl_stmt|;
comment|/** False if documents matching the start query for the graph will be excluded from the final result set.  */
DECL|field|returnRoot
specifier|private
name|boolean
name|returnRoot
init|=
literal|true
decl_stmt|;
comment|/**    * Create a graph query     * q - the starting node query    * fromField - the field containing the node id    * toField - the field containing the edge ids    */
DECL|method|GraphQuery
specifier|public
name|GraphQuery
parameter_list|(
name|Query
name|q
parameter_list|,
name|String
name|fromField
parameter_list|,
name|String
name|toField
parameter_list|)
block|{
name|this
argument_list|(
name|q
argument_list|,
name|fromField
argument_list|,
name|toField
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a graph query with a traversal filter applied while traversing the frontier.    * q - the starting node query    * fromField - the field containing the node id    * toField - the field containing the edge ids    * traversalFilter - the filter to be applied on each iteration of the frontier.    */
DECL|method|GraphQuery
specifier|public
name|GraphQuery
parameter_list|(
name|Query
name|q
parameter_list|,
name|String
name|fromField
parameter_list|,
name|String
name|toField
parameter_list|,
name|Query
name|traversalFilter
parameter_list|)
block|{
name|this
operator|.
name|q
operator|=
name|q
expr_stmt|;
name|this
operator|.
name|fromField
operator|=
name|fromField
expr_stmt|;
name|this
operator|.
name|toField
operator|=
name|toField
expr_stmt|;
name|this
operator|.
name|traversalFilter
operator|=
name|traversalFilter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
name|Weight
name|graphWeight
init|=
operator|new
name|GraphQueryWeight
argument_list|(
operator|(
name|SolrIndexSearcher
operator|)
name|searcher
argument_list|)
decl_stmt|;
return|return
name|graphWeight
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
literal|"[["
operator|+
name|q
operator|.
name|toString
argument_list|()
operator|+
literal|"],"
operator|+
name|fromField
operator|+
literal|"="
operator|+
name|toField
operator|+
literal|"]"
argument_list|)
expr_stmt|;
if|if
condition|(
name|traversalFilter
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" [TraversalFilter: "
operator|+
name|traversalFilter
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"[maxDepth="
operator|+
name|maxDepth
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"[returnRoot="
operator|+
name|returnRoot
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"[onlyLeafNodes="
operator|+
name|onlyLeafNodes
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"[useAutn="
operator|+
name|useAutn
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|GraphQueryWeight
specifier|protected
class|class
name|GraphQueryWeight
extends|extends
name|Weight
block|{
DECL|field|fromSearcher
name|SolrIndexSearcher
name|fromSearcher
decl_stmt|;
DECL|field|queryNorm
specifier|private
name|float
name|queryNorm
init|=
literal|1.0F
decl_stmt|;
DECL|field|queryWeight
specifier|private
name|float
name|queryWeight
init|=
literal|1.0F
decl_stmt|;
DECL|field|frontierSize
name|int
name|frontierSize
init|=
literal|0
decl_stmt|;
DECL|field|currentDepth
specifier|public
name|int
name|currentDepth
init|=
literal|0
decl_stmt|;
DECL|field|filter
specifier|private
name|Filter
name|filter
decl_stmt|;
DECL|field|resultSet
specifier|private
name|DocSet
name|resultSet
decl_stmt|;
DECL|method|GraphQueryWeight
specifier|public
name|GraphQueryWeight
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
comment|// Grab the searcher so we can run additional searches.
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|fromSearcher
operator|=
name|searcher
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// currently no ranking for graph queries.
specifier|final
name|Scorer
name|cs
init|=
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|exists
init|=
operator|(
name|cs
operator|!=
literal|null
operator|&&
name|cs
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
operator|)
decl_stmt|;
if|if
condition|(
name|exists
condition|)
block|{
name|List
argument_list|<
name|Explanation
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<
name|Explanation
argument_list|>
argument_list|()
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
literal|1.0F
argument_list|,
literal|"Graph Match"
argument_list|,
name|subs
argument_list|)
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|Explanation
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<
name|Explanation
argument_list|>
argument_list|()
decl_stmt|;
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"No Graph Match."
argument_list|,
name|subs
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1F
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|this
operator|.
name|queryWeight
operator|=
name|norm
operator|*
name|topLevelBoost
expr_stmt|;
block|}
comment|/**      * This computes the matching doc set for a given graph query      *       * @return DocSet representing the documents in the graph.      * @throws IOException - if a sub search fails... maybe other cases too! :)      */
DECL|method|getDocSet
specifier|private
name|DocSet
name|getDocSet
parameter_list|()
throws|throws
name|IOException
block|{
name|DocSet
name|fromSet
init|=
literal|null
decl_stmt|;
name|FixedBitSet
name|seedResultBits
init|=
literal|null
decl_stmt|;
comment|// Size that the bit set needs to be.
name|int
name|capacity
init|=
name|fromSearcher
operator|.
name|getRawReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
comment|// The bit set to contain the results that match the query.
name|FixedBitSet
name|resultBits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|capacity
argument_list|)
decl_stmt|;
comment|// The measure of how deep in the graph we have gone.
name|currentDepth
operator|=
literal|0
expr_stmt|;
comment|// the initial query for the frontier for the first query
name|Query
name|frontierQuery
init|=
name|q
decl_stmt|;
comment|// Find all documents in this graph that are leaf nodes to speed traversal
comment|// TODO: speed this up in the future with HAS_FIELD type queries
name|BooleanQuery
operator|.
name|Builder
name|leafNodeQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|WildcardQuery
name|edgeQuery
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|toField
argument_list|,
literal|"*"
argument_list|)
argument_list|)
decl_stmt|;
name|leafNodeQuery
operator|.
name|add
argument_list|(
name|edgeQuery
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|DocSet
name|leafNodes
init|=
name|fromSearcher
operator|.
name|getDocSet
argument_list|(
name|leafNodeQuery
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
comment|// Start the breadth first graph traversal.
do|do
block|{
comment|// Create the graph result collector for this level
name|GraphTermsCollector
name|graphResultCollector
init|=
operator|new
name|GraphTermsCollector
argument_list|(
name|toField
argument_list|,
name|capacity
argument_list|,
name|resultBits
argument_list|,
name|leafNodes
argument_list|)
decl_stmt|;
comment|// traverse the level!
name|fromSearcher
operator|.
name|search
argument_list|(
name|frontierQuery
argument_list|,
name|graphResultCollector
argument_list|)
expr_stmt|;
comment|// All edge ids on the frontier.
name|BytesRefHash
name|collectorTerms
init|=
name|graphResultCollector
operator|.
name|getCollectorTerms
argument_list|()
decl_stmt|;
name|frontierSize
operator|=
name|collectorTerms
operator|.
name|size
argument_list|()
expr_stmt|;
comment|// The resulting doc set from the frontier.
name|fromSet
operator|=
name|graphResultCollector
operator|.
name|getDocSet
argument_list|()
expr_stmt|;
if|if
condition|(
name|seedResultBits
operator|==
literal|null
condition|)
block|{
comment|// grab a copy of the seed bits  (these are the "rootNodes")
name|seedResultBits
operator|=
operator|(
operator|(
name|BitDocSet
operator|)
name|fromSet
operator|)
operator|.
name|getBits
argument_list|()
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
name|Integer
name|fs
init|=
operator|new
name|Integer
argument_list|(
name|frontierSize
argument_list|)
decl_stmt|;
name|FrontierQuery
name|fq
init|=
name|buildFrontierQuery
argument_list|(
name|collectorTerms
argument_list|,
name|fs
argument_list|)
decl_stmt|;
if|if
condition|(
name|fq
operator|==
literal|null
condition|)
block|{
comment|// in case we get null back, make sure we know we're done at this level.
name|fq
operator|=
operator|new
name|FrontierQuery
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|frontierQuery
operator|=
name|fq
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|frontierSize
operator|=
name|fq
operator|.
name|getFrontierSize
argument_list|()
expr_stmt|;
comment|// Add the bits from this level to the result set.
name|resultBits
operator|.
name|or
argument_list|(
operator|(
operator|(
name|BitDocSet
operator|)
name|fromSet
operator|)
operator|.
name|getBits
argument_list|()
argument_list|)
expr_stmt|;
comment|// Increment how far we have gone in the frontier.
name|currentDepth
operator|++
expr_stmt|;
comment|// Break out if we have reached our max depth
if|if
condition|(
name|currentDepth
operator|>=
name|maxDepth
operator|&&
name|maxDepth
operator|!=
operator|-
literal|1
condition|)
block|{
break|break;
block|}
comment|// test if we discovered any new edges, if not , we're done.
block|}
do|while
condition|(
name|frontierSize
operator|>
literal|0
condition|)
do|;
comment|// helper bit set operations on the final result set
if|if
condition|(
operator|!
name|returnRoot
condition|)
block|{
name|resultBits
operator|.
name|andNot
argument_list|(
name|seedResultBits
argument_list|)
expr_stmt|;
block|}
name|BitDocSet
name|resultSet
init|=
operator|new
name|BitDocSet
argument_list|(
name|resultBits
argument_list|)
decl_stmt|;
comment|// If we only want to return leaf nodes do that here.
if|if
condition|(
name|onlyLeafNodes
condition|)
block|{
return|return
name|resultSet
operator|.
name|intersection
argument_list|(
name|leafNodes
argument_list|)
return|;
block|}
else|else
block|{
comment|// create a doc set off the bits that we found.
return|return
name|resultSet
return|;
block|}
block|}
comment|/** Build an automaton to represent the frontier query */
DECL|method|buildAutomaton
specifier|private
name|Automaton
name|buildAutomaton
parameter_list|(
name|BytesRefHash
name|termBytesHash
parameter_list|)
block|{
comment|// need top pass a sorted set of terms to the autn builder (maybe a better way to avoid this?)
specifier|final
name|TreeSet
argument_list|<
name|BytesRef
argument_list|>
name|terms
init|=
operator|new
name|TreeSet
argument_list|<
name|BytesRef
argument_list|>
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
name|termBytesHash
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|termBytesHash
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Automaton
name|a
init|=
name|DaciukMihovAutomatonBuilder
operator|.
name|build
argument_list|(
name|terms
argument_list|)
decl_stmt|;
return|return
name|a
return|;
block|}
comment|/**      * This return a query that represents the documents that match the next hop in the query.      *       * collectorTerms - the terms that represent the edge ids for the current frontier.      * frontierSize - the size of the frontier query (number of unique edges)      *        */
DECL|method|buildFrontierQuery
specifier|public
name|FrontierQuery
name|buildFrontierQuery
parameter_list|(
name|BytesRefHash
name|collectorTerms
parameter_list|,
name|Integer
name|frontierSize
parameter_list|)
block|{
if|if
condition|(
name|collectorTerms
operator|==
literal|null
operator|||
name|collectorTerms
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// return null if there are no terms (edges) to traverse.
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// Create a query
name|Query
name|q
init|=
literal|null
decl_stmt|;
comment|// TODO: see if we should dynamically select this based on the frontier size.
if|if
condition|(
name|useAutn
condition|)
block|{
comment|// build an automaton based query for the frontier.
name|Automaton
name|autn
init|=
name|buildAutomaton
argument_list|(
name|collectorTerms
argument_list|)
decl_stmt|;
name|AutomatonQuery
name|autnQuery
init|=
operator|new
name|AutomatonQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fromField
argument_list|)
argument_list|,
name|autn
argument_list|)
decl_stmt|;
name|q
operator|=
name|autnQuery
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|BytesRef
argument_list|>
name|termList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|collectorTerms
operator|.
name|size
argument_list|()
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
name|collectorTerms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|collectorTerms
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|termList
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
name|q
operator|=
operator|new
name|TermsQuery
argument_list|(
name|fromField
argument_list|,
name|termList
argument_list|)
expr_stmt|;
block|}
comment|// If there is a filter to be used while crawling the graph, add that.
if|if
condition|(
name|traversalFilter
operator|!=
literal|null
condition|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|traversalFilter
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|q
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|// return the new query.
name|FrontierQuery
name|frontier
init|=
operator|new
name|FrontierQuery
argument_list|(
name|q
argument_list|,
name|frontierSize
argument_list|)
decl_stmt|;
return|return
name|frontier
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
name|resultSet
operator|=
name|getDocSet
argument_list|()
expr_stmt|;
name|filter
operator|=
name|resultSet
operator|.
name|getTopFilter
argument_list|()
expr_stmt|;
block|}
name|DocIdSet
name|readerSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
comment|// create a scrorer on the result set, if results from right query are empty, use empty iterator.
return|return
operator|new
name|GraphScorer
argument_list|(
name|this
argument_list|,
name|readerSet
operator|==
literal|null
condition|?
name|DocIdSetIterator
operator|.
name|empty
argument_list|()
else|:
name|readerSet
operator|.
name|iterator
argument_list|()
argument_list|,
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
comment|// NoOp for now , not used.. / supported
block|}
block|}
DECL|class|GraphScorer
specifier|private
class|class
name|GraphScorer
extends|extends
name|Scorer
block|{
DECL|field|iter
specifier|final
name|DocIdSetIterator
name|iter
decl_stmt|;
DECL|field|score
specifier|final
name|float
name|score
decl_stmt|;
comment|// graph query scorer constructor with iterator
DECL|method|GraphScorer
specifier|public
name|GraphScorer
parameter_list|(
name|Weight
name|w
parameter_list|,
name|DocIdSetIterator
name|iter
parameter_list|,
name|float
name|score
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|this
operator|.
name|iter
operator|=
name|iter
operator|==
literal|null
condition|?
name|DocIdSet
operator|.
name|EMPTY
operator|.
name|iterator
argument_list|()
else|:
name|iter
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
comment|// no dynamic scoring now.
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|iter
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
comment|// current position of the doc iterator.
return|return
name|iter
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
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
return|return
name|iter
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
comment|// TODO: potentially very expensive!  what's a good value for this?
return|return
literal|0
return|;
block|}
block|}
comment|/**    * @return The query to be used as a filter for each hop in the graph.    */
DECL|method|getTraversalFilter
specifier|public
name|Query
name|getTraversalFilter
parameter_list|()
block|{
return|return
name|traversalFilter
return|;
block|}
DECL|method|setTraversalFilter
specifier|public
name|void
name|setTraversalFilter
parameter_list|(
name|Query
name|traversalFilter
parameter_list|)
block|{
name|this
operator|.
name|traversalFilter
operator|=
name|traversalFilter
expr_stmt|;
block|}
DECL|method|getQ
specifier|public
name|Query
name|getQ
parameter_list|()
block|{
return|return
name|q
return|;
block|}
DECL|method|setQ
specifier|public
name|void
name|setQ
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|this
operator|.
name|q
operator|=
name|q
expr_stmt|;
block|}
comment|/**    * @return The field that contains the node id    */
DECL|method|getFromField
specifier|public
name|String
name|getFromField
parameter_list|()
block|{
return|return
name|fromField
return|;
block|}
DECL|method|setFromField
specifier|public
name|void
name|setFromField
parameter_list|(
name|String
name|fromField
parameter_list|)
block|{
name|this
operator|.
name|fromField
operator|=
name|fromField
expr_stmt|;
block|}
comment|/**    * @return the field that contains the edge id(s)    */
DECL|method|getToField
specifier|public
name|String
name|getToField
parameter_list|()
block|{
return|return
name|toField
return|;
block|}
DECL|method|setToField
specifier|public
name|void
name|setToField
parameter_list|(
name|String
name|toField
parameter_list|)
block|{
name|this
operator|.
name|toField
operator|=
name|toField
expr_stmt|;
block|}
comment|/**    * @return Max depth for traversal,  -1 for infinite!    */
DECL|method|getMaxDepth
specifier|public
name|int
name|getMaxDepth
parameter_list|()
block|{
return|return
name|maxDepth
return|;
block|}
DECL|method|setMaxDepth
specifier|public
name|void
name|setMaxDepth
parameter_list|(
name|int
name|maxDepth
parameter_list|)
block|{
name|this
operator|.
name|maxDepth
operator|=
name|maxDepth
expr_stmt|;
block|}
comment|/**    * @return If true , an automaton query will be compiled for each new frontier traversal    * this helps to avoid max boolean clause errors.    */
DECL|method|isUseAutn
specifier|public
name|boolean
name|isUseAutn
parameter_list|()
block|{
return|return
name|useAutn
return|;
block|}
DECL|method|setUseAutn
specifier|public
name|void
name|setUseAutn
parameter_list|(
name|boolean
name|useAutn
parameter_list|)
block|{
name|this
operator|.
name|useAutn
operator|=
name|useAutn
expr_stmt|;
block|}
comment|/**    * @return if true only documents that do not have a value in the edge id field will be returned.    */
DECL|method|isOnlyLeafNodes
specifier|public
name|boolean
name|isOnlyLeafNodes
parameter_list|()
block|{
return|return
name|onlyLeafNodes
return|;
block|}
DECL|method|setOnlyLeafNodes
specifier|public
name|void
name|setOnlyLeafNodes
parameter_list|(
name|boolean
name|onlyLeafNodes
parameter_list|)
block|{
name|this
operator|.
name|onlyLeafNodes
operator|=
name|onlyLeafNodes
expr_stmt|;
block|}
comment|/**    * @return if true the documents that matched the rootNodes query will be returned.  o/w they will be removed from the result set.    */
DECL|method|isReturnRoot
specifier|public
name|boolean
name|isReturnRoot
parameter_list|()
block|{
return|return
name|returnRoot
return|;
block|}
DECL|method|setReturnRoot
specifier|public
name|void
name|setReturnRoot
parameter_list|(
name|boolean
name|returnRoot
parameter_list|)
block|{
name|this
operator|.
name|returnRoot
operator|=
name|returnRoot
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
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|fromField
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|fromField
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|maxDepth
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|onlyLeafNodes
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|q
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|q
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|returnRoot
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|toField
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|toField
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|traversalFilter
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|traversalFilter
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|useAutn
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
return|return
name|result
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
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|GraphQuery
name|other
init|=
operator|(
name|GraphQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|fromField
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|fromField
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|fromField
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fromField
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|maxDepth
operator|!=
name|other
operator|.
name|maxDepth
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|onlyLeafNodes
operator|!=
name|other
operator|.
name|onlyLeafNodes
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|q
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|q
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|q
operator|.
name|equals
argument_list|(
name|other
operator|.
name|q
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|returnRoot
operator|!=
name|other
operator|.
name|returnRoot
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|toField
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|toField
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|toField
operator|.
name|equals
argument_list|(
name|other
operator|.
name|toField
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|traversalFilter
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|traversalFilter
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|traversalFilter
operator|.
name|equals
argument_list|(
name|other
operator|.
name|traversalFilter
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|useAutn
operator|!=
name|other
operator|.
name|useAutn
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
