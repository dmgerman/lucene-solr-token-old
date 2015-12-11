begin_unit
begin_package
DECL|package|org.apache.solr.search.mlt
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|mlt
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
name|mlt
operator|.
name|MoreLikeThis
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
name|BoostQuery
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
name|ScoreDoc
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
name|TopDocs
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
name|BytesRefBuilder
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
name|LegacyNumericUtils
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
name|StringUtils
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
name|schema
operator|.
name|SchemaField
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
name|util
operator|.
name|SolrPluginUtils
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
name|ArrayList
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
name|Map
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
begin_class
DECL|class|SimpleMLTQParser
specifier|public
class|class
name|SimpleMLTQParser
extends|extends
name|QParser
block|{
comment|// Pattern is thread safe -- TODO? share this with general 'fl' param
DECL|field|splitList
specifier|private
specifier|static
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
DECL|method|SimpleMLTQParser
specifier|public
name|SimpleMLTQParser
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
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
block|{
name|String
name|defaultField
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|uniqueValue
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
name|String
index|[]
name|qf
init|=
name|localParams
operator|.
name|getParams
argument_list|(
literal|"qf"
argument_list|)
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|Query
name|docIdQuery
init|=
name|createIdQuery
argument_list|(
name|defaultField
argument_list|,
name|uniqueValue
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|boostFields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
name|docIdQuery
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|td
operator|.
name|totalHits
operator|!=
literal|1
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
literal|"Error completing MLT request. Could not fetch "
operator|+
literal|"document with id ["
operator|+
name|uniqueValue
operator|+
literal|"]"
argument_list|)
throw|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|td
operator|.
name|scoreDocs
decl_stmt|;
name|MoreLikeThis
name|mlt
init|=
operator|new
name|MoreLikeThis
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"mintf"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"mintf"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"mindf"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"mindf"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|.
name|get
argument_list|(
literal|"minwl"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMinWordLen
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"minwl"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|.
name|get
argument_list|(
literal|"maxwl"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMaxWordLen
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"maxwl"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|.
name|get
argument_list|(
literal|"maxqt"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMaxQueryTerms
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"maxqt"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|.
name|get
argument_list|(
literal|"maxntp"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMaxNumTokensParsed
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"maxntp"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|.
name|get
argument_list|(
literal|"maxdf"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|mlt
operator|.
name|setMaxDocFreq
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"maxdf"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|localParams
operator|.
name|get
argument_list|(
literal|"boost"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|mlt
operator|.
name|setBoost
argument_list|(
name|localParams
operator|.
name|getBool
argument_list|(
literal|"boost"
argument_list|)
argument_list|)
expr_stmt|;
name|boostFields
operator|=
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
name|qf
argument_list|)
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|qf
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|fieldName
range|:
name|qf
control|)
block|{
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|String
index|[]
name|strings
init|=
name|splitList
operator|.
name|split
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|string
range|:
name|strings
control|)
block|{
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|string
argument_list|)
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|SchemaField
argument_list|>
name|fieldNames
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|fieldNames
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|indexed
argument_list|()
operator|&&
name|fieldNames
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|stored
argument_list|()
condition|)
if|if
condition|(
name|fieldNames
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|getNumericType
argument_list|()
operator|==
literal|null
condition|)
name|fields
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fields
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
block|{
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
literal|"MoreLikeThis requires at least one similarity field: qf"
argument_list|)
throw|;
block|}
name|mlt
operator|.
name|setFieldNames
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getIndexAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|Query
name|rawMLTQuery
init|=
name|mlt
operator|.
name|like
argument_list|(
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|BooleanQuery
name|boostedMLTQuery
init|=
operator|(
name|BooleanQuery
operator|)
name|rawMLTQuery
decl_stmt|;
if|if
condition|(
name|boostFields
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|newQ
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|newQ
operator|.
name|setDisableCoord
argument_list|(
name|boostedMLTQuery
operator|.
name|isCoordDisabled
argument_list|()
argument_list|)
expr_stmt|;
name|newQ
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|boostedMLTQuery
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|boostedMLTQuery
control|)
block|{
name|Query
name|q
init|=
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|Float
name|b
init|=
name|boostFields
operator|.
name|get
argument_list|(
operator|(
operator|(
name|TermQuery
operator|)
name|q
operator|)
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
block|{
name|q
operator|=
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
name|newQ
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|clause
operator|.
name|getOccur
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boostedMLTQuery
operator|=
name|newQ
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|// exclude current document from results
name|BooleanQuery
operator|.
name|Builder
name|realMLTQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|realMLTQuery
operator|.
name|setDisableCoord
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|realMLTQuery
operator|.
name|add
argument_list|(
name|boostedMLTQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|realMLTQuery
operator|.
name|add
argument_list|(
name|docIdQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
return|return
name|realMLTQuery
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
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
literal|"Error completing MLT request"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|createIdQuery
specifier|private
name|Query
name|createIdQuery
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|String
name|uniqueValue
parameter_list|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|defaultField
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|getNumericType
argument_list|()
operator|!=
literal|null
condition|?
name|createNumericTerm
argument_list|(
name|defaultField
argument_list|,
name|uniqueValue
argument_list|)
else|:
operator|new
name|Term
argument_list|(
name|defaultField
argument_list|,
name|uniqueValue
argument_list|)
argument_list|)
return|;
block|}
DECL|method|createNumericTerm
specifier|private
name|Term
name|createNumericTerm
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|uniqueValue
parameter_list|)
block|{
name|BytesRefBuilder
name|bytesRefBuilder
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|bytesRefBuilder
operator|.
name|grow
argument_list|(
name|LegacyNumericUtils
operator|.
name|BUF_SIZE_INT
argument_list|)
expr_stmt|;
name|LegacyNumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|uniqueValue
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bytesRefBuilder
argument_list|)
expr_stmt|;
return|return
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|bytesRefBuilder
argument_list|)
return|;
block|}
block|}
end_class
end_unit
