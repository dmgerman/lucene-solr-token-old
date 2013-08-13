begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|CachingWrapperFilter
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
name|QueryWrapperFilter
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
name|join
operator|.
name|ScoreMode
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
name|join
operator|.
name|ToParentBlockJoinQuery
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
name|SolrParams
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
name|search
operator|.
name|SolrCache
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
name|SolrConstantScoreQuery
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
begin_class
DECL|class|BlockJoinParentQParser
class|class
name|BlockJoinParentQParser
extends|extends
name|QParser
block|{
comment|/** implementation detail subject to change */
DECL|field|CACHE_NAME
specifier|public
name|String
name|CACHE_NAME
init|=
literal|"perSegFilter"
decl_stmt|;
DECL|method|getParentFilterLocalParamName
specifier|protected
name|String
name|getParentFilterLocalParamName
parameter_list|()
block|{
return|return
literal|"which"
return|;
block|}
DECL|method|BlockJoinParentQParser
name|BlockJoinParentQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|String
name|filter
init|=
name|localParams
operator|.
name|get
argument_list|(
name|getParentFilterLocalParamName
argument_list|()
argument_list|)
decl_stmt|;
name|QParser
name|parentParser
init|=
name|subQuery
argument_list|(
name|filter
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Query
name|parentQ
init|=
name|parentParser
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|String
name|queryText
init|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|)
decl_stmt|;
comment|// there is no child query, return parent filter from cache
if|if
condition|(
name|queryText
operator|==
literal|null
operator|||
name|queryText
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|SolrConstantScoreQuery
name|wrapped
init|=
operator|new
name|SolrConstantScoreQuery
argument_list|(
name|getFilter
argument_list|(
name|parentQ
argument_list|)
argument_list|)
decl_stmt|;
name|wrapped
operator|.
name|setCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|wrapped
return|;
block|}
name|QParser
name|childrenParser
init|=
name|subQuery
argument_list|(
name|queryText
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Query
name|childrenQuery
init|=
name|childrenParser
operator|.
name|getQuery
argument_list|()
decl_stmt|;
return|return
name|createQuery
argument_list|(
name|parentQ
argument_list|,
name|childrenQuery
argument_list|)
return|;
block|}
DECL|method|createQuery
specifier|protected
name|Query
name|createQuery
parameter_list|(
name|Query
name|parentList
parameter_list|,
name|Query
name|query
parameter_list|)
block|{
return|return
operator|new
name|ToParentBlockJoinQuery
argument_list|(
name|query
argument_list|,
name|getFilter
argument_list|(
name|parentList
argument_list|)
argument_list|,
name|ScoreMode
operator|.
name|None
argument_list|)
return|;
block|}
DECL|method|getFilter
specifier|protected
name|Filter
name|getFilter
parameter_list|(
name|Query
name|parentList
parameter_list|)
block|{
name|SolrCache
name|parentCache
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getCache
argument_list|(
name|CACHE_NAME
argument_list|)
decl_stmt|;
comment|// lazily retrieve from solr cache
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|parentCache
operator|!=
literal|null
condition|)
block|{
name|filter
operator|=
operator|(
name|Filter
operator|)
name|parentCache
operator|.
name|get
argument_list|(
name|parentList
argument_list|)
expr_stmt|;
block|}
name|Filter
name|result
decl_stmt|;
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|createParentFilter
argument_list|(
name|parentList
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentCache
operator|!=
literal|null
condition|)
block|{
name|parentCache
operator|.
name|put
argument_list|(
name|parentList
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
name|filter
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|createParentFilter
specifier|protected
name|Filter
name|createParentFilter
parameter_list|(
name|Query
name|parentQ
parameter_list|)
block|{
return|return
operator|new
name|CachingWrapperFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
name|parentQ
argument_list|)
argument_list|)
block|{     }
return|;
block|}
block|}
end_class
end_unit
