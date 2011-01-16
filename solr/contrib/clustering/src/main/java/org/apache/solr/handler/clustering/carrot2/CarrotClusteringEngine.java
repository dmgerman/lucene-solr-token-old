begin_unit
begin_package
DECL|package|org.apache.solr.handler.clustering.carrot2
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
operator|.
name|carrot2
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
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
name|SolrDocumentList
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
name|HighlightParams
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|handler
operator|.
name|clustering
operator|.
name|SearchClusteringEngine
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
name|HighlightComponent
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
name|highlight
operator|.
name|SolrHighlighter
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
name|LocalSolrQueryRequest
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
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|Cluster
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|Controller
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|ControllerFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|IClusteringAlgorithm
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|attribute
operator|.
name|AttributeNames
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import
begin_comment
comment|/**  * Search results clustering engine based on Carrot2 clustering algorithms.  *<p/>  * Output from this class is subject to change.  *  * @link http://project.carrot2.org  */
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|CarrotClusteringEngine
specifier|public
class|class
name|CarrotClusteringEngine
extends|extends
name|SearchClusteringEngine
block|{
DECL|field|log
specifier|private
specifier|transient
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CarrotClusteringEngine
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Carrot2 controller that manages instances of clustering algorithms    */
DECL|field|controller
specifier|private
name|Controller
name|controller
init|=
name|ControllerFactory
operator|.
name|createPooling
argument_list|()
decl_stmt|;
DECL|field|clusteringAlgorithmClass
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|IClusteringAlgorithm
argument_list|>
name|clusteringAlgorithmClass
decl_stmt|;
DECL|field|idFieldName
specifier|private
name|String
name|idFieldName
decl_stmt|;
annotation|@
name|Deprecated
DECL|method|cluster
specifier|public
name|Object
name|cluster
parameter_list|(
name|Query
name|query
parameter_list|,
name|DocList
name|docList
parameter_list|,
name|SolrQueryRequest
name|sreq
parameter_list|)
block|{
name|SolrIndexSearcher
name|searcher
init|=
name|sreq
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|SolrDocumentList
name|solrDocList
decl_stmt|;
try|try
block|{
name|Map
argument_list|<
name|SolrDocument
argument_list|,
name|Integer
argument_list|>
name|docIds
init|=
operator|new
name|HashMap
argument_list|<
name|SolrDocument
argument_list|,
name|Integer
argument_list|>
argument_list|(
name|docList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|solrDocList
operator|=
name|SolrPluginUtils
operator|.
name|docListToSolrDocumentList
argument_list|(
name|docList
argument_list|,
name|searcher
argument_list|,
name|getFieldsToLoad
argument_list|(
name|sreq
argument_list|)
argument_list|,
name|docIds
argument_list|)
expr_stmt|;
return|return
name|cluster
argument_list|(
name|query
argument_list|,
name|solrDocList
argument_list|,
name|docIds
argument_list|,
name|sreq
argument_list|)
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
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|cluster
specifier|public
name|Object
name|cluster
parameter_list|(
name|Query
name|query
parameter_list|,
name|SolrDocumentList
name|solrDocList
parameter_list|,
name|Map
argument_list|<
name|SolrDocument
argument_list|,
name|Integer
argument_list|>
name|docIds
parameter_list|,
name|SolrQueryRequest
name|sreq
parameter_list|)
block|{
try|try
block|{
comment|// Prepare attributes for Carrot2 clustering call
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Document
argument_list|>
name|documents
init|=
name|getDocuments
argument_list|(
name|solrDocList
argument_list|,
name|docIds
argument_list|,
name|query
argument_list|,
name|sreq
argument_list|)
decl_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|AttributeNames
operator|.
name|DOCUMENTS
argument_list|,
name|documents
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|AttributeNames
operator|.
name|QUERY
argument_list|,
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Pass extra overriding attributes from the request, if any
name|extractCarrotAttributes
argument_list|(
name|sreq
operator|.
name|getParams
argument_list|()
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
comment|// Perform clustering and convert to named list
return|return
name|clustersToNamedList
argument_list|(
name|controller
operator|.
name|process
argument_list|(
name|attributes
argument_list|,
name|clusteringAlgorithmClass
argument_list|)
operator|.
name|getClusters
argument_list|()
argument_list|,
name|sreq
operator|.
name|getParams
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Carrot2 clustering failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Carrot2 clustering failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|String
name|init
parameter_list|(
name|NamedList
name|config
parameter_list|,
specifier|final
name|SolrCore
name|core
parameter_list|)
block|{
name|String
name|result
init|=
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|,
name|core
argument_list|)
decl_stmt|;
name|SolrParams
name|initParams
init|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|config
argument_list|)
decl_stmt|;
comment|// Initialize Carrot2 controller. Pass initialization attributes, if any.
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|initAttributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|extractCarrotAttributes
argument_list|(
name|initParams
argument_list|,
name|initAttributes
argument_list|)
expr_stmt|;
comment|// Customize the language model factory. The implementation we provide here
comment|// is included in the code base of Solr, so that it's possible to refactor
comment|// the Lucene APIs the factory relies on if needed.
name|initAttributes
operator|.
name|put
argument_list|(
literal|"PreprocessingPipeline.languageModelFactory"
argument_list|,
name|LuceneLanguageModelFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|controller
operator|.
name|init
argument_list|(
name|initAttributes
argument_list|)
expr_stmt|;
name|this
operator|.
name|idFieldName
operator|=
name|core
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
comment|// Make sure the requested Carrot2 clustering algorithm class is available
name|String
name|carrotAlgorithmClassName
init|=
name|initParams
operator|.
name|get
argument_list|(
name|CarrotParams
operator|.
name|ALGORITHM
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|algorithmClass
init|=
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|findClass
argument_list|(
name|carrotAlgorithmClassName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|IClusteringAlgorithm
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|algorithmClass
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Class provided as "
operator|+
name|CarrotParams
operator|.
name|ALGORITHM
operator|+
literal|" must implement "
operator|+
name|IClusteringAlgorithm
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|this
operator|.
name|clusteringAlgorithmClass
operator|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|IClusteringAlgorithm
argument_list|>
operator|)
name|algorithmClass
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldsToLoad
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getFieldsToLoad
parameter_list|(
name|SolrQueryRequest
name|sreq
parameter_list|)
block|{
name|SolrParams
name|solrParams
init|=
name|sreq
operator|.
name|getParams
argument_list|()
decl_stmt|;
comment|// Names of fields to deliver content for clustering
name|String
name|urlField
init|=
name|solrParams
operator|.
name|get
argument_list|(
name|CarrotParams
operator|.
name|URL_FIELD_NAME
argument_list|,
literal|"url"
argument_list|)
decl_stmt|;
name|String
name|titleField
init|=
name|solrParams
operator|.
name|get
argument_list|(
name|CarrotParams
operator|.
name|TITLE_FIELD_NAME
argument_list|,
literal|"title"
argument_list|)
decl_stmt|;
name|String
name|snippetField
init|=
name|solrParams
operator|.
name|get
argument_list|(
name|CarrotParams
operator|.
name|SNIPPET_FIELD_NAME
argument_list|,
name|titleField
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|snippetField
argument_list|)
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
name|SERVER_ERROR
argument_list|,
name|CarrotParams
operator|.
name|SNIPPET_FIELD_NAME
operator|+
literal|" must not be blank."
argument_list|)
throw|;
block|}
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|urlField
argument_list|,
name|titleField
argument_list|,
name|snippetField
argument_list|,
name|idFieldName
argument_list|)
return|;
block|}
comment|/**    * Prepares Carrot2 documents for clustering.    */
DECL|method|getDocuments
specifier|private
name|List
argument_list|<
name|Document
argument_list|>
name|getDocuments
parameter_list|(
name|SolrDocumentList
name|solrDocList
parameter_list|,
name|Map
argument_list|<
name|SolrDocument
argument_list|,
name|Integer
argument_list|>
name|docIds
parameter_list|,
name|Query
name|query
parameter_list|,
specifier|final
name|SolrQueryRequest
name|sreq
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrHighlighter
name|highlighter
init|=
literal|null
decl_stmt|;
name|SolrParams
name|solrParams
init|=
name|sreq
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|SolrCore
name|core
init|=
name|sreq
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|String
name|urlField
init|=
name|solrParams
operator|.
name|get
argument_list|(
name|CarrotParams
operator|.
name|URL_FIELD_NAME
argument_list|,
literal|"url"
argument_list|)
decl_stmt|;
name|String
name|titleField
init|=
name|solrParams
operator|.
name|get
argument_list|(
name|CarrotParams
operator|.
name|TITLE_FIELD_NAME
argument_list|,
literal|"title"
argument_list|)
decl_stmt|;
name|String
name|snippetField
init|=
name|solrParams
operator|.
name|get
argument_list|(
name|CarrotParams
operator|.
name|SNIPPET_FIELD_NAME
argument_list|,
name|titleField
argument_list|)
decl_stmt|;
comment|// Get the documents
name|boolean
name|produceSummary
init|=
name|solrParams
operator|.
name|getBool
argument_list|(
name|CarrotParams
operator|.
name|PRODUCE_SUMMARY
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|snippetFieldAry
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|produceSummary
operator|==
literal|true
condition|)
block|{
name|highlighter
operator|=
name|HighlightComponent
operator|.
name|getHighlighter
argument_list|(
name|core
argument_list|)
expr_stmt|;
if|if
condition|(
name|highlighter
operator|!=
literal|null
condition|)
block|{
name|Map
name|args
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|snippetFieldAry
operator|=
operator|new
name|String
index|[]
block|{
name|snippetField
block|}
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|HighlightParams
operator|.
name|FIELDS
argument_list|,
name|snippetFieldAry
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|HighlightParams
operator|.
name|HIGHLIGHT
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|HighlightParams
operator|.
name|SIMPLE_PRE
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|//we don't care about actually highlighting the area
name|args
operator|.
name|put
argument_list|(
name|HighlightParams
operator|.
name|SIMPLE_POST
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|HighlightParams
operator|.
name|FRAGSIZE
argument_list|,
name|solrParams
operator|.
name|getInt
argument_list|(
name|CarrotParams
operator|.
name|SUMMARY_FRAGSIZE
argument_list|,
name|solrParams
operator|.
name|getInt
argument_list|(
name|HighlightParams
operator|.
name|FRAGSIZE
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|query
operator|.
name|toString
argument_list|()
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|args
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SolrIndexSearcher
name|getSearcher
parameter_list|()
block|{
return|return
name|sreq
operator|.
name|getSearcher
argument_list|()
return|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No highlighter configured, cannot produce summary"
argument_list|)
expr_stmt|;
name|produceSummary
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
name|docsIter
init|=
name|solrDocList
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Document
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Document
argument_list|>
argument_list|(
name|solrDocList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|float
index|[]
name|scores
init|=
block|{
literal|1.0f
block|}
decl_stmt|;
name|int
index|[]
name|docsHolder
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|Query
name|theQuery
init|=
name|query
decl_stmt|;
while|while
condition|(
name|docsIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SolrDocument
name|sdoc
init|=
name|docsIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|snippet
init|=
name|getValue
argument_list|(
name|sdoc
argument_list|,
name|snippetField
argument_list|)
decl_stmt|;
comment|// TODO: docIds will be null when running distributed search.
comment|// See comment in ClusteringComponent#finishStage().
if|if
condition|(
name|produceSummary
operator|&&
name|docIds
operator|!=
literal|null
condition|)
block|{
name|docsHolder
index|[
literal|0
index|]
operator|=
name|docIds
operator|.
name|get
argument_list|(
name|sdoc
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|DocList
name|docAsList
init|=
operator|new
name|DocSlice
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|docsHolder
argument_list|,
name|scores
argument_list|,
literal|1
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
name|NamedList
name|highlights
init|=
name|highlighter
operator|.
name|doHighlighting
argument_list|(
name|docAsList
argument_list|,
name|theQuery
argument_list|,
name|req
argument_list|,
name|snippetFieldAry
argument_list|)
decl_stmt|;
if|if
condition|(
name|highlights
operator|!=
literal|null
operator|&&
name|highlights
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|//should only be one value given our setup
comment|//should only be one document with one field
name|NamedList
name|tmp
init|=
operator|(
name|NamedList
operator|)
name|highlights
operator|.
name|getVal
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
index|[]
name|highlt
init|=
operator|(
name|String
index|[]
operator|)
name|tmp
operator|.
name|get
argument_list|(
name|snippetField
argument_list|)
decl_stmt|;
if|if
condition|(
name|highlt
operator|!=
literal|null
operator|&&
name|highlt
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|snippet
operator|=
name|highlt
index|[
literal|0
index|]
expr_stmt|;
block|}
block|}
block|}
name|Document
name|carrotDocument
init|=
operator|new
name|Document
argument_list|(
name|getValue
argument_list|(
name|sdoc
argument_list|,
name|titleField
argument_list|)
argument_list|,
name|snippet
argument_list|,
operator|(
name|String
operator|)
name|sdoc
operator|.
name|getFieldValue
argument_list|(
name|urlField
argument_list|)
argument_list|)
decl_stmt|;
name|carrotDocument
operator|.
name|setField
argument_list|(
literal|"solrId"
argument_list|,
name|sdoc
operator|.
name|getFieldValue
argument_list|(
name|idFieldName
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|carrotDocument
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Deprecated
DECL|method|getValue
specifier|protected
name|String
name|getValue
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
name|doc
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|vals
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|field
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
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// Join multiple values with a period so that Carrot2 does not pick up
comment|// phrases that cross field value boundaries (in most cases it would
comment|// create useless phrases).
name|result
operator|.
name|append
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|" . "
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
DECL|method|getValue
specifier|protected
name|String
name|getValue
parameter_list|(
name|SolrDocument
name|sdoc
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|vals
init|=
name|sdoc
operator|.
name|getFieldValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|==
literal|null
condition|)
return|return
literal|""
return|;
name|Iterator
argument_list|<
name|Object
argument_list|>
name|ite
init|=
name|vals
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|ite
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// Join multiple values with a period so that Carrot2 does not pick up
comment|// phrases that cross field value boundaries (in most cases it would
comment|// create useless phrases).
name|result
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|ite
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" . "
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
DECL|method|clustersToNamedList
specifier|private
name|List
name|clustersToNamedList
parameter_list|(
name|List
argument_list|<
name|Cluster
argument_list|>
name|carrotClusters
parameter_list|,
name|SolrParams
name|solrParams
parameter_list|)
block|{
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|clustersToNamedList
argument_list|(
name|carrotClusters
argument_list|,
name|result
argument_list|,
name|solrParams
operator|.
name|getBool
argument_list|(
name|CarrotParams
operator|.
name|OUTPUT_SUB_CLUSTERS
argument_list|,
literal|true
argument_list|)
argument_list|,
name|solrParams
operator|.
name|getInt
argument_list|(
name|CarrotParams
operator|.
name|NUM_DESCRIPTIONS
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|clustersToNamedList
specifier|private
name|void
name|clustersToNamedList
parameter_list|(
name|List
argument_list|<
name|Cluster
argument_list|>
name|outputClusters
parameter_list|,
name|List
name|parent
parameter_list|,
name|boolean
name|outputSubClusters
parameter_list|,
name|int
name|maxLabels
parameter_list|)
block|{
for|for
control|(
name|Cluster
name|outCluster
range|:
name|outputClusters
control|)
block|{
name|NamedList
name|cluster
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|parent
operator|.
name|add
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|labels
init|=
name|outCluster
operator|.
name|getPhrases
argument_list|()
decl_stmt|;
if|if
condition|(
name|labels
operator|.
name|size
argument_list|()
operator|>
name|maxLabels
condition|)
name|labels
operator|=
name|labels
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|maxLabels
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|add
argument_list|(
literal|"labels"
argument_list|,
name|labels
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Document
argument_list|>
name|docs
init|=
name|outputSubClusters
condition|?
name|outCluster
operator|.
name|getDocuments
argument_list|()
else|:
name|outCluster
operator|.
name|getAllDocuments
argument_list|()
decl_stmt|;
name|List
name|docList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|add
argument_list|(
literal|"docs"
argument_list|,
name|docList
argument_list|)
expr_stmt|;
for|for
control|(
name|Document
name|doc
range|:
name|docs
control|)
block|{
name|docList
operator|.
name|add
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
literal|"solrId"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|outputSubClusters
condition|)
block|{
name|List
name|subclusters
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|add
argument_list|(
literal|"clusters"
argument_list|,
name|subclusters
argument_list|)
expr_stmt|;
name|clustersToNamedList
argument_list|(
name|outCluster
operator|.
name|getSubclusters
argument_list|()
argument_list|,
name|subclusters
argument_list|,
name|outputSubClusters
argument_list|,
name|maxLabels
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Extracts parameters that can possibly match some attributes of Carrot2 algorithms.    */
DECL|method|extractCarrotAttributes
specifier|private
name|void
name|extractCarrotAttributes
parameter_list|(
name|SolrParams
name|solrParams
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
parameter_list|)
block|{
comment|// Extract all non-predefined parameters. This way, we'll be able to set all
comment|// parameters of Carrot2 algorithms without defining their names as constants.
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|paramNames
init|=
name|solrParams
operator|.
name|getParameterNamesIterator
argument_list|()
init|;
name|paramNames
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|paramName
init|=
name|paramNames
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|CarrotParams
operator|.
name|CARROT_PARAM_NAMES
operator|.
name|contains
argument_list|(
name|paramName
argument_list|)
condition|)
block|{
name|attributes
operator|.
name|put
argument_list|(
name|paramName
argument_list|,
name|solrParams
operator|.
name|get
argument_list|(
name|paramName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
