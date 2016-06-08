begin_unit
begin_package
DECL|package|org.apache.lucene.search.regex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|regex
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Defines basic operations needed by {@link RegexQuery} for a regular  * expression implementation.  */
end_comment
begin_interface
DECL|interface|RegexCapabilities
specifier|public
interface|interface
name|RegexCapabilities
block|{
comment|/**    * Called by the constructor of {@link RegexTermEnum} allowing    * implementations to cache a compiled version of the regular    * expression pattern.    *    * @param pattern regular expression pattern    */
DECL|method|compile
name|void
name|compile
parameter_list|(
name|String
name|pattern
parameter_list|)
function_decl|;
comment|/**    *    * @param string    * @return true if string matches the pattern last passed to {@link #compile}.    */
DECL|method|match
name|boolean
name|match
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
comment|/**    * A wise prefix implementation can reduce the term enumeration (and thus increase performance)    * of RegexQuery dramatically!    *    * @return static non-regex prefix of the pattern last passed to {@link #compile}.  May return null.    */
DECL|method|prefix
name|String
name|prefix
parameter_list|()
function_decl|;
block|}
end_interface
end_unit
