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
name|Query
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
name|SolrDocument
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
name|params
operator|.
name|ModifiableSolrParams
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
name|SolrQueryRequestBase
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
begin_class
DECL|class|CloudMLTQParser
specifier|public
class|class
name|CloudMLTQParser
extends|extends
name|QParser
block|{
DECL|method|CloudMLTQParser
specifier|public
name|CloudMLTQParser
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
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CloudMLTQParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
block|{
name|String
name|id
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
comment|// Do a Real Time Get for the document
name|SolrDocument
name|doc
init|=
name|getDocument
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
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
name|id
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
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
comment|// TODO: Are the mintf and mindf defaults ok at 1/0 ?
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"mintf"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"mindf"
argument_list|,
literal|0
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
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|Object
argument_list|>
argument_list|>
name|filteredDocument
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|qf
operator|!=
literal|null
condition|)
block|{
name|mlt
operator|.
name|setFieldNames
argument_list|(
name|qf
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|qf
control|)
block|{
name|filteredDocument
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
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
name|fields
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|doc
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
comment|// Only use fields that are stored and have an explicit analyzer.
comment|// This makes sense as the query uses tf/idf/.. for query construction.
comment|// We might want to relook and change this in the future though.
if|if
condition|(
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|.
name|stored
argument_list|()
operator|&&
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|isExplicitAnalyzer
argument_list|()
condition|)
block|{
name|fieldNames
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|filteredDocument
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|mlt
operator|.
name|setFieldNames
argument_list|(
name|fieldNames
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fieldNames
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
return|return
name|mlt
operator|.
name|like
argument_list|(
name|filteredDocument
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
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
literal|"Bad Request"
argument_list|)
throw|;
block|}
block|}
DECL|method|getDocument
specifier|private
name|SolrDocument
name|getDocument
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|SolrCore
name|core
init|=
name|req
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|SolrQueryRequestBase
name|request
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
block|{     }
decl_stmt|;
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/get"
argument_list|)
operator|.
name|handleRequest
argument_list|(
name|request
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
name|response
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
return|return
operator|(
name|SolrDocument
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
return|;
block|}
block|}
end_class
end_unit
