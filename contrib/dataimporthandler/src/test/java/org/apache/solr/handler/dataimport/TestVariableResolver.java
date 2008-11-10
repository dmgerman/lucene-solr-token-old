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
name|Assert
import|;
end_import
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
name|text
operator|.
name|SimpleDateFormat
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
comment|/**  *<p>  * Test for VariableResolver  *</p>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestVariableResolver
specifier|public
class|class
name|TestVariableResolver
block|{
annotation|@
name|Test
DECL|method|testSimpleNamespace
specifier|public
name|void
name|testSimpleNamespace
parameter_list|()
block|{
name|VariableResolverImpl
name|vri
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ns
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
name|ns
operator|.
name|put
argument_list|(
literal|"world"
argument_list|,
literal|"WORLD"
argument_list|)
expr_stmt|;
name|vri
operator|.
name|addNamespace
argument_list|(
literal|"hello"
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"WORLD"
argument_list|,
name|vri
operator|.
name|resolve
argument_list|(
literal|"hello.world"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedNamespace
specifier|public
name|void
name|testNestedNamespace
parameter_list|()
block|{
name|VariableResolverImpl
name|vri
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ns
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
name|ns
operator|.
name|put
argument_list|(
literal|"world"
argument_list|,
literal|"WORLD"
argument_list|)
expr_stmt|;
name|vri
operator|.
name|addNamespace
argument_list|(
literal|"hello"
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|ns
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|ns
operator|.
name|put
argument_list|(
literal|"world1"
argument_list|,
literal|"WORLD1"
argument_list|)
expr_stmt|;
name|vri
operator|.
name|addNamespace
argument_list|(
literal|"hello.my"
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"WORLD1"
argument_list|,
name|vri
operator|.
name|resolve
argument_list|(
literal|"hello.my.world1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test3LevelNestedNamespace
specifier|public
name|void
name|test3LevelNestedNamespace
parameter_list|()
block|{
name|VariableResolverImpl
name|vri
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ns
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
name|ns
operator|.
name|put
argument_list|(
literal|"world"
argument_list|,
literal|"WORLD"
argument_list|)
expr_stmt|;
name|vri
operator|.
name|addNamespace
argument_list|(
literal|"hello"
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|ns
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|ns
operator|.
name|put
argument_list|(
literal|"world1"
argument_list|,
literal|"WORLD1"
argument_list|)
expr_stmt|;
name|vri
operator|.
name|addNamespace
argument_list|(
literal|"hello.my.new"
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"WORLD1"
argument_list|,
name|vri
operator|.
name|resolve
argument_list|(
literal|"hello.my.new.world1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|dateNamespaceWithValue
specifier|public
name|void
name|dateNamespaceWithValue
parameter_list|()
block|{
name|VariableResolverImpl
name|vri
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|vri
operator|.
name|addNamespace
argument_list|(
literal|"dataimporter.functions"
argument_list|,
name|EvaluatorBag
operator|.
name|getFunctionsNamespace
argument_list|(
name|vri
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ns
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
name|Date
name|d
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|ns
operator|.
name|put
argument_list|(
literal|"dt"
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|vri
operator|.
name|addNamespace
argument_list|(
literal|"A"
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss"
argument_list|)
operator|.
name|format
argument_list|(
name|d
argument_list|)
argument_list|,
name|vri
operator|.
name|replaceTokens
argument_list|(
literal|"${dataimporter.functions.formatDate(A.dt,yyyy-MM-dd HH:mm:ss)}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|dateNamespaceWithExpr
specifier|public
name|void
name|dateNamespaceWithExpr
parameter_list|()
block|{
name|VariableResolverImpl
name|vri
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|vri
operator|.
name|addNamespace
argument_list|(
literal|"dataimporter.functions"
argument_list|,
name|EvaluatorBag
operator|.
name|getFunctionsNamespace
argument_list|(
name|vri
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|vri
operator|.
name|replaceTokens
argument_list|(
literal|"${dataimporter.functions.formatDate('NOW',yyyy-MM-dd HH:mm)}"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm"
argument_list|)
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultNamespace
specifier|public
name|void
name|testDefaultNamespace
parameter_list|()
block|{
name|VariableResolverImpl
name|vri
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ns
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
name|ns
operator|.
name|put
argument_list|(
literal|"world"
argument_list|,
literal|"WORLD"
argument_list|)
expr_stmt|;
name|vri
operator|.
name|addNamespace
argument_list|(
literal|null
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"WORLD"
argument_list|,
name|vri
operator|.
name|resolve
argument_list|(
literal|"world"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultNamespace1
specifier|public
name|void
name|testDefaultNamespace1
parameter_list|()
block|{
name|VariableResolverImpl
name|vri
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ns
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
name|ns
operator|.
name|put
argument_list|(
literal|"world"
argument_list|,
literal|"WORLD"
argument_list|)
expr_stmt|;
name|vri
operator|.
name|addNamespace
argument_list|(
literal|null
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"WORLD"
argument_list|,
name|vri
operator|.
name|resolve
argument_list|(
literal|"world"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFunctionNamespace1
specifier|public
name|void
name|testFunctionNamespace1
parameter_list|()
block|{
specifier|final
name|VariableResolverImpl
name|resolver
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"class"
argument_list|,
name|E
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|addNamespace
argument_list|(
literal|"dataimporter.functions"
argument_list|,
name|EvaluatorBag
operator|.
name|getFunctionsNamespace
argument_list|(
name|resolver
argument_list|,
name|l
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|resolver
operator|.
name|replaceTokens
argument_list|(
literal|"${dataimporter.functions.formatDate('NOW',yyyy-MM-dd HH:mm)}"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm"
argument_list|)
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Hello World"
argument_list|,
name|resolver
operator|.
name|replaceTokens
argument_list|(
literal|"${dataimporter.functions.test('TEST')}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|E
specifier|public
specifier|static
class|class
name|E
extends|extends
name|Evaluator
block|{
DECL|method|evaluate
specifier|public
name|String
name|evaluate
parameter_list|(
name|VariableResolver
name|resolver
parameter_list|,
name|String
name|expression
parameter_list|)
block|{
return|return
literal|"Hello World"
return|;
block|}
block|}
block|}
end_class
end_unit
