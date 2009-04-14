begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.extraction
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|extraction
package|;
end_package
begin_comment
comment|/**  * The various Solr Parameters names to use when extracting content.  *  **/
end_comment
begin_interface
DECL|interface|ExtractingParams
specifier|public
interface|interface
name|ExtractingParams
block|{
DECL|field|EXTRACTING_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|EXTRACTING_PREFIX
init|=
literal|"ext."
decl_stmt|;
comment|/**    * The param prefix for mapping Tika metadata to Solr fields.    *<p/>    * To map a field, add a name like:    *<pre>ext.map.title=solr.title</pre>    *    * In this example, the tika "title" metadata value will be added to a Solr field named "solr.title"    *    *    */
DECL|field|MAP_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|MAP_PREFIX
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"map."
decl_stmt|;
comment|/**    * The boost value for the name of the field.  The boost can be specified by a name mapping.    *<p/>    * For example    *<pre>    * ext.map.title=solr.title    * ext.boost.solr.title=2.5    *</pre>    * will boost the solr.title field for this document by 2.5    *    */
DECL|field|BOOST_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|BOOST_PREFIX
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"boost."
decl_stmt|;
comment|/**    * Pass in literal values to be added to the document, as in    *<pre>    *  ext.literal.myField=Foo     *</pre>    *    */
DECL|field|LITERALS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|LITERALS_PREFIX
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"literal."
decl_stmt|;
comment|/**    * Restrict the extracted parts of a document to be indexed    *  by passing in an XPath expression.  All content that satisfies the XPath expr.    * will be passed to the {@link SolrContentHandler}.    *<p/>    * See Tika's docs for what the extracted document looks like.    *<p/>    * @see #DEFAULT_FIELDNAME    * @see #CAPTURE_FIELDS    */
DECL|field|XPATH_EXPRESSION
specifier|public
specifier|static
specifier|final
name|String
name|XPATH_EXPRESSION
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"xpath"
decl_stmt|;
comment|/**    * Only extract and return the document, do not index it.    */
DECL|field|EXTRACT_ONLY
specifier|public
specifier|static
specifier|final
name|String
name|EXTRACT_ONLY
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"extract.only"
decl_stmt|;
comment|/**     *  Don't throw an exception if a field doesn't exist, just ignore it    */
DECL|field|IGNORE_UNDECLARED_FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|IGNORE_UNDECLARED_FIELDS
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"ignore.und.fl"
decl_stmt|;
comment|/**    * Index attributes separately according to their name, instead of just adding them to the string buffer    */
DECL|field|INDEX_ATTRIBUTES
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_ATTRIBUTES
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"idx.attr"
decl_stmt|;
comment|/**    * The field to index the contents to by default.  If you want to capture a specific piece    * of the Tika document separately, see {@link #CAPTURE_FIELDS}.    *    * @see #CAPTURE_FIELDS    */
DECL|field|DEFAULT_FIELDNAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FIELDNAME
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"def.fl"
decl_stmt|;
comment|/**    * Capture the specified fields (and everything included below it that isn't capture by some other capture field) separately from the default.  This is different    * then the case of passing in an XPath expression.    *<p/>    * The Capture field is based on the localName returned to the {@link SolrContentHandler}    * by Tika, not to be confused by the mapped field.  The field name can then    * be mapped into the index schema.    *<p/>    * For instance, a Tika document may look like:    *<pre>    *&lt;html&gt;    *    ...    *&lt;body&gt;    *&lt;p&gt;some text here.&lt;div&gt;more text&lt;/div&gt;&lt;/p&gt;    *      Some more text    *&lt;/body&gt;    *</pre>    * By passing in the p tag, you could capture all P tags separately from the rest of the text.    * Thus, in the example, the capture of the P tag would be: "some text here.  more text"    *    * @see #DEFAULT_FIELDNAME    */
DECL|field|CAPTURE_FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|CAPTURE_FIELDS
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"capture"
decl_stmt|;
comment|/**    * The type of the stream.  If not specified, Tika will use mime type detection.    */
DECL|field|STREAM_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_TYPE
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"stream.type"
decl_stmt|;
comment|/**    * Optional.  The file name. If specified, Tika can take this into account while    * guessing the MIME type.    */
DECL|field|RESOURCE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_NAME
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"resource.name"
decl_stmt|;
comment|/**    * Optional.  If specified, the prefix will be prepended to all Metadata, such that it would be possible    * to setup a dynamic field to automatically capture it    */
DECL|field|METADATA_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|METADATA_PREFIX
init|=
name|EXTRACTING_PREFIX
operator|+
literal|"metadata.prefix"
decl_stmt|;
block|}
end_interface
end_unit
