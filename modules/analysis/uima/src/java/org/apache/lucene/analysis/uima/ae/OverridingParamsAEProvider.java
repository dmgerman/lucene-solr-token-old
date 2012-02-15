begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.uima.ae
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
operator|.
name|ae
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|analysis_engine
operator|.
name|AnalysisEngineDescription
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
comment|/**  * {@link AEProvider} implementation that creates an Aggregate AE from the given path, also  * injecting runtime parameters defined in the solrconfig.xml Solr configuration file and assigning  * them as overriding parameters in the aggregate AE  */
end_comment
begin_class
DECL|class|OverridingParamsAEProvider
specifier|public
class|class
name|OverridingParamsAEProvider
extends|extends
name|BasicAEProvider
block|{
DECL|field|runtimeParameters
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|runtimeParameters
decl_stmt|;
DECL|method|OverridingParamsAEProvider
specifier|public
name|OverridingParamsAEProvider
parameter_list|(
name|String
name|aePath
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|runtimeParameters
parameter_list|)
block|{
name|super
argument_list|(
name|aePath
argument_list|)
expr_stmt|;
name|this
operator|.
name|runtimeParameters
operator|=
name|runtimeParameters
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configureDescription
specifier|protected
name|void
name|configureDescription
parameter_list|(
name|AnalysisEngineDescription
name|description
parameter_list|)
block|{
for|for
control|(
name|String
name|attributeName
range|:
name|runtimeParameters
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Object
name|val
init|=
name|getRuntimeValue
argument_list|(
name|description
argument_list|,
name|attributeName
argument_list|)
decl_stmt|;
name|description
operator|.
name|getAnalysisEngineMetaData
argument_list|()
operator|.
name|getConfigurationParameterSettings
argument_list|()
operator|.
name|setParameterValue
argument_list|(
name|attributeName
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* create the value to inject in the runtime parameter depending on its declared type */
DECL|method|getRuntimeValue
specifier|private
name|Object
name|getRuntimeValue
parameter_list|(
name|AnalysisEngineDescription
name|desc
parameter_list|,
name|String
name|attributeName
parameter_list|)
block|{
name|String
name|type
init|=
name|desc
operator|.
name|getAnalysisEngineMetaData
argument_list|()
operator|.
name|getConfigurationParameterDeclarations
argument_list|()
operator|.
name|getConfigurationParameter
argument_list|(
literal|null
argument_list|,
name|attributeName
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// TODO : do it via reflection ? i.e. Class paramType = Class.forName(type)...
name|Object
name|val
init|=
literal|null
decl_stmt|;
name|Object
name|runtimeValue
init|=
name|runtimeParameters
operator|.
name|get
argument_list|(
name|attributeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|runtimeValue
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"String"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|runtimeValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"Integer"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|runtimeValue
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"Boolean"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|runtimeValue
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"Float"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|Float
operator|.
name|valueOf
argument_list|(
name|runtimeValue
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|val
return|;
block|}
block|}
end_class
end_unit
