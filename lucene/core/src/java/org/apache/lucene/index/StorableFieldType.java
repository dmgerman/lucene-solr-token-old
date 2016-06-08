begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|// TODO: Move some properties from IndexableFieldType here, those regarding stored fields.
end_comment
begin_comment
comment|/**   * Describes the properties of a stored field.  * @lucene.experimental   */
end_comment
begin_interface
DECL|interface|StorableFieldType
specifier|public
interface|interface
name|StorableFieldType
block|{
comment|/** DocValues type; if non-null then the field's value    *  will be indexed into docValues */
DECL|method|docValueType
specifier|public
name|DocValues
operator|.
name|Type
name|docValueType
parameter_list|()
function_decl|;
block|}
end_interface
end_unit
