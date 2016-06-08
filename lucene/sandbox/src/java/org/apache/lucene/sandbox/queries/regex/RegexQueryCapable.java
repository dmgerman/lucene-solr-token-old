begin_unit
begin_package
DECL|package|org.apache.lucene.sandbox.queries.regex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|queries
operator|.
name|regex
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Defines methods for regular expression supporting Querys to use.  */
end_comment
begin_interface
DECL|interface|RegexQueryCapable
specifier|public
interface|interface
name|RegexQueryCapable
block|{
comment|/**    * Defines which {@link RegexCapabilities} implementation is used by this instance.    * @see #getRegexImplementation()    */
DECL|method|setRegexImplementation
name|void
name|setRegexImplementation
parameter_list|(
name|RegexCapabilities
name|impl
parameter_list|)
function_decl|;
comment|/**    * Returns the implementation used by this instance.    * @see #setRegexImplementation(RegexCapabilities)    */
DECL|method|getRegexImplementation
name|RegexCapabilities
name|getRegexImplementation
parameter_list|()
function_decl|;
block|}
end_interface
end_unit
