begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
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
name|document
operator|.
name|LazyDocument
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
name|StoredField
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
name|StorableField
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
name|StoredDocument
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
name|Sort
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
name|search
operator|.
name|join
operator|.
name|FixedBitSetCachingWrapperFilter
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
name|ToChildBlockJoinQuery
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
name|UnicodeUtil
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
name|SolrException
operator|.
name|ErrorCode
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
name|response
operator|.
name|ResponseWriterUtil
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
name|IndexSchema
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
name|DocIterator
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
name|DocList
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
name|SyntaxError
import|;
end_import
begin_comment
comment|/**  *  * @since solr 4.9  *  * This transformer returns all descendants of each parent document in a flat list nested inside the parent document.  *  *  * The "parentFilter" parameter is mandatory.  * Optionally you can provide a "childFilter" param to filter out which child documents should be returned and a  * "limit" param which provides an option to specify the number of child documents  * to be returned per parent document. By default it's set to 10.  *  * Examples -  * [child parentFilter="fieldName:fieldValue"]  * [child parentFilter="fieldName:fieldValue" childFilter="fieldName:fieldValue"]  * [child parentFilter="fieldName:fieldValue" childFilter="fieldName:fieldValue" limit=20]  */
end_comment
begin_class
DECL|class|ChildDocTransformerFactory
specifier|public
class|class
name|ChildDocTransformerFactory
extends|extends
name|TransformerFactory
block|{
annotation|@
name|Override
DECL|method|create
specifier|public
name|DocTransformer
name|create
parameter_list|(
name|String
name|field
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|SchemaField
name|uniqueKeyField
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
name|uniqueKeyField
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|" ChildDocTransformer requires the schema to have a uniqueKeyField."
argument_list|)
throw|;
block|}
name|String
name|idField
init|=
name|uniqueKeyField
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|parentFilter
init|=
name|params
operator|.
name|get
argument_list|(
literal|"parentFilter"
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentFilter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Parent filter should be sent as parentFilter=filterCondition"
argument_list|)
throw|;
block|}
name|String
name|childFilter
init|=
name|params
operator|.
name|get
argument_list|(
literal|"childFilter"
argument_list|)
decl_stmt|;
name|int
name|limit
init|=
name|params
operator|.
name|getInt
argument_list|(
literal|"limit"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Filter
name|parentsFilter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Query
name|parentFilterQuery
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|parentFilter
argument_list|,
literal|null
argument_list|,
name|req
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|parentsFilter
operator|=
operator|new
name|FixedBitSetCachingWrapperFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
name|parentFilterQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyntaxError
name|syntaxError
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Failed to create correct parent filter query"
argument_list|)
throw|;
block|}
name|Query
name|childFilterQuery
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|childFilter
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|childFilterQuery
operator|=
name|QParser
operator|.
name|getParser
argument_list|(
name|childFilter
argument_list|,
literal|null
argument_list|,
name|req
argument_list|)
operator|.
name|getQuery
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyntaxError
name|syntaxError
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Failed to create correct child filter query"
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|ChildDocTransformer
argument_list|(
name|field
argument_list|,
name|parentsFilter
argument_list|,
name|idField
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|,
name|childFilterQuery
argument_list|,
name|limit
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|ChildDocTransformer
class|class
name|ChildDocTransformer
extends|extends
name|TransformerWithContext
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|idField
specifier|private
specifier|final
name|String
name|idField
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|parentsFilter
specifier|private
name|Filter
name|parentsFilter
decl_stmt|;
DECL|field|childFilterQuery
specifier|private
name|Query
name|childFilterQuery
decl_stmt|;
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|method|ChildDocTransformer
specifier|public
name|ChildDocTransformer
parameter_list|(
name|String
name|name
parameter_list|,
specifier|final
name|Filter
name|parentsFilter
parameter_list|,
name|String
name|idField
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
specifier|final
name|Query
name|childFilterQuery
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|idField
operator|=
name|idField
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|parentsFilter
operator|=
name|parentsFilter
expr_stmt|;
name|this
operator|.
name|childFilterQuery
operator|=
name|childFilterQuery
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|int
name|docid
parameter_list|)
block|{
name|String
name|parentId
decl_stmt|;
name|Object
name|parentIdField
init|=
name|doc
operator|.
name|get
argument_list|(
name|idField
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentIdField
operator|instanceof
name|StoredField
condition|)
block|{
name|parentId
operator|=
operator|(
operator|(
name|StoredField
operator|)
name|parentIdField
operator|)
operator|.
name|stringValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parentIdField
operator|instanceof
name|Field
condition|)
block|{
name|parentId
operator|=
operator|(
operator|(
name|Field
operator|)
name|parentIdField
operator|)
operator|.
name|stringValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|parentId
operator|==
literal|null
condition|)
block|{
name|parentId
operator|=
operator|(
operator|(
name|Field
operator|)
name|parentIdField
operator|)
operator|.
name|binaryValue
argument_list|()
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|parentId
operator|=
operator|(
name|String
operator|)
name|parentIdField
expr_stmt|;
block|}
try|try
block|{
name|Query
name|parentQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|idField
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|idField
argument_list|)
operator|.
name|readableToIndexed
argument_list|(
name|parentId
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|ToChildBlockJoinQuery
argument_list|(
name|parentQuery
argument_list|,
name|parentsFilter
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DocList
name|children
init|=
name|context
operator|.
name|searcher
operator|.
name|getDocList
argument_list|(
name|query
argument_list|,
name|childFilterQuery
argument_list|,
operator|new
name|Sort
argument_list|()
argument_list|,
literal|0
argument_list|,
name|limit
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|.
name|matches
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DocIterator
name|i
init|=
name|children
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Integer
name|childDocNum
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|StoredDocument
name|childDoc
init|=
name|context
operator|.
name|searcher
operator|.
name|doc
argument_list|(
name|childDocNum
argument_list|)
decl_stmt|;
name|SolrDocument
name|solrChildDoc
init|=
name|ResponseWriterUtil
operator|.
name|toSolrDocument
argument_list|(
name|childDoc
argument_list|,
name|schema
argument_list|)
decl_stmt|;
comment|// TODO: future enhancement...
comment|// support an fl local param in the transformer, which is used to build
comment|// a private ReturnFields instance that we use to prune unwanted field
comment|// names from solrChildDoc
name|doc
operator|.
name|addChildDocument
argument_list|(
name|solrChildDoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|doc
operator|.
name|put
argument_list|(
name|name
argument_list|,
literal|"Could not fetch child Documents"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
