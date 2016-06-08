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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Represents a single commit into an index as seen by the  * {@link IndexDeletionPolicy}.  */
end_comment
begin_interface
DECL|interface|IndexCommitPoint
specifier|public
interface|interface
name|IndexCommitPoint
block|{
comment|/**    * Get the segments file (ie,<code>segments_N</code>) of    * this commit point.    */
DECL|method|getSegmentsFileName
specifier|public
name|String
name|getSegmentsFileName
parameter_list|()
function_decl|;
comment|/**    * Notify the writer that this commit point should be    * deleted.  This should only be called by the {@link    * IndexDeletionPolicy} during its {@link    * IndexDeletionPolicy#onInit} or {@link   * IndexDeletionPolicy#onCommit} method.   */
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|()
function_decl|;
block|}
end_interface
end_unit
