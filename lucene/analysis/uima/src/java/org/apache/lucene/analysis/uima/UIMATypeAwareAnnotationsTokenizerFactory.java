begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.uima
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|uima
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
name|util
operator|.
name|TokenizerFactory
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
name|util
operator|.
name|AttributeFactory
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
name|HashMap
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
comment|/**  * {@link org.apache.lucene.analysis.util.TokenizerFactory} for {@link UIMATypeAwareAnnotationsTokenizer}  */
end_comment
begin_class
DECL|class|UIMATypeAwareAnnotationsTokenizerFactory
specifier|public
class|class
name|UIMATypeAwareAnnotationsTokenizerFactory
extends|extends
name|TokenizerFactory
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
DECL|field|configurationParameters
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configurationParameters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Creates a new UIMATypeAwareAnnotationsTokenizerFactory */
DECL|method|UIMATypeAwareAnnotationsTokenizerFactory
specifier|public
name|UIMATypeAwareAnnotationsTokenizerFactory
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
name|featurePath
operator|=
name|require
argument_list|(
name|args
argument_list|,
literal|"featurePath"
argument_list|)
expr_stmt|;
name|tokenType
operator|=
name|require
argument_list|(
name|args
argument_list|,
literal|"tokenType"
argument_list|)
expr_stmt|;
name|descriptorPath
operator|=
name|require
argument_list|(
name|args
argument_list|,
literal|"descriptorPath"
argument_list|)
expr_stmt|;
name|configurationParameters
operator|.
name|putAll
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|UIMATypeAwareAnnotationsTokenizer
name|create
parameter_list|(
name|AttributeFactory
name|factory
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
name|configurationParameters
argument_list|,
name|factory
argument_list|)
return|;
block|}
block|}
end_class
end_unit
