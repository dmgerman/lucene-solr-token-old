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
begin_comment
comment|/**  * Ignores&amp; removes fields matching the specified   * conditions from any document being added to the index.  *  *<p>  * By default, this processor ignores any field name which does not   * exist according to the schema    *</p>  *   *<p>  * For example, in the configuration below, any field name which would cause   * an error because it does not exist, or match a dynamicField, in the   * schema.xml would be silently removed from any added documents...  *</p>  *  *<pre class="prettyprint">  *&lt;processor class="solr.IgnoreFieldUpdateProcessorFactory" /&gt;</pre>  *  *<p>  * In this second example, any field name ending in "_raw" found in a   * document being added would be removed...  *</p>  *<pre class="prettyprint">  *&lt;processor class="solr.IgnoreFieldUpdateProcessorFactory"&gt;  *&lt;str name="fieldRegex"&gt;.*_raw&lt;/str&gt;  *&lt;/processor&gt;</pre>  */
end_comment
begin_class
DECL|class|IgnoreFieldUpdateProcessorFactory
specifier|public
specifier|final
class|class
name|IgnoreFieldUpdateProcessorFactory
extends|extends
name|FieldMutatingUpdateProcessorFactory
block|{
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
annotation|@
name|Override
specifier|protected
name|SolrInputField
name|mutate
parameter_list|(
specifier|final
name|SolrInputField
name|src
parameter_list|)
block|{
return|return
literal|null
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
annotation|@
name|Override
specifier|public
name|boolean
name|shouldMutate
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
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
return|return
operator|(
literal|null
operator|==
name|type
operator|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
