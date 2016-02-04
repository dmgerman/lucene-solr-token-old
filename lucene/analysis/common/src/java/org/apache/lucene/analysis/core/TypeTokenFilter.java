begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package
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
name|tokenattributes
operator|.
name|TypeAttribute
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
name|FilteringTokenFilter
import|;
end_import
begin_comment
comment|/**  * Removes tokens whose types appear in a set of blocked types from a token stream.  */
end_comment
begin_class
DECL|class|TypeTokenFilter
specifier|public
specifier|final
class|class
name|TypeTokenFilter
extends|extends
name|FilteringTokenFilter
block|{
DECL|field|stopTypes
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|stopTypes
decl_stmt|;
DECL|field|typeAttribute
specifier|private
specifier|final
name|TypeAttribute
name|typeAttribute
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|useWhiteList
specifier|private
specifier|final
name|boolean
name|useWhiteList
decl_stmt|;
comment|/**    * Create a new {@link TypeTokenFilter}.    * @param input        the {@link TokenStream} to consume    * @param stopTypes    the types to filter    * @param useWhiteList if true, then tokens whose type is in stopTypes will    *                     be kept, otherwise they will be filtered out    */
DECL|method|TypeTokenFilter
specifier|public
name|TypeTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|stopTypes
parameter_list|,
name|boolean
name|useWhiteList
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|stopTypes
operator|=
name|stopTypes
expr_stmt|;
name|this
operator|.
name|useWhiteList
operator|=
name|useWhiteList
expr_stmt|;
block|}
comment|/**    * Create a new {@link TypeTokenFilter} that filters tokens out    * (useWhiteList=false).    */
DECL|method|TypeTokenFilter
specifier|public
name|TypeTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|stopTypes
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|stopTypes
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * By default accept the token if its type is not a stop type.    * When the useWhiteList parameter is set to true then accept the token if its type is contained in the stopTypes    */
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|boolean
name|accept
parameter_list|()
block|{
return|return
name|useWhiteList
operator|==
name|stopTypes
operator|.
name|contains
argument_list|(
name|typeAttribute
operator|.
name|type
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
