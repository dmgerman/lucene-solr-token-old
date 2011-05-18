begin_unit
begin_package
DECL|package|org.apache.solr.uima.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uima
operator|.
name|processor
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|List
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
name|uima
operator|.
name|processor
operator|.
name|SolrUIMAConfiguration
operator|.
name|MapField
import|;
end_import
begin_comment
comment|/**  * Read configuration for Solr-UIMA integration  *   * @version $Id$  *   */
end_comment
begin_class
DECL|class|SolrUIMAConfigurationReader
specifier|public
class|class
name|SolrUIMAConfigurationReader
block|{
DECL|field|args
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|args
decl_stmt|;
DECL|method|SolrUIMAConfigurationReader
specifier|public
name|SolrUIMAConfigurationReader
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|args
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
block|}
DECL|method|readSolrUIMAConfiguration
specifier|public
name|SolrUIMAConfiguration
name|readSolrUIMAConfiguration
parameter_list|()
block|{
return|return
operator|new
name|SolrUIMAConfiguration
argument_list|(
name|readAEPath
argument_list|()
argument_list|,
name|readFieldsToAnalyze
argument_list|()
argument_list|,
name|readFieldsMerging
argument_list|()
argument_list|,
name|readTypesFeaturesFieldsMapping
argument_list|()
argument_list|,
name|readAEOverridingParameters
argument_list|()
argument_list|,
name|readIgnoreErrors
argument_list|()
argument_list|,
name|readLogField
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readAEPath
specifier|private
name|String
name|readAEPath
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"analysisEngine"
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|getAnalyzeFields
specifier|private
name|NamedList
name|getAnalyzeFields
parameter_list|()
block|{
return|return
operator|(
name|NamedList
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"analyzeFields"
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|readFieldsToAnalyze
specifier|private
name|String
index|[]
name|readFieldsToAnalyze
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|getAnalyzeFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"fields"
argument_list|)
decl_stmt|;
return|return
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|readFieldsMerging
specifier|private
name|boolean
name|readFieldsMerging
parameter_list|()
block|{
return|return
operator|(
name|Boolean
operator|)
name|getAnalyzeFields
argument_list|()
operator|.
name|get
argument_list|(
literal|"merge"
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|readTypesFeaturesFieldsMapping
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
argument_list|>
name|readTypesFeaturesFieldsMapping
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|NamedList
name|fieldMappings
init|=
operator|(
name|NamedList
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"fieldMappings"
argument_list|)
decl_stmt|;
comment|/* iterate over UIMA types */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldMappings
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|NamedList
name|type
init|=
operator|(
name|NamedList
operator|)
name|fieldMappings
operator|.
name|get
argument_list|(
literal|"type"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|String
name|typeName
init|=
operator|(
name|String
operator|)
name|type
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
name|subMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
argument_list|()
decl_stmt|;
comment|/* iterate over mapping definitions */
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|type
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|j
operator|++
control|)
block|{
name|NamedList
name|mapping
init|=
operator|(
name|NamedList
operator|)
name|type
operator|.
name|get
argument_list|(
literal|"mapping"
argument_list|,
name|j
operator|+
literal|1
argument_list|)
decl_stmt|;
name|String
name|featureName
init|=
operator|(
name|String
operator|)
name|mapping
operator|.
name|get
argument_list|(
literal|"feature"
argument_list|)
decl_stmt|;
name|String
name|fieldNameFeature
init|=
literal|null
decl_stmt|;
name|String
name|mappedFieldName
init|=
operator|(
name|String
operator|)
name|mapping
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mappedFieldName
operator|==
literal|null
condition|)
block|{
name|fieldNameFeature
operator|=
operator|(
name|String
operator|)
name|mapping
operator|.
name|get
argument_list|(
literal|"fieldNameFeature"
argument_list|)
expr_stmt|;
name|mappedFieldName
operator|=
operator|(
name|String
operator|)
name|mapping
operator|.
name|get
argument_list|(
literal|"dynamicField"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mappedFieldName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"either of field or dynamicField should be defined for feature "
operator|+
name|featureName
argument_list|)
throw|;
name|MapField
name|mapField
init|=
operator|new
name|MapField
argument_list|(
name|mappedFieldName
argument_list|,
name|fieldNameFeature
argument_list|)
decl_stmt|;
name|subMap
operator|.
name|put
argument_list|(
name|featureName
argument_list|,
name|mapField
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|typeName
argument_list|,
name|subMap
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|readAEOverridingParameters
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readAEOverridingParameters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|runtimeParameters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|NamedList
name|runtimeParams
init|=
operator|(
name|NamedList
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"runtimeParameters"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|runtimeParams
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|runtimeParams
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|value
init|=
name|runtimeParams
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|runtimeParameters
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|runtimeParameters
return|;
block|}
DECL|method|readIgnoreErrors
specifier|private
name|boolean
name|readIgnoreErrors
parameter_list|()
block|{
name|Object
name|ignoreErrors
init|=
name|args
operator|.
name|get
argument_list|(
literal|"ignoreErrors"
argument_list|)
decl_stmt|;
return|return
name|ignoreErrors
operator|==
literal|null
condition|?
literal|false
else|:
operator|(
name|Boolean
operator|)
name|ignoreErrors
return|;
block|}
DECL|method|readLogField
specifier|private
name|String
name|readLogField
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"logField"
argument_list|)
return|;
block|}
block|}
end_class
end_unit
