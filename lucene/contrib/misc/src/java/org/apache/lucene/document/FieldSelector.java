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
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Similar to a {@link java.io.FileFilter}, the FieldSelector allows one to make decisions about  * what Fields get loaded on a {@link Document} by {@link FieldSelectorVisitor}  *  **/
end_comment
begin_interface
DECL|interface|FieldSelector
specifier|public
interface|interface
name|FieldSelector
block|{
comment|/**    *     * @param fieldName the field to accept or reject    * @return an instance of {@link FieldSelectorResult}    * if the {@link Field} named<code>fieldName</code> should be loaded.    */
DECL|method|accept
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
block|}
end_interface
end_unit
