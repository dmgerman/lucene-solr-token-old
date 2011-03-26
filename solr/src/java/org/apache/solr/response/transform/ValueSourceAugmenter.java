begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
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
name|common
operator|.
name|SolrDocument
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
name|QParser
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
name|function
operator|.
name|ValueSource
import|;
end_import
begin_comment
comment|/**  * Add values from a ValueSource (function query etc)  *  * NOT really sure how or if this could work...  *  * @version $Id$  * @since solr 4.0  */
end_comment
begin_class
DECL|class|ValueSourceAugmenter
specifier|public
class|class
name|ValueSourceAugmenter
extends|extends
name|DocTransformer
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|qparser
specifier|public
specifier|final
name|QParser
name|qparser
decl_stmt|;
DECL|field|values
specifier|public
specifier|final
name|ValueSource
name|values
decl_stmt|;
DECL|method|ValueSourceAugmenter
specifier|public
name|ValueSourceAugmenter
parameter_list|(
name|String
name|name
parameter_list|,
name|QParser
name|qparser
parameter_list|,
name|ValueSource
name|values
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|qparser
operator|=
name|qparser
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|setContext
specifier|public
name|void
name|setContext
parameter_list|(
name|TransformContext
name|context
parameter_list|)
block|{
comment|// maybe we do something here?
block|}
annotation|@
name|Override
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|int
name|docid
parameter_list|)
block|{
comment|// TODO, should know what the real type is -- not always string
comment|// how do we get to docvalues?
name|Object
name|v
init|=
literal|"now what..."
decl_stmt|;
comment|//values.g.strVal( docid );
name|doc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
