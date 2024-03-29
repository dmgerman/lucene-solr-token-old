begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|CoreContainer
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
name|CoreDescriptor
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
name|CoresLocator
import|;
end_import
begin_class
DECL|class|ReadOnlyCoresLocator
specifier|public
specifier|abstract
class|class
name|ReadOnlyCoresLocator
implements|implements
name|CoresLocator
block|{
annotation|@
name|Override
DECL|method|create
specifier|public
name|void
name|create
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|persist
specifier|public
name|void
name|persist
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|rename
specifier|public
name|void
name|rename
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|oldCD
parameter_list|,
name|CoreDescriptor
name|newCD
parameter_list|)
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|swap
specifier|public
name|void
name|swap
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|cd1
parameter_list|,
name|CoreDescriptor
name|cd2
parameter_list|)
block|{
comment|// no-op
block|}
block|}
end_class
end_unit
