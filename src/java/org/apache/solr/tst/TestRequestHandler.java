begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.tst
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|tst
package|;
end_package
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
name|*
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|util
operator|.
name|OpenBitSet
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
name|*
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
name|SolrException
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
name|common
operator|.
name|util
operator|.
name|StrUtils
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
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|SolrRequestHandler
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrQueryResponse
import|;
end_import
begin_comment
comment|/**  * @version $Id$  *   * @deprecated Test against the real request handlers instead.  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|TestRequestHandler
specifier|public
class|class
name|TestRequestHandler
implements|implements
name|SolrRequestHandler
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SolrIndexSearcher
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|SolrCore
operator|.
name|log
operator|.
name|log
argument_list|(
name|Level
operator|.
name|INFO
argument_list|,
literal|"Unused request handler arguments:"
operator|+
name|args
argument_list|)
expr_stmt|;
block|}
comment|// use test instead of assert since asserts may be turned off
DECL|method|test
specifier|public
name|void
name|test
parameter_list|(
name|boolean
name|condition
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|condition
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"test requestHandler: assertion failed!"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|(
name|e
operator|)
throw|;
block|}
block|}
DECL|field|numRequests
specifier|private
name|long
name|numRequests
decl_stmt|;
DECL|field|numErrors
specifier|private
name|long
name|numErrors
decl_stmt|;
DECL|field|splitList
specifier|private
specifier|final
name|Pattern
name|splitList
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|",| "
argument_list|)
decl_stmt|;
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|numRequests
operator|++
expr_stmt|;
comment|// TODO: test if lucene will accept an escaped ';', otherwise
comment|// we need to un-escape them before we pass to QueryParser
try|try
block|{
name|String
name|sreq
init|=
name|req
operator|.
name|getQueryString
argument_list|()
decl_stmt|;
if|if
condition|(
name|sreq
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Missing queryString"
argument_list|)
throw|;
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|sreq
argument_list|,
literal|';'
argument_list|)
decl_stmt|;
name|String
name|qs
init|=
name|commands
operator|.
name|size
argument_list|()
operator|>=
literal|1
condition|?
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
literal|""
decl_stmt|;
name|Query
name|query
init|=
name|QueryParsing
operator|.
name|parseQuery
argument_list|(
name|qs
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
comment|// find fieldnames to return (fieldlist)
name|String
name|fl
init|=
name|req
operator|.
name|getParam
argument_list|(
literal|"fl"
argument_list|)
decl_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|fl
operator|!=
literal|null
condition|)
block|{
comment|// TODO - this could become more efficient if widely used.
comment|// TODO - should field order be maintained?
name|String
index|[]
name|flst
init|=
name|splitList
operator|.
name|split
argument_list|(
name|fl
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|flst
operator|.
name|length
operator|>
literal|0
operator|&&
operator|!
operator|(
name|flst
operator|.
name|length
operator|==
literal|1
operator|&&
name|flst
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fname
range|:
name|flst
control|)
block|{
if|if
condition|(
literal|"score"
operator|.
name|equals
argument_list|(
name|fname
argument_list|)
condition|)
name|flags
operator||=
name|SolrIndexSearcher
operator|.
name|GET_SCORES
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|fname
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|setReturnFields
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If the first non-query, non-filter command is a simple sort on an indexed field, then
comment|// we can use the Lucene sort ability.
name|Sort
name|sort
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|commands
operator|.
name|size
argument_list|()
operator|>=
literal|2
condition|)
block|{
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
name|commands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
comment|/***       Object o = searcher.cacheLookup("dfllNode", query);       if (o == null) {         searcher.cacheInsert("dfllNode",query,"Hello Bob");       } else {         System.out.println("User Cache Hit On " + o);       }       ***/
name|int
name|start
init|=
name|req
operator|.
name|getStart
argument_list|()
decl_stmt|;
name|int
name|limit
init|=
name|req
operator|.
name|getLimit
argument_list|()
decl_stmt|;
name|Query
name|filterQuery
init|=
literal|null
decl_stmt|;
name|DocSet
name|filter
init|=
literal|null
decl_stmt|;
name|Filter
name|lfilter
init|=
literal|null
decl_stmt|;
name|DocList
name|results
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getDocList
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
name|sort
argument_list|,
name|req
operator|.
name|getStart
argument_list|()
argument_list|,
name|req
operator|.
name|getLimit
argument_list|()
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|results
argument_list|)
expr_stmt|;
if|if
condition|(
name|qs
operator|.
name|startsWith
argument_list|(
literal|"values"
argument_list|)
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"testname1"
argument_list|,
literal|"testval1"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"testarr1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"my val 1"
block|,
literal|"my val 2"
block|}
argument_list|)
expr_stmt|;
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"myInt"
argument_list|,
literal|333
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"myNullVal"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"myFloat"
argument_list|,
literal|1.414213562f
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"myDouble"
argument_list|,
literal|1e100d
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"myBool"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"myLong"
argument_list|,
literal|999999999999L
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"55"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"myDoc"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"myResult"
argument_list|,
name|results
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"myStr"
argument_list|,
literal|"&wow! test escaping: a&b<c&"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|null
argument_list|,
literal|"this value had a null name..."
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"myIntArray"
argument_list|,
operator|new
name|Integer
index|[]
block|{
literal|100
block|,
literal|5
block|,
operator|-
literal|10
block|,
literal|42
block|}
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"epoch"
argument_list|,
operator|new
name|Date
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"currDate"
argument_list|,
operator|new
name|Date
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"myNamedList"
argument_list|,
name|nl
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qs
operator|.
name|startsWith
argument_list|(
literal|"fields"
argument_list|)
condition|)
block|{
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|Collection
name|flst
decl_stmt|;
name|flst
operator|=
name|searcher
operator|.
name|getReader
argument_list|()
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"indexed"
argument_list|,
name|flst
argument_list|)
expr_stmt|;
name|flst
operator|=
name|searcher
operator|.
name|getReader
argument_list|()
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|UNINDEXED
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"unindexed"
argument_list|,
name|flst
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"fields"
argument_list|,
name|nl
argument_list|)
expr_stmt|;
block|}
name|test
argument_list|(
name|results
operator|.
name|size
argument_list|()
operator|<=
name|limit
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|results
operator|.
name|size
argument_list|()
operator|<=
name|results
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
comment|// System.out.println("limit="+limit+" results.size()="+results.size()+" matches="+results.matches());
name|test
argument_list|(
operator|(
name|start
operator|==
literal|0
operator|&&
name|limit
operator|>=
name|results
operator|.
name|matches
argument_list|()
operator|)
condition|?
name|results
operator|.
name|size
argument_list|()
operator|==
name|results
operator|.
name|matches
argument_list|()
else|:
literal|true
argument_list|)
expr_stmt|;
comment|//
comment|// test against hits
comment|//
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|lfilter
argument_list|,
name|sort
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|hits
operator|.
name|length
argument_list|()
operator|==
name|results
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
name|DocList
name|rrr2
init|=
name|results
operator|.
name|subset
argument_list|(
name|start
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|rrr2
operator|==
name|results
argument_list|)
expr_stmt|;
name|DocIterator
name|iter
init|=
name|results
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|/***       for (int i=0; i<hits.length(); i++) {         System.out.println("doc="+hits.id(i) + " score="+hits.score(i));       }       ***/
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|results
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|test
argument_list|(
name|iter
operator|.
name|nextDoc
argument_list|()
operator|==
name|hits
operator|.
name|id
argument_list|(
name|i
operator|+
name|results
operator|.
name|offset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Document doesn't implement equals()
comment|// test( searcher.document(i).equals(hits.doc(i)));
block|}
name|DocList
name|results2
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getDocList
argument_list|(
name|query
argument_list|,
name|query
argument_list|,
name|sort
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|results2
operator|.
name|size
argument_list|()
operator|==
name|results
operator|.
name|size
argument_list|()
operator|&&
name|results2
operator|.
name|matches
argument_list|()
operator|==
name|results
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
name|DocList
name|results3
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getDocList
argument_list|(
name|query
argument_list|,
name|query
argument_list|,
literal|null
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|results3
operator|.
name|size
argument_list|()
operator|==
name|results
operator|.
name|size
argument_list|()
operator|&&
name|results3
operator|.
name|matches
argument_list|()
operator|==
name|results
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
comment|//
comment|// getting both the list and set
comment|//
name|DocListAndSet
name|both
init|=
name|searcher
operator|.
name|getDocListAndSet
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|sort
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|both
operator|.
name|docList
operator|.
name|equals
argument_list|(
name|results
argument_list|)
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|both
operator|.
name|docList
operator|.
name|matches
argument_list|()
operator|==
name|both
operator|.
name|docSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|test
argument_list|(
operator|(
name|start
operator|==
literal|0
operator|&&
name|both
operator|.
name|docSet
operator|.
name|size
argument_list|()
operator|<=
name|limit
operator|)
condition|?
name|both
operator|.
name|docSet
operator|.
name|equals
argument_list|(
name|both
operator|.
name|docList
argument_list|)
else|:
literal|true
argument_list|)
expr_stmt|;
comment|// use the result set as a filter itself...
name|DocListAndSet
name|both2
init|=
name|searcher
operator|.
name|getDocListAndSet
argument_list|(
name|query
argument_list|,
name|both
operator|.
name|docSet
argument_list|,
name|sort
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|both2
operator|.
name|docList
operator|.
name|equals
argument_list|(
name|both
operator|.
name|docList
argument_list|)
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|both2
operator|.
name|docSet
operator|.
name|equals
argument_list|(
name|both
operator|.
name|docSet
argument_list|)
argument_list|)
expr_stmt|;
comment|// use the query as a filter itself...
name|DocListAndSet
name|both3
init|=
name|searcher
operator|.
name|getDocListAndSet
argument_list|(
name|query
argument_list|,
name|query
argument_list|,
name|sort
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|both3
operator|.
name|docList
operator|.
name|equals
argument_list|(
name|both
operator|.
name|docList
argument_list|)
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|both3
operator|.
name|docSet
operator|.
name|equals
argument_list|(
name|both
operator|.
name|docSet
argument_list|)
argument_list|)
expr_stmt|;
name|OpenBitSet
name|bits
init|=
name|both
operator|.
name|docSet
operator|.
name|getBits
argument_list|()
decl_stmt|;
name|OpenBitSet
name|neg
init|=
operator|(
operator|(
name|OpenBitSet
operator|)
name|bits
operator|.
name|clone
argument_list|()
operator|)
decl_stmt|;
name|neg
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|bits
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
comment|// use the negative as a filter (should result in 0 matches)
comment|// todo - fix if filter is not null
name|both2
operator|=
name|searcher
operator|.
name|getDocListAndSet
argument_list|(
name|query
argument_list|,
operator|new
name|BitDocSet
argument_list|(
name|neg
argument_list|)
argument_list|,
name|sort
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|both2
operator|.
name|docList
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|both2
operator|.
name|docList
operator|.
name|matches
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|both2
operator|.
name|docSet
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|DocSet
name|allResults
init|=
name|searcher
operator|.
name|getDocSet
argument_list|(
name|query
argument_list|,
name|filter
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|allResults
operator|.
name|equals
argument_list|(
name|both
operator|.
name|docSet
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|DocSet
name|res
init|=
name|searcher
operator|.
name|getDocSet
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|res
operator|.
name|size
argument_list|()
operator|>=
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|res
operator|.
name|intersection
argument_list|(
name|filter
argument_list|)
operator|.
name|equals
argument_list|(
name|both
operator|.
name|docSet
argument_list|)
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|res
operator|.
name|intersectionSize
argument_list|(
name|filter
argument_list|)
operator|==
name|both
operator|.
name|docSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|filterQuery
operator|!=
literal|null
condition|)
block|{
name|test
argument_list|(
name|searcher
operator|.
name|numDocs
argument_list|(
name|filterQuery
argument_list|,
name|res
argument_list|)
operator|==
name|both
operator|.
name|docSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|numErrors
operator|++
expr_stmt|;
return|return;
block|}
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|TestRequestHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|SolrCore
operator|.
name|version
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A test handler that runs some sanity checks on results"
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|QUERYHANDLER
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
name|NamedList
name|lst
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"requests"
argument_list|,
name|numRequests
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
name|numErrors
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
block|}
end_class
end_unit
