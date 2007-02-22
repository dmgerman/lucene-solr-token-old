begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|io
operator|.
name|StringReader
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
name|HashSet
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
name|LinkedList
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
name|ListIterator
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
name|lucene
operator|.
name|analysis
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
name|highlight
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Collection of Utility and Factory methods for Highlighting.  */
end_comment
begin_class
DECL|class|HighlightingUtils
specifier|public
class|class
name|HighlightingUtils
block|{
DECL|field|SIMPLE
specifier|private
specifier|static
specifier|final
name|String
name|SIMPLE
init|=
literal|"simple"
decl_stmt|;
DECL|field|HIGHLIGHT
specifier|private
specifier|static
specifier|final
name|String
name|HIGHLIGHT
init|=
literal|"hl"
decl_stmt|;
DECL|field|PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"hl."
decl_stmt|;
DECL|field|FIELDS
specifier|private
specifier|static
specifier|final
name|String
name|FIELDS
init|=
name|PREFIX
operator|+
literal|"fl"
decl_stmt|;
DECL|field|SNIPPETS
specifier|private
specifier|static
specifier|final
name|String
name|SNIPPETS
init|=
name|PREFIX
operator|+
literal|"snippets"
decl_stmt|;
DECL|field|FRAGSIZE
specifier|private
specifier|static
specifier|final
name|String
name|FRAGSIZE
init|=
name|PREFIX
operator|+
literal|"fragsize"
decl_stmt|;
DECL|field|FORMATTER
specifier|private
specifier|static
specifier|final
name|String
name|FORMATTER
init|=
name|PREFIX
operator|+
literal|"formatter"
decl_stmt|;
DECL|field|SIMPLE_PRE
specifier|private
specifier|static
specifier|final
name|String
name|SIMPLE_PRE
init|=
name|PREFIX
operator|+
name|SIMPLE
operator|+
literal|".pre"
decl_stmt|;
DECL|field|SIMPLE_POST
specifier|private
specifier|static
specifier|final
name|String
name|SIMPLE_POST
init|=
name|PREFIX
operator|+
name|SIMPLE
operator|+
literal|".post"
decl_stmt|;
DECL|field|FIELD_MATCH
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_MATCH
init|=
name|PREFIX
operator|+
literal|"requireFieldMatch"
decl_stmt|;
DECL|field|DEFAULTS
specifier|private
specifier|static
name|SolrParams
name|DEFAULTS
init|=
literal|null
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SNIPPETS
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|FRAGSIZE
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|FORMATTER
argument_list|,
name|SIMPLE
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SIMPLE_PRE
argument_list|,
literal|"<em>"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SIMPLE_POST
argument_list|,
literal|"</em>"
argument_list|)
expr_stmt|;
name|DEFAULTS
operator|=
operator|new
name|MapSolrParams
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
comment|/** Combine request parameters with highlighting defaults. */
DECL|method|getParams
specifier|private
specifier|static
name|SolrParams
name|getParams
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|DefaultSolrParams
argument_list|(
name|request
operator|.
name|getParams
argument_list|()
argument_list|,
name|DEFAULTS
argument_list|)
return|;
block|}
comment|/**     * Check whether Highlighting is enabled for this request.     * @param request The current SolrQueryRequest     * @return<code>true</code> if highlighting enabled,<code>false</code> if not.     */
DECL|method|isHighlightingEnabled
specifier|public
specifier|static
name|boolean
name|isHighlightingEnabled
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|getParams
argument_list|(
name|request
argument_list|)
operator|.
name|getBool
argument_list|(
name|HIGHLIGHT
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**     * Return a Highlighter appropriate for this field.     * @param query The current Query     * @param fieldName The name of the field     * @param request The current SolrQueryRequest     */
DECL|method|getHighlighter
specifier|public
specifier|static
name|Highlighter
name|getHighlighter
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
name|getFormatter
argument_list|(
name|fieldName
argument_list|,
name|request
argument_list|)
argument_list|,
name|getQueryScorer
argument_list|(
name|query
argument_list|,
name|fieldName
argument_list|,
name|request
argument_list|)
argument_list|)
decl_stmt|;
name|highlighter
operator|.
name|setTextFragmenter
argument_list|(
name|getFragmenter
argument_list|(
name|fieldName
argument_list|,
name|request
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|highlighter
return|;
block|}
comment|/**     * Return a QueryScorer suitable for this Query and field.     * @param query The current query     * @param fieldName The name of the field     * @param request The SolrQueryRequest     */
DECL|method|getQueryScorer
specifier|public
specifier|static
name|QueryScorer
name|getQueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
name|boolean
name|reqFieldMatch
init|=
name|getParams
argument_list|(
name|request
argument_list|)
operator|.
name|getFieldBool
argument_list|(
name|fieldName
argument_list|,
name|FIELD_MATCH
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqFieldMatch
condition|)
block|{
return|return
operator|new
name|QueryScorer
argument_list|(
name|query
argument_list|,
name|request
operator|.
name|getSearcher
argument_list|()
operator|.
name|getReader
argument_list|()
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|QueryScorer
argument_list|(
name|query
argument_list|)
return|;
block|}
block|}
comment|/**     * Return a String array of the fields to be highlighted.     * Falls back to the programatic defaults, or the default search field if the list of fields     * is not specified in either the handler configuration or the request.     * @param query The current Query     * @param request The current SolrQueryRequest     * @param defaultFields Programmatic default highlight fields, used if nothing is specified in the handler config or the request.     */
DECL|method|getHighlightFields
specifier|public
specifier|static
name|String
index|[]
name|getHighlightFields
parameter_list|(
name|Query
name|query
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|String
index|[]
name|defaultFields
parameter_list|)
block|{
name|String
name|fields
index|[]
init|=
name|getParams
argument_list|(
name|request
argument_list|)
operator|.
name|getParams
argument_list|(
name|FIELDS
argument_list|)
decl_stmt|;
comment|// if no fields specified in the request, or the handler, fall back to programmatic default, or default search field.
if|if
condition|(
name|emptyArray
argument_list|(
name|fields
argument_list|)
condition|)
block|{
comment|// use default search field if highlight fieldlist not specified.
if|if
condition|(
name|emptyArray
argument_list|(
name|defaultFields
argument_list|)
condition|)
block|{
name|fields
operator|=
operator|new
name|String
index|[]
block|{
name|request
operator|.
name|getSchema
argument_list|()
operator|.
name|getDefaultSearchFieldName
argument_list|()
block|}
expr_stmt|;
block|}
else|else
block|{
name|fields
operator|=
name|defaultFields
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|1
condition|)
block|{
comment|// if there's a single request/handler value, it may be a space/comma separated list
name|fields
operator|=
name|SolrPluginUtils
operator|.
name|split
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
return|;
block|}
DECL|method|emptyArray
specifier|private
specifier|static
name|boolean
name|emptyArray
parameter_list|(
name|String
index|[]
name|arr
parameter_list|)
block|{
return|return
operator|(
name|arr
operator|==
literal|null
operator|||
name|arr
operator|.
name|length
operator|==
literal|0
operator|||
name|arr
index|[
literal|0
index|]
operator|==
literal|null
operator|||
name|arr
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
return|;
block|}
comment|/**     * Return the max number of snippets for this field. If this has not     * been configured for this field, fall back to the configured default     * or the solr default.     * @param fieldName The name of the field     * @param request The current SolrQueryRequest     */
DECL|method|getMaxSnippets
specifier|public
specifier|static
name|int
name|getMaxSnippets
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|getParams
argument_list|(
name|request
argument_list|)
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|SNIPPETS
argument_list|)
argument_list|)
return|;
block|}
comment|/**     * Return a formatter appropriate for this field. If a formatter     * has not been configured for this field, fall back to the configured     * default or the solr default (SimpleHTMLFormatter).     *      * @param fieldName The name of the field     * @param request The current SolrQueryRequest     * @return An appropriate Formatter.     */
DECL|method|getFormatter
specifier|public
specifier|static
name|Formatter
name|getFormatter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
name|SolrParams
name|p
init|=
name|getParams
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// SimpleHTMLFormatter is the only supported Formatter at the moment
return|return
operator|new
name|SimpleHTMLFormatter
argument_list|(
name|p
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|SIMPLE_PRE
argument_list|)
argument_list|,
name|p
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|SIMPLE_POST
argument_list|)
argument_list|)
return|;
block|}
comment|/**     * Return a fragmenter appropriate for this field. If a fragmenter     * has not been configured for this field, fall back to the configured     * default or the solr default (GapFragmenter).     *      * @param fieldName The name of the field     * @param request The current SolrQueryRequest     * @return An appropriate Fragmenter.     */
DECL|method|getFragmenter
specifier|public
specifier|static
name|Fragmenter
name|getFragmenter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
name|int
name|fragsize
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getParams
argument_list|(
name|request
argument_list|)
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|FRAGSIZE
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
name|fragsize
operator|<=
literal|0
operator|)
condition|?
operator|new
name|NullFragmenter
argument_list|()
else|:
operator|new
name|GapFragmenter
argument_list|(
name|fragsize
argument_list|)
return|;
block|}
comment|/**     * Generates a list of Highlighted query fragments for each item in a list     * of documents, or returns null if highlighting is disabled.     *     * @param docs query results     * @param query the query     * @param req the current request     * @param defaultFields default list of fields to summarize     *     * @return NamedList containing a NamedList for each document, which in     * turns contains sets (field, summary) pairs.     */
DECL|method|doHighlighting
specifier|public
specifier|static
name|NamedList
name|doHighlighting
parameter_list|(
name|DocList
name|docs
parameter_list|,
name|Query
name|query
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|String
index|[]
name|defaultFields
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isHighlightingEnabled
argument_list|(
name|req
argument_list|)
condition|)
return|return
literal|null
return|;
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|NamedList
name|fragments
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|String
index|[]
name|fieldNames
init|=
name|getHighlightFields
argument_list|(
name|query
argument_list|,
name|req
argument_list|,
name|defaultFields
argument_list|)
decl_stmt|;
name|Document
index|[]
name|readDocs
init|=
operator|new
name|Document
index|[
name|docs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
block|{
comment|// pre-fetch documents using the Searcher's doc cache
name|Set
argument_list|<
name|String
argument_list|>
name|fset
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
name|f
range|:
name|fieldNames
control|)
block|{
name|fset
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
comment|// fetch unique key if one exists.
name|SchemaField
name|keyField
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|keyField
condition|)
name|fset
operator|.
name|add
argument_list|(
name|keyField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|readDocs
argument_list|(
name|readDocs
argument_list|,
name|docs
argument_list|,
name|fset
argument_list|)
expr_stmt|;
block|}
comment|// Highlight each document
name|DocIterator
name|iterator
init|=
name|docs
operator|.
name|iterator
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
name|docs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|docId
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|readDocs
index|[
name|i
index|]
decl_stmt|;
name|NamedList
name|docSummaries
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
name|fieldName
operator|=
name|fieldName
operator|.
name|trim
argument_list|()
expr_stmt|;
name|String
index|[]
name|docTexts
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|docTexts
operator|==
literal|null
condition|)
continue|continue;
comment|// get highlighter, and number of fragments for this field
name|Highlighter
name|highlighter
init|=
name|getHighlighter
argument_list|(
name|query
argument_list|,
name|fieldName
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|int
name|numFragments
init|=
name|getMaxSnippets
argument_list|(
name|fieldName
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|String
index|[]
name|summaries
decl_stmt|;
name|TextFragment
index|[]
name|frag
decl_stmt|;
if|if
condition|(
name|docTexts
operator|.
name|length
operator|==
literal|1
condition|)
block|{
comment|// single-valued field
name|TokenStream
name|tstream
decl_stmt|;
try|try
block|{
comment|// attempt term vectors
name|tstream
operator|=
name|TokenSources
operator|.
name|getTokenStream
argument_list|(
name|searcher
operator|.
name|getReader
argument_list|()
argument_list|,
name|docId
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// fall back to analyzer
name|tstream
operator|=
operator|new
name|TokenOrderingFilter
argument_list|(
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
operator|new
name|StringReader
argument_list|(
name|docTexts
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|frag
operator|=
name|highlighter
operator|.
name|getBestTextFragments
argument_list|(
name|tstream
argument_list|,
name|docTexts
index|[
literal|0
index|]
argument_list|,
literal|false
argument_list|,
name|numFragments
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// multi-valued field
name|MultiValueTokenStream
name|tstream
decl_stmt|;
name|tstream
operator|=
operator|new
name|MultiValueTokenStream
argument_list|(
name|fieldName
argument_list|,
name|docTexts
argument_list|,
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|frag
operator|=
name|highlighter
operator|.
name|getBestTextFragments
argument_list|(
name|tstream
argument_list|,
name|tstream
operator|.
name|asSingleValue
argument_list|()
argument_list|,
literal|false
argument_list|,
name|numFragments
argument_list|)
expr_stmt|;
block|}
comment|// convert fragments back into text
comment|// TODO: we can include score and position information in output as snippet attributes
if|if
condition|(
name|frag
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|fragTexts
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|frag
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|frag
index|[
name|j
index|]
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|frag
index|[
name|j
index|]
operator|.
name|getScore
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|fragTexts
operator|.
name|add
argument_list|(
name|frag
index|[
name|j
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|summaries
operator|=
name|fragTexts
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|summaries
operator|.
name|length
operator|>
literal|0
condition|)
name|docSummaries
operator|.
name|add
argument_list|(
name|fieldName
argument_list|,
name|summaries
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|printId
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|printableUniqueKey
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|fragments
operator|.
name|add
argument_list|(
name|printId
operator|==
literal|null
condition|?
literal|null
else|:
name|printId
argument_list|,
name|docSummaries
argument_list|)
expr_stmt|;
block|}
return|return
name|fragments
return|;
block|}
block|}
end_class
begin_comment
comment|/**   * Helper class which creates a single TokenStream out of values from a   * multi-valued field.  */
end_comment
begin_class
DECL|class|MultiValueTokenStream
class|class
name|MultiValueTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|field|values
specifier|private
name|String
index|[]
name|values
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|curIndex
specifier|private
name|int
name|curIndex
decl_stmt|;
comment|// next index into the values array
DECL|field|curOffset
specifier|private
name|int
name|curOffset
decl_stmt|;
comment|// offset into concatenated string
DECL|field|currentStream
specifier|private
name|TokenStream
name|currentStream
decl_stmt|;
comment|// tokenStream currently being iterated
DECL|field|orderTokenOffsets
specifier|private
name|boolean
name|orderTokenOffsets
decl_stmt|;
comment|/** Constructs a TokenStream for consecutively-analyzed field values    *    * @param fieldName name of the field    * @param values array of field data    * @param analyzer analyzer instance    */
DECL|method|MultiValueTokenStream
specifier|public
name|MultiValueTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
index|[]
name|values
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|boolean
name|orderTokenOffsets
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|curIndex
operator|=
operator|-
literal|1
expr_stmt|;
name|curOffset
operator|=
literal|0
expr_stmt|;
name|currentStream
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|orderTokenOffsets
operator|=
name|orderTokenOffsets
expr_stmt|;
block|}
comment|/** Returns the next token in the stream, or null at EOS. */
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|extra
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|currentStream
operator|==
literal|null
condition|)
block|{
name|curIndex
operator|++
expr_stmt|;
if|if
condition|(
name|curIndex
operator|<
name|values
operator|.
name|length
condition|)
block|{
name|currentStream
operator|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
operator|new
name|StringReader
argument_list|(
name|values
index|[
name|curIndex
index|]
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|orderTokenOffsets
condition|)
name|currentStream
operator|=
operator|new
name|TokenOrderingFilter
argument_list|(
name|currentStream
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// add extra space between multiple values
if|if
condition|(
name|curIndex
operator|>
literal|0
condition|)
name|extra
operator|=
name|analyzer
operator|.
name|getPositionIncrementGap
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
name|Token
name|nextToken
init|=
name|currentStream
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextToken
operator|==
literal|null
condition|)
block|{
name|curOffset
operator|+=
name|values
index|[
name|curIndex
index|]
operator|.
name|length
argument_list|()
expr_stmt|;
name|currentStream
operator|=
literal|null
expr_stmt|;
return|return
name|next
argument_list|()
return|;
block|}
comment|// create an modified token which is the offset into the concatenated
comment|// string of all values
name|Token
name|offsetToken
init|=
operator|new
name|Token
argument_list|(
name|nextToken
operator|.
name|termText
argument_list|()
argument_list|,
name|nextToken
operator|.
name|startOffset
argument_list|()
operator|+
name|curOffset
argument_list|,
name|nextToken
operator|.
name|endOffset
argument_list|()
operator|+
name|curOffset
argument_list|)
decl_stmt|;
name|offsetToken
operator|.
name|setPositionIncrement
argument_list|(
name|nextToken
operator|.
name|getPositionIncrement
argument_list|()
operator|+
name|extra
operator|*
literal|10
argument_list|)
expr_stmt|;
return|return
name|offsetToken
return|;
block|}
comment|/**    * Returns all values as a single String into which the Tokens index with    * their offsets.    */
DECL|method|asSingleValue
specifier|public
name|String
name|asSingleValue
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|str
range|:
name|values
control|)
name|sb
operator|.
name|append
argument_list|(
name|str
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
begin_comment
comment|/**  * A simple modification of SimpleFragmenter which additionally creates new  * fragments when an unusually-large position increment is encountered  * (this behaves much better in the presence of multi-valued fields).  */
end_comment
begin_class
DECL|class|GapFragmenter
class|class
name|GapFragmenter
extends|extends
name|SimpleFragmenter
block|{
comment|/**     * When a gap in term positions is observed that is at least this big, treat    * the gap as a fragment delimiter.    */
DECL|field|INCREMENT_THRESHOLD
specifier|public
specifier|static
specifier|final
name|int
name|INCREMENT_THRESHOLD
init|=
literal|50
decl_stmt|;
DECL|field|fragOffsetAccum
specifier|protected
name|int
name|fragOffsetAccum
init|=
literal|0
decl_stmt|;
DECL|method|GapFragmenter
specifier|public
name|GapFragmenter
parameter_list|()
block|{   }
DECL|method|GapFragmenter
specifier|public
name|GapFragmenter
parameter_list|(
name|int
name|fragsize
parameter_list|)
block|{
name|super
argument_list|(
name|fragsize
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.search.highlight.TextFragmenter#start(java.lang.String)    */
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
name|String
name|originalText
parameter_list|)
block|{
name|fragOffsetAccum
operator|=
literal|0
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.search.highlight.TextFragmenter#isNewFragment(org.apache.lucene.analysis.Token)    */
DECL|method|isNewFragment
specifier|public
name|boolean
name|isNewFragment
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
name|boolean
name|isNewFrag
init|=
name|token
operator|.
name|endOffset
argument_list|()
operator|>=
name|fragOffsetAccum
operator|+
name|getFragmentSize
argument_list|()
operator|||
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|>
name|INCREMENT_THRESHOLD
decl_stmt|;
if|if
condition|(
name|isNewFrag
condition|)
block|{
name|fragOffsetAccum
operator|+=
name|token
operator|.
name|endOffset
argument_list|()
operator|-
name|fragOffsetAccum
expr_stmt|;
block|}
return|return
name|isNewFrag
return|;
block|}
block|}
end_class
begin_comment
comment|/** Orders Tokens in a window first by their startOffset ascending.  * endOffset is currently ignored.  * This is meant to work around fickleness in the highlighter only.  It  * can mess up token positions and should not be used for indexing or querying.  */
end_comment
begin_class
DECL|class|TokenOrderingFilter
class|class
name|TokenOrderingFilter
extends|extends
name|TokenFilter
block|{
DECL|field|windowSize
specifier|private
specifier|final
name|int
name|windowSize
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Token
argument_list|>
name|queue
init|=
operator|new
name|LinkedList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|method|TokenOrderingFilter
specifier|protected
name|TokenOrderingFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|int
name|windowSize
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|windowSize
operator|=
name|windowSize
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
operator|!
name|done
operator|&&
name|queue
operator|.
name|size
argument_list|()
operator|<
name|windowSize
condition|)
block|{
name|Token
name|newTok
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|newTok
operator|==
literal|null
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
break|break;
block|}
comment|// reverse iterating for better efficiency since we know the
comment|// list is already sorted, and most token start offsets will be too.
name|ListIterator
argument_list|<
name|Token
argument_list|>
name|iter
init|=
name|queue
operator|.
name|listIterator
argument_list|(
name|queue
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasPrevious
argument_list|()
condition|)
block|{
if|if
condition|(
name|newTok
operator|.
name|startOffset
argument_list|()
operator|>=
name|iter
operator|.
name|previous
argument_list|()
operator|.
name|startOffset
argument_list|()
condition|)
block|{
comment|// insertion will be before what next() would return (what
comment|// we just compared against), so move back one so the insertion
comment|// will be after.
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|iter
operator|.
name|add
argument_list|(
name|newTok
argument_list|)
expr_stmt|;
block|}
return|return
name|queue
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|queue
operator|.
name|removeFirst
argument_list|()
return|;
block|}
block|}
end_class
end_unit
