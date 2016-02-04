begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
package|;
end_package
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|expressions
operator|.
name|Expression
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
name|expressions
operator|.
name|SimpleBindings
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
name|expressions
operator|.
name|js
operator|.
name|JavascriptCompiler
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|SortField
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
name|spell
operator|.
name|Dictionary
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
name|suggest
operator|.
name|DocumentValueSourceDictionary
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
name|core
operator|.
name|SolrCore
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|TrieDoubleField
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
name|schema
operator|.
name|TrieFloatField
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
name|schema
operator|.
name|TrieIntField
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
name|schema
operator|.
name|TrieLongField
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
name|SolrIndexSearcher
import|;
end_import
begin_comment
comment|/**  * Factory for {@link org.apache.lucene.search.suggest.DocumentValueSourceDictionary}  */
end_comment
begin_class
DECL|class|DocumentExpressionDictionaryFactory
specifier|public
class|class
name|DocumentExpressionDictionaryFactory
extends|extends
name|DictionaryFactory
block|{
comment|/** Label for defining field to use for terms */
DECL|field|FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
comment|/** Label for defining payloadField to use for terms (optional) */
DECL|field|PAYLOAD_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|PAYLOAD_FIELD
init|=
literal|"payloadField"
decl_stmt|;
comment|/** Label for defining expression to evaluate the weight for the terms */
DECL|field|WEIGHT_EXPRESSION
specifier|public
specifier|static
specifier|final
name|String
name|WEIGHT_EXPRESSION
init|=
literal|"weightExpression"
decl_stmt|;
comment|/** Label used to define the name of the    * sortField used in the {@link #WEIGHT_EXPRESSION} */
DECL|field|SORT_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|SORT_FIELD
init|=
literal|"sortField"
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|public
name|Dictionary
name|create
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
comment|// should not happen; implies setParams was not called
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Value of params not set"
argument_list|)
throw|;
block|}
name|String
name|field
init|=
operator|(
name|String
operator|)
name|params
operator|.
name|get
argument_list|(
name|FIELD
argument_list|)
decl_stmt|;
name|String
name|payloadField
init|=
operator|(
name|String
operator|)
name|params
operator|.
name|get
argument_list|(
name|PAYLOAD_FIELD
argument_list|)
decl_stmt|;
name|String
name|weightExpression
init|=
operator|(
name|String
operator|)
name|params
operator|.
name|get
argument_list|(
name|WEIGHT_EXPRESSION
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|SortField
argument_list|>
name|sortFields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|FIELD
operator|+
literal|" is a mandatory parameter"
argument_list|)
throw|;
block|}
if|if
condition|(
name|weightExpression
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|WEIGHT_EXPRESSION
operator|+
literal|" is a mandatory parameter"
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|params
operator|.
name|getName
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|SORT_FIELD
argument_list|)
condition|)
block|{
name|String
name|sortFieldName
init|=
operator|(
name|String
operator|)
name|params
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SortField
operator|.
name|Type
name|sortFieldType
init|=
name|getSortFieldType
argument_list|(
name|core
argument_list|,
name|sortFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortFieldType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|sortFieldName
operator|+
literal|" could not be mapped to any appropriate type"
operator|+
literal|" [long, int, float, double]"
argument_list|)
throw|;
block|}
name|SortField
name|sortField
init|=
operator|new
name|SortField
argument_list|(
name|sortFieldName
argument_list|,
name|sortFieldType
argument_list|)
decl_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
name|sortField
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|DocumentValueSourceDictionary
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|field
argument_list|,
name|fromExpression
argument_list|(
name|weightExpression
argument_list|,
name|sortFields
argument_list|)
argument_list|,
name|payloadField
argument_list|)
return|;
block|}
DECL|method|fromExpression
specifier|public
name|ValueSource
name|fromExpression
parameter_list|(
name|String
name|weightExpression
parameter_list|,
name|Set
argument_list|<
name|SortField
argument_list|>
name|sortFields
parameter_list|)
block|{
name|Expression
name|expression
init|=
literal|null
decl_stmt|;
try|try
block|{
name|expression
operator|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
name|weightExpression
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
for|for
control|(
name|SortField
name|sortField
range|:
name|sortFields
control|)
block|{
name|bindings
operator|.
name|add
argument_list|(
name|sortField
argument_list|)
expr_stmt|;
block|}
return|return
name|expression
operator|.
name|getValueSource
argument_list|(
name|bindings
argument_list|)
return|;
block|}
DECL|method|getSortFieldType
specifier|private
name|SortField
operator|.
name|Type
name|getSortFieldType
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|sortFieldName
parameter_list|)
block|{
name|SortField
operator|.
name|Type
name|type
init|=
literal|null
decl_stmt|;
name|String
name|fieldTypeName
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|sortFieldName
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|getTypeName
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldTypes
argument_list|()
operator|.
name|get
argument_list|(
name|fieldTypeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ft
operator|instanceof
name|TrieFloatField
condition|)
block|{
name|type
operator|=
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ft
operator|instanceof
name|TrieIntField
condition|)
block|{
name|type
operator|=
name|SortField
operator|.
name|Type
operator|.
name|INT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ft
operator|instanceof
name|TrieLongField
condition|)
block|{
name|type
operator|=
name|SortField
operator|.
name|Type
operator|.
name|LONG
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ft
operator|instanceof
name|TrieDoubleField
condition|)
block|{
name|type
operator|=
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
block|}
end_class
end_unit
