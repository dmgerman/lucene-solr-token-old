begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
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
name|luke
operator|.
name|FieldFlag
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
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * This is an incomplete representation of the data returned from Luke  *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|LukeResponse
specifier|public
class|class
name|LukeResponse
extends|extends
name|SolrResponseBase
block|{
DECL|class|FieldTypeInfo
specifier|public
specifier|static
class|class
name|FieldTypeInfo
implements|implements
name|Serializable
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|className
name|String
name|className
decl_stmt|;
DECL|field|tokenized
name|boolean
name|tokenized
decl_stmt|;
DECL|field|analyzer
name|String
name|analyzer
decl_stmt|;
DECL|field|fields
name|List
argument_list|<
name|String
argument_list|>
name|fields
decl_stmt|;
DECL|method|FieldTypeInfo
specifier|public
name|FieldTypeInfo
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|fields
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
DECL|method|getAnalyzer
specifier|public
name|String
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
DECL|method|getClassName
specifier|public
name|String
name|getClassName
parameter_list|()
block|{
return|return
name|className
return|;
block|}
DECL|method|getFields
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
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
DECL|method|isTokenized
specifier|public
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
name|tokenized
return|;
block|}
comment|/*      Sample:      types={ignored={fields=null,tokenized=false,analyzer=org.apache.solr.schema.FieldType$DefaultAnalyzer@f94934},      integer={fields=null,tokenized=false,analyzer=org.apache.solr.schema.FieldType$DefaultAnalyzer@3525a2},      sfloat={fields=[price, weight],tokenized=false,analyzer=org.apache.solr.schema.FieldType$DefaultAnalyzer@39cf9c},      text_ws={fields=[cat],tokenized=true,analyzer=TokenizerChain(org.apache.solr.analysis.WhitespaceTokenizerFactory@6d3ca2)},      alphaOnlySort={fields=[alphaNameSort],tokenized=true,analyzer=TokenizerChain(org.apache.solr.analysis.KeywordTokenizerFactory@a7bd3b,       org.apache.solr.analysis.LowerCaseFilterFactory@78aae2, org.apache.solr.analysis.TrimFilterFactory@1b16a7,       org.apache.solr.analysis.PatternReplaceFilterFactory@6c6b08)},date={fields=[timestamp],tokenized=false,       analyzer=org.apache.solr.schema.FieldType$DefaultAnalyzer@e6e42e},sint={fields=[popularity],       tokenized=false,analyzer=org.apache.solr.schema.FieldType$DefaultAnalyzer@8ea21d},       boolean={fields=[inStock],tokenized=false,analyzer=org.apache.solr.schema.BoolField$1@354949},       textTight={fields=[sku],tokenized=true,analyzer=TokenizerChain(org.apache.solr.analysis.WhitespaceTokenizerFactory@5e88f7,        org.apache.solr.analysis.SynonymFilterFactory@723646, org.apache.solr.analysis.StopFilterFactory@492ff1,        org.apache.solr.analysis.WordDelimiterFilterFactory@eaabad, org.apache.solr.analysis.LowerCaseFilterFactory@ad1355,         org.apache.solr.analysis.EnglishPorterFilterFactory@d03a00, org.apache.solr.analysis.RemoveDuplicatesTokenFilterFactory@900079)},         long={fields=null,tokenized=false,analyzer=org.apache.solr.schema.FieldType$DefaultAnalyzer@f3b83},         double={fields=null,tokenized=false,analyzer=org.apache.solr.schema.FieldType$DefaultAnalyzer@c2b07},        */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|read
specifier|public
name|void
name|read
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|nl
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"fields"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|theFields
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|fields
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|theFields
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"tokenized"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
operator|==
literal|true
condition|)
block|{
name|tokenized
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"analyzer"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
operator|==
literal|true
condition|)
block|{
name|analyzer
operator|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"className"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
operator|==
literal|true
condition|)
block|{
name|className
operator|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|FieldInfo
specifier|public
specifier|static
class|class
name|FieldInfo
implements|implements
name|Serializable
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|type
name|String
name|type
decl_stmt|;
DECL|field|schema
name|String
name|schema
decl_stmt|;
DECL|field|docs
name|int
name|docs
decl_stmt|;
DECL|field|distinct
name|int
name|distinct
decl_stmt|;
DECL|field|flags
name|EnumSet
argument_list|<
name|FieldFlag
argument_list|>
name|flags
decl_stmt|;
DECL|field|cacheableFaceting
name|boolean
name|cacheableFaceting
decl_stmt|;
DECL|field|topTerms
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|topTerms
decl_stmt|;
DECL|method|FieldInfo
specifier|public
name|FieldInfo
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|name
operator|=
name|n
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|read
specifier|public
name|void
name|read
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|nl
control|)
block|{
if|if
condition|(
literal|"type"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|type
operator|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|"flags"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|flags
operator|=
name|parseFlags
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"schema"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|schema
operator|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"docs"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|docs
operator|=
operator|(
name|Integer
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"distinct"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|distinct
operator|=
operator|(
name|Integer
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"cacheableFaceting"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|cacheableFaceting
operator|=
operator|(
name|Boolean
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"topTerms"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|topTerms
operator|=
operator|(
name|NamedList
argument_list|<
name|Integer
argument_list|>
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|parseFlags
specifier|public
specifier|static
name|EnumSet
argument_list|<
name|FieldFlag
argument_list|>
name|parseFlags
parameter_list|(
name|String
name|flagStr
parameter_list|)
block|{
name|EnumSet
argument_list|<
name|FieldFlag
argument_list|>
name|result
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|FieldFlag
operator|.
name|class
argument_list|)
decl_stmt|;
name|char
index|[]
name|chars
init|=
name|flagStr
operator|.
name|toCharArray
argument_list|()
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
name|chars
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|chars
index|[
name|i
index|]
operator|!=
literal|'-'
condition|)
block|{
name|FieldFlag
name|flag
init|=
name|FieldFlag
operator|.
name|getFlag
argument_list|(
name|chars
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|flag
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|getFlags
specifier|public
name|EnumSet
argument_list|<
name|FieldFlag
argument_list|>
name|getFlags
parameter_list|()
block|{
return|return
name|flags
return|;
block|}
DECL|method|isCacheableFaceting
specifier|public
name|boolean
name|isCacheableFaceting
parameter_list|()
block|{
return|return
name|cacheableFaceting
return|;
block|}
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getDistinct
specifier|public
name|int
name|getDistinct
parameter_list|()
block|{
return|return
name|distinct
return|;
block|}
DECL|method|getDocs
specifier|public
name|int
name|getDocs
parameter_list|()
block|{
return|return
name|docs
return|;
block|}
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
DECL|method|getSchema
specifier|public
name|String
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
DECL|method|getTopTerms
specifier|public
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|getTopTerms
parameter_list|()
block|{
return|return
name|topTerms
return|;
block|}
block|}
DECL|field|indexInfo
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|indexInfo
decl_stmt|;
DECL|field|fieldInfo
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
name|fieldInfo
decl_stmt|;
DECL|field|fieldTypeInfo
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|FieldTypeInfo
argument_list|>
name|fieldTypeInfo
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|setResponse
specifier|public
name|void
name|setResponse
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
parameter_list|)
block|{
name|super
operator|.
name|setResponse
argument_list|(
name|res
argument_list|)
expr_stmt|;
comment|// Parse indexinfo
name|indexInfo
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|schema
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|get
argument_list|(
literal|"schema"
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|flds
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|res
operator|.
name|get
argument_list|(
literal|"fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|flds
operator|==
literal|null
operator|&&
name|schema
operator|!=
literal|null
condition|)
block|{
name|flds
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|schema
operator|.
name|get
argument_list|(
literal|"fields"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|flds
operator|!=
literal|null
condition|)
block|{
name|fieldInfo
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|field
range|:
name|flds
control|)
block|{
name|FieldInfo
name|f
init|=
operator|new
name|FieldInfo
argument_list|(
name|field
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|f
operator|.
name|read
argument_list|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|fieldInfo
operator|.
name|put
argument_list|(
name|field
operator|.
name|getKey
argument_list|()
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|schema
operator|!=
literal|null
condition|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|fldTypes
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|schema
operator|.
name|get
argument_list|(
literal|"types"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fldTypes
operator|!=
literal|null
condition|)
block|{
name|fieldTypeInfo
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldTypeInfo
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldType
range|:
name|fldTypes
control|)
block|{
name|FieldTypeInfo
name|ft
init|=
operator|new
name|FieldTypeInfo
argument_list|(
name|fieldType
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|ft
operator|.
name|read
argument_list|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|fieldType
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|fieldTypeInfo
operator|.
name|put
argument_list|(
name|fieldType
operator|.
name|getKey
argument_list|()
argument_list|,
name|ft
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//----------------------------------------------------------------
comment|//----------------------------------------------------------------
DECL|method|getIndexDirectory
specifier|public
name|String
name|getIndexDirectory
parameter_list|()
block|{
if|if
condition|(
name|indexInfo
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|String
operator|)
name|indexInfo
operator|.
name|get
argument_list|(
literal|"directory"
argument_list|)
return|;
block|}
DECL|method|getNumDocs
specifier|public
name|Integer
name|getNumDocs
parameter_list|()
block|{
if|if
condition|(
name|indexInfo
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|Integer
operator|)
name|indexInfo
operator|.
name|get
argument_list|(
literal|"numDocs"
argument_list|)
return|;
block|}
DECL|method|getMaxDoc
specifier|public
name|Integer
name|getMaxDoc
parameter_list|()
block|{
if|if
condition|(
name|indexInfo
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|Integer
operator|)
name|indexInfo
operator|.
name|get
argument_list|(
literal|"maxDoc"
argument_list|)
return|;
block|}
DECL|method|getNumTerms
specifier|public
name|Integer
name|getNumTerms
parameter_list|()
block|{
if|if
condition|(
name|indexInfo
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|Integer
operator|)
name|indexInfo
operator|.
name|get
argument_list|(
literal|"numTerms"
argument_list|)
return|;
block|}
DECL|method|getFieldTypeInfo
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|FieldTypeInfo
argument_list|>
name|getFieldTypeInfo
parameter_list|()
block|{
return|return
name|fieldTypeInfo
return|;
block|}
DECL|method|getFieldTypeInfo
specifier|public
name|FieldTypeInfo
name|getFieldTypeInfo
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|fieldTypeInfo
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|getIndexInfo
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getIndexInfo
parameter_list|()
block|{
return|return
name|indexInfo
return|;
block|}
DECL|method|getFieldInfo
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
name|getFieldInfo
parameter_list|()
block|{
return|return
name|fieldInfo
return|;
block|}
DECL|method|getFieldInfo
specifier|public
name|FieldInfo
name|getFieldInfo
parameter_list|(
name|String
name|f
parameter_list|)
block|{
return|return
name|fieldInfo
operator|.
name|get
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|//----------------------------------------------------------------
block|}
end_class
end_unit
