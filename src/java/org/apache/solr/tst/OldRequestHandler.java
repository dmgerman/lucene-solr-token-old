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
name|io
operator|.
name|IOException
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
name|search
operator|.
name|DocSlice
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
name|QueryParsing
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
name|response
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
DECL|class|OldRequestHandler
specifier|public
class|class
name|OldRequestHandler
implements|implements
name|SolrRequestHandler
block|{
DECL|field|numRequests
name|long
name|numRequests
decl_stmt|;
DECL|field|numErrors
name|long
name|numErrors
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
name|info
argument_list|(
literal|"Unused request handler arguments:"
operator|+
name|args
argument_list|)
expr_stmt|;
block|}
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
name|Query
name|query
init|=
literal|null
decl_stmt|;
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
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
name|req
operator|.
name|getQueryString
argument_list|()
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
name|query
operator|=
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
expr_stmt|;
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
name|Hits
name|hits
init|=
literal|null
decl_stmt|;
try|try
block|{
name|hits
operator|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|sort
argument_list|)
expr_stmt|;
name|int
name|numHits
init|=
name|hits
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|startRow
init|=
name|Math
operator|.
name|min
argument_list|(
name|numHits
argument_list|,
name|req
operator|.
name|getStart
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|endRow
init|=
name|Math
operator|.
name|min
argument_list|(
name|numHits
argument_list|,
name|req
operator|.
name|getStart
argument_list|()
operator|+
name|req
operator|.
name|getLimit
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numRows
init|=
name|endRow
operator|-
name|startRow
decl_stmt|;
name|int
index|[]
name|ids
init|=
operator|new
name|int
index|[
name|numRows
index|]
decl_stmt|;
name|Document
index|[]
name|data
init|=
operator|new
name|Document
index|[
name|numRows
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startRow
init|;
name|i
operator|<
name|endRow
condition|;
name|i
operator|++
control|)
block|{
name|ids
index|[
name|i
index|]
operator|=
name|hits
operator|.
name|id
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|data
index|[
name|i
index|]
operator|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|null
argument_list|,
operator|new
name|DocSlice
argument_list|(
literal|0
argument_list|,
name|numRows
argument_list|,
name|ids
argument_list|,
literal|null
argument_list|,
name|numHits
argument_list|,
literal|0.0f
argument_list|)
argument_list|)
expr_stmt|;
comment|/***********************       rsp.setResults(new DocSlice(0,numRows,ids,null,numHits));        // Setting the actual document objects is optional       rsp.setResults(data);       ************************/
block|}
catch|catch
parameter_list|(
name|IOException
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
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|OldRequestHandler
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
literal|"The original Hits based request handler"
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
