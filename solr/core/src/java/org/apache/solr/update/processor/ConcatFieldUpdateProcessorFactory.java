begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|FieldType
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
name|schema
operator|.
name|TextField
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
name|StrField
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
name|SolrInputField
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
name|commons
operator|.
name|lang
operator|.
name|StringUtils
import|;
end_import
begin_comment
comment|/**  * Concatenates multiple values for fields matching the specified   * conditions using a configurable<code>delimiter</code> which defaults   * to "<code>,</code>".  *<p>  * By default, this processor concatenates the values for any field name   * which according to the schema is<code>multiValued="false"</code>   * and uses<code>TextField</code> or<code>StrField</code>  *</p>  *   *<p>  * For example, in the configuration below, any "single valued" string and   * text field which is found to contain multiple values<i>except</i> for   * the<code>primary_author</code> field will be concatenated using the   * string "<code>;</code>" as a delimeter.  For the   *<code>primary_author</code> field, the multiple values will be left   * alone for<code>FirstFieldValueUpdateProcessorFactory</code> to deal with.  *</p>  *  *<pre class="prettyprint">  *&lt;updateRequestProcessorChain&gt;  *&lt;processor class="solr.ConcatFieldUpdateProcessorFactory"&gt;  *&lt;str name="delimiter"&gt;;&lt;/str&gt;  *&lt;lst name="exclude"&gt;  *&lt;str name="fieldName"&gt;primary_author&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.FirstFieldValueUpdateProcessorFactory"&gt;  *&lt;str name="fieldName"&gt;primary_author&lt;/str&gt;  *&lt;/processor&gt;  *&lt;/updateRequestProcessorChain&gt;  *</pre>  */
end_comment
begin_class
DECL|class|ConcatFieldUpdateProcessorFactory
specifier|public
specifier|final
class|class
name|ConcatFieldUpdateProcessorFactory
extends|extends
name|FieldMutatingUpdateProcessorFactory
block|{
DECL|field|delimiter
name|String
name|delimiter
init|=
literal|", "
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|Object
name|d
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"delimiter"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|d
condition|)
name|delimiter
operator|=
name|d
operator|.
name|toString
argument_list|()
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
return|return
operator|new
name|FieldMutatingUpdateProcessor
argument_list|(
name|getSelector
argument_list|()
argument_list|,
name|next
argument_list|)
block|{
specifier|protected
name|SolrInputField
name|mutate
parameter_list|(
specifier|final
name|SolrInputField
name|src
parameter_list|)
block|{
if|if
condition|(
name|src
operator|.
name|getValueCount
argument_list|()
operator|<=
literal|1
condition|)
return|return
name|src
return|;
name|SolrInputField
name|result
init|=
operator|new
name|SolrInputField
argument_list|(
name|src
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|StringUtils
operator|.
name|join
argument_list|(
name|src
operator|.
name|getValues
argument_list|()
argument_list|,
name|delimiter
argument_list|)
argument_list|,
name|src
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
DECL|method|getDefaultSelector
name|getDefaultSelector
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|)
block|{
specifier|final
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
return|return
operator|new
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
argument_list|()
block|{
specifier|public
name|boolean
name|shouldMutate
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
comment|// first check type since it should be fastest
name|FieldType
name|type
init|=
name|schema
operator|.
name|getFieldTypeNoEx
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|type
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
operator|(
name|TextField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|type
argument_list|)
operator|||
name|StrField
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|type
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// only ask for SchemaField if we passed the type check.
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
comment|// shouldn't be null since since type wasn't, but just in case
if|if
condition|(
literal|null
operator|==
name|sf
condition|)
return|return
literal|false
return|;
return|return
operator|!
name|sf
operator|.
name|multiValued
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
