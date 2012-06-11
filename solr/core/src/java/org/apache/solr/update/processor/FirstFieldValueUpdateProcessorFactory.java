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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Iterator
import|;
end_import
begin_comment
comment|/**  * Keeps only the first value of fields matching the specified   * conditions.  Correct behavior assumes that the SolrInputFields being mutated   * are either single valued, or use an ordered Collection (ie: not a Set).  *<p>  * By default, this processor matches no fields.  *</p>  *   *<p>  * For example, in the configuration below, if a field named   *<code>primary_author</code> contained multiple values (ie:   *<code>"Adam Doe", "Bob Smith", "Carla Jones"</code>) then only the first   * value (ie:<code>"Adam Doe"</code>) will be kept  *</p>  *  *<pre class="prettyprint">  *&lt;processor class="solr.FirstFieldValueUpdateProcessorFactory"&gt;  *&lt;str name="fieldName"&gt;primary_author&lt;/str&gt;  *&lt;/processor&gt;  *</pre>  *  * @see LastFieldValueUpdateProcessorFactory  */
end_comment
begin_class
DECL|class|FirstFieldValueUpdateProcessorFactory
specifier|public
specifier|final
class|class
name|FirstFieldValueUpdateProcessorFactory
extends|extends
name|FieldValueSubsetUpdateProcessorFactory
block|{
annotation|@
name|Override
DECL|method|pickSubset
specifier|public
name|Collection
argument_list|<
name|Object
argument_list|>
name|pickSubset
parameter_list|(
name|Collection
argument_list|<
name|Object
argument_list|>
name|values
parameter_list|)
block|{
comment|// trust the iterator
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|values
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
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
return|return
name|FieldMutatingUpdateProcessor
operator|.
name|SELECT_NO_FIELDS
return|;
block|}
block|}
end_class
end_unit
