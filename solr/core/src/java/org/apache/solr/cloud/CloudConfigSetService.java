begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
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
name|ConfigSetService
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
name|SolrResourceLoader
import|;
end_import
begin_class
DECL|class|CloudConfigSetService
specifier|public
class|class
name|CloudConfigSetService
extends|extends
name|ConfigSetService
block|{
DECL|field|zkController
specifier|private
specifier|final
name|ZkController
name|zkController
decl_stmt|;
DECL|method|CloudConfigSetService
specifier|public
name|CloudConfigSetService
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|ZkController
name|zkController
parameter_list|)
block|{
name|super
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|this
operator|.
name|zkController
operator|=
name|zkController
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createCoreResourceLoader
specifier|public
name|SolrResourceLoader
name|createCoreResourceLoader
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
block|{
comment|// TODO: Shouldn't the collection node be created by the Collections API?
name|zkController
operator|.
name|createCollectionZkNode
argument_list|(
name|cd
operator|.
name|getCloudDescriptor
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|configName
init|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|readConfigName
argument_list|(
name|cd
operator|.
name|getCollectionName
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ZkSolrResourceLoader
argument_list|(
name|cd
operator|.
name|getInstanceDir
argument_list|()
argument_list|,
name|configName
argument_list|,
name|parentLoader
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|cd
operator|.
name|getSubstitutableProperties
argument_list|()
argument_list|,
name|zkController
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|configName
specifier|public
name|String
name|configName
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
block|{
return|return
literal|"collection "
operator|+
name|cd
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
return|;
block|}
block|}
end_class
end_unit
