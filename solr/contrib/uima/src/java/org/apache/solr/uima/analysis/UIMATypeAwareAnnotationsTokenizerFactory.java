begin_unit
begin_package
DECL|package|org.apache.solr.uima.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uima
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Tokenizer
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
name|uima
operator|.
name|UIMATypeAwareAnnotationsTokenizer
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
name|analysis
operator|.
name|BaseTokenizerFactory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * Solr {@link org.apache.solr.analysis.TokenizerFactory} for {@link UIMATypeAwareAnnotationsTokenizer}  */
end_comment
begin_class
DECL|class|UIMATypeAwareAnnotationsTokenizerFactory
specifier|public
class|class
name|UIMATypeAwareAnnotationsTokenizerFactory
extends|extends
name|BaseTokenizerFactory
block|{
DECL|field|descriptorPath
specifier|private
name|String
name|descriptorPath
decl_stmt|;
DECL|field|tokenType
specifier|private
name|String
name|tokenType
decl_stmt|;
DECL|field|featurePath
specifier|private
name|String
name|featurePath
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
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
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|descriptorPath
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"descriptorPath"
argument_list|)
expr_stmt|;
name|tokenType
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"tokenType"
argument_list|)
expr_stmt|;
name|featurePath
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"featurePath"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|Tokenizer
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
return|return
operator|new
name|UIMATypeAwareAnnotationsTokenizer
argument_list|(
name|descriptorPath
argument_list|,
name|tokenType
argument_list|,
name|featurePath
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
end_class
end_unit
