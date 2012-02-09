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
comment|/**  * An update processor that keeps only the the maximum value from any selected   * fields where multiple values are found.  Correct behavior assumes that all   * of the values in the SolrInputFields being mutated are mutually comparable;   * If this is not the case, then the full list of all values found will be   * used as is.  *<p>  * By default, this processor matches no fields.  *</p>  *  *<p>  * In the example configuration below, if a document contains multiple integer   * values (ie:<code>64, 128, 1024</code>) in the field   *<code>largestFileSize</code> then only the biggest value   * (ie:<code>1024</code>) will be kept in that field.  *<p>  *  *<pre class="prettyprint">  *&lt;processor class="solr.MaxFieldValueUpdateProcessorFactory"&gt;  *&lt;str name="fieldName"&gt;largestFileSize&lt;/str&gt;  *&lt;/processor&gt;  *</pre>  *  * @see MinFieldValueUpdateProcessorFactory  * @see Collections#max  */
end_comment
begin_class
DECL|class|MaxFieldValueUpdateProcessorFactory
specifier|public
specifier|final
class|class
name|MaxFieldValueUpdateProcessorFactory
extends|extends
name|FieldValueSubsetUpdateProcessorFactory
block|{
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
name|Collection
argument_list|<
name|Object
argument_list|>
name|result
init|=
name|values
decl_stmt|;
try|try
block|{
name|result
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|Collections
operator|.
name|max
argument_list|(
operator|(
name|Collection
operator|)
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
comment|/* NOOP */
block|}
return|return
name|result
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
