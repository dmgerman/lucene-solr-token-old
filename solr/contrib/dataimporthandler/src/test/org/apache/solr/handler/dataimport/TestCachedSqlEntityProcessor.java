begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Locale
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
comment|/**  *<p>  * Test for CachedSqlEntityProcessor  *</p>  *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestCachedSqlEntityProcessor
specifier|public
class|class
name|TestCachedSqlEntityProcessor
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
annotation|@
name|Test
DECL|method|withoutWhereClause
specifier|public
name|void
name|withoutWhereClause
parameter_list|()
block|{
name|List
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"desc"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|q
init|=
literal|"select * from x where id=${x.id}"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
init|=
name|createMap
argument_list|(
literal|"query"
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|MockDataSource
name|ds
init|=
operator|new
name|MockDataSource
argument_list|()
decl_stmt|;
name|VariableResolverImpl
name|vr
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|vr
operator|.
name|addNamespace
argument_list|(
literal|"x"
argument_list|,
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
name|vr
argument_list|,
name|ds
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
name|entityAttrs
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|"desc"
argument_list|,
literal|"another one"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|vr
operator|.
name|replaceTokens
argument_list|(
name|q
argument_list|)
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|EntityProcessor
name|csep
init|=
operator|new
name|EntityProcessorWrapper
argument_list|(
operator|new
name|CachedSqlEntityProcessor
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|csep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|csep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|rows
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rows
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
name|csep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|csep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|rows
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rows
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rows
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rows
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withoutWhereClauseWithTransformers
specifier|public
name|void
name|withoutWhereClauseWithTransformers
parameter_list|()
block|{
name|List
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"desc"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|q
init|=
literal|"select * from x where id=${x.id}"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
init|=
name|createMap
argument_list|(
literal|"query"
argument_list|,
name|q
argument_list|,
literal|"transformer"
argument_list|,
name|UppercaseTransformer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MockDataSource
name|ds
init|=
operator|new
name|MockDataSource
argument_list|()
decl_stmt|;
name|VariableResolverImpl
name|vr
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|vr
operator|.
name|addNamespace
argument_list|(
literal|"x"
argument_list|,
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
name|vr
argument_list|,
name|ds
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
name|entityAttrs
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|"desc"
argument_list|,
literal|"another one"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|vr
operator|.
name|replaceTokens
argument_list|(
name|q
argument_list|)
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|EntityProcessor
name|csep
init|=
operator|new
name|EntityProcessorWrapper
argument_list|(
operator|new
name|CachedSqlEntityProcessor
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|csep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|csep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|rows
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rows
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
name|csep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|csep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|rows
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r
operator|.
name|get
argument_list|(
literal|"desc"
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
name|r
operator|.
name|get
argument_list|(
literal|"desc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rows
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rows
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rows
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withoutWhereClauseWithMultiRowTransformer
specifier|public
name|void
name|withoutWhereClauseWithMultiRowTransformer
parameter_list|()
block|{
name|List
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"desc"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|q
init|=
literal|"select * from x where id=${x.id}"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
init|=
name|createMap
argument_list|(
literal|"query"
argument_list|,
name|q
argument_list|,
literal|"transformer"
argument_list|,
name|DoubleTransformer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MockDataSource
name|ds
init|=
operator|new
name|MockDataSource
argument_list|()
decl_stmt|;
name|VariableResolverImpl
name|vr
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|vr
operator|.
name|addNamespace
argument_list|(
literal|"x"
argument_list|,
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
name|vr
argument_list|,
name|ds
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
name|entityAttrs
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|"desc"
argument_list|,
literal|"another one"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|vr
operator|.
name|replaceTokens
argument_list|(
name|q
argument_list|)
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|EntityProcessor
name|csep
init|=
operator|new
name|EntityProcessorWrapper
argument_list|(
operator|new
name|CachedSqlEntityProcessor
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|csep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|csep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|rows
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|rows
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
name|csep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|csep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|rows
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|rows
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rows
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rows
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|DoubleTransformer
specifier|public
specifier|static
class|class
name|DoubleTransformer
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
return|return
name|rows
return|;
block|}
block|}
DECL|class|UppercaseTransformer
specifier|public
specifier|static
class|class
name|UppercaseTransformer
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
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
name|row
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|String
condition|)
block|{
name|String
name|s
init|=
operator|(
name|String
operator|)
name|val
decl_stmt|;
name|entry
operator|.
name|setValue
argument_list|(
name|s
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|row
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|withKeyAndLookup
specifier|public
name|void
name|withKeyAndLookup
parameter_list|()
block|{
name|List
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"desc"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|q
init|=
literal|"select * from x"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
init|=
name|createMap
argument_list|(
literal|"query"
argument_list|,
name|q
argument_list|,
name|DIHCacheSupport
operator|.
name|CACHE_PRIMARY_KEY
argument_list|,
literal|"id"
argument_list|,
name|DIHCacheSupport
operator|.
name|CACHE_FOREIGN_KEY
argument_list|,
literal|"x.id"
argument_list|)
decl_stmt|;
name|MockDataSource
name|ds
init|=
operator|new
name|MockDataSource
argument_list|()
decl_stmt|;
name|VariableResolverImpl
name|vr
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|Map
name|xNamespace
init|=
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|vr
operator|.
name|addNamespace
argument_list|(
literal|"x"
argument_list|,
name|xNamespace
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
name|vr
argument_list|,
name|ds
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
name|entityAttrs
argument_list|)
decl_stmt|;
name|doWhereTest
argument_list|(
name|q
argument_list|,
name|context
argument_list|,
name|ds
argument_list|,
name|xNamespace
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withWhereClause
specifier|public
name|void
name|withWhereClause
parameter_list|()
block|{
name|List
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"desc"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|q
init|=
literal|"select * from x"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
init|=
name|createMap
argument_list|(
literal|"query"
argument_list|,
name|q
argument_list|,
literal|"where"
argument_list|,
literal|"id=x.id"
argument_list|)
decl_stmt|;
name|MockDataSource
name|ds
init|=
operator|new
name|MockDataSource
argument_list|()
decl_stmt|;
name|VariableResolverImpl
name|vr
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|Map
name|xNamespace
init|=
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|vr
operator|.
name|addNamespace
argument_list|(
literal|"x"
argument_list|,
name|xNamespace
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
name|vr
argument_list|,
name|ds
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
name|entityAttrs
argument_list|)
decl_stmt|;
name|doWhereTest
argument_list|(
name|q
argument_list|,
name|context
argument_list|,
name|ds
argument_list|,
name|xNamespace
argument_list|)
expr_stmt|;
block|}
DECL|method|doWhereTest
specifier|private
name|void
name|doWhereTest
parameter_list|(
name|String
name|q
parameter_list|,
name|Context
name|context
parameter_list|,
name|MockDataSource
name|ds
parameter_list|,
name|Map
name|xNamespace
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"one"
argument_list|,
literal|"id"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|,
literal|"desc"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|,
literal|"desc"
argument_list|,
literal|"another two"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|3
argument_list|,
literal|"desc"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|3
argument_list|,
literal|"desc"
argument_list|,
literal|"another three"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|3
argument_list|,
literal|"desc"
argument_list|,
literal|"another another three"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|q
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|EntityProcessor
name|csep
init|=
operator|new
name|EntityProcessorWrapper
argument_list|(
operator|new
name|CachedSqlEntityProcessor
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|csep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|csep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|rows
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rows
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
name|csep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|xNamespace
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|csep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|rows
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rows
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|csep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|xNamespace
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|csep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|rows
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|rows
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
