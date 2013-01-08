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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|/**  * An {@link IndexDeletionPolicy} which keeps all index commits around, never  * deleting them. This class is a singleton and can be accessed by referencing  * {@link #INSTANCE}.  */
end_comment
begin_class
DECL|class|NoDeletionPolicy
specifier|public
specifier|final
class|class
name|NoDeletionPolicy
implements|implements
name|IndexDeletionPolicy
block|{
comment|/** The single instance of this class. */
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|IndexDeletionPolicy
name|INSTANCE
init|=
operator|new
name|NoDeletionPolicy
argument_list|()
decl_stmt|;
DECL|method|NoDeletionPolicy
specifier|private
name|NoDeletionPolicy
parameter_list|()
block|{
comment|// keep private to avoid instantiation
block|}
annotation|@
name|Override
DECL|method|onCommit
specifier|public
name|void
name|onCommit
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|onInit
specifier|public
name|void
name|onInit
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
block|{}
block|}
end_class
end_unit
