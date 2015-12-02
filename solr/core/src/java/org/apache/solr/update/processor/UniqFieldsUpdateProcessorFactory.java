begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|HashSet
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
name|Set
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
name|common
operator|.
name|SolrInputDocument
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
name|solr
operator|.
name|update
operator|.
name|AddUpdateCommand
import|;
end_import
begin_comment
comment|/**  * Removes duplicate values found in fields matching the specified conditions.    * The existing field values are iterated in order, and values are removed when   * they are equal to a value that has already been seen for this field.  *<p>  * By default this processor matches no fields.  *</p>  *   *<p>  * In the example configuration below, if a document initially contains the values   *<code>"Steve","Lucy","Jim",Steve","Alice","Bob","Alice"</code> in a field named   *<code>foo_uniq</code> then using this processor will result in the final list of   * field values being<code>"Steve","Lucy","Jim","Alice","Bob"</code>  *</p>  *<pre class="prettyprint">  *&lt;processor class="solr.UniqFieldsUpdateProcessorFactory"&gt;  *&lt;str name="fieldRegex"&gt;.*_uniq&lt;/str&gt;  *&lt;/processor&gt;  *</pre>   */
end_comment
begin_class
DECL|class|UniqFieldsUpdateProcessorFactory
specifier|public
class|class
name|UniqFieldsUpdateProcessorFactory
extends|extends
name|FieldValueSubsetUpdateProcessorFactory
block|{
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|pickSubset
specifier|public
name|Collection
name|pickSubset
parameter_list|(
name|Collection
name|values
parameter_list|)
block|{
name|Set
argument_list|<
name|Object
argument_list|>
name|uniqs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|values
control|)
block|{
if|if
condition|(
operator|!
name|uniqs
operator|.
name|contains
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|uniqs
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|o
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
