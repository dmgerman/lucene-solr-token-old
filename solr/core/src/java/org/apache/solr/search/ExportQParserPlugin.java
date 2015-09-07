begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|solr
operator|.
name|handler
operator|.
name|component
operator|.
name|MergeStrategy
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
name|SolrRequestInfo
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
name|index
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
name|Map
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
begin_class
DECL|class|ExportQParserPlugin
specifier|public
class|class
name|ExportQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"xport"
decl_stmt|;
DECL|field|logger
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ExportQParserPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|namedList
parameter_list|)
block|{   }
DECL|method|createParser
specifier|public
name|QParser
name|createParser
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
name|request
parameter_list|)
block|{
return|return
operator|new
name|ExportQParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|request
argument_list|)
return|;
block|}
DECL|class|ExportQParser
specifier|public
class|class
name|ExportQParser
extends|extends
name|QParser
block|{
DECL|method|ExportQParser
specifier|public
name|ExportQParser
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
name|request
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
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
try|try
block|{
return|return
operator|new
name|ExportQuery
argument_list|(
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|ExportQuery
specifier|public
class|class
name|ExportQuery
extends|extends
name|RankQuery
block|{
DECL|field|mainQuery
specifier|private
name|Query
name|mainQuery
decl_stmt|;
DECL|field|id
specifier|private
name|Object
name|id
decl_stmt|;
DECL|method|clone
specifier|public
name|RankQuery
name|clone
parameter_list|()
block|{
name|ExportQuery
name|clone
init|=
operator|new
name|ExportQuery
argument_list|()
decl_stmt|;
name|clone
operator|.
name|id
operator|=
name|id
expr_stmt|;
return|return
name|clone
return|;
block|}
DECL|method|wrap
specifier|public
name|RankQuery
name|wrap
parameter_list|(
name|Query
name|mainQuery
parameter_list|)
block|{
name|this
operator|.
name|mainQuery
operator|=
name|mainQuery
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getMergeStrategy
specifier|public
name|MergeStrategy
name|getMergeStrategy
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
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
return|return
name|mainQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|q
init|=
name|mainQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|==
name|mainQuery
condition|)
block|{
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|clone
argument_list|()
operator|.
name|wrap
argument_list|(
name|q
argument_list|)
return|;
block|}
block|}
DECL|method|getTopDocsCollector
specifier|public
name|TopDocsCollector
name|getTopDocsCollector
parameter_list|(
name|int
name|len
parameter_list|,
name|SolrIndexSearcher
operator|.
name|QueryCommand
name|cmd
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|leafCount
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|FixedBitSet
index|[]
name|sets
init|=
operator|new
name|FixedBitSet
index|[
name|leafCount
index|]
decl_stmt|;
return|return
operator|new
name|ExportCollector
argument_list|(
name|sets
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|id
operator|.
name|hashCode
argument_list|()
return|;
block|}
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
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ExportQuery
name|q
init|=
operator|(
name|ExportQuery
operator|)
name|o
decl_stmt|;
return|return
name|id
operator|==
name|q
operator|.
name|id
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
return|;
block|}
DECL|method|ExportQuery
specifier|public
name|ExportQuery
parameter_list|()
block|{      }
DECL|method|ExportQuery
specifier|public
name|ExportQuery
parameter_list|(
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|id
operator|=
operator|new
name|Object
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ExportCollector
specifier|private
class|class
name|ExportCollector
extends|extends
name|TopDocsCollector
block|{
DECL|field|sets
specifier|private
name|FixedBitSet
index|[]
name|sets
decl_stmt|;
DECL|method|ExportCollector
specifier|public
name|ExportCollector
parameter_list|(
name|FixedBitSet
index|[]
name|sets
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|sets
operator|=
name|sets
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|sets
index|[
name|context
operator|.
name|ord
index|]
operator|=
name|set
expr_stmt|;
return|return
operator|new
name|LeafCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
operator|++
name|totalHits
expr_stmt|;
name|set
operator|.
name|set
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|getScoreDocs
specifier|private
name|ScoreDoc
index|[]
name|getScoreDocs
parameter_list|(
name|int
name|howMany
parameter_list|)
block|{
name|ScoreDoc
index|[]
name|docs
init|=
operator|new
name|ScoreDoc
index|[
name|Math
operator|.
name|min
argument_list|(
name|totalHits
argument_list|,
name|howMany
argument_list|)
index|]
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docs
index|[
name|i
index|]
operator|=
operator|new
name|ScoreDoc
argument_list|(
name|i
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|docs
return|;
block|}
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|howMany
parameter_list|)
block|{
assert|assert
operator|(
name|sets
operator|!=
literal|null
operator|)
assert|;
name|SolrRequestInfo
name|info
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|req
operator|=
name|info
operator|.
name|getReq
argument_list|()
operator|)
operator|!=
literal|null
operator|)
condition|)
block|{
name|Map
name|context
init|=
name|req
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"export"
argument_list|,
name|sets
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"totalHits"
argument_list|,
name|totalHits
argument_list|)
expr_stmt|;
block|}
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|getScoreDocs
argument_list|(
name|howMany
argument_list|)
decl_stmt|;
assert|assert
name|scoreDocs
operator|.
name|length
operator|<=
name|totalHits
assert|;
return|return
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
literal|0.0f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
comment|// TODO: is this the case?
block|}
block|}
block|}
end_class
end_unit
