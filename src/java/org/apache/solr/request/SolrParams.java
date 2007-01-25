begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package
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
name|util
operator|.
name|StrUtils
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
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
name|Iterator
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
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  SolrParams hold request parameters.  *  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|SolrParams
specifier|public
specifier|abstract
class|class
name|SolrParams
block|{
comment|/** the query type - which query handler should handle the request */
DECL|field|QT
specifier|public
specifier|static
specifier|final
name|String
name|QT
init|=
literal|"qt"
decl_stmt|;
comment|/** the response writer type - the format of the response */
DECL|field|WT
specifier|public
specifier|static
specifier|final
name|String
name|WT
init|=
literal|"wt"
decl_stmt|;
comment|/** query string */
DECL|field|Q
specifier|public
specifier|static
specifier|final
name|String
name|Q
init|=
literal|"q"
decl_stmt|;
comment|/** Lucene query string(s) for filtering the results without affecting scoring */
DECL|field|FQ
specifier|public
specifier|static
specifier|final
name|String
name|FQ
init|=
literal|"fq"
decl_stmt|;
comment|/** zero based offset of matching documents to retrieve */
DECL|field|START
specifier|public
specifier|static
specifier|final
name|String
name|START
init|=
literal|"start"
decl_stmt|;
comment|/** number of documents to return starting at "start" */
DECL|field|ROWS
specifier|public
specifier|static
specifier|final
name|String
name|ROWS
init|=
literal|"rows"
decl_stmt|;
comment|/** stylesheet to apply to XML results */
DECL|field|XSL
specifier|public
specifier|static
specifier|final
name|String
name|XSL
init|=
literal|"xsl"
decl_stmt|;
comment|/** stylesheet to apply to XML results */
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|String
name|VERSION
init|=
literal|"version"
decl_stmt|;
comment|/** query and init param for field list */
DECL|field|FL
specifier|public
specifier|static
specifier|final
name|String
name|FL
init|=
literal|"fl"
decl_stmt|;
comment|/** default query field */
DECL|field|DF
specifier|public
specifier|static
specifier|final
name|String
name|DF
init|=
literal|"df"
decl_stmt|;
comment|/** whether to include debug data */
DECL|field|DEBUG_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|DEBUG_QUERY
init|=
literal|"debugQuery"
decl_stmt|;
comment|/** another query to explain against */
DECL|field|EXPLAIN_OTHER
specifier|public
specifier|static
specifier|final
name|String
name|EXPLAIN_OTHER
init|=
literal|"explainOther"
decl_stmt|;
comment|/** wether to highlight */
DECL|field|HIGHLIGHT
specifier|public
specifier|static
specifier|final
name|String
name|HIGHLIGHT
init|=
literal|"highlight"
decl_stmt|;
comment|/** fields to highlight */
DECL|field|HIGHLIGHT_FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|HIGHLIGHT_FIELDS
init|=
literal|"highlightFields"
decl_stmt|;
comment|/** maximum highlight fragments to return */
DECL|field|MAX_SNIPPETS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_SNIPPETS
init|=
literal|"maxSnippets"
decl_stmt|;
comment|/** override default highlight Formatter class */
DECL|field|HIGHLIGHT_FORMATTER_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|HIGHLIGHT_FORMATTER_CLASS
init|=
literal|"highlightFormatterClass"
decl_stmt|;
comment|/**    * Should facet counts be calculated?    */
DECL|field|FACET
specifier|public
specifier|static
specifier|final
name|String
name|FACET
init|=
literal|"facet"
decl_stmt|;
comment|/**    * Any lucene formated queries the user would like to use for    * Facet Contraint Counts (multi-value)    */
DECL|field|FACET_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|FACET_QUERY
init|=
literal|"facet.query"
decl_stmt|;
comment|/**    * Any field whose terms the user wants to enumerate over for    * Facet Contraint Counts (multi-value)    */
DECL|field|FACET_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FACET_FIELD
init|=
literal|"facet.field"
decl_stmt|;
comment|/**    * The offset into the list of facets.    * Can be overriden on a per field basis.    */
DECL|field|FACET_OFFSET
specifier|public
specifier|static
specifier|final
name|String
name|FACET_OFFSET
init|=
literal|"facet.offset"
decl_stmt|;
comment|/**    * Numeric option indicating the maximum number of facet field counts    * be included in the response for each field - in descending order of count.    * Can be overriden on a per field basis.    */
DECL|field|FACET_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_LIMIT
init|=
literal|"facet.limit"
decl_stmt|;
comment|/**    * Numeric option indicating the minimum number of hits before a facet should    * be included in the response.  Can be overriden on a per field basis.    */
DECL|field|FACET_MINCOUNT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_MINCOUNT
init|=
literal|"facet.mincount"
decl_stmt|;
comment|/**    * Boolean option indicating whether facet field counts of "0" should     * be included in the response.  Can be overriden on a per field basis.    */
DECL|field|FACET_ZEROS
specifier|public
specifier|static
specifier|final
name|String
name|FACET_ZEROS
init|=
literal|"facet.zeros"
decl_stmt|;
comment|/**    * Boolean option indicating whether the response should include a     * facet field count for all records which have no value for the     * facet field. Can be overriden on a per field basis.    */
DECL|field|FACET_MISSING
specifier|public
specifier|static
specifier|final
name|String
name|FACET_MISSING
init|=
literal|"facet.missing"
decl_stmt|;
comment|/**    * Boolean option: true causes facets to be sorted    * by the count, false results in natural index order.    */
DECL|field|FACET_SORT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_SORT
init|=
literal|"facet.sort"
decl_stmt|;
comment|/**    * Only return constraints of a facet field with the given prefix.    */
DECL|field|FACET_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|FACET_PREFIX
init|=
literal|"facet.prefix"
decl_stmt|;
comment|/** returns the String value of a param, or null if not set */
DECL|method|get
specifier|public
specifier|abstract
name|String
name|get
parameter_list|(
name|String
name|param
parameter_list|)
function_decl|;
comment|/** returns an array of the String values of a param, or null if none */
DECL|method|getParams
specifier|public
specifier|abstract
name|String
index|[]
name|getParams
parameter_list|(
name|String
name|param
parameter_list|)
function_decl|;
comment|/** returns an Iterator over the parameter names */
DECL|method|getParameterNamesIterator
specifier|public
specifier|abstract
name|Iterator
argument_list|<
name|String
argument_list|>
name|getParameterNamesIterator
parameter_list|()
function_decl|;
comment|/** returns the value of the param, or def if not set */
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|param
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|val
return|;
block|}
DECL|method|fpname
specifier|protected
name|String
name|fpname
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|)
block|{
return|return
literal|"f."
operator|+
name|field
operator|+
literal|'.'
operator|+
name|param
return|;
block|}
comment|/** returns the String value of the field parameter, "f.field.param", or    *  the value for "param" if that is not set.    */
DECL|method|getFieldParam
specifier|public
name|String
name|getFieldParam
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|fpname
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|val
operator|!=
literal|null
condition|?
name|val
else|:
name|get
argument_list|(
name|param
argument_list|)
return|;
block|}
comment|/** Returns the Boolean value of the param, or null if not set */
DECL|method|getBool
specifier|public
name|Boolean
name|getBool
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|parseBool
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the boolean value of the param, or def if not set */
DECL|method|getBool
specifier|public
name|boolean
name|getBool
parameter_list|(
name|String
name|param
parameter_list|,
name|boolean
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|parseBool
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the Boolean value of the field param,        or the value for param, or null if neither is set. */
DECL|method|getFieldBool
specifier|public
name|Boolean
name|getFieldBool
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|parseBool
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the boolean value of the field param,    or the value for param, or def if neither is set. */
DECL|method|getFieldBool
specifier|public
name|boolean
name|getFieldBool
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|,
name|boolean
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|parseBool
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the Integer value of the param, or null if not set */
DECL|method|getInt
specifier|public
name|Integer
name|getInt
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the int value of the param, or def if not set */
DECL|method|getInt
specifier|public
name|int
name|getInt
parameter_list|(
name|String
name|param
parameter_list|,
name|int
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the int value of the field param,   or the value for param, or def if neither is set. */
DECL|method|getFieldInt
specifier|public
name|Integer
name|getFieldInt
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the int value of the field param,    or the value for param, or def if neither is set. */
DECL|method|getFieldInt
specifier|public
name|int
name|getFieldInt
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|,
name|int
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the Float value of the param, or null if not set */
DECL|method|getFloat
specifier|public
name|Float
name|getFloat
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the float value of the param, or def if not set */
DECL|method|getFloat
specifier|public
name|float
name|getFloat
parameter_list|(
name|String
name|param
parameter_list|,
name|float
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** how to transform a String into a boolean... more flexible than    * Boolean.parseBoolean() to enable easier integration with html forms.    */
DECL|method|parseBool
specifier|protected
name|boolean
name|parseBool
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|startsWith
argument_list|(
literal|"true"
argument_list|)
operator|||
name|s
operator|.
name|startsWith
argument_list|(
literal|"on"
argument_list|)
operator|||
name|s
operator|.
name|startsWith
argument_list|(
literal|"yes"
argument_list|)
return|;
block|}
comment|/** Create a Map<String,String> from a NamedList given no keys are repeated */
DECL|method|toMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|toMap
parameter_list|(
name|NamedList
name|params
parameter_list|)
block|{
name|HashMap
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|params
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|params
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|/** Create a Map<String,String[]> from a NamedList */
DECL|method|toMultiMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|toMultiMap
parameter_list|(
name|NamedList
name|params
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
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
name|params
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|params
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|params
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|MultiMapSolrParams
operator|.
name|addParam
argument_list|(
name|name
argument_list|,
name|val
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|/** Create SolrParams from NamedList. */
DECL|method|toSolrParams
specifier|public
specifier|static
name|SolrParams
name|toSolrParams
parameter_list|(
name|NamedList
name|params
parameter_list|)
block|{
comment|// if no keys are repeated use the faster MapSolrParams
name|HashMap
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|prev
init|=
name|map
operator|.
name|put
argument_list|(
name|params
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|params
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|prev
operator|!=
literal|null
condition|)
return|return
operator|new
name|MultiMapSolrParams
argument_list|(
name|toMultiMap
argument_list|(
name|params
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|MapSolrParams
argument_list|(
name|map
argument_list|)
return|;
block|}
comment|/** Convert this to a NamedList */
DECL|method|toNamedList
specifier|public
name|NamedList
name|toNamedList
parameter_list|()
block|{
specifier|final
name|NamedList
name|result
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|getParameterNamesIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|String
name|name
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
name|getParams
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// currently no reason not to use the same array
name|result
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
