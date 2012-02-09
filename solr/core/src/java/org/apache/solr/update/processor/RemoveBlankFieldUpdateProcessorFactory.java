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
begin_comment
comment|/**  * Removes any values found which are CharSequence with a length of 0.   * (ie: empty strings)   *<p>  * By default this processor applies itself to all fields.  *</p>  *  *<p>  * For example, with the configuration listed below, blank strings will be   * removed from all fields except those whose name ends with   * "<code>_literal</code>".  *</p>  *  *<pre class="prettyprint">  *&lt;processor class="solr.RemoveBlankFieldUpdateProcessorFactory"&gt;  *&lt;lst name="exclude"&gt;  *&lt;str name="fieldRegex"&gt;.*_literal&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/processor&gt;  *</pre>  *  */
end_comment
begin_class
DECL|class|RemoveBlankFieldUpdateProcessorFactory
specifier|public
specifier|final
class|class
name|RemoveBlankFieldUpdateProcessorFactory
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
comment|// no trim specific init args
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
operator|&&
literal|0
operator|==
operator|(
operator|(
name|CharSequence
operator|)
name|src
operator|)
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|DELETE_VALUE_SINGLETON
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
