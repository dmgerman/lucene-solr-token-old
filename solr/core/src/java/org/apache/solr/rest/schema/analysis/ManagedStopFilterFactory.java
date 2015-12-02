begin_unit
begin_package
DECL|package|org.apache.solr.rest.schema.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
operator|.
name|analysis
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
operator|.
name|StopFilter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|CharArraySet
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
name|common
operator|.
name|SolrException
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|rest
operator|.
name|ManagedResource
import|;
end_import
begin_comment
comment|/**  * TokenFilterFactory that uses the ManagedWordSetResource implementation  * for managing stop words using the REST API.  */
end_comment
begin_class
DECL|class|ManagedStopFilterFactory
specifier|public
class|class
name|ManagedStopFilterFactory
extends|extends
name|BaseManagedTokenFilterFactory
block|{
comment|// this only gets changed once during core initialization and not every
comment|// time an update is made to the underlying managed word set.
DECL|field|stopWords
specifier|private
name|CharArraySet
name|stopWords
init|=
literal|null
decl_stmt|;
comment|/**    * Initialize the managed "handle"    */
DECL|method|ManagedStopFilterFactory
specifier|public
name|ManagedStopFilterFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * This analysis component knows the most logical "path"    * for which to manage stop words from.    */
annotation|@
name|Override
DECL|method|getResourceId
specifier|public
name|String
name|getResourceId
parameter_list|()
block|{
return|return
literal|"/schema/analysis/stopwords/"
operator|+
name|handle
return|;
block|}
comment|/**    * Returns the implementation class for managing stop words.    */
DECL|method|getManagedResourceImplClass
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|ManagedResource
argument_list|>
name|getManagedResourceImplClass
parameter_list|()
block|{
return|return
name|ManagedWordSetResource
operator|.
name|class
return|;
block|}
comment|/**    * Callback invoked by the {@link ManagedResource} instance to trigger this    * class to create the CharArraySet used to create the StopFilter using the    * wordset managed by {@link ManagedWordSetResource}. Keep in mind that    * a schema.xml may reuse the same {@link ManagedStopFilterFactory} many    * times for different field types; behind the scenes all instances of this    * class/handle combination share the same managed data, hence the need for    * a listener/callback scheme.    */
annotation|@
name|Override
DECL|method|onManagedResourceInitialized
specifier|public
name|void
name|onManagedResourceInitialized
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|args
parameter_list|,
name|ManagedResource
name|res
parameter_list|)
throws|throws
name|SolrException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|managedWords
init|=
operator|(
operator|(
name|ManagedWordSetResource
operator|)
name|res
operator|)
operator|.
name|getWordSet
argument_list|()
decl_stmt|;
comment|// first thing is to rebuild the Lucene CharArraySet from our managedWords set
comment|// which is slightly inefficient to do for every instance of the managed filter
comment|// but ManagedResource's don't have access to the luceneMatchVersion
name|boolean
name|ignoreCase
init|=
name|args
operator|.
name|getBooleanArg
argument_list|(
literal|"ignoreCase"
argument_list|)
decl_stmt|;
name|stopWords
operator|=
operator|new
name|CharArraySet
argument_list|(
name|managedWords
operator|.
name|size
argument_list|()
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
name|stopWords
operator|.
name|addAll
argument_list|(
name|managedWords
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a StopFilter based on our managed stop word set.    */
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
if|if
condition|(
name|stopWords
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Managed stopwords not initialized correctly!"
argument_list|)
throw|;
block|}
return|return
operator|new
name|StopFilter
argument_list|(
name|input
argument_list|,
name|stopWords
argument_list|)
return|;
block|}
block|}
end_class
end_unit
