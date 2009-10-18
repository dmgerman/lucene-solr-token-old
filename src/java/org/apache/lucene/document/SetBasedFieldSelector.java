begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Declare what fields to load normally and what fields to load lazily  *  **/
end_comment
begin_class
DECL|class|SetBasedFieldSelector
specifier|public
class|class
name|SetBasedFieldSelector
implements|implements
name|FieldSelector
block|{
DECL|field|fieldsToLoad
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|fieldsToLoad
decl_stmt|;
DECL|field|lazyFieldsToLoad
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|lazyFieldsToLoad
decl_stmt|;
comment|/**    * Pass in the Set of {@link Field} names to load and the Set of {@link Field} names to load lazily.  If both are null, the    * Document will not have any {@link Field} on it.      * @param fieldsToLoad A Set of {@link String} field names to load.  May be empty, but not null    * @param lazyFieldsToLoad A Set of {@link String} field names to load lazily.  May be empty, but not null      */
DECL|method|SetBasedFieldSelector
specifier|public
name|SetBasedFieldSelector
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|fieldsToLoad
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|lazyFieldsToLoad
parameter_list|)
block|{
name|this
operator|.
name|fieldsToLoad
operator|=
name|fieldsToLoad
expr_stmt|;
name|this
operator|.
name|lazyFieldsToLoad
operator|=
name|lazyFieldsToLoad
expr_stmt|;
block|}
comment|/**    * Indicate whether to load the field with the given name or not. If the {@link Field#name()} is not in either of the     * initializing Sets, then {@link org.apache.lucene.document.FieldSelectorResult#NO_LOAD} is returned.  If a Field name    * is in both<code>fieldsToLoad</code> and<code>lazyFieldsToLoad</code>, lazy has precedence.    *     * @param fieldName The {@link Field} name to check    * @return The {@link FieldSelectorResult}    */
DECL|method|accept
specifier|public
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|FieldSelectorResult
name|result
init|=
name|FieldSelectorResult
operator|.
name|NO_LOAD
decl_stmt|;
if|if
condition|(
name|fieldsToLoad
operator|.
name|contains
argument_list|(
name|fieldName
argument_list|)
operator|==
literal|true
condition|)
block|{
name|result
operator|=
name|FieldSelectorResult
operator|.
name|LOAD
expr_stmt|;
block|}
if|if
condition|(
name|lazyFieldsToLoad
operator|.
name|contains
argument_list|(
name|fieldName
argument_list|)
operator|==
literal|true
condition|)
block|{
name|result
operator|=
name|FieldSelectorResult
operator|.
name|LAZY_LOAD
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
