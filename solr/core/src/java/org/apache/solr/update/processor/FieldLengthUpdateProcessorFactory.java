begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import
begin_comment
comment|/**  * Replaces any CharSequence values found in fields matching the specified   * conditions with the lengths of those CharSequences (as an Integer).  *<p>  * By default, this processor matches no fields.  *</p>  *<p>For example, with the configuration listed below any documents   * containing  String values (such as "<code>abcdef</code>" or   * "<code>xyz</code>") in a field declared in the schema using   *<code>TrieIntField</code> or<code>TrieLongField</code>   * would have those Strings replaced with the length of those fields as an   * Integer   * (ie:<code>6</code> and<code>3</code> respectively)  *</p>  *<pre class="prettyprint">  *&lt;processor class="solr.FieldLengthUpdateProcessorFactory"&gt;  *&lt;arr name="typeClass"&gt;  *&lt;str&gt;solr.TrieIntField&lt;/str&gt;  *&lt;str&gt;solr.TrieLongField&lt;/str&gt;  *&lt;/arr&gt;  *&lt;/processor&gt;  *</pre>  */
end_comment
begin_class
DECL|class|FieldLengthUpdateProcessorFactory
specifier|public
specifier|final
class|class
name|FieldLengthUpdateProcessorFactory
extends|extends
name|FieldMutatingUpdateProcessorFactory
block|{
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
comment|// no length specific init args
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
return|return
name|FieldMutatingUpdateProcessor
operator|.
name|SELECT_NO_FIELDS
return|;
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
name|FieldValueMutatingUpdateProcessor
argument_list|(
name|getSelector
argument_list|()
argument_list|,
name|next
argument_list|)
block|{
specifier|protected
name|Object
name|mutateValue
parameter_list|(
specifier|final
name|Object
name|src
parameter_list|)
block|{
if|if
condition|(
name|src
operator|instanceof
name|CharSequence
condition|)
block|{
return|return
operator|new
name|Integer
argument_list|(
operator|(
operator|(
name|CharSequence
operator|)
name|src
operator|)
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
return|return
name|src
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
