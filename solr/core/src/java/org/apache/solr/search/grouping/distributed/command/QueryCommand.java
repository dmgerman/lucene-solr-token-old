begin_unit
begin_package
DECL|package|org.apache.solr.search.grouping.distributed.command
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|command
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
name|QParser
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
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SyntaxError
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
name|grouping
operator|.
name|Command
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
name|grouping
operator|.
name|collector
operator|.
name|FilterCollector
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
name|List
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|QueryCommand
specifier|public
class|class
name|QueryCommand
implements|implements
name|Command
argument_list|<
name|QueryCommandResult
argument_list|>
block|{
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|sort
specifier|private
name|Sort
name|sort
decl_stmt|;
DECL|field|queryString
specifier|private
name|String
name|queryString
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|field|docSet
specifier|private
name|DocSet
name|docSet
decl_stmt|;
DECL|field|docsToCollect
specifier|private
name|Integer
name|docsToCollect
decl_stmt|;
DECL|field|needScores
specifier|private
name|boolean
name|needScores
decl_stmt|;
DECL|method|setSort
specifier|public
name|Builder
name|setSort
parameter_list|(
name|Sort
name|sort
parameter_list|)
block|{
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setQuery
specifier|public
name|Builder
name|setQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the group query from the specified groupQueryString.      * The groupQueryString is parsed into a query.      *      * @param groupQueryString The group query string to parse      * @param request The current request      * @return this      */
DECL|method|setQuery
specifier|public
name|Builder
name|setQuery
parameter_list|(
name|String
name|groupQueryString
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|QParser
name|parser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|groupQueryString
argument_list|,
literal|null
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|this
operator|.
name|queryString
operator|=
name|groupQueryString
expr_stmt|;
return|return
name|setQuery
argument_list|(
name|parser
operator|.
name|getQuery
argument_list|()
argument_list|)
return|;
block|}
DECL|method|setDocSet
specifier|public
name|Builder
name|setDocSet
parameter_list|(
name|DocSet
name|docSet
parameter_list|)
block|{
name|this
operator|.
name|docSet
operator|=
name|docSet
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the docSet based on the created {@link DocSet}      *      * @param searcher The searcher executing the      * @return this      * @throws IOException If I/O related errors occur.      */
DECL|method|setDocSet
specifier|public
name|Builder
name|setDocSet
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|setDocSet
argument_list|(
name|searcher
operator|.
name|getDocSet
argument_list|(
name|query
argument_list|)
argument_list|)
return|;
block|}
DECL|method|setDocsToCollect
specifier|public
name|Builder
name|setDocsToCollect
parameter_list|(
name|int
name|docsToCollect
parameter_list|)
block|{
name|this
operator|.
name|docsToCollect
operator|=
name|docsToCollect
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNeedScores
specifier|public
name|Builder
name|setNeedScores
parameter_list|(
name|boolean
name|needScores
parameter_list|)
block|{
name|this
operator|.
name|needScores
operator|=
name|needScores
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|QueryCommand
name|build
parameter_list|()
block|{
if|if
condition|(
name|sort
operator|==
literal|null
operator|||
name|query
operator|==
literal|null
operator|||
name|docSet
operator|==
literal|null
operator|||
name|docsToCollect
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"All fields must be set"
argument_list|)
throw|;
block|}
return|return
operator|new
name|QueryCommand
argument_list|(
name|sort
argument_list|,
name|query
argument_list|,
name|docsToCollect
argument_list|,
name|needScores
argument_list|,
name|docSet
argument_list|,
name|queryString
argument_list|)
return|;
block|}
block|}
DECL|field|sort
specifier|private
specifier|final
name|Sort
name|sort
decl_stmt|;
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|docSet
specifier|private
specifier|final
name|DocSet
name|docSet
decl_stmt|;
DECL|field|docsToCollect
specifier|private
specifier|final
name|int
name|docsToCollect
decl_stmt|;
DECL|field|needScores
specifier|private
specifier|final
name|boolean
name|needScores
decl_stmt|;
DECL|field|queryString
specifier|private
specifier|final
name|String
name|queryString
decl_stmt|;
DECL|field|collector
specifier|private
name|TopDocsCollector
name|collector
decl_stmt|;
DECL|field|filterCollector
specifier|private
name|FilterCollector
name|filterCollector
decl_stmt|;
DECL|method|QueryCommand
specifier|private
name|QueryCommand
parameter_list|(
name|Sort
name|sort
parameter_list|,
name|Query
name|query
parameter_list|,
name|int
name|docsToCollect
parameter_list|,
name|boolean
name|needScores
parameter_list|,
name|DocSet
name|docSet
parameter_list|,
name|String
name|queryString
parameter_list|)
block|{
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|docsToCollect
operator|=
name|docsToCollect
expr_stmt|;
name|this
operator|.
name|needScores
operator|=
name|needScores
expr_stmt|;
name|this
operator|.
name|docSet
operator|=
name|docSet
expr_stmt|;
name|this
operator|.
name|queryString
operator|=
name|queryString
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|List
argument_list|<
name|Collector
argument_list|>
name|create
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|sort
operator|==
literal|null
operator|||
name|sort
operator|==
name|Sort
operator|.
name|RELEVANCE
condition|)
block|{
name|collector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|docsToCollect
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collector
operator|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|docsToCollect
argument_list|,
literal|true
argument_list|,
name|needScores
argument_list|,
name|needScores
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|filterCollector
operator|=
operator|new
name|FilterCollector
argument_list|(
name|docSet
argument_list|,
name|collector
argument_list|)
expr_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Collector
operator|)
name|filterCollector
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|result
specifier|public
name|QueryCommandResult
name|result
parameter_list|()
block|{
return|return
operator|new
name|QueryCommandResult
argument_list|(
name|collector
operator|.
name|topDocs
argument_list|()
argument_list|,
name|filterCollector
operator|.
name|getMatches
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|queryString
operator|!=
literal|null
condition|?
name|queryString
else|:
name|query
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getGroupSort
specifier|public
name|Sort
name|getGroupSort
parameter_list|()
block|{
return|return
name|sort
return|;
block|}
annotation|@
name|Override
DECL|method|getSortWithinGroup
specifier|public
name|Sort
name|getSortWithinGroup
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
