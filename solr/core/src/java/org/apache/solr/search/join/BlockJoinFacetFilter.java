begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|DelegatingCollector
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
name|search
operator|.
name|PostFilter
import|;
end_import
begin_class
DECL|class|BlockJoinFacetFilter
class|class
name|BlockJoinFacetFilter
extends|extends
name|Query
implements|implements
name|PostFilter
block|{
DECL|field|COST
specifier|public
specifier|static
specifier|final
name|int
name|COST
init|=
literal|120
decl_stmt|;
DECL|field|blockJoinFacetCollector
specifier|private
name|DelegatingCollector
name|blockJoinFacetCollector
decl_stmt|;
DECL|method|BlockJoinFacetFilter
specifier|public
name|BlockJoinFacetFilter
parameter_list|(
name|DelegatingCollector
name|blockJoinFacetCollector
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockJoinFacetCollector
operator|=
name|blockJoinFacetCollector
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getFilterCollector
specifier|public
name|DelegatingCollector
name|getFilterCollector
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
block|{
return|return
name|blockJoinFacetCollector
return|;
block|}
annotation|@
name|Override
DECL|method|getCache
specifier|public
name|boolean
name|getCache
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setCache
specifier|public
name|void
name|setCache
parameter_list|(
name|boolean
name|cache
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
name|COST
return|;
block|}
annotation|@
name|Override
DECL|method|setCost
specifier|public
name|void
name|setCost
parameter_list|(
name|int
name|cost
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|getCacheSep
specifier|public
name|boolean
name|getCacheSep
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setCacheSep
specifier|public
name|void
name|setCacheSep
parameter_list|(
name|boolean
name|cacheSep
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|BlockJoinFacetFilter
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|blockJoinFacetCollector
argument_list|,
name|other
operator|.
name|blockJoinFacetCollector
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|classHash
argument_list|()
operator|*
literal|31
operator|+
name|blockJoinFacetCollector
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
