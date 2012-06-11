begin_unit
begin_package
DECL|package|org.apache.solr.handler.clustering.carrot2
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
operator|.
name|carrot2
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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|Cluster
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|IClusteringAlgorithm
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|ProcessingComponentBase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|ProcessingException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|attribute
operator|.
name|AttributeNames
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|attribute
operator|.
name|Processing
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Attribute
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Bindable
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Input
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|Output
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import
begin_comment
comment|/**  * A mock Carrot2 clustering algorithm that outputs input documents as clusters.  * Useful only in tests.  */
end_comment
begin_class
annotation|@
name|Bindable
argument_list|(
name|prefix
operator|=
literal|"EchoClusteringAlgorithm"
argument_list|)
DECL|class|EchoClusteringAlgorithm
specifier|public
class|class
name|EchoClusteringAlgorithm
extends|extends
name|ProcessingComponentBase
implements|implements
name|IClusteringAlgorithm
block|{
annotation|@
name|Input
annotation|@
name|Processing
annotation|@
name|Attribute
argument_list|(
name|key
operator|=
name|AttributeNames
operator|.
name|DOCUMENTS
argument_list|)
DECL|field|documents
specifier|private
name|List
argument_list|<
name|Document
argument_list|>
name|documents
decl_stmt|;
annotation|@
name|Output
annotation|@
name|Processing
annotation|@
name|Attribute
argument_list|(
name|key
operator|=
name|AttributeNames
operator|.
name|CLUSTERS
argument_list|)
DECL|field|clusters
specifier|private
name|List
argument_list|<
name|Cluster
argument_list|>
name|clusters
decl_stmt|;
annotation|@
name|Input
annotation|@
name|Processing
annotation|@
name|Attribute
argument_list|(
name|key
operator|=
literal|"custom-fields"
argument_list|)
DECL|field|customFields
specifier|private
name|String
name|customFields
init|=
literal|""
decl_stmt|;
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|ProcessingException
block|{
name|clusters
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|documents
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Document
name|document
range|:
name|documents
control|)
block|{
specifier|final
name|Cluster
name|cluster
init|=
operator|new
name|Cluster
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|addPhrases
argument_list|(
name|document
operator|.
name|getTitle
argument_list|()
argument_list|,
name|document
operator|.
name|getSummary
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|.
name|getLanguage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|addPhrases
argument_list|(
name|document
operator|.
name|getLanguage
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|field
range|:
name|customFields
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|Object
name|value
init|=
name|document
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|addPhrases
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|cluster
operator|.
name|addDocuments
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|clusters
operator|.
name|add
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
