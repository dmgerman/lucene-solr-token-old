begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
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
begin_class
DECL|class|ExtendedQueryBase
specifier|public
class|class
name|ExtendedQueryBase
extends|extends
name|Query
implements|implements
name|ExtendedQuery
block|{
DECL|field|cost
specifier|private
name|int
name|cost
decl_stmt|;
DECL|field|cache
specifier|private
name|boolean
name|cache
init|=
literal|true
decl_stmt|;
DECL|field|cacheSep
specifier|private
name|boolean
name|cacheSep
decl_stmt|;
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
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
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
name|cache
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
block|{
name|this
operator|.
name|cacheSep
operator|=
name|cacheSep
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCacheSep
specifier|public
name|boolean
name|getCacheSep
parameter_list|()
block|{
return|return
name|cacheSep
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
block|{
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
block|}
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
DECL|method|getOptions
specifier|public
name|String
name|getOptions
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|cache
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"{!cache=false"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" cost="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|cost
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cacheSep
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"{!cache=sep"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
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
name|getOptions
argument_list|()
return|;
block|}
block|}
end_class
end_unit
